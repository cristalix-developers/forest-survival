package me.reidj.forest.drop.booty

import me.reidj.forest.drop.block.BlockUnit
import me.reidj.forest.user.User
import org.bukkit.Location

@FunctionalInterface
interface Booty {

    fun get(resource: BlockUnit, location: Location, user: User)

}