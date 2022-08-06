package me.reidj.forest.listener

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent
import me.func.mod.util.after
import me.reidj.forest.app
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

object CancelEvents : Listener {

    @EventHandler
    fun EntityDismountEvent.handle() {
        if (entity is CraftPlayer) {
            val user = app.getUser(entity.uniqueId) ?: return

            // Если игрок находится в состоянии просмотра туториала,
            // не давать слезть с вертолета
            if (user.watchTutorial())
                return
            after {
                if (dismounted != null && !dismounted.isDead) {
                    dismounted.addPassenger(entity)
                }
            }
        }
    }

    @EventHandler
    fun BlockPlaceEvent.handle() { isCancelled = true }

    @EventHandler
    fun CraftItemEvent.handle() { isCancelled = true }

    @EventHandler
    fun PlayerInteractEntityEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockFadeEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockSpreadEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockGrowEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockFromToEvent.handle() { isCancelled = true }

    @EventHandler
    fun HangingBreakByEntityEvent.handle() { isCancelled = true }

    @EventHandler
    fun BlockBurnEvent.handle() { isCancelled = true }

    @EventHandler
    fun EntityExplodeEvent.handle() { isCancelled = true }

    @EventHandler
    fun PlayerArmorStandManipulateEvent.handle() { isCancelled = true }

    @EventHandler
    fun PlayerAdvancementCriterionGrantEvent.handle() { isCancelled = true }

    @EventHandler
    fun PlayerSwapHandItemsEvent.handle() { isCancelled = true }
}