package me.reidj.forest

import ru.cristalix.core.network.Capability
import ru.cristalix.core.network.CorePackage
import ru.cristalix.core.network.ISocketClient
import kotlin.reflect.KClass

/**
 * @project : forest
 * @author : Рейдж
 **/

fun ISocketClient.capabilities(vararg classes: KClass<out CorePackage>) = registerCapabilities(*classes.map {
    Capability.builder()
        .className(it.java.name)
        .notification(true)
        .build()
}.toTypedArray())
