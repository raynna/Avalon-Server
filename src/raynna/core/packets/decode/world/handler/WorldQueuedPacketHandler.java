package raynna.core.packets.decode.world.handler;

import raynna.core.packets.decode.WorldPacketsDecoder;
import raynna.core.packets.decode.world.support.*;

import raynna.core.packets.InputStream;
import raynna.game.player.Player;

@FunctionalInterface
public interface WorldQueuedPacketHandler {

    void handle(Player player, InputStream stream);
}
