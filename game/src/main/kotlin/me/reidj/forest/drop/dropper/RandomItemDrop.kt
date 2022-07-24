package me.reidj.forest.drop.dropper

import implario.ListUtils
import me.reidj.forest.channel.item.ItemList
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class RandomItemDrop(private val maxDropCount: Int, private vararg val drop: ItemList) : Dropper {

    override fun drop(drop: ItemList, location: Location, player: Player?) {
        val ableDrop = this.drop.asList()
        val finalDrop = ArrayList<ItemStack>()

        finalDrop.add(drop.item)

        repeat(1 + (Math.random() * maxDropCount).toInt()) {
            finalDrop.add(ListUtils.random(ableDrop).item)
        }

        DropItem.drop(location, 0.2, finalDrop)
    }

}