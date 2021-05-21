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

    private var state = 0.0
    private var weather = false

    override fun run() {
        app.getWorld().time += 2

        state--
        if (state < 0) {
            state = Math.random() * 20.0 * 300.0
            app.getWorld().weatherDuration = (Math.random() * 20.0 * 300.0).toInt()
            app.getWorld().setStorm(weather)
            weather = !weather
        }

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