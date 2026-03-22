package raynna.core.packets.decode.world.support;

import raynna.core.packets.decode.WorldPacketsDecoder;

import raynna.core.packets.InputStream;
import raynna.core.packets.handlers.InventoryOptionsHandler;
import raynna.core.packets.handlers.ObjectHandler;
import raynna.game.Animation;
import raynna.game.Graphics;
import raynna.game.World;
import raynna.game.WorldObject;
import raynna.game.WorldTile;
import raynna.game.item.FloorItem;
import raynna.game.item.Item;
import raynna.game.item.ground.AutomaticGroundItem;
import raynna.game.item.ground.GroundItems;
import raynna.game.npc.NPC;
import raynna.game.npc.familiar.Familiar;
import raynna.game.npc.familiar.Familiar.SpecialAttack;
import raynna.game.player.Inventory;
import raynna.game.player.Player;
import raynna.game.player.RouteEvent;
import raynna.game.player.actions.PlayerFollow;
import raynna.game.player.content.clans.ClansManager;
import raynna.game.player.bot.PlayerBotManager;
import raynna.game.route.RouteFinder;
import raynna.game.route.strategy.FixedTileStrategy;
import raynna.util.Utils;
import raynna.game.player.combat.CombatAction;
import raynna.game.player.combat.magic.Spell;
import raynna.game.player.combat.magic.SpellHandler;
import raynna.game.player.combat.magic.SpellType;
import raynna.game.player.combat.magic.Spellbook;
import raynna.game.world.pvp.PvpManager;

public final class WorldInteractionSupport {

