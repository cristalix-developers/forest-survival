package ru.func.mod

import KotlinMod
import dev.xdark.clientapi.entity.EntityArmorStand
import dev.xdark.clientapi.entity.EntityLightningBolt
import dev.xdark.clientapi.entity.EntityPlayerSP
import dev.xdark.clientapi.entity.EntityProvider
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.event.render.GuiOverlayRender
import dev.xdark.clientapi.inventory.EntityEquipmentSlot
import dev.xdark.clientapi.item.ItemStack
import dev.xdark.clientapi.nbt.NBTPrimitive
import dev.xdark.clientapi.nbt.NBTTagCompound
import dev.xdark.clientapi.nbt.NBTTagString
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.V3
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Forest : KotlinMod() {

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
    private var radius = 5.0
    private var omega = 0.1

    private lateinit var player: EntityPlayerSP
    private lateinit var bolt: EntityLightningBolt

    override fun onEnable() {
        UIEngine.initialize(this)

        player = clientApi.minecraft().player

        registerHandler<PluginMessage> {
            if (channel == "guide") {
                helicopter = clientApi.entityProvider()
                    .newEntity(EntityProvider.ARMOR_STAND, clientApi.minecraft().world) as EntityArmorStand
                bolt = clientApi.entityProvider()
                    .newEntity(EntityProvider.LIGHTNING_BOLT, clientApi.minecraft().world) as EntityLightningBolt

                UIEngine.overlayContext.schedule(0.05) {
                    clientApi.minecraft().world.removeEntity(bolt)
                }

                helicopter.setItemInSlot(EntityEquipmentSlot.HEAD, helicopterItem)

                val local = clientApi.minecraft().player

                // helicopter.isInvisible = true
                helicopter.setNoGravity(false)
                helicopter.teleport(local.x, local.y, local.z)

                helicopter.entityId = (-Math.random() * 1000).toInt()
                helicopter.setUniqueId(UUID.randomUUID())

                clientApi.minecraft().world.spawnEntity(helicopter)

                player.startRiding(helicopter, true)

                animationActive = true
            } else if (channel == "complete_resources") {
                BarManager()
                Map()
            }
        }

        registerHandler<GuiOverlayRender> {
            if (!animationActive)
                return@registerHandler

            val now = System.currentTimeMillis()
            if (lastTime < 1L)
                lastTime = now
            if ((now - lastTime) / 1000 > 1) {
                // Если прошла секунда
                lastTime = now
                seconds++
            }
            if (seconds < 12) {
                helicopter.teleport(helicopter.x, helicopter.y + 0.0006, helicopter.z + 0.01)
                clientApi.minecraft().world.setRainStrength(0.1F * (seconds - 2))

                if (seconds > 8) {
                    spawnBoltAt(helicopter.x + Math.random() * 10, helicopter.y + 10, helicopter.z + Math.random() * 10)
                }
            } else if (seconds < 22) {
                if (helicopterCenter == null) {
                    spawnBoltAt(helicopter.x, helicopter.y - 2, helicopter.z)
                    clientApi.minecraft().world.setRainStrength(1F)

                    helicopterCenter = V3(helicopter.x, helicopter.y, helicopter.z)
                }
                val angle = Math.toRadians(System.currentTimeMillis().toDouble() % (360 * (1 / omega)))
                helicopter.teleport(
                    helicopterCenter!!.x + sin(angle * omega) * radius,
                    helicopter.y - 0.01,
                    helicopterCenter!!.z + cos(angle * omega) * radius
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

    fun spawnBoltAt(x: Double, y: Double, z: Double) {
        bolt.teleport(x, y, z)
        bolt.entityId = (-Math.random() * 1000).toInt()
        bolt.setUniqueId(UUID.randomUUID())
        clientApi.minecraft().world.spawnEntity(bolt)
    }
}