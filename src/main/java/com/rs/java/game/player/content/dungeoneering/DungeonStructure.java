package com.rs.java.game.player.content.dungeoneering;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.rs.Settings;

public class DungeonStructure {

	private RoomNode base;
	private RoomNode[][] rooms;
	private List<RoomNode> roomList;
	private List<Integer> availableKeys = IntStream.rangeClosed(0, 63).boxed().collect(Collectors.toList());
	private Random random;
	private boolean keyshare;
	private int complexity;
	private int size;

	public DungeonStructure(int size, Random random, int complexity, boolean keyshare) {
		this.complexity = complexity;
		this.size = size;
		this.keyshare = keyshare;
		this.random = random;
		Collections.shuffle(availableKeys, random);

		int attempt = 0;
		boolean success = false;

		while (attempt++ < 10 && !success) {
			rooms = new RoomNode[DungeonConstants.DUNGEON_RATIO[size][0]][DungeonConstants.DUNGEON_RATIO[size][1]];
			success = generate();
			if (!success && Settings.DEBUG) {
				System.out.println("Dungeon generation attempt " + attempt + " failed");
			}
		}

		if (!success) {
			throw new RuntimeException("Failed to generate dungeon after 10 attempts");
		}

		if (Settings.DEBUG) {
			System.out.println("Final roomCount: " + getRoomCount());
		}
	}

