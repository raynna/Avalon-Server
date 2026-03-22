package raynna.game.player.conversation.scenes

import raynna.core.packets.handlers.ButtonHandler
import raynna.game.player.content.WildernessArtefacts
import raynna.game.player.queue.QueueTask
import raynna.game.player.shop.shops.PvpShop

suspend fun QueueTask.mandrith(npcId: Int) {
    val artefacts =
        intArrayOf(
            14876,
            14877,
            14878,
            14879,
            14880,
            14881,
            14882,
            14883,
            14884,
            14885,
            14886,
            14887,
            14888,
            14889,
            14890,
            14891,
            14892,
        )

    if (player.inventory.containsOneItem(*artefacts)) {
        npc(
            "Glorious, brave warrior! I see you have found some of the ancient artefacts my brother and I are seeking.",
            npcId,
        )
        npc("Would you like to sell them to me?", npcId)

        while (true) {
            when (
                options(
                    "Sure I can do that",
                    "Who are you?",
                    "Oh, sorry, I thought you were someone else.",
                    "No, sorry.",
                )
            ) {
                1 -> {
                    player("Sure I can do that.")

                    if (player.moneyPouch.isFull &&
                        player.inventory.containsItem("item.coins", Int.MAX_VALUE)
                    ) {
                        npc(
                            "Sorry but it seems like you have max cash in both inventory and money pouch.",
                            npcId,
                        )
                    } else {
                        WildernessArtefacts.trade(player)
                    }
                    return
                }

                2 -> {
                    whoAreYou(npcId)
                }

                3 -> {
                    player("Oh, sorry, I thought you were someone else.")
                    npc("I'm not sure how you could confuse ME with anyone!", npcId)
                    return
                }

                4 -> {
                    player("No, sorry.")
                    return
                }
            }
        }
    }

    npc("How can I help you?", npcId)
    while (true) {
        when (
            options(
                "Do you have anything for sale?",
                "Can I claim my untradeables?",
                "Who are you?",
                "Oh, sorry, I thought you were someone else.",
            )
        ) {
            1 -> {
                npc("Ah! Yes.. here is what I sell.", npcId)
                player.interfaceManager.closeChatBoxInterface()
                player.shopSystem.openShop(PvpShop)
                return
            }

            2 -> {
                if (player.untradeables.containerItems.isEmpty()) {
                    npc(
                        "It seems like you haven't lost any untradeables.",
                        npcId,
                    )
                    return
                } else {
                    npc("Yes, here is what you've lost.", npcId)
                    player.interfaceManager.closeChatBoxInterface()
                    ButtonHandler.refreshUntradeables(player)
                    return
                }
            }

            3 -> {
                whoAreYou(npcId)
            }

            4 -> {
                player("Oh, sorry, I thought you were someone else.")
                return
            }
        }
    }
}

suspend fun QueueTask.whoAreYou(npcId: Int) {
    player("Who are you?")
    npc(
        "Why, I'm Mandrith! Inspiration to combatants both mighty and puny!",
        npcId,
    )

    player("Okay...fair enough")

    while (true) {
        when (
            options(
                "What do you do here?",
                "Erm, what's with the outfit?",
                "I have to go now.",
            )
        ) {
            1 -> {
                artefactExplanation(npcId)
            }

            2 -> {
                player("Erm, what's with the outfit?")
                npc(
                    "You like not my kingly robes? They were my father's, and his father's before him, and his father's before him, and-",
                    npcId,
                )
                player("Okay! Okay! I get the picture.")
                return
            }

            3 -> {
                player("I have to go now.")
                stop()
                return
            }
        }
    }
}

suspend fun QueueTask.artefactExplanation(npcId: Int) {
    player("What do you do here?")
    npc(
        "I am here to collect ancient artefacts acquired by adventurers in return for some well-deserved rewards.",
        npcId,
    )

    while (true) {
        when (
            options(
                "What ancient artefacts?",
                "That sounds great, goodbye.",
            )
        ) {
            1 -> {
                player("What ancient artefacts?")

                npc("Haha! I can tell you are new to these parts.", npcId)

                npc(
                    "As the blood of warriors is spilled on the ground, as it once was during the God Wars, relics of that age feel the call of battle and are drawn into the rays of the sun once more.",
                    npcId,
                )

                npc(
                    "If you come across any, bring them to me or my brother Nastroth. We will pay you. Not noted, and no weapons or armour.",
                    npcId,
                )

                while (true) {
                    when (
                        options(
                            "You have a brother?",
                            "Why won't you buy weapons or armour?",
                            "That sounds great. Goodbye.",
                        )
                    ) {
                        1 -> {
                            player("You have a brother?")
                            npc(
                                "Yes, why else would I have referred to him as such?",
                                npcId,
                            )
                            player("You make a good point.")

                            when (
                                options(
                                    "Why won't you buy weapons or armour?",
                                    "That sounds great. Goodbye",
                                )
                            ) {
                                1 -> {
                                    player("Why won't you buy weapons or armour?")
                                    npc(
                                        "They should be used as they were meant to be used, and not traded in for rewards.",
                                        npcId,
                                    )
                                }

                                2 -> {
                                    player("That sounds great. Goodbye.")
                                    stop()
                                    return
                                }
                            }
                        }

                        2 -> {
                            player("Why won't you buy weapons or armour?")
                            npc(
                                "They should be used as they were meant to be used, and not traded in for rewards.",
                                npcId,
                            )

                            when (
                                options(
                                    "You have a brother?",
                                    "That sounds great. Goodbye",
                                )
                            ) {
                                1 -> {
                                    npc(
                                        "Yes, why else would I have referred to him as such?",
                                        npcId,
                                    )
                                    player("You make a good point.")
                                }

                                2 -> {
                                    player("That sounds great. Goodbye")
                                    stop()
                                    return
                                }
                            }
                        }

                        3 -> {
                            player("That sounds great. Goodbye.")
                            stop()
                            return
                        }
                    }
                }
            }

            2 -> {
                player("That sounds great, goodbye.")
                stop()
                return
            }
        }
    }
}
