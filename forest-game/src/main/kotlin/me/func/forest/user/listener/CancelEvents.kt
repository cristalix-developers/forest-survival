package me.func.forest.user.listener

import clepto.bukkit.B
import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import me.func.forest.app
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.spigotmc.event.entity.EntityDismountEvent

class CancelEvents : Listener {

    @EventHandler
    fun dismountEvent(event: EntityDismountEvent) {
        val entity = event.entity
        val dismounted = event.dismounted

        if (entity is CraftPlayer) {
            val user = app.getUser(entity) ?: return

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

    @EventHandler
    fun disable(event: BlockPlaceEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: CraftItemEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: PlayerInteractEntityEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: BlockFadeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: BlockSpreadEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: BlockGrowEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: BlockFromToEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: HangingBreakByEntityEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: BlockBurnEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: EntityExplodeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: PlayerArmorStandManipulateEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: PlayerAdvancementCriterionGrantEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun disable(event: PlayerSwapHandItemsEvent) {
        event.isCancelled = true
    }
}