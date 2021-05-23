package me.func.forest.user.listener.prepare

import me.func.forest.Postulates
import me.func.forest.app
import me.func.forest.user.User
import org.bukkit.Bukkit
import org.bukkit.Location

class TutorialLoader : PrepareUser {

    init {
        // При завершении туториала
        Bukkit.getMessenger().registerIncomingPluginChannel(app, "guide-end") { _, player, _ ->

            player.health = 1.0
            player.saturation = 5F
        }
    }

    private val startLocation: Location = app.worldMeta.getLabel("guide_start")

    override fun execute(user: User) {
        if (!user.watchTutorial()) {
            // test


            //player.teleport(startLocation)
        }
    }
}