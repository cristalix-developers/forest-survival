package ru.func.mod

import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.event.render.RenderTickPre
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.UIEngine.registerHandler
import ru.cristalix.uiengine.element.animate
import ru.cristalix.uiengine.utility.*

class Temperature {

    init {
        var lastUpdate: Long = 0
        var hidden = true

        val temperature = text {
            align = TOP
            origin = TOP
            color = WHITE
            scale = V3(1.4, 1.4, 1.4)
            offset.y -= 35
        }

        var prevTemp = 36.6

        registerHandler(PluginMessage::class.java) {
            if (channel == "temperature-update") {
                val original = data.readDouble()
                val temp = (original * 10).toInt() / 10.0

                if (prevTemp < original)
                    temperature.content = "$temp §c▲"
                else
                    temperature.content = "$temp §b▼"
                if (original < 31)
                    temperature.content = "§b▼ §fгипотермия §b▼"
                else if (original > 39)
                    temperature.content = "§c▲ §fгипертермия §c▲"

                prevTemp = original

                lastUpdate = System.currentTimeMillis()
                if (hidden) {
                    temperature.animate(15, Easings.EXPO_OUT) {
                        offset.y += 40
                    }
                }
                hidden = false
            }
        }
        registerHandler(RenderTickPre::class.java) {
            if (System.currentTimeMillis() - lastUpdate > 10_000 && !hidden) {
                temperature.animate(15, Easings.EXPO_OUT) {
                    offset.y -= 40
                }
                hidden = true
            }
        }
        UIEngine.overlayContext.addChild(temperature)
    }

}