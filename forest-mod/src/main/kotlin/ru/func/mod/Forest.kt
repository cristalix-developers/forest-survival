package ru.func.mod

import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.resource.ResourceLocation
import org.lwjgl.input.Keyboard
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

const val NAMESPACE = "forest"
const val FILE_STORE = "http://51.38.128.132/"

class Forest : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)

        Guide()
        Indicator()
        BonfireIndicator()
        ItemTitle()
        LoseHeart()
        Temperature()
        TentSettings()

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
            load("forest/apple.png", "08880C088F83D1890028117"),
            load("forest/stone.png", "08880C088F83D2890028118"),
            load("forest/flint.png", "08880C088F83D8390028117"),
            load("forest/red_mushroom.png", "08880C088F43D8890028118"),
            load("forest/heal.png", "08880C088F53D8890028117"),
        ).thenRun {
            BarManager()
            Highlight()
            Banner()

            registerHandler<KeyPress> {
                if (key == Keyboard.KEY_C)
                    clientApi.chat().sendChatMessage("/craft")
            }
        }
    }

    private fun load(path: String, hash: String): RemoteTexture {
        return RemoteTexture(ResourceLocation.of(NAMESPACE, path), hash)
    }
}