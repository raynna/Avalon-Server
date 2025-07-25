package com.rs.java.game.player.content.dungeoneering;

import java.util.ArrayList;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.item.Item;
import com.rs.java.game.player.content.dungeoneering.DungeonConstants.SkillDoors;
import com.rs.java.game.player.content.dungeoneering.rooms.BossRoom;
import com.rs.java.game.player.content.dungeoneering.rooms.HandledRoom;
import com.rs.java.utils.Utils;

public final class DungeonUtils {

	public static int getMaxFloor(int level) {
		return (level + 1) / 2;
		//return (level / 2) + 1;
	}

	public static int getLevel(int floor) {
		return (floor * 2) - 1;
		//return (floor - 1) * 2;
	}

	public static boolean isBindAmmo(Item item) {
		if (item.getId() == 19867 || item.getId() == 19868) //runes box
			return true;
		for (int id : DungeonConstants.ARROWS)
			if (item.getId() == id)
				return true;
		for (int id : DungeonConstants.RUNES)
			if (item.getId() == id)
				return true;
		return false;
	}

	public static int getMaxBindItems(int level) {
		if (level >= 120)
			return 5;
		if (level >= 90)
			return 4;
		if (level >= 50)
			return 3;
		if (level >= 20)
			return 2;
		return 1;
	}

	public static int getBindedId(Item item) {
		String name = item.getName();
		for (int i = 15775; i <= 16272; i++) {
			if (ItemDefinitions.getItemDefinitions(i).name.replace(" (b)", "").equals(name))
				return i;
		}
		for (int i = 19865; i <= 19866; i++) {
			if (ItemDefinitions.getItemDefinitions(i).name.replace(" (b)", "").equals(name))
				return i;
		}
		return -1;
	}

	public static int[] getFloorThemeRange(int floorId) {
		if (floorId <= 11)
			return new int[] { 1, 11 };
		if (floorId <= 17)
			return new int[] { 12, 17 };
		if (floorId <= 29)
			return new int[] { 18, 29 };
		if (floorId <= 35)
			return new int[] { 30, 35 };
		if (floorId <= 47)
			return new int[] { 36, 47 };
		return new int[] { 48, 60 };
	}

	public static int getFloorType(int floorId) {
		if (floorId <= 11)
			return DungeonConstants.FROZEN_FLOORS;
		if (floorId <= 17)
			return DungeonConstants.ABANDONED_FLOORS;
		if (floorId <= 29)
			return DungeonConstants.FURNISHED_FLOORS;
		if (floorId <= 35)
			return DungeonConstants.ABANDONED_FLOORS;
		if (floorId <= 47)
			return DungeonConstants.OCCULT_FLOORS;
		return DungeonConstants.WARPED_FLOORS;
	}

	public static int getSafeMusic(int type) {
		return DungeonConstants.SAFE_MUSICS[type][Utils.random(DungeonConstants.DANGEROUS_MUSICS[type].length)];
	}

	public static int getDangerousMusic(int type) {
		return DungeonConstants.DANGEROUS_MUSICS[type][Utils.random(DungeonConstants.DANGEROUS_MUSICS[type].length)];
	}

	public static RoomReference getRandomRoom(Room[][] map) {
		return new RoomReference(Utils.random(map.length), Utils.random(map[0].length));
	}

	public static int getGuardianCreature(int type) {
		return DungeonConstants.GUARDIAN_CREATURES[type + 1][Utils.random(DungeonConstants.GUARDIAN_CREATURES[type + 1].length)];
	}

	public static int getGuardianCreature() {
		return DungeonConstants.GUARDIAN_CREATURES[0][Utils.random(DungeonConstants.GUARDIAN_CREATURES[0].length)];
	}

	public static int getForgottenWarrior() {
		int type = Utils.random(DungeonConstants.FORGOTTEN_WARRIORS.length);
		return DungeonConstants.FORGOTTEN_WARRIORS[type][Utils.random(type)];
	}

	public static int getHunterCreature() {
		return DungeonConstants.HUNTER_CREATURES[Utils.random(DungeonConstants.HUNTER_CREATURES.length)];
	}

	public static int getSlayerCreature() {
		return DungeonConstants.SLAYER_CREATURES[Utils.random(DungeonConstants.SLAYER_CREATURES.length)];
	}

	public static BossRoom getBossRoomWithChunk(int type, int x, int y) {
		for (BossRoom room : DungeonConstants.BOSS_ROOMS[type])
			if (room.getChunkX() == x && room.getChunkY() == y)
				return room;
		return null;
	}

