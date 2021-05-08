package me.func.forest.channel

import io.netty.buffer.Unpooled
import me.func.forest.user.User
import net.minecraft.server.v1_12_R1.ItemStack
import net.minecraft.server.v1_12_R1.PacketDataSerializer
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload
import ru.cristalix.core.GlobalSerializers


/**
 * @author func 02.01.2021
 * @project forest
 */
class ModTransfer {
    private val serializer = PacketDataSerializer(Unpooled.buffer())

    fun json(`object`: Any?): ModTransfer {
        return string(GlobalSerializers.toJson(`object`))
    }

    fun string(string: String?): ModTransfer {
        serializer.writeString(string)
        return this
    }

    fun item(item: ItemStack?): ModTransfer {
        serializer.writeItem(item)
        return this
    }

    fun integer(integer: Int): ModTransfer {
        serializer.writeInt(integer)
        return this
    }

    fun send(channel: String?, user: User) {
        user.sendPacket(PacketPlayOutCustomPayload(channel, serializer))
    }
}