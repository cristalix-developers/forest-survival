package ru.func.mod

import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.resource.ResourceLocation
import dev.xdark.feder.NetUtil
import org.lwjgl.input.Keyboard
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.utility.*

class LevelNew {

    init {
        val width = 150.0
        val height = 200.0

        val topText = text {
            align = CENTER
            origin = CENTER
            shadow = true
        }
        val topBlock = rectangle {
            align = TOP
            origin = TOP
            color = Color(0, 0, 0, 0.62)
            size = V3(width, height / 10, 0.0)
            addChild(topText)
        }
        val bottomText = text {
            content = "Закрыть [ X ]"
            align = CENTER
            origin = CENTER
            shadow = true
        }
        val bottomBlock = rectangle {
            align = BOTTOM
            origin = BOTTOM
            color = Color(0, 0, 0, 0.62)
            size = V3(width, height / 10, 0.0)
            addChild(bottomText)
        }
        val middleImage = rectangle {
            origin = TOP
            align = TOP
            size = V3(height / 10 * 3, height / 10 * 3, 0.0)
            color = WHITE
        }
        val middleBlock = rectangle {
            origin = CENTER
            align = CENTER
            size = V3(width / 10 * 8, height / 10 * 8, 0.0)
            addChild(middleImage)
        }
        val block = rectangle {
            enabled = false
            color = Color(0, 0, 0, 0.62)
            size = V3(width, height, 0.0)
            align = CENTER
            origin = CENTER
            addChild(topBlock, middleBlock, bottomBlock)
        }
        UIEngine.overlayContext.addChild(block)
        UIEngine.registerHandler(PluginMessage::class.java) {
            if (channel == "level-new") {
                block.enabled = true
                topText.content = "Получен ${data.readInt()} уровень"
                middleImage.textureLocation = ResourceLocation.of(NAMESPACE, "${NetUtil.readUtf8(data)}.png")
                val crafts = NetUtil.readUtf8(data).split("next")
                crafts.mapIndexed { i, it ->
                    text {
                        content = "Рецепт $it"
                        align = TOP
                        origin = TOP
                        align.y += i * (height / 10 * 5 / (crafts.size + 1))
                    }
                }.forEach(middleBlock::addChild)
            }
        }
        UIEngine.registerHandler(KeyPress::class.java) {
            if (block.enabled && key == Keyboard.KEY_X)
                block.enabled = false
        }
    }

}