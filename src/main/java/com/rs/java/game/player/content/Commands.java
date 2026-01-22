package com.rs.java.game.player.content;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.rs.Settings;
import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.core.cache.defintions.NPCDefinitions;
import com.rs.java.game.*;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.item.Item;
import com.rs.java.game.player.*;
import com.rs.kotlin.game.npc.drops.Drop;
import com.rs.kotlin.game.npc.drops.DropTable;
import com.rs.kotlin.game.npc.drops.DropTableRegistry;
import com.rs.java.game.player.Ranks.Rank;
import com.rs.java.game.player.actions.combat.Magic;
import com.rs.core.packets.packet.ButtonHandler;
import com.rs.java.utils.EconomyPrices;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.player.AccountCreation;

public final class Commands {

    private enum CommandCategory {
        NORMAL,
        MODERATOR,
        SUPPORT,
        ADMIN,
        DEVELOPER
    }

    private interface Command {
        boolean execute(Player player, String[] args);
    }

    private static final Map<String, CommandHandler> COMMAND_REGISTRY = new HashMap<>();

    private static class CommandHandler {
        private final Command command;
        private final CommandCategory category;
        private final String description;

        public CommandHandler(Command command, CommandCategory category, String description) {
            this.command = command;
            this.category = category;
            this.description = description;
        }

        public boolean execute(Player player, String[] args) {
            return command.execute(player, args);
        }

        public CommandCategory getCategory() {
            return category;
        }

        public String getDescription() {
            return description;
        }
    }

    static {
        registerCommands();
    }

    private static void registerCommands() {
        registerCommand("item", Commands::itemCommand, CommandCategory.NORMAL,
                "Spawn an item. Usage: ::item [id/name] [amount]");
        registerCommand("claim", Commands::claimCommand, CommandCategory.NORMAL,
                "Claim untradeable items");
        registerCommand("yell", Commands::yellCommand, CommandCategory.NORMAL,
                "Send a message to all players");
        registerCommand("home", Commands::homeCommand, CommandCategory.NORMAL,
                "Teleport to home");
        registerCommand("edgeville", Commands::edgevilleCommand, CommandCategory.NORMAL,
                "Teleport to Edgeville");
        registerCommand("edge", Commands::edgevilleCommand, CommandCategory.NORMAL,
                "Teleport to Edgeville");
        registerCommand("teles", Commands::telesCommand, CommandCategory.NORMAL,
                "Show teleport commands");
        registerCommand("commands", Commands::commandsCommand, CommandCategory.NORMAL,
                "Show available commands");
        registerCommand("players", Commands::playersCommand, CommandCategory.NORMAL,
                "Show online players count");
        registerCommand("online", Commands::playersCommand, CommandCategory.NORMAL,
                "Show online players count");
        registerCommand("pricecheck", Commands::priceCheckCommand, CommandCategory.NORMAL,
                "Check item prices");
        registerCommand("pc", Commands::priceCheckCommand, CommandCategory.NORMAL,
                "Check item prices");
        registerCommand("price", Commands::priceCheckCommand, CommandCategory.NORMAL,
                "Check item prices");
        registerCommand("droplog", Commands::dropLogCommand, CommandCategory.NORMAL,
                "Show drop log");
        registerCommand("cleardroplog", Commands::clearDropLogCommand, CommandCategory.NORMAL,
                "Clear drop log");
        registerCommand("kdr", Commands::kdrCommand, CommandCategory.NORMAL,
                "Show kill/death ratio");
        registerCommand("score", Commands::kdrCommand, CommandCategory.NORMAL,
                "Show kill/death ratio");
        registerCommand("testdrop", Commands::testDropCommand, CommandCategory.DEVELOPER,
                "Test drop tables. Usage: ::testdrop [npcId] [times]");
        registerCommand("droptest", Commands::dropTestToggleCommand, CommandCategory.DEVELOPER,
                "Toggle drop testing mode");
        registerCommand("dropamount", Commands::dropAmountCommand, CommandCategory.DEVELOPER,
                "Set drop testing amount");
        registerCommand("healmode", Commands::healModeCommand, CommandCategory.DEVELOPER,
                "Toggle immunity to hits");
        registerCommand("god", Commands::godModeCommand, CommandCategory.DEVELOPER,
                "Toggle god mode");
        registerCommand("kill", Commands::killCommand, CommandCategory.DEVELOPER,
                "Kill a player. Usage: ::kill [player]");
        registerCommand("giveitem", Commands::giveItemCommand, CommandCategory.DEVELOPER,
                "Give item to player. Usage: ::giveitem [player] [itemId] [amount]");
        registerCommand("givepkp", Commands::givePkpCommand, CommandCategory.DEVELOPER,
                "Give PK points. Usage: ::givepkp [player] [amount]");
        registerCommand("setlevel", Commands::setLevelCommand, CommandCategory.DEVELOPER,
                "Set skill level. Usage: ::setlevel [skillId] [level]");
        registerCommand("setlvl", Commands::setLevelCommand, CommandCategory.DEVELOPER,
                "Set skill level. Usage: ::setlevel [skillId] [level]");
        registerCommand("master", Commands::masterCommand, CommandCategory.DEVELOPER,
                "Max all skills");
        registerCommand("reset", Commands::resetSkillsCommand, CommandCategory.DEVELOPER,
                "Reset all skills");
        registerCommand("tele", Commands::teleportCommand, CommandCategory.DEVELOPER,
                "Teleport to coordinates. Usage: ::tele [x] [y] [plane]");
        registerCommand("npc", Commands::spawnNpcCommand, CommandCategory.DEVELOPER,
                "Spawn NPC. Usage: ::npc [id]");
        registerCommand("tonpc", Commands::toNpcCommand, CommandCategory.DEVELOPER,
                "Become NPC. Usage: ::tonpc [id]");
        registerCommand("mypos", Commands::myPosCommand, CommandCategory.DEVELOPER,
                "Shows players current location. Usage: ::mypos");
        registerCommand("object", Commands::spawnObjectCommand, CommandCategory.DEVELOPER,
                "Spawn object. Usage: ::object [id] [type] [rotation]");
        registerCommand("emote", Commands::emoteCommand, CommandCategory.DEVELOPER,
                "Play emote. Usage: ::emote [id]");
        registerCommand("gfx", Commands::gfxCommand, CommandCategory.DEVELOPER,
                "Play graphics. Usage: ::gfx [id] [height] [rotation] [speed]");
        registerCommand("sound", Commands::soundCommand, CommandCategory.DEVELOPER,
                "Play sound. Usage: ::sound [id]");
        registerCommand("inter", Commands::interfaceCommand, CommandCategory.DEVELOPER,
                "Show interface. Usage: ::inter [id]");
        registerCommand("varbit", Commands::varbitCommand, CommandCategory.DEVELOPER,
                "Set varbit. Usage: ::varbit [id] [value]");
        registerCommand("var", Commands::varCommand, CommandCategory.DEVELOPER,
                "Set var. Usage: ::var [id] [value]");
        registerCommand("global", Commands::globalCommand, CommandCategory.DEVELOPER,
                "Set global config. Usage: ::global [id] [value]");

        registerCommand("mute", Commands::muteCommand, CommandCategory.MODERATOR,
                "Mute player. Usage: ::mute [player]");
        registerCommand("unmute", Commands::unmuteCommand, CommandCategory.MODERATOR,
                "Unmute player. Usage: ::unmute [player]");
        registerCommand("kick", Commands::kickCommand, CommandCategory.MODERATOR,
                "Kick player. Usage: ::kick [player]");
        registerCommand("forcekick", Commands::kickCommand, CommandCategory.MODERATOR,
                "Kick player. Usage: ::kick [player]");
        registerCommand("teleto", Commands::teleportToCommand, CommandCategory.MODERATOR,
                "Teleport to player. Usage: ::teleto [player]");
        registerCommand("teletome", Commands::teleportToMeCommand, CommandCategory.MODERATOR,
                "Teleport player to you. Usage: ::teletome [player]");
        registerCommand("sendhome", Commands::sendHomeCommand, CommandCategory.MODERATOR,
                "Send player home. Usage: ::sendhome [player]");

        registerCommand("answer", Commands::answerTicketCommand, CommandCategory.SUPPORT,
                "Answer player ticket. Usage: ::answer [player]");
        registerCommand("openticket", Commands::answerTicketCommand, CommandCategory.SUPPORT,
                "Answer player ticket. Usage: ::answer [player]");
    }

