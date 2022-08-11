package me.reidj.forest.user.prepare

import me.func.mod.Glow
import me.reidj.forest.app
import me.reidj.forest.user.User
import org.bukkit.Bukkit

object TutorialLoader : PrepareUser {

    init {
        // При завершении туториала
        Bukkit.getMessenger().registerIncomingPluginChannel(app, "guide-end") { _, player, _ ->
            Glow.animate(player, 0.4, 255, 0, 0)
            player.teleport(app.spawn)
            player.health = 10.0
            player.saturation = 5F
            val user = app.getUser(player)!!
            user.stat.tutorial = true
        }
    }

    override fun execute(user: User) { user.player!!.teleport(app.start) }
}