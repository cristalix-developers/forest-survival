package me.func.forest.weather

import me.func.forest.user.User
import java.util.function.Consumer

enum class ZoneType(val playerIn: Consumer<User>) {

    NEUTRAL({ it.normalizeTemperature(0.01) }),
    COLD({ it.changeTemperature(-0.01) }),
    HOT({ it.changeTemperature(0.02) }),
    BONFIRE({ it.changeTemperature(0.14) }),;

}