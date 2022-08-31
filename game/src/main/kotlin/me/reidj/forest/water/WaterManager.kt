package me.reidj.forest.water

import me.reidj.forest.app
import me.reidj.forest.channel.ModHelper
import me.reidj.forest.clock.ClockInject
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionType

/**
 * @project : forest
 * @author : Рейдж
 **/

const val MAX_WATER_AMOUNT = 20

class WaterManager : ClockInject, Listener {

    override fun run() {
        Bukkit.getOnlinePlayers()
            .mapNotNull { app.getUser(it) }
            .filter { it.player != null }
            .filter { it.player!!.health > 2.0 }
            .forEach {
                val stat = it.stat
                if (stat.water > 1) {
                    stat.water -= if (stat.water >= 39.0) 3 else 1
                    ModHelper.updateWaterAmount(it)
                } else {
                    it.player!!.damage(1.0)
                }
            }
    }

    override fun doEvery() = 2000

    @EventHandler
    fun PlayerItemConsumeEvent.handle() {
        val user = app.getUser(player) ?: return
        val stat = user.stat
        val itemHand = player.itemInHand
        val type = itemHand.getType()

        if (type == Material.POTION && (itemHand.itemMeta as PotionMeta).basePotionData.type == PotionType.WATER) {
            if (stat.water == 20) {
                isCancelled = true
                return
            }
            stat.water = MAX_WATER_AMOUNT - maxOf(0, MAX_WATER_AMOUNT - stat.water - 5)
            ModHelper.updateWaterAmount(user)
        }
    }
}