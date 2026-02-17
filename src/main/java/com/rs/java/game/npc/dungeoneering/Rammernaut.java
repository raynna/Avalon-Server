package com.rs.java.game.npc.dungeoneering;

import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.ForceTalk;
import com.rs.java.game.Hit;
import com.rs.java.game.Hit.HitLook;
import com.rs.java.game.NewForceMovement;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.dungeoneering.DungeonManager;
import com.rs.java.game.player.content.dungeoneering.RoomReference;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;

@SuppressWarnings("serial")
public class Rammernaut extends DungeonBoss {

	private transient Player chargeTarget;
	private int count;
	private boolean requestSpecNormalAttack;

	public Rammernaut(int id, WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(id, tile, manager, reference);
		setForceFollowClose(true);
	}

	public void fail() {
		animate(new Animation(13707));
		setNextForceTalk(new ForceTalk("Oooof!!"));
		count = -14;
	}

	public void sucess() {
		resetWalkSteps();
		animate(new Animation(13698));
		applyStunHit(chargeTarget, (int) (chargeTarget.getMaxHitpoints() * 0.6));
		requestSpecNormalAttack = true;
		count = -12;
	}

	public void applyStunHit(final Entity entity, int maxHit) {
		entity.applyHit(new Hit(this, Utils.random(maxHit) + 1, HitLook.REGULAR_DAMAGE));
		entity.addFreezeDelay(6, true);
		if (entity instanceof Player) {
			Player player = (Player) entity;
			player.stopAll();
			player.getPackets().sendGameMessage("You've been stunned.");
			player.addFreezeDelay(6, true);
			if (player.getPrayer().hasActivePrayers()) {
				player.getPackets().sendGameMessage("Your prayers have been disabled.");
				player.setPrayerDelay(8);// Five seconds
			}
			final NPC npc = this;
			WorldTasksManager.schedule(new WorldTask() {
				private int ticks;
				private WorldTile tile;

				@Override
				public void run() {
					ticks++;
					if (ticks == 1) {
						/*byte[] dirs = Utils.getDirection(getDirection());
						for (int distance = 6; distance >= 0; distance--) {
							tile = new WorldTile(new WorldTile(entity.getX() + (dirs[0] * distance), entity.getY() + (dirs[1] * distance), entity.getPlane()));
							if (World.isFloorFree(tile.getPlane(), tile.getX(), tile.getY()) && getManager().isAtBossRoom(tile))
								break;
							else if (distance == 0)
								tile = new WorldTile(entity);
						}*///TODO 
						entity.faceEntity(npc);
						entity.animate(new Animation(10070));
						entity.setNextForceMovement(new NewForceMovement(entity, 0, tile, 2, entity.getDirection()));
					} else if (ticks == 2) {
						entity.setNextWorldTile(tile);
						stop();
						return;
					}
				}
			}, 0, 0);
		}
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		if (chargeTarget != null) {
			setNextFaceEntity(chargeTarget);
			if (count == 0) {
				setNextForceTalk(new ForceTalk("CHAAAAAARGE!"));
				setRun(true);
			} else if (count == -10) {
				setRun(false);
				resetWalkSteps();
				calcFollow(chargeTarget, true);
			} else if (count == -8) {
				setChargeTarget(null);
			} else if (count > 2) {
				resetWalkSteps();
				/*
				 * skip first step else it stucks ofc
				 */
				calcFollow(chargeTarget, true);

				if (count != 3 && !World.isTileFree(getPlane(), getX(), getY(), getSize()))
					fail();
				else if (Utils.isOnRange(getX(), getY(), getSize(), chargeTarget.getX(), chargeTarget.getY(), chargeTarget.getSize(), 0))
					sucess();
				else if (!hasWalkSteps())
					fail();
			}
			count++;
			return;
		}
		super.processNPC();

		/*int chargeCount = getChargeCount(npc);
		if (chargeCount > 1) {
		    if (chargeCount == 2) {
			npc.setNextForceTalk(new ForceTalk("CHAAAAAARGE!"));
			npc.setRun(true);
			setChargeCount(npc, 3);
			return defs.getAttackDelay();
		    } else if (chargeCount == 3) {
			npc.calcFollow(target, true); //gotta be changed later
			if (Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) {
			    npc.setNextAnimation(new Animation(13705));
			    setChargeCount(npc, 0);
			    return defs.getAttackDelay();
			}
		    }
		//    System.out.println("Time to charge " + chargeCount);
		    return 0;
		}*/
	}

	public void setChargeTarget(Player target) {
		this.chargeTarget = target;
		getCombat().removeTarget();
		count = 0;
	}

	public boolean isRequestSpecNormalAttack() {
		return requestSpecNormalAttack;
	}

	public void setRequestSpecNormalAttack(boolean requestSpecNormalAttack) {
		this.requestSpecNormalAttack = requestSpecNormalAttack;
	}
}