	private boolean generate() {
		if (Settings.DEBUG) {
			System.out.println("Starting dungeon generation...");
			System.out.println("Dungeon dimensions: " + rooms.length + "x" + rooms[0].length);
		}

		int x = random.nextInt(rooms.length);
		int y = random.nextInt(rooms[0].length);
		if (Settings.DEBUG) System.out.println("Initial base position: " + x + ", " + y);

		base = new RoomNode(null, x, y);
		roomList = new LinkedList<RoomNode>();
		addRoom(base);

		// Use a Set to avoid duplicate points in the queue
		Set<Point> visitedPoints = new HashSet<>();
		List<Point> queue = new ArrayList<Point>();

		// Add initial neighbors
		addNeighborsToQueue(queue, visitedPoints, x, y);

		if (Settings.DEBUG) System.out.println("Initial queue size: " + queue.size());

		// Generate full dungeon
		if (Settings.DEBUG) System.out.println("Generating full dungeon layout...");
		int iteration = 0;
		int maxIterations = rooms.length * rooms[0].length * 2; // Safety limit

		while (!queue.isEmpty() && iteration++ < maxIterations) {
			if (Settings.DEBUG && iteration % 10 == 0) {
				System.out.println("Iteration " + iteration + ", queue size: " + queue.size() + ", rooms: " + roomList.size());
			}

			Point next = randomFromQueue(queue);
			if (next == null) {
				if (Settings.DEBUG) System.out.println("Queue returned null point");
				break;
			}

			// Remove the point from queue
			queue.remove(next);

			// Check if point is valid and not already occupied
			if (next.x < 0 || next.y < 0 || next.x >= rooms.length || next.y >= rooms[0].length) {
				if (Settings.DEBUG) System.out.println("Skipping out-of-bounds point: " + next.x + ", " + next.y);
				continue;
			}

			if (getRoom(next.x, next.y) != null) {
				if (Settings.DEBUG) System.out.println("Skipping already occupied point: " + next.x + ", " + next.y);
				continue;
			}

			// Connect this edge to a random neighboring room
			RoomNode parent = randomParent(next.x, next.y);
			if (parent == null) {
				if (Settings.DEBUG) System.out.println("No valid parent found for position: " + next.x + ", " + next.y);
				// Add this point back to queue for later consideration
				queue.add(next);
				continue;
			}

			RoomNode room = new RoomNode(parent, next.x, next.y);
			addRoom(room);

			if (Settings.DEBUG) System.out.println("Added room at " + next.x + ", " + next.y + " with parent at " + parent.x + ", " + parent.y);

			// Add neighbors to queue
			addNeighborsToQueue(queue, visitedPoints, next.x, next.y);
		}

		if (iteration >= maxIterations) {
			if (Settings.DEBUG) System.out.println("WARNING: Exceeded maximum iterations in layout generation");
		}

		if (Settings.DEBUG) System.out.println("Initial room count: " + roomList.size());

		// Check if we have enough rooms
		if (roomList.size() < 5) {
			if (Settings.DEBUG) System.out.println("Failed to generate enough rooms: " + roomList.size());
			return false;
		}

		int maxSize = rooms.length * rooms[0].length;
		int minSize = (int) (maxSize * 0.8);
		double multiplier = 1D - ((double) (6D - complexity) * 0.06D);
		maxSize = (int) (maxSize * multiplier);
		minSize = (int) (minSize * multiplier);

		if (Settings.DEBUG) {
			System.out.println("Target sizes - max: " + maxSize + ", min: " + minSize + ", multiplier: " + multiplier);
			System.out.println("Current room count: " + roomList.size());
		}

		// Only remove rooms if we have more than the minimum
		if (roomList.size() > minSize) {
			// Create gaps by removing random DE's with better logic
			int targetSize = minSize + random.nextInt(maxSize - minSize + 1);
			int roomsToRemove = roomList.size() - targetSize;

			if (Settings.DEBUG) System.out.println("Removing " + roomsToRemove + " rooms to reach target size: " + targetSize);

			// Only remove rooms that won't break connectivity
			List<RoomNode> removableRooms = shuffledRooms()
					.filter(r -> r.children.isEmpty() && !r.equals(base))
					.collect(Collectors.toList());

			if (Settings.DEBUG) System.out.println("Found " + removableRooms.size() + " removable rooms");

			roomsToRemove = Math.min(roomsToRemove, removableRooms.size());
			for (int i = 0; i < roomsToRemove; i++) {
				removeRoom(removableRooms.get(i));
			}
		}

		if (Settings.DEBUG) System.out.println("Room count after removal: " + roomList.size());

		if (roomList.size() < minSize) {
			if (Settings.DEBUG) System.out.println("Failed to maintain minimum room count: " + roomList.size() + " < " + minSize);
			return false;
		}

		// DECLARE boss OUTSIDE the loop
		RoomNode boss = null;
		int count = 0;
		boolean critPathValid = false;

		if (Settings.DEBUG) System.out.println("Starting crit path generation...");

		while (!critPathValid && count++ < 50) {
			if (Settings.DEBUG && count > 1) System.out.println("Crit path attempt #" + count);

			// Choose a boss room from leaf nodes
			List<RoomNode> leafNodes = shuffledRooms()
					.filter(r -> r.children.isEmpty())
					.collect(Collectors.toList());

			if (leafNodes.isEmpty()) {
				if (Settings.DEBUG) System.out.println("No leaf nodes for boss");
				return false;
			}

			boss = leafNodes.get(random.nextInt(leafNodes.size()));
			if (Settings.DEBUG) System.out.println("Selected boss room at: " + boss.x + ", " + boss.y);

			// Build crit path from boss to base
			Set<RoomNode> critPath = new HashSet<>(boss.pathToBase());
			if (Settings.DEBUG) System.out.println("Base crit path size: " + critPath.size());

			// Add some random branches to crit path
			int additionalBranches = 2 + random.nextInt(2);
			if (Settings.DEBUG) System.out.println("Adding " + additionalBranches + " additional branches");

			for (int i = 0; i < additionalBranches; i++) {
				RoomNode finalBoss = boss;
				List<RoomNode> nonCritRooms = shuffledRooms()
						.filter(r -> !critPath.contains(r) && !r.equals(finalBoss))
						.collect(Collectors.toList());

				if (!nonCritRooms.isEmpty()) {
					RoomNode branchRoom = nonCritRooms.get(random.nextInt(nonCritRooms.size()));
					critPath.addAll(branchRoom.pathToBase());
					if (Settings.DEBUG) System.out.println("Added branch from room at: " + branchRoom.x + ", " + branchRoom.y);
				}
			}

			if (Settings.DEBUG) System.out.println("Total crit path size: " + critPath.size());
			if (Settings.DEBUG) System.out.println("Required range: " + DungeonConstants.MIN_CRIT_PATH[size] + " - " + DungeonConstants.MAX_CRIT_PATH[size]);

			// Validate crit path size
			if (critPath.size() >= DungeonConstants.MIN_CRIT_PATH[size] &&
					critPath.size() <= DungeonConstants.MAX_CRIT_PATH[size]) {
				critPath.forEach(r -> r.isCritPath = true);
				boss.isBoss = true;
				critPathValid = true;

				if (Settings.DEBUG) System.out.println("Crit path generation successful! Size: " + critPath.size());
			} else {
				if (Settings.DEBUG) System.out.println("Crit path size out of range, retrying...");
			}
		}

		if (!critPathValid) {
			if (Settings.DEBUG) System.out.println("Failed to create valid crit path after 50 attempts");
			return false;
		}

		// Now boss is accessible here since it was declared outside the loop
		if (boss == null) {
			if (Settings.DEBUG) System.out.println("Boss is null - critical error");
			return false;
		}

		// Move the base somewhere randomly on crit, base can't be a straight 2 way though
		List<RoomNode> validBaseRooms = shuffledRooms()
				.filter(r -> !r.isBoss && r.isCritPath &&
						!(r.west() && r.east() && !r.north() && !r.south()) &&
						!(!r.west() && !r.east() && r.north() && r.south()))
				.collect(Collectors.toList());

		if (Settings.DEBUG) System.out.println("Found " + validBaseRooms.size() + " valid base rooms");

		if (!validBaseRooms.isEmpty()) {
			RoomNode newBase = validBaseRooms.get(random.nextInt(validBaseRooms.size()));
			if (Settings.DEBUG) System.out.println("Moving base to: " + newBase.x + ", " + newBase.y);
			setBase(newBase);
		} else {
			if (Settings.DEBUG) System.out.println("No valid base room found");
			return false;
		}

// Crit DE locks
		if (Settings.DEBUG) System.out.println("Assigning crit DE locks...");
		List<RoomNode> critLockRooms = rooms()
				.filter(r -> !r.isBoss && r.children.stream().noneMatch(c -> c.isCritPath) && r.isCritPath)
				.collect(Collectors.toList());

		if (Settings.DEBUG) System.out.println("Found " + critLockRooms.size() + " rooms for crit locks");

// Check if we have available keys before trying to assign them
		if (availableKeys.isEmpty()) {
			if (Settings.DEBUG) System.out.println("No available keys for crit locks!");
		} else {
			for (RoomNode room : critLockRooms) {
				if (availableKeys.isEmpty()) {
					if (Settings.DEBUG) System.out.println("Ran out of keys while assigning crit locks");
					break;
				}
				assignKey(room, true);
				if (Settings.DEBUG) System.out.println("Assigned crit lock to room at: " + room.x + ", " + room.y);
			}
		}

// Some extra crit locks
		if (Settings.DEBUG) System.out.println("Assigning extra crit locks...");
		List<RoomNode> extraCritLockCandidates = shuffledRooms()
				.filter(r -> !r.isBoss && r.isCritPath && r.key == -1)
				.collect(Collectors.toList());

		long extraCritLocks = Math.min((size * 2) + 1, extraCritLockCandidates.size());
		if (Settings.DEBUG) System.out.println("Attempting to assign " + extraCritLocks + " extra crit locks");

		int assignedExtraLocks = 0;
		for (int i = 0; i < extraCritLocks; i++) {
			if (i >= extraCritLockCandidates.size() || availableKeys.isEmpty()) {
				break;
			}
			RoomNode room = extraCritLockCandidates.get(i);
			assignKey(room, true);
			assignedExtraLocks++;
			if (Settings.DEBUG) System.out.println("Assigned extra crit lock to room at: " + room.x + ", " + room.y);
		}

		if (Settings.DEBUG) System.out.println("Assigned " + assignedExtraLocks + " extra crit locks");

		// Boss lock assignment
		if (Settings.DEBUG) System.out.println("Checking boss lock...");
		if (boss.lock == -1) {
			if (Settings.DEBUG) System.out.println("Boss has no lock, attempting to assign one...");
			// Optionally force a lock on the boss (RS has boss locks ~95% of the time)
			if (random.nextInt(100) < 95) { // 95% chance for boss lock
				List<RoomNode> critKeyRooms = shuffledRooms()
						.filter(r -> r.isCritPath && !r.isBoss && r.key == -1)
						.collect(Collectors.toList());

				if (Settings.DEBUG) System.out.println("Found " + critKeyRooms.size() + " available crit key rooms");
				if (Settings.DEBUG) System.out.println("Available keys: " + availableKeys.size());

				if (!critKeyRooms.isEmpty() && !availableKeys.isEmpty()) {
					RoomNode keyRoom = critKeyRooms.get(random.nextInt(critKeyRooms.size()));
					keyRoom.key = availableKeys.remove(0);
					boss.lock = keyRoom.key;
					if (Settings.DEBUG) System.out.println("Assigned boss lock with key from room at: " + keyRoom.x + ", " + keyRoom.y);
				} else {
					if (Settings.DEBUG) System.out.println("Could not assign boss lock - no available key rooms or keys");
				}
			} else {
				if (Settings.DEBUG) System.out.println("Boss lock skipped (5% chance)");
			}
		} else {
			if (Settings.DEBUG) System.out.println("Boss already has a lock");
		}

		// Bonus keys
		if (Settings.DEBUG) System.out.println("Assigning bonus keys...");
		long nonCritRooms = rooms().filter(r -> !r.isCritPath).count();
		long bonusLockCount = nonCritRooms / 4 + random.nextInt((size + 1) * 2);
		if (Settings.DEBUG) System.out.println("Non-crit rooms: " + nonCritRooms + ", assigning " + bonusLockCount + " bonus locks");

		long assignedBonusLocks = shuffledRooms().filter(r -> !r.isBoss && r.key == -1).limit(bonusLockCount).count();
		if (Settings.DEBUG) System.out.println("Assigned " + assignedBonusLocks + " bonus locks");

		if (Settings.DEBUG) {
			System.out.println("Dungeon generation completed successfully!");
			System.out.println("Final room count: " + roomList.size());
			System.out.println("Crit path rooms: " + rooms().filter(r -> r.isCritPath).count());
			System.out.println("Total keys assigned: " + rooms().filter(r -> r.key != -1).count());
			System.out.println("Total locks assigned: " + rooms().filter(r -> r.lock != -1).count());
		}

		return true;
	}

