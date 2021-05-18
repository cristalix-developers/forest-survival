package me.func.forest.item

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class ItemManager : Listener {

    @EventHandler
    fun interactEvent(event: PlayerInteractEvent) {
        if (!event.hasItem())
            return
        isItem(event.item, event)
    }

    @EventHandler
    fun pickUpItem(event: PlayerAttemptPickupItemEvent) {
        isItem(event.item.itemStack, event)
    }

    private fun isItem(itemStack: ItemStack, on: PlayerEvent) {
        if (!itemStack.hasItemMeta())
            return

        for (item in ItemList.values()) {
            if (item.item.itemMeta.displayName == itemStack.itemMeta.displayName) {
                if (item.on != null)
                    item.on[on::class.java]?.accept(item, on)
                return
            }
        }
    }
}