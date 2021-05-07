package me.func.forest.user.listener.prepare

import me.func.forest.app
import me.func.forest.channel.ModTransfer
import me.func.forest.user.User
import org.bukkit.Location

class TutorialLoader : PrepareUser {

    private val startLocation: Location = app.worldMeta
        .getLabel("guide_start")
        .toCenterLocation()

    override fun execute(user: User) {
        ModTransfer.string("1").send("guide", user)
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