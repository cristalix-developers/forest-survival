package me.reidj.forest.listener

import io.netty.buffer.Unpooled
import me.func.mod.Anime
import me.func.mod.Glow
import me.func.mod.conversation.ModTransfer
import me.reidj.forest.app
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import java.util.*

/**
 * @project : forest
 * @author : Рейдж
 **/
object DamageHandler : Listener {

    private val corpses: MutableMap<UUID, Pair<Location, Inventory>> = mutableMapOf()

    init {
        Bukkit.getMessenger().registerIncomingPluginChannel(app, "corpse:remove") { _, _, bytes ->
            corpses.remove(UUID.fromString(Unpooled.wrappedBuffer(bytes).toString(Charsets.UTF_8)))
        }
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        corpses.values.filter { player.location.subtract(0.0, 0.0, 1.0).distanceSquared(it.first) < 0.7 * 1.2 }
            .forEach { player.openInventory(it.second) }
    }

    @EventHandler
    fun EntityDamageEvent.handle() {
        if (entity is Player)
            Glow.animate(entity as Player, 0.6, 255, 0, 0)
    }

    @EventHandler
    fun EntityDamageByEntityEvent.handle() {
        if (entity !is CraftPlayer)
            return

        val damageBy = if (damager is CraftPlayer)
            damager as CraftPlayer
        else if (damager is Projectile && (damager as Projectile).shooter is CraftPlayer)
            (damager as Projectile).shooter as CraftPlayer
        else null

        /*if (damageBy != null) {
            val entityUser = app.getUser(entity as CraftPlayer)!!
            val damagerUser = app.getUser(damageBy)!!
            val entityLvl = entityUser.level
            val damagerLvl = damagerUser.level

            // Если уровень <3 и уровни не равны
            if (entityLvl != damagerLvl || entityLvl < 3 || damagerLvl < 3)
                cancelled = true
            else if (
                entityUser.tent != null &&
                (entity.location.distanceSquared(entityUser.tent!!.location) < 10 ||
                        damageBy.location.distanceSquared(entityUser.tent!!.location) < 10)
            ) {
                cancelled = true
            } else if (
                damagerUser.tent != null &&
                (damageBy.location.distanceSquared(damagerUser.tent!!.location) < 10 ||
                        entity.location.distanceSquared(damagerUser.tent!!.location) < 10)
            )
                cancelled = true
            cancelled = true
        }*/
    }

    @EventHandler
    fun PlayerDeathEvent.handle() {
        val player = getEntity()
        val user = app.getUser(player)!!
        val stat = user.stat

        val killer = player.killer
        if (killer != null)
            app.getUser(killer)!!.stat.kills++

        val playerLocation = player.location
        val uuid = UUID.randomUUID()
        Bukkit.getOnlinePlayers().forEach {
            ModTransfer(
                uuid.toString(),
                "",
                "",
                "",
                playerLocation.x,
                playerLocation.y + 3,
                playerLocation.z,
                20
            ).send("forest:corpse", it)
        }
        user.inventory = Bukkit.createInventory(null, InventoryType.PLAYER, "Труп ${player.name}")
        player.inventory.filterNotNull().forEach { user.inventory.addItem(it) }
        corpses[uuid] = playerLocation to user.inventory

        player.activePotionEffects.forEach { player.removePotionEffect(it.type) }
        player.inventory.clear()

        Anime.title(player, "§cВы погибли")

        cancelled = true
        stat.deaths++
        stat.temperature = 36.6
        stat.playerInventory.clear()
        user.player!!.health = 20.0
        user.stat.exit = null
        user.spawn()
    }
}