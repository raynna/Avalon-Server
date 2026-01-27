package com.rs.java.game.npc.familiar;

import java.io.Serializable;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.World;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.npc.glacior.Glacyte;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.TickManager;
import com.rs.java.game.player.actions.skills.summoning.Summoning;
import com.rs.java.game.player.actions.skills.summoning.Summoning.Pouch;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.java.utils.Utils;
import com.rs.kotlin.game.npc.combatdata.NpcCombatDefinition;

public abstract class Familiar extends NPC implements Serializable {

	private static final long serialVersionUID = -3255206534594320406L;

	private transient Player owner;
	private String ownerUsername;
	private transient int ticks;
	private transient int trackTimer;
	public int specialEnergy;
	public transient boolean specialActivated;
	private transient boolean finished = false;
	private transient boolean trackDrain;

	private BeastOfBurden bob;
	private Pouch pouch;

	public Familiar(Player owner, Pouch pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(Summoning.getNPCId(pouch.getRealPouchId()), tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
		this.ownerUsername = owner.getUsername();
		this.owner = owner;
		this.pouch = pouch;
		resetTickets();
		specialEnergy = 60;
		if (getBOBSize() > 0)
			bob = new BeastOfBurden(getBOBSize());
		call(true);
		setRun(true);
	}

	public void setPlayer(String username) {
		this.owner = World.getPlayer(username);
	}

	public void store() {
		if (bob == null)
			return;
		bob.open();
	}

	public boolean canDepositOnly() {
		return getDefinitions().hasOption("withdraw");
	}

	public boolean canStoreEssOnly() {
		return pouch == Pouch.ABYSSAL_LURKER || pouch == Pouch.ABYSSAL_PARASITE || pouch == Pouch.ABYSSAL_TITAN;
	}

	public int getOriginalId() {
		return Summoning.getNPCId(pouch.getRealPouchId());
	}

	public void resetTickets() {
		ticks = (int) (pouch.getPouchTime() / 1000 / 30);
		trackTimer = 0;
	}

	private void sendFollow() {
		if (getLastFaceEntity() != owner.getClientIndex())
			setNextFaceEntity(owner);
		if (isFrozen())
			return;
		int size = getSize();
		int targetSize = owner.getSize();
		if (Utils.colides(getX(), getY(), size, owner.getX(), owner.getY(), targetSize) && !owner.hasWalkSteps()) {
			resetWalkSteps();
			if (!addWalkSteps(owner.getX() + targetSize, getY())) {
				resetWalkSteps();
				if (!addWalkSteps(owner.getX() - size, getY())) {
					resetWalkSteps();
					if (!addWalkSteps(getX(), owner.getY() + targetSize)) {
						resetWalkSteps();
						if (!addWalkSteps(getX(), owner.getY() - size)) {
							return;
						}
					}
				}
			}
			return;
		}
		resetWalkSteps();
		if (!clipedProjectile(owner, true) || !Utils.isOnRange(getX(), getY(), size, owner.getX(), owner.getY(), targetSize, 0))
			calcFollow(owner, 2, true, false);
	}

	public void startDialogue() {
		if (owner.getFamiliar().getId() == 7343 || owner.getFamiliar().getId() == 7344) {
			owner.getDialogueManager().startDialogue("SteelTitan");
		} else if (owner.getFamiliar().getId() == 7375 || owner.getFamiliar().getId() == 7376) {
			owner.getDialogueManager().startDialogue("IronTitan");
		} else if (owner.getFamiliar().getId() == 7339 || owner.getFamiliar().getId() == 7340) {
			owner.getDialogueManager().startDialogue("GeyserTitan");
		} else if (owner.getFamiliar().getId() == 6822 || owner.getFamiliar().getId() == 6823) {
			owner.getDialogueManager().startDialogue("UnicornStallion");
		} else {
			owner.getDialogueManager().startDialogue("SimpleMessage", "Your Familiar does not support a dialogue yet.");
		}
	}

	public String getOwnerUsername() {
		return this.ownerUsername;
	}

	@SuppressWarnings("unused")
	private enum FamiliarForceTalk {

		WOLPERTINGER(6869, 6870, new String[] { "Mew!", "Miii!", "Raawr!" }), PACK_YAK(6873, 6874,
				new String[] { "Baroo baroo!", "Barooo!" }), STEEL_TITAN(7343, 7344,
						new String[] { "I am legend!", "Out of the way!" }), UNICORN_STALLION(6822, 6823,
								new String[] { "Neigh!", "Neigh neigh!" });

		private int id;
		private int id2;
		private String[] word;

		FamiliarForceTalk(int id, int id2, String[] word) {
			this.id = id;
			this.word = word;
		}

		public int getId() {
			return id;
		}

		public int getId2() {
			return id2;
		}

		public String[] getWord() {
			return word;
		}

	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		System.out.println("Familiar ID: " + getId() + " | Original ID: " + getOriginalId() +
				" | Owner PvP: " + owner.isCanPvp() +
				" | Time: " + Utils.currentTimeMillis());
		System.out.println("Familiar count for owner " + owner.getUsername() + ": " +
				World.getNPCs().stream()
						.filter(n -> n instanceof Familiar)
						.filter(f -> ((Familiar)f).getOwner() == owner)
						.count());
		trackTimer++;
		if (trackTimer == 50) {
			trackTimer = 0;
			ticks--;
			if (trackDrain)
				owner.getSkills().drainSummoning(1);
			trackDrain = !trackDrain;
			if (ticks == 2)
				owner.getPackets().sendGameMessage("You have 1 minute before your familiar vanishes.");
			else if (ticks == 1)
				owner.getPackets().sendGameMessage("You have 30 seconds before your familiar vanishes.");
			else if (ticks == 0) {
				removeFamiliar();
				dissmissFamiliar(false);
				return;
			}
			sendTimeRemaining();
		}
		int originalId = getOriginalId() + 1;
		if (owner.isCanPvp() && getId() == getOriginalId()) {
			setNextNPCTransformation(originalId);
			call(false);
			return;
		} else if (!owner.isCanPvp() && getId() == originalId && pouch != Pouch.MAGPIE && pouch != Pouch.IBIS && pouch != Pouch.BEAVER && pouch != Pouch.MACAW && pouch != Pouch.FRUIT_BAT) {
			setNextNPCTransformation(originalId - 1);
			call(false);
			return;
		} else if (!withinDistance(owner, 12)) {
			call(false);
			return;
		}
		if (!getCombat().process()) {
			if (isAgressive() && owner.getTickManager().isActive(TickManager.TickKeys.LAST_ATTACK_TICK) && canAttack(owner.getTemporaryTarget()))
				getCombat().setTarget(owner.getTemporaryTarget());
			else
				sendFollow();
		}
	}

	public boolean canAttack(Entity target) {
		if (target instanceof Player) {
			if (target == owner)
				return false;
			Player player = (Player) target;
			if (!owner.isCanPvp() || !player.isCanPvp())
				return false;
			if (player == owner)
				return false;
		} else if (target instanceof NPC) {
			if (target instanceof Familiar) {
				return false;
			}
			NPC n = (NPC) target;
			if (n.getId() == 8133) {
				sendDeath(this);
				return false;
			}
			if (n.getId() == 14301 || n.getId() == 14302 || n.getId() == 14303 || n.getId() == 14304) {
				Glacyte glacyte = (Glacyte) n;
				if (glacyte.getGlacor().getTargetIndex() != -1
						&& getOwner().getIndex() != glacyte.getGlacor().getTargetIndex()) {
					getOwner().getPackets().sendGameMessage("This isn't your target.");
					return false;
				}
			}
		}
		return !target.isDead() && ((target.isAtMultiArea()) || (target.isForceMultiArea()))
				&& owner.getControlerManager().canAttack(target);
	}

	public boolean renewFamiliar() {
		if (!owner.getInventory().getItems().contains(new Item(pouch.getRealPouchId(), 1))) {
			owner.getPackets()
					.sendGameMessage("You need a "
							+ ItemDefinitions.getItemDefinitions(pouch.getRealPouchId()).getName().toLowerCase()
							+ " to renew your familiar's timer.");
			return false;
		}
		resetTickets();
		owner.getInventory().deleteItem(pouch.getRealPouchId(), 1);
		call(true);
		owner.getPackets().sendGameMessage("You use your remaining pouch to renew your familiar.");
		return true;
	}

	public void takeBob() {
		if (bob == null)
			return;
		bob.takeBob();
	}

	public void sendTimeRemaining() {
		owner.getVarsManager().sendVar(1176, ticks * 65);
	}

	/**
	 * public void sendMainConfigs() {
	 *     // 1. Set familiar type
	 *     owner.getVarsManager().sendVar(448, pouch.getRealPouchId());
	 *
	 *     // 2. Refresh orb (value should be based on if familiar is active)
	 *     // -1 = visible/lit, 0 = hidden
	 *     owner.getVarsManager().sendVar(1174, -1);
	 *
	 *     // 3. Set familiar head animation/emote
	 *     // You need to implement getHeadAnimIndex() or use a default
	 *     int headAnimIndex = getHeadAnimIndex(); // Method to get animation index
	 *     owner.getVarsManager().sendVarBit(4282, headAnimIndex);
	 *
	 *     // 4. Update special energy and timer
	 *     refreshSpecialEnergy();
	 *     sendTimeRemaining();
	 *
	 *     // 5. Set special attack cost (bit 4288)
	 *     owner.getVarsManager().sendVarBit(4288, getSpecialAmount() >> 23);

	 *     // 6. Set special attack name/description
	 *     owner.getPackets().sendGlobalString(204, getSpecialName());
	 *     owner.getPackets().sendGlobalString(205, getSpecialDescription());
	 *
	 *     // 7. Set special attack type
	 *     owner.getPackets().sendGlobalVar(1436, getSpecialAttack() == SpecialAttack.CLICK ? 1 : 0);
	 *
	 *     // 8. Initialize interface with script 751
	 *     owner.getPackets().sendRunScript(751);
	 *
	 *     // 9. Send left click option
	 *     sendLeftClickOption(owner);
	 *
	 *     // 10. Send orb target parameters (need to implement)
	 *     sendOrbTargetParams();
	 *
	 *     // 11. Unlock orb controls
	 *     unlockOrb();
	 * }
	 */

	public void sendMainConfigs() {
		switchOrb(true);
		owner.getVarsManager().sendVar(448, pouch.getRealPouchId());
		owner.getVarsManager().setVarBit(4282, pouch.getPouchSetting());
		refreshSpecialEnergy();
		sendTimeRemaining();
		owner.getVarsManager().setVarBit(4288, pouch.getSummoningCost());
		owner.getPackets().sendGlobalString(204, getSpecialName());
		owner.getPackets().sendGlobalString(205, getSpecialDescription());
		owner.getPackets().sendGlobalVar(1436, getSpecialAttack() == SpecialAttack.CLICK ? 1 : 0);
		owner.getPackets().sendRunScript(751);
		sendLeftClickOption(owner);
		sendOrbParams();
	}


	public void sendFollowerDetails() {
		boolean res = owner.getInterfaceManager().hasRezizableScreen();
		owner.getInterfaceManager().sendTab(res ? "tab.summoning_resizeable" : "tab.summoning", "interface.summoning_tab");
		owner.getPackets().sendGlobalVar(168, 98);
	}

	public void switchOrb(boolean on) {
		owner.getVarsManager().sendVar(1174, on ? -1 : 0);
		owner.getPackets().sendHideIComponent(747, 9, !on);
	}

	public static void selectLeftOption(Player player) {
		boolean res = player.getInterfaceManager().hasRezizableScreen();
		player.getInterfaceManager().sendTab(res ? "tab.summoning_resizeable" : "tab.summoning", "interface.summoning_options");//send interface on tab
		player.getPackets().sendGlobalVar(168, 98);//navigate to hidden tab
		sendLeftClickOption(player);
	}

	public static void confirmLeftOption(Player player) {
		player.getPackets().sendGlobalVar(168, 4);// inv tab id
		sendLeftClickOption(player);
	}

	public static void setLeftclickOption(Player player, int summoningLeftClickOption) {
		if (summoningLeftClickOption == player.getSummoningLeftClickOption())
			return;
		player.setSummoningLeftClickOption(summoningLeftClickOption);
		sendLeftClickOption(player);
	}

	public static void sendLeftClickOption(Player player) {
		player.getVarsManager().sendVar(1493, player.getSummoningLeftClickOption());
		player.getVarsManager().sendVar(1494, player.getSummoningLeftClickOption());
	}

	public boolean specialActivated() {
		return specialActivated;
	}

	public void setSpecialActivated(boolean activated) {
		specialActivated = activated;
	}

	public void sendOrbParams() {
		switch (getSpecialAttack()) {
		case CLICK:
			owner.getPackets().sendComponentSettings(747, 18, 0, 0, 2);
			owner.getPackets().sendComponentSettings(662, 74, 0, 0, 2);
			break;
		case ENTITY:
			owner.getPackets().sendComponentSettings(747, 18, 0, 0, 20480);
			owner.getPackets().sendComponentSettings(662, 74, 0, 0, 20480);
			break;
		case OBJECT:
			owner.getPackets().sendComponentSettings(747, 18, 0, 0, 78321);
			owner.getPackets().sendComponentSettings(662, 74, 0, 0, 78321);
			break;
		case ITEM:
			owner.getPackets().sendComponentSettings(747, 18, 0, 0, 65536);
			owner.getPackets().sendComponentSettings(662, 74, 0, 0, 65536);
			break;
		}
	}

	private transient int[][] checkNearDirs;
	private transient boolean sentRequestMoveMessage;

	public void call() {
		if (isDead())
			return;
		if (getAttackedBy() != null && getAttackedByDelay() > Utils.currentTimeMillis()) {
			owner.getPackets().sendGameMessage("You cannot call your familiar while it is under attack.");
			return;
		}
		call(false);
	}

	public void call(boolean login) {
		int size = getSize();
		switchOrb(true);
		if (login) {
			if (bob != null)
				bob.setEntitys(owner, this);
			checkNearDirs = Utils.getCoordOffsetsNear(size);
			sendMainConfigs();
		} else
			removeTarget();
		WorldTile teleTile = null;
		for (int dir = 0; dir < checkNearDirs[0].length; dir++) {
			final WorldTile tile = new WorldTile(new WorldTile(owner.getX() + checkNearDirs[0][dir],
					owner.getY() + checkNearDirs[1][dir], owner.getPlane()));
			if (World.isTileFree(tile.getPlane(), tile.getX(), tile.getY(), size)) {
				teleTile = tile;
				break;
			}
		}
		if (login || teleTile != null)
			WorldTasksManager.schedule(
					new WorldTask() {
				@Override
				public void run() {
					gfx(new Graphics(getDefinitions().size > 1 ? 1315 : 1314));
				}
			});
		if (teleTile == null) {
			if (!sentRequestMoveMessage) {
				owner.getPackets().sendGameMessage("Theres not enough space for your familiar appear.");
				sentRequestMoveMessage = true;
			}
			return;
		}
		sentRequestMoveMessage = false;
		setNextWorldTile(teleTile);
	}

	public void removeFamiliar() {
		owner.setFamiliar(null);
		owner.familiarPouch = null;
	}

	public void dissmissFamiliar(boolean logged) {
		finish();
		if (!logged && !isFinished()) {
			setFinished(true);
			switchOrb(false);
			owner.getPackets().sendRunScript(2471);
			if (owner.storedScrolls >= 1) {
				if (owner.getInventory().hasFreeSlots())
					owner.getInventory().addItem(Summoning.getScrollId(pouch.getRealPouchId()), owner.storedScrolls);
				else
					World.updateGroundItem(new Item(Summoning.getScrollId(pouch.getRealPouchId()), owner.storedScrolls),
							new WorldTile(owner), owner, 60, 1);
				owner.storedScrolls = 0;
			}
			if (bob != null)
				bob.dropBob();
			removeFamiliar();
		}
	}

	private transient boolean dead;

	@Override
	public boolean isDead() {
		return dead || super.isDead();
	}

	@Override
	public void sendDeath(Entity source) {
		if (dead)
			return;
		dead = true;
		final NpcCombatDefinition defs = getCombatDefinitions();
		resetWalkSteps();
		setCantInteract(true);
		getCombat().removeTarget();
		animate(defs.getDeathAnim());
		owner.message("Your familiar slowly begins to fade away..");
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop >= defs.getDeathDelay() - 4) {
					dissmissFamiliar(false);
					removeFamiliar();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public void respawnFamiliar(Player owner) {
		this.owner = owner;
		initEntity();
		deserialize();
		call(true);
	}

	public abstract String getSpecialName();

	public abstract String getSpecialDescription();

	public abstract int getBOBSize();

	public abstract int getSpecialAmount();

	public abstract SpecialAttack getSpecialAttack();

	public abstract boolean submitSpecial(Object object);

	public boolean isAgressive() {
		return true;
	}

	public boolean isInstantSpecial() {
		int id = owner.getFamiliar().getId();
		switch (id) {
		case 6822:
		case 6823:
			return true;
		}
		return false;
	}

	public boolean isOneclickAttack() {
		int id = owner.getFamiliar().getId();
		switch (id) {
		case 7343://titans
		case 7344:
		case 7375:
		case 7376:
			return true;
		}
		return false;
	}

	public static enum SpecialAttack {
		ITEM, ENTITY, CLICK, OBJECT
	}

	public BeastOfBurden getBob() {
		return bob;
	}

	public void refreshSpecialEnergy() {
		owner.getVarsManager().sendVar(1177, specialEnergy);
	}

	public void restoreSpecialAttack(int energy) {
		if (specialEnergy >= 60)
			return;
		specialEnergy = energy + specialEnergy >= 60 ? 60 : specialEnergy + energy;
		refreshSpecialEnergy();
	}

	public void setSpecial(boolean on) {
		double specialAmount = (owner.getEquipment().getCapeId() == 20769 || owner.getEquipment().getCapeId() == 20771
				|| owner.getEquipment().getCapeId() == 19893) ? getSpecialAmount() * 0.80 : getSpecialAmount();
		if (!on)
			owner.temporaryAttribute().remove("FamiliarSpec");
		else {
			if (specialEnergy < specialAmount) {
				owner.getPackets().sendGameMessage("Your special move bar is too low to use this scroll.");
				return;
			} else {
				if (!withinDistance(owner, 9)) {
					owner.getPackets().sendGameMessage(
							"Your follower is too far away or can't see you to perform its special move.");
					return;
				}
			}
			owner.temporaryAttribute().put("FamiliarSpec", Boolean.TRUE);
		}
	}

	public void storeScrolls(int itemId) {
		int maxAmount = Integer.MAX_VALUE;
		int getScrolls = owner.getInventory().getAmountOf(Summoning.getScrollId(pouch.getRealPouchId()));
		int scrollId = Summoning.getScrollId(pouch.getRealPouchId());
		if (owner.storedScrolls >= maxAmount) {
			owner.getDialogueManager().startDialogue("SimpleMessage",
					"You cannot store more than " + Utils.getFormattedNumber(maxAmount) + " scrolls.");
			return;
		}
		if (owner.getFamiliar().getBOBSize() >= 1) {
			owner.getDialogueManager().startDialogue("SimpleMessage",
					"Beast Of Burden Familiars cannot store scrolls.");
			return;
		}
		if (owner.getFamiliar().getId() == 6822 || owner.getFamiliar().getId() == 6823
				|| owner.getFamiliar().getDefinitions().getName().contains("unicorn")) {
			owner.getDialogueManager().startDialogue("SimpleMessage",
					"You cannot store scrolls inside of a healing familiar.");
			return;
		}
		if (itemId == Summoning.getScrollId(pouch.getRealPouchId())) {
			owner.getInventory().deleteItem(scrollId, getScrolls);
			owner.storedScrolls += getScrolls;
			owner.animate(new Animation(1649));
			owner.getDialogueManager().startDialogue("SimpleMessage",
					"You store " + Utils.getFormattedNumber(getScrolls, ',') + " "
							+ owner.getFamiliar().getSpecialName() + " in to your " + getName() + "<br>Total stored: "
							+ owner.storedScrolls);
		} else {
			owner.getDialogueManager().startDialogue("SimpleMessage",
					"You cannot store these kind of scrolls inside of this familiar.");
		}
	}

	public void takeStoredScrolls() {
		int scrollId = Summoning.getScrollId(pouch.getRealPouchId());
		if (!owner.getInventory().hasFreeSlots()) {
			owner.getDialogueManager().startDialogue("SimpleMessage", "You do not have enough inventory space.");
		} else {
			owner.message("You take out the stored scrolls inside your: " + getName());
			owner.getInventory().addItem(scrollId, owner.storedScrolls);
			owner.animate(new Animation(1649));
			owner.storedScrolls = 0;
		}
	}

	public void drainSpecial(int specialReduction) {
		specialEnergy -= specialReduction;
		if (specialEnergy < 0) {
			specialEnergy = 0;
		}
		refreshSpecialEnergy();
	}

	public void drainSpecial() {
		double specialAmount = (owner.getEquipment().getCapeId() == 20769 || owner.getEquipment().getCapeId() == 20771
				|| owner.getEquipment().getCapeId() == 19893) ? getSpecialAmount() * 0.80 : getSpecialAmount();
		specialEnergy -= specialAmount;
		refreshSpecialEnergy();
	}

	public boolean hasSpecialActivated() {
		if (owner.temporaryAttribute().get("FamiliarSpec") != null)
			return true;
		return false;
	}

	public boolean hasSpecialOn() {
		if (hasSpecialActivated()) {
			int scrollId = owner.getFamiliarScroll();
			if (!owner.getInventory().containsItem(scrollId, 1)) {
				owner.getPackets().sendGameMessage("You don't have the scrolls to use this move.");
				owner.temporaryAttribute().remove("FamiliarSpec");
				return false;
			}
			if (owner.getFamiliarDelay() > Utils.currentTimeMillis() && !isInstantSpecial()) {
				owner.getPackets().sendGameMessage("You need to wait a bit before using this move again.");
				return false;
			}
			if (owner.getHitpoints() >= owner.getMaxHitpoints()
					&& (owner.getFamiliar().getId() == 6822 || owner.getFamiliar().getId() == 6823)) {
				owner.getPackets()
						.sendGameMessage("You need to have at least some damage before being able to heal yourself.");
				return false;
			}
			owner.temporaryAttribute().remove("FamiliarSpec");
			drainSpecial();
			return true;
		}
		return false;
	}

	public Player getOwner() {
		return owner;
	}

	public boolean isFinished() {
		return finished;
	}

	public Pouch getPouch() {
		return pouch;
	}

}
