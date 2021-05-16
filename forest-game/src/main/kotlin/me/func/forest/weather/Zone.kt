package me.func.forest.weather

import org.bukkit.Location
import kotlin.math.pow

class Zone(private val center: Location, private val radiusSquared: Double, val type: ZoneType) {

    fun inside(location: Location): Boolean {
        return (center.x - location.x).pow(2) +
                (center.z - location.z).pow(2) < radiusSquared
    }

}