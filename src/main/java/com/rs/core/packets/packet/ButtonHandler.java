package com.rs.core.packets.packet;

import java.util.*;

import com.rs.Settings;
import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.core.thread.CoresManager;
import com.rs.core.thread.WorldThread;
import com.rs.java.game.Animation;
import com.rs.java.game.Entity;
import com.rs.java.game.Graphics;
import com.rs.java.game.WorldTile;
import com.rs.java.game.item.Item;
import com.rs.java.game.item.meta.DragonFireShieldMetaData;
import com.rs.java.game.item.meta.GreaterRunicStaffMetaData;
import com.rs.java.game.item.meta.ItemMetadata;
import com.rs.java.game.item.meta.PolyporeStaffMetaData;
import com.rs.java.game.item.plugins.misc.RunePouch;
import com.rs.java.game.minigames.clanwars.FfaZone;
import com.rs.java.game.minigames.crucible.Crucible;
import com.rs.java.game.minigames.duel.DuelControler;
import com.rs.java.game.minigames.lividfarm.LividStore;
import com.rs.java.game.minigames.pest.CommendationExchange;
import com.rs.java.game.npc.familiar.Familiar;
import com.rs.java.game.player.CharacterDesign;
import com.rs.java.game.player.CombatDefinitions;
import com.rs.java.game.player.EmotesManager;
import com.rs.java.game.player.Equipment;
import com.rs.java.game.player.Inventory;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.Skills;
import com.rs.java.game.player.actions.FightPitsViewingOrb;
import com.rs.java.game.player.actions.HomeTeleport;
import com.rs.java.game.player.actions.Rest;
import com.rs.java.game.player.actions.WildyViewing;
import com.rs.java.game.player.actions.combat.*;
import com.rs.java.game.player.actions.combat.Poison.AntiDotes;
import com.rs.java.game.player.actions.skills.construction.House;
import com.rs.java.game.player.actions.skills.construction.Sawmill;
import com.rs.java.game.player.actions.skills.fletching.EnchantingBolts;
import com.rs.java.game.player.actions.skills.fletching.EnchantingBolts.Enchant;
import com.rs.java.game.player.actions.skills.runecrafting.Runecrafting;
import com.rs.java.game.player.actions.skills.smithing.DungeoneeringSmithing;
import com.rs.java.game.player.actions.skills.smithing.JewllerySmithing;
import com.rs.java.game.player.actions.skills.smithing.Smithing.ForgingInterface;
import com.rs.java.game.player.actions.skills.summoning.Summoning;
import com.rs.java.game.player.content.*;
import com.rs.java.game.player.content.GreaterRunicStaffManager.RunicStaffSpellStore;
import com.rs.java.game.player.content.Pots.Pot;
import com.rs.java.game.player.content.clans.ClansManager;
import com.rs.java.game.player.content.customtab.AchievementsTab;
import com.rs.java.game.player.content.customtab.GearTab;
import com.rs.java.game.player.content.customtab.JournalTab;
import com.rs.java.game.player.content.customtab.QuestTab;
import com.rs.java.game.player.content.customtab.SettingsTab;
import com.rs.java.game.player.content.customtab.TeleportTab;
import com.rs.java.game.player.content.grandexchange.GrandExchange;
import com.rs.java.game.player.dialogues.LevelUp;
import com.rs.java.game.player.dialogues.Transportation;
import com.rs.core.tasks.WorldTask;
import com.rs.core.tasks.WorldTasksManager;
import com.rs.core.packets.InputStream;
import com.rs.core.packets.decode.WorldPacketsDecoder;
import com.rs.java.utils.ItemBonuses;
import com.rs.java.utils.ItemExamines;
import com.rs.java.utils.Logger;
import com.rs.java.utils.ShopsHandler;
import com.rs.java.utils.Utils;
import com.rs.kotlin.Rscm;
import com.rs.kotlin.game.player.combat.magic.SpellHandler;
import com.rs.kotlin.game.player.combat.special.SpecialAttack;
import com.rs.kotlin.game.player.customtab.GearTabDSL;
import com.rs.kotlin.game.player.customtab.JournalTabDSL;
import com.rs.kotlin.game.player.customtab.SettingsTabDSL;
import com.rs.kotlin.game.player.customtab.TeleportTabDSL;
import com.rs.kotlin.game.player.equipment.BonusType;

/**
 * @Improved Andreas, Phillip - AvalonPK
 */

public class ButtonHandler {

