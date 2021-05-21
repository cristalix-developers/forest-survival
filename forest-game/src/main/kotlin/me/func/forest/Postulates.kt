package me.func.forest

import org.bukkit.Location

object Postulates {

    val SPAWN = app.worldMeta.getLabel("guide_end")

    fun isGround(location: Location): Boolean {
        return true // todo: define this
    }

}