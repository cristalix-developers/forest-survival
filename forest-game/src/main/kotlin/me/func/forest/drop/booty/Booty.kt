package me.func.forest.drop.booty

import me.func.forest.drop.Resources
import me.func.forest.user.User
import org.bukkit.Location

@FunctionalInterface
interface Booty {

    fun get(resource: Resources, location: Location, user: User)

}