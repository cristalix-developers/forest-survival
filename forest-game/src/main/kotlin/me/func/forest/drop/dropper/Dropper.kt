package me.func.forest.drop.dropper

import me.func.forest.drop.Resources
import org.bukkit.Location
import org.bukkit.entity.Player

@FunctionalInterface
interface Dropper {

    fun drop(resource: Resources, location: Location, player: Player?)

}