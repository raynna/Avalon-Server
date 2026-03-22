package raynna.game.player.travel

import raynna.core.tasks.WorldTask
import raynna.core.tasks.WorldTasksManager
import raynna.game.WorldObject
import raynna.game.WorldTile
import raynna.game.player.Player
import raynna.game.player.actions.combat.Magic

class FairyRings {
    companion object {
        private val LETTERS =
            arrayOf(
                arrayOf("a", "b", "c", "d"),
                arrayOf("i", "j", "k", "l"),
                arrayOf("p", "q", "r", "s"),
            )

        private val FAIRY_SOURCE = WorldTile(2412, 4434, 0)

        private const val FIRST_ANIMATION = 3254
        private const val SECOND_ANIMATION = 3255
        private const val FIRST_GRAPHICS = 2670
        private const val SECOND_GRAPHICS = 2671

        fun checkAll(player: Player): Boolean = true

        fun openRingInterface(
            player: Player,
            fairyRing: WorldObject,
        ): Boolean {
            if (!checkAll(player)) return false
            player.temporaryAttributtes["fairy_ring_object"] = fairyRing
            if (fairyRing.id == 12128) {
                player.interfaceManager.sendInterface(734)
                sendTravelLog(player)
                resetRingHash(player)
            } else {
                sendTeleport(player, FAIRY_SOURCE)
            }
            return true
        }

        fun handleButtons(
            player: Player,
            interfaceId: Int,
            componentId: Int,
        ): Boolean {
            when (interfaceId) {
                735 -> {
                    if (componentId >= 14 && componentId <= 14 + 64) {
                        sendRingTeleport(player, componentId - 14)
                    }
                    return true
                }

                734 -> {
                    if (componentId == 21) {
                        confirmRingHash(player)
                    } else {
                        handleDialButtons(player, componentId)
                    }
                    return true
                }
            }
            return false
        }

        private fun sendTravelLog(player: Player) {
            player.interfaceManager.sendInventoryInterface(735)

            Rings.entries
                .filter { it.component != null && it.description != null }
                .forEach {
                    player.packets.sendTextOnComponent(
                        735,
                        it.component!!,
                        "          ${it.description}",
                    )
                }
        }

        fun confirmRingHash(player: Player): Boolean {
            val locationArray =
                player.temporaryAttributtes.remove("location_array") as? IntArray ?: return false

            val builder = StringBuilder()
            var index = 0
            for (value in locationArray) {
                builder.append(LETTERS[index++][value])
            }

            return sendRingTeleport(player, builder.toString().uppercase())
        }

        fun sendRingTeleport(
            player: Player,
            hash: Int,
        ): Boolean {
            var h = hash
            val letter1 = h / 16
            h -= letter1 * 16
            val letter2 = h / 4
            h -= letter2 * 4
            val letter3 = h

            val code = LETTERS[0][letter1] + LETTERS[1][letter2] + LETTERS[2][letter3]

            return sendRingTeleport(player, code.uppercase())
        }

        fun sendRingTeleport(
            player: Player,
            hash: String,
        ): Boolean {
            val ring =
                try {
                    Rings.valueOf(hash)
                } catch (e: Exception) {
                    null
                }

            if (ring?.tile == null) {
                sendTeleport(player, WorldTile(FAIRY_SOURCE, 2))
                return false
            }

            sendTeleport(player, ring.tile)
            return true
        }

        private fun resetRingHash(player: Player) {
            player.temporaryAttributtes["location_array"] = IntArray(3)

            for (i in 0..2) {
                player.varsManager.sendVarBit(2341 + i, 0)
            }
        }

        private fun sendTeleport(
            player: Player,
            tile: WorldTile,
        ) {
            val fairyRing =
                player.temporaryAttributtes["fairy_ring_object"] as? WorldObject
                    ?: return
            player.interfaceManager.closeScreenInterface()
            player.addWalkSteps(fairyRing.x, fairyRing.y, -1, false)
            WorldTasksManager.schedule(2) {
                player.message("teleport now")
                Magic.sendTeleportSpell(
                    player,
                    FIRST_ANIMATION,
                    SECOND_ANIMATION,
                    FIRST_GRAPHICS,
                    SECOND_GRAPHICS,
                    0,
                    0.0,
                    tile,
                    2,
                    false,
                    Magic.OBJECT_TELEPORT,
                )
            }
        }

        fun handleDialButtons(
            player: Player,
            componentId: Int,
        ) {
            var locationArray =
                player.temporaryAttributtes["location_array"] as? IntArray ?: run {
                    player.closeInterfaces()
                    return
                }
            if (componentId == 19) return // x button

            if (player.temporaryAttributtes["location_changing"] != null) return

            val index = (componentId - 23) / 2

            if (componentId % 2 == 0) {
                locationArray[index]++
            } else {
                locationArray[index]--
            }

            locationArray = getCorrectValues(locationArray)

            player.temporaryAttributtes["location_array"] = locationArray
            player.temporaryAttributtes["location_changing"] = true

            for (i in 0..2) {
                player.varsManager.sendVarBit(
                    2341 + i,
                    when (locationArray[i]) {
                        1 -> 3
                        3 -> 1
                        else -> locationArray[i]
                    },
                )
            }

            WorldTasksManager.schedule(
                object : WorldTask() {
                    override fun run() {
                        player.temporaryAttributtes.remove("location_changing")
                    }
                },
                3,
            )
        }

        private fun getCorrectValues(locationArray: IntArray): IntArray {
            for (i in locationArray.indices) {
                locationArray[i] = locationArray[i] and 0x3
            }
            return locationArray
        }
    }
}
