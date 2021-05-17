package ru.func.mod

import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import org.lwjgl.input.Keyboard
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.animate
import ru.cristalix.uiengine.utility.*

class Banner {

    private val leftText = text {
        align = TOP_LEFT
        origin = TOP_LEFT
        offset.x += 10
        offset.y += 7
        size = V3(600.0, 50.0, 0.0)
        scale = V3(4.0, 4.0, 4.0)
    }
    private val rightText = text {
        align = TOP_RIGHT
        origin = TOP_RIGHT
        content = "Закрыть - [X]"
        offset.x -= 10
        offset.y += 7
        size = V3(600.0, 50.0, 0.0)
        scale = V3(4.0, 4.0, 4.0)
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
        size = V3(1294.0, 350.0, 0.0)
        color = Color(0, 0, 0, 0.1)
        enabled = false
        scale = V3(0.27, 0.27, 0.27)
        addChild(rightText, leftText, photo)
    }

    init {
        UIEngine.overlayContext.addChild(block)

        UIEngine.registerHandler(PluginMessage::class.java) {
            if (channel == "banner-new") {
                photo.textureLocation = ResourceLocation.of(NAMESPACE, "forest/${data.readInt()}.png")
                block.enabled = true
                leftText.content = NetUtil.readUtf8(data)
                block.animate(0.2) {
                    color = Color(0, 0, 0, 0.62)
                }
                photo.animate(0.2) {
                    color = Color(255, 255, 255, 1.0)
                }
                UIEngine.overlayContext.schedule(8) {
                    end()
                }
            }
        }
        UIEngine.registerHandler(KeyPress::class.java) {
            if (block.enabled && key == Keyboard.KEY_X)
                end()
        }
    }

    private fun end() {
        block.animate(0.3) {
            color = Color(0, 0, 0, 0.1)
        }
        photo.animate(0.3) {
            color = Color(0, 0, 0, 0.1)
        }
        UIEngine.overlayContext.schedule(0.3) {
            block.enabled = false
        }
    }
}