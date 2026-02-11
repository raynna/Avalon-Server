package com.rs.java.game.player;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.rs.Settings;
import com.rs.java.game.Hit;
import com.rs.java.game.World;
import com.rs.java.game.npc.NPC;
import com.rs.core.packets.OutputStream;
import com.rs.java.game.player.content.Tint;
import com.rs.java.utils.Utils;

public final class LocalNPCUpdate {

	private transient Player player;
	private LinkedList<NPC> localNPCs;

	public void reset() {
		localNPCs.clear();
	}

	public LocalNPCUpdate(Player player) {
		this.player = player;
		localNPCs = new LinkedList<>();
	}

	public OutputStream createPacketAndProcess() {
		boolean largeSceneView = player.hasLargeSceneView();
		OutputStream stream = new OutputStream();
		OutputStream updateBlockData = new OutputStream();
		stream.writePacketVarShort(player, largeSceneView ? 64 : 72);
		processLocalNPCsInform(stream, updateBlockData, largeSceneView);
		stream.writeBytes(updateBlockData.getBuffer(), 0, updateBlockData.getOffset());
		stream.endPacketVarShort();
		return stream;
	}

	private void processLocalNPCsInform(OutputStream stream, OutputStream updateBlockData, boolean largeSceneView) {
		stream.initBitAccess();
		processInScreenNPCs(stream, updateBlockData, largeSceneView);
		addInScreenNPCs(stream, updateBlockData, largeSceneView);
		if (updateBlockData.getOffset() > 0)
			stream.writeBits(15, 32767);
		stream.finishBitAccess();
	}

	private void processInScreenNPCs(OutputStream stream, OutputStream updateBlockData, boolean largeSceneView) {
		stream.writeBits(8, localNPCs.size());
		for (Iterator<NPC> it = localNPCs.iterator(); it.hasNext();) {
			NPC n = it.next();
			if (n == null || n.hasFinished() || !n.withinDistance(player, largeSceneView ? 126 : 14) || n.hasTeleported()) {
				stream.writeBits(1, 1);
				stream.writeBits(2, 3);
				it.remove();
				continue;
			}
			boolean needUpdate = n.needMasksUpdate();
			boolean walkUpdate = n.getNextWalkDirection() != -1;
			stream.writeBits(1, (needUpdate || walkUpdate) ? 1 : 0);
			if (walkUpdate) {
				stream.writeBits(2, n.getNextRunDirection() == -1 ? 1 : 2);
				if (n.getNextRunDirection() != -1)
					stream.writeBits(1, 1);
				stream.writeBits(3, Utils.getNpcMoveDirection(n.getNextWalkDirection()));
				if (n.getNextRunDirection() != -1)
					stream.writeBits(3, Utils.getNpcMoveDirection(n.getNextRunDirection()));
				stream.writeBits(1, needUpdate ? 1 : 0);
			} else if (needUpdate) {
				stream.writeBits(2, 0);
			}
			if (needUpdate)
				appendUpdateBlock(n, updateBlockData, false);
		}
	}

	private void addInScreenNPCs(OutputStream stream, OutputStream updateBlockData, boolean largeSceneView) {
		for (int regionId : player.getMapRegionsIds()) {
			List<Integer> indexes = World.getRegion(regionId).getNPCsIndexes();
			if (indexes == null)
				continue;
			for (int npcIndex : indexes) {
				if (localNPCs.size() == Settings.LOCAL_NPCS_LIMIT)
					break;
				NPC n = World.getNPCs().get(npcIndex);
				if (n == null || n.hasFinished() || localNPCs.contains(n)
						|| !n.withinDistance(player, largeSceneView ? 126 : 14) || n.isDead())
					continue;
				stream.writeBits(15, n.getIndex());
				boolean needUpdate = n.needMasksUpdate() || n.getLastFaceEntity() != -1;
				int x = n.getX() - player.getX();
				int y = n.getY() - player.getY();
				if (largeSceneView) {
					if (x < 127) x += 256;
					if (y < 127) y += 256;
				} else {
					if (x < 15) x += 32;
					if (y < 15) y += 32;
				}
				stream.writeBits(3, getNpcDirection(n));
				stream.writeBits(largeSceneView ? 8 : 5, y);
				stream.writeBits(15, n.getId());
				stream.writeBits(largeSceneView ? 8 : 5, x);
				stream.writeBits(2, n.getPlane());
				stream.writeBits(1, needUpdate ? 1 : 0);
				stream.writeBits(1, n.hasTeleported() ? 1 : 0);
				localNPCs.add(n);
				if (needUpdate)
					appendUpdateBlock(n, updateBlockData, true);
			}
		}
	}

