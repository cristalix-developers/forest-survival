package me.func.forest.item

import dev.implario.bukkit.item.item
import me.func.forest.app
import me.func.forest.item.ItemHelper.item
import me.func.forest.item.ItemHelper.useItem
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

    STONE3(item { type = STONE }.text("Камень в полете").build(), null),
    APPLE3(item { type = APPLE }.text("Яблоко в полете").build(), null),

    ARROW1(item {
        type = ARROW
        amount = 4
        text("Стрела")
    }.build(), null),
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
    IRON1(item { type = IRON_INGOT }.text("Железо").build(), null),
    IRON_SWORD1(item { type = IRON_SWORD }.text("Железный меч").build(), null),
    IRON_HELMET1(item { type = CHAINMAIL_HELMET }.text("Железный шлем").build(), null),
    IRON_CHEST1(item { type = CHAINMAIL_CHESTPLATE }.text("Железный нагрудник").build(), null),
    IRON_LEGGINGS1(item { type = CHAINMAIL_LEGGINGS }.text("Железные поножи").build(), null),
    IRON_BOOTS1(item { type = CHAINMAIL_BOOTS }.text("Железные ботинки").build(), null),
    FLINT1(item { type = FLINT }.text("Кремень").build(), mapOf(StandardsHandlers.knowledgeItem(Knowledge.FLINT))),
    FLINT_AND_STEEL1(
        item { type = FLINT_AND_STEEL }.text("Поджег костра").build(), mapOf(
            PlayerInteractEvent::class.java to BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                if (event.action == Action.RIGHT_CLICK_BLOCK) {
                    val location = event.blockClicked.location
                    val fire = me.func.forest.drop.generator.BonfireGenerator.BONFIRES[location]
                    if (fire != null && !fire) {
                        me.func.forest.drop.Resources.FIRE.generate(location)
                        useItem(event.player)
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
    STICK1(item { type = STICK }.text("Палка").build(), null),
    STRING1(item { type = STRING }.text("Нить").build(), null),
    TENT1(
        item {
            type = EMERALD
            nbt("forest", "p2")
        }.text("Палатка").build(), mapOf(
            PlayerInteractEvent::class.java to BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                if (event.action != Action.RIGHT_CLICK_BLOCK)
                    return@BiConsumer

                val user = app.getUser(it.player)!!
                val stat = user.stat!!

                if (stat.place != null) {
                    me.func.forest.channel.ModHelper.error(
                        user,
                        "Уже на ${stat.place!!.x.toInt()} ${stat.place!!.z.toInt()}"
                    )
                    return@BiConsumer
                }

                useItem(it.player)

                val location = event.blockClicked.location
                stat.place = implario.math.V3.of(location.x, location.y, location.z)
            })
    ),
    STONE1(
        item { type = FIREWORK_CHARGE }.text("Камень").build(),
        mapOf(
            StandardsHandlers.throwableItem(STONE3, 3.0),
            StandardsHandlers.knowledgeItem(Knowledge.STONE)
        )
    ),
    APPLE1(
        item { type = APPLE }.text("Плод").build(),
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
                    useItem(event.player)
                    event.player.health += 1.7
                }
            }, StandardsHandlers.knowledgeItem(Knowledge.HEAL)
        )
    ),
    SKULL1(item { type = BONE }.text("Череп").build(), null),
    BOW1(item { type = BOW }.text("Лук").build(), null),
    LEATHER1(item { type = LEATHER }.text("Кожа").build(), null),
    LEATHER_HELMET1(item { type = LEATHER_HELMET }.text("Кожаный шлем").build(), null),
    LEATHER_CHEST1(item { type = LEATHER_CHESTPLATE }.text("Кожаный нагрудник").build(), null),
    LEATHER_LEGGINGS1(item { type = LEATHER_LEGGINGS }.text("Кожаные поножи").build(), null),
    LEATHER_BOOTS1(item { type = LEATHER_BOOTS }.text("Кожаные ботинки").build(), null),
    STONE_AXE1(item { type = STONE_AXE }.text("Каменное орудие").build(), null),

    MUSHROOM2(item { type = BROWN_MUSHROOM }.text("Гриб").build(), null),
    RED_MUSHROOM2(
        item { type = RED_MUSHROOM }.text("Мухомор").build(), mapOf(
            PlayerInteractEvent::class.java to BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
                    useItem(event.player)
                    event.player.addPotionEffect(
                        PotionEffect(
                            implario.ListUtils.random(PotionEffectType.values()),
                            20 * (5 + 30 * Math.random()).toInt(),
                            1
                        )
                    )
                }
            }, StandardsHandlers.knowledgeItem(Knowledge.TOXIC)
        )
    ),
    SHELL2(item {
        type = CARPET
        data = 10
        text("Ракушка")
    }.build(), null),
    STONE2(item { type = CARPET }.build(), null),
    FULL_BUSH2(item {
        type = CARPET
        data = 1
    }.build(), null),
    EMPTY_BUSH2(item {
        type = CARPET
        data = 6
    }.build(), null),
    TOTEM2(item {
        type = CARPET
        data = 4
    }.build(), null),

    BONFIRE_OFF2(
        item {
            type = CARPET
            data = 14
            text("Потухший костер")
        }.build(), mapOf(
            PlayerInteractEvent::class.java to BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                val location = event.blockClicked.location
                if (event.action == Action.RIGHT_CLICK_BLOCK && me.func.forest.Postulates.isGround(location)) {
                    useItem(event.player)
                    me.func.forest.drop.Resources.FIRE.generate(location.clone().add(0.0, 1.0, 0.0))
                }
            })
    ),
    BONFIRE_ON2(item { type = TORCH }.build(), null),
    ;
}