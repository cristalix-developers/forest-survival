package me.func.forest.craft

import me.func.forest.item.ItemList
import me.func.forest.item.ItemList.*

enum class CraftItem(val to: ItemList, val from: Iterable<Pair<ItemList, Int>>) {
    ARROW(ARROW1, listOf(Pair(STRING1, 1), Pair(STICK1, 1), Pair(FLINT1, 1))),
    POISONED_ARROW(POISONED_ARROW1, listOf(Pair(ARROW1, 1), Pair(RED_MUSHROOM2, 1))),
    BOW(BOW1, listOf(Pair(STRING1, 12), Pair(STICK1, 8))),
    ;

}