package me.reidj.forest.util

/**
 * @project : forest
 * @author : Рейдж
 **/
enum class Images {
    HEALTH_BAR,
    XP_BAR,
    ENERGY,
    FIRST,
    SECOND,
    THIRD,
    FOURTH,
    FIFTH,
    COLD,
    HOT,
    APPLE,
    STONE,
    FLINT,
    RED_MUSHROOM,
    HEAL,
    HEALTH,
    WATER,
    FOOD,
    ;

    fun path() = "https://storage.c7x.dev/reidj/forest/${name.lowercase()}.png"
}