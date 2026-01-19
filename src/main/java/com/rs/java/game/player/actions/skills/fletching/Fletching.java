package com.rs.java.game.player.actions.skills.fletching;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.item.Item;
import com.rs.java.game.player.Inventory;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.Action;
import com.rs.java.game.player.content.tasksystem.TaskManager.Tasks;

public class Fletching extends Action {

    private final FletchingData data;
    private final int option;
    private int ticks;

    public Fletching(FletchingData data, int option, int ticks) {
        this.data = data;
        this.option = option;
        this.ticks = ticks;
    }

    @Override
    public boolean start(Player player) {
        if (option >= data.getProducts().length)
            return false;

        if (!hasTool(player)) {
            int tool = data.getToolId();
            player.message("You need a " + ItemDefinitions.getItemDefinitions(tool).getName() + " to fletch this.");
            return false;
        }

        if (!process(player))
            return false;

        setActionDelay(player, 1);
        return true;
    }

    @Override
    public boolean process(Player player) {
        if (ticks <= 0)
            return false;

        Inventory inventory = player.getInventory();
        Skills skills = player.getSkills();

        FletchingProduct product = data.getProducts()[option];

        if (skills.getLevel(Skills.FLETCHING) < product.getLevel()) {
            player.message("You need a level of " + product.getLevel() + " to fletch this.");
            return false;
        }

        if (!inventory.containsItem(data.getBaseId(), 1)) {
            player.message("You have run out of " +
                    ItemDefinitions.getItemDefinitions(data.getBaseId()).getName().toLowerCase() + ".");
            return false;
        }

        if (!hasTool(player))
            return false;

        if (!inventory.hasFreeSlots()
                && ItemDefinitions.getItemDefinitions(product.getProductId()).isStackable()
                && !inventory.containsOneItem(product.getProductId())) {
            player.message("You don't have any inventory space left to fletch this.");
            return false;
        }

        return true;
    }

    private boolean hasTool(Player player) {
        return player.getInventory().containsOneItem(data.getToolId())
                || player.getToolbelt().contains(data.getToolId());
    }

    @Override
    public int processWithDelay(Player player) {
        ticks--;

        player.animate(data.getAnimation());

        Inventory inv = player.getInventory();
        FletchingProduct product = data.getProducts()[option];

        int baseId = data.getBaseId();
        int toolId = data.getToolId();

        boolean toolConsumed = !isPermanentTool(toolId);

        int baseCount = inv.getNumberOf(baseId);
        int toolCount = toolConsumed ? inv.getNumberOf(toolId) : baseCount;

        int makeAmount = Math.min(baseCount, toolCount);

        player.getPackets().sendGameMessage(
                "[DEBUG] Base: " + baseCount +
                        " Tool: " + toolCount +
                        " InitialMake: " + makeAmount
        );

        // Batch limited recipes (headless arrows etc)
        if (product.getInputPerUnit() > 1) {
            player.getPackets().sendGameMessage(
                    "[DEBUG] Batch recipe. Cap = " + product.getInputPerUnit()
            );

            if (makeAmount > product.getInputPerUnit()) {
                makeAmount = product.getInputPerUnit();
                player.getPackets().sendGameMessage(
                        "[DEBUG] MakeAmount capped to " + makeAmount
                );
            }
        }

        if (makeAmount <= 0) {
            player.getPackets().sendGameMessage("[DEBUG] Nothing can be made.");
            return -1;
        }

        int maxQuantity = getMaxQuantityPerAction(data, product);
        if (makeAmount > maxQuantity) {
            player.getPackets().sendGameMessage(
                    "[DEBUG] UI cap: " + maxQuantity + " applied."
            );
            makeAmount = maxQuantity;
        }

        int basesToRemove = makeAmount;
        int toolsToRemove = toolConsumed ? makeAmount : 0;
        int actualOutput;

        if (product.getInputPerUnit() > 1) {
            actualOutput = makeAmount;
        } else {
            actualOutput = makeAmount * product.getAmount();
        }

        player.getPackets().sendGameMessage(
                "[DEBUG] Removing Base=" + basesToRemove +
                        " Tool=" + toolsToRemove +
                        " Output=" + actualOutput
        );

        inv.deleteItem(baseId, basesToRemove);

        if (toolConsumed) {
            inv.deleteItem(toolId, toolsToRemove);
        }

        inv.addItem(product.getProductId(), actualOutput);

        player.getSkills().addXp(
                Skills.FLETCHING,
                product.getExperience() * actualOutput
        );

        player.getPackets().sendGameMessage(
                fletchingMessage(new Item(product.getProductId()), toolId, baseId, actualOutput)
        );

        checkTasks(player, product.getProductId());

        if (ticks > 0 && process(player)) {
            player.getPackets().sendGameMessage("[DEBUG] Continuing next tick...");
            return 2;
        }

        player.getPackets().sendGameMessage("[DEBUG] Finished action.");
        return -1;
    }



