package me.reidj.forest.data

import java.util.*

/**
 * @project : forest
 * @author : Рейдж
 **/
data class Effect(
    val uuid: UUID = UUID.randomUUID(),
    var objectName: String = "",
    var duration: Int = 3,
) {
    constructor(init: Effect.() -> Unit) : this() { this.init() }
}
