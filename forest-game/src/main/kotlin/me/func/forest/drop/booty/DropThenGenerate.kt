package me.func.forest.drop.booty

import me.func.forest.drop.Resources
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

object DropThenGenerate : Booty {
    override fun get(resource: Resources, location: Location, player: Player) {
        resource.drop(location, player)
        resource.generate(location)
        location.block.type = Material.AIR
    }
}