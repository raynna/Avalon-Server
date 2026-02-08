package com.rs.java.game.player.content;

import com.rs.core.thread.WorldThread;

public record Tint(
        int startDelay,
        int duration,
        int hue,
        int saturation,
        int lightness,
        int strength
) {

    public static Tint of(int startDelay, int duration,
                          int hue, int saturation,
                          int lightness, int strength) {
        return new Tint(
                startDelay,
                duration,
                hue,
                saturation,
                lightness,
                strength
        );
    }
}
