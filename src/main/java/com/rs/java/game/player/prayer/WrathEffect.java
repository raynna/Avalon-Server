package com.rs.java.game.player.prayer;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

import java.util.List;

public class WrathEffect {
    private static final int NORMAL_WRATH_GFX = 437;
    private static final int NORMAL_WRATH_EXPLOSION_GFX = 438;
    private static final int NORMAL_WRATH_DAMAGE = 180;
    private static final int NORMAL_WRATH_RADIUS = 1;
    
    private static final int ANCIENT_WRATH_PROJECTILE = 2260;
    private static final int ANCIENT_WRATH_GFX = 2259;
    private static final int ANCIENT_WRATH_DAMAGE = 250;
    private static final int ANCIENT_WRATH_RADIUS = 2;

    public static void handleWrathEffect(Player deadPlayer, Entity killer) {
        if (!deadPlayer.getPrayer().hasActivePrayers()) return;
        
        if (deadPlayer.getPrayer().isActive(NormalPrayer.REDEMPTION)) {
            handleNormalWrath(deadPlayer, killer);
        } else if (deadPlayer.getPrayer().isActive(AncientPrayer.WRATH)) {
            handleAncientWrath(deadPlayer, killer);
        }
    }

    private static void handleNormalWrath(Player deadPlayer, Entity killer) {
        deadPlayer.gfx(new Graphics(NORMAL_WRATH_GFX));

        applyDamageInRadius(deadPlayer, killer, NORMAL_WRATH_RADIUS, NORMAL_WRATH_DAMAGE);

        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                playExplosionGraphics(deadPlayer, NORMAL_WRATH_EXPLOSION_GFX, NORMAL_WRATH_RADIUS);
            }
        });
    }

    private static void handleAncientWrath(Player deadPlayer, Entity killer) {
        shootProjectiles(deadPlayer, ANCIENT_WRATH_PROJECTILE);

        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                deadPlayer.gfx(new Graphics(ANCIENT_WRATH_GFX));
                applyDamageInRadius(deadPlayer, killer, ANCIENT_WRATH_RADIUS, ANCIENT_WRATH_DAMAGE);
                playExplosionGraphics(deadPlayer, ANCIENT_WRATH_PROJECTILE, ANCIENT_WRATH_RADIUS);
            }
        });
    }

    private static void applyDamageInRadius(Player source, Entity killer, int radius, int maxDamage) {
        if (source.isAtMultiArea()) {
            for (int regionId : source.getMapRegionsIds()) {
                processPlayersInRegion(source, killer, regionId, radius, maxDamage);
                processNPCsInRegion(source, killer, regionId, radius, maxDamage);
            }
        } else {
            if (killer != null && killer != source && !killer.isDead() && !killer.hasFinished() &&
                killer.withinDistance(source, radius) && source.getControlerManager().canHit(killer)) {
                killer.applyHit(new Hit(source, Utils.getRandom(maxDamage), Hit.HitLook.REGULAR_DAMAGE));
            }
        }
    }

    private static void processPlayersInRegion(Player source, Entity killer, int regionId, int radius, int maxDamage) {
        List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
        if (playerIndexes == null) return;
        
        for (int playerIndex : playerIndexes) {
            Player player = World.getPlayers().get(playerIndex);
            if (player == null || !player.hasStarted() || player.isDead() || player.hasFinished() || 
                !player.withinDistance(source, radius) || !player.isCanPvp() || 
                !source.getControlerManager().canHit(player)) {
                continue;
            }
            player.applyHit(new Hit(source, Utils.getRandom(maxDamage), Hit.HitLook.REGULAR_DAMAGE));
        }
    }

    private static void processNPCsInRegion(Player source, Entity killer, int regionId, int radius, int maxDamage) {
        List<Integer> npcIndexes = World.getRegion(regionId).getNPCsIndexes();
        if (npcIndexes == null) return;
        
        for (int npcIndex : npcIndexes) {
            NPC npc = World.getNPCs().get(npcIndex);
            if (npc == null || npc.isDead() || npc.hasFinished() || !npc.withinDistance(source, radius) || 
                !npc.getDefinitions().hasAttackOption() || !source.getControlerManager().canHit(npc)) {
                continue;
            }
            npc.applyHit(new Hit(source, Utils.getRandom(maxDamage), Hit.HitLook.REGULAR_DAMAGE));
        }
    }

    private static void shootProjectiles(Player source, int projectileId) {
        WorldTile center = new WorldTile(source.getX(), source.getY(), source.getPlane());
        
        for (int dx = -2; dx <= 2; dx += 2) {
            for (int dy = -2; dy <= 2; dy += 2) {
                if (dx == 0 && dy == 0) continue;
                World.sendObjectProjectile(source, center.transform(dx, dy, 0), projectileId);
            }
        }
    }

    private static void playExplosionGraphics(Player source, int gfxId, int radius) {
        WorldTile center = new WorldTile(source.getX(), source.getY(), source.getPlane());
        
        World.sendGraphics(source, new Graphics(gfxId), center);
        
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                if (dx == 0 && dy == 0) continue;
                World.sendGraphics(source, new Graphics(gfxId), center.transform(dx, dy, 0));
            }
        }
    }
}