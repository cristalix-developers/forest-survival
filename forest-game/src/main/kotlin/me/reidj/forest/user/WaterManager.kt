package me.reidj.forest.user

import me.reidj.forest.app
import me.reidj.forest.channel.ModHelper
import me.reidj.forest.clock.ClockInject
import org.bukkit.Bukkit

object WaterManager : ClockInject {

    override fun run() {
        Bukkit.getOnlinePlayers()
            .map { app.getUser(it) }
            .forEach {
                if (it?.stat?.waterAmount!! > 1) {
                    if (it.stat.temperature >= 37.0) it.stat.waterAmount -= 2 else it.stat.waterAmount -= 1
                    ModHelper.waterAmountUpdate(it)
                } else {
                    if (it.player?.health == 2.0)
                        return@forEach
                    it.player?.damage(1.0)
                }
            }
    }

    override fun doEvery(): Int {
        return 2000
    }
}