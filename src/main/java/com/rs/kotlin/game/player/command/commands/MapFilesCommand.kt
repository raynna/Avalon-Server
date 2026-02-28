package com.rs.kotlin.game.player.command.commands

import com.displee.cache.CacheLibrary
import com.rs.core.cache.Cache
import com.rs.java.game.player.Player
import com.rs.java.game.player.Ranks
import com.rs.kotlin.game.player.command.Command

class MapFilesCommand : Command {
    override val requiredRank = Ranks.Rank.DEVELOPER
    override val description = "Prints region map and landscape archive ids"
    override val usage = "::checkmap"

    override fun execute(
        player: Player,
        args: List<String>,
        trigger: String,
    ): Boolean {
        val regionId = player.regionId
        val regionX = regionId shr 8
        val regionY = regionId and 0xff

        val mapName = "m${regionX}_$regionY"
        val landName = "l${regionX}_$regionY"

        val mapArchiveId = Cache.STORE.indexes[5].getArchiveId(mapName)
        val landscapeArchiveId = Cache.STORE.indexes[5].getArchiveId(landName)

        println("RegionId: $regionId")
        println("Landscape Archive: $landscapeArchiveId")
        println("Map Archive: $mapArchiveId")

        player.message("Region: $regionId | Map: $mapArchiveId | Land: $landscapeArchiveId")

        return true
    }
}
