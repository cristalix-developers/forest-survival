package ru.func.mod

import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.network.PluginMessage
import dev.xdark.clientapi.event.render.RenderTickPre
import dev.xdark.clientapi.resource.ResourceLocation
import org.lwjgl.input.Keyboard
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.animate
import ru.cristalix.uiengine.utility.*

const val NAMESPACE = "forest"

class Forest : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        loadTexture(
            RemoteTexture(ResourceLocation.of(NAMESPACE, "health_bar.png"), "08880C088F83D8890128127"),
            RemoteTexture(ResourceLocation.of(NAMESPACE, "xp_bar.png"), "08880C088F83D8890128128"),
            RemoteTexture(ResourceLocation.of(NAMESPACE, "energy.png"), "08880C088F83D8890128129"),
            RemoteTexture(ResourceLocation.of(NAMESPACE, "second_level.png"), "08880C088F83D8890128111"),
        )

        BarManager()
        //Guide()
        Indicator()
        BonfireIndicator()
        ItemTitle()
        Highlight()
        LevelNew()
        LoseHeart()

        registerHandler<KeyPress> {
            if (key == Keyboard.KEY_C)
                clientApi.chat().sendChatMessage("/craft")
        }

        var lastUpdate: Long = 0
        var hidden = true

        val temperature = text {
            align = TOP
            origin = TOP
            color = WHITE
            scale = V3(1.4, 1.4, 1.4)
            offset.y -= 40
        }

        var prevTemp = 36.6

        registerHandler<PluginMessage> {
            if (channel == "temperature-update") {
                val original = data.readDouble()
                val temp = (original * 10).toInt() / 10.0

                if (prevTemp < original)
                    temperature.content = "$temp §c▲"
                else
                    temperature.content = "$temp §b▼"
                if (original < 31 || original > 39)
                    temperature.content = ""

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
        registerHandler<RenderTickPre> {
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