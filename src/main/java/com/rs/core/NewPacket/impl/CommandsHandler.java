// CommandsHandler.java (60)
package com.rs.core.NewPacket.impl;

import com.rs.core.NewPacket.*;
import com.rs.java.game.player.content.Commands;
import com.rs.java.utils.Logger;
import com.rs.kotlin.game.player.command.CommandRegistry;

public final class CommandsHandler implements PacketHandler {
    public static final int ID = 60;
    @Override public PacketDefinition definition() { return PacketDefinition.remaining(ID); } // -1
    @Override public void handle(PacketContext ctx) {
        var p = ctx.player; var s = ctx.stream;
        if (!p.isActive()) return;
        boolean clientCommand = s.readUnsignedByte() == 1;
        boolean unknown = s.readUnsignedByte() == 1;
        String command = s.readString();
        if (CommandRegistry.execute(p, command)) return;
        if (!Commands.processCommand(p, command, true, clientCommand)) Logger.log(this, "Command: " + command);
    }
}
