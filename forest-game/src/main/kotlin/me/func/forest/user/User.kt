package me.func.forest.user

import me.func.forest.channel.ModTransfer
import me.func.forest.craft.CraftItem
import net.minecraft.server.v1_12_R1.Packet
import net.minecraft.server.v1_12_R1.PlayerConnection
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import ru.cristalix.core.stats.player.PlayerWrapper
import java.util.*
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

class User(uuid: UUID, name: String, var stat: Stat?) : PlayerWrapper(uuid, name) {

    private var connection: PlayerConnection? = null
    var level by Delegates.notNull<Int>()

    init {
        if (stat == null) {
            stat = Stat(false, 1.0, 1, 1.0, 1, 1.0, 3, 0, 0)
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
}