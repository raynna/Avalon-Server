package raynna.game.npc.dungeoneering;

import raynna.game.Animation;
import raynna.game.Hit;
import raynna.game.Hit.HitLook;
import raynna.game.WorldObject;
import raynna.game.WorldTile;
import raynna.game.player.CombatDefinitions;
import raynna.game.player.actions.combat.Combat;
import raynna.game.player.content.dungeoneering.DungeonManager;
import raynna.game.player.content.dungeoneering.RoomReference;
import raynna.util.Utils;
import raynna.util.WeaponTypesLoader.WeaponType;

@SuppressWarnings("serial")
public final class GluttonousBehemoth extends DungeonBoss {

	private WorldObject heal;
	private int ticks;

	public GluttonousBehemoth(int id, WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(id, tile, manager, reference);
		setCantFollowUnderCombat(true);
	}

	public void setHeal(WorldObject food) {
		ticks = 0;
		heal = food;
		removeTarget();
	}

	private static final WeaponType[][] WEAKNESS =
	{
	{ new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.SLASH_ATTACK), new WeaponType(Combat.MELEE_TYPE, CombatDefinitions.CRUSH_ATTACK) }};

	public WeaponType[][] getWeaknessStyle() {
		return WEAKNESS;
	}

	@Override
	public void processNPC() {
		if (heal != null) {
			setNextFaceEntity(null);
			ticks++;
			if (ticks == 1) {
				calcFollow(heal, true);
			} else if (ticks == 5) {
				animate(new Animation(13720));
			} else if (ticks < 900 && ticks > 7) {
				if (getHitpoints() >= (getMaxHitpoints() * 0.75)) {
					animate(new Animation(-1));
					addWalkSteps(getRespawnTile().getX(), getRespawnTile().getY());
					ticks = 995;
					return;
				}
				applyHit(new Hit(this, 50 + Utils.getRandom(50), HitLook.HEALED_DAMAGE));
				animate(new Animation(13720));
			} else if (ticks > 1000)
				heal = null;
			return;
		}
		super.processNPC();
	}

}
