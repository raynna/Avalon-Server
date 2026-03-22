package raynna.game.player.actions.skills.runecrafting;

public enum RuneEssencePouchType {

    SMALL(5509, 3, 1),
    MEDIUM(5510, 6, 25),
    LARGE(5512, 9, 50),
    GIANT(5514, 12, 75);

    private final int itemId;
    private final int capacity;
    private final int levelReq;

    RuneEssencePouchType(int itemId, int capacity, int levelReq) {
        this.itemId = itemId;
        this.capacity = capacity;
        this.levelReq = levelReq;
    }

    public int getItemId() {
        return itemId;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getLevelReq() {
        return levelReq;
    }

    public static RuneEssencePouchType forItem(int id) {
        for (RuneEssencePouchType type : values()) {
            if (type.itemId == id)
                return type;
        }
        return null;
    }
}