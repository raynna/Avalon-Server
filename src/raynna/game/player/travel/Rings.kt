package raynna.game.player.travel

import raynna.game.WorldTile

enum class Rings(
    val tile: WorldTile?,
    val component: Int?,
    val description: String?,
) {
    AIQ(WorldTile(2996, 3114, 0), 15, "Asgarnia: Mudskipper Point"),
    AJQ(WorldTile(2735, 5221, 0), 16, "Islands: South of Witchaven"),
    AJR(WorldTile(2780, 3613, 0), 19, "Dungeons: Dark cave south of Dorgesh-Kaan"),
    AKQ(WorldTile(2319, 3619, 0), 20, "Kandarin: Slayer cave south-east of Rellekka"),
    AKS(WorldTile(2571, 2956, 0), 21, "Islands: Penguins near Miscellania"),

    ALP(WorldTile(2468, 4189, 0), 23, "Kandarin: Piscatoris Hunter area"),
    ALQ(WorldTile(3597, 3495, 0), 25, "Feldip Hills: Feldip Hunter area"),
    ALS(WorldTile(2644, 3495, 0), 26, "Kandarin: Feldip Hills"),

    BIP(WorldTile(3410, 3324, 0), 27, "Morytania: Haunted Woods east of Canifis"),
    BIQ(WorldTile(3251, 3095, 0), 28, "Other Realms: Abyss"),
    BJQ(WorldTile(1737, 5342, 0), 29, "Kandarin: McGrubor's Wood"),

    BJR(WorldTile(2650, 4730, 0), 30, "Islands: Polypore Dungeon"),
    BKP(WorldTile(2385, 3035, 0), 31, "Kharidian Desert: Near Kalphite hive"),
    BKR(WorldTile(3469, 3431, 0), 32, "Sparse Plane"),
    BLP(WorldTile(4622, 5147, 0), 33, "Kandarin: Ardougne Zoo unicorns"),

    BLR(WorldTile(2740, 3351, 0), 35, "Dungeons: Ancient Dungeon"),
}