	// Helper method to add neighbors to queue
	private void addNeighborsToQueue(List<Point> queue, Set<Point> visitedPoints, int x, int y) {
		int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

		for (int[] dir : directions) {
			int newX = x + dir[0];
			int newY = y + dir[1];
			Point neighbor = new Point(newX, newY);

			// Check if within bounds and not already visited/queued
			if (newX >= 0 && newX < rooms.length &&
					newY >= 0 && newY < rooms[0].length &&
					!visitedPoints.contains(neighbor) &&
					getRoom(newX, newY) == null) {

				queue.add(neighbor);
				visitedPoints.add(neighbor);
				if (Settings.DEBUG) System.out.println("Added neighbor to queue: " + newX + ", " + newY);
			}
		}
	}

	RoomNode randomParent(int x, int y) {
		List<RoomNode> neighbors = new LinkedList<RoomNode>();
		RoomNode west = getRoom(x - 1, y);
		RoomNode east = getRoom(x + 1, y);
		RoomNode north = getRoom(x, y - 1);
		RoomNode south = getRoom(x, y + 1);

		if (west != null) neighbors.add(west);
		if (east != null) neighbors.add(east);
		if (north != null) neighbors.add(north);
		if (south != null) neighbors.add(south);

		if (Settings.DEBUG) System.out.println("Found " + neighbors.size() + " potential parents for " + x + ", " + y);

		return neighbors.isEmpty() ? null : neighbors.get(random.nextInt(neighbors.size()));
	}

