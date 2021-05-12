package me.func.forest.drop.dropper

import me.func.forest.item.ItemList
import org.bukkit.Location
import org.bukkit.entity.Player

@FunctionalInterface
interface Dropper {

    fun drop(drop: ItemList, location: Location, player: Player?)

}