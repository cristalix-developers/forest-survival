package me.reidj.forest.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @project : forest
 * @author : Рейдж
 **/

val BARRIER = ItemStack(Material.BARRIER)

fun ItemStack.text(value: String) = apply {
    val strings = value.replace('&', '§').split("\n")
    val meta = itemMeta
    meta.displayName = strings.first()
    meta.displayName
    meta.lore = strings.drop(1).map { it.trimStart() }
    itemMeta = meta
}

fun ItemStack.data(value: Short) = apply { durability = value }
fun item(material: Material) = ItemStack(material)

fun item(itemStack: ItemStack, apply: ItemStack.() -> Unit) = apply.invoke(itemStack)
fun item(apply: ItemStack.() -> Unit) = ItemStack(Material.CLAY_BALL).apply { apply.invoke(this) }