    private int getMaxQuantityPerAction(FletchingData data, FletchingProduct product) {
        Item baseItem = new Item(data.getBaseId());
        int maxFromMethod = maxMakeQuantity(data, baseItem);

        if (product.getInputPerUnit() == 1) {
            String baseName = ItemDefinitions.getItemDefinitions(data.getBaseId()).getName().toLowerCase();
            if (baseName.contains("log") || baseName.contains("tree")) {
                return 1;
            }
        }

        return maxFromMethod;
    }





    private boolean isPermanentTool(int id) {
        Item tool = new Item(id);
        return tool.isAnyOf("item.chisel", "item.knife", "item.hammer", "item.needle");
    }



    private void checkTasks(Player player, int productId) {
        if (productId == 50)
            player.getTaskManager().checkComplete(Tasks.FLETCH_SHORTBOW);
        if (productId == 62)
            player.getTaskManager().checkComplete(Tasks.FLETCH_MAPLE_LONGBOW);
        if (productId == 68)
            player.getTaskManager().checkComplete(Tasks.FLETCH_YEW_SHORTBOW);
        if (productId == 70)
            player.getTaskManager().checkComplete(Tasks.FLETCH_MAGIC_LONGBOW);
    }

    @Override
    public void stop(Player player) {
        setActionDelay(player, 3);
    }

    // ---- Lookup ----

    public static FletchingData findFletchingData(Item first, Item second) {
        for (FletchingData d : FletchingData.values()) {
            if ((d.getBaseId() == first.getId() && d.getToolId() == second.getId()) ||
                    (d.getBaseId() == second.getId() && d.getToolId() == first.getId()))
                return d;
        }
        return null;
    }

    // ---- Quantity logic preserved ----

    public static int maxMakeQuantity(FletchingData data, Item item) {
        String itemName = item.getName().toLowerCase();

        // Check if it's a multiple consumption product
        for (FletchingProduct product : data.getProducts()) {
            if (product.consumesMultiple()) {
                // For multiple consumption items, return the inputPer value
                return product.getInputPerUnit();
            }
        }

        // Fall back to your existing logic
        if (itemName.contains("shaft") || itemName.contains("arrow"))
            return 15;
        if (itemName.contains("dart"))
            return 10;
        if (itemName.contains("bolt"))
            return 10;
        if (itemName.contains("brutal"))
            return 3;
        if (itemName.contains("sagaie"))
            return 5;
        if (itemName.contains("tip"))
            return 12;

        return 1; // Default for single consumption
    }

    public static boolean maxMakeQuantityTen(Item item) {
        String name = item.getName().toLowerCase();

        if (name.contains(" arrow"))
            return true;
        if (name.contains(" dart"))
            return true;
        if (name.contains("feather"))
            return true;
        if (name.contains(" bolt"))
            return true;
        if (name.equalsIgnoreCase("mutated vine"))
            return true;

        return false;
    }


    // ---- Messages preserved ----

    private String fletchingMessage(Item item, int used, int usedWith, int amount) {
        Item usedItem = new Item(used);
        Item usedWithItem = new Item(usedWith);
        String defs = ItemDefinitions.getItemDefinitions(item.getId()).getName().toLowerCase();
        String usedItemDefs = ItemDefinitions.getItemDefinitions(usedItem.getId()).getName().toLowerCase();
        String usedItemWithDefs = ItemDefinitions.getItemDefinitions(usedWithItem.getId()).getName().toLowerCase();

        if (usedItemDefs.contains(" limbs") || usedItemWithDefs.contains(" limbs"))
            return "You attach the stock to the limbs and create an unstrung crossbow";

        if (usedItemDefs.contains("string") || usedItemWithDefs.contains("string")) {
            if (usedItemDefs.contains("crossbow") || usedItemWithDefs.contains("crossbow"))
                return "You add a string to the crossbow.";
            return "You add a string to the bow.";
        }

        if (defs.contains("sagaie"))
            return "You fletch " + amount + " " + defs + ".";

        if (defs.contains("bolas"))
            return "You add 2 excrescence and a mutated vine together and make some bolas.";

        if (defs.contains("headless"))
            return "You attach feathers to " + amount + " arrow shaft" + (amount > 1 ? "s" : "") + ".";

        if (defs.contains("dart"))
            return "You finish making " + amount + " " + defs + ".";

        if (defs.contains("arrow"))
            return "You attach arrow heads to some of your arrows.";

        if (defs.contains("bolt") || defs.contains("stake") || defs.contains("brutal"))
            return "You fletch " + amount + " " + defs + ".";

        return "You carefully cut the wood into a " + defs.replace(" (u)", "") + ".";
    }
}
