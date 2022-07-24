package me.reidj.forest

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @project : forest
 * @author : Рейдж
 **/

fun ItemStack.text(value: String) = apply {
    val strings = value.replace('&', '§').split("\n")
    val meta = itemMeta
    meta.displayName = strings.first()
    meta.displayName
    meta.lore = strings.drop(1).map { it.trimStart() }
    itemMeta = meta
}

fun ItemStack.data(value: Short) = apply { durability = value }
fun item(material: Material): ItemStack = ItemStack(material)

fun item(itemStack: ItemStack, apply: ItemStack.() -> Unit): Unit = apply.invoke(itemStack)
fun item(apply: ItemStack.() -> Unit): ItemStack = ItemStack(Material.CLAY_BALL).apply { apply.invoke(this) }