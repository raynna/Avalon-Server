package com.rs.java.game.player.dialogues;

import java.util.TimerTask;

import com.rs.core.thread.CoresManager;
import com.rs.java.game.WorldTile;
import com.rs.java.game.player.controlers.FightKiln;
import com.rs.java.game.player.cutscenes.Cutscene;
import com.rs.java.utils.Logger;

public class TokHaarHok extends Dialogue {

	private int npcId;
	private int type;
	private FightKiln fightKiln;

	/// So...you accept our challenge. Let our sport be glorious. Xill - attack!
	@Override
	public void start() {
		type = (Integer) parameters[0];
		npcId = (Integer) parameters[1];
		fightKiln = (FightKiln) parameters[2];
		if (type == 0)
			sendNPCDialogue(npcId, 9827, "Let us talk...");
		else if (type == 1)
			sendNPCDialogue(npcId, 9827, "So...you accept our challenge. Let our sport be glorious. Xill - attack!");
		else if (type == 2)
			sendNPCDialogue(npcId, 9827, "Well fought, " + player.getDisplayName()
					+ ". You are ferocious, but you must fight faster... The lava is rising.");
		else if (type == 3)
			sendNPCDialogue(npcId, 9827,
					"You must be carved from a rock inpervious to magic... You are quite the worthy foe.");
		else if (type == 4)
			sendNPCDialogue(npcId, 9827,
					"Hurry, " + player.getDisplayName() + "... Kill my brothers before the lava consumes you.");
		else if (type == 7)
			sendNPCDialogue(npcId, 9827,
					"Amazing! We haven't had such fun in such a long time. But now, the real challenge begins...");
		else if (type == 5)
			sendNPCDialogue(npcId, 9827,
					"We have thrown many waves at you... You have handled yourself like a true Tokhaar. You have earned our respect.");
		else if (type == 6)
			sendNPCDialogue(npcId, 9827,
					"You are a Tokhaar... born in a human's body. Truly, we have not seen such skill from anyone out of our kiln.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (type) {
		case 0: // TODO
			switch (stageInt) {
			case -1:
				stageInt = 0;
				sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Let's fight.",
						"I'd like to speak more about you and your kind.");
				break;
			case 0:
				switch (componentId) {
				case OPTION_1:
					stageInt = 1;
					sendNPCDialogue(npcId, 9827, "Do you have any questions on the rules of our engagement?");
					break;
				case OPTION_2:
				default:

					break;
				}
				break;
			case 1:
				stageInt = 2;
				sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "No, let's just fight.", "What do I get if I beat you?",
						"What are the rules?");
				break;
			case 2:
				switch (componentId) {
				case OPTION_1:
					stageInt = 3;
					sendPlayerDialogue(9827, "No let's just fight.");
					break;
				case OPTION_2:
					break;
				case OPTION_3:
				default:
					break;
				}
				break;
			case 3:
				fightKiln.removeTokHaarTok();
				fightKiln.nextWave();
				end();
				break;
			}
			break;
		case 1:
			end();
			break;
		case 2:
			switch (stageInt) {
			case -1:
				stageInt = 0;
				player.getInterfaceManager().closeChatBoxInterface();
				WorldTile lookTo = fightKiln.getWorldTile(37, 50);
				player.getPackets().sendCameraLook(Cutscene.getX(player, lookTo.getX()),
						Cutscene.getY(player, lookTo.getY()), 1000);
				WorldTile posTile = fightKiln.getWorldTile(37, 45);
				player.getPackets().sendCameraPos(Cutscene.getX(player, posTile.getX()),
						Cutscene.getY(player, posTile.getY()), 3000);
				CoresManager.fastExecutor.schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							sendNPCDialogue(npcId, 9827, "Our Mej wish to test you...");
						} catch (Throwable e) {
							Logger.handle(e);
						}
					}
				}, 3000);
				break;
			case 0:
				end();
				break;
			}
			break;
		case 3:
			switch (stageInt) {
			case -1:
				stageInt = 0;
				sendNPCDialogue(npcId, 9827,
						"Ah, the platform is crumbling. Be quick little one - our Ket are comming.");
				break;
			case 0:
				end();
				break;
			}
			break;
		case 4:
			end();
			break;
		case 7:
			switch (stageInt) {
			case -1:
				stageInt = 0;
				sendPlayerDialogue(9810, "The real challenge?");
				break;
			case 0:
				stageInt = 1;
				sendNPCDialogue(npcId, 9827,
						"Many creatures have entered the kiln over the ages. We remember their shapes.");
				break;
			case 1:
				end();
				break;
			}
			break;
		case 5:
			switch (stageInt) {
			case -1:
				stageInt = 0;
				sendNPCDialogue(npcId, 9827, "	Take this cape as a symbol of our -");
				break;
			case 0:
				stageInt = 1;
				fightKiln.showHarAken();
				player.getInterfaceManager().closeChatBoxInterface();
				CoresManager.fastExecutor.schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							sendNPCDialogue(npcId, 9827, "Ah - yes, there is one final challenge...");
						} catch (Throwable e) {
							Logger.handle(e);
						}
					}
				}, 3000);
				break;
			case 1:
				end();
				fightKiln.hideHarAken();
				CoresManager.fastExecutor.schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							fightKiln.removeScene();
						} catch (Throwable e) {
							Logger.handle(e);
						}
					}
				}, 3000);
				break;
			}
			break;

		case 6:
			switch (stageInt) {
			case -1:
				stageInt = 0;
				sendNPCDialogue(npcId, 9827,
						"You have done very well. To mark your triumph, accept a trophy from our home.");
				break;
			case 0:
				stageInt = 1;
				sendOptionsDialogue("Choose your reward:", "The TokHaar-Kal", "An fury amulet");
				break;
			case 1:
				if (componentId == OPTION_1) {
					stageInt = 2;
					sendNPCDialogue(npcId, 9827,
							"The TokHaar-Kal is a powerful cape that will let others see that you have mastered the Fight Kiln. In addition to this, it provides several stat boosts including 8+ strength.");
				} else {
					stageInt = 3;
					sendNPCDialogue(npcId, 9827, "Amulet of Fury.");
				}
				break;
			case 2:
			case 3:
				stageInt = (int) (stageInt == 2 ? 4 : 5);
				sendOptionsDialogue("Accept the " + (stageInt == 4 ? "TokHaar-Kal" : "Fury amulet") + "?", "Yes.", "No.");
				break;
			case 4:
			case 5:
				if (componentId == OPTION_1) {
					player.temporaryAttribute().put("FightKilnReward", stageInt == 4 ? 0 : 1);
					stageInt = 6;
					sendNPCDialogue(npcId, 9827, "Let us test our strength again...soon...");
				} else {
					stageInt = 1;
					sendOptionsDialogue("Choose your reward:", "The TokHaar-Kal", "An Fury Amulet");
				}
				break;
			case 6:
				stageInt = 7;
				sendNPCDialogue(npcId, 9827, "Now,leave...before the lava consumes you.");
				break;
			case 7:
				end();
				break;
			}
			break;
		}

	}

	@Override
	public void finish() {
		if (type == 5)
			fightKiln.unlockPlayer();
		else if (type != 0)
			fightKiln.removeScene();

	}

}
