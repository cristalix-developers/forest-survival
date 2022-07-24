package me.reidj.forest.drop.booty

import me.reidj.forest.drop.block.BlockUnit
import me.reidj.forest.drop.generator.BonfireGenerator
import me.reidj.forest.user.User
import org.bukkit.Location
import org.bukkit.Material

object BonfireBooty : Booty {
    override fun get(resource: BlockUnit, location: Location, user: User) {
        val fire = BonfireGenerator.BONFIRES[location]
        if (fire != null) {
            if (fire > 0) {
                user.player!!.fireTicks = 20 * 10
            } else {
                resource.drop(location, user.player!!)
                BonfireGenerator.BONFIRES.remove(location)
                location.block.type = Material.AIR
            }
        }
    }
}