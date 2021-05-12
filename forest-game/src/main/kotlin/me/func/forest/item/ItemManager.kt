package me.func.forest.item

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class ItemManager : Listener {

    @EventHandler
    fun interactEvent(event: PlayerInteractEvent) {
        if (!event.hasItem() || !event.item.hasItemMeta())
            return

        for (item in ItemList.values()) {
            if (item.onClick == null)
                continue
            if (item.item.itemMeta.displayName == event.item.itemMeta.displayName) {
                item.onClick.accept(item, event)
                break
            }
        }
    }
}