	public static int getNpcDirection(NPC npc) {
		int direction = npc.getDirection();
		return direction >> 8;
	}

	private void appendUpdateBlock(NPC n, OutputStream data, boolean added) {
		int maskData = 0;
		//missing 0x2
		//missing 0x8000
		//missing 0x10_000
		if (n.getNextForceTalk() != null) maskData |= 0x20;//correctorder
		if (n.getNextFaceEntity() != -2 || (added && n.getLastFaceEntity() != -1)) maskData |= 0x10;//correctorder
		if (n.getTint() != null)
			maskData |= 0x80000;
		if (n.getNextFaceWorldTile() != null && n.getNextRunDirection() == -1 && n.getNextWalkDirection() == -1)
			maskData |= 0x80;//correctorder
		if (n.getNextAnimation() != null) maskData |= 0x8;//correctorder
		//missing 0x200
		//missing 0x2000
		if (!n.getNextHits().isEmpty()) maskData |= 0x4;//correctorder
		//missing 0x1000
		if (n.getNextGraphics4() != null) maskData |= 0x2000000;//correctorder
		if (n.getNextGraphics1() != null) maskData |= 0x40;//correctorder
		if (n.getNextGraphics2() != null) maskData |= 0x100;//correctorder
		//missing 0x4000
		//missing 0x400
		//missing 0x100_000
		if (n.getNextForceMovement() != null) maskData |= 0x800;//correctorder
		//missing 0x400_000
		if (n.getNextGraphics3() != null) maskData |= 0x1000000;//correctorder
		if (n.hasChangedName() || (added && n.getCustomName() != null)) maskData |= 0x800000;//correctorder
		//missing 0x20_000
		//missing 0x200_000
		if (n.hasChangedCombatLevel() || (added && n.getCustomCombatLevel() >= 0)) maskData |= 0x40000;//correctorder
		if (n.getNextTransformation() != null) maskData |= 0x1;//correctorder
		if (maskData > 0xff) maskData |= 0x2;
		if (maskData > 0xffff) maskData |= 0x8000;
		if (maskData > 0xffffff) maskData |= 0x10000;

		data.writeByte(maskData);
		if (maskData > 0xff) data.writeByte(maskData >> 8);
		if (maskData > 0xffff) data.writeByte(maskData >> 16);
		if (maskData > 0xffffff) data.writeByte(maskData >> 24);

		if (n.getNextForceTalk() != null) applyForceTalkMask(n, data);
		if (n.getNextFaceEntity() != -2 || (added && n.getLastFaceEntity() != -1)) applyFaceEntityMask(n, data);
		if (n.getTint() != null) applyTint(n, data);
		if (n.getNextFaceWorldTile() != null && n.getNextRunDirection() == -1 && n.getNextWalkDirection() == -1)
			applyFaceWorldTileMask(n, data);
		if (n.getNextAnimation() != null) applyAnimationMask(n, data);
		if (!n.getNextHits().isEmpty()) applyHitMask(n, data);
		if (n.getNextGraphics4() != null) applyGraphicsMask4(n, data);
		if (n.getNextGraphics1() != null) applyGraphicsMask1(n, data);
		if (n.getNextGraphics2() != null) applyGraphicsMask2(n, data);
		if (n.getNextForceMovement() != null) applyForceMovementMask(n, data);
		if (n.getNextGraphics3() != null) applyGraphicsMask3(n, data);
		if (n.hasChangedName() || (added && n.getCustomName() != null)) applyNameChangeMask(n, data);
		if (n.hasChangedCombatLevel() || (added && n.getCustomCombatLevel() >= 0)) applyChangeLevelMask(n, data);
		if (n.getNextTransformation() != null) applyTransformationMask(n, data);
	}

	private void applyChangeLevelMask(NPC n, OutputStream data) {
		data.writeShortLE(Math.max(0, n.getCombatLevel()));
	}

	private void applyNameChangeMask(NPC npc, OutputStream data) {
		String name = npc.getName();
		if (name == null) name = "null";
		data.writeString(name);
	}

	private void applyTransformationMask(NPC n, OutputStream data) {
		if (n.getNextTransformation() != null)
			data.writeBigSmart(n.getNextTransformation().getToNPCId());
	}

	private void applyForceTalkMask(NPC n, OutputStream data) {
		String text = n.getNextForceTalk().getText();
		if (text == null) text = "";
		data.writeString(text);
	}


	private void applyTint(NPC n, OutputStream data) {
		Tint mask = n.getTint();

		data.writeByte128(mask.hue() & 0xFF);
		data.writeByte128(mask.saturation() & 0xFF);
		data.writeByteC(mask.lightness() & 0xFF);
		data.writeByte128(mask.strength() & 0xFF);

		data.writeShortLE128(mask.startDelay());
		data.writeShortLE128(mask.duration());

	}

