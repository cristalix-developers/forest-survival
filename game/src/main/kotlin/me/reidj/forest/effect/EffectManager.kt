package me.reidj.forest.effect

import me.func.mod.conversation.ModTransfer
import me.reidj.forest.app
import me.reidj.forest.channel.ModHelper
import me.reidj.forest.clock.ClockInject
import me.reidj.forest.data.Effect
import org.bukkit.Bukkit

/**
 * @project : forest
 * @author : Рейдж
 **/
class EffectManager : ClockInject {

    companion object {
        val effects = mutableMapOf<String, Effect>()
    }

    override fun run() {
        Bukkit.getOnlinePlayers().mapNotNull { app.getUser(it) }.filter { it.stat.activeEffects.isNotEmpty() }
            .forEach { user ->
                val stat = user.stat
                with(stat.activeEffects.iterator()) {
                    forEach {
                        if (it.duration == 1) {
                            ModTransfer(it.uuid.toString()).send("forest:effect-remove", user.player)
                            remove()
                            effects.remove(it.objectName)
                        } else {
                            it.duration--
                            ModHelper.activeEffect(false, user, it)
                        }
                    }
                }
            }
    }

    override fun doEvery() = 20
}