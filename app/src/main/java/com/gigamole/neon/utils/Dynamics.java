package com.gigamole.neon.utils;

import android.util.FloatMath;

/**
 * Created by GIGAMOLE on 17.05.2015.
 */
public class Dynamics {

    // Used to compare floats, if the difference is smaller than this, they are considered equal
    private static final float TOLERANCE = 0.01f;

    // The position the dynamics should to be at
    private float targetPosition;

    // The current position of the dynamics
    private float position;

    // The current velocity of the dynamics
    private float velocity;

    // The time the last update happened
    private long lastTime;

    // The amount of springiness that the dynamics has
    private float springiness;

    // The damping that the dynamics has
    private float damping;

    // Custom speed
    private float speed = 1000f;

    public Dynamics(float springiness, float dampingRatio) {
        this.springiness = springiness;
        this.damping = dampingRatio * 2 * FloatMath.sqrt(springiness);
    }

    public void setPosition(float position, long now) {
        this.position = position;
        this.lastTime = now;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }


    public void setVelocity(float velocity, long now) {
        this.velocity = velocity;
        this.lastTime = now;
    }

    public void setTargetPosition(float targetPosition, long now) {
        this.targetPosition = targetPosition;
        this.lastTime = now;
    }

    public void update(long now) {
        final float dt = Math.min(now - this.lastTime, 50) / this.speed;

        float x = this.position - this.targetPosition;
        float acceleration = -this.springiness * x - this.damping * this.velocity;

        this.velocity += acceleration * dt;
        this.position += this.velocity * dt;

        this.lastTime = now;
    }

    public boolean isAtRest() {
        final boolean standingStill = Math.abs(this.velocity) < TOLERANCE;
        final boolean isAtTarget = (this.targetPosition - this.position) < TOLERANCE;
        return standingStill && isAtTarget;
    }

    public float getPosition() {
        return this.position;
    }

    public float getTargetPos() {
        return this.targetPosition;
    }

    public float getVelocity() {
        return this.velocity;
    }

}
