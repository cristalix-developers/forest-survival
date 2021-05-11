package me.func.forest.user.listener

import clepto.bukkit.Cycle
import me.func.forest.drop.Resources
import me.func.forest.drop.dropper.DropItem
import net.minecraft.server.v1_12_R1.EnumItemSlot
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class ThrowStone : Listener {

    private val omega = 20.0
    private val ticksLive = 5 * 20
    private val damage = 3.0

    @EventHandler
    fun leftClick(event: PlayerInteractEvent) {
        val player = event.player

        if (event.action == Action.LEFT_CLICK_AIR && event.material == Material.FIREWORK_CHARGE) {
            val hand = player.itemInHand
            hand.setAmount(hand.getAmount() - 1)
            player.itemInHand = hand

            val stand = player.world.spawnEntity(player.location.clone().add(0.0, 0.4, 0.0), EntityType.ARMOR_STAND)

            stand.velocity = player.eyeLocation.clone().add(0.0, 3.0, 0.0).direction.multiply(1.8)

            val nmsStand = (stand as CraftArmorStand).handle

            nmsStand.isInvisible = true
            nmsStand.isMarker = true
            nmsStand.setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(ItemStack(Material.STONE)))

            Cycle(1, ticksLive) {
                val pose = stand.headPose
                pose.x += Math.toRadians(omega)
                stand.headPose = pose

                collision(player, it, stand, 2.3) { killStone(stand) }

                if ((stand.isOnGround || it == ticksLive - 1) && !collision(player, it, stand, 3.2) { killStone(stand) })
                    killStone(stand)
            }
        }
    }

    private fun killStone(stand: CraftArmorStand) {
        DropItem.drop(Resources.STONE, stand.location, null)

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
}