	private void applyForceMovementMask(NPC n, OutputStream data) {
		if (n.getNextForceMovement() == null) return;
		data.write128Byte(n.getNextForceMovement().getToFirstTile().getX() - n.getX());
		data.writeByte(n.getNextForceMovement().getToFirstTile().getY() - n.getY());
		data.writeByteC(n.getNextForceMovement().getToSecondTile() == null ? 0 :
				n.getNextForceMovement().getToSecondTile().getX() - n.getX());
		data.writeByteC(n.getNextForceMovement().getToSecondTile() == null ? 0 :
				n.getNextForceMovement().getToSecondTile().getY() - n.getY());
		data.writeShortLE((n.getNextForceMovement().getFirstTileTicketDelay() * 600) / 20);
		data.writeShortLE128(n.getNextForceMovement().getToSecondTile() == null ? 0 :
				((n.getNextForceMovement().getSecondTileTicketDelay() * 600) / 20));
		data.writeShort128(n.getNextForceMovement().getDirection());
	}

	private void applyFaceWorldTileMask(NPC n, OutputStream data) {
		if (n.getNextFaceWorldTile() == null) return;
		data.writeShort128((n.getNextFaceWorldTile().getX() << 1) + 1);
		data.writeShortLE128((n.getNextFaceWorldTile().getY() << 1) + 1);
	}

	private void applyHitMask(NPC n, OutputStream data) {
		int count = n.getNextHits().size();
		data.writeByteC(count);
		if (count > 0) {
			int hp = n.getHitpoints();
			int maxHp = Math.max(1, n.getMaxHitpoints()); // avoid div by zero
			if (hp > maxHp) hp = maxHp;
			int hpBarPercentage = hp * 255 / maxHp;
			for (Hit hit : n.getNextHits()) {
				if (hit == null) continue;
				boolean interactingWith = hit.interactingWith(player, n);
				if (hit.missed() && !interactingWith) {
					data.writeSmart(32766);
				} else {
					boolean oneXHits = player.getVarsManager().getBitValue(1485) == 1;
					int reduced = (int) Math.max(1, Math.ceil(hit.getDamage() / 10.0));
					double hitAmount = oneXHits ? reduced : hit.getDamage();
					if (hit.getSoaking() != null) {
						int reducedSoak = (int) Math.max(1, Math.ceil(hit.getSoaking().getDamage() / 10.0));
						double soakAmount = oneXHits ? reducedSoak : hit.getSoaking().getDamage();
						data.writeSmart(32767);
						data.writeSmart(hit.getMark(player, n));
						data.writeSmart((int) hitAmount);
						data.writeSmart(hit.getSoaking().getMark(player, n));
						data.writeSmart((int) soakAmount);
					} else {
						data.writeSmart(hit.getMark(player, n));
						data.writeSmart((int) hitAmount);
					}
				}
				data.writeSmart(hit.getDelay());
				data.writeByte128(hpBarPercentage);
			}
		}
	}

	private void applyFaceEntityMask(NPC n, OutputStream data) {
		data.writeShortLE128(n.getNextFaceEntity() == -2 ? n.getLastFaceEntity() : n.getNextFaceEntity());
	}

	private void applyAnimationMask(NPC n, OutputStream data) {
		if (n.getNextAnimation() == null) return;
		for (int id : n.getNextAnimation().getIds())
			data.writeBigSmart(id);
		data.writeByte(n.getNextAnimation().getDelay());
	}

	private void applyGraphicsMask4(NPC n, OutputStream data) {
		data.writeShort128(n.getNextGraphics4().getId());
		data.writeInt(n.getNextGraphics4().getSettingsHash());
		data.writeByte128(n.getNextGraphics4().getSettings2Hash());
	}

	private void applyGraphicsMask3(NPC n, OutputStream data) {
		data.writeShortLE128(n.getNextGraphics3().getId());
		data.writeInt(n.getNextGraphics3().getSettingsHash());
		data.write128Byte(n.getNextGraphics3().getSettings2Hash());
	}

	private void applyGraphicsMask2(NPC n, OutputStream data) {
		data.writeShort128(n.getNextGraphics2().getId());
		data.writeIntLE(n.getNextGraphics2().getSettingsHash());
		data.writeByteC(n.getNextGraphics2().getSettings2Hash());
	}

	private void applyGraphicsMask1(NPC n, OutputStream data) {
		data.writeShortLE128(n.getNextGraphics1().getId());
		data.writeIntV1(n.getNextGraphics1().getSettingsHash());
		data.write128Byte(n.getNextGraphics1().getSettings2Hash());
	}
}
