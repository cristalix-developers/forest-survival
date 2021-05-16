package me.func.forest.user

import me.func.forest.channel.ModHelper
import me.func.forest.channel.ModTransfer
import me.func.forest.craft.CraftItem
import net.minecraft.server.v1_12_R1.Packet
import net.minecraft.server.v1_12_R1.PlayerConnection
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import ru.cristalix.core.stats.player.PlayerWrapper
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.properties.Delegates

object LevelHelper {
    fun exp2level(exp: Int): Int {
        return sqrt(exp.toDouble()).toInt()
    }

    fun level2exp(level: Int): Int {
        return level * level
    }

    fun need2next(currentLevel: Int): Int {
        return level2exp(currentLevel + 1) - level2exp(currentLevel)
    }

    fun levelImage(level: Int): String {
        return when(level) {
            2 -> "second_level"
            3 -> "second_level"
            4 -> "second_level"
            5 -> "second_level"
            else -> "second_level"
        }
    }
}

const val MAX_TEMPERATURE = 41.2
const val AVR_TEMPERATURE = 36.6
const val MIN_TEMPERATURE = 25.0
const val CRITICAL_MIN_TEMPERATURE = 31.0
const val CRITICAL_MAX_TEMPERATURE = 39.0

class User(uuid: UUID, name: String, var stat: Stat?) : PlayerWrapper(uuid, name) {

    private var connection: PlayerConnection? = null
    var level by Delegates.notNull<Int>()

    init {
        if (stat == null) {
            stat = Stat(false, 1.0, 1, 1, 36.0, 1, 3, 0)
        }
        level = LevelHelper.exp2level(stat!!.exp)
    }

    fun watchTutorial(): Boolean {
        return stat?.tutorial!!
    }

    fun sendPacket(packet: Packet<*>) {
        if (connection == null)
            connection = (player as CraftPlayer).handle.playerConnection
        connection?.sendPacket(packet)
    }

    fun giveExperience(exp: Int) {
        stat!!.exp += exp
        val currentLevel = LevelHelper.exp2level(stat!!.exp)

        ModTransfer()
            .integer(currentLevel)
            .integer(stat!!.exp - LevelHelper.level2exp(currentLevel))
            .integer(LevelHelper.need2next(level))
            .send("exp-level", this)

        if (currentLevel != level) {
            level++
            ModTransfer()
                .integer(currentLevel)
                .string(LevelHelper.levelImage(currentLevel))
                .string(CraftItem.values()
                    .filter { it.minLevel == currentLevel }
                    .joinToString("next") { it.to.item.itemMeta.displayName }
                ).send("level-new", this)
        }
    }

    fun hasLevel(level: Int): Boolean {
        return level <= this.level
    }

    fun changeTemperature(dx: Double) {
        stat!!.temperature += dx

        val temperature = stat!!.temperature

        if (abs(temperature - AVR_TEMPERATURE) < 0.05)
            return

        if (temperature < CRITICAL_MIN_TEMPERATURE)
            player.damage(0.06)
        if (temperature > CRITICAL_MAX_TEMPERATURE)
            player.damage(0.07)

        ModHelper.updateTemperature(this)

        if (temperature < MIN_TEMPERATURE || temperature > MAX_TEMPERATURE) {
            stat!!.temperature = min(max(MIN_TEMPERATURE, temperature), MAX_TEMPERATURE)
            return
        }
    }

    fun normalizeTemperature(step: Double) {
        val temperature = stat!!.temperature

        if (temperature < AVR_TEMPERATURE)
            changeTemperature(step)
        else if (temperature > AVR_TEMPERATURE)
            changeTemperature(-step)
    }
}