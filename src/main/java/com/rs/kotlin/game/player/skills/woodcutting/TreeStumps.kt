package com.rs.kotlin.game.player.skills.woodcutting

object TreeStumps {
    private val stumpMap =
        mapOf(
            // Swamp trees
            3300 to 9389,
            9354 to 9389,
            9355 to 9389,
            9366 to 9389,
            9387 to 9389,
            9388 to 9389,
            // Dead tree
            11866 to 11864,
            // old regular
            1278 to 1342,
            1279 to 1342,
            1280 to 1342,
            // Regular trees
            38782 to 40350,
            38760 to 40350,
            38783 to 40352,
            38784 to 40352,
            38785 to 40354,
            38786 to 40354,
            61192 to 40355,
            38787 to 40356,
            38788 to 40357,
            // Oak trees
            38731 to 38754,
            38732 to 38741,
            // Willow trees
            38627 to 38725,
            38616 to 38725,
            // Maple trees
            4674 to 54766,
            46277 to 54766,
            51843 to 54766,
            // Mahogany trees
            70076 to 70081,
            70075 to 70080,
            70074 to 70079,
            70077 to 70082,
            46274 to 70082,
            // Yew trees
            38755 to 38759,
            // Magic trees
            63176 to 63179,
            37823 to 63179,
            // Cursed magic trees
            37821 to 37822,
            // Achey trees
            69554 to 69555,
            69556 to 69557,
            // Bloodwood trees
            4135 to 4136,
            19153 to 4136,
        )

    fun getStumpId(objectId: Int): Int = stumpMap[objectId] ?: 1342
}
