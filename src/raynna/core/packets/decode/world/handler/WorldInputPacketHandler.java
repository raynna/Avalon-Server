package raynna.core.packets.decode.world.handler;

import raynna.core.packets.decode.WorldPacketsDecoder;
import raynna.core.packets.decode.world.support.*;

import raynna.core.packets.InputStream;

public final class WorldInputPacketHandler implements WorldPacketHandler {

    @Override
    public boolean handle(WorldPacketsDecoder decoder, int packetId, InputStream stream, int length) {
        switch (packetId) {
            case WorldPacketsDecoder.PLAYER_OPTION_3_PACKET,
                    WorldPacketsDecoder.PLAYER_OPTION_7_PACKET,
                    WorldPacketsDecoder.PLAYER_OPTION_8_PACKET,
                    WorldPacketsDecoder.PLAYER_OPTION_10_PACKET -> {
                return true;
            }
            case WorldPacketsDecoder.ENTER_NAME_PACKET -> {
                WorldPromptSupport.handleEnterName(decoder.getPlayer(), stream);
                return true;
            }
            case WorldPacketsDecoder.ENTER_LONG_TEXT_PACKET -> {
                WorldPromptSupport.handleEnterLongText(decoder.getPlayer(), stream);
                return true;
            }
            case WorldPacketsDecoder.ENTER_INTEGER_PACKET -> {
                WorldPromptSupport.handleEnterInteger(decoder.getPlayer(), stream);
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
