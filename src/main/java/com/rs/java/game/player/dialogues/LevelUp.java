package com.rs.java.game.player.dialogues;

import java.util.HashMap;
import java.util.Map;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.Graphics;
import com.rs.java.game.item.Item;
import com.rs.java.game.player.Equipment;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.kotlin.game.player.equipment.DecorativeUpgrader;

public final class LevelUp extends Dialogue {

    public enum Configs {
        ATTACK(1, 0), STRENGTH(2, 2), DEFENCE(5, 1), HITPOINTS(6, 3), RANGE(3, 4), MAGIC(4, 6), PRAYER(7, 5), AGILITY(8,
                16), HERBLORE(9, 15), THIEVING(10, 17), CRAFTING(11, 12), RUNECRAFTING(12, 20), MINING(13,
                14), SMITHING(14, 13), FISHING(15, 10), COOKING(16, 7), FIREMAKING(17, 11), WOODCUTTING(18,
                8), FLETCHING(19, 9), SLAYER(20, 18), FARMING(21, 19), CONSTRUCTION(22,
                22), HUNTER(23, 21), SUMMONING(24, 23), DUNGEONEERING(25, 24);

        private final int id;
        private final int skill;

        Configs(int id, int skill) {
            this.id = id;
            this.skill = skill;
        }

        public int getId() {
            return id;
        }

        private static final Map<Integer, Configs> configs = new HashMap<>();

        public static Configs levelup(int skill) {
            return configs.get(skill);
        }

        static {
            for (Configs config : Configs.values()) {
                configs.put(config.skill, config);
            }
        }
    }

    /*
     * Levelup sounds 1 - 99
     */

    public enum Musics {
        ATTACK(29, 30, 0), STRENGTH(65, 66, 2), DEFENCE(37, 38, 1), HITPOINTS(47, 48, 3), RANGE(57, 58, 4), MAGIC(51,
                52, 6), PRAYER(55, 56, 5), AGILITY(28, 322, 16), HERBLORE(45, 46, 15), THIEVING(67, 68,
                17), CRAFTING(35, 36, 12), RUNECRAFTING(59, 60, 20), MINING(53, 54,
                14), SMITHING(63, 64, 13), FISHING(41, 42, 10), COOKING(33, 34, 7), FIREMAKING(39, 40,
                11), WOODCUTTING(69, 70, 8), FLETCHING(43, 44, 9), SLAYER(61, 62,
                18), FARMING(11, 10, 19), CONSTRUCTION(31, 32, 22), HUNTER(49, 50,
                21), SUMMONING(300, 301, 23), DUNGEONEERING(416, 417, 24);

        private final int id;
        private final int id2;
        private final int skill;

        Musics(int id, int id2, int skill) {
            this.id = id;
            this.id2 = id2;
            this.skill = skill;
        }

        public int getId() {
            return id;
        }

        public int getId2() {
            return id2;
        }

        private static final Map<Integer, Musics> musics = new HashMap<>();

        public static Musics levelup(int skill) {
            return musics.get(skill);
        }

        static {
            for (Musics music : Musics.values()) {
                musics.put(music.skill, music);
            }
        }
    }

    @Override
    public void start() {
        int skill = (Integer) parameters[0];
        int level = player.getSkills().getLevelForXp(skill);
        String name = Skills.SKILL_NAME[skill];
        player.temporaryAttribute().put("leveledUp[" + skill + "]", Boolean.TRUE);
        if (level >= 90)
            player.getAdventureLog().addActivity("I levelled up my " + name + ". I am now level " + level + ".");
        Musics musicId = Musics.levelup(skill);
        player.getPackets().sendMusicEffect(level > 50 ? musicId.getId2() : musicId.getId());
        player.gfx(new Graphics(199));
        if (level == 99 || level == 120)
            player.gfx(new Graphics(1765));
        player.lastlevelUp.clear();
        player.lastSkill.clear();
        player.lastlevelUp.add(Integer.valueOf(level).toString());
        player.lastSkill.add(name);
        DecorativeUpgrader.INSTANCE.onLevelUp(player, skill, level);
        player.getInterfaceManager().sendFadingInterface("interface.levelup_orb");
        player.getVarsManager().sendVarBit("varbit.levelup_skill_icon", getIconValue(skill));
        Configs levelup = Configs.levelup(skill);
        player.getPackets().sendGlobalVar("globalvar.levelup_orb_skill", levelup.getId());
        switchFlash(player, skill, true);
    }


