package com.rs.java.game.player;

import java.io.Serializable;

import com.rs.java.game.Entity;
import com.rs.java.game.Hit;
import com.rs.java.game.WorldObject;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.FloorItem;
import com.rs.java.game.item.Item;
import com.rs.java.game.npc.NPC;
import com.rs.java.game.player.controllers.Controller;
import com.rs.java.game.player.controllers.ControlerHandler;

public final class ControlerManager implements Serializable {

	private static final long serialVersionUID = 2084691334731830796L;

	private transient Player player;
	private transient Controller controller;
	private transient boolean inited;
	private Object[] lastControlerArguments;

	private String lastControler;

	public ControlerManager() {
		//lastControler = Settings.SPAWN_WORLD ? Settings.SPAWN_WORLD_CONTROLLER : Settings.START_CONTROLER;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Controller getControler() {
		return controller;
	}

	public void startControler(Object key, Object... parameters) {
		if (controller != null)
			forceStop();
		controller = (Controller) (key instanceof Controller ? key : ControlerHandler.getControler(key));
		if (controller == null)
			return;
		controller.setPlayer(player);
		lastControlerArguments = parameters;
		lastControler = (String) key;
		controller.start();
		inited = true;
		//Logger.globalLog(player.getUsername(), player.getSession().getIP(), new String(" started controller: " + key.toString() + "."));
	}

	public void login() {
		if (lastControler == null)
			return;
		controller = ControlerHandler.getControler(lastControler);
		if (controller == null) {
			forceStop();
			return;
		}
		controller.setPlayer(player);
		if (controller.login())
			forceStop();
		else
			inited = true;
	}

	public void logout() {
		if (controller == null)
			return;
		if (controller.logout())
			forceStop();
	}

	public boolean canMove(int dir) {
		if (controller == null || !inited)
			return true;
		return controller.canMove(dir);
	}

	public boolean addWalkStep(int lastX, int lastY, int nextX, int nextY) {
		if (controller == null || !inited)
			return true;
		return controller.checkWalkStep(lastX, lastY, nextX, nextY);
	}

	public boolean canTakeItem(FloorItem item) {
		if (controller == null || !inited)
			return true;
		return controller.canTakeItem(item);
	}

	public boolean keepCombating(Entity target) {
		if (controller == null || !inited)
			return true;
		return controller.keepCombating(target);
	}

	public boolean canEquip(int slotId, int itemId) {
		if (controller == null || !inited)
			return true;
		return controller.canEquip(slotId, itemId);
	}

	public boolean canAddInventoryItem(int itemId, int amount) {
		if (controller == null || !inited)
			return true;
		return controller.canAddInventoryItem(itemId, amount);
	}

	public void trackXP(int skillId, int addedXp) {
		if (controller == null || !inited)
			return;
		controller.trackXP(skillId, addedXp);
	}

