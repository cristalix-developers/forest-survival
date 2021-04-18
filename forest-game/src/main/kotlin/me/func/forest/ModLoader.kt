package me.func.forest

import io.netty.buffer.Unpooled
import net.minecraft.server.v1_12_R1.PacketDataSerializer
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import ru.cristalix.core.display.DisplayChannels
import ru.cristalix.core.display.messages.Mod
import java.io.File
import java.nio.file.Files

class ModLoader : Listener {

    private var modList = try {
        File("./mods/").listFiles()!!.map {
            val buffer = Unpooled.buffer()
            buffer.writeBytes(Mod.serialize(Mod(Files.readAllBytes(it.toPath()))))
            buffer
        }.toList()
    } catch (exception: Exception) {
        throw RuntimeException(exception)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val connection = (event.player as CraftPlayer).handle.playerConnection

        modList.forEach {
            connection.sendPacket(
                PacketPlayOutCustomPayload(
                    DisplayChannels.MOD_CHANNEL,
                    PacketDataSerializer(it.retainedSlice())
                )
            )
        }
    }
}