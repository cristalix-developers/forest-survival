package me.func.forest.drop.generator

import clepto.bukkit.B
import me.func.forest.drop.block.BlockPlacer
import me.func.forest.drop.block.BlockUnit
import me.func.forest.item.ItemList
import me.func.forest.weather.Zone
import me.func.forest.weather.ZoneType
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
        B.postpone(wait) {
            val time = BONFIRES[location]
            if (time != null) {
                BONFIRES[location] = time - wait
            }
            if (time != null && time > 0) {
                tryRemove(time - wait + 1, location, zone)
                return@postpone
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