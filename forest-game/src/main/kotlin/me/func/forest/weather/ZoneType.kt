package me.func.forest.weather

import me.func.forest.user.User
import java.util.function.Consumer

enum class ZoneType(val title: String, val playerIn: Consumer<User>) {

    NEUTRAL("§fЯсно §a➕§7", {
        it.normalizeTemperature(0.002)
    }),
    COLD("§fМороз §b▼§7", {
        it.changeTemperature(-0.008)
        me.func.forest.knowledge.Knowledge.COLD.tryGive(it)
    }),
    HOT("§fЖара §c▲§7", {
        it.changeTemperature(0.01)
        me.func.forest.knowledge.Knowledge.HOT.tryGive(it)
    }),
    BONFIRE("§fКостер §c▲§7", {
        it.changeTemperature(0.022)
    }),
    RAIN("§fДождь §b▼§7", {
        it.changeTemperature(-0.005)
    }),
    NIGHT("§fНочь §b▼§7", {
        it.changeTemperature(-0.007)
    }),;
}