package com.rs.java.game.npc.dungeonnering;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.actions.combat.Combat;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.game.player.content.dungeoneering.RoomReference;
import com.rs.java.game.tasks.WorldTask;
import com.rs.java.game.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.java.utils.WeaponTypesLoader.WeaponType;

@SuppressWarnings("serial")
public class FleshspoilerHaasghenahk extends DungeonBoss {

	private List<FleshspoilerSpawn> creatures = new CopyOnWriteArrayList<FleshspoilerSpawn>();

	private Entity cachedTarget;
	private boolean secondStage, useMagicOnly;
	private int fleshTicks;

	public FleshspoilerHaasghenahk(int id, WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(id, tile, manager, reference);
		useMagicOnly = true;
	}

	@Override
	public void processNPC() {
		fleshTicks++;
		if (fleshTicks % 25 == 0) {
			if (secondStage)
				animate(new Animation(14469));
			for (Entity t : getPossibleTargets()) {
				t.gfx(new Graphics(2775));
				t.applyHit(new Hit(this, (int) Utils.random(Utils.random(getMaxHit() * .01, getMaxHit() * .1), getMaxHit()), HitLook.REGULAR_DAMAGE));
			}
		}
		if (secondStage && cachedTarget != null && (cachedTarget.isDead() || cachedTarget.hasFinished())) {
			calculateNextTarget();
			for (DungeonNPC npc : creatures) {
				npc.resetCombat();
				if (cachedTarget != null)
					npc.getCombat().setTarget(cachedTarget);
			}
		}
		super.processNPC();
	}

	private void addFleshCreatures() {
		final WorldTile centerTile = getManager().getTile(getReference(), 8, 7);
		final List<WorldTile> tiles = new LinkedList<WorldTile>();
		for (int i = 0; i < 5; i++) {
			WorldTile tile = Utils.getFreeTile(centerTile, 6);
			World.sendProjectileToTile(this, tile, 2765);
			tiles.add(tile);
		}
		final FleshspoilerHaasghenahk boss = this;
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				for (int index = 0; index < 5; index++)
					creatures.add(new FleshspoilerSpawn(boss, 11910, tiles.get(index), getManager(), getMultiplier() * .65));
			}
		}, 3);
	}

	public void removeFleshCreature(FleshspoilerSpawn spoiler_spawn) {
		creatures.remove(spoiler_spawn);
	}

	@Override
	public void sendDeath(Entity source) {
		if (!secondStage) {
			secondStage = true;
			calculateNextTarget();
			animate(new Animation(14467));
			gfx(new Graphics(2765, 240, 0));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					animate(new Animation(-1));
					addFleshCreatures();
					setNextNPCTransformation(11895);
					setCombatLevel((int) (getCombatLevel() * 0.85)); //15% nerf
					setHitpoints(getMaxHitpoints());
					setLureDelay(Integer.MAX_VALUE);
					setCantFollowUnderCombat(true);
					resetBonuses();
				}
			}, 5);
			return;
		}
		for (DungeonNPC npc : creatures)
			npc.sendDeath(this);
		creatures.clear();
		super.sendDeath(source);
	}
	
	private static final WeaponType[][] WEAKNESS =
		{ { new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.STAB_ATTACK) },};

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}

	public boolean isSecondStage() {
		return secondStage;
	}

	public boolean canUseMagicOnly() {
		return useMagicOnly;
	}

	public void setUseMagicOnly(boolean useMagicOnly) {
		this.useMagicOnly = useMagicOnly;
	}

	public Entity getCachedTarget() {
		return cachedTarget;
	}

	public void calculateNextTarget() {
		cachedTarget = null;
		List<Entity> targets = getPossibleTargets();
		if (targets.isEmpty())
			return;
		cachedTarget = targets.get(Utils.random(targets.size()));
	}
}
