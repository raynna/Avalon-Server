package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.player.Player;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.world.projectile.Projectile;
import com.rs.kotlin.game.world.projectile.ProjectileManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KalphiteQueenCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[]{"Kalphite Queen"};
	}

	// -------------------- Attacks --------------------

	private void attackRanged(NPC npc, Entity target) {
		npc.animate(new Animation(npc.getId() == 1158 ? 6240 : 6234));
		for (Entity t : npc.getPossibleTargets()) {
			delayHit(npc, t, 0, npc.rangedHit(t, npc.getMaxHit()));
			ProjectileManager.sendSimple(
					Projectile.ELEMENTAL_SPELL, 289,
					npc, t
			);
		}
	}

	private void attackMagic(NPC npc, Entity target) {
		npc.animate(new Animation(npc.getId() == 1158 ? 6240 : 6234));
		npc.gfx(new Graphics(npc.getId() == 1158 ? 278 : 279));

		Entity chosen = target != null ? target : getClosestTarget(npc, new ArrayList<>());
		if (chosen == null) return;

		if (chosen instanceof Player p) {
			new ArrayList<Player>().add(p);
		}

		ProjectileManager.sendWithGraphic(
				Projectile.ELEMENTAL_SPELL, 280,
				npc, chosen,
				new Graphics(281)
		);
		delayHit(npc, chosen, 2, npc.magicHit(chosen, npc.getMaxHit()));
	}

	private void attackMelee(NPC npc, Entity target) {
		npc.animate(new Animation(npc.getId() == 1158 ? 6241 : 6235));
		delayHit(npc, target, 0, npc.meleeHit(target, npc.getMaxHit()));
	}


	private Player getClosestTarget(Entity fromEntity, List<Player> excluded) {
		if (fromEntity == null) return null;

		List<Player> nearby = new ArrayList<>();
		for (int regionId : fromEntity.getMapRegionsIds()) {
			List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playerIndexes == null) continue;

			for (int idx : playerIndexes) {
				Player player = World.getPlayers().get(idx);
				if (player == null
						|| excluded.contains(player)
						|| !player.withinDistance(fromEntity)) {
					continue;
				}
				nearby.add(player);
			}
		}

		return nearby.stream()
				.min(Comparator.comparingInt(p -> Utils.getDistance(p, fromEntity)))
				.orElse(null);
	}

	@Override
	public int attack(NPC npc, Entity target) {

		int style = Utils.random(3);
		if (style == 0) { // melee attempt
			int dx = target.getX() - npc.getX();
			int dy = target.getY() - npc.getY();
			int size = npc.getSize();
			if (dx > size || dx < -1 || dy > size || dy < -1) {
				style = Utils.random(2) + 1;
			} else {
				attackMelee(npc, target);
				return npc.getAttackSpeed();
			}
		}

		if (style == 1) {
			attackRanged(npc, target);
		} else {
			attackMagic(npc, target);
		}

		return npc.getAttackSpeed();
	}
}
