package me.reidj.forest.craft

import me.reidj.forest.channel.item.ItemList
import me.reidj.forest.channel.item.ItemList.*

enum class CraftItem(val minLevel: Int, val to: ItemList, val from: Iterable<Pair<ItemList, Int>>) {
    MUSHROOM_SOUP(1, MUSHROOM_SOUP1, listOf(Pair(MUSHROOM2, 2), Pair(STICK1, 3))),
    BOW(2, BOW1, listOf(Pair(STRING1, 12), Pair(STICK1, 8))),
    ARROW(2, ARROW1, listOf(Pair(STRING1, 1), Pair(STICK1, 1), Pair(FLINT1, 1))),
    POISONED_ARROW(2, POISONED_ARROW1, listOf(Pair(ARROW1, 1), Pair(RED_MUSHROOM2, 1))),
    FIRE(3, BONFIRE_OFF2, listOf(Pair(STONE1, 8), Pair(STICK1, 8))),
    FURNACE(4, FURNACE2, listOf(Pair(STONE1, 32), Pair(SHELL2, 16), Pair(FLINT_AND_STEEL1, 1))),
    FLINT_AND_STEEL(3, FLINT_AND_STEEL1, listOf(Pair(FLINT1, 2))),
    LEATHER_HELMET(2, LEATHER_HELMET1, listOf(Pair(STRING1, 8), Pair(LEATHER1, 5))),
    LEATHER_CHEST(2, LEATHER_CHEST1, listOf(Pair(STRING1, 10), Pair(LEATHER1, 8))),
    LEATHER_LEGGINGS(2, LEATHER_LEGGINGS1, listOf(Pair(STRING1, 9), Pair(LEATHER1, 7))),
    LEATHER_BOOTS(2, LEATHER_BOOTS1, listOf(Pair(STRING1, 7), Pair(LEATHER1, 4))),
    STONE_AXE(3, STONE_AXE1, listOf(Pair(STICK1, 3), Pair(STONE1, 3), Pair(SHELL2, 2))),
    IRON_SWORD(5, IRON_SWORD1, listOf(Pair(IRON1, 8), Pair(STICK1, 2), Pair(STRING1, 24), Pair(SHELL2, 24), Pair(SKULL1, 4))),
    IRON_HELMET(5, IRON_HELMET1, listOf(Pair(IRON1, 64), Pair(SHELL2, 52), Pair(SKULL1, 1))),
    IRON_CHEST(5, IRON_CHEST1, listOf(Pair(IRON1, 96), Pair(SHELL2, 72), Pair(SKULL1, 3))),
    IRON_LEGGINGS(5, IRON_LEGGINGS1, listOf(Pair(IRON1, 72), Pair(SHELL2, 62), Pair(SKULL1, 2))),
    IRON_BOOTS(5, IRON_BOOTS1, listOf(Pair(IRON1, 52), Pair(SHELL2, 36), Pair(SKULL1, 1))),
    TENT_START(3, TENT1, listOf(Pair(STRING1, 16), Pair(LEATHER1, 8), Pair(STICK1, 8))),
    TENT_MIDDLE(4, TENT2, listOf(Pair(TENT1, 1), Pair(STRING1, 32), Pair(LEATHER1, 32), Pair(STICK1, 32))),
    TENT_HIGH(5, TENT3, listOf(Pair(TENT2, 1), Pair(STRING1, 64), Pair(LEATHER1, 64), Pair(STICK1, 64))),
    SPEAR(2, SPEAR1, listOf(Pair(STICK1, 4), Pair(STRING1, 4), Pair(STONE1, 1))),
    ROD(2, ROD1, listOf(Pair(STICK1, 6), Pair(STRING1, 10))),
    SPADE(2, SPADE1, listOf(Pair(STICK1, 10), Pair(STRING1, 8))),
    ;
}