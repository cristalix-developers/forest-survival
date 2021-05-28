package me.func.forest.item

import clepto.bukkit.B
import me.func.forest.app
import me.func.forest.channel.ModTransfer
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class ItemManager : Listener {

    private val startLocation: Location = app.worldMeta.getLabel("guide_start")

    @EventHandler
    fun PlayerInteractEvent.handle() {
        if (blockClicked != null && blockClicked.type == Material.CAKE_BLOCK && !app.getUser(player)!!.watchTutorial()) {
            player.teleport(startLocation)
            isCancelled = true
            B.postpone(10) { ModTransfer().send("lets-start", app.getUser(player)!!) }
        }
        if (!hasItem())
            return
        isItem(item, this)
    }

    @EventHandler
    fun PlayerFishEvent.handle() {
        isItem(player.inventory.itemInHand, this)
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