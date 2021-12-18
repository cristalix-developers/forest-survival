import dev.xdark.clientapi.entity.EntityArmorStand
import dev.xdark.clientapi.entity.EntityLightningBolt
import dev.xdark.clientapi.entity.EntityProvider
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.input.MousePress
import dev.xdark.clientapi.event.render.GuiOverlayRender
import dev.xdark.clientapi.inventory.EntityEquipmentSlot
import dev.xdark.clientapi.item.ItemStack
import dev.xdark.clientapi.nbt.NBTPrimitive
import dev.xdark.clientapi.nbt.NBTTagCompound
import dev.xdark.clientapi.nbt.NBTTagString
import io.netty.buffer.Unpooled
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.mod
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.*
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Guide {

    private var animation = false

    private val wrapper = rectangle {
        size = V3(1000.0, 1000.0, 0.0)
        color = Color(0, 0, 0, 0.62)
        origin = CENTER
        align = CENTER
        enabled = false
        addChild(text {
            origin = CENTER
            align = CENTER
            content = "Выжить в тайге"
            color = WHITE
            offset.y -= 60.0
            scale = V3(4.0, 4.0, 4.0)
            shadow = true
        })
    }

    private val button = rectangle {
        size = V3(130.0, 30.0, 0.0)
        color = Color(70, 255, 70, 0.6)
        origin = CENTER
        align = CENTER
        offset.y += 70
        onHover { this.color.alpha = if (hovered) 0.9 else 0.6 }
        addChild(text {
            origin = CENTER
            align = CENTER
            content = "Начать игру"
            color = WHITE
            shadow = true
        })
    }

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
        wrapper.addChild(button)
        UIEngine.overlayContext.addChild(wrapper)
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
                if (seconds in 9..11) {
                    spawnBoltAt(
                        helicopter.x + (Math.random() - 0.5) * 100,
                        helicopter.y + 100,
                        helicopter.z + (Math.random() - 0.5) * 100
                    )
                }

                if (seconds < 22) {
                    val chunk = clientApi.minecraft().world.chunkProvider.getLoadedChunk(
                        helicopter.x.toInt() shr 4,
                        helicopter.z.toInt() shr 4
                    )
                    val under = chunk?.getBlockState(
                        helicopter.x.toInt(),
                        helicopter.y.toInt() - 2,
                        helicopter.z.toInt()
                    )?.block?.id
                    if ((under != 0 && under != 166)
                        || helicopter.y < 10
                    ) {
                        clientApi.minecraft().world.removeEntity(helicopter)
                        clientApi.clientConnection().sendPayload("guide-end", Unpooled.buffer())
                        GlowEffect.show(0.4, 255, 0, 0, 0.7)
                        clientApi.minecraft().world.setRainStrength(0F)
                        seconds = 100
                    }
                }
            }
            if (seconds < 12) {
                helicopter.teleport(helicopter.x, helicopter.y + 0.01, helicopter.z + 0.015)
                clientApi.minecraft().world.setRainStrength(0.1F * (seconds - 2))
            } else if (seconds < 22) {
                if (helicopterCenter == null) {
                    spawnBoltAt(helicopter.x, helicopter.y - 20, helicopter.z)
                    clientApi.minecraft().world.setRainStrength(1F)

                    helicopterCenter = V3(helicopter.x, helicopter.y, helicopter.z)
                }
                val angle = Math.toRadians(System.currentTimeMillis().toDouble() % (360 * (1 / omega)))
                val x = helicopterCenter!!.x + sin(angle * omega) * radius
                val z = helicopterCenter!!.z + cos(angle * omega) * radius

                helicopter.teleport(
                    x,
                    helicopter.y - 0.11,
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

        App::class.mod.registerChannel("lets-start") {
            wrapper.enabled = true
            clientApi.minecraft().setIngameNotInFocus()
        }
        registerHandler<MousePress> {
            if (wrapper.enabled) {
                animate()
                wrapper.enabled = false
                UIEngine.clientApi.minecraft().setIngameFocus()
            }
        }
        registerHandler<KeyPress> {
            if (wrapper.enabled) {
                animate()
                wrapper.enabled = false
                UIEngine.clientApi.minecraft().setIngameFocus()
            }
        }
    }

    private fun animate() {
        if (animation)
            return

        animation = true
        helicopter = clientApi.entityProvider()
            .newEntity(EntityProvider.ARMOR_STAND, clientApi.minecraft().world) as EntityArmorStand

        helicopter.setItemInSlot(EntityEquipmentSlot.HEAD, helicopterItem)

        val local = clientApi.minecraft().player

        helicopter.isInvisible = true
        helicopter.setNoGravity(false)
        helicopter.teleport(local.x, local.y, local.z)
        //helicopter.headRotation = Rotations.of(Math.toRadians(-45.0).toFloat(), 0F, 0F)

        helicopter.entityId = (-Math.random() * 1000).toInt()
        helicopter.setUniqueId(UUID.randomUUID())

        clientApi.minecraft().world.spawnEntity(helicopter)

        local.startRiding(helicopter, true)

        animationActive = true
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