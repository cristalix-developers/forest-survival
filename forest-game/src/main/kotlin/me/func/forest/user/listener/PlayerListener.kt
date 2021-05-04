package me.func.forest.user.listener

import clepto.bukkit.B
import me.func.forest.app
import me.func.forest.channel.ModTransfer
import me.func.forest.user.listener.prepare.ModLoader
import me.func.forest.user.listener.prepare.PrepareUser
import me.func.forest.user.listener.prepare.SetupScoreBoard
import me.func.forest.user.listener.prepare.TutorialLoader
import net.minecraft.server.v1_12_R1.EnumItemSlot
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import org.bukkit.inventory.ItemStack
import org.spigotmc.event.entity.EntityDismountEvent

/**
 * Временный класс
 */
class PlayerListener : Listener {

    private val textureUrl = System.getenv("RESOURCE_PACK_URL")

    private val prepares = listOf(
        ModLoader(),
        TutorialLoader(),
        PrepareUser { B.postpone(4) { it.player.setResourcePack(
            textureUrl,
            "08880C088F83D8890128126"
        ) } },
        SetupScoreBoard()
    )

    private val interactiveItems = mapOf(
        0 to Pair(ItemStack(Material.STONE_BUTTON), Material.STONE),
        1 to Pair(ItemStack(Material.APPLE), Material.LEAVES_2)
    )

    @EventHandler
    fun joinEvent(event: PlayerJoinEvent) {
        val user = app.getUser(event.player)!!

        prepares.forEach { it.execute(user) }
    }

    @EventHandler
    fun completeResources(event: PlayerResourcePackStatusEvent) {
        if (event.status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED)
            ModTransfer.send("complete_resources", app.getUser(event.getPlayer())!!)
    }

    @EventHandler
    fun dismountEvent(event: EntityDismountEvent) {
        val entity = event.entity
        val dismounted = event.dismounted

        if (entity is CraftPlayer) {
            val user = app.getUser(entity) ?: return

            // Если игрок находится в состоянии просмотра туториала,
            // не давать слезть с вертолета
            if (user.watchTutorial())
                return
            B.postpone(1) {
                if (dismounted != null && !dismounted.isDead) {
                    dismounted.addPassenger(entity)
                }
            }
        }
    }

    @EventHandler
    fun blockBreakEvent(event: BlockBreakEvent) {
        val block = event.block
        val player = event.player

        if (block.type == Material.CARPET) {

            val data = block.data

            if (interactiveItems.containsKey(data.toInt())) {
                val type = block.type

                val pair = interactiveItems[data.toInt()]

                player.inventory.addItem(pair?.first)
                block.type = pair?.second

                B.postpone(20 * 5) {
                    block.type = type
                    block.data = data
                }
            }
        } else {
            event.cancel = true
        }
    }

    @EventHandler
    fun leftClick(event: PlayerInteractEvent) {
        val player = event.player

        if (event.action == Action.LEFT_CLICK_AIR && event.material == Material.STONE_BUTTON) {
            val stand = player.world.spawnEntity(player.location, EntityType.ARMOR_STAND)

            stand.velocity = player.eyeLocation.direction.multiply(1.3)

            val nmsStand = (stand as CraftArmorStand).handle

            nmsStand.isInvisible = true
            nmsStand.isMarker = true
            nmsStand.setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(event.item))
        }
    }

    @EventHandler
    fun move(event: EntityChangeBlockEvent) {
        if (event.entityType == EntityType.FALLING_BLOCK) {
            event.entity.passengers.forEach { it.remove() }
            event.entity.remove()
        }
    }
}