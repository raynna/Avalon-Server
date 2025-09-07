package com.rs.kotlin.game.world.area

import com.rs.core.tasks.WorldTask
import com.rs.core.tasks.WorldTasksManager
import com.rs.java.game.WorldTile
import com.rs.java.game.player.Player
import com.rs.kotlin.game.world.activity.BarrowsArea
import com.rs.kotlin.game.world.area.areas.banks.*
import com.rs.kotlin.game.world.area.areas.multi.*
import com.rs.kotlin.game.world.area.areas.zones.WildernessArea
import com.rs.kotlin.game.world.area.areas.zones.WildernessSafeArea


object AreaManager {

    private val allAreas = mutableListOf<Area>()
    private val areasByRegion = mutableMapOf<Int, MutableList<Area>>()

    fun init() {
        allAreas.clear()
        areasByRegion.clear()

        //minigames
        register(BarrowsArea())
        //wilderness areas
        register(WildernessArea(), WildernessSafeArea())
        //safezones
        register(AlkharidBank(), ArdougneNorthBank(), ArdougneSouthBank(), CamelotBank(), CanifisBank(),CastleWarsBank(),CatherbyBank(),DraynorBank(),DuelArenaBank(),EdgevilleBank(),FaladorEastBank(),FaladorWestBank(),FishingGuildBank(),LumbridgeCastle(),MageBank(),MobilisingArmiesBank(),NardahBank(),OoglogBank(),PortPhasmatysBank(),VarrockEastBank(),VarrockWestBank(),WarriorGuildBank(),YanilleBank())
        //multiareas
        register(AlkharidMulti(),ApeAtollMulti(),BarbarianVillageMulti(),BurthorpeMulti(),CastleWarsMulti(),CorporealBeastCaveMulti(),FaladorMulti(),GodwarsMulti(),Islands(),KalphiteLairMulti(),KingBlackDragonMulti(),MorytaniaMulti(),PestControlMulti(),PiscatorisColonyMulti(),WaterbirthDungeonMulti(),WhiteWolfMountainMulti(),WildernessMulti())
    }

    private fun register(vararg areas: Area) {
        for (a in areas) {
            allAreas += a
            for (region in a.regions()) {
                areasByRegion.computeIfAbsent(region) { mutableListOf() }.add(a)
            }
        }
    }

    @JvmStatic
    fun getAll(tile: WorldTile): List<Area> {
        val result = mutableListOf<Area>()
        result += areasByRegion[tile.regionId] ?: emptyList()
        result += allAreas.filter { it.shapes().any { s -> s.inside(tile) } }
        return result
    }

    @JvmStatic
    fun isInEnvironment(player: Player, env: Area.Environment): Boolean {
        return getAll(player.tile).any { it.environment() == env }
    }

    @JvmStatic
    fun isInEnvironment(tile: WorldTile, env: Area.Environment): Boolean {
        return AreaManager.getAll(tile).any { it.environment() == env }
    }

    @JvmStatic
    fun get(tile: WorldTile): Area? = getAll(tile).firstOrNull()

    fun onMoved(player: Player) {
        val last = player.lastAreas ?: emptySet()
        val current = getAll(player.tile).toSet() ?: emptySet()
        current.forEach { it.onMoved(player) }
        for (a in last - current) {
            a.onExit(player)
        }

        for (a in current - last) {
            a.onEnter(player)
            //if (a.environment() == Area.Environment.MINIGAME)
               // update(player, a)
        }

        player.lastAreas = current
    }


    fun update(player: Player, area: Area) {
        player.packets.sendTextOnComponent(1073, 10, "<col=ffffff>You have reached")
        player.packets.sendTextOnComponent(1073, 11, "<col=ffcf00>${area.name()}")
        player.interfaceManager.sendOverlay(1073, false)

        WorldTasksManager.schedule(object : WorldTask() {
            override fun run() {
                player.interfaceManager.closeOverlay(false)
                stop()
            }
        }, 3)
    }
}
