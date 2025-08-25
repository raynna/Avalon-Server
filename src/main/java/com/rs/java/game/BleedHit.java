package com.rs.java.game;

public class BleedHit extends Hit {
    private int ticksRemaining;

    public BleedHit(Entity source, int damage, HitLook look, int delayTicks) {
        super(source, damage, look);
        this.ticksRemaining = delayTicks;
    }

    public boolean tick() {
        if (ticksRemaining > 0) {
            ticksRemaining--;
            return false; // Not ready yet
        }
        return true; // Ready to apply
    }
}
