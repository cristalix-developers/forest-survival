package me.reidj.forest.util

/**
 * @project : forest
 * @author : Рейдж
 **/
enum class Images {
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
    MAGAZINE,
    IMMORTALITY,
    REGENERATION,
    RUNNING,
    WATER,
    ;

    fun path() = "https://storage.c7x.dev/reidj/forest/${name.lowercase()}.png"
}