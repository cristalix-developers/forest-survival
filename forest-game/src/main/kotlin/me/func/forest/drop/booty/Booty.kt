package me.func.forest.drop.booty

import me.func.forest.drop.block.BlockUnit
import me.func.forest.user.User
import org.bukkit.Location

@FunctionalInterface
interface Booty {

    fun get(resource: BlockUnit, location: Location, user: User)

}