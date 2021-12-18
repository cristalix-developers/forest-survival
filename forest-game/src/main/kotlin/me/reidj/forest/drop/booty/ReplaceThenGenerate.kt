package me.reidj.forest.drop.booty

import clepto.bukkit.B
import me.reidj.forest.drop.block.BlockPlacer
import me.reidj.forest.drop.block.BlockUnit
import me.reidj.forest.item.ItemList
import me.reidj.forest.user.User
import org.bukkit.Location

class ReplaceThenGenerate(private val block: ItemList) : Booty {

    override fun get(resource: BlockUnit, location: Location, user: User) {
        DropThenGenerate.get(resource, location, user)
        B.postpone(1) { BlockPlacer.place(block, location) }
    }
}