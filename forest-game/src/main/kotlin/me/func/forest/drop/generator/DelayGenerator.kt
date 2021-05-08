package me.func.forest.drop.generator

import clepto.bukkit.B
import me.func.forest.drop.Resources
import org.bukkit.Location
import org.bukkit.Material

class DelayGenerator(private val block: Pair<Material, Byte>, private val waitSeconds: Int) : Generator {
    override fun generate(resource: Resources, location: Location) {
        B.postpone(20 * waitSeconds) {
            location.block.type = block.first
            location.block.data = block.second
        }
    }
}