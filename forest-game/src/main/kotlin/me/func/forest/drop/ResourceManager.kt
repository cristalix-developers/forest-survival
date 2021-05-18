package me.func.forest.drop

import me.func.forest.app
import me.func.forest.drop.booty.BonfireBooty
import me.func.forest.drop.generator.BonfireGenerator
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class ResourceManager : Listener {

    private var resources: Map<Location, Resources> = app.worldMeta.getLabels("drop")
        .associate { it.toBlockLocation() to Resources.valueOf(it.tag.toUpperCase()) }

    init {
        resources.forEach { (location, resource) -> resource.generate(location) }
    }

    @EventHandler
    fun BlockBreakEvent.handle() {

        val resource = resources[block.location]
        val item = resource?.generator?.getStand()?.item
        if (block.typeId == item?.typeId && block.data == item.data.data)
            resource.booty(block.location, player)

        val bonfire = BonfireGenerator.BONFIRES[block.location]
        if (bonfire != null)
            BonfireBooty.get(Resources.FIRE, block.location, app.getUser(player)!!)

        cancel = true
    }
}