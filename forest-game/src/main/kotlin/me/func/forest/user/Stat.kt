package me.func.forest.user

data class Stat (
    var tutorial: Boolean,
    var health: Double,
    var heart: Int,
    var thirst: Double,
    var level: Int,
    var money: Double,
    var maxHeart: Int,
    var lastEntry: Long,
    var timeAlive: Long,
)