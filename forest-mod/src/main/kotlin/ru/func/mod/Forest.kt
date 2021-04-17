package ru.func.mod

import dev.xdark.clientapi.ClientApi
import dev.xdark.clientapi.entry.ModMain
import ru.cristalix.uiengine.UIEngine

class Forest : ModMain {

    override fun load(api: ClientApi) {
        UIEngine.initialize(api)
    }

    override fun unload() {
        UIEngine.uninitialize()
    }
}