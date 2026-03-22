package raynna.core.packets.decode.world.handler;

import raynna.core.packets.decode.WorldPacketsDecoder;
import raynna.core.packets.decode.world.support.*;

import raynna.core.packets.InputStream;
import raynna.core.packets.handlers.ButtonHandler;
import raynna.game.player.Player;

public final class WorldInterfacePacketHandler implements WorldPacketHandler {

    @Override
    public boolean handle(WorldPacketsDecoder decoder, int packetId, InputStream stream, int length) {
        Player player = decoder.getPlayer();
        switch (packetId) {
            case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
            case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
            case WorldPacketsDecoder.ACTION_BUTTON3_PACKET:
            case WorldPacketsDecoder.ACTION_BUTTON4_PACKET:
            case WorldPacketsDecoder.ACTION_BUTTON5_PACKET:
            case WorldPacketsDecoder.ACTION_BUTTON6_PACKET:
            case WorldPacketsDecoder.ACTION_BUTTON7_PACKET:
            case WorldPacketsDecoder.ACTION_BUTTON8_PACKET:
            case WorldPacketsDecoder.ACTION_BUTTON9_PACKET:
            case WorldPacketsDecoder.ACTION_BUTTON10_PACKET:
                ButtonHandler.handleButtons(player, stream, packetId);
                return true;
            case WorldPacketsDecoder.ENTER_STRING_PACKET:
                if (!player.isActive() || player.isDead()) {
                    return true;
                }
                stream.readString();
                return true;
            default:
                return false;
        }
    }
}
