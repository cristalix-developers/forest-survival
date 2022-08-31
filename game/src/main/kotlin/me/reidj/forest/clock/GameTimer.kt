package me.reidj.forest.clock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@FunctionalInterface
interface ClockInject {

    fun run()

    fun doEvery(): Int

}

class GameTimer(private val injects: List<ClockInject>) : () -> Unit {

    private var tick = 0
    private val maxTick = 1000000000

    private val scope = CoroutineScope(Dispatchers.Default)
    private val mutex = Mutex()

    override fun invoke() {
        if (mutex.isLocked) return
        scope.launch {
            mutex.withLock {
                tick++

                if (tick > maxTick)
                    tick = 0

                injects.forEach {
                    if (tick % it.doEvery() == 0)
                        it.run()
                }
            }
        }
    }
}