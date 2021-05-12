package me.func.forest.drop.generator

import clepto.bukkit.B
import me.func.forest.drop.Resources
import me.func.forest.item.ItemList
import org.bukkit.Location

class DelayGenerator(private val item: ItemList, private val waitSeconds: Int) : Generator {

    override fun generate(resource: Resources, location: Location) {
        B.postpone(20 * waitSeconds) {
            location.block.setTypeAndDataFast(item.item.type, item.item.getData().data)
        }
    }

    override fun getStand(): ItemList {
        return item
    }
}