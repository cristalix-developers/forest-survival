package me.func.forest.user

data class Stat (
    var tutorial: Boolean,
    var health: Double,
    var heart: Int,
    var thirst: Double,
    var exp: Int,
    var money: Double,
    var maxHeart: Int,
    var lastEntry: Long,
    var timeAlive: Long,
)