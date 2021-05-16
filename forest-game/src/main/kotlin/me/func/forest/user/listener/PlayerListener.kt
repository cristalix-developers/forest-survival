package me.func.forest.user.listener

import clepto.bukkit.B
import me.func.forest.app
import me.func.forest.channel.ModTransfer
import me.func.forest.user.listener.prepare.ModLoader
import me.func.forest.user.listener.prepare.PrepareUser
import me.func.forest.user.listener.prepare.SetupScoreBoard
import me.func.forest.user.listener.prepare.TutorialLoader
import org.bukkit.GameMode
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED
import org.bukkit.event.player.PlayerRespawnEvent

/**
 * Временный класс
 */
class PlayerListener : Listener {

    private val textureUrl = System.getenv("RESOURCE_PACK_URL")
    private val textureHash = "08880C088F83D8890128128"

    private val prepares = listOf(
        ModLoader(),
        TutorialLoader(),
        //PrepareUser { it.player.setResourcePack(textureUrl, textureHash) },
        PrepareUser { it.player.gameMode = GameMode.SURVIVAL },
        SetupScoreBoard()
    )

    @EventHandler
    fun joinEvent(event: PlayerJoinEvent) {
        val user = app.getUser(event.player)!!

        prepares.forEach { it.execute(user) }
    }

    @EventHandler
    fun completeResources(event: PlayerResourcePackStatusEvent) {
        if (event.status == SUCCESSFULLY_LOADED)
            B.postpone(5) { ModTransfer().send("rp-complete", app.getUser(event.getPlayer())!!) }
    }

    @EventHandler
    fun foodLevelChange(event: FoodLevelChangeEvent) {
        val human = event.entity

        if (human is CraftPlayer)
            ModTransfer().integer(event.foodLevel).send("food-level", app.getUser(human)!!)
    }

    @EventHandler
    fun respawn(event: PlayerRespawnEvent) {
        event.respawnLocation = app.worldMeta.getLabel("guide_end")
    }

    @EventHandler
    fun deathEvent(event: PlayerDeathEvent) {
        val user = app.getUser(event.getEntity())!!
        val stat = user.stat!!
        stat.heart--

        if (stat.heart < 1) {
            stat.exp = 1
            stat.heart = 3
            stat.timeAlive = 0
        } else {
            event.cancelled = true
            user.player.health = 20.0
            user.player.teleport(app.worldMeta.getLabel("guide_end"))
            ModTransfer()
                .integer(stat.heart + 1)
                .send("player-dead", user)
        }
    }
}