	public static Room[] selectPossibleBossRooms(int type, int complexity, int floorId, boolean n, boolean e, boolean s, boolean w, int rotation) {
		ArrayList<Room> possiblities = new ArrayList<Room>();
		for (BossRoom handledRoom : DungeonConstants.BOSS_ROOMS[type]) {
			if (!handledRoom.isComplexity(complexity))
				continue;
			if (handledRoom.getMinFloor() > floorId)
				continue;
			Room room = new Room(handledRoom, rotation);
			if (room.hasNorthDoor() == n && room.hasEastDoor() == e && room.hasSouthDoor() == s && room.hasWestDoor() == w) {
				possiblities.add(room);
			}
		}
		return possiblities.toArray(new Room[possiblities.size()]);
	}

	public static boolean theresDoorTo(RoomReference checkRoom, Room[][] map) {
		if (checkRoom.getX() < 0 || checkRoom.getX() >= map.length || checkRoom.getY() < 0 || checkRoom.getY() >= map[checkRoom.getX()].length)
			return false;
		if (checkRoom.getX() != 0 && map[checkRoom.getX() - 1][checkRoom.getY()] != null && map[checkRoom.getX() - 1][checkRoom.getY()].hasEastDoor()) {
			return true;
		}
		if (checkRoom.getX() != map.length - 1 && map[checkRoom.getX() + 1][checkRoom.getY()] != null && map[checkRoom.getX() + 1][checkRoom.getY()].hasWestDoor()) {
			return true;
		}
		if (checkRoom.getY() != 0 && map[checkRoom.getX()][checkRoom.getY() - 1] != null && map[checkRoom.getX()][checkRoom.getY() - 1].hasNorthDoor()) {
			return true;
		}
		if (checkRoom.getY() != map[checkRoom.getX()].length - 1 && map[checkRoom.getX()][checkRoom.getY() + 1] != null && map[checkRoom.getX()][checkRoom.getY() + 1].hasSouthDoor()) {
			return true;
		}
		return false;

	}

	public static Room[] selectPossibleRooms(HandledRoom[] handledRooms, int complexity, int floorType, boolean n, boolean e, boolean s, boolean w) {
		ArrayList<Room> possiblities = new ArrayList<Room>();
		for (HandledRoom handledRoom : handledRooms) {
			if (!handledRoom.isAvailableOnFloorType(floorType))
				continue;
			if (!handledRoom.isComplexity(complexity))
				continue;
			for (int rotation = 0; rotation < DungeonConstants.ROTATIONS_COUNT; rotation++) {
				Room room = new Room(handledRoom, rotation);
				if (room.hasNorthDoor() == n && room.hasEastDoor() == e && room.hasSouthDoor() == s && room.hasWestDoor() == w) {
					possiblities.add(room);
				}
			}
		}
		return possiblities.toArray(new Room[possiblities.size()]);
	}

	public static Room[] selectPossibleRooms(HandledRoom[] handledRooms, int complexity, int floorType, boolean n, boolean e, boolean s, boolean w, int rotation) {
		ArrayList<Room> possiblities = new ArrayList<Room>();
		for (HandledRoom handledRoom : handledRooms) {
			if (!handledRoom.isAvailableOnFloorType(floorType))
				continue;
			if (!handledRoom.isComplexity(complexity))
				continue;
			Room room = new Room(handledRoom, rotation);
			if (room.hasNorthDoor() == n && room.hasEastDoor() == e && room.hasSouthDoor() == s && room.hasWestDoor() == w) {
				possiblities.add(room);
			}
		}
		return possiblities.toArray(new Room[possiblities.size()]);
	}

	public static boolean checkDungeonBounds(RoomReference reference, Room[][] map, Room room) {
		if (reference.getX() == 0 && room.hasWestDoor())
			return false;
		if (reference.getX() == map.length - 1 && room.hasEastDoor())
			return false;
		if (reference.getY() == 0 && room.hasSouthDoor())
			return false;
		if (reference.getY() == map[0].length - 1 && room.hasNorthDoor())
			return false;
		return true;
	}

	public static boolean isClipped(int objectId) {
		for (int id : DungeonConstants.DUNGEON_BOSS_DOORS)
			if (id == objectId)
				return true;
		for (int id : DungeonConstants.DUNGEON_DOORS)
			if (id == objectId)
				return true;
		/*	for (int id : DungeonConstants.DUNGEON_SKILL_DOORS)
			    if (id == objectId)
				return true;
			for (int id : DungeonConstants.THIEF_CHEST_OPEN)
			    if (id == objectId)
				return true;
			for (int id : DungeonConstants.THIEF_CHEST_LOCKED)
			    if (id == objectId)
				return true;
			if (objectId >= KeyDoors.ORANGE_TRIANGLE.getObjectId() && objectId <= KeyDoors.GOLD_SHIELD.getObjectId())
				return true;*/
		return false;
	}

	public static int getMiningResource(int rockType, int floorType) {
		return DungeonConstants.MINING_RESOURCES[floorType] + rockType * 2;
	}

