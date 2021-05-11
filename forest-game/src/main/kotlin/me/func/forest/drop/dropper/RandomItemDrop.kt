package me.func.forest.drop.dropper

import implario.ListUtils
import me.func.forest.drop.Resources
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class RandomItemDrop(private val maxDropCount: Int, private vararg val drop: ItemStack) : Dropper {

    override fun drop(resource: Resources, location: Location, player: Player?) {
        val ableDrop = drop.asList()
        val finalDrop = ArrayList<ItemStack>()

        finalDrop.add(resource.item)

        repeat(1 + (Math.random() * maxDropCount).toInt()) {
            finalDrop.add(ListUtils.random(ableDrop))
        }

        DropItem.drop(location, 0.2, finalDrop)
    }

}