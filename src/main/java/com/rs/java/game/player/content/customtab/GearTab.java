package com.rs.java.game.player.content.customtab;

import java.util.Map.Entry;

import com.rs.java.game.World;
import com.rs.java.game.player.AccountCreation;
import com.rs.java.game.player.Player;
import com.rs.java.game.player.content.presets.Preset;
import com.rs.java.utils.Utils;

public class GearTab extends CustomTab {


	private static int EQUIPMENT_SPRITE = 675, CLOSE_SPRITE = 8553, ADD_SPRITE = 1842, REMOVE_SPRITE = 1845, SEARCH_SPRITE = 8486;

	public static void refresh(Player player) {
		int i = 3;
		player.getTemporaryAttributtes().put("CUSTOMTAB", 3);
		player.getTemporaryAttributtes().remove("ACHIEVEMENTTAB");
		player.getTemporaryAttributtes().remove("SELECTEDGEAR");
		player.getPackets().sendTextOnComponent(3002, 25, "Gear Setups");
		player.getPackets().sendHideIComponent(3002, PURPLE_STAR_COMP, false);
		player.getPackets().sendHideIComponent(3002, YELLOW_STAR_COMP, false);
		player.getPackets().sendIComponentSprite(3002, PURPLE_STAR_COMP, "sprite.add_note");
		player.getPackets().sendIComponentSprite(3002, YELLOW_STAR_COMP, "sprite.remove_note");
		//player.getPackets().sendIComponentSprite(3002, PURPLE_STAR_COMP, "");//1842
		//player.getPackets().sendIComponentSprite(3002, YELLOW_STAR_COMP, "");//1845
		for (Entry<String, Preset> gear : player.getPresetManager().PRESET_SETUPS.entrySet()) {
			if (gear != null) {
				player.getPackets().sendHideIComponent(3002, i, false);
				player.getPackets().sendTextOnComponent(3002, i, (gear.getKey()) + "");
			}
			i++;
		}
	}

	public static void open(Player player, String name) {
		String otherName = Utils.formatPlayerNameForDisplay(name);
		Player p2 = World.getPlayerByDisplayName(otherName);
		if (p2 == null && name != null)
			p2 = AccountCreation.loadPlayer(otherName);
		for (int i = 3; i <= 22; i++) {
			player.getPackets().sendHideIComponent(3002, i, true);
		}
		for (int i = 28; i <= 56; i++) {
			player.getPackets().sendHideIComponent(3002, i, true);
		}
		if (p2 == null) {
			player.getTemporaryAttributtes().remove("OTHERPRESET_NAME");
		}
		player.getPackets().sendHideIComponent(3002, 24, true);
		player.getTemporaryAttributtes().put("CUSTOMTAB", 3);
		player.getTemporaryAttributtes().remove("ACHIEVEMENTTAB");
		player.getTemporaryAttributtes().remove("SELECTEDGEAR");
		player.getPackets().sendHideIComponent(3002, BACK_BUTTON, false);
		player.getPackets().sendHideIComponent(3002, FORWARD_BUTTON, true);
		player.getPackets().sendHideIComponent(3002, BLUE_STAR_COMP, false);
		player.getPackets().sendIComponentSprite(3002, BLUE_STAR_COMP, "sprite.leave_door");// 9747
		player.getPackets().sendHideIComponent(3002, GREEN_STAR_COMP, false);
		//player.getPackets().sendIComponentSprite(3002, RED_STAR_COMP, EQUIPMENT_SPRITE);
		player.getPackets().sendIComponentSprite(3002, RED_STAR_COMP, "sprite.add_to_bag");
		player.getPackets().sendHideIComponent(3002, RED_STAR_COMP, true);
		player.getPackets().sendHideIComponent(3002, YELLOW_STAR_COMP, true);
		if (p2 == null) {
			player.getPackets().sendHideIComponent(3002, PURPLE_STAR_COMP, false);
			player.getPackets().sendHideIComponent(3002, YELLOW_STAR_COMP, true);
			player.getPackets().sendIComponentSprite(3002, PURPLE_STAR_COMP, "sprite.add_note");
			player.getPackets().sendIComponentSprite(3002, YELLOW_STAR_COMP, "sprite.remove_note");
			//player.getPackets().sendIComponentSprite(3002, PURPLE_STAR_COMP, ADD_SPRITE);
			//player.getPackets().sendIComponentSprite(3002, YELLOW_STAR_COMP, REMOVE_SPRITE);
		} else {
			player.getPackets().sendHideIComponent(3002, GREEN_STAR_COMP, true);
			player.getPackets().sendHideIComponent(3002, PURPLE_STAR_COMP, true);
			player.getPackets().sendHideIComponent(3002, YELLOW_STAR_COMP, true);
		}
		player.getPackets().sendIComponentSprite(3002, GREEN_STAR_COMP, "sprite.search");
		if (p2 != null)
			player.getPackets().sendTextOnComponent(3002, 25, otherName + "<br> Presets");
		else
			player.getPackets().sendTextOnComponent(3002, 25, "Gear Setups");
		player.getTemporaryAttributtes().remove("CONFIRM_OVERWRITE");
		int i = 3;
		for (Entry<String, Preset> gear : p2 != null ? p2.getPresetManager().PRESET_SETUPS.entrySet()
				: player.getPresetManager().PRESET_SETUPS.entrySet()) {
			if (gear != null) {
				player.getPackets().sendHideIComponent(3002, i, false);
				player.getPackets().sendTextOnComponent(3002, i, (gear.getKey()) + "");
			}
			i++;
		}
	}

