package ru.func.mod

import dev.xdark.clientapi.event.input.KeyPress
import org.lwjgl.input.Keyboard
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.uiengine.UIEngine

class Forest : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)
        BarManager()
        //Guide()
        Indicator()

        registerHandler<KeyPress> {
            if (key == Keyboard.KEY_C)
                clientApi.chat().sendChatMessage("/craft")
        }
    }
}