    public static void handleButtons(final Player player, InputStream stream, final int packetId) {

        int interfaceHash = stream.readIntV2();

        int interfaceId = interfaceHash >>> 16;
        int componentId = interfaceHash & 0xFFFF;

        if (componentId == 65535)
            componentId = -1;

        if (interfaceId < 0 || interfaceId >= Utils.getInterfaceDefinitionsSize())
            return;

        if (componentId != -1 &&
                (componentId < 0 || componentId >= Utils.getInterfaceDefinitionsComponentsSize(interfaceId)))
            return;

        if (player.isDead())
            return;

        final int slotId2 = stream.readUnsignedShort128();
        final int slotId = stream.readUnsignedShortLE128();
        if (!player.getControlerManager().processButtonClick(interfaceId, componentId, slotId, slotId2, packetId))
            return;
        if (interfaceId == 403) Sawmill.handlePlanksConvertButtons(player, componentId, packetId);
        if (interfaceId == 190 || interfaceId == 1243) {
            player.getQuestManager().handleButton(interfaceId, componentId, slotId);
        }
        if (interfaceId == 1263 || interfaceId == 1081) {
            player.getDialogueManager().continueDialogue(interfaceId, componentId);
        }
        if (interfaceId == 813) {
            player.getCreationKiln().handleButton(componentId, packetId);
            return;
        }

        if (interfaceId == 1163 || interfaceId == 1164 || interfaceId == 1168 || interfaceId == 1170 || interfaceId == 1171 || interfaceId == 1173)
            player.getDominionTower().handleButtons(interfaceId, componentId, slotId, packetId);
        if (interfaceId == 3010) {
            player.getShopSystem().handleItemOption(slotId2, packetId);
        } else if (interfaceId == 548 || interfaceId == 746) {
            if (componentId == 75 || componentId == 99) {
                player.getTemporaryAttributtes().put("ACHIEVEMENTTAB", 0);
                player.getTemporaryAttributtes().remove("GEARTAB");
                Integer achievement = (Integer) player.getTemporaryAttributtes().get("ACHIEVEMENTTAB");
                if (achievement == 0) {
                    String category = (String) player.getTemporaryAttributtes().get("ACHIEVEMENTCATEGORY");
                    if (category != null) AchievementsTab.openTasks(player, category);
                    else AchievementsTab.open(player);
                }
            }
            if (componentId == 68) {
                player.getTemporaryAttributtes().put("GEARTAB", 0);
                GearTab.open(player, null);
            }
            if (componentId == 77 || componentId == 101) {
                player.getTemporaryAttributtes().remove("GEARTAB");
                player.getTemporaryAttributtes().remove("ACHIEVEMENTTAB");
                Integer tab = (Integer) player.temporaryAttribute().get("CUSTOMTAB");
                if (tab == null || tab == 0) {
                    JournalTab.open(player);
                } else if (tab == 1) {
                    TeleportTab.open(player);
                } else if (tab == 2) {
                    SettingsTab.open(player);
                } else if (tab == 3) {
                    GearTab.open(player, null);
                } else if (tab == 4) {
                    QuestTab.open(player);
                }
            }
            if ((interfaceId == 548 && componentId == 148) || (interfaceId == 746 && componentId == 199)) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
                    player.getHintIconsManager().removeAll();
                    player.getPackets().sendVar(1159, 0);
                    return;
                }
                if (player.getInterfaceManager().containsScreenInter() || player.getInterfaceManager().containsInventoryInter()) {
                    player.getPackets().sendGameMessage("Please finish what you're doing before opening the world map.");
                    return;
                }
                if (player.isInCombat()) {
                    player.getPackets().sendGameMessage("It wouldn't be wise to open World Map during combat.");
                    return;
                }
                player.animate(new Animation(840));
                player.getPackets().sendWindowsPane(755, 0);
                int posHash = player.getX() << 14 | player.getY();
                player.getPackets().sendGlobalVar(622, posHash);
                player.getPackets().sendGlobalVar(674, posHash);
            } else if (interfaceId == 629 && componentId == 68) {
                player.closeInterfaces();
            } else if ((interfaceId == 548 && componentId == 17) || (interfaceId == 746 && componentId == 54)) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) player.getSkills().switchXPDisplay();
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) player.getSkills().switchXPPopup(false);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) player.getSkills().setupXPCounter();
            } else if ((interfaceId == 746 && componentId == 207) || (interfaceId == 548 && componentId == 159) || (interfaceId == 548 && componentId == 194)) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    player.getPackets().sendRunScript(5557, 1);
                    player.refreshMoneyPouch();
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
                    if (player.getInterfaceManager().containsScreenInter()) {
                        player.getPackets().sendGameMessage("Please first finish with what you are currently doing.");
                        return;
                    }
                    player.temporaryAttribute().put("money_pouch_remove", Boolean.TRUE);
                    player.getPackets().sendRunScript(108, new Object[]{"                          Your money pouch contains " + Utils.getFormattedNumber(player.getMoneyPouch().getTotal(), ',') + " coins." + "                           How many would you like to withdraw?"});
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
                    player.getMoneyPouch().sendExamine();
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
                    if (player.getInterfaceManager().containsScreenInter()) {
                        player.getPackets().sendGameMessage("Please finish with what you're doing before opening the price checker.");
                        return;
                    }
                    if (player.isLocked()) {
                        player.getPackets().sendGameMessage("You can't open this while perfoming an action.");
                        return;
                    }
                    player.stopAll();
                    player.getPriceCheckManager().openPriceCheck();
                }
            }
        } else if (interfaceId == 1083) {
            LividStore.handleButtons(player, componentId);
        } else if (interfaceId == 3003) {
            Integer achievement = (Integer) player.getTemporaryAttributtes().get("ACHIEVEMENTTAB");
            if (achievement != null) {
                AchievementsTab.handleButtons(player, componentId);
                return;
            }
        } else if (interfaceId == 3002) {
            Integer tab = (Integer) player.temporaryAttribute().get("CUSTOMTAB");
            Integer gear = (Integer) player.getTemporaryAttributtes().get("GEARTAB");
            if (gear != null) {
                String otherPreset = (String) player.getTemporaryAttributtes().get("OTHERPRESET_NAME");
                GearTab.handleButtons(player, otherPreset, componentId);
                return;
            } else {
                if (componentId == 62) JournalTab.open(player);
                if (componentId == 61) TeleportTab.open(player);
                if (componentId == 60) SettingsTab.open(player);
                if (componentId == 26) GearTab.open(player, null);
                if (componentId == 59) QuestTab.open(player);
                if (tab == null) {
                    JournalTab.open(player);
                }
                if (tab != null) {
                    if (tab == 0) {
                        JournalTab.handleButtons(player, componentId);
                        return;
                    } else if (tab == 1) {
                        TeleportTab.handleButtons(player, componentId);
                        return;
                    } else if (tab == 2) {
                        SettingsTab.handleButtons(player, componentId);
                        return;
                    } else if (tab == 3) {
                        String otherPreset = (String) player.getTemporaryAttributtes().get("OTHERPRESET_NAME");
                        GearTab.handleButtons(player, otherPreset, componentId);
                        return;
                    } else if (tab == 4) {
                        QuestTab.handleButtons(player, componentId);
                        return;
                    }
                }
            }
        } else if (interfaceId == 939) {
            if (componentId == 112) player.getDungManager().closePartyInterface();
            else if (componentId >= 59 && componentId <= 72) {
                int playerIndex = (componentId - 59) / 3;
                if ((componentId & 0x3) != 0)
                    player.getDungManager().pressOption(playerIndex, packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET ? 0 : packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET ? 1 : 2);
                else player.getDungManager().pressOption(playerIndex, 3);
            } else if (componentId == 45) player.getDungManager().formParty();
            else if (componentId == 33 || componentId == 36) player.getDungManager().checkLeaveParty();
            else if (componentId == 43) player.getDungManager().invite();
            else if (componentId == 102) player.getDungManager().changeComplexity();
            else if (componentId == 108) player.getDungManager().changeFloor();
            else if (componentId == 87) player.getDungManager().openResetProgress();
            else if (componentId == 94) player.getDungManager().switchGuideMode();
        } else if (interfaceId == 949) {
            if (componentId == 65) player.getDungManager().acceptInvite();
            else if (componentId == 61 || componentId == 63) player.closeInterfaces();
        } else if (interfaceId == 938) {
            if (componentId >= 56 && componentId <= 81)
                player.getDungManager().selectComplexity((componentId - 56) / 5 + 1);
            else if (componentId == 39) player.getDungManager().confirmComplexity();
        } else if (interfaceId == 947) {
            if (componentId >= 48 && componentId <= 107) player.getDungManager().selectFloor((componentId - 48) + 1);
            else if (componentId == 766) player.getDungManager().confirmFloor();
        } else if (interfaceId == 364 && componentId == 4) {
            player.getPackets().sendGameMessage(ItemExamines.getExamine(new Item(slotId2)));
        } else if (interfaceId == 1253 || interfaceId == 1252 || interfaceId == 1139) {
            player.getSquealOfFortune().processClick(packetId, interfaceId, componentId, slotId, slotId2);
        } else if (interfaceId == 522) {
            if (componentId == 6) {
                player.getInterfaceManager().sendTabInterfaces(false);
                return;
            }
        } else if (interfaceId == 398) {
            if (componentId == 19) player.getInterfaceManager().sendSettings();
            else if (componentId == 15 || componentId == 1) {
                player.getHouse().setBuildMode(componentId == 15);
            } else if (componentId == 25 || componentId == 26) player.getHouse().setArriveInPortal(componentId == 25);
            else if (componentId == 27) player.getHouse().expelGuests();
            else if (componentId == 29) House.leaveHouse(player);
        } else if (interfaceId == 402) {
            if (componentId >= 93 && componentId <= 115) player.getHouse().createRoom(componentId - 93);
        } else if (interfaceId == 394 || interfaceId == 396) {
            if (componentId == 11) player.getHouse().build(slotId);
        } else if (interfaceId == 432) {
            Enchant enchant = EnchantingBolts.isEnchanting(new Item(getEnchantId(componentId)), new Item(getEnchantId2(componentId)));
            if (enchant != null) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    int invQuantity = player.getInventory().getItems().getNumberOf(getEnchantId(componentId));
                    int quantity = 1;
                    if (quantity > invQuantity) quantity = invQuantity;
                    player.getActionManager().setAction(new EnchantingBolts(enchant, quantity));
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
                    int invQuantity = player.getInventory().getItems().getNumberOf(getEnchantId(componentId));
                    int quantity = 5;
                    if (quantity > invQuantity) quantity = invQuantity;
                    player.getActionManager().setAction(new EnchantingBolts(enchant, quantity));
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
                    int invQuantity = player.getInventory().getItems().getNumberOf(getEnchantId(componentId));
                    int quantity = 10;
                    if (quantity > invQuantity) quantity = invQuantity;
                    player.getActionManager().setAction(new EnchantingBolts(enchant, quantity));
                }
            }
        } else if (interfaceId == 3049) {
            switch (componentId) {
                case RunePouch.INVENTORY_CONTAINER:
                    Item item = player.getInventory().getItem(slotId);
                    if (item == null) return;
                    switch (packetId) {
                        case 55:
                            RunePouch.storeRunePouch(player, item, 16000);
                            break;
                        case 5:
                            RunePouch.storeRunePouch(player, item, 100);
                            break;
                        case 67:
                            RunePouch.storeRunePouch(player, item, 10);
                            break;
                        case 14:
                            RunePouch.storeRunePouch(player, item, 1);
                            break;
                    }
                    break;
                case RunePouch.RUNE_CONTAINER:
                    switch (packetId) {
                        case 55:
                            RunePouch.withdrawRunePouch(player, slotId, 16000);
                            break;
                        case 5:
                            RunePouch.withdrawRunePouch(player, slotId, 100);
                            break;
                        case 67:
                            RunePouch.withdrawRunePouch(player, slotId, 10);
                            break;
                        case 14:
                            RunePouch.withdrawRunePouch(player, slotId, 1);
                            break;
                    }
                    break;
                case RunePouch.TAKE_ALL_COMPONENT:
                    if (packetId == 14) {
                        int runePouchSlot = (int) player.getTemporaryAttributtes().get("rune_pouch_slot");
                        RunePouch.withdrawAll(player, runePouchSlot);
                    }
                    break;
            }
        } else if (interfaceId == 1284) {
            if (player.getTemporaryAttributtes().get("untradeables") != null) {
                Item item = player.getUntradeables().get(slotId);
                if (item == null && componentId == 7) return;
                switch (componentId) {
                    case 7:
                        if (player.getInventory().getFreeSlots() == 0 && !player.getInventory().containsItem(player.getUntradeables().get(slotId).getId(), 1)) {
                            player.getPackets().sendGameMessage("You don't have enough inventory space.");
                            return;
                        }
                        switch (packetId) {
                            case 67:
                                player.getDialogueManager().startDialogue("ClaimUntradeablesD", item.getId(), slotId, true);
                                break;
                            case 14:
                                player.getDialogueManager().startDialogue("ClaimUntradeablesD", item.getId(), slotId, false);
                                break;
                            case 5:
                                player.getInventory().sendExamine(slotId);
                                break;
                        }
                        break;
                }
            }
        } else if (interfaceId == 34) {// notes interface
            switch (componentId) {
                case 35:
                case 37:
                case 39:
                case 41:
                    player.getNotes().colour((componentId - 35) / 2);
                    player.getPackets().sendHideIComponent(34, 16, true);
                    break;
                case 3:
                    player.getPackets().sendInputLongTextScript("Add note:");
                    player.temporaryAttribute().put("entering_note", Boolean.TRUE);
                    break;
                case 9:
                    switch (packetId) {
                        case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
                            if (player.getNotes().getCurrentNote() == slotId) player.getNotes().removeCurrentNote();
                            else player.getNotes().setCurrentNote(slotId);
                            break;
                        case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
                            player.getPackets().sendInputLongTextScript("Edit note:");
                            player.getNotes().setCurrentNote(slotId);
                            player.temporaryAttribute().put("editing_note", Boolean.TRUE);
                            break;
                        case WorldPacketsDecoder.ACTION_BUTTON3_PACKET:
                            player.getNotes().setCurrentNote(slotId);
                            player.getPackets().sendHideIComponent(34, 16, false);
                            break;
                        case WorldPacketsDecoder.ACTION_BUTTON4_PACKET:
                            player.getNotes().delete(slotId);
                            break;
                    }
                    break;
                case 8:
                case 11:
                    switch (packetId) {
                        case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
                            player.getNotes().delete(player.getNotes().getCurrentNote());
                            break;
                        case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
                            player.getNotes().deleteAll();
                            break;
                    }
                    break;
            }
        } else if (interfaceId == 164 || interfaceId == 161 || interfaceId == 378) {
            player.getSlayerManager().handleRewardButtons(interfaceId, componentId);
        } else if (interfaceId == 523) {
            if (componentId == 105) {
                player.getInterfaceManager().sendQuestTab();
            }
            player.closeInterfaces();
        } else if (interfaceId == 1011) {
            CommendationExchange.handleButtonOptions(player, componentId);
        } else if (interfaceId == 1309) {
            if (componentId == 20)
                player.getPackets().sendGameMessage("Use your enchanted stone ring onto the player that you would like to invite.", true);
            else if (componentId == 22) {
                Player p2 = player.getSlayerManager().getSocialPlayer();
                if (p2 == null)
                    player.getPackets().sendGameMessage("You have no slayer group, invite a player to start one.");
                else
                    player.getPackets().sendGameMessage("Your current slayer group consists of you and " + p2.getDisplayName() + ".");
            } else if (componentId == 24) player.getSlayerManager().resetSocialGroup(true);
            player.closeInterfaces();
        } else if (interfaceId == 675) {
            JewllerySmithing.handleButtonClick(player, componentId, packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET ? 1 : packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET ? 5 : 10);
        } else if (interfaceId == 1008) {
            if (componentId == 29) {
                player.setNextWorldTile(new WorldTile(2336, 3689, 0));
                player.getControlerManager().startControler("WildernessControler");
                player.resetReceivedDamage();
                player.getInterfaceManager().closeScreenInterface();
                return;
            }
            if (componentId == 28) {
                player.closeInterfaces();
                return;
            }
            if (componentId == 30) {
                player.getActionManager().setAction(new WildyViewing());
                player.closeInterfaces();
                return;
            }
        } else if (interfaceId == 182) {
            if (player.getInterfaceManager().containsInventoryInter()) return;
            if (componentId == 13) if (!player.hasFinished()) {
                player.logout(componentId == 6);
            }
        } else if (interfaceId == 1165) {
            // if (componentId == 22)
            // Summoning.closeDreadnipInterface(player);
        } else if (interfaceId == 880) {
            if (componentId >= 7 && componentId <= 19) Familiar.setLeftclickOption(player, (componentId - 7) / 2);
            else if (componentId == 21) Familiar.confirmLeftOption(player);
            else if (componentId == 25) Familiar.setLeftclickOption(player, 7);
        } else if (interfaceId == 662) {
            if (player.getFamiliar() == null) {
                if (player.getPet() == null) {
                    return;
                }
                if (componentId == 49) player.getPet().call();
                else if (componentId == 51) player.getDialogueManager().startDialogue("DismissD");
                return;
            }
            if (componentId == 49) player.getFamiliar().call();
            else if (componentId == 51) player.getDialogueManager().startDialogue("DismissD");
            else if (componentId == 67) player.getFamiliar().takeBob();
            else if (componentId == 69) player.getFamiliar().renewFamiliar();
            else if (componentId == 74) {
                if (player.getFamiliar().getSpecialAttack() == Familiar.SpecialAttack.CLICK) {
                    if (player.getFamiliar().isOneclickAttack()) {
                        int scrollId = Summoning.getScrollId(player.getFamiliar().getPouch().getRealPouchId());
                        if (!player.getInventory().containsItem(scrollId, 1)) {
                            player.getPackets().sendGameMessage("You don't have the scrolls to use this move.");
                            player.temporaryAttribute().remove("FamiliarSpec");
                            return;
                        }
                        player.getFamiliar().submitSpecial(player);
                        player.getFamiliar().setSpecial(true);
                    } else {
                        player.getFamiliar().setSpecial(true);
                        if (player.getFamiliar().hasSpecialOn()) player.getFamiliar().submitSpecial(player);
                    }
                }
            }
        } else if (interfaceId == 747) {
            if (componentId == 8) {
                Familiar.selectLeftOption(player);
            } else if (player.getPet() != null) {
                if (componentId == 11 || componentId == 20) {
                    player.getPet().call();
                } else if (componentId == 12 || componentId == 21) {
                    player.getDialogueManager().startDialogue("DismissD");
                } else if (componentId == 10 || componentId == 19) {
                    player.getPet().sendFollowerDetails();
                }
            } else if (player.getFamiliar() != null) {
                if (componentId == 11 || componentId == 20) player.getFamiliar().call();
                else if (componentId == 12 || componentId == 21) player.getDialogueManager().startDialogue("DismissD");
                else if (componentId == 13 || componentId == 22) player.getFamiliar().takeBob();
                else if (componentId == 14 || componentId == 23) player.getFamiliar().renewFamiliar();
                else if (componentId == 19 || componentId == 10) player.getFamiliar().sendFollowerDetails();
                else if (componentId == 18) {
                    if (player.getFamiliar().getSpecialAttack() == Familiar.SpecialAttack.CLICK) {
                        if (player.getFamiliar().isOneclickAttack()) {
                            int scrollId = Summoning.getScrollId(player.getFamiliar().getPouch().getRealPouchId());
                            if (!player.getInventory().containsItem(scrollId, 1)) {
                                player.getPackets().sendGameMessage("You don't have the scrolls to use this move.");
                                player.temporaryAttribute().remove("FamiliarSpec");
                                return;
                            }
                            player.getFamiliar().submitSpecial(player);
                            player.getFamiliar().setSpecial(true);
                        } else {
                            player.getFamiliar().setSpecial(true);
                            if (player.getFamiliar().hasSpecialOn()) player.getFamiliar().submitSpecial(player);
                        }
                    }
                }
            }
        } else if (interfaceId == 309) PlayerLook.handleHairdresserSalonButtons(player, componentId, slotId);
        else if (interfaceId == 363) {
            if (componentId == 4) player.getTreasureTrailsManager().movePuzzlePeice(slotId);
        } else if (interfaceId == 729) PlayerLook.handleThessaliasMakeOverButtons(player, componentId, slotId);
        else if (interfaceId == 728) {
            PlayerLook.handleYrsaShoes(player, componentId, slotId);
        } else if (interfaceId == 187) {
            if (componentId == 1) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
                    player.getMusicsManager().playAnotherMusic(slotId / 2);
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
                    player.getMusicsManager().sendHint(slotId / 2);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    player.getMusicsManager().addToPlayList(slotId / 2);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
                    player.getMusicsManager().removeFromPlayList(slotId / 2);
            } else if (componentId == 4) player.getMusicsManager().addPlayingMusicToPlayList();
            else if (componentId == 10) player.getMusicsManager().switchPlayListOn();
            else if (componentId == 11) player.getMusicsManager().clearPlayList();
            else if (componentId == 13) player.getMusicsManager().switchShuffleOn();
        } else if (interfaceId == 275) {
            if (componentId == 14) {
                player.getPackets().sendOpenURL(Settings.WEBSITE_LINK);
            }
        } else if ((interfaceId == 590 && componentId == 8) || interfaceId == 464) {
            player.getEmotesManager().useBookEmote(interfaceId == 464 ? componentId : EmotesManager.getId(slotId, packetId));
        } else if (interfaceId == 192) {
            if (componentId == 2) player.getCombatDefinitions().switchDefensiveCasting();
            else if (componentId == 7) player.getCombatDefinitions().switchShowCombatSpells();
            else if (componentId == 9) player.getCombatDefinitions().switchShowTeleportSkillSpells();
            else if (componentId == 11) player.getCombatDefinitions().switchShowMiscallaneousSpells();
            else if (componentId == 13) player.getCombatDefinitions().switchShowSkillSpells();
            else if (componentId >= 15 & componentId <= 17)
                player.getCombatDefinitions().setSortSpellBook(componentId - 15);
            else {
                SpellHandler.selectCombatSpell(player, componentId);
            }
        } else if (interfaceId == 1276) {
            if (componentId == 145) player.getInterfaceManager().closeInventoryInterface();
            else player.getRunicStaff().processSpell(player, componentId, packetId);
        } else if (interfaceId == 334) {
            if (componentId == 22) player.closeInterfaces();
            else if (componentId == 21) {
                player.getTrade().accept(false);
            }
        } else if (interfaceId == 335) {
            if (componentId == 18) player.getTrade().accept(true);
            else if (componentId == 53) {
                player.temporaryAttribute().put("trade_moneypouch_X_Slot", slotId);
                player.getPackets().sendRunScript(108, new Object[]{"Enter Amount:"});
            } else if (componentId == 20) player.closeInterfaces();
            else if (componentId == 32) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) player.getTrade().removeItem(slotId, 1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
                    player.getTrade().removeItem(slotId, 5);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    player.getTrade().removeItem(slotId, 10);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
                    player.getTrade().removeItem(slotId, Integer.MAX_VALUE);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
                    player.temporaryAttribute().put("trade_removeitem_X_Slot", slotId);
                    player.getPackets().sendRunScript(108, new Object[]{"Enter Amount:"});
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
                    player.getTrade().sendValue(slotId, false);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
                    player.getTrade().sendExamine(slotId, false);
            } else if (componentId == 35) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
                    player.getTrade().sendValue(slotId, true);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
                    player.getTrade().sendExamine(slotId, true);
            }
        } else if (interfaceId == 336) {
            if (componentId == 0) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) player.getTrade().addItem(slotId, 1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
                    player.getTrade().addItem(slotId, 5);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    player.getTrade().addItem(slotId, 10);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
                    player.getTrade().addItem(slotId, Integer.MAX_VALUE);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
                    player.temporaryAttribute().put("trade_item_X_Slot", slotId);
                    player.getPackets().sendRunScript(108, new Object[]{"Enter Amount:"});
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
                    player.getTrade().sendValue(slotId);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
                    player.getInventory().sendExamine(slotId);
            }
        } else if (interfaceId == 300) {
            ForgingInterface.handleIComponents(player, componentId);
        } else if (interfaceId == 934) {
            for (int index = 0; index < DungeoneeringSmithing.COMPONENTS[1].length; index++) {
                if (componentId == (index == 0 ? 22 : index == 1 ? 23 : (14 + index * 5))) {
                    int cycles = packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET ? 1 : packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET ? 5 : packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET ? -1 : 28;
                    if (cycles == -1) {
                        player.getPackets().sendInputIntegerScript(true, "How many would you like to make: ");
                        player.getTemporaryAttributtes().put("FORGE_X", index + 100);
                    } else {
                        player.closeInterfaces();
                        player.getActionManager().setAction(new DungeoneeringSmithing(index, cycles, true));
                    }
                }
            }
        } else if (interfaceId == 206) {
            if (componentId == 15) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
                    player.getPriceCheckManager().removeItem(slotId, 1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
                    player.getPriceCheckManager().removeItem(slotId, 5);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    player.getPriceCheckManager().removeItem(slotId, 10);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
                    player.getPriceCheckManager().removeItem(slotId, Integer.MAX_VALUE);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
                    player.temporaryAttribute().put("pc_item_X_Slot", slotId);
                    player.temporaryAttribute().put("pc_isRemove", Boolean.TRUE);
                    player.getPackets().sendRunScript(108, new Object[]{"Enter Amount:"});
                }
            }
        } else if (interfaceId == 672) {
            if (componentId == 16) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
                    Summoning.handlePouchInfusion(player, slotId, 1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
                    Summoning.handlePouchInfusion(player, slotId, 5);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    Summoning.handlePouchInfusion(player, slotId, 10);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
                    player.temporaryAttribute().put("infuse_pouch_x", slotId);
                    player.getPackets().sendRunScript(108, new Object[]{"Enter Amount:"});
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
                    Summoning.handlePouchInfusion(player, slotId, 28);// x
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET) {
                    Summoning.sendItemList(player, (boolean) player.temporaryAttribute().get("infusing_scroll"), 1, slotId);
                }
            } else if (componentId == 19) {
                Summoning.switchInfusionOption(player);
            }
        } else if (interfaceId == 666) {
            if (componentId == 16) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
                    Summoning.handlePouchInfusion(player, slotId, 1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
                    Summoning.handlePouchInfusion(player, slotId, 5);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    Summoning.handlePouchInfusion(player, slotId, 10);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
                    player.temporaryAttribute().put("infuse_scroll_x", slotId);
                    player.getPackets().sendRunScript(108, new Object[]{"Enter Amount:"});
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
                    Summoning.handlePouchInfusion(player, slotId, 28);// x
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET) {
                    // player.getPackets().sendGameMessage("You currently need "
                    // +
                    // ItemDefinitions.getItemDefinitions(slotId2).getCreateItemRequirements());
                }
            } else if (componentId == 18) Summoning.switchInfusionOption(player);
        } else if (interfaceId == 207) {
            if (componentId == 0) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
                    player.getPriceCheckManager().addItem(slotId, 1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
                    player.getPriceCheckManager().addItem(slotId, 5);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    player.getPriceCheckManager().addItem(slotId, 10);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
                    player.getPriceCheckManager().addItem(slotId, Integer.MAX_VALUE);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
                    player.temporaryAttribute().put("pc_item_X_Slot", slotId);
                    player.temporaryAttribute().remove("pc_isRemove");
                    player.getPackets().sendRunScript(108, new Object[]{"Enter Amount:"});
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
                    player.getInventory().sendExamine(slotId);
            }
        } else if (interfaceId == 665) {
            if (player.getFamiliar() == null || player.getFamiliar().getBob() == null) return;
            if (componentId == 0) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
                    player.getFamiliar().getBob().addItem(slotId, 1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
                    player.getFamiliar().getBob().addItem(slotId, 5);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    player.getFamiliar().getBob().addItem(slotId, 10);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
                    player.getFamiliar().getBob().addItem(slotId, Integer.MAX_VALUE);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
                    player.temporaryAttribute().put("bob_item_X_Slot", slotId);
                    player.temporaryAttribute().remove("bob_isRemove");
                    player.getPackets().sendRunScript(108, new Object[]{"Enter Amount:"});
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
                    player.getInventory().sendExamine(slotId);
            }
        } else if (interfaceId == 671) {
            if (player.getFamiliar() == null || player.getFamiliar().getBob() == null) return;
            if (componentId == 27) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
                    player.getFamiliar().getBob().removeItem(slotId, 1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
                    player.getFamiliar().getBob().removeItem(slotId, 5);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    player.getFamiliar().getBob().removeItem(slotId, 10);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
                    player.getFamiliar().getBob().removeItem(slotId, Integer.MAX_VALUE);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
                    player.temporaryAttribute().put("bob_item_X_Slot", slotId);
                    player.temporaryAttribute().put("bob_isRemove", Boolean.TRUE);
                    player.getPackets().sendRunScript(108, new Object[]{"Enter Amount:"});
                }
            } else if (componentId == 29) player.getFamiliar().takeBob();
        } else if (interfaceId == 916) {
            SkillsDialogue.handleSetQuantityButtons(player, componentId);
        } else if (interfaceId == 193) {
            if (componentId == 5) player.getCombatDefinitions().switchShowCombatSpells();
            else if (componentId == 7) player.getCombatDefinitions().switchShowTeleportSkillSpells();
            else if (componentId >= 9 && componentId <= 11)
                player.getCombatDefinitions().setSortSpellBook(componentId - 9);
            else if (componentId == 18) player.getCombatDefinitions().switchDefensiveCasting();
            else SpellHandler.selectCombatSpell(player, componentId);
        } else if (interfaceId == 645) {
            if (componentId == 16) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) ItemSets.sendComponents(player, slotId2);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) ItemSets.exchangeSet(player, slotId2);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) ItemSets.examineSet(player, slotId2);
            }
        } else if (interfaceId == 644) {
            if (componentId == 0) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
                    ItemSets.sendComponentsBySlot(player, slotId, slotId2);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
                    ItemSets.exchangeSet(player, slotId, slotId2);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    player.getInventory().sendExamine(slotId);
            }
        } else if (interfaceId == 430) {
            if (componentId == 5) player.getCombatDefinitions().switchShowCombatSpells();
            else if (componentId == 7) player.getCombatDefinitions().switchShowTeleportSkillSpells();
            else if (componentId == 9) player.getCombatDefinitions().switchShowMiscallaneousSpells();
            else if (componentId >= 11 & componentId <= 13)
                player.getCombatDefinitions().setSortSpellBook(componentId - 11);
            else if (componentId == 20) player.getCombatDefinitions().switchDefensiveCasting();
            else LunarMagicks.hasRequirement(player, componentId);
        } else if (interfaceId == 982) {
            if (componentId == 5) player.getInterfaceManager().sendSettings();
            else if (componentId == 41) player.setPrivateChatSetup(player.getPrivateChatSetup() == 0 ? 1 : 0);
            else if (componentId >= 17 && componentId <= 36) player.setClanChatSetup(componentId - 17);
            else if (componentId >= 49 && componentId <= 66) player.setPrivateChatSetup(componentId - 48);
            else if (componentId >= 72 && componentId <= 91) player.setFriendChatSetup(componentId - 72);
            else if (componentId >= 97 && componentId <= 116) player.setGuestChatSetup(componentId - 97);
        } else if (interfaceId == 261) {
            if (player.getInterfaceManager().containsInventoryInter()) return;
            if (componentId == 6) {
                player.switchRCReport();
                return;
            }
            if (componentId == 22) {
                player.stopAll();
                player.getInterfaceManager().sendInterface(742);
            } else if (componentId == 12) player.switchAllowChatEffects();
            else if (componentId == 11) {
                player.switchProfanity();
                player.refreshProfanity();
            } else if (componentId == 15) player.switchAcceptAid();
            else if (componentId == 16)
                player.getInterfaceManager().sendTab(player.getInterfaceManager().isResizableScreen() ? 123 : 183, 398);
            else if (componentId == 13) { // chat setup
                player.refreshPrivateChatSetup();
                player.refreshOtherChatsSetup();
                player.getInterfaceManager().sendSettings(982);
            } else if (componentId == 14) player.switchMouseButtons();
            else if (componentId == 24) // audio options
                player.getInterfaceManager().sendSettings(429);
            else if (componentId == 26) {
                player.getInterfaceManager().openGameTab(3);
                SettingsTab.open(player);
            }
        } else if (interfaceId == 429) {
            if (componentId == 18) player.getInterfaceManager().sendSettings();
        } else if (interfaceId == 940) {
            DungShop.handleButtons(player, componentId, slotId);
            return;
        } else if (interfaceId == 52) {
            if (componentId >= 30 && componentId <= 34) {
                player.getTemporaryAttributtes().put("selected_canoe", componentId - 30);
                Canoes.createShapedCanoe(player);
            }
        } else if (interfaceId == 95) {
            if (componentId >= 23 && componentId <= 33) CarrierTravel.handleCharterOptions(player, componentId);
            return;
        } else if (interfaceId == 53) {
            int selectedArea = -1;
            if (componentId == 47) selectedArea = 0;
            else if (componentId == 48) selectedArea = 1;
            else if (componentId == 3) selectedArea = 2;
            else if (componentId == 6) selectedArea = 3;
            else if (componentId == 49) selectedArea = 4;
            if (selectedArea != -1) Canoes.deportCanoeStation(player, selectedArea);
            return;
        } else if (interfaceId == 982) {
            if (componentId == 5) player.getInterfaceManager().sendSettings();
            else if (componentId == 41) player.setPrivateChatSetup(player.getPrivateChatSetup() == 0 ? 1 : 0);
            else if (componentId >= 49 && componentId <= 66) player.setPrivateChatSetup(componentId - 48);
            else if (componentId >= 72 && componentId <= 91) player.setFriendChatSetup(componentId - 72);
        } else if (interfaceId == Rscm.lookup("interface.prayerbook")) {
            player.getPrayer().handlePrayerClick(componentId, slotId);
            /*WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    player.getPrayer().handlePrayerClick(componentId, slotId);
                }
            });*/
        } else if (interfaceId == 1213) {// level up orb
            player.stopAll();
            int lvlupSkill = -1;
            int skillMenu = -1;
            int skillId = -1;
            if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                switch (componentId) {
                    case 30: // Attack
                        skillId = Skills.ATTACK;
                        skillMenu = Skills.SkillData.ATTACK.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 31: // Strength
                        skillId = Skills.STRENGTH;
                        skillMenu = Skills.SkillData.STRENGTH.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 32: // Defence
                        skillId = Skills.DEFENCE;
                        skillMenu = Skills.SkillData.DEFENCE.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 33: // Ranged
                        skillId = Skills.RANGE;
                        skillMenu = Skills.SkillData.RANGE.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 34: // Prayer
                        skillId = Skills.PRAYER;
                        skillMenu = Skills.SkillData.PRAYER.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 35: // Magic
                        skillId = Skills.MAGIC;
                        skillMenu = Skills.SkillData.MAGIC.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 48: // Runecrafting
                        skillId = Skills.RUNECRAFTING;
                        skillMenu = Skills.SkillData.RUNECRAFTING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 36: // Hitpoints
                        skillId = Skills.HITPOINTS;
                        skillMenu = Skills.SkillData.HITPOINTS.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 37: // Agility
                        skillId = Skills.AGILITY;
                        skillMenu = Skills.SkillData.AGILITY.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 38: // Herblore
                        skillId = Skills.HERBLORE;
                        skillMenu = Skills.SkillData.HERBLORE.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 39: // Thieving
                        skillId = Skills.THIEVING;
                        skillMenu = Skills.SkillData.THIEVING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 40: // Crafting
                        skillId = Skills.CRAFTING;
                        skillMenu = Skills.SkillData.CRAFTING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 41: // Fletching
                        skillId = Skills.FLETCHING;
                        skillMenu = Skills.SkillData.FLETCHING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 49: // Slayer
                        skillId = Skills.SLAYER;
                        skillMenu = Skills.SkillData.SLAYER.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 51: // Hunter
                        skillId = Skills.HUNTER;
                        skillMenu = Skills.SkillData.HUNTER.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 52: // Construction
                        skillId = Skills.CONSTRUCTION;
                        skillMenu = Skills.SkillData.CONSTRUCTION.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 42: // Mining
                        skillId = Skills.MINING;
                        skillMenu = Skills.SkillData.MINING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 43: // Smithing
                        skillId = Skills.SMITHING;
                        skillMenu = Skills.SkillData.SMITHING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 44: // Fishing
                        skillId = Skills.FISHING;
                        skillMenu = Skills.SkillData.FISHING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 45: // Cooking
                        skillId = Skills.COOKING;
                        skillMenu = Skills.SkillData.COOKING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 46: // Firemaking
                        skillId = Skills.FIREMAKING;
                        skillMenu = Skills.SkillData.FIREMAKING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 47: // Woodcutting
                        skillId = Skills.WOODCUTTING;
                        skillMenu = Skills.SkillData.WOODCUTTING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 50: // Farming
                        skillId = Skills.FARMING;
                        skillMenu = Skills.SkillData.FARMING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 53: // Summoning
                        skillId = Skills.SUMMONING;
                        skillMenu = Skills.SkillData.SUMMONING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 54: // Dung
                        skillId = Skills.DUNGEONEERING;
                        skillMenu = Skills.SkillData.DUNGEONEERING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar(965, skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                }
                player.getPackets().sendHideIComponent(lvlupSkill != -1 ? 741 : 499, lvlupSkill != -1 ? 9 : 27, true);
                player.getInterfaceManager().sendInterface(lvlupSkill != -1 ? 741 : 499);
                if (lvlupSkill != -1) {
                    LevelUp.switchFlash(player, lvlupSkill, false);
                }
                if (skillMenu != -1) {
                    player.getTemporaryAttributtes().put("skillMenu", skillMenu);
                }
                int finalSkillId = skillId;
                player.setCloseInterfacesEvent(new Runnable() {

                    @Override
                    public void run() {
                        player.getTemporaryAttributtes().remove("MILESTONE");
                        player.getTemporaryAttributtes().remove("COMBATMILESTONE");
                        player.getTemporaryAttributtes().remove("SLAYERCOMBATMILESTONE");
                        player.getTemporaryAttributtes().remove("LEVELUP[" + finalSkillId + "]:GAINEDLEVELS");
                        player.getVarsManager().sendVarBit(4727, -1);
                        player.getVarsManager().sendVarBit(4728, -1);
                        player.getVarsManager().sendVarBit(4730, 0);
                        player.getVarsManager().sendVarBit(4731, 0);
                        player.getVarsManager().sendVarBit(5395, 0);
                    }
                });
            }
        } else if (interfaceId == 320) {
            player.stopAll();
            if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                int lvlupSkill = -1;
                int skillMenu = -1;
                int skillId = -1;
                switch (componentId) {
                    case 150: // Attack
                        skillId = Skills.ATTACK;
                        skillMenu = Skills.SkillData.ATTACK.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 9: // Strength
                        skillId = Skills.STRENGTH;
                        skillMenu = Skills.SkillData.STRENGTH.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 22: // Defence
                        skillId = Skills.DEFENCE;
                        skillMenu = Skills.SkillData.DEFENCE.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 40: // Ranged
                        skillId = Skills.RANGE;
                        skillMenu = Skills.SkillData.RANGE.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 58: // Prayer
                        skillId = Skills.PRAYER;
                        skillMenu = Skills.SkillData.PRAYER.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 71: // Magic
                        skillId = Skills.MAGIC;
                        skillMenu = Skills.SkillData.MAGIC.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 84: // Runecrafting
                        skillId = Skills.RUNECRAFTING;
                        skillMenu = Skills.SkillData.RUNECRAFTING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 145: // Hitpoints
                        skillId = Skills.HITPOINTS;
                        skillMenu = Skills.SkillData.HITPOINTS.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 15: // Agility
                        skillId = Skills.AGILITY;
                        skillMenu = Skills.SkillData.AGILITY.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 28: // Herblore
                        skillId = Skills.HERBLORE;
                        skillMenu = Skills.SkillData.HERBLORE.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 46: // Thieving
                        skillId = Skills.THIEVING;
                        skillMenu = Skills.SkillData.THIEVING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 64: // Crafting
                        skillId = Skills.CRAFTING;
                        skillMenu = Skills.SkillData.CRAFTING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 77: // Fletching
                        skillId = Skills.FLETCHING;
                        skillMenu = Skills.SkillData.FLETCHING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 90: // Slayer
                        skillId = Skills.SLAYER;
                        skillMenu = Skills.SkillData.SLAYER.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 108: // Hunter
                        skillId = Skills.HUNTER;
                        skillMenu = Skills.SkillData.HUNTER.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 102: // Construction
                        skillId = Skills.CONSTRUCTION;
                        skillMenu = Skills.SkillData.CONSTRUCTION.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 140: // Mining
                        skillId = Skills.MINING;
                        skillMenu = Skills.SkillData.MINING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 135: // Smithing
                        skillId = Skills.SMITHING;
                        skillMenu = Skills.SkillData.SMITHING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getPackets().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 34: // Fishing
                        skillId = Skills.FISHING;
                        skillMenu = Skills.SkillData.FISHING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 52: // Cooking
                        skillId = Skills.COOKING;
                        skillMenu = Skills.SkillData.COOKING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 130: // Firemaking
                        skillId = Skills.FIREMAKING;
                        skillMenu = Skills.SkillData.FIREMAKING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 125: // Woodcutting
                        skillId = Skills.WOODCUTTING;
                        skillMenu = Skills.SkillData.WOODCUTTING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 96: // Farming
                        skillId = Skills.FARMING;
                        skillMenu = Skills.SkillData.FARMING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 114: // Summoning
                        skillId = Skills.SUMMONING;
                        skillMenu = Skills.SkillData.SUMMONING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                    case 120: // Dung
                        skillId = Skills.DUNGEONEERING;
                        skillMenu = Skills.SkillData.DUNGEONEERING.getValue();
                        if (player.getTemporaryAttributtes().remove("leveledUp[" + skillId + "]") != Boolean.TRUE) {
                            player.getVarsManager().sendVar("var.skillmenu_type", skillMenu);
                        } else {
                            Skills.sendLevelConfigs(player, skillId);
                            lvlupSkill = skillId;
                        }
                        break;
                }
                player.getPackets().sendHideIComponent(lvlupSkill != -1 ? "interface.levelup" : "interface.skillmenu", lvlupSkill != -1 ? 9 : 27, true);
                player.getInterfaceManager().sendInterface(lvlupSkill != -1 ? "interface.levelup" : "interface.skillmenu");
                if (lvlupSkill != -1) {
                    LevelUp.switchFlash(player, lvlupSkill, false);
                }
                if (skillMenu != -1) {
                    player.getTemporaryAttributtes().put("skillMenu", skillMenu);
                }
                int finalSkillId = skillId;
                player.setCloseInterfacesEvent(new Runnable() {

                    @Override
                    public void run() {
                        player.getTemporaryAttributtes().remove("MILESTONE");
                        player.getTemporaryAttributtes().remove("COMBATMILESTONE");
                        player.getTemporaryAttributtes().remove("LEVELUP[" + finalSkillId + "]:GAINEDLEVELS");
                        player.getVarsManager().sendVarBit(4727, -1);
                        player.getVarsManager().sendVarBit(4728, -1);
                        player.getVarsManager().sendVarBit(4730, 0);
                        player.getVarsManager().sendVarBit(4731, 0);
                    }
                });
            } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET || packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
                int skillId = player.getSkills().getTargetIdByComponentId(componentId);
                boolean usingLevel = packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET;
                player.temporaryAttribute().put(usingLevel ? "setLevel" : "setXp", skillId);
                player.getPackets().sendInputIntegerScript(true, "Please enter " + (usingLevel ? "level" : "xp") + " you want to set as a target: ");

            } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
                int skillId = player.getSkills().getTargetIdByComponentId(componentId);
                player.getSkills().setSkillTargetEnabled(skillId, false);
                player.getSkills().setSkillTargetValue(skillId, 0);
                player.getSkills().setSkillTargetUsingLevelMode(skillId, false);
            }
        } else if (interfaceId == 1218) {
            if ((componentId >= 33 && componentId <= 55) || componentId == 120 || componentId == 151 || componentId == 189)
                player.getPackets().sendInterface(false, 1218, 1, 1217);
        } else if (interfaceId == 499) {
            int skillMenu = -1;
            if (player.temporaryAttribute().get("skillMenu") != null)
                skillMenu = (Integer) player.temporaryAttribute().get("skillMenu");
            if (componentId >= 9 && componentId <= 25)
                player.getPackets().sendVar(965, ((componentId - 9) * 1024) + skillMenu);
            else if (componentId == 29) player.stopAll();
        } else if (interfaceId == 387) {
            if (componentId == 6) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
                    int hatId = player.getEquipment().getHatId();
                    if (hatId == 20804 || hatId == 20805 || hatId == 20806) {
                        if ((Long) player.temporaryAttribute().get("LeetEmote") != null && (Long) player.temporaryAttribute().get("LeetEmote") + 8500 > Utils.currentTimeMillis())
                            return;
                        player.animate(new Animation(9098));
                        player.gfx(new Graphics(92));
                        player.temporaryAttribute().put("LeetEmote", Utils.currentTimeMillis());
                        return;
                    }
                    if (hatId == 20801) {
                        player.animate(new Animation(534));
                        return;
                    }
                    if (hatId == 20802) {
                        player.animate(new Animation(412));
                        player.gfx(new Graphics(121));
                        return;
                    }
                    if (hatId == 20803) {
                        player.animate(new Animation(361));
                        player.gfx(new Graphics(122));
                        return;
                    }
                    if (hatId == 10507) {
                        player.animate(new Animation(5059));
                        player.gfx(new Graphics(263));
                        return;
                    }
                    if (hatId == 24437 || hatId == 24439 || hatId == 24440 || hatId == 24441) {
                        player.getDialogueManager().startDialogue("FlamingSkull", player.getEquipment().getItem(Equipment.SLOT_HEAD), -1);
                        return;
                    } else if (componentId == 15) {
                        int weaponId = player.getEquipment().getWeaponId();
                        if (weaponId == 10501) {
                            player.sendDefaultPlayersOptions();
                            return;
                        }
                        if (weaponId == 15426 && slotId2 == 15426) {
                            player.animate(new Animation(12664));
                        }
                    }
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    ButtonHandler.registerUnEquip(player, Equipment.SLOT_HEAD);
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
                    player.getEquipment().sendExamine(Equipment.SLOT_HEAD);
            } else if (componentId == 9) {

                if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
                    int capeId = player.getEquipment().getCapeId();
                    if (capeId == 20769 || capeId == 20771) SkillCapeCustomizer.startCustomizing(player, capeId);

                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
                    int capeId = player.getEquipment().getCapeId();
                    if (capeId == 20769 || capeId == 20771) {
                        renewSummoningPoints(player);
                    }

                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
                    int capeId = player.getEquipment().getCapeId();
                    if (capeId == 20769 || capeId == 20771) {
                        player.message("Ardy Farm Teleport");
                    }

                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
                    int capeId = player.getEquipment().getCapeId();
                    if (capeId == 20767) SkillCapeCustomizer.startCustomizing(player, capeId);
                    if (capeId == 20769 || capeId == 20771) {// add wilderness
                        // block after
                        // level 20
                        Magic.sendJewerlyTeleportSpell(player, true, 9603, 1684, 4, new WorldTile(3054, 3511, 0));// edge
                        // monestary
                        // teleport
                    }

                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    registerUnEquip(player, Equipment.SLOT_CAPE);

                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET) {
                    player.getEquipment().sendExamine(Equipment.SLOT_CAPE);
                }
            } else if (componentId == 12) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
                    int amuletId = player.getEquipment().getAmuletId();
                    if (amuletId <= 1712 && amuletId >= 1706) {
                        if (Magic.sendJewerlyTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4, new WorldTile(3086, 3496, 0))) {
                            Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
                            if (amulet != null) {
                                amulet.setId(amulet.getId() - 2);
                                player.getEquipment().refresh(Equipment.SLOT_AMULET);
                            }
                        }
                    } else if (amuletId == 1704)
                        player.getPackets().sendGameMessage("The amulet has ran out of charges. You need to recharge it if you wish it use it once more.");
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
                    int amuletId = player.getEquipment().getAmuletId();
                    if (amuletId <= 1712 && amuletId >= 1706) {
                        if (Magic.sendJewerlyTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4, new WorldTile(3081, 3648, 0))) {
                            Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
                            if (amulet != null) {
                                amulet.setId(amulet.getId() - 2);
                                player.getEquipment().refresh(Equipment.SLOT_AMULET);
                            }
                        }
                    }
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
                    int amuletId = player.getEquipment().getAmuletId();
                    if (amuletId <= 1712 && amuletId >= 1706) {
                        if (Magic.sendJewerlyTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4, new WorldTile(3105, 3251, 0))) {
                            Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
                            if (amulet != null) {
                                amulet.setId(amulet.getId() - 2);
                                player.getEquipment().refresh(Equipment.SLOT_AMULET);
                            }
                        }
                    }
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
                    int amuletId = player.getEquipment().getAmuletId();
                    if (amuletId <= 1712 && amuletId >= 1706) {
                        if (Magic.sendJewerlyTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4, new WorldTile(3293, 3163, 0))) {
                            Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
                            if (amulet != null) {
                                amulet.setId(amulet.getId() - 2);
                                player.getEquipment().refresh(Equipment.SLOT_AMULET);
                            }
                        }
                    }
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    registerUnEquip(player, Equipment.SLOT_AMULET);
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
                    player.getEquipment().sendExamine(Equipment.SLOT_AMULET);
            } else if (componentId == 15) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
                    Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
                    if (weapon.isItem("item.polypore_staff")) {
                        player.message("Your polypore staff is fully charged.");
                        return;
                    }
                    if (weapon.isItem("item.polypore_staff_degraded")) {
                        if (weapon.getMetadata() == null) {
                            weapon.setMetadata(new PolyporeStaffMetaData(3000));
                        }
                        ItemMetadata data = weapon.getMetadata();
                        if (data instanceof PolyporeStaffMetaData) {
                            player.message("Your " + weapon.getName() + " currently has " + data.getValue() + " charges left.");
                            return;
                        }
                    }
                    int weaponId = player.getEquipment().getWeaponId();
                    if (weaponId == 24202 || weaponId == 24203) {
                        if (weapon.getMetadata() == null) {
                            weapon.setMetadata(new GreaterRunicStaffMetaData(-1, 0));
                        }
                        if (weapon.getMetadata() != null && weapon.getMetadata() instanceof GreaterRunicStaffMetaData) {
                            if (((GreaterRunicStaffMetaData) weapon.getMetadata()).getSpellId() == -1) {
                                player.message("Your greater runic staff doesn't have any spell selected");
                                return;
                            }
                            for (RunicStaffSpellStore s : RunicStaffSpellStore.values()) {
                                if (s.spellId != ((GreaterRunicStaffMetaData) weapon.getMetadata()).getSpellId()) {
                                    continue;
                                }
                                player.getPackets().sendGameMessage("You currently have " + ((GreaterRunicStaffMetaData) weapon.getMetadata()).getCharges() + " " + s.name().toLowerCase().replace('_', ' ') + " charges left.");
                            }
                            return;
                        }
                    }
                    if (weaponId == 24201) {
                        player.getRunicStaff().openChooseSpell(player, weapon);
                        return;
                    }
                    if (weaponId >= 18349 && weaponId <= 18357) {
                        player.getChargeManager().checkPercentage("Your " + ItemDefinitions.getItemDefinitions(weaponId).getName() + " has ##% charges left.", weapon, false);
                    }
                    if (weaponId == 15484) player.getInterfaceManager().gazeOrbOfOculus();
                    if (weaponId == 9013) {
                        if (player.getSkullSkeptreCharges() == 1) {
                            player.getEquipment().deleteItem(9013, 1);
                            player.getAppearence().generateAppearenceData();
                            player.setSkullSkeptreCharges(5);
                            player.getPackets().sendGameMessage("You have no more charges, the sceptre crumbled to dust.");
                            player.animate(new Animation(9601));
                            player.gfx(new Graphics(94));
                            WorldTasksManager.schedule(new WorldTask() {

                                @Override
                                public void run() {
                                    Magic.sendSkullSceptreTeleport(player, false, 4731, -1, 2, new WorldTile(3081, 3421, 0));
                                }
                            }, 2);
                            return;
                        }
                        player.setSkullSkeptreCharges(player.getSkullSkeptreCharges() - 1);
                        player.getPackets().sendGameMessage("You have " + player.getSkullSkeptreCharges() + " charges left.");
                        player.animate(new Animation(9601));
                        player.gfx(new Graphics(94));
                        WorldTasksManager.schedule(new WorldTask() {
                            @Override
                            public void run() {
                                Magic.sendSkullSceptreTeleport(player, false, 4731, -1, 2, new WorldTile(3081, 3421, 0));
                            }
                        }, 2);
                    }
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    registerUnEquip(player, Equipment.SLOT_WEAPON);
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
                    player.getEquipment().sendExamine(Equipment.SLOT_WEAPON);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
                    int weaponId = player.getEquipment().getWeaponId();
                    Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
                    if (weaponId == 24202 || weaponId == 24203) {
                        player.getTemporaryAttributtes().put("GREATER_RUNIC_STAFF", weapon);
                        player.getTemporaryAttributtes().put("INTERACT_STAFF_FROM_INVENTORY", false);
                        player.getDialogueManager().startDialogue("GreaterRunicStaffD");
                        return;
                    }
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
                    int weaponId = player.getEquipment().getWeaponId();
                    Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
                    if (weaponId == 24203) {
                        player.getTemporaryAttributtes().put("GREATER_RUNIC_STAFF", weapon);
                        player.getTemporaryAttributtes().put("INTERACT_STAFF_FROM_INVENTORY", false);
                        player.getRunicStaff().clearCharges(true, false);
                        return;
                    }
                    if (weaponId == 24202) {
                        player.getTemporaryAttributtes().put("GREATER_RUNIC_STAFF", weapon);
                        player.getTemporaryAttributtes().put("INTERACT_STAFF_FROM_INVENTORY", false);
                        player.getRunicStaff().clearSpell(true, false);
                    }
                }
            } else if (componentId == 18) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    registerUnEquip(player, Equipment.SLOT_CHEST);
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET) {
                    player.getEquipment().sendExamine(Equipment.SLOT_CHEST);
                }
            } else if (componentId == 21) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    registerUnEquip(player, Equipment.SLOT_SHIELD);
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
                    int shieldId = player.getEquipment().getShieldId();
                    if (shieldId >= 18359 && shieldId <= 18363) {
                        Item shield = player.getEquipment().getItem(Equipment.SLOT_SHIELD);
                        player.getChargeManager().checkPercentage(ItemDefinitions.getItemDefinitions(shieldId).getName() + " has ##% charges left.", shield, false);
                    }
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET) {
                    player.getEquipment().sendExamine(Equipment.SLOT_SHIELD);
                }
            } else if (componentId == 24) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    registerUnEquip(player, Equipment.SLOT_LEGS);
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET) {
                    player.getEquipment().sendExamine(Equipment.SLOT_LEGS);
                }
            } else if (componentId == 27) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    registerUnEquip(player, Equipment.SLOT_HANDS);
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET) {
                    player.getEquipment().sendExamine(Equipment.SLOT_HANDS);
                }
            } else if (componentId == 30) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    registerUnEquip(player, Equipment.SLOT_FEET);
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET) {
                    player.getEquipment().sendExamine(Equipment.SLOT_FEET);
                }
            } else if (componentId == 33) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
                    if (player.getEquipment().getRingId() == 2550) {
                        if (player.getRecoilCharges() <= 0 || player.getRecoilCharges() == 500) {
                            player.message("Your ring of recoil is fully charged.");
                            return;
                        }
                        player.message("Your " + ItemDefinitions.getItemDefinitions(2550).getName() + " has " + player.getRecoilCharges() + " left.");
                    }
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    registerUnEquip(player, Equipment.SLOT_RING);
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET) {
                    player.getEquipment().sendExamine(Equipment.SLOT_RING);
                }
            } else if (componentId == 36) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    registerUnEquip(player, Equipment.SLOT_ARROWS);
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET) {
                    player.getEquipment().sendExamine(Equipment.SLOT_ARROWS);
                }
            } else if (componentId == 45) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
                    registerUnEquip(player, Equipment.SLOT_AURA);
                    player.getAuraManager().removeAura();
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
                    player.getEquipment().sendExamine(Equipment.SLOT_AURA);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) player.getAuraManager().activate();
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    player.getAuraManager().sendAuraRemainingTime();
            } else if (componentId == 40) {
                player.stopAll();
                openItemsKeptOnDeath(player);
            } else if (componentId == 41) {
                player.getToolbelt().openToolbelt();
            } else if (componentId == 37) {
                openEquipmentBonuses(player, false);
            }
        } else if (interfaceId == 17) {
            if (componentId == 28) sendItemsKeptOnDeath(player, player.getVarsManager().getBitValue(9226) == 0);
        } else if (interfaceId == 449) {
            if (componentId == 1) {

                Shop shop = (Shop) player.temporaryAttribute().get("Shop");
                if (shop == null) return;
                shop.sendInventory(player);
            } else if (componentId == 21) {
                Shop shop = (Shop) player.temporaryAttribute().get("Shop");
                if (shop == null) return;
                Integer slot = (Integer) player.temporaryAttribute().get("ShopSelectedSlot");
                if (slot == null) return;
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) shop.buy(player, slot, 1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) shop.buy(player, slot, 5);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) shop.buy(player, slot, 10);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) shop.buy(player, slot, 50);

            }
        } else if (interfaceId == 621) {
            if (componentId == 0) {

                if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET) player.getInventory().sendExamine(slotId);
                else {
                    Shop shop = (Shop) player.temporaryAttribute().get("Shop");
                    if (shop == null) return;
                    if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) shop.sendValue(player, slotId);
                    else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) shop.sell(player, slotId, 1);
                    else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) shop.sell(player, slotId, 5);
                    else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) shop.sell(player, slotId, 10);
                    else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) shop.sell(player, slotId, 50);
                }
            }
        } else if (interfaceId == 640) {
            if (componentId == 18 || componentId == 22) {
                player.temporaryAttribute().put("WillDuelFriendly", true);
                player.getPackets().sendVar(283, 67108864);
            } else if (componentId == 19 || componentId == 21) {
                player.temporaryAttribute().put("WillDuelFriendly", false);
                player.getPackets().sendVar(283, 134217728);
            } else if (componentId == 20) {
                DuelControler.challenge(player);
            }
        } else if (interfaceId == 650) {
            if (componentId == 15) {
                player.stopAll();
                player.setNextWorldTile(new WorldTile(2974, 4384, player.getPlane()));
                player.getControlerManager().startControler("CorpBeastControler");
            } else if (componentId == 16) player.closeInterfaces();
        } else if (interfaceId == 667) {
            if (componentId == 14) {
                if (slotId >= 14) return;
                Item item = player.getEquipment().getItem(slotId);
                if (item == null) return;

                if (packetId == WorldPacketsDecoder.EQUIPMENT_EXAMINE_PACKET)
                    player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
                else if (packetId == WorldPacketsDecoder.EQUIPMENT_REMOVE_PACKET) {
                    registerUnEquip(player, slotId);
                    ButtonHandler.refreshEquipBonuses(player);
                }
            }
            if (componentId == 87) {
                player.stopAll();
            }
            if (componentId == 9) {
                if (slotId >= 14) return;
                Item item = player.getEquipment().getItem(slotId);
                if (item == null) return;
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON10_PACKET) {
                    sendItemStats(player, item);
                    return;
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    registerUnEquip(player, slotId);
                    player.getPackets().sendGlobalVar(779, player.getEquipment().getWeaponRenderEmote());
                    ButtonHandler.refreshEquipBonuses(player);
                }
            } else if (componentId == 46 && player.temporaryAttribute().remove("Banking") != null) {
                player.getBank().openBank();
            }
        } else if (interfaceId == 670) {
            if (componentId == 0 && packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
                return;
            }
            if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
                Item item = player.getInventory().getItem(slotId);
                if (item == null) return;
                sendItemStats(player, item);
                return;
            }
            if (player.getInterfaceManager().containsInterface(RunePouch.INTERFACEID)) {
                if (componentId == 0) {
                    if (slotId >= player.getInventory().getItemsContainerSize()) return;
                    Item item = player.getInventory().getItem(slotId);
                    if (item == null) return;
                    if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                        RunePouch.storeRunePouch(player, item, 1);
                        return;
                    } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
                        RunePouch.storeRunePouch(player, item, 10);
                        return;
                    } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
                        RunePouch.storeRunePouch(player, item, 100);
                        return;
                    } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
                        RunePouch.storeRunePouch(player, item, 16000);
                    return;
                }
            } else {
                if (componentId == 0) {
                    if (slotId >= player.getInventory().getItemsContainerSize()) return;
                    Item item = player.getInventory().getItem(slotId);
                    if (item == null) return;
                    if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                        long passedTime = Utils.currentTimeMillis() - WorldThread.getLastCycleTime();
                        WorldTasksManager.schedule(new WorldTask() {

                            @Override
                            public void run() {
                                List<Integer> slots = player.getSwitchItemCache();
                                int[] slot = new int[slots.size()];
                                for (int i = 0; i < slot.length; i++)
                                    slot[i] = slots.get(i);
                                player.getSwitchItemCache().clear();
                                ButtonHandler.sendWear(player, slot);
                            }
                        }, passedTime >= 300 ? 0 : passedTime > 150 ? 1 : 0);
                        if (player.getSwitchItemCache().contains(slotId)) return;
                        player.getSwitchItemCache().add(slotId);
                    } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
                        player.getInventory().sendExamine(slotId);
                }
            }
        } else if (interfaceId == Inventory.INVENTORY_INTERFACE) { // inventory
            if (componentId == 0) {
                if (slotId > 27) return;
                final Item item = player.getInventory().getItem(slotId);
                if (item == null || item.getId() != slotId2) return;
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    InventoryOptionsHandler.handleItemOption1(player, slotId, item, 1);//correct
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
                    InventoryOptionsHandler.handleItemOption2(player, slotId, item, 2);//correct
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    InventoryOptionsHandler.handleItemOption3(player, slotId, item, 3);//correct
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)//unknown
                    InventoryOptionsHandler.handleItemOption4(player, slotId, item, -1);//unknown
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)//unknown
                    InventoryOptionsHandler.handleItemOption5(player, slotId, item, -1);//unknown
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET)
                    InventoryOptionsHandler.handleItemOption6(player, slotId, item, 4);//correct
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON7_PACKET) {//drop packet
                    InventoryOptionsHandler.handleItemOption7(player, slotId, item, 5);//correct
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)//examine packet
                    InventoryOptionsHandler.handleItemOption8(player, slotId, item, -1);//unknown
            }
        } else if (interfaceId == 748) {// HP
            if (componentId == 2 && slotId == 65535) {
                for (AntiDotes antis : AntiDotes.values()) {
                    if (player.getInventory().containsItem(antis.getItemId(), 1)) {
                        int potion = antis.getItemId();
                        Pot pot = Pots.getPot(potion);
                        int slot = player.getInventory().getItems().getThisItemSlot(new Item(potion, 1));
                        Pots.pot(player, new Item(potion, 1), slot);
                        pot.effect.extra(player);
                        return;
                    }
                }
                player.getPackets().sendGameMessage("You don't have anything to cure your poison with.");
            }
        } else if (interfaceId == 742) {
            if (componentId == 46) // close
                player.stopAll();
        } else if (interfaceId == 743) {
            if (componentId == 20) // close
                player.stopAll();
        } else if (interfaceId == 741) {
            if (componentId == 9) // close
                player.stopAll();
        } else if (interfaceId == 749) {
            if (componentId == 4) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) { // activate
                    player.getPrayer().switchQuickPrayers();
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) // switch
                    player.getPrayer().switchQuickPrayerSettings();
            }
        } else if (interfaceId == 13 || interfaceId == 14 || interfaceId == 759) {
            // player.getBankPin().handleButtons(interfaceId, componentId);
        } else if (interfaceId == 750) {
            if (componentId == 4) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
                    if (player.isLocked()) {
                        player.getPackets().sendGameMessage("You can't toggle run while perfoming an action.");
                        player.sendRunButtonConfig();
                        return;
                    }
                    player.toogleRun(player.isResting() ? false : true);
                    if (player.isResting()) player.stopAll();
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
                    if (player.isResting()) {
                        player.stopAll();
                        return;
                    }
                    long currentTime = Utils.currentTimeMillis();
                    if (player.getEmotesManager().getNextEmoteEnd() >= currentTime) {
                        player.getPackets().sendGameMessage("You can't rest while perfoming an emote.");
                        return;
                    }
                    if (player.isLocked()) {
                        player.getPackets().sendGameMessage("You can't rest while perfoming an action.");
                        return;
                    }
                    player.stopAll();
                    player.getActionManager().setAction(new Rest());
                }
            }
        } else if (interfaceId == 11) {
            if (componentId == 17) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
                    player.getBank().depositItem(slotId, 1, true);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
                    player.getBank().depositItem(slotId, 5, true);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    player.getBank().depositItem(slotId, 10, true);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
                    player.getBank().depositItem(slotId, Integer.MAX_VALUE, true);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
                    player.temporaryAttribute().put("bank_item_X_Slot", slotId);
                    player.temporaryAttribute().remove("bank_isWithdraw");
                    player.getPackets().sendRunScript(108, new Object[]{"Enter Amount:"});
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
                    player.getInventory().sendExamine(slotId);
            } else if (componentId == 18) player.getBank().depositAllInventory(false);
            else if (componentId == 22) player.getBank().depositAllEquipment(false);
            else if (componentId == 24) player.getBank().depositAllBob(false);
            else if (componentId == 20) player.getBank().depositMoneyPouch(false);
        } else if (interfaceId == 762) {
            if (componentId == 15) player.getBank().switchInsertItems();
            else if (componentId == 19) {
                player.getPackets().sendVar(115, player.getBank().getWithdrawNotes() ? 1 : 0);
                player.getBank().switchWithdrawNotes();
            } else if (componentId == 33) player.getBank().depositAllInventory(true);
            else if (componentId == 37) player.getBank().depositAllEquipment(true);
            else if (componentId == 39) player.getBank().depositAllBob(true);
            else if (componentId == 35) player.getBank().depositMoneyPouch(true);
            else if (componentId == 46) {
                long moneyPouch = player.getMoneyPouch().getTotal();
                long bankValue = player.getBank().getBankValue();
                long inventoryValue = player.getInventory().getInventoryValue();
                long equipmentValue = player.getEquipment().getEquipmentValue();
                long totalValue = 0;
                long grandexchangeValue = GrandExchange.getTotalOfferValues(player);
                long collectionValue = GrandExchange.getTotalCollectionValue(player);
                player.closeInterfaces();
                player.getInterfaceManager().sendInterface(629);
                player.getPackets().sendTextOnComponent(629, 11, "Information Tab");
                player.getPackets().sendTextOnComponent(629, 12, "");
                player.getPackets().sendTextOnComponent(629, 41, "Money pouch:");
                player.getPackets().sendTextOnComponent(629, 54, Utils.formatDoubledAmount(moneyPouch));
                player.getPackets().sendTextOnComponent(629, 42, "Bank:");
                player.getPackets().sendTextOnComponent(629, 55, Utils.formatDoubledAmount(bankValue));
                player.getPackets().sendTextOnComponent(629, 43, "Inventory:");
                player.getPackets().sendTextOnComponent(629, 56, Utils.formatDoubledAmount(inventoryValue));
                player.getPackets().sendTextOnComponent(629, 44, "Equipment:");
                player.getPackets().sendTextOnComponent(629, 57, Utils.formatDoubledAmount(equipmentValue));
                player.getPackets().sendTextOnComponent(629, 45, "Grand Exchange");
                player.getPackets().sendTextOnComponent(629, 58, "");
                player.getPackets().sendTextOnComponent(629, 46, "Pending Offers:");
                player.getPackets().sendTextOnComponent(629, 59, Utils.formatDoubledAmount(grandexchangeValue));
                player.getPackets().sendTextOnComponent(629, 47, "Collection Box:");
                player.getPackets().sendTextOnComponent(629, 60, Utils.formatDoubledAmount(collectionValue));
                totalValue = bankValue + inventoryValue + equipmentValue + moneyPouch + collectionValue + grandexchangeValue;
                player.getPackets().sendTextOnComponent(629, 48, "Total wealth:");
                player.getPackets().sendTextOnComponent(629, 61, Utils.formatDoubledAmount(totalValue));
                player.getPackets().sendTextOnComponent(629, 49, "");
                player.getPackets().sendTextOnComponent(629, 62, "");
                player.getPackets().sendTextOnComponent(629, 50, "Highest value Wildy kill:");
                player.getPackets().sendTextOnComponent(629, 63, (player.getHighestValuedKill() >= Integer.MAX_VALUE ? "Lots!" : Utils.getFormattedNumber(player.getHighestValuedKill(), ',')));
                int bossKills = 0;
                bossKills += player.getBossKillcount().size();
                player.getPackets().sendTextOnComponent(629, 51, "Total boss kills:");
                player.getPackets().sendTextOnComponent(629, 64, Utils.getFormattedNumber(bossKills, ','));
                player.getPackets().sendTextOnComponent(629, 52, "Slayer tasks completed:");
                player.getPackets().sendTextOnComponent(629, 65, Utils.getFormattedNumber(player.getSlayerManager().getCompletedTasks()));
                player.getPackets().sendHideIComponent(629, 68, true);
                player.getPackets().sendHideIComponent(629, 69, true);
            } else if (componentId >= 46 && componentId <= 64) {
                int tabId = 9 - ((componentId - 46) / 2);
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) player.getBank().setCurrentTab(tabId);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) player.getBank().collapse(tabId);
            } else if (componentId == 95) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) player.getBank().withdrawItem(slotId, 1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
                    player.getBank().withdrawItem(slotId, 5);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    player.getBank().withdrawItem(slotId, 10);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
                    player.getBank().withdrawLastAmount(slotId);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
                    player.temporaryAttribute().put("bank_item_X_Slot", slotId);
                    player.temporaryAttribute().put("bank_isWithdraw", Boolean.TRUE);
                    player.getPackets().sendRunScript(108, new Object[]{"Enter Amount:"});
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
                    player.getBank().withdrawItem(slotId, Integer.MAX_VALUE);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET)
                    player.getBank().withdrawItemButOne(slotId);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET) player.getBank().sendExamine(slotId);

            } else if (componentId == 119) {
                openEquipmentBonuses(player, true);
            }
        } else if (interfaceId == 763) {
            if (componentId == 0) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
                    player.getBank().depositItem(slotId, 1, true);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
                    player.getBank().depositItem(slotId, 5, true);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    player.getBank().depositItem(slotId, 10, true);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
                    player.getBank().depositLastAmount(slotId);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
                    player.temporaryAttribute().put("bank_item_X_Slot", slotId);
                    player.temporaryAttribute().remove("bank_isWithdraw");
                    player.getPackets().sendRunScript(108, new Object[]{"Enter Amount:"});
                } else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
                    player.getBank().depositItem(slotId, Integer.MAX_VALUE, true);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
                    player.getInventory().sendExamine(slotId);
            }
        } else if (interfaceId == 72) {
            if (componentId == 68) {
                ShopsHandler.openShop(player, 10);// General
                // store
                // }
                /*
                 * if (componentId == 67) { ShopsHandler.openShop(player, 15);// Melee Store }
                 * else if (componentId == 66) { ShopsHandler.openShop(player, 16);// Melee
                 * Armour } else if (componentId == 65) { ShopsHandler.openShop(player, 13);//
                 * Range store } else if (componentId == 64) { ShopsHandler.openShop(player,
                 * 14);// Range Armour } else if (componentId == 73) {
                 * ShopsHandler.openShop(player, 11);// Magic Store } else if (componentId ==
                 * 72) { ShopsHandler.openShop(player, 12);// Magic Armour
                 */
            } else if (componentId == 71) {
                ShopsHandler.openShop(player, 17);// Skilling Store
            } else if (componentId == 70) {
            } else if (componentId == 69) {
            }
        } else if (interfaceId == 767) {
            if (componentId == 10) player.getBank().openBank();
        } else if (interfaceId == 884) {
            if (componentId == 4) {
                SpecialAttack.submitSpecialRequest(player);
            }
            else if (componentId >= 7 && componentId <= 10) {
                player.getCombatDefinitions().setAttackStyle(componentId - 7);
            } else if (componentId == 11) {
                player.getCombatDefinitions().switchAutoRelatie();
            }
        } else if (interfaceId == 755) {
            if (componentId == 44) {
                player.getPackets().sendWindowsPane(player.getInterfaceManager().hasRezizableScreen() ? 746 : 548, 2);
                player.animate(new Animation(-1));
            } else if (componentId == 42) {
                player.getHintIconsManager().removeAll();
                player.getPackets().sendVar(1159, 1);
            }
        } else if (interfaceId == 20) SkillCapeCustomizer.handleSkillCapeCustomizer(player, componentId);
        else if (interfaceId == 1056) {
            if (componentId == 173) player.getInterfaceManager().sendInterface(917);
        } else if (interfaceId == 751) {
            if (componentId == 26) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
                    player.getFriendsIgnores().setPrivateStatus(0);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    player.getFriendsIgnores().setPrivateStatus(1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
                    player.getFriendsIgnores().setPrivateStatus(2);
            } else if (componentId == 14) {
                player.getDialogueManager().startDialogue("Report");
            } else if (componentId == 32) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) player.setFilterGame(false);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) player.setFilterGame(true);
            } else if (componentId == 29) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) player.setPublicStatus(0);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) player.setPublicStatus(1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) player.setPublicStatus(2);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) player.setPublicStatus(3);
            } else if (componentId == 0) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
                    player.getFriendsIgnores().setFriendsChatStatus(0);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
                    player.getFriendsIgnores().setFriendsChatStatus(1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
                    player.getFriendsIgnores().setFriendsChatStatus(2);
            } else if (componentId == 23) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) player.setClanStatus(0);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) player.setClanStatus(1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) player.setClanStatus(2);
            } else if (componentId == 20) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) player.setTradeStatus(0);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) player.setTradeStatus(1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) player.setTradeStatus(2);
            } else if (componentId == 23) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) player.setClanStatus(0);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) player.setClanStatus(1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) player.setClanStatus(2);
            } else if (componentId == 17) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) player.setAssistStatus(0);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) player.setAssistStatus(1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) player.setAssistStatus(2);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET) {
                    // ASSIST XP Earned/Time
                }
            }
        } else if (interfaceId == 105 || interfaceId == 107 || interfaceId == 109 || interfaceId == 449)
            player.getGeManager().handleButtons(interfaceId, componentId, slotId, packetId);
        else if (interfaceId == 900) PlayerLook.handleMageMakeOverButtons(player, componentId);
        else if (interfaceId == 1265) {
            Shop shop = (Shop) player.temporaryAttribute().get("Shop");
            if (shop == null) return;
            Integer slot = (Integer) player.temporaryAttribute().get("ShopSelectedSlot");
            player.temporaryAttribute().put("shop_buying", true);
            boolean isBuying = player.temporaryAttribute().get("shop_buying") != null;
            if (componentId == 20) {
                player.temporaryAttribute().put("ShopSelectedSlot", slotId);
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) shop.sendInfo(player, slotId, isBuying);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) shop.handleShop(player, slotId, 1);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) shop.handleShop(player, slotId, 5);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) shop.handleShop(player, slotId, 10);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) shop.handleShop(player, slotId, 50);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET) shop.handleShop(player, slotId, 500);
                else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
                    shop.handleShop(player, slotId, shop.getMainStock()[slot].getAmount());
            } else if (componentId == 201) {
                if (slot == null) {
                    player.getPackets().sendGameMessage("slot null");
                    return;
                }
                shop.handleShop(player, slot, shop.getAmount());
                shop.setAmount(player, 0);
            } else if (componentId == 208) {
                shop.setAmount(player, shop.getAmount() + 5);
            } else if (componentId == 15) {
                shop.setAmount(player, shop.getAmount() + 1);
            } else if (componentId == 214) {
                if (shop.getAmount() > 1) shop.setAmount(player, shop.getAmount() - 1);
                else player.getPackets().sendGameMessage("You can't set quantity to any lower.");
            } else if (componentId == 217) {
                if (shop.getAmount() == 1) {
                    player.getPackets().sendGameMessage("You can't set quantity to any lower.");
                    return;
                }
                if (shop.getAmount() > 5) shop.setAmount(player, shop.getAmount() - 5);
                else shop.setAmount(player, 1);
            } else if (componentId == 220) {
                if (shop.getAmount() == 1) {
                    player.getPackets().sendGameMessage("You can't set quantity to any lower.");
                    return;
                }
                shop.setAmount(player, 1);
            } else if (componentId == 211) {
                if (slot == null) return;
                shop.setAmount(player, isBuying ? shop.getMainStock()[slot].getAmount() : player.getInventory().getNumberOf(player.getInventory().getItem(slot).getId()));
            } else if (componentId == 29) {
                player.getPackets().sendVar(2561, 93);
                player.getTemporaryAttributtes().remove("shop_buying");
            } else if (componentId == 28) {
                player.getTemporaryAttributtes().put("shop_buying", true);

            }
        } else if (interfaceId == 1266) {
            if (player.getInterfaceManager().containsInterface(3010)) {
                Item item = player.getInventory().getItem(slotId);
                player.getShopSystem().handleSellOption(item, packetId);
                return;
            }
            player.temporaryAttribute().put("ShopSelectedSlot", slotId);
            if (componentId == 0) {
                if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET) player.getInventory().sendExamine(slotId);
                else {
                    Shop shop = (Shop) player.temporaryAttribute().get("Shop");
                    if (shop == null) return;
                    Integer slot = (Integer) player.temporaryAttribute().get("ShopSelectedSlot");
                    player.temporaryAttribute().remove("shop_buying");
                    player.getPackets().sendVar(2563, slotId);
                    if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) shop.sendValue(player, slotId);
                    else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) shop.sell(player, slot, 1);
                    else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) shop.sell(player, slot, 5);
                    else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) shop.sell(player, slot, 10);
                    else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) shop.sell(player, slot, 50);
                }
            }
        } else if (interfaceId == 1028) CharacterDesign.handleButtons(player, componentId, slotId, packetId);
        else if (interfaceId == 1108 || interfaceId == 1109) {
            if (player.isLocked()) {
                player.getPackets().sendGameMessage("You can't open this while perfoming an action.");
                return;
            }
            player.getFriendsIgnores().handleFriendChatButtons(interfaceId, componentId, packetId);
        } else if (interfaceId == 1089) {
            if (player.isLocked()) {
                player.getPackets().sendGameMessage("You can't open this while perfoming an action.");
                return;
            }
            if (componentId == 30) player.temporaryAttribute().put("clanflagselection", slotId);
            else if (componentId == 26) {
                Integer flag = (Integer) player.temporaryAttribute().remove("clanflagselection");
                player.stopAll();
                if (flag != null) ClansManager.setClanFlagInterface(player, flag);
            }
        } else if (interfaceId == 1096) {
            if (componentId == 41) ClansManager.viewClammateDetails(player, slotId);
            else if (componentId == 94) ClansManager.switchGuestsInChatCanEnterInterface(player);
            else if (componentId == 95) ClansManager.switchGuestsInChatCanTalkInterface(player);
            else if (componentId == 96) ClansManager.switchRecruitingInterface(player);
            else if (componentId == 97) ClansManager.switchClanTimeInterface(player);
            else if (componentId == 124) ClansManager.openClanMottifInterface(player);
            else if (componentId == 131) ClansManager.openClanMottoInterface(player);
            else if (componentId == 240) ClansManager.setTimeZoneInterface(player, -720 + slotId * 10);
            else if (componentId == 262) player.temporaryAttribute().put("editclanmatejob", slotId);
            else if (componentId == 276) player.temporaryAttribute().put("editclanmaterank", slotId);
            else if (componentId == 309) ClansManager.kickClanmate(player);
            else if (componentId == 318) ClansManager.saveClanmateDetails(player);
            else if (componentId == 290) ClansManager.setWorldIdInterface(player, slotId);
            else if (componentId == 297) ClansManager.openForumThreadInterface(player);
            else if (componentId == 346) ClansManager.openNationalFlagInterface(player);
            else if (componentId == 113) ClansManager.showClanSettingsClanMates(player);
            else if (componentId == 120) ClansManager.showClanSettingsSettings(player);
            else if (componentId == 386) ClansManager.showClanSettingsPermissions(player);
            else if (componentId >= 395 && componentId <= 475) {
                int selectedRank = (componentId - 395) / 8;
                if (selectedRank == 10) selectedRank = 125;
                else if (selectedRank > 5) selectedRank = 100 + selectedRank - 6;
                ClansManager.selectPermissionRank(player, selectedRank);
            } else if (componentId == 489) ClansManager.selectPermissionTab(player, 1);
            else if (componentId == 498) ClansManager.selectPermissionTab(player, 2);
            else if (componentId == 506) ClansManager.selectPermissionTab(player, 3);
            else if (componentId == 514) ClansManager.selectPermissionTab(player, 4);
            else if (componentId == 522) ClansManager.selectPermissionTab(player, 5);
        } else if (interfaceId == 1105) {
            if (componentId == 63 || componentId == 66)
                ClansManager.setClanMottifTextureInterface(player, false, slotId);
            else if (componentId == 35) ClansManager.openSetMottifColor(player, 0);
            else if (componentId == 80) ClansManager.openSetMottifColor(player, 1);
            else if (componentId == 92) ClansManager.openSetMottifColor(player, 2);
            else if (componentId == 104) ClansManager.openSetMottifColor(player, 3);
            else if (componentId == 120) player.stopAll();
        } else if (interfaceId == 1110) {
            if (componentId == 82) ClansManager.joinClanChatChannel(player);
            else if (componentId == 75) ClansManager.openClanDetails(player);
            else if (componentId == 78) ClansManager.openClanSettings(player);
            else if (componentId == 91) ClansManager.joinGuestClanChat(player);
            else if (componentId == 95) ClansManager.banPlayer(player);
            else if (componentId == 99) ClansManager.unbanPlayer(player);
            else if (componentId == 11) ClansManager.unbanPlayer(player, slotId);
            else if (componentId == 109) ClansManager.leaveClan(player);
        } else if (interfaceId == 1079) player.closeInterfaces();
        else if (interfaceId == 374) {
            if (componentId >= 5 && componentId <= 9)
                player.setNextWorldTile(new WorldTile(FightPitsViewingOrb.ORB_TELEPORTS[componentId - 5]));
            else if (componentId == 15) player.stopAll();
        } else if (interfaceId == 1092) {
            String[] lodestoneNames = new String[]{"Lunar Isle", "Al Kharid", "Ardougne", "Burthorpe", "Catherby", "Draynor Village", "Edgeville", "Falador", "Lumbridge", "Port Sarim", "Seer's Village", "Taverley", "Varrock", "Yannile"};
            if (componentId != 7 && !player.lodestone[componentId - 38]) {
                player.getPackets().sendGameMessage(lodestoneNames[componentId - 39] + " lodestone is not activated.");
                return;
            } else if (componentId == 7 && !player.lodestone[0]) {
                player.getPackets().sendGameMessage("Bandit Camp lodestone is not activated.");
                return;
            }
            player.stopAll();
            WorldTile destTile = null;
            switch (componentId) {
                case 47:
                    destTile = HomeTeleport.LUMBRIDGE_LODE_STONE;
                    break;
                case 42:
                    destTile = HomeTeleport.BURTHORPE_LODE_STONE;
                    break;
                case 39:
                    destTile = HomeTeleport.LUNAR_ISLE_LODE_STONE;
                    break;
                case 7:
                    destTile = HomeTeleport.BANDIT_CAMP_LODE_STONE;
                    break;
                case 50:
                    destTile = HomeTeleport.TAVERLY_LODE_STONE;
                    break;
                case 40:
                    destTile = HomeTeleport.ALKHARID_LODE_STONE;
                    break;
                case 51:
                    destTile = HomeTeleport.VARROCK_LODE_STONE;
                    break;
                case 45:
                    destTile = HomeTeleport.EDGEVILLE_LODE_STONE;
                    break;
                case 46:
                    destTile = HomeTeleport.FALADOR_LODE_STONE;
                    break;
                case 48:
                    destTile = HomeTeleport.PORT_SARIM_LODE_STONE;
                    break;
                case 44:
                    destTile = HomeTeleport.DRAYNOR_VILLAGE_LODE_STONE;
                    break;
                case 41:
                    destTile = HomeTeleport.ARDOUGNE_LODE_STONE;
                    break;
                case 43:
                    destTile = HomeTeleport.CATHERBY_LODE_STONE;
                    break;
                case 52:
                    destTile = HomeTeleport.YANILLE_LODE_STONE;
                    break;
                case 49:
                    destTile = HomeTeleport.SEERS_VILLAGE_LODE_STONE;
                    break;
            }
            if (destTile != null) player.getActionManager().setAction(new HomeTeleport(destTile));
        } else if (interfaceId == 1214) player.getSkills().handleSetupXPCounter(componentId);
        else if (interfaceId == 1292) {
            if (componentId == 12) Crucible.enterArena(player);
            else if (componentId == 13) player.closeInterfaces();
        }

        if (interfaceId == 679) {
            if (packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET && (slotId2 == 20769 || slotId2 == 20771)) {
                player.getDialogueManager().startDialogue("CompFeatures");
            }
            if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {

            }
        }
        if (player.isDeveloper()) {
            player.getPackets().sendPanelBoxMessage("Interface: " + interfaceId + " Component: " + componentId + " Slot: " + slotId);
        }
    }

    public static void sendItemStats(Player player, Item item) {
        if (item.getId() == 772) return;

        StringBuilder b = new StringBuilder();

        // Create a temporary bonuses array for this single item
        int[] bonuses = new int[BonusType.getEntries().size()];
        Arrays.fill(bonuses, 0);

        ItemDefinitions definitions = item.getDefinitions();
        if (definitions != null) {
            // Get base bonuses from item definitions
            for (BonusType bonus : BonusType.getEntries()) {
                int value = definitions.getDataFromClientScript(bonus.getClientScriptId(), 0);
                if (bonus == BonusType.RangedStrBonus) {
                    // Special handling for ranged strength
                    continue;
                }
                bonuses[bonus.getIndex()] += value;
            }

            // Handle special item metadata (like Dragonfire Shield)
            ItemMetadata metadata = item.getMetadata();
            if (metadata instanceof DragonFireShieldMetaData dfsMeta) {
                int extraDef = dfsMeta.getValue();
                for (int i = BonusType.StabDefence.getIndex(); i <= BonusType.CrushDefence.getIndex(); i++) {
                    bonuses[i] += extraDef;
                }
                bonuses[BonusType.RangeDefence.getIndex()] += extraDef;
            }

            // Handle ranged strength separately
            int rangedStrength = definitions.getRangedStrengthBonus();
            if (rangedStrength > 0) {
                bonuses[BonusType.RangedStrBonus.getIndex()] += rangedStrength;
            }

            // Handle god arrows special scaling
            if (item.getEquipSlot() == Equipment.SLOT_ARROWS && isGodArrow(item)) {
                int rangedLevel = player.getSkills().getLevel(Skills.RANGE);
                int scaling = Math.min((int) Math.floor((rangedLevel / 70.0) * 49), 49);
                bonuses[BonusType.RangedStrBonus.getIndex()] += scaling * 10;
            }

            // Handle goliath gloves if it's gloves
            if (item.getEquipSlot() == Equipment.SLOT_HANDS && hasGoliath(item)) {
                bonuses[BonusType.StregthBonus.getIndex()] += 820;
            }
        }

        // Now format and display the bonuses
        for (int i = 0; i < bonuses.length; i++) {
            int bonus = bonuses[i];
            String bonusName = CombatDefinitions.BONUS_LABELS[i] + ": ";

            // Apply the same transformations as in refreshEquipBonuses
            if (i == BonusType.StregthBonus.getIndex() || i == BonusType.RangedStrBonus.getIndex()) {
                bonus /= 10; // Strength and Ranged Strength are divided by 10
            }

            bonusName = bonusName + (bonus >= 0 ? "+" : "") + bonus;

            // Add percentage sign for specific bonuses
            if (i == BonusType.MagicDamage.getIndex() ||
                    (i >= BonusType.AbsorbMelee.getIndex() && i <= BonusType.AbsorbMage.getIndex())) {
                bonusName = bonusName + "%";
            }

            // Only show bonuses that are not zero
            if (bonus != 0) {
                b.append(bonusName).append("<br>");
            }
        }

        // If no bonuses found, show message
        if (b.length() == 0) {
            b.append("No combat bonuses<br>");
        }

        player.getPackets().sendGlobalString(321, "Stats for " + item.getName());
        player.getPackets().sendGlobalString(324, b.toString());
        player.getPackets().sendHideIComponent(667, 49, false);

        player.setCloseInterfacesEvent(new Runnable() {
            @Override
            public void run() {
                player.getPackets().sendGlobalString(321, "");
                player.getPackets().sendGlobalString(324, "");
                player.getPackets().sendHideIComponent(667, 49, true);
            }
        });
    }

    // Helper methods (should be static or accessible)
    private static boolean hasGoliath(Item gloves) {
        if (gloves == null)
            return false;
        return gloves.isItem("item.goliath_gloves_black") || gloves.isItem("item.goliath_gloves_red") ||
                gloves.isItem("item.goliath_gloves_white") || gloves.isItem("item.goliath_gloves_yellow");
    }

    private static boolean isGodArrow(Item ammo) {
        return ammo.isItem("item.zamorak_arrows") || ammo.isItem("item.saradomin_arrows") ||
                ammo.isItem("item.guthix_arrows");
    }


    public static void sendWear(Player player, int[] slotIds) {
        if (player.hasFinished() || player.isDead()) return;
        boolean worn = false;
        Item[] copy = player.getInventory().getItems().getItemsCopy();
        for (int slotId : slotIds) {
            Item item = player.getInventory().getItem(slotId);
            if (item == null) continue;
            if (sendWear(player, slotId, item.getId())) worn = true;
        }
        player.getInventory().refreshItems(copy);
        if (worn) {
            player.getAppearence().generateAppearenceData();
            player.getPackets().sendSound(2240, 0, 1);
        }
    }

    public static void sendTakeOff(Player player, int[] slotIds) {
        if (player.hasFinished() || player.isDead()) return;
        boolean worn = false;
        for (int slotId : slotIds) {
            Item item = player.getEquipment().getItem(slotId);
            if (item == null) continue;
            if (sendTakeOff(player, slotId, item.getId())) {
                worn = true;
            }
        }
        player.getEquipment().refresh();
        if (worn) {
            player.getAppearence().generateAppearenceData();
            player.getPackets().sendSound(2241, 0, 1);
        }
    }

    public static boolean sendTakeOff(final Player player, final int slotId, int itemId) {
        Item item = player.getEquipment().getItem(slotId);
        if (player.hasStaffOfLight() && player.hasStaffOfLightActive()) {
            player.resetStaffOfLightEffect();
            player.getPackets().sendGameMessage("The power of the light fades. Your resistance to melee attacks return to normal.");
        }
        if (!player.getInventory().addItem(item)) {
            return false;
        }
        player.getEquipment().getItems().set(slotId, null);
        player.getEquipment().refresh(slotId);
        if (item.getId() == 4024) player.getAppearence().transformIntoNPC(-1);
        if (slotId == 3) player.getCombatDefinitions().decreaseSpecialAttack(0);

        if (player.getHitpoints() > (player.getMaxHitpoints() * 1.15)) {
            player.setHitpoints(player.getMaxHitpoints());
            player.refreshHitPoints();
        }
        if (Runecrafting.isTiara(item.getId())) player.getPackets().sendVar(491, 0);
        player.getAppearence().generateAppearenceData();
        refreshEquipBonuses(player);
        return true;
    }

    public static void registerUnEquip(Player player, int slotId) {
        if (player.getTakeOffSwitchItemCache().contains(slotId)) {
            return;
        }
        player.stopAll(false, false, true);
        player.getTakeOffSwitchItemCache().add(slotId);
    }

    public static void registerEquip(Player player, int slotId) {
        if (player.getSwitchItemCache().contains(slotId)) return;
        player.lastItemSwitchTick = WorldThread.getCycleIndex();
        player.stopAll(false, false, true);
        if (player.getInventory().getItem(slotId).getEquipSlot() == Equipment.SLOT_WEAPON) {
            if (player.lastSpecClickTick != player.lastItemSwitchTick && player.getCombatDefinitions().isUsingSpecialAttack()) {
                player.combatDefinitions.switchUsingSpecialAttack();
                //player.getQueuedInstantCombats().clear();
            }
            player.itemSwitch = true;
        }
        player.getSwitchItemCache().add(slotId);
    }

    public static boolean sendWear(Player player, int slotId, int itemId) {
        if (player.hasFinished() || player.isDead()) {
            return false;
        }
        Item inventoryItem = player.getInventory().getItem(slotId);
        if (inventoryItem == null || inventoryItem.getId() != itemId) {
            return false;
        }

        int equipmentSlot = Equipment.getItemSlot(itemId);
        if (equipmentSlot == -1 || inventoryItem.getDefinitions().isNoted()) {
            player.getPackets().sendGameMessage("You can't wear that.");
            return true;
        }

        //player.stopAll(false, false, true, equipmentSlot == Equipment.SLOT_WEAPON);
        if (!hasSkillRequirements(player, inventoryItem)) {
            return false;
        }

        if (!player.getControlerManager().canEquip(equipmentSlot, itemId)) {
            return false;
        }

        boolean isTwoHandedWeapon = equipmentSlot == Equipment.SLOT_WEAPON && Equipment.isTwoHandedWeapon(inventoryItem);

        if (isTwoHandedWeapon && player.getInventory().getItems().getFreeSlots() == 0 && player.getEquipment().getWeaponId() != -1 && player.getEquipment().hasShield()) {
            player.getPackets().sendGameMessage("Not enough free space in your inventory.");
            return false;
        }
        player.getInventory().getItems().set(slotId, null);

        if (equipmentSlot == Equipment.SLOT_WEAPON && isTwoHandedWeapon) {
            removeShieldForTwoHandedWeapon(player, slotId, inventoryItem);
        } else if (equipmentSlot == Equipment.SLOT_SHIELD) {
            removeWeaponIfTwoHanded(player, slotId, inventoryItem);
        }

        handleReplacingEquippedItem(player, equipmentSlot, slotId, inventoryItem);

        if (equipmentSlot == Equipment.SLOT_AURA) {
            player.getAuraManager().removeAura();
        }

        equipItem(player, equipmentSlot, inventoryItem);

        if (player.getHitpoints() > player.getMaxHitpoints() * 1.15) {
            player.setHitpoints(player.getMaxHitpoints());
            player.refreshHitPoints();
        }
        Item currentlyEquipped = player.getEquipment().getItem(equipmentSlot);
        if (currentlyEquipped.getEquipSlot() == Equipment.SLOT_WEAPON) {
            player.itemSwitch = false;
            //player.getQueuedInstantCombats().clear();
        }
        refreshEquipBonuses(player);
        return true;
    }

    private static boolean hasSkillRequirements(Player player, Item item) {
        HashMap<Integer, Integer> requirements = item.getDefinitions().getWearingSkillRequiriments();
        if (requirements == null) return true;

        boolean hasRequirements = true;
        for (var entry : requirements.entrySet()) {
            int skillId = entry.getKey();
            int requiredLevel = entry.getValue();

            if (skillId < 0 || skillId > 24 || requiredLevel < 0 || requiredLevel > 120) {
                continue;
            }

            int playerLevel = player.getSkills().getLevelForXp(skillId);
            if (playerLevel < requiredLevel) {
                if (hasRequirements) {
                    player.getPackets().sendGameMessage("You are not high enough level to use this item.");
                }
                hasRequirements = false;

                String skillName = Skills.SKILL_NAME[skillId].toLowerCase();
                String article = skillName.startsWith("a") ? "an" : "a";
                player.getPackets().sendGameMessage("You need to have " + article + " " + skillName + " level of " + requiredLevel + ".");
            }
        }
        return hasRequirements;
    }

    private static void removeShieldForTwoHandedWeapon(Player player, int slotId, Item inventoryItem) {
        Item shield = player.getEquipment().getItem(Equipment.SLOT_SHIELD);
        if (shield != null) {
            if (!player.getInventory().getItems().add(shield)) {
                player.getInventory().getItems().set(slotId, inventoryItem);
                return;
            }
            player.getEquipment().getItems().set(Equipment.SLOT_SHIELD, null);
            player.getEquipment().refresh(Equipment.SLOT_SHIELD);
        }
    }

    private static void removeWeaponIfTwoHanded(Player player, int slotId, Item inventoryItem) {
        Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
        if (weapon != null && Equipment.isTwoHandedWeapon(weapon)) {
            if (player.hasStaffOfLight() && player.hasStaffOfLightActive()) {
                player.resetStaffOfLightEffect();
                player.getPackets().sendGameMessage("The power of the light fades. Your resistance to melee attacks return to normal.");
            }
            if (!player.getInventory().getItems().add(weapon)) {
                player.getInventory().getItems().set(slotId, inventoryItem);
                return;
            }
            player.getEquipment().getItems().set(Equipment.SLOT_WEAPON, null);
            player.getEquipment().refresh(Equipment.SLOT_WEAPON);
        }
    }

    private static void handleReplacingEquippedItem(Player player, int equipmentSlot, int slotId, Item inventoryItem) {
        Item currentlyEquipped = player.getEquipment().getItem(equipmentSlot);
        if (currentlyEquipped != null && (inventoryItem.getId() != currentlyEquipped.getId() || !inventoryItem.getDefinitions().isStackable())) {
            if (player.getInventory().getItems().get(slotId) == null) {
                player.getInventory().getItems().set(slotId, currentlyEquipped.clone());
            } else {
                player.getInventory().getItems().add(currentlyEquipped.clone());
            }
            player.getEquipment().getItems().set(equipmentSlot, null);
        }
    }

    private static void equipItem(Player player, int equipmentSlot, Item itemToEquip) {
        Item equippedItem = player.getEquipment().getItem(equipmentSlot);
        if (equippedItem != null && itemToEquip.getDefinitions().isStackable() && equippedItem.getId() == itemToEquip.getId()) {
            itemToEquip.setAmount(itemToEquip.getAmount() + equippedItem.getAmount());
        }
        player.getEquipment().getItems().set(equipmentSlot, itemToEquip);
        player.getEquipment().refresh(equipmentSlot);
    }

    public static void openItemsKeptOnDeath(Player player) {
        player.getInterfaceManager().sendInterface(17);
        sendItemsKeptOnDeath(player, player.inPkingArea() ? true : false);
    }

    public static final class PartialKeepRecord {
        public final int id;
        public final int amount;
        public final int originalSlot;

        public PartialKeepRecord(int id, int amount, int originalSlot) {
            this.id = id;
            this.amount = amount;
            this.originalSlot = originalSlot;
        }
    }

    private static final List<PartialKeepRecord> partialKeeps = new ArrayList<>();

    public static Integer[][] getItemSlotsKeptOnDeath(final Player player, boolean atWilderness, boolean skulled, boolean protectPrayer) {
        partialKeeps.clear(); // Clear previous partials

        ArrayList<Integer> protectedItems = new ArrayList<>();
        ArrayList<Integer> lostItems = new ArrayList<>();

        boolean inRiskArea = FfaZone.inRiskArea(player);
        int keptAmount = (player.hasSkull() || inRiskArea) ? 0 : 3;
        if (protectPrayer) keptAmount++;

        class SlotEntry {
            int slot;
            int itemId;
            int amount;
            long totalValue;
            long perUnitValue;

            SlotEntry(int slot, int itemId, int amount, long totalValue, long perUnitValue) {
                this.slot = slot;
                this.itemId = itemId;
                this.amount = amount;
                this.totalValue = totalValue;
                this.perUnitValue = perUnitValue;
            }
        }

        List<SlotEntry> droppedSlots = new ArrayList<>();

        for (int i = 1; i < 44; i++) {
            Item item = i >= 16 ? player.getInventory().getItem(i - 16) : player.getEquipment().getItem(i - 1);
            if (item == null) continue;

            int stageOnDeath = item.getDefinitions().getStageOnDeath();

            if (!atWilderness && stageOnDeath == 1) {
                protectedItems.add(i);
            } else if (ItemConstants.keptOnDeath(item) && atWilderness) {
                protectedItems.add(i);
            } else if (stageOnDeath == -1) {
                lostItems.add(i);
            } else {
                int price = GrandExchange.getPrice(item.getId());
                long totalValue = (long) price * item.getAmount();
                droppedSlots.add(new SlotEntry(i, item.getId(), item.getAmount(), totalValue, price));
            }
        }

        droppedSlots.sort((a, b) -> Long.compare(b.perUnitValue, a.perUnitValue));

        ArrayList<Integer> keptItems = new ArrayList<>();
        ArrayList<Integer> droppedItems = new ArrayList<>();
        int keptUnits = 0;
        int partialIndex = 0;

        for (SlotEntry entry : droppedSlots) {
            if (keptUnits >= keptAmount) {
                droppedItems.add(entry.slot);
                continue;
            }

            int remaining = keptAmount - keptUnits;

            if (entry.amount <= remaining) {
                keptItems.add(entry.slot);
                keptUnits += entry.amount;
            } else {
                // Partial stack handling
                int keptPart = remaining;
                int droppedPart = entry.amount - keptPart;

                int syntheticSlot = -1000 - partialIndex++;
                keptItems.add(syntheticSlot);
                partialKeeps.add(new PartialKeepRecord(entry.itemId, keptPart, entry.slot));

                droppedItems.add(entry.slot);
                keptUnits += keptPart;

            }
        }
        return new Integer[][]{keptItems.toArray(new Integer[0]), droppedItems.toArray(new Integer[0]), protectedItems.toArray(new Integer[0]), atWilderness ? new Integer[0] : lostItems.toArray(new Integer[0])};
    }


    public static Item[][] getItemsKeptOnDeath(Player player, Integer[][] slots) {
        List<Item> keptItems = new ArrayList<>();
        List<Item> droppedItems = new ArrayList<>();

        for (int slot : slots[0]) {
            if (slot >= 0) {
                Item item = slot >= 16 ? player.getInventory().getItem(slot - 16) : player.getEquipment().getItem(slot - 1);
                if (item == null) continue;
                keptItems.add(new Item(item.getId(), item.getAmount()));
            } else {
                int realSlot = -(slot + 1000);
                PartialKeepRecord partial = partialKeeps.get(realSlot);
                if (partial != null) {
                    keptItems.add(new Item(partial.id, partial.amount));
                }
            }
        }

        for (int slot : slots[2]) {
            Item item = slot >= 16 ? player.getInventory().getItem(slot - 16) : player.getEquipment().getItem(slot - 1);
            if (item != null) keptItems.add(new Item(item.getId(), item.getAmount()));
        }

        for (int slot : slots[1]) {
            Item item = slot >= 16 ? player.getInventory().getItem(slot - 16) : player.getEquipment().getItem(slot - 1);
            if (item != null) droppedItems.add(new Item(item.getId(), item.getAmount()));
        }

        return new Item[][]{keptItems.toArray(new Item[0]), droppedItems.toArray(new Item[0])};
    }


    public static void sendItemsKeptOnDeath(Player player, boolean wilderness) {
        boolean skulled = player.hasSkull();
        boolean inFfa = FfaZone.inArea(player);
        @SuppressWarnings("unused")
        // TODO
        boolean inRiskArea = FfaZone.inRiskArea(player);
        Integer[][] slots = getItemSlotsKeptOnDeath(player, wilderness, skulled, player.getPrayer().hasProtectItemPrayerActive());
        Item[][] items = getItemsKeptOnDeath(player, slots);
        long riskedWealth = 0;
        long carriedWealth = 0;
        for (Item item : items[1]) {
            if (item == null) continue;
            long amount = item.getAmount();
            long price = GrandExchange.getPrice(item.getId());
            carriedWealth = riskedWealth += price * amount;
        }
        for (Item item : items[0]) {
            if (item == null) continue;
            long amount = item.getAmount();
            long price = GrandExchange.getPrice(item.getId());
            carriedWealth += price * amount;
        }
        Set<Integer> sentSlots = new HashSet<>();

        int keptSlotCount = 0;
        for (int i = 0; i < slots[0].length && keptSlotCount < 4; i++) {
            int rawSlot = slots[0][i];
            int slotIndex;
            if (rawSlot < 0) {
                int realSlot = -(rawSlot + 1000);
                Item keptItem = items[0][i];
                slotIndex = player.getInventory().getItems().getThisItemSlot(keptItem.getId());
            } else {
                slotIndex = rawSlot;
            }
            if (sentSlots.add(slotIndex)) {
                int slotConfig = Rscm.lookup("varbit.death_kept_item_count") + keptSlotCount;
                player.getVarsManager().sendVarBit(slotConfig, slotIndex);
                keptSlotCount++;
            }
        }

        //clear the unused slots
        for (int i = keptSlotCount; i < 4; i++) {
            int slotConfig = Rscm.lookup("varbit.death_kept_item_count") + i;
            player.getVarsManager().sendVarBit(slotConfig, -1);
        }

        player.getVarsManager().sendVarBit("varbit.death_kept_item_count", Math.max(keptSlotCount, 1));
        player.getVarsManager().sendVarBit("varbit.death_in_unsafe_area", (wilderness || inFfa) ? 1 : 0);
        if (!inFfa) player.getVarsManager().sendVarBit("varbit.death_player_skulled", player.hasSkull() ? 1 : 0);
        StringBuffer text = new StringBuffer();
        text.append("Items kept on death:").append("<br><br>");
        for (Item item : items[0]) {
            text.append(item.getName()).append("<br>").append("<br>");
        }
        text.append("<br>").append("<br>").append("Carried wealth:").append("<br>").append(Utils.getFormattedNumber(carriedWealth, ',')).append("<br>").append("<br>").append("Risked wealth:").append("<br>").append(Utils.getFormattedNumber(riskedWealth, ',')).append("<br>").append("<br>");
        text.append("Respawn point:").append("<br>").append("Edgeville");
        player.getPackets().sendGlobalString(352, text.toString());
    }

    /*
     * player.getVarsManager().sendVar(8348, 1); WorldTasksManager.schedule(new
     * WorldTask() {
     *
     *
     */
    public static void openEquipmentBonuses(final Player player, boolean banking) {
        int EQUIPMENT_BONUS_INTERFACE = Rscm.lookup("interface.equipment_bonuses");
        int EQUIPMENT_INVENTORY = Rscm.lookup("interface.equipment_bonuses_inventory");

        player.stopAll();
        player.getInterfaceManager().sendInventoryInterface(EQUIPMENT_BONUS_INTERFACE);
        player.getInterfaceManager().sendInterface(EQUIPMENT_INVENTORY);
        player.getPackets().sendHideIComponent(EQUIPMENT_INVENTORY, 1, false);
        player.getVarsManager().sendVarBit("varbit.equipment_bonuses_from_bank", banking ? 1 : 0);
        player.getVarsManager().sendVarBit("varbit.equipment_bonuses_open", 1);
        player.getPackets().sendGlobalVar("globalvar.weapon_render_emote", player.getEquipment().getWeaponRenderEmote());
        player.getPackets().sendRunScript("clientscript.setup_equipment_bonuses", 1);

        int container = Rscm.lookup("container.inventory_container");
        player.getPackets().sendItems(container, player.getInventory().getItems());
        player.getPackets().sendInterSetItemsOptionsScript(EQUIPMENT_BONUS_INTERFACE, 0, container, 4, 7, "Equip", "Compare", "Stats", "Examine");
        player.getPackets().sendUnlockOptions(EQUIPMENT_BONUS_INTERFACE, 0, 0, 27, 0, 1, 2, 3);
        player.getPackets().sendUnlockOptions(EQUIPMENT_INVENTORY, 9, 0, 24, 0, 8, 9);
        player.getPackets().sendComponentSettings(EQUIPMENT_INVENTORY, 14, 0, 13, 1030);
        refreshEquipBonuses(player);
        if (banking) {
            player.getTemporaryAttributtes().put("Banking", Boolean.TRUE);
            player.setCloseInterfacesEvent(new Runnable() {
                @Override
                public void run() {
                    player.getTemporaryAttributtes().remove("Banking");
                    player.getVarsManager().sendVarBit("varbit.equipment_bonuses_from_bank", 0);
                }
            });
        }
    }

    public static void renewSummoningPoints(Player player) {
        int summonLevel = player.getSkills().getLevelForXp(Skills.SUMMONING);

        if (player.restoreDelay < Utils.currentTimeMillis() && player.getSkills().getLevel(Skills.SUMMONING) < summonLevel) {
            player.restoreDelay = (Utils.currentTimeMillis() + 30000);
            player.getSkills().set(Skills.SUMMONING, summonLevel);
            player.animate(new Animation(8502));
            player.gfx(new Graphics(1308));
            player.getPackets().sendGameMessage("You restored your Summoning points with the Completionist cape!", true);
        } else if (player.restoreDelay > Utils.currentTimeMillis()) {
            player.message("Your cape is still recharging from it's last use.");
        } else {
            player.message("Your cape does not respond due to you already having full summoning points.");
        }
    }

    public static int getEnchantId(int id) {
        switch (id) {
            case 14:
                return 879;
            case 29:
                return 9337;
            case 18:
                return 9335;
            case 22:
                return 880;
            case 32:
                return 9338;
            case 26:
                return 9336;
            case 35:
                return 9339;
            case 38:
                return 9340;
            case 41:
                return 9341;
            case 44:
                return 9342;
        }
        return -1;
    }

    public static int getEnchantId2(int id) {
        switch (id) {
            case 14:
                return 9236;
            case 29:
                return 9240;
            case 18:
                return 9237;
            case 22:
                return 9238;
            case 32:
                return 9241;
            case 26:
                return 9239;
            case 35:
                return 9242;
            case 38:
                return 9243;
            case 41:
                return 9244;
            case 44:
                return 9245;
        }
        return -1;
    }

    public static void refreshEquipBonuses(Player player) {
        if (player.getInterfaceManager().containsInterface(667)) {
            player.getPackets().sendGlobalVar(779, player.getEquipment().getWeaponRenderEmote());
            for (int i = 0; i < BonusType.getEntries().size(); i++) {
                String bonusName = names[i <= BonusType.RangeAttack.getIndex() ? i : i - 5] + ": ";
                int bonus = player.combatDefinitions.getBonuses()[i];
                if (i == BonusType.StregthBonus.getIndex() || i == BonusType.RangedStrBonus.getIndex())
                    bonus /= 10;
                bonusName = bonusName + (bonus >= 0 ? "+" : "") + bonus;
                if (i == BonusType.MagicDamage.getIndex() || i >= BonusType.AbsorbMelee.getIndex() && i <= BonusType.AbsorbMage.getIndex())
                    bonusName = bonusName + "%";
                player.getPackets().sendWeight(player.getWeight());
                player.getPackets().sendTextOnComponent(667, 28 + i, bonusName);
            }
        }
    }

    public static void refreshUntradeables(Player player) {
        Item[] items = player.getUntradeables().getContainerItems();
        player.getInterfaceManager().sendInterface(1284);
        player.getPackets().sendTextOnComponent(1284, 28, "Untradeables");
        player.getPackets().sendTextOnComponent(1284, 4, "-");
        player.getPackets().sendTextOnComponent(1284, 42, "-");
        player.getPackets().sendTextOnComponent(1284, 46, "-");
        player.getPackets().sendHideIComponent(1284, 8, true);
        player.getPackets().sendHideIComponent(1284, 9, true);
        player.getPackets().sendHideIComponent(1284, 10, true);
        player.getPackets().sendInterSetItemsOptionsScript(1284, 7, 100, 8, 3, "Claim", "Bank", "Examine");
        player.getPackets().sendUnlockOptions(1284, 7, 0, 28, 0, 1, 2);
        player.getPackets().sendItems(100, items);
        player.temporaryAttribute().put("untradeables", Boolean.TRUE);
        player.setCloseInterfacesEvent(new Runnable() {
            @Override
            public void run() {
                player.temporaryAttribute().remove("untradeables");
            }
        });
    }


    public static void openSkillGuide(Player player) {
        player.getInterfaceManager().sendInterface(499);
    }

    private static String names[] = {"Stab", "Slash", "Crush", "Magic", "Ranged", "Summoning", "Absorb Melee", "Absorb Magic", "Absorb Ranged", "Strength", "Ranged Strength", "Prayer", "Magic Damage"};
}