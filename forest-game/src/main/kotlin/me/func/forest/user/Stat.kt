package me.func.forest.user

import implario.math.V3
import me.func.forest.item.ItemList
import me.func.forest.knowledge.Knowledge

data class Stat (
    var tutorial: Boolean,
    var health: Double,
    var heart: Int,
    var exp: Int,
    var place: V3?,
    var placeInventory: List<Pair<ItemList, Int>>,
    var temperature: Double,
    var maxHeart: Int,
    var lastEntry: Long,
    var timeAlive: Long,
    var knowledge: MutableList<Knowledge>
)