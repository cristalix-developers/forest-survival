package me.reidj.forest.drop.dropper

import me.reidj.forest.channel.item.ItemList
import org.bukkit.Location
import org.bukkit.entity.Player

@FunctionalInterface
interface Dropper {

    fun drop(drop: ItemList, location: Location, player: Player?)

}