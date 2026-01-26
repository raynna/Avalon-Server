package com.rs.java.game.npc.combat.impl;

import com.rs.java.game.*;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.combat.CombatScript;
import com.rs.java.game.npc.combat.NpcCombatCalculations;
import com.rs.java.game.player.Player;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcAttackStyle;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public class GiantMoleCombat extends CombatScript {

	private static final WorldTile[] COORDS = { new WorldTile(1737, 5228, 0), new WorldTile(1751, 5233, 0),
			new WorldTile(1778, 5237, 0), new WorldTile(1736, 5227, 0), new WorldTile(1780, 5152, 0),
			new WorldTile(1758, 5162, 0), new WorldTile(1745, 5169, 0), new WorldTile(1760, 5183, 0) };

	@Override
	public Object[] getKeys() {
		return new Object[] { 3340 };
	}

	@Override
	public int attack(final NPC npc, Entity target) {
		NpcCombatDefinition defs = npc.getCombatDefinitions();
		if (Utils.random(5) == 0) { // bury
			npc.animate(new Animation(3314));
			npc.setCantInteract(true);
			npc.getCombat().removeTarget();
			final Player player = (Player) (target instanceof Player ? target : null);
			if (player != null)
				player.getInterfaceManager().sendTab(player.getInterfaceManager().hasRezizableScreen() ? 1 : 11, 226);
			final WorldTile middle = npc.getMiddleWorldTile();
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (player != null)
						player.getPackets().closeInterface(player.getInterfaceManager().hasRezizableScreen() ? 1 : 11);
					npc.setCantInteract(false);
					if (npc.isDead())
						return;
					World.sendGraphics(npc, new Graphics(572), middle);
					World.sendGraphics(npc, new Graphics(571),
							new WorldTile(middle.getX(), middle.getY() - 1, middle.getPlane()));
					World.sendGraphics(npc, new Graphics(571),
							new WorldTile(middle.getX(), middle.getY() + 1, middle.getPlane()));
					World.sendGraphics(npc, new Graphics(571),
							new WorldTile(middle.getX() - 1, middle.getY() - 1, middle.getPlane()));
					World.sendGraphics(npc, new Graphics(571),
							new WorldTile(middle.getX() - 1, middle.getY() + 1, middle.getPlane()));
					World.sendGraphics(npc, new Graphics(571),
							new WorldTile(middle.getX() + 1, middle.getY() - 1, middle.getPlane()));
					World.sendGraphics(npc, new Graphics(571),
							new WorldTile(middle.getX() + 1, middle.getY() + 1, middle.getPlane()));
					World.sendGraphics(npc, new Graphics(571),
							new WorldTile(middle.getX() - 1, middle.getY(), middle.getPlane()));
					World.sendGraphics(npc, new Graphics(571),
							new WorldTile(middle.getX() + 1, middle.getY(), middle.getPlane()));
					npc.setNextWorldTile(new WorldTile(COORDS[Utils.random(COORDS.length)]));
					npc.animate(new Animation(3315));

				}

			}, 2);

		} else {
			npc.animate(new Animation(defs.getAttackAnim()));
			Hit meleeHit = npc.meleeHit(target, defs.getMaxHit());
			delayHit(npc, target, 0, meleeHit);
		}
		return npc.getAttackSpeed();
	}

}
