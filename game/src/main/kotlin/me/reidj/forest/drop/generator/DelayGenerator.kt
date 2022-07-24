package me.reidj.forest.drop.generator

import me.func.mod.util.after
import me.reidj.forest.channel.item.ItemList
import me.reidj.forest.drop.block.BlockPlacer
import me.reidj.forest.drop.block.BlockUnit
import org.bukkit.Location

class DelayGenerator(private val item: ItemList, private val waitSeconds: Int) : Generator {

    override fun generate(resource: BlockUnit, location: Location) {
        after(20 * waitSeconds.toLong()) { BlockPlacer.place(item, location) }
    }

    override fun getStand(): ItemList {
        return item
    }
}