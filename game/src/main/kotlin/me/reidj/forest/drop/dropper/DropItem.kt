package me.reidj.forest.drop.dropper

import me.reidj.forest.channel.item.ItemList
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

object DropItem : Dropper {

    private val TOP = Vector(0.0, 0.5, 0.0)

    override fun drop(drop: ItemList, location: Location, player: Player?) {
        val item = location.world.dropItemNaturally(location, drop.item)
        item.velocity = TOP
    }

    fun drop(location: Location, vectorRandom: Double, items: Iterable<ItemStack>) {
        val centredLocation = location.clone().add(0.5, 0.0, 0.5)

        for (item in items) {
            val drop = location.world.dropItemNaturally(centredLocation, item)
            drop.velocity = Vector((Math.random() - 0.5) * vectorRandom, 0.5, (Math.random() - 0.5) * vectorRandom)
        }
    }
}