	public static int getWoodcuttingResource(int treeType, int floorType) {
		return DungeonConstants.WOODCUTTING_RESOURCES[floorType] + treeType * 2;
	}

	public static int getFarmingResource(int flowerType, int floorType) {
		return DungeonConstants.FARMING_RESOURCES[floorType] + flowerType * 2;
	}

	public static int getBattleaxe(int tier) {
		return 15753 + (tier - 1) * 2;
	}

	public static int getGauntlets(int tier) {
		return 16273 + (tier - 1) * 2;
	}

	public static int getPickaxe(int tier) {
		return 16295 + (tier - 1) * 2;
	}

	public static int getBoots(int tier) {
		return 16339 + (tier - 1) * 2;
	}

	public static int getHatchet(int tier) {
		return 16361 + (tier - 1) * 2;
	}

	public static int getLongsword(int tier) {
		return 16383 + (tier - 1) * 2;
	}

	public static int getMaul(int tier) {
		return 16405 + (tier - 1) * 2;
	}

	public static int getArrows(int tier) {
		return 16427 + (tier - 1) * 5;
	}

	public static int getPlatelegs(int tier, boolean male) {
		return (male ? 16669 : 16647) + (tier - 1) * 2;
	}

	public static int getFullHelm(int tier) {
		return 16691 + (tier - 1) * 2;
	}

	public static int getChainbody(int tier) {
		return 16713 + (tier - 1) * 2;
	}

	public static int getDagger(int tier) {
		return 16757 + (tier - 1) * 2;
	}

	public static int getTornBag(int tier) {
		return 17995 + (tier - 1) * 2;
	}

	public static int getOre(int tier) {
		return 17630 + (tier - 1) * 2;
	}

	public static int getBranche(int tier) {
		return 17682 + (tier - 1) * 2;
	}

	public static int getTextile(int tier) {
		return 17448 + (tier - 1) * 2;
	}

	public static int getHerb(int tier) {
		return 17494 + (tier - 1) * 2;
	}

	public static int getSeed(int tier) {
		return 17823 + (tier - 1);
	}

	public static int getRandomGear(int tier) {
		switch (Utils.random(29)) {
			case 0:
				return getLongbow(tier);
			case 1:
				return getShortbow(tier);
			case 2:
				return getChaps(tier);
			case 3:
				return getLeatherBoots(tier);
			case 4:
				return getVambrances(tier);
			case 5:
				return getLeatherBody(tier);
			case 6:
				return getCoif(tier);
			case 7:
				return getRobeTop(tier);
			case 8:
				return getElementalStaff(tier);
			case 9:
				return getBasicStaff(tier);
			case 10:
				return getGloves(tier);
			case 11:
				return getShoes(tier);
			case 12:
				return getRobeBottom(tier);
			case 13:
				return getHood(tier);
			case 14:
				return getKiteshield(tier);
			case 15:
				return getPlatebody(tier);
			case 16:
				return getSpear(tier);
			case 17:
				return getWarhammer(tier);
			case 18:
				return getRapier(tier);
			case 19:
				return get2HSword(tier);
			case 20:
				return getDagger(tier);
			case 21:
				return getChainbody(tier);
			case 22:
				return getFullHelm(tier);
			case 23:
				return getPlatelegs(tier, true);
			case 24:
				return getMaul(tier);
			case 25:
				return getLongsword(tier);
			case 26:
				return getBoots(tier);
			case 27:
				return getGauntlets(tier);
			case 28:
				return getBattleaxe(tier);
		}
		return getHatchet(tier); //shouldnt
	}

	public static int getRandomRangeGear(int tier) {
		switch (Utils.random(7)) {
			case 0:
				return getLongbow(tier);
			case 1:
				return getShortbow(tier);
			case 2:
				return getChaps(tier);
			case 3:
				return getLeatherBoots(tier);
			case 4:
				return getVambrances(tier);
			case 5:
				return getLeatherBody(tier);
			case 6:
				return getCoif(tier);
		}
		return getHatchet(tier); //shouldnt
	}

	public static int getRandomMagicGear(int tier) {
		switch (Utils.random(7)) {
			case 0:
				return getRobeTop(tier);
			case 1:
				return getElementalStaff(tier);
			case 2:
				return getBasicStaff(tier);
			case 3:
				return getGloves(tier);
			case 4:
				return getShoes(tier);
			case 5:
				return getRobeBottom(tier);
			case 6:
				return getHood(tier);
		}
		return getHatchet(tier); //shouldnt
	}

