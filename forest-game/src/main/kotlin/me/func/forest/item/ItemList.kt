package me.func.forest.item

import me.func.forest.app
import me.func.forest.item.ItemHelper.item
import me.func.forest.knowledge.Knowledge
import org.bukkit.Material.*
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import ru.cristalix.core.formatting.Color
import java.util.function.BiConsumer

enum class ItemList(val item: ItemStack, val on: Map<Class<out PlayerEvent>, BiConsumer<ItemList, PlayerEvent>>?) {

    STONE3(item("Камень в полете", STONE), null),
    APPLE3(item("Яблоко в полете", APPLE), null),

    ARROW1(item("Стрела", 4, ARROW, listOf("Орудие дальнего боя.")), null),
    POISONED_ARROW1(
        item(
            "Отравленная стрела",
            1,
            TIPPED_ARROW,
            listOf("Отравленное орудие дальнего боя."),
            PotionMeta::class.java
        ) {
            it.basePotionData = PotionData(PotionType.POISON)
        }, null
    ),
    FLINT1(item("Кремень", FLINT), mapOf(StandardsHandlers.knowledgeItem(Knowledge.FLINT))),
    FLINT_AND_STEEL1(
        item("Поджег костра", FLINT_AND_STEEL, listOf("Необходимо для разжега костра.")), mapOf(
            PlayerInteractEvent::class.java to BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                if (event.action == Action.RIGHT_CLICK_BLOCK) {
                    val location = event.blockClicked.location
                    val fire = me.func.forest.drop.generator.BonfireGenerator.BONFIRES[location]
                    if (fire != null && !fire) {
                        me.func.forest.drop.Resources.FIRE.generate(location)
                        ItemHelper.useItem(event.player)
                        org.bukkit.Bukkit.getOnlinePlayers().forEach {
                            me.func.forest.channel.ModTransfer()
                                .double(location.x + 0.5)
                                .double(location.y + 1.4)
                                .double(location.z + 0.5)
                                .integer(me.func.forest.drop.generator.BonfireGenerator.TICKS_FIRE)
                                .send("bonfire-new", app.getUser(it)!!)
                        }
                    }
                }
            })
    ),
    STICK1(item("Палка", STICK), null),
    STRING1(item("Нить", STRING), null),
    STONE1(
        item("Камень", FIREWORK_CHARGE),
        mapOf(
            StandardsHandlers.throwableItem(STONE3, 3.0),
            StandardsHandlers.knowledgeItem(Knowledge.STONE)
        )
    ),
    APPLE1(
        item("Плод", APPLE),
        mapOf(
            StandardsHandlers.throwableItem(APPLE3, 1.2),
            StandardsHandlers.knowledgeItem(Knowledge.APPLE)
        )
    ),
    HEAL1(
        item("Целебная трава", INK_SACK, Color.LIME), mapOf(
            PlayerInteractEvent::class.java to BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                if (event.action == Action.RIGHT_CLICK_AIR) {
                    ItemHelper.useItem(event.player)
                    event.player.health += 1.7
                }
            }, StandardsHandlers.knowledgeItem(Knowledge.HEAL))
    ),
    SKULL1(item("Череп", BONE), null),
    BOW1(item("Лук", BOW, listOf("Оружие дальнего боя.")), null),

    MUSHROOM2(item("Гриб", BROWN_MUSHROOM), null),
    RED_MUSHROOM2(
        item("Мухомор", RED_MUSHROOM), mapOf(
            PlayerInteractEvent::class.java to BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
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
            }, StandardsHandlers.knowledgeItem(Knowledge.TOXIC))
    ),
    SHELL2(item("Ракушка", CARPET, 10), null),
    STONE2(item("Камень блок", CARPET), null),
    FULL_BUSH2(item("Куст с плодами", CARPET, 1), null),
    EMPTY_BUSH2(item("Пустой куст", CARPET, 6), null),
    TOTEM2(item("Тотем", CARPET, 4), null),

    BONFIRE_OFF2(
        item("Потухший костер", CARPET, 14, listOf("Согревает в холоде.")), mapOf(
            PlayerInteractEvent::class.java to BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                val location = event.blockClicked.location
                if (event.action == Action.RIGHT_CLICK_BLOCK && me.func.forest.Postulates.isGround(location)) {
                    ItemHelper.useItem(event.player)
                    me.func.forest.drop.Resources.FIRE.generate(location.clone().add(0.0, 1.0, 0.0))
                }
            })
    ),
    BONFIRE_ON2(item("Горящий костер", TORCH), null),
    ;
}