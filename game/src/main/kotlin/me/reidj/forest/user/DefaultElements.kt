package me.reidj.forest.user

import me.reidj.forest.data.Stat
import java.util.*

/**
 * @project : forest
 * @author : Рейдж
 **/
object DefaultElements {

    fun createNewUser(userId: UUID) = Stat(
        userId,
        false,
        20.0,
        20,
        20,
        0,
        0,
        0,
        0,
        36.6,
        3.0,
        null,
        null,
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf()
    )
}