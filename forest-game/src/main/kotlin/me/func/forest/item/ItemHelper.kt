package me.func.forest.item

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import ru.cristalix.core.formatting.Color
import ru.cristalix.core.item.Items
import java.util.function.Consumer

object ItemHelper {

    fun useItem(player: Player) {
        val hand = player.itemInHand
        hand.setAmount(hand.getAmount() - 1)
        player.itemInHand = hand
    }

    fun item(name: String, type: Material, data: Byte): ItemStack {
        return Items.builder()
            .displayName(name)
            .type(type)
            .damage(data.toShort())
            .build()
    }

    fun item(type: Material): ItemStack {
        return Items.builder()
            .type(type)
            .build()
    }

    fun item(name: String, type: Material): ItemStack {
        return Items.builder()
            .displayName(name)
            .type(type)
            .build()
    }

    fun item(name: String, count: Int, type: Material): ItemStack {
        return Items.builder()
            .displayName(name)
            .amount(count)
            .type(type)
            .build()
    }

    fun <T : ItemMeta> item(name: String, count: Int, type: Material, clazz: Class<T>, consumer: Consumer<T>): ItemStack {
        return Items.builder()
            .displayName(name)
            .amount(count)
            .type(type)
            .meta(clazz, consumer)
            .build()
    }

    fun item(name: String, type: Material, color: Color): ItemStack {
        return Items.builder()
            .displayName(name)
            .type(type)
            .color(color)
            .build()
    }

    fun item(name: String, type: Material, lore: Iterable<String>): ItemStack {
        return Items.builder()
            .displayName(name)
            .flags(ItemFlag.HIDE_ATTRIBUTES)
            .type(type)
            .lore(mutableListOf("").plus(lore))
            .build()
    }

    fun item(name: String, count: Int, type: Material, lore: Iterable<String>): ItemStack {
        return Items.builder()
            .displayName(name)
            .amount(count)
            .type(type)
            .flags(ItemFlag.HIDE_ATTRIBUTES)
            .lore(mutableListOf("").plus(lore))
            .build()
    }

    fun <T : ItemMeta> item(name: String, count: Int, type: Material, lore: Iterable<String>, clazz: Class<T>, consumer: Consumer<T>): ItemStack {
        return Items.builder()
            .displayName(name)
            .amount(count)
            .type(type)
            .meta(clazz, consumer)
            .flags(ItemFlag.HIDE_ATTRIBUTES)
            .lore(mutableListOf("").plus(lore))
            .build()
    }

}