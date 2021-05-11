package me.func.forest.drop.booty

import clepto.bukkit.B
import me.func.forest.drop.Resources
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

class ReplaceThenGenerate(private val block: Pair<Material, Byte>) : Booty {

    constructor(block: Material) : this(Pair(block, 0))

    override fun get(resource: Resources, location: Location, player: Player) {
        DropThenGenerate.get(resource, location, player)
        B.postpone(1) { location.block.setTypeAndDataFast(block.first.id, block.second) }
    }
}