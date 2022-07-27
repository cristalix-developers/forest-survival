
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import dev.xdark.clientapi.entity.AbstractClientPlayer
import dev.xdark.clientapi.entity.EntityProvider
import dev.xdark.clientapi.math.BlockPos
import dev.xdark.clientapi.util.EnumFacing
import dev.xdark.feder.NetUtil
import io.netty.buffer.Unpooled
import ru.cristalix.clientapi.JavaMod
import ru.cristalix.uiengine.UIEngine
import java.util.*

object CorpseManager {

    private val corpses = mutableListOf<AbstractClientPlayer>()

    init {
        mod.registerChannel("func:corpse-clear") {
            corpses.forEach { JavaMod.clientApi.minecraft().world.removeEntity(it) }
            corpses.clear()
        }

        mod.registerChannel("forest:corpse") {
            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            val name = NetUtil.readUtf8(this)

            if (corpses.size > 36)
                corpses.clear()

            val corpse = JavaMod.clientApi.entityProvider()
                .newEntity(EntityProvider.PLAYER, JavaMod.clientApi.minecraft().world) as AbstractClientPlayer

            corpse.setUniqueId(uuid)

            val profile = GameProfile(uuid, name)
            profile.properties.put("skinURL", Property("skinURL", NetUtil.readUtf8(this)))
            profile.properties.put("skinDigest", Property("skinDigest", NetUtil.readUtf8(this)))
            corpse.gameProfile = profile

            val info = JavaMod.clientApi.clientConnection().newPlayerInfo(profile)
            info.responseTime = -2
            info.skinType = "DEFAULT"
            JavaMod.clientApi.clientConnection().addPlayerInfo(info)

            val x = readDouble()
            var y = readDouble()
            val z = readDouble()
            var counter = 0
            var id: Int
            do {
                y -= 0.15
                counter++
                id = JavaMod.clientApi.minecraft().world.getBlockState(x, y, z).id
            } while ((id == 0 || id == 171 || id == 96 || id == 167) && counter < 50)

            corpse.enableSleepAnimation(
                BlockPos.of(x.toInt(), y.toInt(), z.toInt()), EnumFacing.NORTH
            )

            corpse.teleport(x, y + 0.2, z)
            corpse.setNoGravity(false)

            corpses.add(corpse)

            readInt().apply {
                UIEngine.schedule(this) {
                    UIEngine.clientApi.clientConnection()
                        .sendPayload("corpse:remove", Unpooled.copiedBuffer(uuid.toString(), Charsets.UTF_8))
                    corpses.remove(corpse)
                    JavaMod.clientApi.minecraft().world.removeEntity(corpse)
                }
            }
            JavaMod.clientApi.minecraft().world.spawnEntity(corpse)
        }
    }

}