package me.func.forest.item.click

import clepto.bukkit.Cycle
import me.func.forest.drop.dropper.DropItem
import me.func.forest.item.ItemHelper
import me.func.forest.item.ItemList
import net.minecraft.server.v1_12_R1.EnumItemSlot
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.util.function.BiConsumer
import java.util.function.Consumer

class ThrowableItem(private val icon: ItemList, private val damage: Double) : BiConsumer<ItemList, PlayerInteractEvent> {

    private val omega = 20.0
    private val ticksLive = 5 * 20

    private fun killStone(itemList: ItemList, stand: CraftArmorStand) {
        DropItem.drop(itemList, stand.location, null)

        stand.remove()
        Cycle.exit()
    }

    private fun collision(sender: Player, tick: Int, stand: CraftArmorStand, radiusSquared: Double, doWith: Consumer<CraftArmorStand>): Boolean {
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

    override fun accept(itemList: ItemList, event: PlayerInteractEvent) {
        if (event.action != Action.LEFT_CLICK_AIR)
            return

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

            collision(player, it, stand, 2.3) { killStone(itemList, stand) }

            if ((stand.isOnGround || it == ticksLive - 1) && !collision(player, it, stand, 3.2) { killStone(itemList, stand) })
                killStone(itemList, stand)
        }
    }
}