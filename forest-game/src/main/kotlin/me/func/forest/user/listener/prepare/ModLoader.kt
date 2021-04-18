package me.func.forest.user.listener.prepare

import io.netty.buffer.Unpooled
import me.func.forest.user.User
import net.minecraft.server.v1_12_R1.PacketDataSerializer
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import ru.cristalix.core.display.DisplayChannels
import ru.cristalix.core.display.messages.Mod
import java.io.File
import java.nio.file.Files

class ModLoader : PrepareUser {

    private var modList = try {
        File("./mods/").listFiles()!!.map {
            val buffer = Unpooled.buffer()
            buffer.writeBytes(Mod.serialize(Mod(Files.readAllBytes(it.toPath()))))
            buffer
        }.toList()
    } catch (exception: Exception) {
        throw RuntimeException(exception)
    }

    override fun execute(user: User) {
        val connection = (user.player as CraftPlayer).handle.playerConnection

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