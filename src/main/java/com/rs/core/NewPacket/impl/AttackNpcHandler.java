package com.rs.core.NewPacket.impl;

import com.rs.core.NewPacket.*;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.player.combat.CombatAction;

public final class AttackNpcHandler implements PacketHandler {
    public static final int ID = 20;
    @Override public PacketDefinition definition() { return PacketDefinition.fixed(ID, 3); }
    @Override public boolean enqueueAsLogicPacket() { return true; }

    @Override
    public void handle(PacketContext ctx) {
        Player player = ctx.player;
        var stream = ctx.stream;

        if (!player.hasStarted() || !player.clientHasLoadedMapRegion() || player.isDead()) return;
        if (player.getLockDelay() > Utils.currentTimeMillis()) return;

        int npcIndex = stream.readUnsignedShort128();
        boolean forceRun = stream.read128Byte() == 1;
        if (forceRun) player.setRun(true);

        NPC npc = World.getNPCs().get(npcIndex);
        if (npc == null || npc.isDead() || npc.hasFinished()
                || !player.getMapRegionsIds().contains(npc.getRegionId())
                || !npc.getDefinitions().hasAttackOption()) return;

        if (!player.getControlerManager().canAttack(npc)) return;

        if (npc instanceof Familiar) {
            Familiar fam = (Familiar) npc;
            if (fam == player.getFamiliar()) { player.getPackets().sendGameMessage("You can't attack your own familiar."); return; }
            if (!fam.canAttack(player)) { player.getPackets().sendGameMessage("You can't attack this npc."); return; }
        } else if (!npc.isForceMultiAttacked()) {
            if (player.isAtMultiArea() && !npc.isAtMultiArea()) {
                if (npc.getAttackedBy() != player && npc.getAttackedByDelay() > Utils.currentTimeMillis()) {
                    player.getPackets().sendGameMessage("This npc is already in combat."); return;
                }
            }
            if (!npc.isAtMultiArea() && !player.isAtMultiArea()) {
                if (player.getAttackedBy() != npc && player.getAttackedByDelay() > Utils.currentTimeMillis()) {
                    player.getPackets().sendGameMessage("You are already in combat."); return;
                }
                if (npc.getAttackedBy() != player && npc.getAttackedByDelay() > Utils.currentTimeMillis()) {
                    player.getPackets().sendGameMessage("This npc is already in combat."); return;
                }
            }
        }

        player.stopAll(false);
        player.getNewActionManager().setAction(new com.rs.kotlin.game.player.combat.CombatAction(npc));
    }
}
