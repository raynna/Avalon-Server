package com.rs.java.game.minigames.godwars.zamorak;

import java.util.ArrayList;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.controllers.Controller;
import com.rs.java.game.player.controllers.GodWars;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

@SuppressWarnings("serial")
public class GodwarsZammorakFaction extends NPC {

	public GodwarsZammorakFaction(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(6000);// Lurable boss
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		if (!withinDistance(new WorldTile(2881, 5306, 0), 200))
			return super.getPossibleTargets();
		else {
			ArrayList<Entity> targets = getPossibleTargets(true, true);
			ArrayList<Entity> targetsCleaned = new ArrayList<Entity>();
			for (Entity t : targets) {
				if (t instanceof GodwarsZammorakFaction || (t instanceof Player && hasGodItem((Player) t)) || t instanceof Familiar)
					continue;
				targetsCleaned.add(t);
			}
			return targetsCleaned;
		}
	}


	public static boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItems().getContainerItems()) {
			if (item == null || item.getId() == -1)
				continue; // shouldn't happen
			String name = item.getDefinitions().getName().toLowerCase();
			if (name.contains("zamorak coif") || name.contains("zamorak mitre") || name.contains("zamorak full helm")
					|| name.contains("zamorak halo") || name.contains("torva full helm") || name.contains("pernix cowl")
					|| name.contains("virtus mask"))
				return true;
			else if (name.contains("zamorak cape") || name.contains("zamorak cloak"))
				return true;
			else if (name.contains("unholy symbol") || name.contains("zamorak stole"))
				return true;
			else if (name.contains("illuminated unholy book") || name.contains("unholy book")
					|| name.contains("zamorak kiteshield"))
				return true;
			else if (name.contains("zamorak arrows"))
				return true;
			else if (name.contains("zamorak godsword") || name.contains("zamorakian spear")
					|| name.contains("zamorak staff") || name.contains("zamorak crozier")
					|| name.contains("zaryte Bow"))
				return true;
			else if (name.contains("zamorak d'hide") || name.contains("zamorak platebody")
					|| name.contains("torva platebody") || name.contains("pernix body")
					|| name.contains("virtus robe top"))
				return true;
			else if (name.contains("zamorak robe") || name.contains("zamorak robe bottom ")
					|| name.contains("zamorak chaps") || name.contains("zamorak platelegs")
					|| name.contains("zamorak plateskirt") || name.contains("torva platelegs")
					|| name.contains("pernix chaps") || name.contains("virtus robe legs"))
				return true;
			else if (name.contains("zamorak vambraces"))
				return true;
		}
		return false;
	}

	public void sendDeath(final Entity source) {
		final NpcCombatDefinition defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		animate(-1);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					animate(new Animation(defs.getDeathAnim()));
				} else if (loop >= defs.getDeathDelay()) {
					if (source instanceof Player) {
						Player player = (Player) source;
						Controller controller = player.getControlerManager().getControler();
						if (controller != null && controller instanceof GodWars) {
							GodWars godControler = (GodWars) controller;
							godControler.incrementKillCount(3);
						}
					}
					drop();
					reset();
					setLocation(getRespawnTile());
					finish();
					if (!isSpawned())
						setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
}
