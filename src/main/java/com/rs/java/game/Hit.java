package com.rs.java.game;

import com.rs.java.game.player.Player;

public final class Hit {

	public enum HitLook {

		MISSED(8), REGULAR_DAMAGE(3), MELEE_DAMAGE(0), RANGE_DAMAGE(1), MAGIC_DAMAGE(2), REFLECTED_DAMAGE(
				4), ABSORB_DAMAGE(5), POISON_DAMAGE(6), DESEASE_DAMAGE(7), HEALED_DAMAGE(9), CRITICAL_DAMAGE(
						11), CANNON_DAMAGE(13);
		private int mark;

		HitLook(int mark) {
			this.mark = mark;
		}

		public int getMark() {
			return mark;
		}
	}

	private Entity source;
	private HitLook look;
	private int damage;
	private int maxHit;
	private int baseMaxHit;
	public boolean critical;
	public boolean landed;
	private Hit soaking;
	private int delay;

	public void setCriticalMark() {
		critical = true;
	}

	public boolean checkCritical(int baseDamage, int maxHit) {
        return baseDamage >= Math.floor(maxHit * 0.95);
    }

	public void setHealHit() {
		look = HitLook.HEALED_DAMAGE;
		critical = false;
	}

	public void setMissedHit() {
		look = HitLook.MISSED;
		critical = false;
	}

	public boolean isCombatLook() {
		return look == HitLook.MELEE_DAMAGE || look == HitLook.RANGE_DAMAGE || look == HitLook.MAGIC_DAMAGE;
	}



	public Hit(Entity source, int damage, HitLook look) {
		this(source, damage, 0, look, 0, true);
	}

	public Hit(Entity source, int damage, HitLook look, int delay) {
		this(source, damage, 0, look, delay, true);
	}

	public Hit(Entity source, int damage, int maxHit, HitLook look) {
		this(source, damage, maxHit, look, 0, true);
	}

	public Hit(Entity source, int damage, int maxHit, HitLook look, boolean landed) {
		this(source, damage, maxHit, look, 0, landed);
	}

	public Hit(Entity source, int damage, int maxHit, HitLook look, int delay, boolean landed) {
		this.source = source;
		this.damage = damage;
		this.maxHit = maxHit;
		this.look = look;
		this.delay = delay;
		this.landed = landed;
	}

	public boolean missed() {
		return damage == 0;
	}

	public boolean interactingWith(Player player, Entity victm) {
		return player == victm || player == source;
	}

	public int getMark(Player player, Entity victm) {
		if (HitLook.HEALED_DAMAGE == look)
			return look.getMark();
		if (damage == 0) {
			return HitLook.MISSED.getMark();
		}
		if (damage == -1)
			return -1;
		int mark = look.getMark();
		if (critical)
			mark += 10;
		if (!interactingWith(player, victm))
			mark += 14;
		return mark;
	}

	public HitLook getLook() {
		return look;
	}
	
	public Hit setLook(HitLook look) {
		this.look = look;
		return this;
	}

	public int getDamage() {
		return damage;
	}

	public int getMaxHit() { return maxHit; }
	public int getBaseMaxHit() { return baseMaxHit; }

	public void setMaxHit(int maxHit) { this.maxHit = maxHit; }
	public void setBaseMaxHit(int maxHit) { this.baseMaxHit = maxHit; }

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public Entity getSource() {
		return source;
	}

	public void setSource(Entity source) {
		this.source = source;
	}

	public boolean isCriticalHit() {
		return critical;
	}

	public Hit getSoaking() {
		return soaking;
	}

	public void setSoaking(Hit soaking) {
		this.soaking = soaking;
	}

	public int getDelay() {
		return delay;
	}

	public Hit copy() {
		Hit copy = new Hit(this.source, this.damage, this.maxHit, this.look, this.delay, this.landed);
		copy.critical = this.critical;
		copy.soaking = this.soaking; // shallow copy, adjust if needed
		return copy;
	}

	// Overloaded copy with new damage example (optional)
	public Hit copyWithDamage(int newDamage) {
		Hit copy = new Hit(this.source, newDamage, this.maxHit, this.look, this.delay, this.landed);
		copy.critical = this.critical;
		copy.soaking = this.soaking;
		return copy;
	}

}
