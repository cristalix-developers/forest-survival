package me.func.forest.user.listener

import clepto.bukkit.B
import me.func.forest.app
import me.func.forest.channel.ModTransfer
import me.func.forest.item.ItemHelper
import me.func.forest.user.listener.prepare.ModLoader
import me.func.forest.user.listener.prepare.PrepareUser
import me.func.forest.user.listener.prepare.SetupScoreBoard
import me.func.forest.user.listener.prepare.TutorialLoader
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.util.Vector
import kotlin.math.min


/**
 * Временный класс
 */
class PlayerListener : Listener {

    private val textureUrl = System.getenv("RESOURCE_PACK_URL")
    private val textureHash = "11180C188F11D8890132123"

    private val mapPositive = app.worldMeta.getLabel("wall-negative")
    private val mapNegative = app.worldMeta.getLabel("wall-positive")

    init {
        B.regCommand({ player: Player, _: Array<String> ->
            player.setResourcePack(textureUrl, textureHash)
            null
        }, "rp", "resourcepack")
    }

    private val prepares = listOf(
        ModLoader(),
        PrepareUser { it.player.performCommand("rp") },
        PrepareUser { it.player.gameMode = GameMode.SURVIVAL },
        TutorialLoader(),
        SetupScoreBoard()
    )

    @EventHandler
    fun PlayerJoinEvent.handle() {
        val user = app.getUser(player)!!
        user.spawn()
        prepares.forEach { it.execute(user) }
    }

    @EventHandler
    fun FoodLevelChangeEvent.handle() {
        val player = entity
        if (player is CraftPlayer) {
            ModTransfer().integer(min(20, foodLevel)).send("food-level", app.getUser(player)!!)
            if (player.itemInHand.type0 == Material.BOWL)
                ItemHelper.useItem(player)
        }
    }

    @EventHandler
    fun PlayerInteractAtEntityEvent.handle() {
        if (hand == EquipmentSlot.OFF_HAND)
            return
        val entity = clickedEntity
        if (entity.hasMetadata("owner") && entity.getMetadata("owner")[0].asString() == player.uniqueId.toString())
            ModTransfer().send("tent-open", app.getUser(player)!!)
    }

    @EventHandler
    fun PlayerRespawnEvent.handle() {
        B.postpone(1) {
            val tent = app.getUser(player)!!.tent
            if (tent != null)
                player.teleport(tent)
            else
                player.teleport(app.spawn)
        }
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

        if (damageBy != null) {
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
        }
    }

    @EventHandler
    fun PlayerDeathEvent.handle() {
        val player = getEntity()
        val user = app.getUser(player)!!
        val stat = user.stat!!
        stat.heart--

        val killer = player.killer
        if (killer != null)
            app.getUser(killer)!!.stat!!.kills++

        player.activePotionEffects.forEach {
            player.removePotionEffect(it.type)
        }

        if (stat.heart < 1) {
            stat.exp = 0
            user.giveExperience(0)
            stat.heart = 3
            stat.timeAlive = 0
            stat.place = null
            stat.placeInventory?.clear()
            user.homeInventory.clear()
            stat.deaths++
            user.tent?.remove()
        } else {
            cancelled = true
            user.player.health = 20.0
            user.stat!!.exit = null
            user.spawn()
            ModTransfer()
                .integer(stat.heart + 1)
                .send("player-dead", user)
        }
        stat.temperature = 36.6
    }

    @EventHandler
    fun PlayerMoveEvent.handle() {
        if (from.blockX != to.blockX || from.blockZ != to.blockZ) {
            if (to.blockX < mapNegative.x) {
                player.velocity = Vector(2.0, 0.7, 0.0)
            }
            if (to.blockZ < mapNegative.z) {
                player.velocity = Vector(0.0, 0.7, 2.0)
            }
            if (to.blockX > mapPositive.x) {
                player.velocity = Vector(-2.0, 0.7, 0.0)
            }
            if (to.blockZ > mapPositive.z) {
                player.velocity = Vector(0.0, 0.7, -2.0)
            }
        }
    }
}