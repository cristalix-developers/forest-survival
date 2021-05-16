package me.func.forest.weather

import me.func.forest.app
import me.func.forest.clock.ClockInject
import me.func.forest.drop.generator.BonfireGenerator
import org.bukkit.Bukkit
import kotlin.math.pow

class ZoneManager : ClockInject {

    private var zones: List<Zone> = app.worldMeta.getLabels("zone").map {
        val contents = it.tag.split(" ")
        Zone(it, contents[0].toDouble().pow(2), ZoneType.valueOf(contents[1].toUpperCase()))
    }

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.isDead)
                continue
            val user = app.getUser(player)!!
            val zonesIn = zones
                .filter { it.inside(player.location) }
                .distinctBy { it.type }

            if (zonesIn.isEmpty())
                ZoneType.NEUTRAL.playerIn.accept(user)
            else
                zonesIn.forEach { it.type.playerIn.accept(user) }

            BonfireGenerator.MARK_FIRING
                .filter { it.inside(player.location) }
                .forEach { it.type.playerIn.accept(user) }
        }
    }

    override fun doEvery(): Int {
        return 10
    }

}