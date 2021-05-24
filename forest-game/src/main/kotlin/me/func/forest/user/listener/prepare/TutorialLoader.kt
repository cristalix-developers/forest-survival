package me.func.forest.user.listener.prepare

import me.func.forest.app
import me.func.forest.user.User
import org.bukkit.Bukkit

class TutorialLoader : PrepareUser {

    init {
        // При завершении туториала
        Bukkit.getMessenger().registerIncomingPluginChannel(app, "guide-end") { _, player, _ ->
            player.teleport(app.spawn)
            player.health = 10.0
            player.saturation = 5F
            app.getUser(player)!!.giveExperience(1)
        }
    }

    override fun execute(user: User) {
        if (!user.watchTutorial())
            user.player.teleport(app.start)
    }
}