    private static void registerCommand(String name, Command command, CommandCategory category, String description) {
        COMMAND_REGISTRY.put(name.toLowerCase(), new CommandHandler(command, category, description));
    }

    /**
     * Main command processor
     */
    public static boolean processCommand(Player player, String command, boolean console, boolean clientCommand) {
        if (command == null || command.trim().isEmpty()) {
            return false;
        }
        String[] cmd = command.toLowerCase().split(" ");
        if (cmd.length == 0) {
            return false;
        }

        String commandName = cmd[0];

        boolean processed = processCommandByCategory(player, cmd, console, clientCommand);
        if (processed && player.isStaff()) {
            archiveLogs(player, cmd);
        }
        return processed;
    }

    private static boolean processCommandByCategory(Player player, String[] cmd, boolean console, boolean clientCommand) {
        String commandName = cmd[0];
        CommandHandler handler = COMMAND_REGISTRY.get(commandName);

        if (handler == null) {
            return false;
        }

        if (!hasPermission(player, handler.getCategory())) {
            player.getPackets().sendGameMessage("You don't have permission to use this command.");
            return true;
        }

        try {
            return handler.execute(player, cmd);
        } catch (Exception e) {
            player.getPackets().sendGameMessage("Error executing command: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }

    private static boolean hasPermission(Player player, CommandCategory category) {
        switch (category) {
            case NORMAL:
                return true;
            case SUPPORT:
                return player.isStaff();
            case MODERATOR:
                return player.isModerator() || player.isDeveloper();
            case ADMIN:
            case DEVELOPER:
                return player.isDeveloper();
            default:
                return false;
        }
    }

    // ================ COMMAND IMPLEMENTATIONS ================

    // Normal Player Commands
    private static boolean itemCommand(Player player, String[] cmd) {
        if (Settings.ECONOMY_MODE == Settings.FULL_ECONOMY) {
            player.message("You can't use ::item in this mode.");
            return true;
        }
        if (player.inPkingArea()) {
            player.getPackets().sendGameMessage("You can't use ::item in pvp.");
            return true;
        }

        try {
            String input = String.join(" ", Arrays.copyOfRange(cmd, 1, cmd.length));
            int amount = 1;
            String searchTerm = input;

            String[] parts = input.split(" ");
            if (parts.length > 1 && parts[parts.length - 1].matches("\\d+")) {
                amount = Integer.parseInt(parts[parts.length - 1]);
                searchTerm = String.join(" ", Arrays.copyOfRange(parts, 0, parts.length - 1));
            }

            ItemDefinitions itemDef = null;
            int itemId = 0;

            try {
                itemId = Integer.parseInt(searchTerm);
                itemDef = ItemDefinitions.getItemDefinitions(itemId);
            } catch (NumberFormatException e) {
                List<ItemDefinitions> results = ItemDefinitions.searchItems(searchTerm, 10);
                player.message("Results for '" + searchTerm + "':");
                for (ItemDefinitions def : results) {
                    player.message("- " + def.getName() + " (<col=ff0000>" + def.getId() + "</col>)");
                }
                if (!results.isEmpty()) {
                    itemDef = results.getFirst();
                    itemId = itemDef.getId();
                }
            }

            if (itemDef == null) {
                player.getPackets().sendGameMessage("No item found for: '" + searchTerm + "'");
                return true;
            }

            if (Settings.ECONOMY_MODE < Settings.FULL_SPAWN) {
                if (EconomyPrices.getPrice(itemId) > 0 && !player.getRank().isDeveloper()) {
                    player.message("This item isn't free, therefore it cannot be spawned, look for this item in shops.");
                    return true;
                }
            }

            player.getInventory().addItem(itemId, amount);
            player.getPackets().sendGameMessage("You spawn " + amount + " x " + itemDef.getName() + "("+ itemId + ").");
        } catch (Exception e) {
            player.getPackets().sendGameMessage("Use: ::item id|name (optional: amount)");
        }
        return true;
    }

    private static boolean claimCommand(Player player, String[] cmd) {
        ButtonHandler.refreshUntradeables(player);
        return true;
    }

    private static boolean yellCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.getPackets().sendGameMessage("Use: ::yell [message]");
            return true;
        }

        String data = "";
        for (int i = 1; i < cmd.length; i++) {
            data += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
        }

        ServerMessage.filterMessage(player, Utils.fixChatMessage(data), false);
        archiveYell(player, Utils.fixChatMessage(data));
        return true;
    }

    private static boolean homeCommand(Player player, String[] cmd) {
        if (!player.canUseCommand()) {
            player.getPackets().sendGameMessage("You can't use ::home at this location.");
            return true;
        }
        if (player.isInCombat()) {
            player.getPackets().sendGameMessage("You can't teleport out of combat.");
            return true;
        }

        Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(Settings.HOME_PLAYER_LOCATION));
        player.getPackets().sendGameMessage("You have teleported home.");
        return true;
    }

