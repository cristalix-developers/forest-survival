package me.reidj.forest.user

import me.reidj.forest.item.ItemList
import me.reidj.forest.knowledge.Knowledge
import ru.cristalix.core.math.V3
import java.util.*

data class Stat (
    var uuid: UUID,

    var tutorial: Boolean,

    var health: Double,
    var heart: Int,
    var deaths: Int,
    var kills: Int,
    var killMobs: Int,
    var exp: Int,
    var placeLevel: Int,
    var temperature: Double,
    var maxHeart: Int,
    var lastEntry: Long,
    var timeAlive: Long,

    var exit: V3?,
    var place: V3?,

    var placeInventory: MutableList<Pair<ItemList, Int>>?,
    var knowledge: MutableList<Knowledge>
)