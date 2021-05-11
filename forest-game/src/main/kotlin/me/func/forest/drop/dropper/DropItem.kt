package me.func.forest.drop.dropper

import me.func.forest.drop.Resources
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

object DropItem : Dropper {

    private val TOP = Vector(0.0, 0.5, 0.0)

    override fun drop(resource: Resources, location: Location, player: Player?) {
        val item = location.world.dropItemNaturally(location, resource.item)
        item.velocity = TOP
    }

    fun drop(location: Location, vectorRandom: Double, items: Iterable<ItemStack>) {
        for (item in items) {
            val drop = location.world.dropItemNaturally(location, item)
            drop.velocity = Vector((Math.random() - 0.5) * vectorRandom, 0.5, (Math.random() - 0.5) * vectorRandom)
        }
    }
}