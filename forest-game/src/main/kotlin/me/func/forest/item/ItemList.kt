package me.func.forest.item

import me.func.forest.item.ItemHelper.item
import me.func.forest.item.click.ThrowableItem
import org.bukkit.Material.*
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import ru.cristalix.core.formatting.Color
import java.util.function.BiConsumer

enum class ItemList(val item: ItemStack, val onClick: BiConsumer<ItemList, PlayerInteractEvent>?) {

    STONE3(item("Камень в полете", STONE), null),
    APPLE3(item("Яблоко в полете", APPLE), null),

    ARROW1(item("Стрела", 4, ARROW, listOf("Орудие дальнего боя.")), null),
    POISONED_ARROW1(item("Отравленная стрела", 1, TIPPED_ARROW, listOf("Отравленное орудие дальнего боя."), PotionMeta::class.java) {
        it.basePotionData = PotionData(PotionType.POISON)
    }, null),
    FLINT1(item("Кремень", FLINT), null),
    STICK1(item("Палка", STICK), null),
    STRING1(item("Нить", STRING), null),
    STONE1(item("Камень", FIREWORK_CHARGE), ThrowableItem(STONE3, 3.0)),
    APPLE1(item("Плод", APPLE), ThrowableItem(APPLE3, 0.5)),
    HEAL1(item("Целебная трава", INK_SACK, Color.GREEN), { _, event ->
        if (event.action == Action.RIGHT_CLICK_AIR) {
            ItemHelper.useItem(event.player)
            event.player.health += 1.4
        }
    }),
    SKULL1(item("Череп", BONE), null),
    BOW1(item("Лук", BOW, listOf("Оружие дальнего боя.")), null),

    MUSHROOM2(item("Гриб", BROWN_MUSHROOM), null),
    RED_MUSHROOM2(item("Мухомор", RED_MUSHROOM), { _, event ->
        if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            ItemHelper.useItem(event.player)
            event.player.addPotionEffect(
                PotionEffect(
                    implario.ListUtils.random(PotionEffectType.values()),
                    20 * (5 + 30 * Math.random()).toInt(),
                    1
                )
            )
        }
    }),
    SHELL2(item("Ракушка", CARPET, 10), null),
    STONE2(item("Камень блок", CARPET, 0), null),
    FULL_BUSH2(item("Куст с плодами", CARPET, 1), null),
    EMPTY_BUSH2(item("Пустой куст", CARPET, 6), null),
    TOTEM2(item("Тотем", CARPET, 4), null),
    ;
}