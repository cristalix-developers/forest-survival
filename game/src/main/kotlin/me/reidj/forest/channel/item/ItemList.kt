package me.reidj.forest.channel.item

import data.Knowledge
import me.func.mod.Anime
import me.func.mod.util.after
import me.func.mod.util.nbt
import me.reidj.forest.channel.item.ItemHelper.item
import me.reidj.forest.channel.item.ItemHelper.useItem
import me.reidj.forest.data
import me.reidj.forest.drop.block.BlockUnit
import me.reidj.forest.text
import org.bukkit.Material.*
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerFishEvent
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
    SPEAR3(item("Копье в полете", WOOD_SWORD, "SPEAR3"), null),

    ARROW1(item("⭐ §7Стрела", ARROW, "ARROW1"), null),
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
    WORM1(me.reidj.forest.item(STRING).nbt("code", "WORM1").nbt("color", "1109533183").text("⭐ §7Червь"), null),
    IRON1(item("⭐⭐⭐ §cЖелезо", IRON_INGOT, "IRON1"), null),
    IRON_SWORD1(me.reidj.forest.item(IRON_SWORD).text("⭐⭐⭐ §cЖелезный меч"), null),
    IRON_HELMET1(item("⭐⭐⭐ §cЖелезные шлем", CHAINMAIL_HELMET, "IRON_HELMET1"), null),
    IRON_CHEST1(item("⭐⭐⭐ §cЖелезные нагрудник", CHAINMAIL_CHESTPLATE, "IRON_CHEST1"), null),
    IRON_LEGGINGS1(item("⭐⭐⭐ §cЖелезные поножи", CHAINMAIL_LEGGINGS, "IRON_LEGGINGS1"), null),
    IRON_BOOTS1(item("⭐⭐⭐ §cЖелезные ботинки", CHAINMAIL_BOOTS, "IRON_BOOTS1"), null),
    FLINT1(me.reidj.forest.item(FLINT).text("⭐ §7Кремень"), mapOf(StandardsHandlers.knowledgeItem(Knowledge.FLINT))),
    SPEAR1(item("⭐ §7Копьё", WOOD_SWORD, "SPEAR1"), mapOf(StandardsHandlers.throwableItem(SPEAR3, 3.0))),
    ROD1(item("⭐ §7Удочка", FISHING_ROD, "ROD1"), mapOf(
        PlayerFishEvent::class.java to BiConsumer { _, it ->
            val event = it as PlayerFishEvent

            if (event.state != PlayerFishEvent.State.FISHING)
                return@BiConsumer

            if (!ItemHelper.tryUseItem(event.player, WORM1)) {
                it.isCancelled = true
                Anime.itemTitle(event.player, me.reidj.forest.BARRIER, "Нет червей", null)
            }
        }
    )),
    SPADE1(item("⭐ §7Лопата для червей", WOOD_SPADE, "SPADE1"), mapOf(
        PlayerInteractEvent::class.java to BiConsumer { _, it ->
            val event = it as PlayerInteractEvent

            if (event.hasBlock() && event.blockClicked.type == GRASS) {
                if (Math.random() < 0.2) {
                    me.reidj.forest.drop.dropper.DropItem.drop(
                        WORM1,
                        event.blockClicked.location,
                        it.player
                    )
                }
            }
        }
    )),
    FLINT_AND_STEEL1(
        item("⭐⭐ §aПоджег костра", FLINT_AND_STEEL, "FLINT_AND_STEEL1"), mapOf(
            PlayerInteractEvent::class.java to BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                if (event.action == Action.RIGHT_CLICK_BLOCK) {
                    val location = event.blockClicked.location.toBlockLocation()
                    val fire = me.reidj.forest.drop.generator.BonfireGenerator.BONFIRES[location]
                    if (fire != null && fire < 1) {
                        BlockUnit.FIRE.generate(location)
                        useItem(event.player)
                        me.reidj.forest.channel.ModHelper.indicator(
                            me.reidj.forest.drop.generator.BonfireGenerator.NORMAL_TICKS_FIRE,
                            location.clone().add(0.5, 1.4, 0.5)
                        )
                    }
                }
            })
    ),
    STICK1(item("⭐ §7Палка", STICK, "STICK1"), mapOf(StandardsHandlers.dropInFire(10 * 20))),
    STRING1(item("⭐ §7Нить", STRING, "STRING1"), mapOf(StandardsHandlers.dropInFire(8 * 20))),
    TENT1(
        me.reidj.forest.item(EMERALD).nbt("forest", "p2").nbt("code", "TENT1").text("⭐ §7Палатка 1УР."),
        mapOf(StandardsHandlers.tentItem(1))
    ),
    TENT2(
        me.reidj.forest.item(EMERALD).nbt("forest", "p3").nbt("code", "TENT2").text("⭐⭐ §aПалатка 2УР."),
        mapOf(StandardsHandlers.tentItem(2))
    ),
    TENT3(
        me.reidj.forest.item(EMERALD).nbt("forest", "p1").nbt("code", "TENT3").text("⭐⭐⭐ §cПалатка 3УР."),
        mapOf(StandardsHandlers.tentItem(3))
    ),
    STONE1(
        item("⭐ §7Камень", FIREWORK_CHARGE, "STONE1"),
        mapOf(
            StandardsHandlers.throwableItem(STONE3, 2.0),
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
        item("⭐⭐ §aЦелебная трава", INK_SACK, "HEAL1"), mapOf(
            PlayerInteractEvent::class.java to BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                if (event.action == Action.RIGHT_CLICK_AIR) {
                    useItem(event.player)
                    if (event.player.health + 1.7 > 20.0)
                        event.player.health = 20.0
                    else
                        event.player.health += 1.7
                }
            }, StandardsHandlers.knowledgeItem(Knowledge.HEAL)
        )
    ),
    COOKED_RABBIT_MEAL1(
        item("⭐⭐ §a Жареная крольчатина", COOKED_RABBIT, "COOKED_RABBIT_MEAL1"),
        mapOf(StandardsHandlers.cookItem(FLINT1, 5))
    ),
    COOKED_WOLF_MEAL1(
        item("⭐⭐ §aЖареная Волчатинка", COOKED_MUTTON, "COOKED_WOLF_MEAL1"),
        mapOf(StandardsHandlers.cookItem(FLINT1, 5))
    ),
    COOKED_BEAR_MEAL1(
        item("⭐⭐⭐ §cЖареная медвежатина", COOKED_BEEF, "COOKED_BEAR_MEAL1"),
        mapOf(StandardsHandlers.cookItem(FLINT1, 5))
    ),
    RABBIT_MEAL1(
        item("⭐ §7Крольчатинка", RABBIT, "RABBIT_MEAL1"),
        mapOf(StandardsHandlers.cookItem(COOKED_RABBIT_MEAL1, 5))
    ),
    WOLF_MEAL1(item("⭐⭐ §aВолчатинка", MUTTON, "WOLF_MEAL1"), mapOf(StandardsHandlers.cookItem(COOKED_WOLF_MEAL1, 5))),
    BEAR_MEAL1(
        item("⭐⭐ §aМедвежатинка", RAW_BEEF, "BEAR_MEAL1"),
        mapOf(StandardsHandlers.cookItem(COOKED_BEAR_MEAL1, 5))
    ),
    SKULL1(item("⭐⭐⭐ §cЧереп", BONE, "SKULL1"), null),
    BOW1(item("⭐⭐ §aЛук", BOW, "BOW1"), null),
    LEATHER1(item("⭐ §7Кожа", LEATHER, "LEATHER1"), null),
    LEATHER_HELMET1(item("⭐⭐ §aКожаный шлем", LEATHER_HELMET, "LEATHER_HELMET1"), null),
    LEATHER_CHEST1(item("⭐⭐ §aКожаный нагрудник", LEATHER_CHESTPLATE, "LEATHER_CHEST1"), null),
    LEATHER_LEGGINGS1(item("⭐⭐ §aКожаные поножи", LEATHER_LEGGINGS, "LEATHER_LEGGINGS1"), null),
    LEATHER_BOOTS1(item("⭐⭐ §aКожаные ботинки", LEATHER_BOOTS, "LEATHER_BOOTS1"), null),
    STONE_AXE1(item("⭐⭐ §aКаменное орудие", STONE_AXE, "STONE_AXE1"), null),
    MUSHROOM_SOUP1(item("⭐⭐ §aГрибной суп", MUSHROOM_SOUP, "MUSHROOM_SOUP1"), null),
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
    FURNACE2(item("⭐⭐⭐ §cПечь", FURNACE, "FURNACE2"), mapOf(
        PlayerInteractEvent::class.java to BiConsumer { _, it ->
            val event = it as PlayerInteractEvent

            if (event.action == Action.RIGHT_CLICK_BLOCK) {
                if (ItemHelper.tryUseItem(event.player, IRON2)) {
                    useItem(event.player)

                    val location = event.blockClicked.location.clone().add(0.0, 1.0, 0.0).toCenterLocation()
                    location.block.type = FURNACE
                    me.reidj.forest.channel.ModHelper.indicator(30 * 20, location.clone().add(0.0, 1.0, 0.0))
                    after(30 * 20) {
                        location.block.type = org.bukkit.Material.AIR
                        me.reidj.forest.drop.dropper.DropItem.drop(ItemList.IRON1, location, null)
                        me.reidj.forest.drop.dropper.DropItem.drop(ItemList.FURNACE2, location, null)
                    }
                } else {
                    it.isCancelled = true
                    Anime.itemTitle(it.player, me.reidj.forest.BARRIER, "Нет металла", null)
                }
            }
        }
    )),
    SHELL2(me.reidj.forest.item(CARPET).nbt("code", "SHELL2").text("⭐⭐ §aРакушка").data(10), null),
    IRON2(
        me.reidj.forest.item(CARPET).nbt("code", "IRON2").text("⭐⭐ §aМутное железо").data(5), null),
    STONE2(Items.builder().type(CARPET).build(), null),
    FULL_BUSH2(Items.builder().type(CARPET).damage(1).build(), null),
    EMPTY_BUSH2(Items.builder().type(CARPET).damage(6).build(), null),
    TOTEM2(Items.builder().type(CARPET).damage(4).build(), null),

    BONFIRE_OFF2(
        me.reidj.forest.item(CARPET).nbt("code", "BONFIRE_OFF2").text("⭐⭐ §aПотухший костер").data(14), mapOf(
            PlayerInteractEvent::class.java to BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                if (event.blockClicked == null)
                    return@BiConsumer
                val location = event.blockClicked.location
                if (event.action == Action.RIGHT_CLICK_BLOCK && me.reidj.forest.Postulates.isGround(location)) {
                    useItem(event.player)
                    BlockUnit.FIRE.generate(location.toBlockLocation().clone().add(0.0, 1.0, 0.0))
                }
            })
    ),
    BONFIRE_ON2(Items.builder().type(TORCH).build(), null),
    ;
}