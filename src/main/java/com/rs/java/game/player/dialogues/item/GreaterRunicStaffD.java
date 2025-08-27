package com.rs.java.game.player.dialogues.item;

import com.rs.java.game.item.Item;
import com.rs.java.game.item.meta.GreaterRunicStaffMetaData;
import com.rs.java.game.player.dialogues.Dialogue;

public class GreaterRunicStaffD extends Dialogue {


	@Override
	public void start() {
		stage = 0;
		sendOptionsDialogue("How many casts do you with to charge?", "Store 10", "Store 100", "Store X");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		Item item = (Item) player.getTemporaryAttributtes().get("GREATER_RUNIC_STAFF");
		Boolean inventory = (Boolean) player.getTemporaryAttributtes().get("INTERACT_STAFF_FROM_INVENTORY");
		GreaterRunicStaffMetaData data = (GreaterRunicStaffMetaData) item.getMetadata();
		switch (stage) {
		case 0:
			stage = -1;
			if (componentId == OPTION_1) {
				end();
				player.getRunicStaff().chargeStaff(10, data.getSpellId(), inventory);
			} else if (componentId == OPTION_2) {
				end();
				player.getRunicStaff().chargeStaff(100, data.getSpellId(), inventory);
			} else if (componentId == OPTION_3) {
				end();
				player.temporaryAttribute().put("charge_staff", Boolean.TRUE);
				player.getPackets().sendRunScript(108, new Object[] { "Enter how many you want to charge" });
			}
			break;
		case -1:
			end();
			break;
		default:
			end();
			break;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
