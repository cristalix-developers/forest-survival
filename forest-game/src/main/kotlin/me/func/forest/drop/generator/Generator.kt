package me.func.forest.drop.generator

import me.func.forest.drop.Resources
import me.func.forest.item.ItemList
import org.bukkit.Location

@FunctionalInterface
interface Generator {

    fun generate(resource: Resources, location: Location)

    fun getStand(): ItemList

}