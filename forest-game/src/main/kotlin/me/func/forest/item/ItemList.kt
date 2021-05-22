package me.func.forest.item

import dev.implario.bukkit.item.item
import me.func.forest.app
import me.func.forest.drop.block.BlockUnit
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
import ru.cristalix.core.item.Items
import java.util.function.BiConsumer

enum class ItemList(val item: ItemStack, val on: Map<Class<out PlayerEvent>, BiConsumer<ItemList, PlayerEvent>>?) {

    STONE3(item("Камень в полете", STONE, "STONE3"), null),
    APPLE3(item("Яблоко в полете", APPLE, "APPLE3"), null),

    ARROW1(item {
        type = ARROW
        text("⭐ §7Стрела")
        nbt("code", "ARROW1")
    }.build(), null),
    POISONED_ARROW1(
        item(
            "⭐⭐ §aОтравленная стрела",
            1,
            TIPPED_ARROW,
            listOf("Отравленное орудие дальнего боя."),
            PotionMeta::class.java
        ) {
            it.basePotionData = PotionData(PotionType.POISON)
        }, null
    ),
    IRON1(item("⭐⭐⭐ §aЖелезо", IRON_INGOT, "IRON1"), null),
    IRON_SWORD1(item { type = IRON_SWORD }.text("⭐⭐⭐ §cЖелезный меч").build(), null),
    IRON_HELMET1(item("⭐⭐⭐ §cЖелезные шлем", CHAINMAIL_HELMET, "IRON_HELMET1"), null),
    IRON_CHEST1(item("⭐⭐⭐ §cЖелезные нагрудник", CHAINMAIL_CHESTPLATE, "IRON_CHEST1"), null),
    IRON_LEGGINGS1(item("⭐⭐⭐ §cЖелезные поножи", CHAINMAIL_LEGGINGS, "IRON_LEGGINGS1"), null),
    IRON_BOOTS1(item("⭐⭐⭐ §cЖелезные ботинки", CHAINMAIL_BOOTS, "IRON_BOOTS1"), null),
    FLINT1(item { type = FLINT }.text("⭐ §7Кремень").build(), mapOf(StandardsHandlers.knowledgeItem(Knowledge.FLINT))),
    FLINT_AND_STEEL1(
        item {
            type = FLINT_AND_STEEL
            text("⭐⭐ §aПоджег костра")
            nbt("code", "FLINT_AND_STEEL1")
        }.build(), mapOf(
            PlayerInteractEvent::class.java to BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                if (event.action == Action.RIGHT_CLICK_BLOCK) {
                    val location = event.blockClicked.location.toBlockLocation()
                    val fire = me.func.forest.drop.generator.BonfireGenerator.BONFIRES[location]
                    if (fire != null && !fire) {
                        BlockUnit.FIRE.generate(location)
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
    STICK1(item { type = STICK }.text("⭐ §7Палка").build(), null),
    STRING1(item { type = STRING }.text("⭐ §7Нить").build(), null),
    TENT1(
        item {
            type = EMERALD
            nbt("forest", "p2")
            nbt("code", "TENT1")
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
                stat.place = ru.cristalix.core.math.V3(location.x, location.y, location.z)
                stat.placeLevel = 1
                user.showTent(location)
            })
    ),
    STONE1(
        item("⭐ §7Камень", FIREWORK_CHARGE, "STONE1"),
        mapOf(
            StandardsHandlers.throwableItem(STONE3, 3.0),
            StandardsHandlers.knowledgeItem(Knowledge.STONE)
        )
    ),
    APPLE1(
        item("⭐ §7Плод", APPLE, "APPLE1"),
        mapOf(
            StandardsHandlers.throwableItem(APPLE3, 1.2),
            StandardsHandlers.knowledgeItem(Knowledge.APPLE)
        )
    ),
    HEAL1(
        item {
            text("⭐⭐ §aЦелебная трава")
            type = INK_SACK
            data = 5
            nbt("code", "HEAL1")
        }.build(), mapOf(
            PlayerInteractEvent::class.java to BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                if (event.action == Action.RIGHT_CLICK_AIR) {
                    useItem(event.player)
                    event.player.health += 1.7
                    if (event.player.health > 20.0)
                        event.player.health = 20.0
                }
            }, StandardsHandlers.knowledgeItem(Knowledge.HEAL)
        )
    ),
    RABBIT_MEAL1(item("⭐ §7Крольчатинка", RABBIT, "RABBIT_MEAL1"), null),
    WOLF_MEAL1(item("⭐⭐ §aВолчатинка", MUTTON, "WOLF_MEAL1"), null),
    BEAR_MEAL1(item("⭐⭐⭐ §cМедвежатинка", RAW_BEEF, "BEAR_MEAL1"), null),
    SKULL1(item("⭐⭐⭐ §cЧереп", BONE, "SKULL1"), null),
    BOW1(item("⭐⭐ §aЛук", BOW, "BOW1"), null),
    LEATHER1(item("⭐ §7Кожа", LEATHER, "LEATHER1"), null),
    LEATHER_HELMET1(item("⭐⭐ §aКожаный шлем", LEATHER_HELMET, "LEATHER_HELMET1"), null),
    LEATHER_CHEST1(item("⭐⭐ §aКожаный нагрудник", LEATHER_CHESTPLATE, "LEATHER_CHEST1"), null),
    LEATHER_LEGGINGS1(item("⭐⭐ §aКожаные поножи", LEATHER_LEGGINGS, "LEATHER_LEGGINGS1"), null),
    LEATHER_BOOTS1(item("⭐⭐ §aКожаные ботинки", LEATHER_BOOTS, "LEATHER_BOOTS1"), null),
    STONE_AXE1(item("⭐⭐ §aКаменное орудие", STONE_AXE, "STONE_AXE1"), null),

    MUSHROOM2(item("⭐ §7Гриб", BROWN_MUSHROOM, "MUSHROOM2"), null),
    RED_MUSHROOM2(
        item("⭐⭐ §aМухомор", RED_MUSHROOM, "RED_MUSHROOM2"), mapOf(
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
    SHELL2(Items.builder().type(CARPET).displayName("⭐⭐ §aРакушка").damage(10).build(), null),
    STONE2(Items.builder().type(CARPET).build(), null),
    FULL_BUSH2(Items.builder().type(CARPET).damage(1).build(), null),
    EMPTY_BUSH2(Items.builder().type(CARPET).damage(6).build(), null),
    TOTEM2(Items.builder().type(CARPET).damage(4).build(), null),

    BONFIRE_OFF2(
        item {
            type = CARPET
            data = 14
            text("⭐⭐ §aПотухший костер")
            nbt("code", "BONFIRE_OFF2")
        }.build(), mapOf(
            PlayerInteractEvent::class.java to BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                val location = event.blockClicked.location
                if (event.action == Action.RIGHT_CLICK_BLOCK && me.func.forest.Postulates.isGround(location)) {
                    useItem(event.player)
                    BlockUnit.FIRE.generate(location.toBlockLocation().clone().add(0.0, 1.0, 0.0))
                }
            })
    ),
    BONFIRE_ON2(Items.builder().type(TORCH).build(), null),
    ;
}