package me.reidj.forest.user.listener

import io.netty.buffer.Unpooled
import me.func.mod.Anime
import me.func.mod.Glow
import me.func.mod.conversation.ModLoader
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.func.protocol.Indicators.*
import me.reidj.forest.app
import me.reidj.forest.channel.ModHelper
import me.reidj.forest.user.LevelHelper
import me.reidj.forest.user.listener.prepare.PrepareUser
import me.reidj.forest.user.listener.prepare.SetupScoreBoard
import me.reidj.forest.user.listener.prepare.TutorialLoader
import me.reidj.forest.util.Images
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import java.util.*
import kotlin.math.min


/**
 * Временный класс
 */

class PlayerListener : Listener {

    private val mapPositive = app.worldMeta.getLabel("wall-negative")
    private val mapNegative = app.worldMeta.getLabel("wall-positive")

    private val corpses: MutableMap<UUID, Pair<Location, Inventory>> = mutableMapOf()

    init {
        Bukkit.getMessenger().registerIncomingPluginChannel(app, "corpse:remove") { _, _, bytes ->
            corpses.remove(UUID.fromString(Unpooled.wrappedBuffer(bytes).toString(Charsets.UTF_8)))
        }
    }

    private val prepares = listOf(
        PrepareUser { it.player!!.performCommand("rp") },
        PrepareUser { it.player!!.gameMode = GameMode.SURVIVAL },
        TutorialLoader,
        SetupScoreBoard,
    )

    @EventHandler
    fun PlayerJoinEvent.handle() {
        val user = app.getUser(player)!!
        user.player = player
        after {
            ModLoader.send("mod-bundle-1.0-SNAPSHOT.jar", player)
            ModTransfer(user.level, user.stat.exp, LevelHelper.level2exp(user.level)).send("exp-level", user.player)
            ModHelper.updateTemperature(user)
            prepares.forEach { it.execute(user) }
            user.spawn()
            Anime.hideIndicator(player, HEALTH, EXP, HUNGER, ARMOR, AIR_BAR, VEHICLE)
            Anime.loadTextures(player, *Images.values().map { it.path() }.toTypedArray())
        }
        player.isOp = true
    }

    @EventHandler
    fun PlayerQuitEvent.handle() = app.getUser(player)!!.tent?.remove()

    @EventHandler
    fun PlayerInteractAtEntityEvent.handle() {
        if (hand == EquipmentSlot.OFF_HAND)
            return
        val entity = clickedEntity
        if (entity.hasMetadata("owner") && entity.getMetadata("owner")[0].asString() == player.uniqueId.toString())
            ModTransfer().send("tent-open", player)
    }

    @EventHandler
    fun PlayerRespawnEvent.handle() {
        after {
            val tent = app.getUser(player)!!.tent
            if (tent != null)
                player.teleport(tent)
            else
                player.teleport(app.spawn)
        }
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
    fun PlayerInteractEvent.handle() {
        corpses.values.filter { player.location.subtract(0.0, 0.0, 1.0).distanceSquared(it.first) < 0.7 * 1.2 }
            .forEach { player.openInventory(it.second) }
    }

    @EventHandler
    fun FoodLevelChangeEvent.handle() {
        val player = entity
        if (player is CraftPlayer)
            ModTransfer(min(20, foodLevel)).send("food-level", player)
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
        player.inventory.filter { Objects.nonNull(it) }.forEach { user.inventory.addItem(it) }
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