    public static int getIconValue(int skill) {
        if (skill == Skills.ATTACK)
            return 1;
        if (skill == Skills.STRENGTH)
            return 2;
        if (skill == Skills.RANGE)
            return 3;
        if (skill == Skills.MAGIC)
            return 4;
        if (skill == Skills.DEFENCE)
            return 5;
        if (skill == Skills.HITPOINTS)
            return 6;
        if (skill == Skills.PRAYER)
            return 7;
        if (skill == Skills.AGILITY)
            return 8;
        if (skill == Skills.HERBLORE)
            return 9;
        if (skill == Skills.THIEVING)
            return 10;
        if (skill == Skills.CRAFTING)
            return 11;
        if (skill == Skills.RUNECRAFTING)
            return 12;
        if (skill == Skills.MINING)
            return 13;
        if (skill == Skills.SMITHING)
            return 14;
        if (skill == Skills.FISHING)
            return 15;
        if (skill == Skills.COOKING)
            return 16;
        if (skill == Skills.FIREMAKING)
            return 17;
        if (skill == Skills.WOODCUTTING)
            return 18;
        if (skill == Skills.FLETCHING)
            return 19;
        if (skill == Skills.SLAYER)
            return 20;
        if (skill == Skills.FARMING)
            return 21;
        if (skill == Skills.CONSTRUCTION)
            return 22;
        if (skill == Skills.HUNTER)
            return 23;
        if (skill == Skills.SUMMONING)
            return 24;
        if (skill == Skills.DUNGEONEERING)
            return 25;
        return 0;
    }

    public static int getLevelGainedConfig(int skill) {
        if (skill == Skills.ATTACK)
            return 1469;
        if (skill == Skills.STRENGTH)
            return 1470;
        if (skill == Skills.DEFENCE)
            return 1471;
        if (skill == Skills.RANGE)
            return 1472;
        if (skill == Skills.PRAYER)
            return 1473;
        if (skill == Skills.MAGIC)
            return 1474;
        if (skill == Skills.HITPOINTS)
            return 1475;
        if (skill == Skills.AGILITY)
            return 1476;
        if (skill == Skills.HERBLORE)
            return 1477;
        if (skill == Skills.THIEVING)
            return 1478;
        if (skill == Skills.CRAFTING)
            return 1479;
        if (skill == Skills.FLETCHING)
            return 1480;
        if (skill == Skills.MINING)
            return 1481;
        if (skill == Skills.SMITHING)
            return 1482;
        if (skill == Skills.FISHING)
            return 1483;
        if (skill == Skills.COOKING)
            return 1484;
        if (skill == Skills.FIREMAKING)
            return 1485;
        if (skill == Skills.WOODCUTTING)
            return 1486;
        if (skill == Skills.RUNECRAFTING)
            return 1487;
        if (skill == Skills.SLAYER)
            return 1488;
        if (skill == Skills.FARMING)
            return 1489;
        if (skill == Skills.CONSTRUCTION)
            return 1490;
        if (skill == Skills.HUNTER)
            return 1491;
        if (skill == Skills.SUMMONING)
            return 1492;
        if (skill == Skills.DUNGEONEERING)
            return 1493;
        return -1;
    }

    public static void switchFlash(Player player, int skill, boolean on) {
        int id = 0;
        if (skill == Skills.ATTACK)
            id = 4732;
        if (skill == Skills.STRENGTH)
            id = 4733;
        if (skill == Skills.DEFENCE)
            id = 4734;
        if (skill == Skills.RANGE)
            id = 4735;
        if (skill == Skills.PRAYER)
            id = 4736;
        if (skill == Skills.MAGIC)
            id = 4737;
        if (skill == Skills.HITPOINTS)
            id = 4738;
        if (skill == Skills.AGILITY)
            id = 4739;
        if (skill == Skills.HERBLORE)//4736
            id = 4740;
        if (skill == Skills.THIEVING)
            id = 4741;
        if (skill == Skills.CRAFTING)
            id = 4742;
        if (skill == Skills.FLETCHING)
            id = 4743;
        if (skill == Skills.MINING)
            id = 4744;
        if (skill == Skills.SMITHING)
            id = 4745;
        if (skill == Skills.FISHING)
            id = 4746;
        if (skill == Skills.COOKING)
            id = 4747;
        if (skill == Skills.FIREMAKING)
            id = 4748;
        if (skill == Skills.WOODCUTTING)
            id = 4749;
        if (skill == Skills.RUNECRAFTING)//4754
            id = 4750;
        if (skill == Skills.SLAYER)
            id = 4751;
        if (skill == Skills.FARMING)
            id = 4752;
        if (skill == Skills.CONSTRUCTION)
            id = 4753;
        if (skill == Skills.HUNTER)
            id = 4754;
        if (skill == Skills.SUMMONING)
            id = 4755;
        if (skill == Skills.DUNGEONEERING)
            id = 7756;
        player.getVarsManager().sendVarBit(id, on ? 1 : 0);
    }

    @Override
    public void run(int interfaceId, int componentId) {
        end();
    }

    @Override
    public void finish() {
        // player.getPackets().sendConfig(1179, SKILL_ICON[skill]); //removes
        // random flash
    }
}
