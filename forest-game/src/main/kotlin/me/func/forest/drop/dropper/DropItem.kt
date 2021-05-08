package me.func.forest.drop.dropper

import me.func.forest.drop.Resources
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

object DropItem : Dropper {
    override fun drop(resource: Resources, location: Location, player: Player?) {
        val item = location.world.dropItemNaturally(location, resource.item)
        item.velocity = Vector(0.0, 0.4, 0.0)
    }
}