package me.func.forest.craft

import me.func.forest.item.ItemList
import me.func.forest.item.ItemList.*

enum class CraftItem(val minLevel: Int, val to: ItemList, val from: Iterable<Pair<ItemList, Int>>) {
    BOW(2, BOW1, listOf(Pair(STRING1, 12), Pair(STICK1, 8))),
    ARROW(2, ARROW1, listOf(Pair(STRING1, 1), Pair(STICK1, 1), Pair(FLINT1, 1))),
    POISONED_ARROW(2, POISONED_ARROW1, listOf(Pair(ARROW1, 1), Pair(RED_MUSHROOM2, 1))),
    FIRE(3, BONFIRE_OFF2, listOf(Pair(STONE1, 8), Pair(STICK1, 8))),
    FLINT_AND_STEEL(3, FLINT_AND_STEEL1, listOf(Pair(FLINT1, 2))),
    ;

}