	public static void removeAttributtes(Player player) {
		player.getTemporaryAttributtes().remove("CONFIRM_OVERWRITE");
		player.getTemporaryAttributtes().remove("CONFIRM_DELETE");
		player.getTemporaryAttributtes().remove("RENAME_SETUP");
		//player.getDialogueManager().finishDialogue();
	}

	public static void handleButtons(Player player, String name, int compId) {
		String otherName = Utils.formatPlayerNameForDisplay(name);
		Player p2 = World.getPlayerByDisplayName(otherName);
		if (p2 == null && name != null)
			p2 = AccountCreation.loadPlayer(otherName);
		if (compId == 62) {
			if (p2 != null)
				open(player, null);
			else
				SettingsTab.open(player);
			return;
		}
		Integer selectedGear = (Integer) player.getTemporaryAttributtes().get("SELECTEDGEAR");
		if (compId == 61) {
			Integer selectedGearId = (Integer) player.getTemporaryAttributtes().get("SELECTEDGEAR");
			if (selectedGearId != null) {
				for (Entry<String, Preset> gear : p2 != null ? p2.getPresetManager().PRESET_SETUPS.entrySet()
						: player.getPresetManager().PRESET_SETUPS.entrySet()) {
					if (gear != null && gear.getValue().getId(p2 != null ? p2 : player) == selectedGearId) {
						player.getTemporaryAttributtes().put("RENAME_SETUP", true);
						player.getTemporaryAttributtes().put("SELECTED_RENAME", selectedGearId);
						player.getPackets().sendRunScript(109, "Enter new setup name: ");
						return;
					}
				}
			} else {
				player.temporaryAttribute().remove("SAVESETUP");
				player.temporaryAttribute().put("OTHERPRESET", true);
				player.getPackets().sendRunScript(109, "Search for other players presets: ");
			}
			return;
		}
		if (compId == 60) {
			if (selectedGear != null) {
				for (Entry<String, Preset> gear : p2 != null ? p2.getPresetManager().PRESET_SETUPS.entrySet()
						: player.getPresetManager().PRESET_SETUPS.entrySet()) {
					if (gear != null) {
						if (gear.getValue().getId(p2 != null ? p2 : player) == selectedGear) {
							player.getPresetManager().loadPreset(gear.getKey(), p2 != null ? p2 : null);
							if (p2 != null)
								open(player, null);
							else
								refresh(player);
							return;
						}
					}
				}
			} else {
				player.getPackets().sendGameMessage("You don't have any gear setup selected.");
				return;
			}
		}
		if (compId == 59) {
			if (selectedGear != null) {
				Boolean confirm = (Boolean) player.getTemporaryAttributtes().get("CONFIRM_OVERWRITE");
				if (confirm != null && confirm) {
					String keyToOverwrite = null;
					for (Entry<String, Preset> gear : player.getPresetManager().PRESET_SETUPS.entrySet()) {
						if (gear != null && gear.getValue().getId(player) == selectedGear) {
							keyToOverwrite = gear.getKey();
							break;
						}
					}

					if (keyToOverwrite != null) {
						player.getPresetManager().removePreset(keyToOverwrite);

						player.getPresetManager().savePreset(keyToOverwrite);

						player.getPackets().sendGameMessage("Preset \"" + keyToOverwrite + "\" has been overwritten.");
						open(player, null);
						player.getTemporaryAttributtes().remove("SELECTEDGEAR");
						player.getTemporaryAttributtes().remove("CONFIRM_OVERWRITE");
						return;
					} else {
						player.getPackets().sendGameMessage("Could not find the preset to overwrite.");
						player.getTemporaryAttributtes().remove("CONFIRM_OVERWRITE");
						return;
					}
				} else {
					player.getPackets().sendGameMessage("Are you sure you want to overwrite this preset? Click <img=14> to confirm.");
					player.getPackets().sendIComponentSprite(3002, PURPLE_STAR_COMP, "sprite.green_checkmark_2");
					player.getTemporaryAttributtes().put("CONFIRM_OVERWRITE", true);
					return;
				}
			} else {
				player.temporaryAttribute().remove("OTHERPRESET");
				player.temporaryAttribute().put("SAVESETUP", true);
				player.getPackets().sendRunScript(109, "Enter setup name: ");
			}
		}

		if (compId == 26) {
			if (selectedGear != null) {
				Boolean confirmDelete = (Boolean) player.getTemporaryAttributtes().get("CONFIRM_DELETE");
				if (confirmDelete != null && confirmDelete) {
					for (Entry<String, Preset> gear : player.getPresetManager().PRESET_SETUPS.entrySet()) {
						if (gear != null && gear.getValue().getId(player) == selectedGear) {
							player.getPresetManager().removePreset(gear.getKey());
							player.getPackets().sendGameMessage("Preset \"" + gear.getKey() + "\" has been deleted.");
							open(player, null);
							player.getTemporaryAttributtes().remove("SELECTEDGEAR");
							player.getTemporaryAttributtes().remove("CONFIRM_DELETE");
							return;
						}
					}
					player.getPackets().sendGameMessage("Could not find the preset to delete.");
					player.getTemporaryAttributtes().remove("CONFIRM_DELETE");
				} else {
					// first click = ask for confirmation
					player.getPackets().sendGameMessage("Are you sure you want to delete this preset? Click <img=14> to confirm.");
					player.getPackets().sendIComponentSprite(3002, YELLOW_STAR_COMP, "sprite.green_checkmark_2");
					player.getTemporaryAttributtes().put("CONFIRM_DELETE", true);
				}
				return;
			} else {
				player.getPackets().sendGameMessage("You don't have any gear setup selected.");
				return;
			}
		}

		int i = 3;
		for (Entry<String, Preset> gear : p2 != null ? p2.getPresetManager().PRESET_SETUPS.entrySet()
				: player.getPresetManager().PRESET_SETUPS.entrySet()) {

			if (gear != null) {
				player.getPackets().sendTextOnComponent(3002, i, gear.getKey());

				if (compId == i) {
					int gearId = gear.getValue().getId(p2 != null ? p2 : player);

					// already selected? -> unselect it
					if (selectedGear != null && selectedGear == gearId) {
						player.getTemporaryAttributtes().remove("SELECTEDGEAR");
						player.getPackets().sendTextOnComponent(3002, i, gear.getKey()); // remove highlight
						player.getPackets().sendIComponentSprite(3002, GREEN_STAR_COMP, "sprite.search");
						player.getPackets().sendHideIComponent(3002, RED_STAR_COMP, true);
						player.getPackets().sendHideIComponent(3002, YELLOW_STAR_COMP, true);
						player.getPackets().sendIComponentSprite(3002, PURPLE_STAR_COMP, "sprite.add_note");
						removeAttributtes(player);

						// not selected? -> select it
					} else {
						player.getTemporaryAttributtes().put("SELECTEDGEAR", gearId);
						player.getPackets().sendTextOnComponent(3002, i, gear.getKey() + "<img=12>");
						player.getPackets().sendIComponentSprite(3002, GREEN_STAR_COMP, "sprite.edit_note");
						player.getPackets().sendHideIComponent(3002, RED_STAR_COMP, false);
						player.getPackets().sendHideIComponent(3002, YELLOW_STAR_COMP, false);
						player.getPackets().sendIComponentSprite(3002, PURPLE_STAR_COMP, "sprite.out_of_bag");
						player.getPackets().sendIComponentSprite(3002, YELLOW_STAR_COMP, "sprite.remove_note");
						removeAttributtes(player);
					}
				}
				i++;
			}
		}

		switch (compId) {
		case BACK_BUTTON:
			SettingsTab.open(player);
			break;
		case FORWARD_BUTTON:
			QuestTab.open(player);
			break;
		default:
			break;
		}
	}

}