	public RoomNode getBase() {
		return base;
	}

	public void setBase(RoomNode newBase) {
		if (base == newBase) {
			return;
		}
		base = newBase;
		swapTree(newBase);
	}

	private void swapTree(RoomNode child) {
		if (child.parent != null && child.parent.parent != null) {
			swapTree(child.parent);
		}
		if (child.parent != null) {
			child.parent.parent = child;
			child.parent.children.remove(child);
			child.children.add(child.parent);
			child.parent = null;
		}
	}

	public RoomNode getRoom(int x, int y) {
		if (x < 0 || y < 0 || x >= rooms.length || y >= rooms[x].length) {
			return null;
		}
		return rooms[x][y];
	}

	public void addRoom(RoomNode room) {
		rooms[room.x][room.y] = room;
		roomList.add(room);
	}

	public void removeRoom(RoomNode r) {
		if (r.parent != null) {
			r.parent.children.remove(r);
		}
		roomList.remove(r);
		rooms[r.x][r.y] = null;
	}

	public Stream<RoomNode> rooms() {
		return roomList.stream();
	}

	public Stream<RoomNode> shuffledRooms() {
		List<RoomNode> shuffled = new ArrayList<>(roomList);
		Collections.shuffle(shuffled, random);
		return shuffled.stream();
	}

