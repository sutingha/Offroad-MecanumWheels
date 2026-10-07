package com.suting.mecanumwheels.physics;

import org.joml.Vector3d;

public class CacheContext {
    public Vector3d groundNormalLocal;
    public Vector3d sideNormalLocal;
    public Vector3d rollNormalLocal;
    public Vector3d velocityLocal;

    public float wheelSpeed = 0.0f;
    public boolean isMecanum = false;
    public int rollerSign = 0;
    public boolean liftedUp = false;

    long lastTick = -1;

    public void invalidate() {
        groundNormalLocal = null;
        sideNormalLocal = null;
        rollNormalLocal = null;
        velocityLocal = null;
        lastTick = -1;
    }

    boolean isValid(long tick) {
        return lastTick == tick;
    }

    void markValid(long tick) {
        lastTick = tick;
    }
}