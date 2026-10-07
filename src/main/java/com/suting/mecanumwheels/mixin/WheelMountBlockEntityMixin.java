package com.suting.mecanumwheels.mixin;

import com.suting.mecanumwheels.WheelsForceGroups;
import com.suting.mecanumwheels.physics.Physics;
import com.suting.mecanumwheels.physics.CacheContext;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlockEntity;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WheelMountBlockEntity.class)
public abstract class WheelMountBlockEntityMixin {
    @Shadow @Final
    private Vector3d queuedForce;

    @Shadow @Final
    private Vector3d queuedForcePos;

    @Shadow
    private double angularVelocity;

    @Shadow
    private boolean liftedUp;

    @Shadow
    public abstract ItemStack getHeldItem();

    @Unique
    private final CacheContext MecanumWheels$cache = new CacheContext();

    @Unique
    private int MecanumWheels$getMecanumSign() {
        ItemStack stack = this.getHeldItem();
        if (stack.isEmpty()) return 0;

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String path = id.getPath();
        if (path.endsWith("_cw"))  return 1;
        if (path.endsWith("_ccw")) return -1;
        return 0;
    }

    @Unique
    private boolean MecanumWheels$isMecanumWheel() {
        return this.MecanumWheels$getMecanumSign() != 0;
    }

    @Inject(
            method = "sable$physicsTick",
            at = @At("HEAD")
    )
    private void MecanumWheels$clearCached(
            ServerSubLevel subLevel,
            RigidBodyHandle handle,
            double timeStep,
            CallbackInfo ci
    ) {
        this.MecanumWheels$cache.invalidate();

        int sign = this.MecanumWheels$getMecanumSign();
        this.MecanumWheels$cache.rollerSign = sign;
        this.MecanumWheels$cache.isMecanum = (sign != 0);
        this.MecanumWheels$cache.liftedUp = this.liftedUp;
        this.MecanumWheels$cache.wheelSpeed = ((KineticBlockEntity)(Object)this).getSpeed();
    }

    @ModifyVariable(
            method = "computeMaxExtensionToTerrain",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/ryanhcode/sable/companion/math/Pose3dc;transformNormalInverse(Lorg/joml/Vector3d;)Lorg/joml/Vector3d;",
                    shift = At.Shift.AFTER
            )
    )
    private Vector3d MecanumWheels$cacheAcceptedSurfaceNormal(Vector3d hitNormal) {
        if (this.MecanumWheels$isMecanumWheel()
                && hitNormal.dot(0.0, 1.0, 0.0) >= 0.5) {
            this.MecanumWheels$cache.groundNormalLocal = MecanumWheels$safeNormalize(hitNormal);
        }

        return hitNormal;
    }

    @ModifyVariable(
            method = "sable$physicsTick",
            ordinal = 0,
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Ldev/ryanhcode/offroad/content/blocks/wheel_mount/WheelMountBlockEntity;getRotatedWheelAxis(Lnet/minecraft/core/Vec3i;)Lorg/joml/Vector3dc;",
                    ordinal = 0
            )
    )
    private Vector3dc MecanumWheels$cacheAcceptedSideNormal(Vector3dc sideD) {
        if (this.MecanumWheels$isMecanumWheel()) {
            this.MecanumWheels$cache.sideNormalLocal = MecanumWheels$safeNormalize(sideD);
        }

        return sideD;
    }

    @ModifyVariable(
            method = "sable$physicsTick",
            ordinal = 1,
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Ldev/ryanhcode/offroad/content/blocks/wheel_mount/WheelMountBlockEntity;getRotatedWheelAxis(Lnet/minecraft/core/Vec3i;)Lorg/joml/Vector3dc;",
                    ordinal = 1
            )
    )
    private Vector3dc MecanumWheels$cacheAcceptedRollDirection(Vector3dc normalD) {
        if (this.MecanumWheels$isMecanumWheel()) {
            this.MecanumWheels$cache.rollNormalLocal = MecanumWheels$safeNormalize(normalD);
        }

        return normalD;
    }

    @ModifyVariable(
            method = "sable$physicsTick",
            ordinal = 1,
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Ldev/ryanhcode/sable/companion/math/Pose3d;transformNormalInverse(Lorg/joml/Vector3d;)Lorg/joml/Vector3d;"
            )
    )
    private Vector3d MecanumWheels$cacheWheelVelocity(Vector3d localVelocity) {
        if (this.MecanumWheels$isMecanumWheel() && MecanumWheels$isFinite(localVelocity)) {
            this.MecanumWheels$cache.velocityLocal = new Vector3d(localVelocity);
        }

        return localVelocity;
    }

    @Inject(
            method = "sable$physicsTick",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/ryanhcode/sable/api/physics/force/ForceTotal;applyImpulseAtPoint(Ldev/ryanhcode/sable/sublevel/ServerSubLevel;Lorg/joml/Vector3dc;Lorg/joml/Vector3dc;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void MecanumWheels$addForces(
            ServerSubLevel subLevel,
            RigidBodyHandle handle,
            double timeStep,
            CallbackInfo ci
    ) {
        if (!this.MecanumWheels$isMecanumWheel()
                || this.MecanumWheels$cache.groundNormalLocal == null) {
            return;
        }

        Vector3d queuedForceBefore = new Vector3d(this.queuedForce);
        Physics.PhysicsForces appliedForces = Physics.applyForces(
                this.MecanumWheels$cache,
                subLevel,
                timeStep,
                this.queuedForce
        );
        if (!MecanumWheels$isFinite(this.queuedForce)) {
            this.queuedForce.set(queuedForceBefore);
            return;
        }

        this.MecanumWheels$recordForceVectors(subLevel, appliedForces);
    }

    @Unique
    private void MecanumWheels$recordForceVectors(ServerSubLevel subLevel, Physics.PhysicsForces appliedForces) {

        if (MecanumWheels$isFinite(appliedForces.adhesionImpulse()))
            subLevel.getOrCreateQueuedForceGroup(WheelsForceGroups.MECANUM_FORCES.get())
                    .recordPointForce(this.queuedForcePos, appliedForces.adhesionImpulse());

        if (MecanumWheels$isFinite(appliedForces.sideFrictionImpulse()))
            subLevel.getOrCreateQueuedForceGroup(WheelsForceGroups.MECANUM_FORCES.get())
                    .recordPointForce(this.queuedForcePos, appliedForces.sideFrictionImpulse());

        if (MecanumWheels$isFinite(appliedForces.rollingResistanceImpulse()))
            subLevel.getOrCreateQueuedForceGroup(WheelsForceGroups.MECANUM_FORCES.get())
                    .recordPointForce(this.queuedForcePos, appliedForces.rollingResistanceImpulse());
    }

    @Unique
    private static Vector3d MecanumWheels$safeNormalize(Vector3dc vector) {
        if (!MecanumWheels$isFinite(vector) || vector.lengthSquared() < 1.0E-8) {
            return null;
        }

        return new Vector3d(vector).normalize();
    }

    @Unique
    private static boolean MecanumWheels$isFinite(Vector3dc vector) {
        return vector != null
                && Double.isFinite(vector.x())
                && Double.isFinite(vector.y())
                && Double.isFinite(vector.z());
    }
}