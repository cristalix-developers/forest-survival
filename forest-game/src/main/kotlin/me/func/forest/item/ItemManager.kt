package me.func.forest.item

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class ItemManager : Listener {

    @EventHandler
    fun PlayerInteractEvent.handle() {
        if (!hasItem())
            return
        isItem(item, this)
    }

    @EventHandler
    fun PlayerAttemptPickupItemEvent.handle() {
        isItem(item.itemStack, this)
    }

    private fun isItem(itemStack: ItemStack, on: PlayerEvent) {
        val tag = CraftItemStack.asNMSCopy(itemStack).tag

        if (tag != null && tag.hasKey("code")) {
            val item = ItemList.valueOf(tag.getString("code"))
            if (item.on != null)
                item.on[on::class.java]?.accept(item, on)
        }
    }
}