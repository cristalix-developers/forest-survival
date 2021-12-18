package me.reidj.forest.drop.block

import me.reidj.forest.item.ItemList
import org.bukkit.Location

object BlockPlacer {

    fun place(item: ItemList, location: Location) {
        val data = item.item.getData()
        var realData: Byte = 0
        if (data != null)
            realData = data.data

        location.block.setTypeAndDataFast(item.item.typeId, realData)
    }
}