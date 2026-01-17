package com.rs.kotlin.game.player

import com.rs.java.game.player.Player
import com.rs.json.GSONParser
import java.io.File

class AccountCreation {

    companion object {

        private val userDir: String =
            System.getProperty("user.dir") + "/data/characters/"

        @JvmStatic
        fun init() {
            // make sure to create a /data/characters/ folder if it doesn't exist.
            val dir = File(userDir)
            if (!dir.exists()) {
                val created = dir.mkdirs()
                println("Characters directory created: $created")
            }
        }

        @JvmStatic
        fun loadPlayer(username: String): Player? {
            val path = "$userDir$username.json"
            val file = File(path)

            if (!file.exists() || !file.isFile) {
                println("Player not found: $username")
                return null
            }

            return GSONParser.load(path, Player::class.java)
        }

        @JvmStatic
        fun savePlayer(player: Player) {
            // todo(Mujtaba): forbid namespace in user names.
            val filename = player.username.replace(" ", "_")
            //GSONParser.debugSave(player, Player::class.java);
            GSONParser.save(
                player,
                "$userDir$filename.json",
                Player::class.java
            )
        }

        @JvmStatic
        fun exists(username: String): Boolean {
            val path = "$userDir$username.json"
            val file = File(path)

            if (!file.exists() || !file.isFile) {
                println("Player: '$username' doesn't exist")
                return false
            }
            return true
        }
    }
}