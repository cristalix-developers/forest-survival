package me.func.forest.user

import me.func.forest.item.ItemList
import me.func.forest.knowledge.Knowledge
import ru.cristalix.core.math.V3

data class Stat (
    var tutorial: Boolean,
    var health: Double,
    var heart: Int,
    var exp: Int,
    var placeLevel: Int,
    var place: V3?,
    var placeInventory: MutableList<Pair<ItemList, Int>>?,
    var temperature: Double,
    var maxHeart: Int,
    var lastEntry: Long,
    var timeAlive: Long,
    var knowledge: MutableList<Knowledge>
)