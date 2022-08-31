package me.reidj.forest.channel.item

import me.func.mod.util.nbt
import me.reidj.forest.util.text
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta
import ru.cristalix.core.formatting.Color
import ru.cristalix.core.item.Items
import java.util.function.Consumer

object ItemHelper {

    fun useItem(player: Player) = player.itemInHand.run { setAmount(getAmount() - 1) }

    fun tryUseItem(player: Player, itemList: ItemList) = player.inventory.run { toMutableList().removeIf { containsAtLeast(itemList.item, 1) } }

    fun item(name: String, type: Material, color: Color) = Items.builder()
        .displayName(name)
        .type(type)
        .color(color)
        .build()

    fun <T : ItemMeta> item(
        name: String,
        count: Int,
        type: Material,
        lore: Iterable<String>,
        clazz: Class<T>,
        consumer: Consumer<T>
    ) = Items.builder()
        .displayName(name)
        .amount(count)
        .type(type)
        .meta(clazz, consumer)
        .flags(ItemFlag.HIDE_ATTRIBUTES)
        .lore(mutableListOf("").plus(lore))
        .build()

    fun item(name: String, material: Material, code: String) = me.reidj.forest.util.item(material)
        .text(name).nbt("code", code)
}