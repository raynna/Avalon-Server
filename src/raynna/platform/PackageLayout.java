package raynna.platform;

import java.util.List;

public final class PackageLayout {

    private PackageLayout() {
    }

    public static String cityEventImplPath() {
        return "src/main/java/com/rs/java/game/cityhandler/impl";
    }

    public static String cityEventImplPackage() {
        return "raynna.game.cityhandler.impl";
    }

    public static List<String> combatScriptPackages() {
        return List.of(
                "raynna.game.npc.combat.impl"
        );
    }

    public static List<String> itemPluginPackages() {
        return List.of(
                "raynna.game.item.plugins",
                "raynna.game.item.plugins.weapons",
                "raynna.game.item.plugins.misc",
                "raynna.game.item.plugins.tools",
                "raynna.game.item.plugins.summoning",
                "raynna.game.item.plugins.skilling",
                "raynna.game.item.plugins.minigames"
        );
    }

    public static List<String> npcPluginPackages() {
        return List.of(
                "raynna.game.npc.plugins"
        );
    }

    public static List<String> objectPluginPackages() {
        return List.of(
                "raynna.game.objects.plugins"
        );
    }

    public static List<String> dialoguePackages() {
        return List.of(
                "raynna.game.player.dialogues",
                "raynna.game.player.dialogues.item",
                "raynna.game.player.dialogues.npcs",
                "raynna.game.player.dialogues.player",
                "raynna.game.player.dialogues.skilling",
                "raynna.game.player.dialogues.dungeoneering",
                "raynna.game.player.content.quest.impl.cooksassistant.dialogues",
                "raynna.game.player.content.jobs.impl.miscellania.dialogues",
                "raynna.game.player.content.quest.impl.druidicritual.dialogues",
                "raynna.game.player.content.quest.impl.doricsquest.dialogues",
                "raynna.game.player.content.quest.impl.restlessghost.dialogues",
                "raynna.game.player.content.quest.impl.impcatcher.dialogues",
                "raynna.game.player.content.quest.impl.runemysteries.dialogues",
                "raynna.game.player.content.quest.impl.demonslayer.dialogues",
                "raynna.game.player.content.quest.impl.goblindiplomacy.dialogues",
                "raynna.game.player.content.quest.impl.piratestreasure.dialogues",
                "raynna.game.player.content.quest.impl.princealirescue.dialogues",
                "raynna.game.player.content.quest.impl.vampireslayer.dialogues"
        );
    }

    public static String dialogueItemPackage() {
        return "raynna.game.player.dialogues.item";
    }

    public static String dialogueNpcPackage() {
        return "raynna.game.player.dialogues.npcs";
    }

    public static String dialoguePlayerPackage() {
        return "raynna.game.player.dialogues.player";
    }

    public static String dialogueSkillingPackage() {
        return "raynna.game.player.dialogues.skilling";
    }

    public static String dialogueDungeoneeringPackage() {
        return "raynna.game.player.dialogues.dungeoneering";
    }
}
