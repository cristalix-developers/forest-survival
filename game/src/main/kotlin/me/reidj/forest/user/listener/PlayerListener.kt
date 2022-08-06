package me.reidj.forest.user.listener

import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.reidj.forest.app
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.EquipmentSlot
import kotlin.math.min


/**
 * Временный класс
 */

class PlayerListener : Listener {

    private val mapPositive = app.worldMeta.getLabel("wall-negative")
    private val mapNegative = app.worldMeta.getLabel("wall-positive")

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
        after { app.getUser(player)!!.tent.run { player.teleport(if (this != null) location else app.spawn) } }
    }

    @EventHandler
    fun FoodLevelChangeEvent.handle() {
        val player = entity
        if (player is CraftPlayer)
            ModTransfer(min(20, foodLevel)).send("food-level", player)
    }
}