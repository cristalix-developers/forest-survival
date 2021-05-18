package ru.func.mod

import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import io.netty.buffer.ByteBuf
import org.lwjgl.input.Keyboard
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.animate
import ru.cristalix.uiengine.utility.*

class Banner {

    private val duration = 7.0

    private val leftText = text {
        align = TOP_LEFT
        origin = TOP_LEFT
        offset.x += 20
        offset.y += 20
        size = V3(600.0, 70.0, 0.0)
        scale = V3(3.0, 3.0, 3.0)
    }
    private val rightText = text {
        align = TOP_RIGHT
        origin = TOP_RIGHT
        content = "[X] - Закрыть"
        offset.x -= 20
        offset.y += 20
        size = V3(600.0, 50.0, 0.0)
        scale = V3(3.0, 3.0, 3.0)
    }
    private val photo = rectangle {
        align = BOTTOM
        origin = BOTTOM
        color = Color(0, 0, 0, 0.1)
        size = V3(1294.0, 300.0, 0.0)
    }
    private val block = rectangle {
        align = CENTER
        origin = CENTER
        offset.y -= 100
        size = V3(1294.0, 370.0, 0.0)
        color = Color(0, 0, 0, 0.1)
        enabled = false
        scale = V3(0.24, 0.24, 0.24)
        addChild(rightText, leftText, photo)
    }

    init {
        UIEngine.overlayContext.addChild(block)

        UIEngine.registerHandler(PluginMessage::class.java) {
            if (channel == "banner-new") {
                start(data)
            }
        }
        UIEngine.registerHandler(KeyPress::class.java) {
            if (block.enabled && key == Keyboard.KEY_X)
                end()
        }
    }

    private fun end() {
        block.animate(0.5) {
            color = Color(0, 0, 0, 0.1)
        }
        photo.animate(0.5) {
            color = Color(0, 0, 0, 0.1)
        }
        UIEngine.overlayContext.schedule(duration / 10) {
            block.enabled = false
        }
    }

    private fun start(data: ByteBuf) {
        photo.textureLocation = ResourceLocation.of(NAMESPACE, "forest/${NetUtil.readUtf8(data)}.png")
        block.enabled = true
        leftText.content = NetUtil.readUtf8(data)
        block.animate(0.2) {
            color = Color(0, 0, 0, 0.62)
        }
        photo.animate(0.2) {
            color = Color(255, 255, 255, 1.0)
        }
        UIEngine.overlayContext.schedule(duration) {
            end()
        }
    }
}