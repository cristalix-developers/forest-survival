package ru.func.mod

import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.resource.ResourceLocation
import org.lwjgl.input.Keyboard
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

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

        registerHandler<KeyPress> {
            if (key == Keyboard.KEY_C)
                clientApi.chat().sendChatMessage("/craft")
        }
    }
}