    private WorldInteractionSupport() {
    }
    public static boolean basicPlayerActiveAndLoaded(Player p) {
        return p != null && p.hasStarted() && p.clientHasLoadedMapRegion() && !p.isDead();
    }
    public static boolean canUseInput(Player p) {
        long currentTime = Utils.currentTimeMillis();
        return !p.isLocked() && p.getEmotesManager().getNextEmoteEnd() < currentTime && !p.isLocked();
    }
    public static void handleWalking(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player))
            return;
        if (player.isDead()) {
            player.resetWalkSteps();
            return;
        }
        if (player.isLocked()) {
            return;
        }
        if (player.isFrozen()) {
            player.getPackets().sendGameMessage("A magical force prevents you from moving.");
            player.stopAll(true, false, true);
            return;
        }
        if (!player.getControlerManager().canMove(0)) {
            player.stopAll(true, false, true);
            return;
        }
        int length = stream.getLength();
        int baseX = stream.readUnsignedShort128();
        boolean forceRun = stream.readUnsigned128Byte() == 1;
        int baseY = stream.readUnsignedShort128();
        int steps = (length - 5) / 2;
        if (steps > 25)
            steps = 25;
        player.stopAll();
        player.resetWalkSteps();
        player.setNextFaceEntity(null);
        if (forceRun)
            player.setRun(true);
        if (steps <= 0)
            return;
        int x = 0, y = 0;
        for (int step = 0; step < steps; step++) {
            x = baseX + stream.readUnsignedByte();
            y = baseY + stream.readUnsignedByte();
        }
        steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, player.getX(), player.getY(),
                player.getPlane(), player.getSize(), new FixedTileStrategy(x, y), true);
        int[] bufferX = RouteFinder.getLastPathBufferX();
        int[] bufferY = RouteFinder.getLastPathBufferY();
        int last = -1;
        for (int i = steps - 1; i >= 0; i--) {
            if (!player.addWalkSteps(bufferX[i], bufferY[i], 25, true))
                break;
            last = i;
        }

        if (last != -1) {
            WorldTile tile = new WorldTile(bufferX[last], bufferY[last], player.getPlane());
            player.getPackets().sendMinimapFlag(
                    tile.getLocalX(player.getLastLoadedMapRegionTile(), player.getMapSize()),
                    tile.getLocalY(player.getLastLoadedMapRegionTile(), player.getMapSize()));
        } else {
            player.getPackets().sendResetMinimapFlag();
        }
        if (player.temporaryAttribute().get("Dreaming") == Boolean.TRUE) {
            player.stopAll(true);
            player.animate(new Animation(6297));
            player.temporaryAttribute().remove("Dreaming");
        }
    }
    public static void handleInterfaceOnObject(Player player, InputStream stream) {
        boolean forceRun = stream.readByte128() == 1;
        int itemId = stream.readShortLE128();
        int y = stream.readShortLE128();
        int objectId = stream.readIntV2();
        int interfaceHash = stream.readInt();
        final int interfaceId = interfaceHash >> 16;
        int componentId = interfaceHash - (interfaceId << 16);
        int slot = stream.readShortLE();
        int x = stream.readShort128();

        if (!basicPlayerActiveAndLoaded(player))
            return;
        if (!canUseInput(player))
            return;

        final WorldTile tile = new WorldTile(x, y, player.getPlane());
        int regionId = tile.getRegionId();
        if (!player.getMapRegionsIds().contains(regionId))
            return;

        WorldObject mapObject = World.getObjectWithId(tile, objectId);
        if (mapObject == null || mapObject.getId() != objectId)
            return;
        final WorldObject object = !player.isAtDynamicRegion() ? mapObject
                : new WorldObject(objectId, mapObject.getType(), mapObject.getRotation(), x, y, player.getPlane());
        final Item item = player.getInventory().getItem(slot);
        if (player.isDead() || componentId < 0 || Utils.getInterfaceDefinitionsSize() <= interfaceId)
            return;
        if (player.isLocked())
            return;
        if (!player.getInterfaceManager().containsInterface(interfaceId))
            return;
        player.stopAll(false);
        if (forceRun)
            player.setRun(true);

        switch (interfaceId) {
            case 192, 430 -> player.setRouteEvent(new RouteEvent(object, () -> {
                player.faceObject(object);
                SpellHandler.INSTANCE.castOnObject(player, componentId, object);
            }));
            case Inventory.INVENTORY_INTERFACE -> {
                if (item == null || item.getId() != itemId)
                    return;
                ObjectHandler.handleItemOnObject(player, object, interfaceId, item);
            }
        }
    }
    public static void handlePlayerOption2(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player))
            return;
        stream.readByte();
        int playerIndex = stream.readUnsignedShortLE128();
        Player p2 = World.getPlayers().get(playerIndex);
        if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
            return;
        if (player.isLocked())
            return;

        if (player.getEquipment().getWeaponId() == 10501) {
            player.faceEntity(p2);
            player.getEquipment().deleteItem(10501, 1);
            player.stopAll(true);
            player.animate(new Animation(7530));
            World.sendFastBowProjectile(player, p2, 1281);
            p2.gfx(new Graphics(862, 100, 0));
            return;
        }
        PlayerBotManager.setConversationTarget(player, p2);
        player.stopAll(false);
        player.getActionManager().setAction(new PlayerFollow(p2));
    }
    public static void handlePlayerOption5(Player player, InputStream stream) {
        stream.readByte();
        int playerIndex = stream.readUnsignedShortLE128();
        final Player p2 = World.getPlayers().get(playerIndex);
        player.setRouteEvent(new RouteEvent(p2, () -> {
            if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
                return;
            if (player.isLocked())
                return;
            player.stopAll(false);
            if (player.getPlayerRank().isIronman()) {
                player.message("You cannot assist as a " + (player.getPlayerRank().isHardcore()
                        ? "Hardcore Iron " + (player.getAppearance().isMale() ? "Man" : "Woman") + "."
                        : "Iron " + (player.getAppearance().isMale() ? "Man" : "Woman") + "."));
                return;
            }
            if (p2.getPlayerRank().isIronman()) {
                player.message("You cannot assist a " + (p2.getPlayerRank().isHardcore() ? "HC ironman." : "Ironman."));
                return;
            }
            if (player.isCantTrade()) {
                player.getPackets().sendGameMessage("You are busy.");
                return;
            }
            if (p2.getInterfaceManager().containsScreenInter() || p2.isCantTrade()) {
                player.getPackets().sendGameMessage("The other player is busy.");
                return;
            }
            if (p2.temporaryAttribute().get("assist") == player) {
                p2.temporaryAttribute().remove("assist");
                player.getAssist().Assist(p2);
                return;
            }
            player.temporaryAttribute().put("assist", p2);
            player.message("Currently Disabled.");
        }));
    }
    public static void handlePlayerOption6(Player player, InputStream stream) {
        stream.readByte();
        int playerIndex = stream.readUnsignedShortLE128();
        final Player p2 = World.getPlayers().get(playerIndex);
        player.setRouteEvent(new RouteEvent(p2, () -> {
            if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
                return;
            if (player.isLocked())
                return;
            player.message("Currently out of order.");
        }));
    }
    public static void handlePlayerOption4(Player player, InputStream stream) {
        stream.readByte();
        int playerIndex = stream.readUnsignedShortLE128();
        final Player p2 = World.getPlayers().get(playerIndex);
        player.setRouteEvent(new RouteEvent(p2, () -> {
            if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
                return;
            if (player.isLocked())
                return;
            player.stopAll(false);
            if (player.getPlayerRank().isIronman()) {
                player.message("You cannot trade as a " + (player.getPlayerRank().isHardcore()
                        ? "Hardcore Iron " + (player.getAppearance().isMale() ? "Man" : "Woman") + "."
                        : "Iron " + (player.getAppearance().isMale() ? "Man" : "Woman") + "."));
                return;
            }
            if (p2.getPlayerRank().isIronman()) {
                player.message("You cannot trade a " + (p2.getPlayerRank().isHardcore() ? "HC ironman." : "Ironman."));
                return;
            }
            if (player.isCantTrade()) {
                player.getPackets().sendGameMessage("You are busy.");
                return;
            }
            if (p2.getInterfaceManager().containsScreenInter() || p2.isCantTrade()) {
                player.getPackets().sendGameMessage("The other player is busy.");
                return;
            }
            if (player.getTemporaryAttributtes().remove("claninvite") != null) {
                ClansManager.viewInvite(player, p2);
                return;
            }
            if (p2.temporaryAttribute().get("TradeTarget") == player) {
                p2.temporaryAttribute().remove("TradeTarget");
                player.getTrade().openTrade(p2);
                p2.getTrade().openTrade(player);
                return;
            }
            player.temporaryAttribute().put("TradeTarget", p2);
            player.getPackets().sendGameMessage("Sending trade offer...");
            p2.getPackets().sendTradeRequestMessage(player);
        }));
    }
    public static void handlePlayerOption1Attack(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player))
            return;
        stream.readByte();
        int playerIndex = stream.readUnsignedShortLE128();
        final Player p2 = World.getPlayers().get(playerIndex);
        if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
            return;
        if (player.isLocked())
            return;
        if (player.getTemporaryAttributtes().get("DUNGEON_INVITE_RECIEVED") != null) {
            Player inviteBy = (Player) player.getTemporaryAttributtes().get("DUNGEON_INVITE_RECIEVED");
            if (inviteBy != null)
                player.getDungManager().acceptInvite(inviteBy.getDisplayName());
            else
                player.message("inviteBy is null");
            return;
        }
        if (!PvpManager.canPlayerAttack(player, p2))
            return;
        if (!player.getControlerManager().canPlayerOption1(p2))
            return;
        if (!player.isCanPvp())
            return;
        if (!player.getControlerManager().canAttack(p2))
            return;
        player.stopAll();
        player.getActionManager().setAction(new CombatAction(p2));
    }
    public static void handlePlayerOption9(Player player, InputStream stream) {
        boolean forceRun = stream.readUnsignedByte() == 1;
        int playerIndex = stream.readUnsignedShortLE128();
        Player p2 = World.getPlayers().get(playerIndex);
        if (p2 == null || p2 == player || p2.isDead() || p2.hasFinished()
                || !player.getMapRegionsIds().contains(p2.getRegionId()))
            return;
        if (player.isLocked())
            return;
        if (forceRun)
            player.setRun(true);
        player.stopAll();
        ClansManager.viewInvite(player, p2);
    }
    public static void handleAttackNpc(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player))
            return;
        if (player.isLocked())
            return;
        int npcIndex = stream.readUnsignedShort128();
        boolean forceRun = stream.read128Byte() == 1;
        player.stopAll();
        if (forceRun)
            player.setRun(true);
        NPC npc = World.getNPCs().get(npcIndex);
        if (npc == null || npc.isDead() || npc.hasFinished()
                || !player.getMapRegionsIds().contains(npc.getRegionId())
                || !npc.getDefinitions().hasAttackOption())
            return;
        if (!player.getControlerManager().canAttack(npc))
            return;
        if (npc instanceof Familiar familiar) {
            if (familiar == player.getFamiliar()) {
                player.getPackets().sendGameMessage("You can't attack your own familiar.");
                return;
            }
            if (!familiar.canAttack(player)) {
                player.getPackets().sendGameMessage("You can't attack this npc.");
                return;
            }
        } else if (!npc.isForceMultiAttacked()) {
            if (player.isAtMultiArea() && !npc.isAtMultiArea()) {
                if (npc.getAttackedBy() != player && npc.isPjBlocked()) {
                    player.getPackets().sendGameMessage("This npc is already in combat.");
                    return;
                }
            }
        }
        player.stopAll();
        player.getActionManager().setAction(new CombatAction(npc));
    }
    public static void handleSpellOnFloorItem(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player) || player.isLocked())
            return;
        boolean forceRun = stream.readUnsignedByte() == 1;
        int anInt8906 = stream.readShortLE128();
        int x = stream.readShortLE128();
        int y = stream.readShort128();
        int interfaceHash = stream.readIntLE();
        int walkType = stream.readShort128();
        int itemId = stream.readShortLE128();
        int interfaceId = interfaceHash >>> 16;
        int spellCompId = interfaceHash & 0xFFFF;
        if (forceRun) player.setRun(true);
        System.out.println("=== SPELL ON FLOOR ITEM ===");
        System.out.println("interfaceId=" + interfaceId + ", spellCompId=" + spellCompId);
        System.out.println("itemId=" + itemId + ", x=" + x + ", y=" + y + ", walkType=" + walkType + ", anInt8906=" + anInt8906);
        if (interfaceId != 192) {
            return;
        }
        WorldTile tile = new WorldTile(x, y, player.getPlane());
        int regionId = tile.getRegionId();
        if (!player.getMapRegionsIds().contains(regionId))
            return;
        FloorItem floorItem = World.getRegion(regionId).getGroundItem(itemId, tile, player);
        if (floorItem == null)
            return;
        player.stopAll(false);
        SpellHandler.INSTANCE.castOnFloorItem(player, spellCompId, floorItem);
    }
    public static void handleInterfaceOnPlayer(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player))
            return;
        if (player.isLocked())
            return;
        stream.readUnsignedShort();
        int playerIndex = stream.readUnsignedShort();
        int interfaceHash = stream.readIntV2();
        stream.readUnsignedShortLE128();
        stream.read128Byte();
        int interfaceId = interfaceHash >> 16;
        int componentId = interfaceHash - (interfaceId << 16);
        if (componentId < 0 || Utils.getInterfaceDefinitionsSize() <= interfaceId)
            return;
        if (!player.getInterfaceManager().containsInterface(interfaceId))
            return;
        if (componentId == 65535) componentId = -1;
        if (componentId != -1 && Utils.getInterfaceDefinitionsComponentsSize(interfaceId) <= componentId)
            return;
        final Player p2 = World.getPlayers().get(playerIndex);
        if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
            return;
        player.message("interfaceid:" + interfaceId);
        switch (interfaceId) {
            case 1110 -> {
                if (componentId == 87) {
                    player.setRouteEvent(new RouteEvent(p2, () -> ClansManager.invite(player, p2)));
                }
            }
            case 662, 747 -> {
                if (player.getFamiliar() == null)
                    return;
                if ((interfaceId == 747 && componentId == 15)
                        || (interfaceId == 662 && componentId == 65)
                        || (interfaceId == 662 && componentId == 74)
                        || (interfaceId == 747 && componentId == 18)) {
                    if (!player.isCanPvp() || !p2.isCanPvp()) {
                        player.getPackets().sendGameMessage("You can only attack players in a player-vs-player area.");
                        return;
                    }
                    if (!player.getFamiliar().canAttack(p2)) {
                        player.getPackets().sendGameMessage("You can only use your familiar in a multi-zone area.");
                        return;
                    }
                    player.getFamiliar().setTarget(p2);
                }
            }
            case 950, 430, 193, 192 -> {
                Spell spell = Spellbook.getSpellById(player, componentId);
                if (spell == null)
                    return;
                player.setNextFaceEntity(p2);
                if (spell.getType() instanceof SpellType.Combat) {
                    if (!player.getControlerManager().canAttack(p2))
                        return;
                    if (!player.isCanPvp() || !p2.isCanPvp()) {
                        player.getPackets().sendGameMessage("You can only attack players in a player-vs-player area.");
                        return;
                    }
                    if (interfaceId != 192 && player.isAtMultiArea() && !p2.isAtMultiArea()) {
                        if (p2.getAttackedBy() != player && p2.isPjBlocked()) {
                            player.getPackets().sendGameMessage("That player is already in combat.");
                            return;
                        }
                    }
                }
                player.getTemporaryAttributtes().put("spell_target", p2);
                SpellHandler.castOnPlayer(player, spell.getId(), p2);
            }
        }
    }
    public static void handleInterfaceOnNpc(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player))
            return;
        if (player.isLocked())
            return;
        stream.readByte();
        int interfaceHash = stream.readInt();
        int npcIndex = stream.readUnsignedShortLE();
        int interfaceSlot = stream.readUnsignedShortLE128();
        stream.readUnsignedShortLE();
        int interfaceId = interfaceHash >> 16;
        int componentId = interfaceHash - (interfaceId << 16);
        if (Utils.getInterfaceDefinitionsSize() <= interfaceId)
            return;
        if (!player.getInterfaceManager().containsInterface(interfaceId))
            return;
        if (componentId == 65535) componentId = -1;
        if (componentId != -1 && Utils.getInterfaceDefinitionsComponentsSize(interfaceId) <= componentId)
            return;
        NPC npc = World.getNPCs().get(npcIndex);
        if (npc == null || npc.isDead() || npc.hasFinished()
                || !player.getMapRegionsIds().contains(npc.getRegionId()))
            return;
        player.stopAll(false);
        switch (interfaceId) {
            case Inventory.INVENTORY_INTERFACE -> {
                Item item = player.getInventory().getItem(interfaceSlot);
                if (item == null) return;
                InventoryOptionsHandler.handleItemOnNPC(player, npc, item, interfaceSlot);
            }
            case 662, 747 -> {
                if (player.getFamiliar() == null)
                    return;
                player.resetWalkSteps();
                boolean isEntitySpecial =
                        (interfaceId == 662 && componentId == 74) ||
                                (interfaceId == 747 && componentId == 18);
                if ((interfaceId == 747 && componentId == 15)
                        || (interfaceId == 662 && componentId == 65)
                        || isEntitySpecial
                        || (interfaceId == 747 && componentId == 24)) {
                    if (isEntitySpecial && player.getFamiliar().getSpecialAttack() != SpecialAttack.ENTITY)
                        return;
                    if (npc instanceof Familiar fam) {
                        if (fam == player.getFamiliar()) {
                            player.getPackets().sendGameMessage("You can't attack your own familiar.");
                            return;
                        }
                        if (!player.getFamiliar().canAttack(fam.getOwner())) {
                            player.getPackets().sendGameMessage("You can only attack players in a player-vs-player area.");
                            return;
                        }
                    }
                    if (!player.getFamiliar().canAttack(npc)) {
                        player.getPackets().sendGameMessage("You can only use your familiar in a multi-zone area.");
                        return;
                    }
                    player.getFamiliar().setTarget(npc);
                }
            }
            case 950, 193, 192, 430 -> {
                Spell spell = Spellbook.getSpellById(player, componentId);
                if (spell == null) return;
                player.setNextFaceEntity(npc);
                if (spell.getType() instanceof SpellType.Combat) {
                    if (!npc.getDefinitions().hasAttackOption() && !(npc instanceof Familiar))
                        return;
                    if (npc.getId() == 23921) {
                        player.getPackets().sendGameMessage("You can't use magic on a dummy.");
                        return;
                    }
                    if (npc instanceof Familiar fam) {
                        if (fam == player.getFamiliar()) {
                            player.getPackets().sendGameMessage("You can't attack your own familiar.");
                            return;
                        }
                        if (!fam.canAttack(player)) {
                            if (!fam.isAtMultiArea()) {
                                Player owner = fam.getOwner();
                                player.setNextFaceEntity(owner);
                                player.getTemporaryAttributtes().put("spell_target", owner);
                                SpellHandler.castOnPlayer(player, spell.getId(), owner);
                                return;
                            }
                            player.getPackets().sendGameMessage("You can't attack this npc.");
                            return;
                        }
                    } else if (!npc.isForceMultiAttacked()) {
                        if (player.isAtMultiArea() && !npc.isAtMultiArea()) {
                            if (npc.getAttackedBy() != player && npc.isPjBlocked()) {
                                player.getPackets().sendGameMessage("This npc is already in combat.");
                                return;
                            }
                        }
                        if (!npc.isAtMultiArea() && !player.isAtMultiArea()) {
                            if (player.getAttackedBy() != npc && player.isPjBlocked()) {
                                player.getPackets().sendGameMessage("You are already in combat.");
                                return;
                            }
                            if (npc.getAttackedBy() != player && npc.isPjBlocked()) {
                                player.getPackets().sendGameMessage("This npc is already in combat.");
                                return;
                            }
                        }
                    }
                    if (!player.getControlerManager().canAttack(npc))
                        return;
                }
                player.getTemporaryAttributtes().put("spell_target", npc);
                SpellHandler.castOnNpc(player, spell.getId(), npc);
            }
        }
    }
    public static void handleItemTake(Player player, InputStream stream) {
        if (!basicPlayerActiveAndLoaded(player))
            return;
        if (player.isLocked())
            return;
        int y = stream.readUnsignedShort();
        int x = stream.readUnsignedShortLE();
        final int itemId = stream.readUnsignedShort();
        boolean forceRun = stream.read128Byte() == 1;
        final WorldTile tile = new WorldTile(x, y, player.getPlane());
        final int regionId = tile.getRegionId();
        if (!player.getMapRegionsIds().contains(regionId))
            return;
        if (forceRun) player.setRun(true);
        player.stopAll(false);
        final FloorItem item = World.getRegion(regionId).getGroundItem(itemId, tile, player);
        player.setRouteEvent(new RouteEvent(item, () -> {
            if (item == null) {
                player.message("The item has disappeared.");
                return;
            }
            if (!player.getControlerManager().canTakeItem(item))
                return;
            if (item.hasOwner() && item.isInvisible() && !item.getOwner().getUsername().equalsIgnoreCase(player.getUsername())) {
                if (player.getPlayerRank().isIronman()) {
                    player.message("You are not able to pick up other players' items.");
                    return;
                }
            }
            player.setNextFaceWorldTile(tile);
            if (!player.getTile().matches(tile)) {
                player.animate(new Animation(832));
            }
            if (!player.isFrozen())
                player.addWalkSteps(tile.getX(), tile.getY(), 1);
            AutomaticGroundItem.pickup(tile, item);
            GroundItems.pickup(player, item);
        }));
    }
}
