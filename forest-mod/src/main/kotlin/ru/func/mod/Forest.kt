package ru.func.mod

import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entity.EntityArmorStand
import dev.xdark.clientapi.entity.EntityPlayerSP
import dev.xdark.clientapi.entity.EntityProvider
import dev.xdark.clientapi.entry.ModMain
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.event.render.GuiOverlayRender
import dev.xdark.clientapi.inventory.EntityEquipmentSlot
import dev.xdark.clientapi.item.ItemStack
import dev.xdark.clientapi.nbt.NBTPrimitive
import dev.xdark.clientapi.nbt.NBTTagCompound
import dev.xdark.clientapi.nbt.NBTTagString
import ru.cristalix.uiengine.UIEngine
import java.util.*

lateinit var api: ClientApi

class Forest : ModMain {

    private val helicopterItem: ItemStack = ItemStack.of(
        NBTTagCompound.of(
            mapOf(
                "id" to NBTTagString.of("emerald"),
                "Count" to NBTPrimitive.of(1),
                "Damage" to NBTPrimitive.of(0),
                "tag" to NBTTagCompound.of(mapOf(
                    "forest" to NBTTagString.of("helic")
                ))
            )
        )
    )

    private lateinit var helicopter: EntityArmorStand

    private var seconds = 0
    private var lastTime = 0L

    private var animationActive = false

    private lateinit var player: EntityPlayerSP

    override fun load(clientApi: ClientApi) {
        UIEngine.initialize(clientApi)
        api = clientApi

        BarManager()
        Map()

        player = api.minecraft().player

        UIEngine.registerHandler(PluginMessage::class.java) {
            if (channel == "guide") {
                helicopter = api.entityProvider()
                    .newEntity(EntityProvider.ARMOR_STAND, api.minecraft().world) as EntityArmorStand

                helicopter.setItemInSlot(EntityEquipmentSlot.HEAD, helicopterItem)

                val local = api.minecraft().player

               // helicopter.isInvisible = true
                helicopter.setNoGravity(false)
                helicopter.teleport(local.x, local.y, local.z)

                helicopter.entityId = (-Math.random() * 1000).toInt()
                helicopter.setUniqueId(UUID.randomUUID())

                api.minecraft().world.spawnEntity(helicopter)

                player.startRiding(helicopter, true)

                animationActive = true
            }
        }

        UIEngine.registerHandler(GuiOverlayRender::class.java) {
            if (!animationActive)
                return@registerHandler

            if (lastTime < 1L)
                lastTime = System.currentTimeMillis()
            if ((System.currentTimeMillis() - lastTime) / 1000 > 1) {
                // Если прошла секунда
                lastTime = System.currentTimeMillis()
                seconds++
                if (seconds == 10) {
                    api.minecraft().world.time = 24000
                }
            }
            if (seconds < 40) {
                val x = helicopter.x
                val y = helicopter.y
                val z = helicopter.z

                helicopter.teleport(x, y + 0.0001, z + 0.004)
            }
        }
    }

    override fun unload() {
        UIEngine.uninitialize()
    }
}