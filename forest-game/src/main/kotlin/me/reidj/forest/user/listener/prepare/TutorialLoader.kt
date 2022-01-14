package me.reidj.forest.user.listener.prepare

import me.reidj.forest.app
import me.reidj.forest.user.User
import org.bukkit.Bukkit

object TutorialLoader : PrepareUser {

    init {
        // При завершении туториала
        Bukkit.getMessenger().registerIncomingPluginChannel(app, "guide-end") { _, player, _ ->
            player.teleport(app.spawn)
            player.health = 10.0
            player.saturation = 5F
            val user = app.getUser(player)!!
            user.giveExperience(1)
            user.stat.tutorial = true
        }
    }

    override fun execute(user: User) {
        if (!user.watchTutorial())
            user.player!!.teleport(app.start)
    }
}