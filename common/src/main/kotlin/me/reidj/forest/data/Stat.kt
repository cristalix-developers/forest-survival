package me.reidj.forest.data

import me.reidj.forest.data.Item
import me.reidj.forest.data.Knowledge
import ru.cristalix.core.math.V3
import java.util.*

/**
 * @project : forest
 * @author : Рейдж
 **/
data class Stat(
    var uuid: UUID,
    var tutorial: Boolean,
    var health: Double,
    var deaths: Int,
    var kills: Int,
    var killMobs: Int,
    var exp: Int,
    var placeLevel: Int,
    var temperature: Double,
    var lastEntry: Double,
    var exit: V3?,
    var place: V3?,
    var tentInventory: MutableList<Item>,
    var playerInventory: MutableList<Item>,
    var knowledge: MutableList<Knowledge>,
)
