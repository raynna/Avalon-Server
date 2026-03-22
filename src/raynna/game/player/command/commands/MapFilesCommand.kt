package raynna.game.player.command.commands

import raynna.core.cache.Cache
import raynna.game.player.Player
import raynna.game.player.Ranks
import raynna.game.player.command.Command

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

        val mapArchiveId = Cache.getArchiveId(5, mapName)
        val landscapeArchiveId = Cache.getArchiveId(5, landName)

        println("RegionId: $regionId")
        println("Landscape Archive: $landscapeArchiveId")
        println("Map Archive: $mapArchiveId")

        player.message("Region: $regionId | Map: $mapArchiveId | Land: $landscapeArchiveId")

        return true
    }
}
