package com.rs.kotlin.game.player.skills.mining

object RockDepletion {
    private val depletionMap =
        mapOf(
            // Clay
            15503 to 11555,
            15504 to 11556,
            15505 to 11557,
            // Copper
            11936 to 11552,
            11937 to 11553,
            11938 to 11554,
            11960 to 11555,
            11961 to 11556,
            11962 to 11557,
            // Tin
            11933 to 11552,
            11934 to 11553,
            11935 to 11554,
            11957 to 11555,
            11958 to 11556,
            11959 to 11557,
            // Iron
            37307 to 11552,
            37308 to 11553,
            37309 to 11554,
            11954 to 11555,
            11955 to 11556,
            11956 to 11557,
            // Coal
            11930 to 11552,
            11931 to 11553,
            11932 to 11554,
            11963 to 11555,
            11964 to 11556,
            // Mithril
            11942 to 11552,
            11943 to 11553,
            11944 to 11554,
            // Adamant
            11939 to 11552,
            11941 to 11554,
            // Runite
            14859 to 11552,
            14860 to 11553,
            45069 to 11552,
            45071 to 11553,
            // Limestone stages
            4027 to 4028,
            4028 to 4029,
            4029 to 4030,
        )

    fun getDepletedId(objectId: Int): Int? = depletionMap[objectId]
}
