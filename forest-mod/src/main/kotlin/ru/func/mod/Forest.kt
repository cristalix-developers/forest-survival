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

        loadTextures(
            load("health_bar.png", "08880C088F83D8890128127"),
            load("xp_bar.png", "08880C088F83D8890128128"),
            load("energy.png", "08880C088F83D8890128129"),
            load("second_level.png", "08880C088F83D8890128111"),
            load("forest/1.png", "08880C088F83D8890028112"),
            load("forest/2.png", "08880C088F83D8890028113"),
            load("forest/3.png", "08880C088F83D8890028114"),
            load("forest/4.png", "08880C088F83D8890028115"),
            load("forest/5.png", "08880C088F83D8890028116"),
            load("forest/cold.png", "08880C088F83D8890028117"),
            load("forest/hot.png", "08880C088F83D8890028118"),
        ).thenRun {
            BarManager()
            //Guide()
            Indicator()
            BonfireIndicator()
            ItemTitle()
            Highlight()
            Banner()
            LoseHeart()
            Temperature()

            registerHandler<KeyPress> {
                if (key == Keyboard.KEY_C)
                    clientApi.chat().sendChatMessage("/craft")
            }
        }
    }

    fun load(path: String, hash: String): RemoteTexture {
        return RemoteTexture(ResourceLocation.of(NAMESPACE, path), hash)
    }
}