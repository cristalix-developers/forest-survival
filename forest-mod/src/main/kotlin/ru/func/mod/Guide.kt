package ru.func.mod

import dev.xdark.clientapi.entity.EntityArmorStand
import dev.xdark.clientapi.entity.EntityLightningBolt
import dev.xdark.clientapi.entity.EntityProvider
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.event.render.GuiOverlayRender
import dev.xdark.clientapi.inventory.EntityEquipmentSlot
import dev.xdark.clientapi.item.ItemStack
import dev.xdark.clientapi.nbt.NBTPrimitive
import dev.xdark.clientapi.nbt.NBTTagCompound
import dev.xdark.clientapi.nbt.NBTTagString
import io.netty.buffer.Unpooled
import ru.cristalix.clientapi.JavaMod
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.V3
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Guide {

    private val helicopterItem: ItemStack = ItemStack.of(
        NBTTagCompound.of(
            mapOf(
                "id" to NBTTagString.of("emerald"),
                "Count" to NBTPrimitive.of(1),
                "Damage" to NBTPrimitive.of(0),
                "tag" to NBTTagCompound.of(
                    mapOf(
                        "forest" to NBTTagString.of("helic")
                    )
                )
            )
        )
    )

    private lateinit var helicopter: EntityArmorStand

    private var seconds = 0

    private var lastTime = 0L
    private var animationActive = false

    private var helicopterCenter: V3? = null
    private var radius = 6.0
    private var omega = 0.14

    init {
        val player = clientApi.minecraft().player

        UIEngine.registerHandler(PluginMessage::class.java) {
            if (channel == "guide") {
                helicopter = clientApi.entityProvider()
                    .newEntity(EntityProvider.ARMOR_STAND, JavaMod.clientApi.minecraft().world) as EntityArmorStand

                helicopter.setItemInSlot(EntityEquipmentSlot.HEAD, helicopterItem)

                val local = clientApi.minecraft().player

                helicopter.isInvisible = true
                helicopter.setNoGravity(false)
                helicopter.teleport(local.x, local.y, local.z)
                //helicopter.headRotation = Rotations.of(Math.toRadians(-45.0).toFloat(), 0F, 0F)

                helicopter.entityId = (-Math.random() * 1000).toInt()
                helicopter.setUniqueId(UUID.randomUUID())

                clientApi.minecraft().world.spawnEntity(helicopter)

                player.startRiding(helicopter, true)

                animationActive = true
            }
        }

        UIEngine.registerHandler(GuiOverlayRender::class.java) {
            if (!animationActive)
                return@registerHandler

            val now = System.currentTimeMillis()
            if (lastTime < 1L)
                lastTime = now
            if ((now - lastTime) / 1000 > 1) {
                // Если прошла секунда
                lastTime = now
                seconds++
                if (seconds in 9..11) {
                    spawnBoltAt(helicopter.x + (Math.random() - 0.5) * 100, helicopter.y + 100, helicopter.z + (Math.random() - 0.5) * 100)
                }

                val chunk = clientApi.minecraft().world.chunkProvider.getLoadedChunk(helicopter.x.toInt() shr 4, helicopter.z.toInt() shr 4)
                if (chunk.getBlockState(helicopter.x.toInt(), helicopter.y.toInt() - 2, helicopter.z.toInt()).block.id != 0) {
                    clientApi.minecraft().world.removeEntity(helicopter)

                    clientApi.clientConnection().sendPayload("guide-end", Unpooled.buffer())

                    GlowEffect.show(0.4, 255, 0, 0)

                    seconds = 100
                }
            }
            if (seconds < 12) {
                helicopter.teleport(helicopter.x, helicopter.y + 0.0006, helicopter.z + 0.01)
                clientApi.minecraft().world.setRainStrength(0.1F * (seconds - 2))
            } else if (seconds < 22) {
                if (helicopterCenter == null) {
                    spawnBoltAt(helicopter.x, helicopter.y - 2, helicopter.z)
                    clientApi.minecraft().world.setRainStrength(1F)

                    helicopterCenter = V3(helicopter.x, helicopter.y, helicopter.z)
                }
                val angle = Math.toRadians(System.currentTimeMillis().toDouble() % (360 * (1 / omega)))
                val x = helicopterCenter!!.x + sin(angle * omega) * radius
                val z = helicopterCenter!!.z + cos(angle * omega) * radius

                helicopter.teleport(
                    x,
                    helicopter.y - 0.01,
                    z
                )
                helicopter.setYaw(
                    Math.toDegrees(
                        -atan2(
                            helicopterCenter!!.x - helicopter.x,
                            helicopterCenter!!.z - helicopter.z
                        )
                    ).toFloat()
                )
            }
        }
    }

    private fun spawnBoltAt(x: Double, y: Double, z: Double) {
        val bolt = clientApi.entityProvider()
            .newEntity(EntityProvider.LIGHTNING_BOLT, clientApi.minecraft().world) as EntityLightningBolt
        bolt.teleport(x, y, z)
        bolt.entityId = (-Math.random() * 1000).toInt()
        bolt.setUniqueId(UUID.randomUUID())
        bolt.boltLivingTime = 60

        clientApi.minecraft().world.spawnEntity(bolt)
    }
}