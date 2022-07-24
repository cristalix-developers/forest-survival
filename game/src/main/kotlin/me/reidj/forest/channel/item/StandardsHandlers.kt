package me.reidj.forest.channel.item

import clepto.bukkit.Cycle
import me.func.mod.util.after
import me.reidj.forest.app
import me.reidj.forest.data.Knowledge
import me.reidj.forest.drop.dropper.DropItem
import me.reidj.forest.drop.generator.BonfireGenerator
import net.minecraft.server.v1_12_R1.EnumItemSlot
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.PolarBear
import org.bukkit.entity.Wolf
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.util.function.BiConsumer
import java.util.function.Consumer

object StandardsHandlers {

    fun knowledgeItem(knowledge: Knowledge): Pair<Class<out PlayerEvent>, BiConsumer<ItemList, PlayerEvent>> {
        return Pair<Class<out PlayerEvent>, BiConsumer<ItemList, PlayerEvent>>(
            PlayerAttemptPickupItemEvent::class.java,
            BiConsumer { _, it -> app.getUser(it.player)!!.knowledgeTryGive(knowledge) }
        )
    }

    fun dropInFire(int: Int): Pair<Class<out PlayerEvent>, BiConsumer<ItemList, PlayerEvent>> {
        return Pair<Class<out PlayerEvent>, BiConsumer<ItemList, PlayerEvent>>(
            PlayerInteractEvent::class.java,
            BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                if (event.hasBlock() && event.hasItem()) {
                    val block = it.blockClicked.location.toBlockLocation()
                    val fire = BonfireGenerator.BONFIRES[block]

                    if (fire != null && fire > 0) {
                        ItemHelper.useItem(it.player)
                        val time = minOf(fire + int, BonfireGenerator.NORMAL_TICKS_FIRE * 3)
                        BonfireGenerator.BONFIRES[block] = time

                        me.reidj.forest.channel.ModHelper.indicator(time, block.clone().add(0.5, 1.4, 0.5))
                    }
                }
            }
        )
    }

    fun cookItem(drop: ItemList, duration: Int): Pair<Class<out PlayerEvent>, BiConsumer<ItemList, PlayerEvent>> {
        return Pair<Class<out PlayerEvent>, BiConsumer<ItemList, PlayerEvent>>(
            PlayerInteractEvent::class.java,
            BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                if (event.hasBlock() && event.hasItem() && it.blockClicked.type == ItemList.BONFIRE_ON2.item.type0) {
                    val block = it.blockClicked.location.toBlockLocation()
                    val fire = BonfireGenerator.BONFIRES[block]

                    if (fire != null && fire > 0) {
                        ItemHelper.useItem(it.player)
                        after(duration.toLong() * 20) { DropItem.drop(drop, block.toCenterLocation(), it.player) }
                    }
                }
            }
        )
    }

    fun tentItem(level: Int): Pair<Class<out PlayerEvent>, BiConsumer<ItemList, PlayerEvent>> {
        return Pair<Class<out PlayerEvent>, BiConsumer<ItemList, PlayerEvent>>(
            PlayerInteractEvent::class.java,
            BiConsumer { _, it ->
                val event = it as PlayerInteractEvent
                if (event.action != Action.RIGHT_CLICK_BLOCK)
                    return@BiConsumer

                val user = app.getUser(it.player)!!
                val stat = user.stat

                if (stat.place != null) {
                    me.reidj.forest.channel.ModHelper.error(
                        it.player,
                        "Уже на ${stat.place!!.x.toInt()} ${stat.place!!.z.toInt()}"
                    )
                    return@BiConsumer
                }

                ItemHelper.useItem(it.player)

                stat.placeLevel = level

                val location = event.blockClicked.location
                stat.place = ru.cristalix.core.math.V3(location.x, location.y, location.z)
                user.showTent(location)
            }
        )
    }

    fun throwableItem(icon: ItemList, damage: Double): Pair<Class<out PlayerEvent>, BiConsumer<ItemList, PlayerEvent>> {
        return Pair<Class<out PlayerEvent>, BiConsumer<ItemList, PlayerEvent>> (
            PlayerInteractEvent::class.java,
            BiConsumer { drop, it ->
                val event = it as PlayerInteractEvent

                if (event.action != Action.LEFT_CLICK_AIR)
                    return@BiConsumer

                val player = event.player

                ItemHelper.useItem(player)

                val stand = player.world.spawnEntity(player.location.clone().add(0.0, 0.4, 0.0), EntityType.ARMOR_STAND)

                stand.velocity = player.eyeLocation.clone().add(0.0, 3.0, 0.0).direction.multiply(1.8)

                val nmsStand = (stand as CraftArmorStand).handle

                nmsStand.isInvisible = true
                nmsStand.isMarker = true
                nmsStand.setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(icon.item))

                Cycle(1, ticksLive) {
                    val pose = stand.headPose
                    pose.x += Math.toRadians(omega)
                    stand.headPose = pose

                    collision(player, it, stand, 2.3, damage) { killStone(drop, stand) }

                    if ((stand.isOnGround || it == ticksLive - 1) && !collision(player, it, stand, 3.2, damage) { killStone(drop, stand) })
                        killStone(drop, stand)
                }
            }
        )
    }

    private const val omega = 20.0
    private const val ticksLive = 5 * 20

    private fun killStone(itemList: ItemList, stand: CraftArmorStand) {
        DropItem.drop(itemList, stand.location, null)

        stand.remove()
        Cycle.exit()
    }

    private fun collision(sender: Player, tick: Int, stand: CraftArmorStand, radiusSquared: Double, damage: Double, doWith: Consumer<CraftArmorStand>): Boolean {
        for (living in sender.world.livingEntities) {
           if (living is Wolf || living is PolarBear || living is CraftPlayer) {
                if (living is CraftPlayer && living.player == sender && tick < 40)
                    continue
                if (stand.location.distanceSquared(living.location) < radiusSquared) {
                    living.damage(damage, sender)
                    doWith.accept(stand)
                    return true
                }
            }
        }
        return false
    }
}