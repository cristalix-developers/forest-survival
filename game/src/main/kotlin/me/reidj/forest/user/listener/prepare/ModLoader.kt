package me.reidj.forest.user.listener.prepare

import io.netty.buffer.Unpooled
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.reidj.forest.channel.ModHelper
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
        after {
            modList.forEach {
                user.sendPacket(
                    PacketPlayOutCustomPayload(
                        DisplayChannels.MOD_CHANNEL,
                        PacketDataSerializer(it.retainedSlice())
                    )
                )
            }
        }
        after(5) {
            ModTransfer(user.level, user.stat.exp, LevelHelper.level2exp(user.level)).send("exp-level", user.player)
            ModHelper.updateTemperature(user)
        }
    }
}