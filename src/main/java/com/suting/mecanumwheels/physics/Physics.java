package com.suting.mecanumwheels.physics;

import com.suting.mecanumwheels.MacenumConfig;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class Physics {
    public static final double GRAVITY_ACCELERATION = 9.81;

    public static final double MIN_RECORDED_IMPULSE = 1.0E-8;

    private static final double MIN_AXIS_LENGTH_SQUARED = 1.0E-8;
    private static final double MIN_ROLL_SPEED = 1.0E-5;

    //最大转速参考值
    private static final double MAX_WHEEL_RPM = 256.0;

    private static final PhysicsForces NO_FORCES = new PhysicsForces(null, null, null);

    private Physics() {
    }

    public static PhysicsForces applyForces(
            CacheContext cache,
            ServerSubLevel subLevel,
            double timeStep,
            Vector3d queuedForce
    ) {
        if (
            cache == null
            || cache.groundNormalLocal == null
            || !isFinite(queuedForce)
            || !Double.isFinite(timeStep)
            || timeStep <= 0.0
        ) {
            return NO_FORCES;
        }

        Vector3d gravity = DimensionPhysicsData.getGravity(subLevel.getLevel());
        Vector3d gravityLocal = subLevel
                .logicalPose()
                .transformNormalInverse(new Vector3d(0.0, gravity.y, 0.0));

        double mass = subLevel.getMassTracker().getMass();

        if (!isFinite(gravityLocal) || !Double.isFinite(mass) || mass <= 0.0) {
            return NO_FORCES;
        }

        PhysicsContext ctx = new PhysicsContext(cache, gravityLocal, mass, timeStep, queuedForce);

        return applyForces(ctx);
    }

    private static PhysicsForces applyForces(PhysicsContext ctx) {
        if (!ctx.valid) {
            return NO_FORCES;
        }

        applyMecanumThrust(ctx);
        applyRollingResistance(ctx);

        return ctx.toPhysicsForces();
    }

    /// 麦克纳姆轮主动推力（恒定模型）
    ///
    ///   正向力 = fam × 归一化转速 × sign(wheelSpeed) × 滚动方向
    ///   侧向力 = side × 归一化转速 × sign(wheelSpeed) × rollerSign × 轮轴方向
    private static void applyMecanumThrust(PhysicsContext ctx) {
        if (!ctx.isMecanum) return;
        if (ctx.liftedUp) return;
        if (ctx.rollNormalLocal == null || ctx.sideNormalLocal == null) return;

        double wheelSpeed = ctx.wheelSpeed;
        if (!Double.isFinite(wheelSpeed)) return;
        double absSpeed = Math.abs(wheelSpeed);
        if (absSpeed <= MIN_ROLL_SPEED) return;

        double famCoef = MacenumConfig.FAM.get();
        double sideCoef = MacenumConfig.SIDE.get();
        boolean famEnabled = Double.isFinite(famCoef) && famCoef > 0.0;
        boolean sideEnabled = Double.isFinite(sideCoef) && sideCoef > 0.0;
        if (!famEnabled && !sideEnabled) return;

        double normalizedSpeed = Math.min(absSpeed / MAX_WHEEL_RPM, 1.0);
        double baseImpulse = normalizedSpeed * ctx.mass * GRAVITY_ACCELERATION * ctx.timeStep;
        double speedSign = Math.signum(wheelSpeed);

        Vector3d totalForce = new Vector3d();

        if (famEnabled) {
            totalForce.add(new Vector3d(ctx.rollNormalLocal).mul(speedSign * famCoef * baseImpulse));
        }

        if (sideEnabled) {
            totalForce.add(new Vector3d(ctx.sideNormalLocal)
                    .mul(speedSign * ctx.rollerSign * sideCoef * baseImpulse));
        }

        if (!isFinite(totalForce) || totalForce.lengthSquared() < MIN_AXIS_LENGTH_SQUARED) {
            return;
        }

        ctx.sideFrictionImpulse.set(totalForce);
        ctx.queuedImpulse.add(ctx.sideFrictionImpulse);
    }

    private static void applyRollingResistance(PhysicsContext ctx) {
        if (ctx.rollNormalLocal == null || ctx.velocityLocal == null) {
            return;
        }

        double rollVelocity = ctx.velocityLocal.dot(ctx.rollNormalLocal);

        if (!Double.isFinite(rollVelocity)) {
            return;
        }

        double absRollVelocity = Math.abs(rollVelocity);

        if (absRollVelocity <= MIN_ROLL_SPEED) {
            return;
        }

        double rollingCoef = MacenumConfig.ROLL.get();
        if (!Double.isFinite(rollingCoef) || rollingCoef <= 0.0) {
            return;
        }

        if (!Double.isFinite(ctx.mass) || ctx.mass <= 0.0) {
            return;
        }

        double rollingImpulseSigned = ctx.queuedImpulse.dot(ctx.rollNormalLocal);
        if (!Double.isFinite(rollingImpulseSigned)) {
            return;
        }

        double groundImpulse = Math.max(0.0, -ctx.queuedImpulse.dot(ctx.groundNormalLocal));
        if (!Double.isFinite(groundImpulse)) {
            return;
        }

        double MAX_SPEED_FACTOR = 5;
        double EPSILON = 0.001;
        double MIN_RESISTANCE = 1;

        double impulseFromVelocity = Math.min(
                MAX_SPEED_FACTOR,
                rollingCoef / (absRollVelocity + EPSILON) + MIN_RESISTANCE
        );

        double resistanceImpulseModule = Math.min(
                impulseFromVelocity,
                Math.abs(rollingImpulseSigned)
        );

        if (!Double.isFinite(resistanceImpulseModule) || resistanceImpulseModule <= MIN_RECORDED_IMPULSE) {
            return;
        }

        ctx.rollingResistanceImpulse = new Vector3d(ctx.rollNormalLocal)
                .mul(-Math.signum(rollVelocity) * resistanceImpulseModule);
        ctx.queuedImpulse.add(ctx.rollingResistanceImpulse);
    }

    private static Vector3d safeNormalize(Vector3dc vector) {
        if (!isFinite(vector) || vector.lengthSquared() < MIN_AXIS_LENGTH_SQUARED) {
            return null;
        }
        return new Vector3d(vector).normalize();
    }

    private static boolean isFinite(Vector3dc vector) {
        return vector != null
                && Double.isFinite(vector.x())
                && Double.isFinite(vector.y())
                && Double.isFinite(vector.z());
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    static class PhysicsContext {
        final boolean valid;
        final Vector3d queuedImpulse;

        final Vector3dc sideNormalLocal;
        final Vector3dc rollNormalLocal;
        final Vector3dc velocityLocal;

        final Vector3dc gravityLocal;
        final double mass;
        final double timeStep;

        final Vector3d groundNormalLocal;

        final double wheelSpeed;
        final boolean isMecanum;
        final int rollerSign;
        final boolean liftedUp;

        Vector3d adhesionImpulse;
        Vector3d sideFrictionImpulse;
        Vector3d rollingResistanceImpulse;

        PhysicsContext(
                CacheContext cache,
                Vector3dc gravityLocal,
                double mass,
                double timeStep,
                Vector3d queuedForce
        ) {
            this.queuedImpulse = queuedForce;
            this.groundNormalLocal = safeNormalize(cache.groundNormalLocal);
            this.sideNormalLocal = cache.sideNormalLocal;
            this.rollNormalLocal = cache.rollNormalLocal;
            this.velocityLocal = cache.velocityLocal;
            this.gravityLocal = gravityLocal;
            this.mass = mass;
            this.timeStep = timeStep;

            this.wheelSpeed = cache.wheelSpeed;
            this.isMecanum = cache.isMecanum;
            this.rollerSign = cache.rollerSign;
            this.liftedUp = cache.liftedUp;

            this.valid = this.groundNormalLocal != null;

            this.adhesionImpulse = new Vector3d();
            this.sideFrictionImpulse = new Vector3d();
            this.rollingResistanceImpulse = null;
        }

        public PhysicsForces toPhysicsForces() {
            return new PhysicsForces(adhesionImpulse, sideFrictionImpulse, rollingResistanceImpulse);
        }
    }

    public record PhysicsForces(
            Vector3d adhesionImpulse,
            Vector3d sideFrictionImpulse,
            Vector3d rollingResistanceImpulse
    ) {}
}