package me.func.forest.user

data class Stat (
    var tutorial: Boolean,
    var health: Double,
    var heart: Int,
    var exp: Int,
    var temperature: Double,
    var maxHeart: Int,
    var lastEntry: Long,
    var timeAlive: Long,
)