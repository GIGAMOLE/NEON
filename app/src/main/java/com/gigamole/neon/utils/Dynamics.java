package com.gigamole.neon.utils;
/*
 * Copyright (C) 2015 Basil Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
