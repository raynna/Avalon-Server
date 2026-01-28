package com.rs.java.game.player.content.collectionlog;

/**
 * @author Simplex
 * @since May 08, 2020
 */
public enum CategoryType {
    BOSSES("Bosses", "Kills:"),
    SLAYER("Slayer", "Kills:"),
    CLUES("Clues", "Completed:"),
    MINIGAMES("Minigames", "Completions:"),
    OTHERS("Others", null);

    String name, killString;

    CategoryType(String name, String killString) {
        this.name = name;
        this.killString = killString;
    }
}
