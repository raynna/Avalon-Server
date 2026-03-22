package raynna.game.player.shop.shops

import raynna.game.npc.drops.tables.minigame.BarrowsChestTable
import raynna.game.player.shop.*
import raynna.game.player.shop.ShopDefinitions.shop

object BarrowsShop : GameShop {
    val ALL_BARROWS_ITEMS: List<String> =
        listOf(
            // Helms
            "item.ahrim_s_hood",
            "item.dharok_s_helm",
            "item.guthan_s_helm",
            "item.karil_s_coif",
            "item.torag_s_helm",
            "item.verac_s_helm",
            "item.akrisae_s_hood",
            // Tops
            "item.ahrim_s_robe_top",
            "item.dharok_s_platebody",
            "item.guthan_s_platebody",
            "item.karil_s_top",
            "item.torag_s_platebody",
            "item.verac_s_brassard",
            "item.akrisae_s_robe_top",
            // Legs
            "item.ahrim_s_robe_skirt",
            "item.dharok_s_platelegs",
            "item.guthan_s_chainskirt",
            "item.karil_s_skirt",
            "item.torag_s_platelegs",
            "item.verac_s_plateskirt",
            "item.akrisae_s_robe_skirt",
            // Weapons
            "item.ahrim_s_staff",
            "item.dharok_s_greataxe",
            "item.guthan_s_warspear",
            "item.karil_s_crossbow",
            "item.torag_s_hammers",
            "item.verac_s_flail",
            "item.akrisae_s_war_mace",
        )

    override val definition: ShopDefinition =
        shop {
            title = "Barrows Store"
            currency = CurrencyType.COINS
            isGlobal = true

            ALL_BARROWS_ITEMS.forEach {
                item(it, 1)
            }
        }
}
