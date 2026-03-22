package raynna.core.packets.decode.world.support;

import raynna.core.packets.decode.WorldPacketsDecoder;

import raynna.app.Settings;
import raynna.core.packets.InputStream;
import raynna.game.World;
import raynna.game.WorldTile;
import raynna.game.item.Item;
import raynna.game.item.meta.GreaterRunicStaffMetaData;
import raynna.game.minigames.clanwars.ClanWars;
import raynna.game.minigames.duel.DuelArena;
import raynna.game.player.Player;
import raynna.game.player.content.ReferSystem;
import raynna.game.player.Skills;
import raynna.game.player.actions.skills.construction.Sawmill;
import raynna.game.player.actions.skills.construction.Sawmill.Plank;
import raynna.game.player.actions.skills.smithing.DungeoneeringSmithing;
import raynna.game.player.actions.skills.summoning.Summoning;
import raynna.game.player.content.GambleTest;
import raynna.game.player.content.customtab.SettingsTab;
import raynna.game.player.content.customtab.GearTab;
import raynna.game.player.content.customtab.JournalTab;
import raynna.game.player.content.pet.Pets;
import raynna.game.player.content.presets.Preset;
import raynna.game.player.dialogues.Report;
import raynna.game.player.controllers.construction.SawmillController;
import raynna.game.player.content.unlockables.UnlockableManager;
import raynna.util.SerializableFilesManager;
import raynna.util.Utils;
import raynna.game.npc.drops.DropTableSource;
import raynna.game.player.AccountCreation;
import raynna.game.player.interfaces.DropInterface;
import raynna.game.player.interfaces.DropSearch;
import raynna.game.player.interfaces.PresetInterface;
import raynna.game.player.content.clans.ClansManager;
import raynna.game.player.actions.skills.construction.House;
import raynna.util.Colors;
import raynna.util.DisplayNames;
import raynna.util.Encrypt;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class WorldPromptSupport {

    private WorldPromptSupport() {
    }
    public static boolean handleQuestionScript(Player player, Object in) {
        Object q = player.temporaryAttribute().get("QUESTION_SCRIPT");
        if (q == null) return false;

        Object[] questionScript = (Object[]) q;
        player.temporaryAttribute().remove("QUESTION_SCRIPT");

        File acc = null;
        String question = null;
        Player target = null;
        String name = "null";
        Integer xtime = 0;
        boolean online = true;

        if (questionScript.length == 4) {
            question = (String) questionScript[0];
            target = (Player) questionScript[1];
            online = (boolean) questionScript[2];
            name = (String) questionScript[3];
        } else if (questionScript.length == 5) {
            question = (String) questionScript[0];
            target = (Player) questionScript[1];
            xtime = (Integer) questionScript[2];
            online = (boolean) questionScript[3];
            name = (String) questionScript[4];
        }

        if (in == null || question == null) return false;

        if (question.startsWith("perm") || question.equalsIgnoreCase("blackmark")) {
            if (((String) in).length() < 5) {
                player.temporaryAttribute().put("QUESTION_SCRIPT", questionScript);
                player.getPackets().sendRunScript(110,
                        new Object[]{"Reason to short! Enter a brief reason for this punishment:"});
                return true;
            }

            if (!online) {
                acc = new File("data/characters/" + name.replace(" ", "_") + ".p");
                try {
                    target = (Player) SerializableFilesManager.loadSerializedFile(acc);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                switch (question.toLowerCase()) {
                    case "permmute":
                        player.getPackets().sendGameMessage("You have permanently muted %s. Reason: %s",
                                Utils.formatPlayerNameForDisplay(name), in);
                        if (target != null) target.mute(player.getDisplayName(), (String) in, -1);
                        break;
                }
                try {
                    if (target != null)
                        SerializableFilesManager.storeSerializableClass(target, acc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                switch (question.toLowerCase()) {
                    case "permmute":
                        player.getPackets().sendGameMessage("You have permanently jailed %s. Reason: %s",
                                target.getDisplayName(), in);
                        target.getPackets().sendGameMessage("You have been jailed by %s. Reason: %s",
                                player.getDisplayName(), in);
                        target.mute(player.getDisplayName(), (String) in, -1);
                        break;
                }
            }
            return true;
        }

        if (question.equalsIgnoreCase("xreasonjail") || question.equalsIgnoreCase("xreasonmute")) {
            if (xtime == 0) return false;

            if (!online) {
                acc = new File("data/characters/" + name.replace(" ", "_") + ".p");
                try {
                    target = (Player) SerializableFilesManager.loadSerializedFile(acc);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                switch (question.toLowerCase()) {
                    case "xreasonmute":
                        player.getPackets().sendGameMessage("You have muted %s for %d days. Reason: %s",
                                Utils.formatPlayerNameForDisplay(name), xtime, in);
                        if (target != null) target.mute(player.getDisplayName(), (String) in, xtime);
                        break;
                }
                try {
                    if (target != null)
                        SerializableFilesManager.storeSerializableClass(target, acc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                switch (question.toLowerCase()) {
                    case "xreasonmute":
                        player.getPackets().sendGameMessage("You have muted %s for %d days. Reason: %s",
                                target.getDisplayName(), xtime, in);
                        target.getPackets().sendGameMessage("You have been muted by %s. Reason: %s",
                                player.getDisplayName(), in);
                        target.mute(player.getDisplayName(), (String) in, xtime);
                        break;
                }
            }
            return true;
        }

        if (question.startsWith("x")) {
            if ((Integer) in < 1 || (Integer) in > 15) {
                player.temporaryAttribute().put("QUESTION_SCRIPT", questionScript);
                player.getPackets().sendInputIntegerScript(true, "Number must be between 1 - 15 days:");
                return true;
            }

            switch (question.toLowerCase()) {
                case "xjail":
                    player.temporaryAttribute().put("QUESTION_SCRIPT",
                            new Object[]{"xreasonjail", target, in, online, name});
                    player.getPackets().sendRunScript(110, new Object[]{"Enter a brief reason for this punishment:"});
                    break;

                case "xmute":
                    player.temporaryAttribute().put("QUESTION_SCRIPT",
                            new Object[]{"xreasonmute", target, in, online, name});
                    player.getPackets().sendRunScript(110, new Object[]{"Enter a brief reason for this punishment:"});
                    break;
            }
            return true;
        }

        return false;
    }
    public static void handleEnterLongText(Player player, InputStream stream) {
        if (!player.isActive() || player.isDead()) return;
        String value = stream.readString();
        if (value.equals("")) return;

        if (handleQuestionScript(player, value)) return;

        if (player.temporaryAttribute().remove("entering_note") == Boolean.TRUE) {
            player.getNotes().add(value);
            return;
        } else if (player.temporaryAttribute().remove("editing_note") == Boolean.TRUE) {
            player.getNotes().edit(value);
            return;
        } else if (player.temporaryAttribute().remove("refer") == Boolean.TRUE) {
            player.getTemporaryAttributtes().put("refer", Boolean.FALSE);
            ReferSystem.SendInvite(player, value);
            return;
        } else if (player.temporaryAttribute().remove("doubledrop") == Boolean.TRUE) {
            if (value.equalsIgnoreCase("enable")) {
                Settings.DOUBLE_DROP = true;
                World.sendWorldMessage("<img=7><col=ff000>Double drop is now enabled!", false);
            } else if (value.equalsIgnoreCase("disable")) {
                Settings.DOUBLE_DROP = false;
                World.sendWorldMessage("<img=7><col=ff000>Double drop is now disabled!", false);
            }
            return;
        } else if (player.temporaryAttribute().remove("servermsg") == Boolean.TRUE) {
            World.sendWorldMessage("<col=ff000>Attention: " + Utils.fixChatMessage(value), false);
            return;
        } else if (player.temporaryAttribute().remove("tp_player") == Boolean.TRUE) {
            Player other = World.getPlayerByDisplayName(value);
            if (other == null) {
                player.message("Target offline, or does not exist.");
                return;
            }
            if (other.getControlerManager().getControler() != null) player.getAppearance().switchHidden();
            player.setNextWorldTile(new WorldTile(other.getX(), other.getY(), other.getPlane()));
            return;
        } else if (player.temporaryAttribute().remove("tp_to_me") == Boolean.TRUE) {
            Player other = World.getPlayerByDisplayName(value);
            if (other == null) {
                player.message("Target offline, or does not exist.");
                return;
            }
            if (other.getControlerManager().getControler() != null) {
                player.message("Target is in a controler, you must teleport to them or they must exit.");
                return;
            }
            other.setNextWorldTile(new WorldTile(player.getX(), player.getY(), player.getPlane()));
            return;
        } else if (player.temporaryAttribute().remove("sendhome") == Boolean.TRUE) {
            Player other = World.getPlayerByDisplayName(value);
            if (other == null) {
                player.message("Offline, or does not exist.");
                return;
            }
            other.message(player.getDisplayName() + " has sent you home.");
            other.setNextWorldTile(new WorldTile(Settings.HOME_PLAYER_LOCATION));
            other.getInterfaceManager().sendTabInterfaces(false);
            if (other.getControlerManager().getControler() != null)
                other.getControlerManager().getControler().removeControler();
            other.unlock();
            return;
        } else if (player.temporaryAttribute().remove("report_category") == Boolean.TRUE) {
            Report.category = value;
            player.getPackets().sendInputLongTextScript("Description of bug:");
            player.temporaryAttribute().put("report_bug", Boolean.TRUE);
            return;
        } else if (player.temporaryAttribute().remove("report_bug") == Boolean.TRUE) {
            Report.bug = value;
            player.message("Thankyou! We will investigate your case further.");
            Report.archiveBug(player);
            return;
        } else if (player.temporaryAttribute().remove("change_troll_name") == Boolean.TRUE) {
            value = Utils.formatPlayerNameForDisplay(value);
            if (value.length() < 3 || value.length() > 14) {
                player.getPackets().sendGameMessage("You can't use a name shorter than 3 or longer than 14 characters.");
                return;
            }
            if (value.equalsIgnoreCase("none")) {
                player.getPetManager().setTrollBabyName(null);
            } else {
                player.getPetManager().setTrollBabyName(value);
                if (player.getPet() != null && player.getPet().getId() == Pets.TROLL_BABY.getBabyNpcId()) {
                    player.getPet().setName(value);
                }
            }
            return;
        } else if (player.temporaryAttribute().remove("setdisplay") == Boolean.TRUE) {
            if (Utils.invalidAccountName(Utils.formatPlayerNameForProtocol(value))) {
                player.getPackets().sendGameMessage("Name contains invalid characters or is too short/long.");
                return;
            }
            if (!DisplayNames.setDisplayName(player, value)) {
                player.getPackets().sendGameMessage("This name is already in use.");
                return;
            }
            player.getPackets().sendGameMessage("Your display name was successfully changed.");
            return;
        }

        if (player.getInterfaceManager().containsInterface(1103))
            ClansManager.setClanMottoInterface(player, value);
    }
    public static void handleEnterInteger(Player player, InputStream stream) {
        if (!player.isActive() || player.isDead()) return;
        int value = stream.readInt();

        if (handleQuestionScript(player, value)) return;

        if ((player.getInterfaceManager().containsInterface(762)
                && player.getInterfaceManager().containsInterface(763))
                || player.getInterfaceManager().containsInterface(11)) {
            if (value < 0) return;
            Integer bankItemXSlot = (Integer) player.temporaryAttribute().remove("bank_item_X_Slot");
            if (bankItemXSlot == null) return;
            player.getBank().setLastX(value);
            player.getBank().refreshLastX();
            if (player.temporaryAttribute().remove("bank_isWithdraw") != null)
                player.getBank().withdrawItem(bankItemXSlot, value);
            else
                player.getBank().depositItem(bankItemXSlot, value,
                        !player.getInterfaceManager().containsInterface(11));
            return;
        }

        if (player.getTemporaryAttributtes().get("PRESET_EDIT_SKILL") != null) {
            Integer skillId = (Integer) player.getTemporaryAttributtes().remove("PRESET_EDIT_SKILL");
            int level = value;
            if (player.inPkingArea()) {
                raynna.game.world.util.Msg.warn(player, "You can't change levels in player killing areas.");
                return;
            }
            if (level < 1) level = 1;
            if (level > 99) level = 99;

            Preset preset = PresetInterface.INSTANCE.getSelectedPreset(player);
            if (preset == null) return;

            int[] levels = preset.getLevels();
            int index = switch (skillId) {
                case 0 -> 0;
                case 1 -> 1;
                case 2 -> 2;
                case 3 -> 3;
                case 4 -> 4;
                case 5 -> 5;
                case 6 -> 6;
                case 23 -> 7;
                default -> -1;
            };
            if (index != -1) {
                levels[index] = level;
            }
            PresetInterface.INSTANCE.selectPresetByName(player, preset.getName());
        }

        if (player.getInterfaceManager().containsInterface(934)
                && player.getTemporaryAttributtes().get("FORGE_X") != null) {
            Integer index = (Integer) player.getTemporaryAttributtes().remove("FORGE_X");
            if (index == null) return;
            boolean dungeoneering = false;
            if (index > 100) {
                index -= 100;
                dungeoneering = true;
            }
            player.closeInterfaces();
            player.getActionManager().setAction(new DungeoneeringSmithing(index, value, dungeoneering));
            return;
        }

        if (player.getTemporaryAttributtes().get("SHOP_BUY_X_ITEM") != null) {
            if (value <= 0) return;
            player.getShopSystem().handleBuyXInput(value);
            return;
        }

        if ((player.getInterfaceManager().containsInterface(628)
                && player.getInterfaceManager().containsInterface(631))) {
            if (value <= 0) return;

            if (player.temporaryAttribute().get("duel_addingmoney") != null) {
                player.temporaryAttribute().remove("duel_addingmoney");
                if (player.getControlerManager().getControler() instanceof DuelArena duel) {
                    Player target = duel.target;
                    if (target != null && target.getControlerManager().getControler() instanceof DuelArena) {
                        duel.addPouch(1, value);
                        duel.refresh(1);
                        return;
                    }
                }
            }

            if (player.temporaryAttribute().get("duel_item_X_Slot") != null) {
                Integer slot = (Integer) player.temporaryAttribute().remove("duel_item_X_Slot");
                if (slot == null) return;
                if (player.getControlerManager().getControler() instanceof DuelArena duel) {
                    Player target = duel.target;
                    if (target != null && target.getControlerManager().getControler() instanceof DuelArena) {
                        if (player.temporaryAttribute().remove("duel_isWithdraw") != null)
                            duel.removeItem(slot, value);
                        else
                            duel.addItem(slot, value);
                        duel.refresh(slot);
                        return;
                    }
                }
            }

            player.getDialogueManager().startDialogue("DungExperiencePurchase", value);
            return;
        }

        if (player.temporaryAttribute().get("gambling") == Boolean.TRUE) {
            player.temporaryAttribute().put("gambling", Boolean.FALSE);
            int money = value;
            if (player.getInventory().getNumberOf(995) < money && player.getMoneyPouch().getTotal() < money) {
                player.message("You do not have the money to do that.");
                return;
            }
            GambleTest.Gamble(player, money);
            return;
        }

        if (player.temporaryAttribute().get("unlock_item") == Boolean.TRUE) {
            player.temporaryAttribute().put("unlock_item", Boolean.FALSE);
            if (value <= 0) {
                player.message("Invalid itemId");
                return;
            }
            UnlockableManager.unlockItemForPlayer(player, value);
            return;
        }

        if (player.temporaryAttribute().get("charge_staff") == Boolean.TRUE) {
            player.temporaryAttribute().put("charge_staff", Boolean.FALSE);
            Item item = (Item) player.getTemporaryAttributtes().get("GREATER_RUNIC_STAFF");
            Boolean inventory = (Boolean) player.getTemporaryAttributtes().get("INTERACT_STAFF_FROM_INVENTORY");
            GreaterRunicStaffMetaData data = (GreaterRunicStaffMetaData) item.getMetadata();
            if (value <= 0) value = 1;
            player.getRunicStaff().chargeStaff(value, data.getSpellId(), inventory);
            return;
        }

        if (player.temporaryAttribute().get("serverupdate") == Boolean.TRUE) {
            player.temporaryAttribute().put("serverupdate", Boolean.FALSE);
            if (value > 30 || value <= 0) {
                player.message("Max is 30 minutes.");
                return;
            }
            World.safeRestart(value * 60);
            return;
        }

        if (player.temporaryAttribute().get("doubleexp") == Boolean.TRUE) {
            player.temporaryAttribute().put("doubleexp", Boolean.FALSE);
            if (value == 0) return;
            if (value > 5) {
                player.message("Max is 5. You can't go above that.");
                return;
            }
            if (value == 1 && Settings.BONUS_EXP_WEEK_MULTIPLIER > 1) {
                Settings.BONUS_EXP_WEEK_MULTIPLIER = 1.0;
                World.sendWorldMessage("<img=7><col=ffc000>DXP is no longer active.", false);
                return;
            }
            Settings.BONUS_EXP_WEEK_MULTIPLIER = (double) value;
            World.sendWorldMessage("<img=7><col=ffc000>DXP is now live with a multiplier of " + ((double) value) + "!", false);
            return;
        }

        if (player.temporaryAttribute().get("bankAmount") != null) {
            Integer itemId = (Integer) player.temporaryAttribute().remove("bankAmount");
            if (itemId == null) return;
            int invAmt = player.getInventory().getAmountOf(itemId);
            Item bankedItem = player.getBank().getItem(itemId);
            if (!player.getBank().hasBankSpace()) {
                player.message("Not enough bank space.");
                return;
            }
            if (bankedItem != null && bankedItem.getDefinitions().isNoted()) {
                player.message("You can't bank this item.");
                return;
            }
            if (bankedItem != null && (bankedItem.getAmount() + value <= 0 || bankedItem.getAmount() + invAmt <= 0)) {
                player.message("Not enough space for " + bankedItem.getName() + ".");
                return;
            }
            if (value > invAmt) {
                player.getInventory().deleteItem(itemId, invAmt);
                player.getBank().addItem(itemId, invAmt, true);
                return;
            }
            player.getInventory().deleteItem(itemId, value);
            player.getBank().addItem(itemId, value, true);
            return;
        }

        if (player.temporaryAttribute().get("GEPRICESET") != null) {
            if (value == 0) return;
            player.temporaryAttribute().remove("GEQUANTITYSET");
            player.temporaryAttribute().remove("GEPRICESET");
            player.getGeManager().setPricePerItem(value);
            return;
        }
        if (player.temporaryAttribute().get("GEQUANTITYSET") != null) {
            player.temporaryAttribute().remove("GEPRICESET");
            player.temporaryAttribute().remove("GEQUANTITYSET");
            player.getGeManager().setAmount(value);
            return;
        }

        if (player.temporaryAttribute().get("exp_lamp") != null) {
            player.temporaryAttribute().remove("exp_lamp");
            if (value <= player.getAvalonPoints()) {
                player.setAvalonPoints(player.getAvalonPoints() - value);
                player.getSkills().addXp(Skills.DUNGEONEERING, value);
                player.getInterfaceManager().closeScreenInterface();
            } else {
                player.getInterfaceManager().closeScreenInterface();
                player.getSkills().addXp(Skills.DUNGEONEERING, player.getAvalonPoints());
                player.setAvalonPoints(0);
            }
            return;
        }

        if (player.getInterfaceManager().containsInterface(206)
                && player.getInterfaceManager().containsInterface(207)) {
            if (value < 0) return;
            Integer pcItemXSlot = (Integer) player.temporaryAttribute().remove("pc_item_X_Slot");
            if (pcItemXSlot == null) return;
            if (player.temporaryAttribute().remove("pc_isRemove") != null)
                player.getPriceCheckManager().removeItem(pcItemXSlot, value);
            else
                player.getPriceCheckManager().addItem(pcItemXSlot, value);
            return;
        }

        if (player.getInterfaceManager().containsInterface(672)
                || player.getInterfaceManager().containsInterface(666)) {
            if (value < 0) return;
            if (player.temporaryAttribute().get("infuse_scroll_x") != null) {
                Integer idx = (Integer) player.temporaryAttribute().remove("infuse_scroll_x");
                if (idx == null) return;
                Summoning.handlePouchInfusion(player, idx, value);
            } else {
                Integer idx = (Integer) player.temporaryAttribute().remove("infuse_pouch_x");
                if (idx == null) return;
                Summoning.handlePouchInfusion(player, idx, value);
            }
            return;
        }

        if (player.getInterfaceManager().containsInterface(671)
                && player.getInterfaceManager().containsInterface(665)) {
            if (player.getFamiliar() == null || player.getFamiliar().getBob() == null) return;
            if (value < 0) return;
            Integer bobItemXSlot = (Integer) player.temporaryAttribute().remove("bob_item_X_Slot");
            if (bobItemXSlot == null) return;
            if (player.temporaryAttribute().remove("bob_isRemove") != null)
                player.getFamiliar().getBob().removeItem(bobItemXSlot, value);
            else
                player.getFamiliar().getBob().addItem(bobItemXSlot, value);
            return;
        }

        if (player.getInterfaceManager().containsInterface(403)
                && player.getTemporaryAttributtes().get("PlanksConvert") != null) {
            Sawmill.convertPlanks(player, (Plank) player.getTemporaryAttributtes().remove("PlanksConvert"), value);
            return;
        }
        if (player.getInterfaceManager().containsInterface(902)
                && player.getTemporaryAttributtes().get("PlankMake") != null) {
            Integer type = (Integer) player.getTemporaryAttributtes().remove("PlankMake");
            if (player.getControlerManager().getControler() instanceof SawmillController)
                ((SawmillController) player.getControlerManager().getControler()).cutPlank(type, value);
            return;
        }
        if (player.getInterfaceManager().containsInterface(903)
                && player.getTemporaryAttributtes().get("PlankWithdraw") != null) {
            Integer type = (Integer) player.getTemporaryAttributtes().remove("PlankWithdraw");
            if (player.getControlerManager().getControler() instanceof SawmillController)
                ((SawmillController) player.getControlerManager().getControler()).withdrawFromCart(type, value);
            return;
        }

        if (player.getControlerManager().getControler() != null
                && player.getTemporaryAttributtes().get("SERVANT_REQUEST_ITEM") != null) {
            Integer type = (Integer) player.getTemporaryAttributtes().remove("SERVANT_REQUEST_TYPE");
            Integer item = (Integer) player.getTemporaryAttributtes().remove("SERVANT_REQUEST_ITEM");
            if (!player.getHouse().isLoaded() || !player.getHouse().getPlayers().contains(player) || type == null || item == null)
                return;
            player.getHouse().getServantInstance().requestType(item, value, type.byteValue());
            return;
        }

        if (player.temporaryAttribute().get("trade_item_X_Slot") != null) {
            Integer slot = (Integer) player.temporaryAttribute().get("trade_item_X_Slot");
            player.temporaryAttribute().remove("trade_item_X_Slot");
            if (value < 0) return;
            player.getTrade().addItem(slot, value);
            return;
        }
        if (player.temporaryAttribute().get("trade_removeitem_X_Slot") != null) {
            Integer slot = (Integer) player.temporaryAttribute().get("trade_removeitem_X_Slot");
            player.temporaryAttribute().remove("trade_removeitem_X_Slot");
            if (value < 0) return;
            player.getTrade().removeItem(slot, value);
            return;
        }
        if (player.temporaryAttribute().get("trade_moneypouch_X_Slot") != null) {
            player.temporaryAttribute().remove("trade_moneypouch_X_Slot");
            if (value < 0) return;
            player.getTrade().addPouch(value);
            return;
        }

        if (player.temporaryAttribute().get("skillId") != null) {
            if (player.getEquipment().wearingArmour()) {
                player.getDialogueManager().finishDialogue();
                player.getDialogueManager().startDialogue("SimpleMessage", "You cannot do this while having armour on!");
                return;
            }
            int skillId = (Integer) player.temporaryAttribute().remove("skillId");
            if (skillId == Skills.HITPOINTS && value <= 9) value = 10;
            else if (value < 1) value = 1;
            else if (value > 99) value = 99;
            player.getSkills().set(skillId, value);
            player.getSkills().setXp(skillId, Skills.getXPForLevel(value));
            player.getAppearance().generateAppearenceData();
            player.getDialogueManager().finishDialogue();
            return;
        }

        if (player.temporaryAttribute().get("setLevel") != null) {
            int skillId = (Integer) player.temporaryAttribute().remove("setLevel");
            if (value <= player.getSkills().getRealLevel(skillId)) {
                player.getPackets().sendGameMessage("You can't set a level target lower than your current level.");
                return;
            }
            if (skillId == 24 && value > 120) value = 120;
            if (skillId != 24 && value > 99) value = 99;
            if (value < 1) value = 1;
            player.getSkills().setSkillTarget(true, skillId, value);
            return;
        }

        if (player.temporaryAttribute().get("setXp") != null) {
            int skillId = (Integer) player.temporaryAttribute().remove("setXp");
            if (value <= player.getSkills().getRealLevel(skillId)) {
                player.getPackets().sendGameMessage("You can't set a experience target lower than your current experience.");
                return;
            }
            if (value > 200000000) value = 200000000;
            if (value < 1) value = 1;
            player.getSkills().setSkillTarget(false, skillId, value);
            return;
        }

        if (player.getTemporaryAttributtes().get("SET_DROPVALUE") == Boolean.TRUE) {
            player.getTemporaryAttributtes().remove("SET_DROPVALUE");
            if (value < 0) value = 0;
            player.toggles.put("DROPVALUE", value);
            player.getPackets().sendGameMessage("Drop value set to: "
                    + Utils.getFormattedNumber((Integer) player.toggles.get("DROPVALUE"), ',') + " gp.");
            SettingsTab.open(player);
            return;
        }

        if (player.getTemporaryAttributtes().get("SET_TITLE") == Boolean.TRUE) {
            player.getTemporaryAttributtes().remove("SET_TITLE");
            if (value < 1) value = 0;
            if (value == 0) value = -1;
            if (value > 58 && value != 65535) value = 58;
            player.getAppearance().setTitle(value);
            player.getAppearance().generateAppearenceData();
            player.getPackets().sendGameMessage("Title set to: " + player.getAppearance().getTitleName());
            JournalTab.open(player);
            return;
        }

        if (player.temporaryAttribute().get("money_pouch_remove") == Boolean.TRUE) {
            player.message("withdraw cash");
            player.getMoneyPouch().withdrawPouch(value);
            player.temporaryAttribute().put("money_pouch_remove", Boolean.FALSE);
        }
    }
    public static void handleEnterName(Player player, InputStream stream) {
        if (!player.isActive() || player.isDead()) return;
        String value = stream.readString();
        if (value.equals("")) return;

        if (player.temporaryAttribute().get("PUNISH_NAME") == Boolean.TRUE) {
            if (World.getPlayer(value) == null) {
                value = Utils.formatPlayerNameForProtocol(value);
                if (!AccountCreation.exists(value)) {
                    player.getPackets().sendGameMessage("No such account named " + value + " was found in the database.");
                } else {
                    player.getDialogueManager().startDialogue("Punish", value, false);
                }
                player.temporaryAttribute().put("PUNISH_NAME", Boolean.FALSE);
                return;
            }
            Player target = World.getPlayerByDisplayName(value);
            try {
                player.getDialogueManager().startDialogue("Punish", target, true);
            } catch (Exception e) {
                player.getPackets().sendGameMessage(Utils.formatPlayerNameForDisplay(value) + " wasn't found.");
            }
            player.temporaryAttribute().put("PUNISH_NAME", Boolean.FALSE);
            return;
        }

        if (player.getInterfaceManager().containsInterface(1108)) {
            player.getFriendsIgnores().setChatPrefix(value);
            return;
        } else if (player.temporaryAttribute().remove("setclan") != null) {
            ClansManager.createClan(player, value);
            return;
        } else if (player.temporaryAttribute().remove("joinguestclan") != null) {
            ClansManager.connectToClan(player, value, true);
            return;
        } else if (player.temporaryAttribute().remove("banclanplayer") != null) {
            ClansManager.banPlayer(player, value);
            return;
        } else if (player.temporaryAttribute().remove("unbanclanplayer") != null) {
            ClansManager.unbanPlayer(player, value);
            return;
        } else if (player.getTemporaryAttributtes().remove("enterhouse") == Boolean.TRUE) {
            House.enterHouse(player, value);
            return;
        } else if (player.getTemporaryAttributtes().remove("DUNGEON_INVITE") == Boolean.TRUE) {
            player.getDungManager().invite(value);
            return;
        } else if (player.temporaryAttribute().get("TITLE_COLOR_SET") != null) {
            String input = value.toLowerCase().trim();
            String presetHex = Colors.getHexByName(input);
            if (presetHex != null) {
                player.setCustomTitleColour(presetHex.toLowerCase());
                player.setCustomTitle(player.getCustomTitle());
                player.getAppearance().generateAppearenceData();
                JournalTab.open(player);
                player.message("Set your title colour to: <col=" + presetHex + ">COLOUR");
            } else {
                String hex = input;
                if (hex.startsWith("#")) {
                    hex = hex.substring(1);
                }
                if (!hex.matches("[0-9a-fA-F]{6}")) {
                    player.getDialogueManager().startDialogue("SimpleMessage", "Enter a valid 6-digit HEX or preset (red, blue, darkred, etc).");
                    return;
                }
                player.setCustomTitleColour(hex.toLowerCase());
                player.setCustomTitle(player.getCustomTitle());
                player.getAppearance().generateAppearenceData();
                JournalTab.open(player);
                player.message("Set your title colour to: <col=" + hex + ">COLOUR");
            }
            player.getPackets().sendRunScript(109, "Would you like the title infront or behind your name? Front/Back");
            player.temporaryAttribute().put("TITLE_ORDER_SET", Boolean.TRUE);
            player.temporaryAttribute().remove("CUSTOM_TITLE_SET");
            player.temporaryAttribute().remove("TITLE_COLOR_SET");
            return;
        } else if (player.temporaryAttribute().get("npc_find") != null) {
            player.temporaryAttribute().remove("npc_find");
            DropInterface.INSTANCE.open(player, true);
            List<DropTableSource> results = DropSearch.INSTANCE.findSourcesByName(value);
            player.temporaryAttribute().put("drop_viewer_found_npcs", results);
            player.temporaryAttribute().put("drop_viewer_npc_page", 0);
            player.temporaryAttribute().put("drop_viewer_in_search", true);
            player.temporaryAttribute().put("drop_viewer_source_filter", value);
            DropInterface.INSTANCE.sendSourceList(player);
            if (!results.isEmpty())
                DropInterface.INSTANCE.selectSource(player, results.get(0));
            return;
        } else if (player.temporaryAttribute().get("drop_find") != null) {
            player.temporaryAttribute().remove("drop_find");
            List<DropTableSource> results = DropSearch.INSTANCE.findSourcesByDrop(value);
            player.temporaryAttribute().put("drop_viewer_found_npcs", results);
            player.temporaryAttribute().put("drop_viewer_npc_page", 0);
            player.temporaryAttribute().put("drop_viewer_in_search", true);
            player.temporaryAttribute().put("drop_viewer_item_filter", value);
            DropInterface.INSTANCE.sendSourceList(player);
            if (!results.isEmpty())
                DropInterface.INSTANCE.selectSource(player, results.getFirst());
            return;
        } else if (player.temporaryAttribute().get("TITLE_ORDER_SET") != null) {
            if (value.toLowerCase().contains("back") || value.equalsIgnoreCase("b")) {
                player.titleIsBehindName = true;
                player.getAppearance().setTitle(901);
                player.message("Set your title order to the back.");
            } else if (value.toLowerCase().contains("front") || value.equalsIgnoreCase("f")) {
                player.titleIsBehindName = false;
                player.getAppearance().setTitle(900);
                player.message("Set your title order to the front.");
            }
            player.getDialogueManager().startDialogue("SimpleMessage", "The process was successfully done!");
            if (player.titleIsBehindName) {
                player.getAppearance().setTitle(901);
            } else {
                player.getAppearance().setTitle(900);
            }
            player.getAppearance().generateAppearenceData();
            JournalTab.open(player);
            player.temporaryAttribute().remove("CUSTOM_TITLE_SET");
            player.temporaryAttribute().remove("TITLE_COLOR_SET");
            player.temporaryAttribute().remove("TITLE_ORDER_SET");
            return;
        } else if (player.temporaryAttribute().get("CUSTOM_TITLE_SET") != null) {
            try {
                int titleId = Integer.parseInt(value);
                if (titleId >= 0 && titleId <= 58) {
                    if (titleId == 0)
                        titleId = -1;
                    player.getAppearance().setTitle(titleId);
                    player.getAppearance().generateAppearenceData();
                    player.getPackets().sendGameMessage("Title set to: " + player.getAppearance().getTitleName());
                    JournalTab.open(player);
                    return;
                }
            } catch (NumberFormatException ignored) {
            }
            String[] invalid = {">", "<", "_", "donator", "superdonator", "member", "mod", "admin", "owner", "jagex", "developer", "recruit"};
            if (value.length() > 10) {
                player.getDialogueManager().startDialogue("SimpleMessage", "Titles are limted to ten characters due to spam.");
                return;
            }
            for (String s : invalid) {
                if (value.toLowerCase().contains(s)) {
                    player.getDialogueManager().startDialogue("SimpleMessage", "You cannot use this in your title.");
                    player.temporaryAttribute().remove("CUSTOM_TITLE_SET");
                    return;
                }
            }
            player.setCustomTitle(value);
            player.getAppearance().setTitle(900);
            player.message("Set your title to: <col=" + player.getCustomTitleColour() + ">" + value);
            JournalTab.open(player);
            player.getPackets().sendRunScript(109, "Enter a color (HEX) or preset: " + Colors.getPresetList());
            player.temporaryAttribute().put("TITLE_COLOR_SET", Boolean.TRUE);
            player.temporaryAttribute().remove("CUSTOM_TITLE_SET");
            player.temporaryAttribute().remove("TITLE_ORDER_SET");
            return;
        } else if (player.temporaryAttribute().get("PRESET_SAVE_PROMPT") == Boolean.TRUE) {
            player.temporaryAttribute().remove("PRESET_SAVE_PROMPT");
            String[] invalid = {">", "<"};
            for (String s : invalid) {
                if (value.toLowerCase().contains(s)) {
                    player.getDialogueManager().startDialogue("SimpleMessage", "You cannot use < or > in preset names.");
                    return;
                }
            }
            player.getPresetManager().savePreset(value);
            boolean fromBank = Boolean.TRUE.equals(player.getTemporaryAttributtes().get("preset_opened_from_bank"));
            PresetInterface.INSTANCE.open(player, fromBank);
            PresetInterface.INSTANCE.selectPresetByName(player, value);
        } else if (player.temporaryAttribute().get("SAVESETUP") == Boolean.TRUE) {
            player.temporaryAttribute().remove("SAVESETUP");
            player.getPresetManager().savePreset(value);
            GearTab.refresh(player);
            return;
        }
        if (player.getTemporaryAttributtes().get("preset_rename_target") != null) {
            String oldName = (String) player.getTemporaryAttributtes().remove("preset_rename_target");
            String[] invalid = {">", "<"};
            for (String s : invalid) {
                if (value.toLowerCase().contains(s)) {
                    player.getDialogueManager().startDialogue("SimpleMessage", "You cannot use < or > in preset names.");
                    return;
                }
            }
            String newName = value;
            if (newName.trim().isEmpty()) {
                player.message("Invalid name.");
                return;
            }
            newName = newName.toLowerCase();
            if (player.getPresetManager().PRESET_SETUPS.containsKey(newName)) {
                player.message("A preset with that name already exists.");
                return;
            }
            Preset preset = player.getPresetManager().PRESET_SETUPS.remove(oldName.toLowerCase());
            if (preset == null) {
                player.message("Preset not found.");
                return;
            }
            preset.setName(newName);
            player.getPresetManager().PRESET_SETUPS.put(newName, preset);
            player.message("Preset renamed to " + newName + ".");
            boolean fromBank = Boolean.TRUE.equals(player.getTemporaryAttributtes().get("preset_opened_from_bank"));
            PresetInterface.INSTANCE.open(player, fromBank);
            PresetInterface.INSTANCE.selectPresetByName(player, newName);
            return;
        } else if (player.temporaryAttribute().get("RENAME_SETUP") == Boolean.TRUE) {
            player.temporaryAttribute().remove("RENAME_SETUP");
            Integer selectedGear = (Integer) player.getTemporaryAttributtes().get("SELECTED_RENAME");
            if (selectedGear != null) {
                String keyToRename = null;
                Preset presetToRename = null;
                for (Map.Entry<String, Preset> entry : player.getPresetManager().PRESET_SETUPS.entrySet()) {
                    if (entry.getValue().getId(player) == selectedGear) {
                        keyToRename = entry.getKey();
                        presetToRename = entry.getValue();
                        break;
                    }
                }
                if (keyToRename != null && presetToRename != null) {
                    player.getPresetManager().PRESET_SETUPS.remove(keyToRename);
                    presetToRename.setName(value);
                    player.getPresetManager().PRESET_SETUPS.put(value, presetToRename);
                    player.getTemporaryAttributtes().remove("SELECTED_RENAME");
                    player.getPackets().sendGameMessage("Preset \"" + keyToRename + "\" renamed to \"" + value + "\".");
                    GearTab.refresh(player);
                    if (player.getInterfaceManager().containsInterface(PresetInterface.INTERFACE_ID)) {
                        PresetInterface.INSTANCE.selectPresetByName(player, value);
                    }
                } else {
                    player.getPackets().sendGameMessage("Could not find the preset to rename.");
                }
            } else {
                player.getPackets().sendGameMessage("No preset selected to rename.");
            }
            return;
        } else if (player.temporaryAttribute().get("OTHERPRESET") == Boolean.TRUE) {
            player.temporaryAttribute().remove("OTHERPRESET");
            String otherName = Utils.formatPlayerNameForDisplay(value);
            Player p2 = World.getPlayerByDisplayName(otherName);
            if (p2 != null) {
                player.getTemporaryAttributtes().put("OTHERPRESET_NAME", otherName);
                GearTab.open(player, otherName);
                player.getPackets().sendGameMessage("Viewing " + otherName + " presets.");
            } else {
                if (!AccountCreation.exists(otherName)) {
                    player.getPackets().sendGameMessage("Account name " + Utils.formatPlayerNameForDisplay(otherName) + " doesn't exist.");
                    GearTab.open(player, null);
                } else {
                    AccountCreation.loadPlayer(otherName);
                    player.getTemporaryAttributtes().put("OTHERPRESET_NAME", otherName);
                    GearTab.open(player, otherName);
                    player.getPackets().sendGameMessage("Viewing " + otherName + " presets.");
                }
            }
            return;
        } else if (player.temporaryAttribute().get("AdventureLog") == Boolean.TRUE) {
            Player other = AccountCreation.loadPlayer(Utils.formatPlayerNameForProtocol(value));
            if (other == null) {
                player.getDialogueManager().startDialogue("SimpleMessage", "This player was not found in our database.");
                return;
            }
            player.getAdventureLog().OpenAdventureLog(other, value);
            player.temporaryAttribute().put("AdventureLog", false);
            return;
        } else if (player.temporaryAttribute().get("muting_reason") == Boolean.TRUE) {
            player.temporaryAttribute().remove("muting_reason");
            player.message("Value: " + value);
            return;
        } else if (player.temporaryAttribute().remove("entering_note") == Boolean.TRUE) {
            player.getNotes().add(value);
            return;
        } else if (player.temporaryAttribute().remove("editing_note") == Boolean.TRUE) {
            player.getNotes().edit(value);
            return;
        } else if (player.temporaryAttribute().get("view_name") == Boolean.TRUE) {
            player.temporaryAttribute().remove("view_name");
            Player other = World.getPlayerByDisplayName(value);
            if (other == null) {
                player.getPackets().sendGameMessage("Couldn't find player.");
                return;
            }
            ClanWars clan = other.getCurrentFriendChat() != null ? other.getCurrentFriendChat().getClanWars() : null;
            if (clan == null) {
                player.getPackets().sendGameMessage("This player's clan is not in war.");
                return;
            }
            if (clan.getSecondTeam().getOwnerDisplayName() != other.getCurrentFriendChat().getOwnerDisplayName()) {
                player.temporaryAttribute().put("view_prefix", 1);
            }
            player.temporaryAttribute().put("view_clan", clan);
            ClanWars.enter(player);
            return;
        } else if (player.temporaryAttribute().remove("setdisplay") != null) {
            DisplayNames.setDisplayName(player, value);
            return;
        } else if (player.temporaryAttribute().remove("VERIFY_PASSWORD") == Boolean.TRUE) {
            value = value.trim();
            String encrypted = Encrypt.hashPassword(value);
            if (!encrypted.equals(player.getPassword())) {
                player.getPackets().sendGameMessage("Incorrect current password.");
                return;
            }
            player.temporaryAttribute().put("SET_NEW_PASSWORD", Boolean.TRUE);
            player.getPackets().sendInputNameScript("Enter your new password:");
            return;
        } else if (player.temporaryAttribute().remove("SET_NEW_PASSWORD") == Boolean.TRUE) {
            value = value.trim();
            if (value.length() < 5 || value.length() > 15) {
                player.getPackets().sendGameMessage("Password length is limited to 5-15 characters.");
                return;
            }
            player.setPassword(Encrypt.hashPassword(value));
            player.getPackets().sendGameMessage("Your password has been changed successfully.");
            return;
        } else if (player.temporaryAttribute().remove("SETUSERNAME") != null) {
            DisplayNames.queueUsernameChange(player, value);
            JournalTab.open(player);
            return;
        }

        if (player.getInterfaceManager().containsInterface(1103))
            ClansManager.setClanMottoInterface(player, value);
    }
}