    private static boolean edgevilleCommand(Player player, String[] cmd) {
        if (!player.canUseCommand()) {
            player.getPackets().sendGameMessage("You can't use ::edgeville at this location.");
            return true;
        }

        Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3082, 3545, 0));
        player.getPackets().sendGameMessage("You have teleported to edgeville.");
        return true;
    }

    private static boolean telesCommand(Player player, String[] cmd) {
        player.getInterfaceManager().sendInterface(275);
        player.getPackets().sendTextOnComponent(275, 1, "*Teleports*");
        player.getPackets().sendTextOnComponent(275, 10, "");
        player.getPackets().sendTextOnComponent(275, 11,
                "::zerk *Teleports to western side of edgeville. SINGLE");
        player.getPackets().sendTextOnComponent(275, 12, "::easts *Teleports to lvl 20 east dragons. SINGLE");
        player.getPackets().sendTextOnComponent(275, 13, "::wests *Teleports to lvl 13 west dragons. SINGLE");
        player.getPackets().sendTextOnComponent(275, 14, "::mb *Teleports inside the mage bank. NOT WILDY");
        player.getPackets().sendTextOnComponent(275, 15,
                "::brid *Teleports to west side of edgeville wilderness. SINGLE");
        player.getPackets().sendTextOnComponent(275, 16, "::gdz *Teleports to Greater demons in lvl 48. MULTI");
        player.getPackets().sendTextOnComponent(275, 17,
                "::44ports *Teleports to lvl 44 wilderness portal. SINGLE");
        player.getPackets().sendTextOnComponent(275, 18,
                "::iceplatue *Teleports to ice platue in lvl 50 wilderness. SINGLE");
        player.getPackets().sendTextOnComponent(275, 19,
                "::kbd *Teleports outside king black dragon lair. MULTI");
        player.getPackets().sendTextOnComponent(275, 20,
                "::50ports *Teleports to lvl 50 wilderness portal. MULTI");
        player.getPackets().sendTextOnComponent(275, 22, "::revs *Teleports to rev cave. SINGLE & MULTI");
        player.getPackets().sendTextOnComponent(275, 23,
                "::altar *Teleports to an altar deep in west of wilderness.");
        player.getPackets().sendTextOnComponent(275, 24,
                "::castle *Teleports to castle near west dragons. MULTI");
        return true;
    }

    private static boolean commandsCommand(Player player, String[] cmd) {
        player.getInterfaceManager().sendInterface(275);
        player.getPackets().sendTextOnComponent(275, 1, "*Commands*");
        player.getPackets().sendTextOnComponent(275, 10, "");
        player.getPackets().sendTextOnComponent(275, 11,
                "::setlevel skillId level - Set your own combat skills<br>You can only set skillIds 1-6 & 23");
        player.getPackets().sendTextOnComponent(275, 12, "");
        player.getPackets().sendTextOnComponent(275, 13, "::teles - Shows all teleport commands");
        player.getPackets().sendTextOnComponent(275, 14, "::tasks - Shows list of all tasks");
        player.getPackets().sendTextOnComponent(275, 15, "::players - Tells you how many players are online");
        player.getPackets().sendTextOnComponent(275, 16, "::playerslist - Shows a list of all players online");
        player.getPackets().sendTextOnComponent(275, 17, "::pricecheck - Search & price checks an item");
        player.getPackets().sendTextOnComponent(275, 18, "::checkoffers - Shows all Grand exchange offers");
        player.getPackets().sendTextOnComponent(275, 19, "::kdr - Prints out your kills & deaths ratio");
        player.getPackets().sendTextOnComponent(275, 20, "::skull - Makes you skulled");
        player.getPackets().sendTextOnComponent(275, 21, "::droplog - Shows droplog");
        player.getPackets().sendTextOnComponent(275, 22, "::cleardroplog - Clears all drops from droplog");
        player.getPackets().sendTextOnComponent(275, 23,
                "::toggledroplogmessage - Toggle on & off droplog messages");
        player.getPackets().sendTextOnComponent(275, 24,
                "::droplogvalue value - Set value of which items should be logged");
        player.getPackets().sendTextOnComponent(275, 25, "::droplog - Shows droplog");
        player.getPackets().sendTextOnComponent(275, 26,
                "::switchitemslook - Changes items look from old / new");
        player.getPackets().sendTextOnComponent(275, 27, "::compreqs - Shows completionist cape requirements");
        player.getPackets().sendTextOnComponent(275, 28, "::emptybank - Resets your whole bank.");
        for (int i = 29; i <= 150; i++)
            player.getPackets().sendTextOnComponent(275, i, "");
        return true;
    }

    private static boolean playersCommand(Player player, String[] cmd) {
        player.getPackets().sendGameMessage("Players online: " + World.getPlayers().size());
        return true;
    }

    private static boolean priceCheckCommand(Player player, String[] cmd) {
        player.getPackets().sendHideIComponent(105, 196, true);
        player.getPackets().sendVar(1109, -1);
        player.getPackets().sendVar1(1241, 16750848);
        player.getPackets().sendVar1(1242, 15439903);
        player.getPackets().sendVar1(741, -1);
        player.getPackets().sendVar1(743, -1);
        player.getPackets().sendVar1(744, 0);
        player.getPackets().sendInterface(true, 752, 7, 389);
        player.getPackets().sendRunScript(570, new Object[]{"Price checker"});
        return true;
    }

    private static boolean dropLogCommand(Player player, String[] cmd) {
        player.getDropLogs().displayInterface();
        return true;
    }

    private static boolean clearDropLogCommand(Player player, String[] cmd) {
        player.getDropLogs().clearDrops();
        return true;
    }

    private static boolean kdrCommand(Player player, String[] cmd) {
        double kill = player.getKillCount();
        double death = player.getDeathCount();
        double dr = kill / death;
        if (kill == 0 && death == 0)
            dr = 0;
        player.setNextForceTalk(
                new ForceTalk("Kills: " + player.getKillCount() + " Deaths: " + player.getDeathCount()
                        + " Streak: " + player.get(Keys.IntKey.KILLSTREAK) + " Ratio: " + new DecimalFormat("##.#").format(dr)));
        return true;
    }

    // Developer Commands
    public static void testDrop(Player player, int npcId, int times) {
        DropTable table = DropTableRegistry.getDropTableForNpc(npcId);
        if (table == null) {
            player.message("No drop table for NPC ID: " + npcId);
            return;
        }

        Map<Integer, Integer> dropCounts = new HashMap<>();

        for (int i = 0; i < times; i++) {
            List<Drop> drops = table.rollDrops(player);
            for (Drop drop : drops) {
                if (drop == null) continue;
                dropCounts.merge(drop.itemId, drop.amount, Integer::sum);

                if (drop.extraDrop != null) {
                    Drop extra = drop.extraDrop;
                    dropCounts.merge(extra.itemId, extra.amount, Integer::sum);
                }
            }
        }

        for (Map.Entry<Integer, Integer> entry : dropCounts.entrySet()) {
            int itemId = entry.getKey();
            int totalAmount = entry.getValue();
            player.getBank().addItem(itemId, totalAmount, true);
        }

        player.message("Simulated " + times + " kills of NPC ID " + npcId + ". Drops deposited to bank.");
    }

    private static boolean testDropCommand(Player player, String[] cmd) {
        if (cmd.length < 3) {
            player.message("Usage: ::testdrop [npcId] [times]");
            return true;
        }

        try {
            int npcId = Integer.parseInt(cmd[1]);
            int times = Integer.parseInt(cmd[2]);
            testDrop(player, npcId, times);
        } catch (NumberFormatException e) {
            player.message("Invalid number format. Usage: ::testdrop [npcId] [times]");
        }
        return true;
    }

    private static boolean dropTestToggleCommand(Player player, String[] cmd) {
        player.dropTesting = !player.dropTesting;
        player.getPackets().sendGameMessage("Drop testing: " + (player.dropTesting ? "Enabled" : "Disabled") + ".");
        return true;
    }

    private static boolean dropAmountCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.message("Usage: ::dropamount [amount]");
            return true;
        }

        try {
            int droptimes = Integer.parseInt(cmd[1]);
            player.dropTestingAmount = droptimes;
            player.getPackets().sendGameMessage("Drop testing amount set to: " + droptimes + ".");
        } catch (NumberFormatException e) {
            player.message("Invalid number. Usage: ::dropamount [amount]");
        }
        return true;
    }

    private static boolean healModeCommand(Player player, String[] cmd) {
        player.healMode = !player.healMode;
        player.message("You have successfully " + (player.healMode ? "enabled" : "disabled") + " immunity to hits.");
        return true;
    }

    private static boolean godModeCommand(Player player, String[] cmd) {
        player.getPackets().sendGameMessage("Godmode is now "
                + (player.getTemporaryAttributtes().get("GODMODE") != null ? "Inactive" : "Active."));
        if (player.getTemporaryAttributtes().get("GODMODE") != null)
            player.getTemporaryAttributtes().remove("GODMODE");
        else
            player.getTemporaryAttributtes().put("GODMODE", 0);
        return true;
    }

    private static boolean killCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.message("Usage: ::kill [player]");
            return true;
        }

        String name = "";
        for (int i = 1; i < cmd.length; i++)
            name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

        Player target = World.getPlayerByDisplayName(name);
        if (target == null) {
            player.message("Player not found.");
            return true;
        }

        target.applyHit(new Hit(target, target.getHitpoints(), HitLook.REGULAR_DAMAGE));
        target.stopAll();
        return true;
    }

    private static boolean giveItemCommand(Player player, String[] cmd) {
        if (cmd.length < 3) {
            player.getPackets().sendGameMessage("Use: ::giveitem player id (optional:amount)");
            return true;
        }

        String targetName = cmd[1];
        Player target = World.getPlayerByDisplayName(targetName);
        if (target == null) {
            player.getPackets().sendGameMessage("Player not found: " + targetName);
            return true;
        }

        try {
            int itemId = Integer.parseInt(cmd[2]);
            int amount = cmd.length > 3 ? Integer.parseInt(cmd[3]) : 1;

            Item itemToGive = new Item(itemId, amount);
            boolean multiple = amount > 1;

            if (!target.getInventory().addItem(itemToGive)) {
                target.getBank().addItem(itemId, amount, true);
            }

            target.getPackets().sendGameMessage(player.getDisplayName() + " has given you "
                    + (multiple ? amount : "one") + " " + itemToGive.getDefinitions().getName() + (multiple ? "s" : ""));

            player.getPackets().sendGameMessage("You have given " + (multiple ? amount : "one")
                    + " " + itemToGive.getDefinitions().getName() + (multiple ? "s" : "")
                    + " to " + target.getDisplayName());
        } catch (NumberFormatException e) {
            player.getPackets().sendGameMessage("Invalid item ID or amount.");
        }
        return true;
    }

    private static boolean givePkpCommand(Player player, String[] cmd) {
        if (cmd.length < 3) {
            player.getPackets().sendGameMessage("Use: ::givepkp player amount");
            return true;
        }

        String targetName = cmd[1];
        Player target = World.getPlayerByDisplayName(targetName);
        if (target == null) {
            player.getPackets().sendGameMessage("Player not found: " + targetName);
            return true;
        }

        try {
            int amount = Integer.parseInt(cmd[2]);
            target.addPKP(amount);
            target.getPackets().sendGameMessage("You receive " + amount + " Pk points.");
            player.getPackets().sendGameMessage("You gave " + amount + " pkp to " + target.getDisplayName());
            player.getPackets().sendGameMessage("They now have " + target.getPKP() + " pkp");
        } catch (NumberFormatException e) {
            player.getPackets().sendGameMessage("Invalid amount.");
        }
        return true;
    }

    private static boolean setLevelCommand(Player player, String[] cmd) {
        if (cmd.length < 3) {
            player.getPackets().sendGameMessage("Usage ::setlevel skillId level");
            return true;
        }

        try {
            int skillId = Integer.parseInt(cmd[1]);
            int level = Integer.parseInt(cmd[2]);

            if (level < 0 || level > 99) {
                player.getPackets().sendGameMessage("Please choose a valid level (0-99).");
                return true;
            }

            if (level < player.getSkills().getLevel(skillId)) {
                player.getSkills().set(skillId, level);
                player.getSkills().setXp(skillId, Skills.getXPForLevel(level));
            } else {
                player.getSkills().addXpNoBonus(skillId, Skills.getXPForLevel(level) - player.getSkills().getXp(skillId));
            }

            player.getAppearence().generateAppearenceData();
            player.getSkills().switchXPPopup(true);
        } catch (NumberFormatException e) {
            player.getPackets().sendGameMessage("Usage ::setlevel skillId level");
        }
        return true;
    }

    private static boolean masterCommand(Player player, String[] cmd) {
        for (int i = 0; i < 24; i++) {
            player.getSkills().set(i, 99);
            player.getSkills().setXp(i, 200000000);
        }
        player.getSkills().set(24, 120);
        player.getSkills().setXp(24, 200000000);

        for (int i = 0; i < 25; i++)
            player.getDialogueManager().startDialogue("LevelUp", i);

        player.getAppearence().generateAppearenceData();
        player.getSkills().switchXPPopup(true);
        return true;
    }

    private static boolean resetSkillsCommand(Player player, String[] cmd) {
        for (int i = 0; i < 25; i++) {
            player.getSkills().setXp(i, 0);
            player.getSkills().set(i, 1);
            player.getSkills().refresh(i);
        }
        return true;
    }

    private static boolean teleportCommand(Player player, String[] cmd) {
        try {
            String[] parts = cmd[1].split(",");

            int plane;
            int x;
            int y;

            if (parts.length == 2) {
                plane = player.getPlane();
                x = Integer.parseInt(parts[0]);
                y = Integer.parseInt(parts[1]);

            } else if (parts.length >= 5) {
                plane = Integer.parseInt(parts[0]);
                x = (Integer.parseInt(parts[1]) << 6) | Integer.parseInt(parts[3]);
                y = (Integer.parseInt(parts[2]) << 6) | Integer.parseInt(parts[4]);

            } else {
                player.message("Use: ::tele x,y OR ::tele plane,x,y,chunkX,chunkY");
                return true;
            }

            player.resetWalkSteps();
            player.setNextWorldTile(new WorldTile(x, y, plane));

            player.message("Teleported to " + plane + "," + x + "," + y);

        } catch (Exception e) {
            player.message("Use: ::tele x,y OR ::tele plane,x,y,chunkX,chunkY");
            e.printStackTrace();
        }
        return true;
    }


    private static boolean spawnNpcCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.getPackets().sendPanelBoxMessage("Use: ::npc id(Integer)");
            return true;
        }

        try {
            int npcId = Integer.parseInt(cmd[1]);
            World.spawnNPC(npcId, player, -1, true, true);
            player.message("Spawned NPC " + npcId + " at " + player.getX() + " " + player.getY() + " " + player.getPlane());
        } catch (NumberFormatException e) {
            player.getPackets().sendPanelBoxMessage("Use: ::npc id(Integer)");
        }
        return true;
    }

    private static boolean myPosCommand(Player player, String[] cmd) {
        player.message("x: " + player.getLocation().getX() + " y: " + player.getLocation().getY() + ", region: " + player.getRegionId());
        return true;
    }

    private static boolean toNpcCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.getPackets().sendPanelBoxMessage("Use: ::tonpc id(Integer)");
            return true;
        }

        try {
            int npcId = Integer.parseInt(cmd[1]);
            player.getAppearence().transformIntoNPC(npcId);
            NPCDefinitions defs = NPCDefinitions.getNPCDefinitions(npcId);
            player.message("You transformed into " + npcId + ", (" + NPCDefinitions.getNPCDefinitions(npcId).name + ")");
            NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npcId);

            if (def.clientScriptData != null) {
                Object bas = def.clientScriptData.get(686);
                if (bas instanceof Integer) {
                    int basId = (int) bas;
                    System.out.println("NPC " + npcId + " uses BAS " + basId);
                }
            }
        } catch (NumberFormatException e) {
            player.getPackets().sendPanelBoxMessage("Use: ::npc id(Integer)");
        }
        return true;
    }

    private static boolean spawnObjectCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.getPackets().sendPanelBoxMessage("Use: ::object id [type] [rotation]");
            return true;
        }

        try {
            int objectId = Integer.parseInt(cmd[1]);
            int type = cmd.length > 2 ? Integer.parseInt(cmd[2]) : 10;
            int rotation = cmd.length > 3 ? Integer.parseInt(cmd[3]) : 0;

            if (type > 22 || type < 0) {
                type = 10;
            }

            World.spawnObject(new WorldObject(objectId, type, rotation, player.getX(),
                    player.getY(), player.getPlane()));
            player.message("Spawned object " + objectId + " type " + type + " rotation " + rotation);
        } catch (NumberFormatException e) {
            player.getPackets().sendPanelBoxMessage("Use: ::object id [type] [rotation]");
        }
        return true;
    }

    private static boolean emoteCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.getPackets().sendPanelBoxMessage("Use: ::emote id");
            return true;
        }

        try {
            int emoteId = Integer.parseInt(cmd[1]);
            player.animate(new Animation(emoteId));
        } catch (NumberFormatException e) {
            player.getPackets().sendPanelBoxMessage("Use: ::emote id");
        }
        return true;
    }

    private static boolean gfxCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.getPackets().sendPanelBoxMessage("Use: ::gfx id [height=0] [rotation=0] [speed=0]");
            return true;
        }

        try {
            int gfxId = Integer.parseInt(cmd[1]);
            int height = cmd.length > 2 ? Integer.parseInt(cmd[2]) : 0;
            int rotation = cmd.length > 3 ? Integer.parseInt(cmd[3]) : 0;
            int speed = cmd.length > 4 ? Integer.parseInt(cmd[4]) : 0;

            player.gfx(new Graphics(gfxId, speed, height, rotation));
            player.getPackets().sendPanelBoxMessage(
                    "GFX spawned: ID=" + gfxId +
                            ", Height=" + height +
                            ", Rotation=" + rotation +
                            ", Speed=" + speed
            );
        } catch (NumberFormatException e) {
            player.getPackets().sendPanelBoxMessage(
                    "Invalid number format. Use: ::gfx id [height] [rotation] [speed]"
            );
        }
        return true;
    }

    private static boolean soundCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.getPackets().sendPanelBoxMessage("Use: ::sound id");
            return true;
        }

        try {
            int soundId = Integer.parseInt(cmd[1]);
            player.getPackets().sendSound(soundId, 0, 1);
            player.message("Playing sound " + soundId);
        } catch (NumberFormatException e) {
            player.getPackets().sendPanelBoxMessage("Use: ::sound id");
        }
        return true;
    }

    private static boolean interfaceCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
            return true;
        }

        try {
            int interfaceId = Integer.parseInt(cmd[1]);
            player.getInterfaceManager().sendInterface(interfaceId);
        } catch (NumberFormatException e) {
            player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
        }
        return true;
    }

    private static boolean varbitCommand(Player player, String[] cmd) {
        if (cmd.length < 3) {
            player.getPackets().sendPanelBoxMessage("Use: ::varbit id value");
            return true;
        }

        try {
            int varbitId = Integer.parseInt(cmd[1]);
            int value = Integer.parseInt(cmd[2]);
            player.getVarsManager().sendVarBit(varbitId, value);
            player.getPackets().sendGameMessage("Sent varbit: " + varbitId + "; " + value);
        } catch (NumberFormatException e) {
            player.getPackets().sendPanelBoxMessage("Use: ::varbit id value");
        }
        return true;
    }

    private static boolean varCommand(Player player, String[] cmd) {
        if (cmd.length < 3) {
            player.getPackets().sendPanelBoxMessage("Use: ::var id value");
            return true;
        }

        try {
            int varId = Integer.parseInt(cmd[1]);
            int value = Integer.parseInt(cmd[2]);
            player.getVarsManager().sendVar(varId, value);
            player.getPackets().sendGameMessage("Sent var: " + varId + "; " + value);
        } catch (NumberFormatException e) {
            player.getPackets().sendPanelBoxMessage("Use: ::var id value");
        }
        return true;
    }

    private static boolean globalCommand(Player player, String[] cmd) {
        if (cmd.length < 3) {
            player.getPackets().sendPanelBoxMessage("Use: ::global id value");
            return true;
        }

        try {
            int configId = Integer.parseInt(cmd[1]);
            int value = Integer.parseInt(cmd[2]);
            player.getPackets().sendGlobalVar(configId, value);
            player.getPackets().sendGameMessage("Sent global config: " + configId + "; " + value);
        } catch (NumberFormatException e) {
            player.getPackets().sendPanelBoxMessage("Use: ::global id value");
        }
        return true;
    }

    // Mod Commands
    private static boolean muteCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.message("Usage: ::mute [player]");
            return true;
        }

        String name = "";
        for (int i = 1; i < cmd.length; i++)
            name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

        Player target = World.getPlayerByDisplayName(name);
        if (target != null) {
            target.mute(target.getUsername(), "-", 1);
            player.getPackets().sendGameMessage("You have muted 1 day: " + target.getDisplayName() + ".");
        } else {
            name = Utils.formatPlayerNameForProtocol(name);
            if (!AccountCreation.exists(name)) {
                player.getPackets().sendGameMessage(
                        "Account name " + Utils.formatPlayerNameForDisplay(name) + " doesn't exist.");
                return true;
            }
            target = AccountCreation.loadPlayer(name);
            target.setUsername(name);
            target.mute(target.getUsername(), "-", 1);
            player.getPackets().sendGameMessage("You have muted 1 day: " + Utils.formatPlayerNameForDisplay(name) + ".");
            AccountCreation.savePlayer(target);
        }
        return true;
    }

    private static boolean unmuteCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.message("Usage: ::unmute [player]");
            return true;
        }

        String name = "";
        for (int i = 1; i < cmd.length; i++)
            name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

        Player target = World.getPlayerByDisplayName(name);
        if (target != null) {
            target.liftMute(false, player);
            player.getPackets().sendGameMessage("You have unmuted: " + target.getDisplayName() + ".");
        } else {
            name = Utils.formatPlayerNameForProtocol(name);
            if (!AccountCreation.exists(name)) {
                player.getPackets().sendGameMessage(
                        "Account name " + Utils.formatPlayerNameForDisplay(name) + " doesn't exist.");
                return true;
            }
            target = AccountCreation.loadPlayer(name);
            target.setUsername(name);
            target.liftMute(false, player);
            player.getPackets().sendGameMessage("You have unmuted: " + Utils.formatPlayerNameForDisplay(name) + ".");
            AccountCreation.savePlayer(target);
        }
        return true;
    }

    private static boolean kickCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.message("Usage: ::kick [player]");
            return true;
        }

        String name = "";
        for (int i = 1; i < cmd.length; i++)
            name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

        Player target = World.getPlayerByDisplayName(name);
        if (target == null) {
            player.message(name + " is not logged in.");
        } else {
            if (target.isDeveloper()) {
                player.message("Cannot kick a developer.");
                return true;
            }
            target.forceLogout();
            player.message("You kicked player: " + name + ".");
        }
        return true;
    }

    private static boolean teleportToCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.message("Usage: ::teleto [player]");
            return true;
        }

        String name = "";
        for (int i = 1; i < cmd.length; i++)
            name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

        Player target = World.getPlayerByDisplayName(name);
        if (target == null) {
            player.getPackets().sendGameMessage("Couldn't find player " + name + ".");
            return true;
        }

        if (player.isAtWild()) {
            player.getPackets().sendGameMessage("You cannot use this command in the wilderness.");
            return true;
        }

        if (target.getAppearence().isHidden()) {
            player.getPackets().sendGameMessage("Couldn't find player " + name + ".");
            return true;
        }

        player.setNextWorldTile(target);
        return true;
    }

    private static boolean teleportToMeCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.message("Usage: ::teletome [player]");
            return true;
        }

        String name = "";
        for (int i = 1; i < cmd.length; i++)
            name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

        Player target = World.getPlayerByDisplayName(name);
        if (target == null) {
            player.getPackets().sendGameMessage("Couldn't find player " + name + ".");
            return true;
        }

        if (player.isAtWild()) {
            player.getPackets().sendGameMessage("You cannot use this command in the wilderness.");
            return true;
        }

        if (target.isDeveloper()) {
            player.getPackets().sendGameMessage("Unable to teleport a developer to you.");
            return true;
        }

        target.setNextWorldTile(player);
        return true;
    }

    private static boolean sendHomeCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.message("Usage: ::sendhome [player]");
            return true;
        }

        String name = "";
        for (int i = 1; i < cmd.length; i++)
            name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

        Player target = World.getPlayerByDisplayName(name);
        if (target == null) {
            player.getPackets().sendGameMessage("This player is offline.");
            return true;
        }

        if (player.isAtWild()) {
            player.getPackets().sendGameMessage("You cannot use this command in the wilderness.");
            return true;
        }

        target.unlock();
        target.getControlerManager().forceStop();
        target.setNextWorldTile(Settings.START_PLAYER_LOCATION);
        player.getPackets().sendGameMessage("You have sent home " + target.getDisplayName() + ".");
        return true;
    }

    // Support Commands
    private static boolean answerTicketCommand(Player player, String[] cmd) {
        if (cmd.length < 2) {
            player.message("Usage: ::answer [player]");
            return true;
        }

        String username = cmd[1];
        Player requester = World.getPlayerByDisplayName(username);

        if (player.isInLiveChat) {
            player.message("<col=ff000>You cannot handle more than one ticket at a time.");
            return true;
        }

        if (requester == null) {
            player.message("<col=ff000>Player not found: " + username);
            return true;
        }

        if (requester.isInLiveChat) {
            player.message(
                    "<col=ff000>" + requester.getDisplayName() + " is currently already placed in a chatroom.");
            return true;
        }

        if (requester.isRequestingChat) {
            TicketSystem.answerTicket(requester, player);
        } else {
            player.message("<col=ff000>" + requester.getDisplayName() + " has no open tickets.");
        }
        return true;
    }

    // ================ HELPER METHODS ================

    public static void archiveLogs(Player player, String[] cmd) {
        try {
            String location = "";
            if (player.isDeveloper()) {
                location = System.getProperty("user.dir") + "/data/logs/commands/admin/" + player.getUsername() + ".txt";
            } else if (player.isModerator()) {
                location = System.getProperty("user.dir") + "/data/logs/commands/mod/" + player.getUsername() + ".txt";
            } else if (player.getPlayerRank().getRank()[0] == Rank.PLAYERSUPPORT) {
                location = System.getProperty("user.dir") + "/data/logs/commands/support/" + player.getUsername() + ".txt";
            } else {
                location = System.getProperty("user.dir") + "/data/logs/commands/player/" + player.getUsername() + ".txt";
            }
            String afterCMD = "";
            for (int i = 1; i < cmd.length; i++)
                afterCMD += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
            BufferedWriter writer = new BufferedWriter(new FileWriter(location, true));
            writer.write("[" + currentTime("dd MMMMM yyyy 'at' hh:mm:ss z") + "] - ::" + cmd[0] + " " + afterCMD);
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void archiveYell(Player player, String message) {
        try {
            String location = "data/logs/yell/" + player.getUsername() + ".txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(location, true));
            writer.write("[" + currentTime("dd MMMMM yyyy 'at' hh:mm:ss z") + "] - ::yell" + message);
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String currentTime(String dateFormat) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(cal.getTime());
    }

    /**
     * Doesn't let it be instanced
     */
    private Commands() {
    }
}