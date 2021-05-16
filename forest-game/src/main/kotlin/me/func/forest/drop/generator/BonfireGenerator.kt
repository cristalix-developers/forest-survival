package me.func.forest.drop.generator

import clepto.bukkit.B
import me.func.forest.drop.BlockPlacer
import me.func.forest.drop.Resources
import me.func.forest.item.ItemList
import me.func.forest.weather.Zone
import me.func.forest.weather.ZoneType
import org.bukkit.Location
import kotlin.math.pow

object BonfireGenerator : Generator {

    val BONFIRES: MutableMap<Location, Boolean> = mutableMapOf() // todo: load from disk
    val MARK_FIRING: MutableList<Zone> = arrayListOf()
    const val TICKS_FIRE = 20 * 12
    private const val HOT_RADIUS = 5.0

    override fun generate(resource: Resources, location: Location) {
        if (BONFIRES[location] == null) {
            BlockPlacer.place(ItemList.BONFIRE_OFF2, location)
            BONFIRES[location] = false
            return
        }
        BlockPlacer.place(ItemList.BONFIRE_ON2, location)
        BONFIRES[location] = true

        val zone = Zone(location, HOT_RADIUS.pow(2), ZoneType.BONFIRE)
        MARK_FIRING.add(zone)

        B.postpone(TICKS_FIRE) {
            BlockPlacer.place(ItemList.BONFIRE_OFF2, location)
            BONFIRES[location] = false
            MARK_FIRING.remove(zone)
        }
    }

    override fun getStand(): ItemList {
        return ItemList.BONFIRE_OFF2
    }

}