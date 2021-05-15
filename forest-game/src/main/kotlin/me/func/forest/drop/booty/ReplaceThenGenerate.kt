package me.func.forest.drop.booty

import clepto.bukkit.B
import me.func.forest.drop.BlockPlacer
import me.func.forest.drop.Resources
import me.func.forest.item.ItemList
import me.func.forest.user.User
import org.bukkit.Location

class ReplaceThenGenerate(private val block: ItemList) : Booty {

    override fun get(resource: Resources, location: Location, user: User) {
        DropThenGenerate.get(resource, location, user)
        B.postpone(1) { BlockPlacer.place(block, location) }
    }
}