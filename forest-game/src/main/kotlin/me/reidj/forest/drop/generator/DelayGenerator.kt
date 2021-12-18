package me.reidj.forest.drop.generator

import clepto.bukkit.B
import me.reidj.forest.drop.block.BlockPlacer
import me.reidj.forest.drop.block.BlockUnit
import me.reidj.forest.item.ItemList
import org.bukkit.Location

class DelayGenerator(private val item: ItemList, private val waitSeconds: Int) : Generator {

    override fun generate(resource: BlockUnit, location: Location) {
        B.postpone(20 * waitSeconds) { BlockPlacer.place(item, location) }
    }

    override fun getStand(): ItemList {
        return item
    }
}