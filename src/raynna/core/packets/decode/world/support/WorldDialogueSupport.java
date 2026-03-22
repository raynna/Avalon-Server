package raynna.core.packets.decode.world.support;

import raynna.core.packets.decode.WorldPacketsDecoder;

import raynna.core.packets.InputStream;
import raynna.game.World;
import raynna.game.player.Player;
import raynna.game.player.RouteEvent;
import raynna.game.player.content.SkillsDialogue;
import raynna.util.Utils;

public final class WorldDialogueSupport {

    private WorldDialogueSupport() {
    }
    public static void handleWorldMapClick(Player player, InputStream stream) {
        int coordinateHash = stream.readInt();
        int x = coordinateHash >> 14;
        int y = coordinateHash & 0x3fff;
        int plane = coordinateHash >> 28;

        Integer hash = (Integer) player.temporaryAttribute().get("worldHash");
        if (hash == null || coordinateHash != hash) {
            player.temporaryAttribute().put("worldHash", coordinateHash);
        } else {
            player.temporaryAttribute().remove("worldHash");
            player.getHintIconsManager().addHintIcon(x, y, plane, 20, 0, 2, -1, true);
            player.getPackets().sendVar(1159, coordinateHash);
        }
    }
    public static void handleDialogueContinue(Player player, InputStream stream) {
        int interfaceHash = stream.readInt();
        stream.readShort128();

        int interfaceId = interfaceHash >> 16;
        if (Utils.getInterfaceDefinitionsSize() <= interfaceId)
            return;
        if (!player.isActive() || !player.getInterfaceManager().containsInterface(interfaceId))
            return;

        int componentId = interfaceHash - (interfaceId << 16);
        int option = -1;

        switch (interfaceId) {
            case 905 -> {
                int slot = SkillsDialogue.getItemSlot(componentId);
                if (slot != -1 && player.getNewDialogueManager().getOptionContinuation() != null) {
                    player.getNewDialogueManager().handleOption(slot);
                    return;
                }
            }
            case 1188 -> option = switch (componentId) {
                case 11 -> 1;
                case 13 -> 2;
                case 14 -> 3;
                case 15 -> 4;
                case 16 -> 5;
                default -> -1;
            };
            case 1185 -> option = switch (componentId) {
                case 15 -> 1;
                case 16 -> 2;
                default -> -1;
            };
        }

        if (option != -1) {
            if (player.getNewDialogueManager().getOptionContinuation() != null) {
                player.getNewDialogueManager().handleOption(option);
                return;
            }
            player.getDialogueManager().continueDialogue(interfaceId, componentId);
            return;
        }

        if (player.getNewDialogueManager().getContinueContinuation() != null) {
            player.getNewDialogueManager().handleContinue();
            return;
        }

        player.getDialogueManager().continueDialogue(interfaceId, componentId);
    }
    public static void handleIncomingAssist(Player player, InputStream stream) {
        stream.readByte();
        int playerIndex = stream.readUnsignedShortLE128();
        final Player p2 = World.getPlayers().get(playerIndex);
        player.setRouteEvent(new RouteEvent(p2, () -> {
            if (p2 == null || p2.isDead() || p2.hasFinished()
                    || !player.getMapRegionsIds().contains(p2.getRegionId()))
                return;
            if (player.isLocked())
                return;
            player.stopAll(false);
            if (player.getPlayerRank().isIronman()) {
                player.message("You cannot assist as a " + (player.getPlayerRank().isHardcore() ? "HC ironman." : "Ironman."));
                return;
            }
            if (p2.getPlayerRank().isIronman()) {
                player.message("You cannot assist a " + (p2.getPlayerRank().isHardcore() ? "HC ironman." : "Ironman."));
                return;
            }
            if (p2.getInterfaceManager().containsScreenInter()) {
                player.getPackets().sendGameMessage("The other player is busy.");
                return;
            }
            if (p2.temporaryAttribute().get("assist") == player) {
                p2.temporaryAttribute().remove("assist");
                player.getAssist().Assist(p2);
            }
        }));
    }
}
