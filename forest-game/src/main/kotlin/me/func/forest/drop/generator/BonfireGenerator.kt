package me.func.forest.drop.generator

import clepto.bukkit.B
import me.func.forest.drop.BlockPlacer
import me.func.forest.drop.Resources
import me.func.forest.item.ItemList
import org.bukkit.Location

object BonfireGenerator : Generator {

    val BONFIRES: MutableMap<Location, Boolean> = mutableMapOf() // todo: load from disk
    const val TICKS_FIRE = 20 * 12

    override fun generate(resource: Resources, location: Location) {
        if (BONFIRES[location] == null) {
            BlockPlacer.place(ItemList.BONFIRE_OFF2, location)
            BONFIRES[location] = false
            return
        }
        BlockPlacer.place(ItemList.BONFIRE_ON2, location)
        BONFIRES[location] = true
        B.postpone(TICKS_FIRE) {
            BlockPlacer.place(ItemList.BONFIRE_OFF2, location)
            BONFIRES[location] = false
        }
    }

    override fun getStand(): ItemList {
        return ItemList.BONFIRE_OFF2
    }

}