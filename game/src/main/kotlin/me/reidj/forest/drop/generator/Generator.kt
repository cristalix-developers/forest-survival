package me.reidj.forest.drop.generator

import me.reidj.forest.drop.block.BlockUnit
import me.reidj.forest.channel.item.ItemList
import org.bukkit.Location

@FunctionalInterface
interface Generator {

    fun generate(resource: BlockUnit, location: Location)

    fun getStand(): ItemList

}