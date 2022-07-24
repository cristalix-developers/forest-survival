package me.reidj.forest.weather

import me.reidj.forest.user.User
import java.util.function.Consumer

enum class ZoneType(val title: String, val playerIn: Consumer<User>) {

    NEUTRAL("§fЯсно §a➕§7", {
        it.normalizeTemperature(0.002)
    }),
    COLD("§fМороз §b▼§7", {
        it.changeTemperature(-0.008)
        it.knowledgeTryGive(me.reidj.forest.data.Knowledge.COLD)
    }),
    HOT("§fЖара §c▲§7", {
        it.changeTemperature(0.01)
        it.knowledgeTryGive(me.reidj.forest.data.Knowledge.HOT)
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