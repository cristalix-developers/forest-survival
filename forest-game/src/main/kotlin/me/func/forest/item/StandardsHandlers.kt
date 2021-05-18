package me.func.forest.item

import clepto.bukkit.Cycle
import me.func.forest.app
import me.func.forest.drop.dropper.DropItem
import me.func.forest.knowledge.Knowledge
import net.minecraft.server.v1_12_R1.EnumItemSlot
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
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
            BiConsumer { _, it -> knowledge.tryGive(app.getUser(it.player)!!) }
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
        for (user in Bukkit.getOnlinePlayers()) {
            if (user.player == sender && tick < 40)
                continue
            if (stand.location.distanceSquared(user?.location) < radiusSquared) {
                user.damage(damage, sender)
                doWith.accept(stand)
                return true
            }
        }
        return false
    }
}