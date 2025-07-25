package com.rs.java.game.player.controlers;

import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.WorldObject;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.FloorItem;
import com.rs.java.game.item.Item;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.Foods.Food;
import com.rs.java.game.player.content.Pots.Pot;

public abstract class Controler {

	// private static final long serialVersionUID = 8384350746724116339L;

	protected transient Player player;

	public final void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public final Object[] getArguments() {
		return player.getControlerManager().getLastControlerArguments();
	}

	public final void setArguments(Object[] objects) {
		player.getControlerManager().setLastControlerArguments(objects);
	}

	public final void removeControler() {
		player.getControlerManager().removeControlerWithoutCheck();
	}

	public abstract void start();

	public boolean canEat(Food food) {
		return true;
	}

	public boolean canComboEat(Food food) {
		return true;
	}

	public boolean canPot(Pot pot) {
		return true;
	}

	/**
	 * after the normal checks, extra checks, only called when you attacking
	 */
	public boolean keepCombating(Entity target) {
		return true;
	}

	public boolean canEquip(int slotId, int itemId) {
		return true;
	}

	/**
	 * after the normal checks, extra checks, only called when you start trying
	 * to attack
	 */
	public boolean canAttack(Entity target) {
		return true;
	}

	public void trackXP(int skillId, int addedXp) {

	}

	public boolean canDeleteInventoryItem(int itemId, int amount) {
		return true;
	}

	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		return true;
	}
	
	public boolean canUseItemOnNpc(Item itemUsed, NPC npc) {
		return true;
	}

	public boolean canAddInventoryItem(int itemId, int amount) {
		return true;
	}

	public boolean canPlayerOption1(Player target) {
		return true;
	}

	/**
	 * hits as ice barrage and that on multi areas
	 */
	public boolean canHit(Entity entity) {
		return true;
	}

	/**
	 * processes every game ticket, usualy not used
	 */
	public void process() {

	}

	public void moved() {

	}

	/**
	 * called once teleport is performed
	 */
	public void magicTeleported(int type) {

	}

	public void sendInterfaces() {

	}

	public void processNPCDeath(NPC npc) {

	}

	/**
	 * return can use script
	 */
	public boolean useDialogueScript(Object key) {
		return true;
	}

	/**
	 * return can teleport
	 */

	public boolean processObjectActionTeleport(WorldTile toTile) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processMagicTeleport(WorldTile toTile) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processItemTeleport(WorldTile toTile) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processJewerlyTeleport(WorldTile toTile) {
		return true;
	}

	/**
	 * return can teleport
	 */
	public boolean processObjectTeleport(WorldTile toTile) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processObjectClick1(WorldObject object) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int packetId) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick1(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick2(NPC npc) {
		return true;
	}

	public boolean processItemClick(int slotId, Item item, Player player) {
		return true;
	}

	public boolean processItemClick2(Item item) {
		return true;
	}

	public boolean processItemClick3(Item item) {
		return true;
	}

	public boolean processItemClick4(Item item) {
		return true;
	}

	public boolean processItemClick5(Item item) {
		return true;
	}

	public boolean processItemClick6(Item item) {
		return true;
	}

	public boolean processItemClick7(Item item) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processNPCClick3(NPC npc) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processObjectClick2(WorldObject object) {
		return true;
	}

	/**
	 * return process normaly
	 */
	public boolean processObjectClick3(WorldObject object) {
		return true;
	}

	public boolean processObjectClick5(WorldObject object) {
		return true;
	}

	/**
	 * return let default death
	 */
	public boolean sendDeath() {
		return true;
	}

	/**
	 * return can move that step
	 */
	public boolean canMove(int dir) {
		return true;
	}

	/**
	 * return can set that step
	 */
	public boolean checkWalkStep(int lastX, int lastY, int nextX, int nextY) {
		return true;
	}

	/**
	 * return remove controler
	 */
	public boolean login() {
		return true;
	}

	/**
	 * return remove controler
	 */
	public boolean logout() {
		return true;
	}

	public void forceClose() {
	}

	public boolean processItemOnNPC(NPC npc, Item item) {
		return true;
	}
	
	public boolean processItemOnObject(WorldObject object, Item item) {
		return true;
	}

	public boolean handleItemOnObject(Player player, WorldObject object, int interfaceId, Item item) {
		return true;
	}
	
	public boolean handleItemOnObject(WorldObject object, Item item) {
		return true;
	}

	public boolean canDropItem(Item item) {
		return true;
	}

	public boolean canSummonFamiliar() {
		return true;
	}

	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, int packetId) {
		return true;
	}

	public boolean processObjectClick4(WorldObject object) {
		return true;
	}
	
	/**
	 * return process normaly
	 */
	public boolean canTakeItem(FloorItem item) {
		return true;
	}

	
	public void processIncommingHit(Hit hit, Entity target) {
		if (this == null || !player.hasStarted())
			return;
		this.processIncommingHit(hit, target);
	}

	public void processIngoingHit(Hit hit) {
		if (this == null || !player.hasStarted())
			return;
		this.processIngoingHit(hit);
	}

	public boolean processItemOnPlayer(Player p2, Item item) {
		if (this == null || !player.hasStarted())
			return true;
		return this.processItemOnPlayer(p2, item);
	}

}
