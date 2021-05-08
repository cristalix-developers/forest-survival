package me.func.forest.user.listener.prepare

import clepto.bukkit.B
import me.func.forest.app
import me.func.forest.channel.ModTransfer
import me.func.forest.user.User
import org.bukkit.Bukkit
import org.bukkit.Location

class TutorialLoader : PrepareUser {

    init {
        // При завершении туториала
        Bukkit.getMessenger().registerIncomingPluginChannel(app, "guide-end") { s, player, bytes ->

            player.teleport(endLocation)
            player.health = 1.0
            player.saturation = 5F
        }
    }

    private val startLocation: Location = app.worldMeta.getLabel("guide_start")
    private val endLocation: Location = app.worldMeta.getLabel("guide_end")

    override fun execute(user: User) {
        B.postpone(5) { ModTransfer().send("guide", user) }

        if (!user.watchTutorial()) {
            // test
            val player = user.player
            player.allowFlight = true
            player.isFlying = true
            player.isOp = true

            player.teleport(startLocation)
        }
    }
}