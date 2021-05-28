package ru.func.mod

import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.opengl.GlStateManager
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector3f
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.utility.*
import kotlin.collections.set

class BonfireIndicator {

    private val bonfires = HashMap<String, Pair<Context3D, Double>>()
    private var maxTime = -1.0

    init {
        UIEngine.registerHandler(PluginMessage::class.java) {
            if (channel == "bonfire-new") {
                val v3 = V3(data.readDouble(), data.readDouble(), data.readDouble())
                val v = "${v3.x} ${v3.y} ${v3.z}"
                val pair = bonfires[v]
                if (pair != null) {
                    bonfires[v] = pair.first to data.readInt().toDouble()
                    return@registerHandler
                }

                val context = Context3D(v3)

                val bar = rectangle {
                    origin = Relative.LEFT
                    align = Relative.LEFT
                    offset.x = 0.5
                    size.x = 10.0
                    size.y = 3.0
                    color = Color(240, 51, 51)
                }

                val body = rectangle {
                    size = V3(16.0, 4.0)
                    color = Color(0, 0, 0, 0.8)
                    origin = Relative.CENTER
                    addChild(bar)
                }

                context.addChild(body)
                maxTime = maxTime.coerceAtLeast(data.readInt().toDouble())
                bonfires[v] = context to maxTime
                UIEngine.worldContexts.add(context)
            }
        }

        UIEngine.registerHandler(RenderTickPre::class.java) {
            if (bonfires.isNotEmpty()) {
                val contextsToRemove = ArrayList<Map.Entry<String, Pair<Context3D, Double>>>()
                bonfires.forEach {
                    val width = (maxTime * 2).coerceAtMost(20.0)
                    val part = minOf(it.value.second, maxTime) / maxTime

                    val body = it.value.first.children[0] as RectangleElement
                    val bar = body.children[0] as RectangleElement

                    bar.size.x = width * part
                    body.size.x = width + 2.0

                    GlStateManager.disableLighting()
                    GL11.glEnable(GL11.GL_TEXTURE_2D)
                    GL11.glDepthMask(false)

                    it.value.first.transformAndRender()

                    GlStateManager.enableLighting()
                    GL11.glDepthMask(true)
                    val player = clientApi.minecraft().player

                    val matrix = Matrix4f()
                    Matrix4f.rotate(
                        ((player.rotationYaw + 180) / 180 * Math.PI).toFloat(),
                        Vector3f(0f, -1f, 0f),
                        matrix,
                        matrix
                    )
                    Matrix4f.rotate(
                        (player.rotationPitch / 180 * Math.PI).toFloat(),
                        Vector3f(-1f, 0f, 0f),
                        matrix,
                        matrix
                    )
                    it.value.first.matrices[rotationMatrix] = matrix
                    if (it.value.second < 0)
                        contextsToRemove.add(it)
                }
                contextsToRemove.forEach {
                    UIEngine.worldContexts.remove(it.value.first)
                    bonfires.remove(it.key)
                }
                bonfires.replaceAll { _, it -> it.first to it.second - 0.05 }
                contextsToRemove.clear()
            }
        }
    }
}