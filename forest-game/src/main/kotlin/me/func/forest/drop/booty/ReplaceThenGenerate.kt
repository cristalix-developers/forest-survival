package me.func.forest.drop.booty

import clepto.bukkit.B
import me.func.forest.drop.Resources
import me.func.forest.item.ItemList
import org.bukkit.Location
import org.bukkit.entity.Player

class ReplaceThenGenerate(private val block: ItemList) : Booty {

    override fun get(resource: Resources, location: Location, player: Player) {
        DropThenGenerate.get(resource, location, player)
        B.postpone(1) { location.block.setTypeAndDataFast(block.item.typeId, block.item.getData().data) }
    }
}