package me.func.forest.user.listener.prepare

import clepto.bukkit.B
import clepto.bukkit.Cycle
import me.func.forest.app
import me.func.forest.user.User
import net.minecraft.server.v1_12_R1.EnumItemSlot
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

class TutorialLoader : PrepareUser {

    private val startLocation: Location = app.worldMeta
        .getLabel("guide_start")
        .toCenterLocation()

    @Suppress("DEPRECATION")
    private val helicopterItem = CraftItemStack.asNMSCopy(ItemStack(Material.EMERALD, 1, 1, 1))

    override fun execute(user: User) {
        if (!user.watchTutorial()) {
            val player = user.player

            val helicopter = app.getWorld().spawnEntity(
                startLocation,
                EntityType.ARMOR_STAND
            )

            val nmsHelicopter = (helicopter as CraftArmorStand).handle

            nmsHelicopter.setSlot(EnumItemSlot.HEAD, helicopterItem)

            player.teleport(startLocation)
            helicopter.teleport(startLocation)
            helicopter.addPassenger(player)

            /*Cycle.run(5, 4 * 60) {
                helicopter.velocity = Vector(0.0, 0.17, 0.23)
            }*/

            val omega = 2.1

            B.postpone(100) {
                Cycle.run(6, 1200) {
                    val x = (kotlin.math.sin(it.toDouble() + .4) - kotlin.math.sin(it.toDouble())) * omega
                    val z = (kotlin.math.cos((it.toDouble() + .4)) - kotlin.math.cos(it.toDouble())) * omega

                    helicopter.velocity = Vector(x, 0.14, z)
                }
            }
        }
    }
}