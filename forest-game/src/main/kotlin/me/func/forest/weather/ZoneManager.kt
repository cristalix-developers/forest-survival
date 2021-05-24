package me.func.forest.weather

import me.func.forest.app
import me.func.forest.channel.ModTransfer
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
    private var storm = false

    override fun run() {
        app.getWorld().time += 3

        state--
        if (state < 0) {
            var multiplier = 100.0

            if (storm)
                multiplier = 20.0

            state = Math.random() * 20.0 * multiplier
            app.getWorld().weatherDuration = state.toInt()
            app.getWorld().setStorm(storm)
            storm = !storm
        }

        for (player in Bukkit.getOnlinePlayers()) {
            if (player.isDead)
                continue
            val user = app.getUser(player)!!

            if (!user.hasLevel(3))
                return

            var weather = ""

            val zonesIn = zones
                .filter { it.inside(player.location) }
                .distinctBy { it.type }

            if (zonesIn.isEmpty()) {
                ZoneType.NEUTRAL.playerIn.accept(user)
            } else {
                zonesIn.forEach {
                    it.type.playerIn.accept(user)
                    weather += it.type.title + " "
                }
            }

            if (app.getWorld().hasStorm()) {
                ZoneType.RAIN.playerIn.accept(user)
                weather += ZoneType.RAIN.title + " "
            }

            if (app.getWorld().time in 12301..23849) {
                ZoneType.NIGHT.playerIn.accept(user)
                weather += ZoneType.NIGHT.title + " "
            }

            BonfireGenerator.MARK_FIRING
                .filter { it.inside(player.location) }
                .forEach {
                    it.type.playerIn.accept(user)
                    if (!weather.contains(ZoneType.BONFIRE.title))
                        weather += ZoneType.BONFIRE.title + " "
                }

            ModTransfer()
                .string(weather)
                .send("weather-update", user)
        }
    }

    override fun doEvery(): Int {
        return 10
    }

}