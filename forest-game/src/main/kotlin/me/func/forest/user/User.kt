package me.func.forest.user

import net.minecraft.server.v1_12_R1.Packet
import net.minecraft.server.v1_12_R1.PlayerConnection
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import ru.cristalix.core.stats.player.PlayerWrapper
import java.util.*

class User(uuid: UUID, name: String, var stat: Stat?) : PlayerWrapper(uuid, name) {

    private var connection: PlayerConnection? = null

    init {
        if (stat == null) {
            stat = Stat(false, 1.0, 1, 1.0, 1, 1.0, 3, 0, 0)
        }
    }

    fun watchTutorial(): Boolean {
        return stat?.tutorial!!
    }

    fun sendPacket(packet: Packet<*>) {
        if (connection == null)
            connection = (player as CraftPlayer).handle.playerConnection
        connection?.sendPacket(packet)
    }
}