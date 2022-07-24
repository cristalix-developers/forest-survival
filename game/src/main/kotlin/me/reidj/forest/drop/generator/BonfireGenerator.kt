package me.reidj.forest.drop.generator

import me.func.mod.util.after
import me.reidj.forest.channel.item.ItemList
import me.reidj.forest.drop.block.BlockPlacer
import me.reidj.forest.drop.block.BlockUnit
import me.reidj.forest.weather.Zone
import me.reidj.forest.weather.ZoneType
import org.bukkit.Location
import kotlin.math.pow

object BonfireGenerator : Generator {

    val BONFIRES: MutableMap<Location, Int> = mutableMapOf() // todo: load from disk
    val MARK_FIRING: MutableList<Zone> = arrayListOf()
    const val NORMAL_TICKS_FIRE = 20 * 120
    private const val HOT_RADIUS = 4.0

    override fun generate(resource: BlockUnit, location: Location) {
        if (BONFIRES[location] == null) {
            BlockPlacer.place(ItemList.BONFIRE_OFF2, location)
            BONFIRES[location] = 0
            return
        }
        BlockPlacer.place(ItemList.BONFIRE_ON2, location)
        BONFIRES[location] = NORMAL_TICKS_FIRE

        val zone = Zone(location, HOT_RADIUS.pow(2), ZoneType.BONFIRE)
        MARK_FIRING.add(zone)

        tryRemove(NORMAL_TICKS_FIRE, location, zone)
    }

    private fun tryRemove(wait: Int, location: Location, zone: Zone) {
        after(wait.toLong()) {
            val time = BONFIRES[location]
            if (time != null) {
                BONFIRES[location] = time - wait
            }
            if (time != null && time > 0) {
                tryRemove(time - wait + 1, location, zone)
                return@after
            }
            BlockPlacer.place(ItemList.BONFIRE_OFF2, location)
            BONFIRES[location] = 0
            MARK_FIRING.remove(zone)
        }
    }

    override fun getStand(): ItemList {
        return ItemList.BONFIRE_OFF2
    }

}