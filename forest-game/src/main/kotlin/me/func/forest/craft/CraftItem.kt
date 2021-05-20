package me.func.forest.craft

import me.func.forest.item.ItemList
import me.func.forest.item.ItemList.*

enum class CraftItem(val minLevel: Int, val to: ItemList, val from: Iterable<Pair<ItemList, Int>>) {
    BOW(2, BOW1, listOf(Pair(STRING1, 12), Pair(STICK1, 8))),
    ARROW(2, ARROW1, listOf(Pair(STRING1, 1), Pair(STICK1, 1), Pair(FLINT1, 1))),
    POISONED_ARROW(2, POISONED_ARROW1, listOf(Pair(ARROW1, 1), Pair(RED_MUSHROOM2, 1))),
    FIRE(3, BONFIRE_OFF2, listOf(Pair(STONE1, 8), Pair(STICK1, 8))),
    FLINT_AND_STEEL(3, FLINT_AND_STEEL1, listOf(Pair(FLINT1, 2))),
    LEATHER_HELMET(2, LEATHER_HELMET1, listOf(Pair(STRING1, 8), Pair(LEATHER1, 5))),
    LEATHER_CHEST(2, LEATHER_CHEST1, listOf(Pair(STRING1, 10), Pair(LEATHER1, 8))),
    LEATHER_LEGGINGS(2, LEATHER_LEGGINGS1, listOf(Pair(STRING1, 9), Pair(LEATHER1, 7))),
    LEATHER_BOOTS(2, LEATHER_BOOTS1, listOf(Pair(STRING1, 7), Pair(LEATHER1, 4))),
    TENT_START(2, TENT1, listOf(Pair(STRING1, 16), Pair(LEATHER1, 8), Pair(STICK1, 8))),
    STONE_AXE(3, STONE_AXE1, listOf(Pair(STICK1, 3), Pair(STONE1, 3), Pair(SHELL2, 2))),
    IRON_SWORD(5, IRON_SWORD1, listOf(Pair(IRON1, 8), Pair(STICK1, 2), Pair(STRING1, 24), Pair(SHELL2, 24), Pair(SKULL1, 4))),
    IRON_HELMET(5, IRON_HELMET1, listOf(Pair(IRON1, 64), Pair(SHELL2, 52), Pair(SKULL1, 1))),
    IRON_CHEST(5, IRON_CHEST1, listOf(Pair(IRON1, 96), Pair(SHELL2, 72), Pair(SKULL1, 3))),
    IRON_LEGGINGS(5, IRON_LEGGINGS1, listOf(Pair(IRON1, 72), Pair(SHELL2, 62), Pair(SKULL1, 2))),
    IRON_BOOTS(5, IRON_BOOTS1, listOf(Pair(IRON1, 52), Pair(SHELL2, 36), Pair(SKULL1, 1))),
    ;
}