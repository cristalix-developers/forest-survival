package me.reidj.forest.user.listener.prepare

import clepto.bukkit.B
import io.netty.buffer.Unpooled
import me.reidj.forest.channel.ModHelper
import me.reidj.forest.channel.ModTransfer
import me.reidj.forest.user.LevelHelper
import me.reidj.forest.user.User
import net.minecraft.server.v1_12_R1.PacketDataSerializer
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload
import ru.cristalix.core.display.DisplayChannels
import ru.cristalix.core.display.messages.Mod
import java.io.File
import java.nio.file.Files

class ModLoader : PrepareUser {

    private var modList = try {
        File("./mods/").listFiles()!!
            .filter { it.name.contains("bundle") }
            .map {
                val buffer = Unpooled.buffer()
                buffer.writeBytes(Mod.serialize(Mod(Files.readAllBytes(it.toPath()))))
                buffer
            }.toList()
    } catch (exception: Exception) {
        throw RuntimeException(exception)
    }

    override fun execute(user: User) {
        B.postpone(1) {
            modList.forEach {
                user.sendPacket(
                    PacketPlayOutCustomPayload(
                        DisplayChannels.MOD_CHANNEL,
                        PacketDataSerializer(it.retainedSlice())
                    )
                )
            }
            B.postpone(5) {
                ModTransfer()
                    .integer(user.level)
                    .integer(user.stat.exp)
                    .integer(LevelHelper.level2exp(user.level))
                    .send("exp-level", user)
                ModHelper.updateTemperature(user)
            }
        }
    }
}