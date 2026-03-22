package raynna.game.world.area.areas.banks

import raynna.game.WorldTile
import raynna.game.world.area.Area
import raynna.game.world.area.shape.Polygon

class GrandExchangeBank :
    Area(
        Polygon(
            arrayOf(
                WorldTile(3161, 3473, 0),
                WorldTile(3160, 3473, 0),
                WorldTile(3159, 3474, 0),
                WorldTile(3158, 3474, 0),
                WorldTile(3157, 3475, 0),
                WorldTile(3156, 3475, 0),
                WorldTile(3155, 3476, 0),
                WorldTile(3154, 3476, 0),
                WorldTile(3153, 3477, 0),
                WorldTile(3152, 3478, 0),
                WorldTile(3151, 3479, 0),
                WorldTile(3151, 3480, 0),
                WorldTile(3150, 3481, 0),
                WorldTile(3150, 3482, 0),
                WorldTile(3149, 3483, 0),
                WorldTile(3149, 3484, 0),
                WorldTile(3148, 3485, 0),
                WorldTile(3148, 3494, 0),
                WorldTile(3149, 3495, 0),
                WorldTile(3149, 3496, 0),
                WorldTile(3150, 3497, 0),
                WorldTile(3150, 3498, 0),
                WorldTile(3151, 3499, 0),
                WorldTile(3151, 3500, 0),
                WorldTile(3154, 3503, 0),
                WorldTile(3155, 3503, 0),
                WorldTile(3156, 3504, 0),
                WorldTile(3157, 3504, 0),
                WorldTile(3158, 3505, 0),
                WorldTile(3159, 3505, 0),
                WorldTile(3160, 3506, 0),
                WorldTile(3169, 3506, 0),
                WorldTile(3170, 3505, 0),
                WorldTile(3171, 3505, 0),
                WorldTile(3172, 3504, 0),
                WorldTile(3173, 3504, 0),
                WorldTile(3174, 3503, 0),
                WorldTile(3175, 3503, 0),
                WorldTile(3178, 3500, 0),
                WorldTile(3178, 3499, 0),
                WorldTile(3179, 3498, 0),
                WorldTile(3179, 3497, 0),
                WorldTile(3180, 3496, 0),
                WorldTile(3180, 3495, 0),
                WorldTile(3180, 3495, 0),
                WorldTile(3181, 3494, 0),
                WorldTile(3181, 3485, 0),
                WorldTile(3180, 3484, 0),
                WorldTile(3180, 3483, 0),
                WorldTile(3179, 3482, 0),
                WorldTile(3179, 3481, 0),
                WorldTile(3178, 3480, 0),
                WorldTile(3178, 3479, 0),
                WorldTile(3175, 3476, 0),
                WorldTile(3174, 3476, 0),
                WorldTile(3173, 3475, 0),
                WorldTile(3172, 3475, 0),
                WorldTile(3171, 3474, 0),
                WorldTile(3170, 3474, 0),
                WorldTile(3169, 3473, 0),
                WorldTile(3160, 3473, 0),
            ),
        ),
    ) {
    override fun update(): Area = this

    override fun name(): String = "Grand Exchange"

    override fun member(): Boolean = false

    override fun environment(): Environment = Environment.SAFEZONE
}
