package me.func.forest.user.listener

import clepto.bukkit.B
import me.func.forest.app
import me.func.forest.user.listener.prepare.ModLoader
import me.func.forest.user.listener.prepare.TutorialLoader
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.spigotmc.event.entity.EntityDismountEvent

class PlayerListener : Listener {

    private val prepares = listOf(
        ModLoader(),
        TutorialLoader()
    )

    @EventHandler
    fun joinEvent(event: PlayerJoinEvent) {
        val user = app.getUser(event.player)

        prepares.forEach { it.execute(user) }
    }

    @EventHandler
    fun dismountEvent(event: EntityDismountEvent) {
        val entity = event.entity
        val dismounted = event.dismounted

        if (entity is CraftPlayer) {
            val user = app.getUser(entity)
            // Если игрок находится в состоянии просмотра туториала,
            // не давать слезть с вертолета
            if (user.watchTutorial())
                return
            B.postpone(1) {
                if (dismounted != null && !dismounted.isDead) {
                    dismounted.addPassenger(entity)
                }
            }
        }
    }
}