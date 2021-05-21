package me.func.forest.drop.generator

import clepto.bukkit.B
import me.func.forest.drop.block.BlockPlacer
import me.func.forest.drop.block.BlockUnit
import me.func.forest.item.ItemList
import org.bukkit.Location

class DelayGenerator(private val item: ItemList, private val waitSeconds: Int) : Generator {

    override fun generate(resource: BlockUnit, location: Location) {
        B.postpone(20 * waitSeconds) { BlockPlacer.place(item, location) }
    }

    override fun getStand(): ItemList {
        return item
    }
}