package me.func.forest.drop.generator

import clepto.bukkit.B
import me.func.forest.drop.Resources
import org.bukkit.Location
import org.bukkit.Material

class DelayGenerator(private val stand: Pair<Material, Byte>, private val waitSeconds: Int) : Generator<Pair<Material, Byte>> {

    constructor(block: Material, waitSeconds: Int) : this(Pair(block, 0), waitSeconds)

    override fun generate(resource: Resources, location: Location) {
        B.postpone(20 * waitSeconds) {
            location.block.setTypeAndDataFast(stand.first.id, stand.second)
        }
    }

    override fun getStand(): Pair<Material, Byte> {
        return stand
    }
}