	public static int getRandomMeleeGear(int tier) {
		switch (Utils.random(15)) {
			case 0:
				return getKiteshield(tier);
			case 1:
				return getPlatebody(tier);
			case 2:
				return getSpear(tier);
			case 3:
				return getWarhammer(tier);
			case 4:
				return getRapier(tier);
			case 5:
				return get2HSword(tier);
			case 6:
				return getDagger(tier);
			case 7:
				return getChainbody(tier);
			case 8:
				return getFullHelm(tier);
			case 9:
				return getPlatelegs(tier, true);
			case 10:
				return getMaul(tier);
			case 11:
				return getLongsword(tier);
			case 12:
				return getBoots(tier);
			case 13:
				return getGauntlets(tier);
			case 14:
				return getBattleaxe(tier);
		}
		return getHatchet(tier); //shouldnt
	}

	public static int getRandomWeapon(int tier) {
		switch (Utils.random(12)) {
			case 0:
				return getLongbow(tier);
			case 1:
				return getShortbow(tier);
			case 2:
				return getElementalStaff(tier);
			case 3:
				return getBasicStaff(tier);
			case 4:
				return getSpear(tier);
			case 5:
				return getWarhammer(tier);
			case 6:
				return getRapier(tier);
			case 7:
				return get2HSword(tier);
			case 8:
				return getDagger(tier);
			case 9:
				return getMaul(tier);
			case 10:
				return getLongsword(tier);
			case 11:
				return getBattleaxe(tier);
		}
		return getHatchet(tier); //shouldnt
	}

	public static int get2HSword(int tier) {
		return 16889 + (tier - 1) * 2;
	}

	public static int getFood(int tier) {
		return 18159 + (tier - 1) * 2;
	}

	public static int getRapier(int tier) {
		return 16935 + (tier - 1) * 2;
	}

	public static int getWarhammer(int tier) {
		return 17019 + (tier - 1) * 2;
	}

	public static int getSpear(int tier) {
		return 17063 + (tier - 1) * 8;
	}

	public static int getPlatebody(int tier) {
		return 17239 + (tier - 1) * 2;
	}

	public static int getKiteshield(int tier) {
		return 17341 + (tier - 1) * 2;
	}

	public static int getHood(int tier) {
		return 16735 + (tier - 1) * 2;
	}

	public static int getRobeBottom(int tier) {
		return 16845 + (tier - 1) * 2;
	}

	public static int getShoes(int tier) {
		return 16911 + (tier - 1) * 2;
	}

	public static int getGloves(int tier) {
		return 17151 + (tier - 1) * 2;
	}

	public static int getBasicStaff(int tier) {
		return 16977 + (tier - 1) * 2;
	}

	public static int getElementalStaff(int tier) {
		if (tier == 2)
			return 17001; // earth
		if (tier == 3)
			return 17005; // fire
		if (tier == 4)
			return 17009; // air
		if (tier == 5)
			return 17013; // catalytic
		if (tier == 6)
			return 16999; // empowered water
		if (tier == 7)
			return 17003; // empowered earth
		if (tier == 8)
			return 17007; // empowered fire
		if (tier == 9)
			return 17011; // empowered air
		if (tier == 10)
			return 17015; // empowered catalytic
		if (tier == 11)
			return 17017; // celestial catalytic
		return 16997; //water
	}

	public static int getStartRunes(int level) {
		if (level >= 41)
			return DungeonConstants.RUNES[6]; //death
		if (level >= 17)
			return DungeonConstants.RUNES[5]; //chaos
		return DungeonConstants.RUNES[4]; //mind
	}

	public static int getRobeTop(int tier) {
		return 17217 + (tier - 1) * 2;
	}

	public static int getCoif(int tier) {
		return 17041 + (tier - 1) * 2;
	}

	public static int getLeatherBody(int tier) {
		return 17173 + (tier - 1) * 2;
	}

	public static int getVambrances(int tier) {
		return 17195 + (tier - 1) * 2;
	}

	public static int getLeatherBoots(int tier) {
		return 17297 + (tier - 1) * 2;
	}

	public static int getChaps(int tier) {
		return 17319 + (tier - 1) * 2;
	}

	public static int getShortbow(int tier) {
		return 16867 + (tier - 1) * 2;
	}

	public static int getLongbow(int tier) {
		return 16317 + (tier - 1) * 2;
	}

	public static int getMaxTier(DungeonPartyManager party, int cap, int... skills) {
		int maxTier = cap;
		for (int skill : skills) {
			int level = party.getMaxLevel(skill);
			int tier = getTier(level);
			if (maxTier > tier)
				maxTier = tier;
		}
		return maxTier;
	}

	public static int getTier(int level) {
		return level == 99 ? 11 : level > 10 ? (1 + level / 10) : 1;

	}

	public static boolean isLadder(int id, int type) {
		return DungeonConstants.LADDERS[type] == id;
	}

	public static boolean isOpenSkillDoor(int id, int type) {
		for (SkillDoors s : SkillDoors.values()) {
			int openId = s.getOpenObject(type);
			if (openId != -1 && openId == id)
				return true;
		}
		return false;
	}
}