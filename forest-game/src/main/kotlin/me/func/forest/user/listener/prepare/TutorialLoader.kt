package me.func.forest.user.listener.prepare

import clepto.bukkit.Cycle
import me.func.forest.app
import me.func.forest.user.User
import org.bukkit.Location
import org.bukkit.entity.EntityType

class TutorialLoader : PrepareUser {

    private val startLocation: Location = app.worldMeta
        .getLabel("guide_start")
        .toCenterLocation()

    override fun execute(user: User) {
        if (!user.watchTutorial()) {
            val player = user.player

            val helicopter = app.getWorld().spawnEntity(
                startLocation,
                EntityType.ARMOR_STAND
            )
            player.teleport(startLocation)
            helicopter.teleport(startLocation)
            helicopter.setGravity(false)
            helicopter.addPassenger(player)

            Cycle.run(5, 4 * 60) {

            }
        }
    }
}