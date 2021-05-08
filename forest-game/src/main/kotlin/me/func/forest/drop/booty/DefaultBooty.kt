package me.func.forest.drop.booty

import me.func.forest.drop.Resources
import org.bukkit.Location
import org.bukkit.entity.Player

object DefaultBooty : Booty {
    override fun get(resource: Resources, location: Location, player: Player) {
        resource.drop(location, player)
        resource.generate(location)
    }
}