package raynna.core.packets.decode.world.handler;

import raynna.core.packets.decode.WorldPacketsDecoder;
import raynna.core.packets.decode.world.support.*;

import raynna.core.packets.InputStream;

public interface WorldPacketHandler {

    boolean handle(WorldPacketsDecoder decoder, int packetId, InputStream stream, int length);
}