	public int getRoomCount() {
		return roomList.size();
	}

	public void assignKey(RoomNode keyRoom, boolean critLock) {
		if (availableKeys.isEmpty()) {
			if (Settings.DEBUG) System.out.println("No keys available for assignment");
			return;
		}

		// SAFETY CHECK: If no candidates after several tries, use fallback
		int attempts = 0;
		List<RoomNode> candidates;

		do {
			List<RoomNode> unrelated = getUnrelatedRooms(keyRoom);
			List<RoomNode> children = keyRoom.getChildrenR();

			candidates = rooms()
					.filter(r -> r.lock == -1 && !r.equals(keyRoom))
					.collect(Collectors.toList());

			if (critLock) {
				candidates = candidates.stream()
						.filter(r -> r.isCritPath)
						.collect(Collectors.toList());
			}

			candidates.retainAll(unrelated);

			if (keyshare) {
				candidates.removeAll(children);
			}

			attempts++;

		} while (candidates.isEmpty() && attempts < 3); // Try up to 3 times

		if (Settings.DEBUG) {
			System.out.println("Key assignment for room at " + keyRoom.x + ", " + keyRoom.y);
			System.out.println("Found " + candidates.size() + " candidate lock rooms");
			System.out.println("Available keys: " + availableKeys.size());
		}

		if (candidates.isEmpty()) {
			if (Settings.DEBUG) System.out.println("Using fallback: any room without lock");
			candidates = rooms()
					.filter(r -> r.lock == -1 && !r.equals(keyRoom))
					.collect(Collectors.toList());
		}

		if (!candidates.isEmpty()) {
			RoomNode lockRoom = random(candidates);
			if (!availableKeys.isEmpty()) {
				int key = availableKeys.remove(0);
				keyRoom.key = key;
				lockRoom.lock = key;
				if (Settings.DEBUG) System.out.println("Assigned key " + key + " to room at " + keyRoom.x + ", " + keyRoom.y + " with lock at " + lockRoom.x + ", " + lockRoom.y);
			}
		} else {
			if (Settings.DEBUG) System.out.println("CRITICAL: No rooms available for lock assignment!");
		}
	}

	public List<RoomNode> getUnrelatedRooms(RoomNode room) {
		if (Settings.DEBUG) System.out.println("Finding unrelated rooms for room at: " + room.x + ", " + room.y);

		List<RoomNode> reachable = new ArrayList<>(roomList);
		Set<RoomNode> visited = new HashSet<>();
		Queue<RoomNode> queue = new LinkedList<>();

		queue.add(room);
		visited.add(room);

		while (!queue.isEmpty()) {
			RoomNode current = queue.poll();
			reachable.remove(current);

			// Follow locks
			if (current.lock != -1) {
				RoomNode keyRoom = rooms()
						.filter(r -> r.key == current.lock)
						.findFirst()
						.orElse(null);
				if (keyRoom != null && !visited.contains(keyRoom)) {
					queue.add(keyRoom);
					visited.add(keyRoom);
				}
			}

			// Follow parent and children
			if (current.parent != null && !visited.contains(current.parent)) {
				queue.add(current.parent);
				visited.add(current.parent);
			}

			for (RoomNode child : current.children) {
				if (!visited.contains(child)) {
					queue.add(child);
					visited.add(child);
				}
			}
		}
		List<RoomNode> unrelated = new ArrayList<>();
		for (RoomNode r : roomList) {
			if (!reachable.contains(r)) {
				unrelated.add(r);
			}
		}

		if (Settings.DEBUG) System.out.println("Found " + reachable.size() + " unrelated rooms");
		return reachable;
	}
	<T> T random(List<T> list) {
		return list.isEmpty() ? null : list.get(random.nextInt(list.size()));
	}

	<T> T randomFromQueue(List<T> queue) {
		if (queue.isEmpty()) return null;
		int index = random.nextInt(queue.size());
		return queue.get(index);
	}
}