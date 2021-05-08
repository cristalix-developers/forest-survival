package ru.func.mod

import KotlinMod
import ru.cristalix.uiengine.UIEngine

class Forest : KotlinMod() {

    override fun onEnable() {
        UIEngine.initialize(this)
        BarManager()
        Guide()
    }
}