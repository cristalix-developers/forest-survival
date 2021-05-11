package me.func.forest.drop

import me.func.forest.app
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class ResourceManager : Listener {

    private var resources: Map<Location, Resources> = app.worldMeta.getLabels("drop")
        .associate { it.toCenterLocation() to Resources.valueOf(it.tag.toUpperCase()) }

    init {
        resources.forEach { (location, resource) -> resource.generate(location) }
    }

    @EventHandler
    fun blockBreakEvent(event: BlockBreakEvent) {
        val block = event.block

        resources.filter { it.key.blockX == block.x && it.key.blockY == block.y && it.key.blockZ == block.z }
            .filter { it.key.block.type == it.value.generator.getStand().first && it.key.block.data == it.value.generator.getStand().second }
            .forEach { (location, resource) -> resource.booty(location, event.player) }

        event.cancel = true
    }
}