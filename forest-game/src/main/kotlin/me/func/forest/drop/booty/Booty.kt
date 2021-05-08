package me.func.forest.drop.booty

import me.func.forest.drop.Resources
import org.bukkit.Location
import org.bukkit.entity.Player

@FunctionalInterface
interface Booty {

    fun get(resource: Resources, location: Location, player: Player)

}