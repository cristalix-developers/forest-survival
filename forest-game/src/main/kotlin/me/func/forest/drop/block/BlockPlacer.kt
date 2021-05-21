package me.func.forest.drop.block

import me.func.forest.item.ItemList
import org.bukkit.Location

object BlockPlacer {

    fun place(item: ItemList, location: Location) {
        location.block.setTypeAndDataFast(item.item.type, item.item.getData().data)
    }

}