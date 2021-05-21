package me.func.forest.drop.generator

import me.func.forest.drop.block.BlockUnit
import me.func.forest.item.ItemList
import org.bukkit.Location

@FunctionalInterface
interface Generator {

    fun generate(resource: BlockUnit, location: Location)

    fun getStand(): ItemList

}