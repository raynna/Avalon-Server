package com.rs.java.game.npc.others;

import java.util.List;

import com.rs.java.game.Animation;
import com.rs.java.game.Graphics;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Equipment;
import com.rs.java.game.player.Player;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

public final class TormentedDemon extends NPC {

	private static final long serialVersionUID = -3391513753727542071L;

	// Constants for combat/prayer
	private static final int MAX_PRAYERS = 3;
	private static final int MELEE = 0;
	private static final int MAGIC = 1;
	private static final int RANGE = 2;

	// Damage & shield constants
	private static final double SHIELD_DAMAGE_REDUCTION = 0.25;
	private static final int SHIELD_COOLDOWN = 100;
	private static final int DAMAGE_THRESHOLD = 310;
	private static final int MIN_DAMAGE_ON_MISS = 20;

	// Attack tick constants
	private static final int MAX_ATTACK_TICKS = 26;
	private static final int ATTACK_STYLE_CHANGE_DELAY = 6;

	// Animations & graphics
	private static final Animation RANDOM_PROJECTILE_ANIM = new Animation(10918);
	private static final Graphics SHIELD_GFX = new Graphics(1885);
	private static final Graphics SPLASH_GFX = new Graphics(1883);

	// Projectile constants
	private static final int RANDOM_PROJECTILE_ID = 1884;
	private static final int RANDOM_PROJECTILE_RADIUS = 3;
	private static final int RANDOM_PROJECTILE_RANGE = 7;

	private boolean[] demonPrayer;
	private int[] cachedDamage;
	private int shieldTimer;
	private int currentCombatType;
	private int previousCombatType;
	private int attackTicks = 0;
	private int currentType = 0;

	public TormentedDemon(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
						  boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		demonPrayer = new boolean[MAX_PRAYERS];
		cachedDamage = new int[MAX_PRAYERS];
		setForceTargetDistance(64);
		shieldTimer = 0;
		switchPrayers(Utils.random(1, 2));
	}

	public void switchPrayers(int type) {
		transformIntoNPC(8349 + type);
		demonPrayer[type] = true;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead()) return;

		if (getCombat().process()) {
			incrementAttackTicks();
			handleAttackStyleChange();
			decrementShieldTimer();
			checkDamageThreshold();
		}
	}

	private void incrementAttackTicks() {
		if (attackTicks < MAX_ATTACK_TICKS) attackTicks++;
	}

	private void handleAttackStyleChange() {
		if (attackTicks < MAX_ATTACK_TICKS) return;

		resetAttackTicks();

		int attackType = Utils.getRandom(2);
		while (attackType == getCurrentCombatType()) {
			attackType = Utils.getRandom(2);
		}

		sendRandomProjectile();
		setPreviousCombatType(getCurrentCombatType());
		setCurrentCombatType(attackType);
		getCombat().setAttackDelay(ATTACK_STYLE_CHANGE_DELAY);
	}

	private void decrementShieldTimer() {
		if (shieldTimer > 0) shieldTimer--;
	}

	private void checkDamageThreshold() {
		if (cachedDamage[currentType] >= DAMAGE_THRESHOLD) {
			demonPrayer = new boolean[MAX_PRAYERS];
			switchPrayers(currentType);
			cachedDamage = new int[MAX_PRAYERS];
		}
	}

	@Override
	public void handleIncommingHit(final Hit hit) {
		super.handleIncommingHit(hit);
		if (shieldTimer <= 0)
			gfx(SHIELD_GFX);
		if (shieldTimer <= 0 && hit.getDamage() > 0) {
			hit.setDamage((int) (hit.getDamage() * SHIELD_DAMAGE_REDUCTION));
		}

		switch (hit.getLook()) {
			case MELEE_DAMAGE -> handleMeleeHit(hit);
			case MAGIC_DAMAGE -> handleMagicHit(hit);
			case RANGE_DAMAGE, CANNON_DAMAGE -> handleRangedHit(hit);
		}

		if (hit.getDamage() <= 0) {
			cachedDamage[currentType] += MIN_DAMAGE_ON_MISS;
		}

		if (hit.getSource() instanceof Player player) {
			if (shieldTimer <= 0 && hit.getDamage() > 0)
				player.getPackets().sendGameMessage("The demon shield absorbs most of your damage.");
		}
	}

	private void handleMeleeHit(Hit hit) {
		currentType = MELEE;
		if (hit.getSource() instanceof Player player) {
			if (demonPrayer[currentType] && !Boolean.TRUE.equals(player.getTemporaryAttributtes().get("VERAC_EFFECT"))) {
				hit.setDamage(0);
			} else {
				Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
				if (weapon.isAnyOf("item.darklight", "item.silverlight") && hit.getDamage() > 0) {
					shieldTimer = SHIELD_COOLDOWN;
					player.getPackets().sendGameMessage("The demon is temporarily weakened by your weapon.");
				}
				cachedDamage[currentType] += hit.getDamage();
			}
		}
	}

	private void handleMagicHit(Hit hit) {
		currentType = MAGIC;
		if (demonPrayer[currentType]) hit.setDamage(0);
		else cachedDamage[currentType] += hit.getDamage();
	}

	private void handleRangedHit(Hit hit) {
		currentType = RANGE;
		if (demonPrayer[currentType]) hit.setDamage(0);
		else cachedDamage[currentType] += hit.getDamage();
	}

	public void sendRandomProjectile() {
		WorldTile tile = new WorldTile(getX() + Utils.random(-RANDOM_PROJECTILE_RANGE, RANDOM_PROJECTILE_RANGE),
				getY() + Utils.random(-RANDOM_PROJECTILE_RANGE, RANDOM_PROJECTILE_RANGE), getPlane());
		animate(RANDOM_PROJECTILE_ANIM);
		World.sendGroundProjectile(this, tile, RANDOM_PROJECTILE_ID);

		NPC npc = this;
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				for (int regionId : getMapRegionsIds()) {
					List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
					if (playerIndexes == null) continue;

					for (int playerIndex : playerIndexes) {
						Player player = World.getPlayers().get(playerIndex);
						if (player == null || player.isDead() || player.hasFinished() || !player.hasStarted()
								|| Utils.getDistance(player, tile) > RANDOM_PROJECTILE_RADIUS || player.getAttackedBy() != npc)
							continue;

						player.gfx(SPLASH_GFX);
						player.message("The demon's magical attack splashes on you.");
						player.applyHit(new Hit(npc, Utils.random(138, 289), HitLook.MAGIC_DAMAGE, 0));
					}
				}
			}
		}, 2);
	}

	// Getters and setters
	public int getCurrentCombatType() { return currentCombatType; }
	public void setCurrentCombatType(int combatType) { this.currentCombatType = combatType; }
	public int getPreviousCombatType() { return previousCombatType; }
	public void setPreviousCombatType(int combatType) { this.previousCombatType = combatType; }
	public int getAttackTicks() { return attackTicks; }
	public void resetAttackTicks() { attackTicks = 0; }
}
