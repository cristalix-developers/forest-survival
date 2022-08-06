package me.reidj.forest.clock

import org.bukkit.scheduler.BukkitRunnable

@FunctionalInterface
interface ClockInject {

    fun run()

    fun doEvery(): Int

}

class GameTimer(private val injects: List<ClockInject>) : BukkitRunnable() {

    private var tick = 0
    private val maxTick = 1000000000

    override fun run() {
        tick++

        if (tick > maxTick)
            tick = 0

        injects.forEach {
            if (tick % it.doEvery() == 0)
                it.run()
        }
    }
}