	public boolean canDeleteInventoryItem(int itemId, int amount) {
		if (controller == null || !inited)
			return true;
		return controller.canDeleteInventoryItem(itemId, amount);
	}

	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		if (controller == null || !inited)
			return true;
		return controller.canUseItemOnItem(itemUsed, usedWith);
	}
	
	public boolean canUseItemOnNpc(Item itemUsed, NPC npc) {
		if (controller == null || !inited)
			return true;
		return controller.canUseItemOnNpc(itemUsed, npc);
	}

	public boolean canAttack(Entity entity) {
		if (controller == null || !inited)
			return true;
		return controller.canAttack(entity);
	}

	public boolean canPlayerOption1(Player target) {
		if (controller == null || !inited)
			return true;
		return controller.canPlayerOption1(target);
	}

	public boolean canHit(Entity entity) {
		if (controller == null || !inited)
			return true;
		return controller.canHit(entity);
	}

	public void moved() {
		if (controller == null || !inited)
			return;
		controller.moved();
	}

	public void magicTeleported(int type) {
		if (controller == null || !inited)
			return;
		player.getAppearance().setRenderEmote(-1);
		controller.magicTeleported(type);
	}

	public void sendInterfaces() {
		if (controller == null || !inited)
			return;
		controller.sendInterfaces();
	}

	public void process() {
		if (controller == null || !inited)
			return;
		controller.process();
	}

	public void processNPCDeath(NPC npc){
		if (controller == null || !inited)
			return;
		controller.processNPCDeath(npc);
	}

	public boolean sendDeath() {
		if (controller == null || !inited)
			return true;
		return controller.sendDeath();
	}

	public boolean useDialogueScript(Object key) {
		if (controller == null || !inited)
			return true;
		return controller.useDialogueScript(key);
	}

	public boolean processMagicTeleport(WorldTile toTile) {
		if (controller == null || !inited)
			return true;
		return controller.processMagicTeleport(toTile);
	}

	public boolean processItemTeleport(WorldTile toTile) {
		if (controller == null || !inited)
			return true;
		return controller.processItemTeleport(toTile);
	}

	public boolean processObjectTeleport(WorldTile toTile) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectTeleport(toTile);
	}
	
	public boolean processJewerlyTeleport(WorldTile toTile) {
		if (controller == null || !inited)
			return true;
		return controller.processJewerlyTeleport(toTile);
	}

	public boolean processObjectClick1(WorldObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick1(object);
	}

	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, int packetId) {
		if (controller == null || !inited)
			return true;
		return controller.processButtonClick(interfaceId, componentId, slotId, slotId2, packetId);
	}

	public boolean processNPCClick1(NPC npc) {
		if (controller == null || !inited)
			return true;
		return controller.processNPCClick1(npc);
	}

	public boolean canSummonFamiliar() {
		if (controller == null || !inited)
			return true;
		return controller.canSummonFamiliar();
	}

	public boolean processItemClick(int slotId, Item item, Player player) {
		if (controller == null || !inited)
			return true;
		return controller.processItemClick(slotId, item, player);
	}


	public boolean processItemClick2(Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processItemClick2(item);
	}

	public boolean processItemClick3(Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processItemClick3(item);
	}

	public boolean processItemClick4(Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processItemClick4(item);
	}

	public boolean processItemClick5(Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processItemClick5(item);
	}

	public boolean processItemClick6(Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processItemClick6(item);
	}

	public boolean processItemClick7(Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processItemClick7(item);
	}

	public boolean processNPCClick2(NPC npc) {
		if (controller == null || !inited)
			return true;
		return controller.processNPCClick2(npc);
	}

	public boolean processNPCClick3(NPC npc) {
		if (controller == null || !inited)
			return true;
		return controller.processNPCClick3(npc);
	}

	public boolean processObjectClick2(WorldObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick2(object);
	}

	public boolean processObjectClick3(WorldObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick3(object);
	}

	public boolean processItemOnNPC(NPC npc, Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processItemOnNPC(npc, item);
	}
	
	public boolean processItemOnObject(WorldObject object, Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processItemOnObject(object, item);
	}

	public boolean canDropItem(Item item) {
		if (controller == null || !inited)
			return true;
		return controller.canDropItem(item);
	}

	public void forceStop() {
		if (controller != null) {
			controller.forceClose();
			controller = null;
		}
		lastControlerArguments = null;
		lastControler = null;
		inited = false;
		//Logger.globalLog(player.getUsername(), player.getSession().getIP(), new String(" current controller has been stopped."));
	}

	public void removeControlerWithoutCheck() {
		controller = null;
		lastControlerArguments = null;
		lastControler = null;
		inited = false;
		//Logger.globalLog(player.getUsername(), player.getSession().getIP(), new String(" current controller has been stopped."));
	}

	public void setLastController(String controller, Object... args) {
		lastControler = controller;
		lastControlerArguments = args;
	}

	public Object[] getLastControlerArguments() {
		return lastControlerArguments;
	}

	public void setLastControlerArguments(Object[] lastControlerArguments) {
		this.lastControlerArguments = lastControlerArguments;
	}

	public boolean processObjectClick4(WorldObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick4(object);
	}

	public boolean processObjectClick5(WorldObject object) {
		if (controller == null || !inited)
			return true;
		return controller.processObjectClick5(object);
	}
	
	public boolean handleItemOnObject(WorldObject object, Item item) {
		if (controller == null || !inited)
			return true;
		return controller.handleItemOnObject(object, item);
	}


	public boolean processItemOnPlayer(Player p2, Item item) {
		if (controller == null || !inited)
			return true;
		return controller.processItemOnPlayer(p2, item);
	}

	public void processIncommingHit(Hit hit, Entity target) {
		if (controller == null || !inited)
			return;
		controller.processIncommingHit(hit, target);
	}

	public void processIngoingHit(Hit hit) {
		if (controller == null || !inited)
			return;
		controller.processIngoingHit(hit);
	}
}
