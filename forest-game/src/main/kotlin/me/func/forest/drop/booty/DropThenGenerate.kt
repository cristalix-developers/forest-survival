package me.func.forest.drop.booty

import me.func.forest.channel.ModHelper
import me.func.forest.drop.block.BlockUnit
import me.func.forest.user.User
import org.bukkit.Location
import org.bukkit.Material

object DropThenGenerate : Booty {
    override fun get(resource: BlockUnit, location: Location, user: User) {
        resource.drop(location, user.player)
        resource.generate(location)
        location.block.type = Material.AIR
        user.giveExperience(resource.exp)
        ModHelper.highlight(user, "§f§l+${resource.exp} §bexp")
    }
}