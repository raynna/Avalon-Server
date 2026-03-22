package raynna.game.minigames.godwars.saradomin;

import java.util.ArrayList;

import raynna.game.Animation;
import raynna.game.Entity;
import raynna.game.WorldTile;
import raynna.game.item.Item;
import raynna.game.npc.NPC;
import raynna.game.npc.familiar.Familiar;
import raynna.game.player.Player;
import raynna.game.player.controllers.Controller;
import raynna.game.player.controllers.GodWars;
import raynna.core.tasks.WorldTask;
import raynna.core.tasks.WorldTasksManager;
import raynna.game.npc.combatdata.NpcCombatDefinition;

@SuppressWarnings("serial")
public class GodwarsSaradominFaction extends NPC {

	public GodwarsSaradominFaction(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		if (!withinDistance(new WorldTile(2881, 5306, 0), 200))
			return super.getPossibleTargets();
		else {
			ArrayList<Entity> targets = getPossibleTargets(true, true);
			ArrayList<Entity> targetsCleaned = new ArrayList<Entity>();
			for (Entity t : targets) {
				if (t instanceof GodwarsSaradominFaction || (t instanceof Player && hasGodItem((Player) t)) || t instanceof Familiar)
					continue;
				targetsCleaned.add(t);
			}
			return targetsCleaned;
		}
	}


	public static boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItems().getContainerItems()) {
			if (item == null)
				continue; // shouldn't happen
			String name = item.getDefinitions().getName().toLowerCase();
			// using else as only one item should count
			if (name.contains("saradomin coif") || name.contains("citharede hood") || name.contains("saradomin mitre")
					|| name.contains("saradomin full helm") || name.contains("saradomin halo")
					|| name.contains("torva full helm") || name.contains("pernix cowl") || name.contains("virtus mask"))
				return true;
			else if (name.contains("saradomin cape") || name.contains("saradomin cloak"))
				return true;
			else if (name.contains("holy symbol") || name.contains("citharede symbol")
					|| name.contains("saradomin stole"))
				return true;
			else if (name.contains("saradomin arrow"))
				return true;
			else if (name.contains("saradomin godsword") || name.contains("saradomin sword")
					|| name.contains("saradomin staff") || name.contains("saradomin crozier")
					|| name.contains("zaryte Bow"))
				return true;
			else if (name.contains("saradomin robe top") || name.contains("saradomin d'hide")
					|| name.contains("citharede robe top") || name.contains("monk's robe top")
					|| name.contains("saradomin platebody") || name.contains("torva platebody")
					|| name.contains("pernix body") || name.contains("virtus robe top"))
				return true;
			else if (name.contains("illuminated holy book") || name.contains("holy book")
					|| name.contains("saradomin kiteshield"))
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
							godControler.incrementKillCount(2);
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
