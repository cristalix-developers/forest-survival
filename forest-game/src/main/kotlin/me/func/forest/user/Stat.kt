package me.func.forest.user

import me.func.forest.knowledge.Knowledge

data class Stat (
    var tutorial: Boolean,
    var health: Double,
    var heart: Int,
    var exp: Int,
    var temperature: Double,
    var maxHeart: Int,
    var lastEntry: Long,
    var timeAlive: Long,
    var knowledge: MutableList<Knowledge>
)