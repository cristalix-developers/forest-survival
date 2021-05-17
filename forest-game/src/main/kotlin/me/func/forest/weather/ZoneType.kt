package me.func.forest.weather

import me.func.forest.user.User
import java.util.function.Consumer

enum class ZoneType(val playerIn: Consumer<User>) {

    NEUTRAL({ it.normalizeTemperature(0.01) }),
    COLD({
        it.changeTemperature(-0.01)
        me.func.forest.knowledge.Knowledge.COLD.tryGive(it)
    }), HOT({
        it.changeTemperature(0.02)
        me.func.forest.knowledge.Knowledge.HOT.tryGive(it)
    }), BONFIRE({ it.changeTemperature(0.07) }),;
}