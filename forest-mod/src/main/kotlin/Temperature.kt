import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.mod
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.eventloop.animate
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

        val weather = text {
            align = BOTTOM_RIGHT
            origin = BOTTOM_RIGHT
            offset.y -= 12
            offset.x -= 1
            color = WHITE
            content = ""
            scale = V3(1.2, 1.2, 1.2)
            shadow = true
        }

        var prevTemp = 36.6

        App::class.mod.registerChannel("temperature-update") {
            val original = readDouble()
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

        App::class.mod.registerChannel("weather-update") {weather.content = NetUtil.readUtf8(this)}

        registerHandler<RenderTickPre> {
            if (System.currentTimeMillis() - lastUpdate > 10_000 && !hidden) {
                temperature.animate(15, Easings.EXPO_OUT) {
                    offset.y -= 40
                }
                hidden = true
            }
        }
        UIEngine.overlayContext.addChild(weather, temperature)
    }

}