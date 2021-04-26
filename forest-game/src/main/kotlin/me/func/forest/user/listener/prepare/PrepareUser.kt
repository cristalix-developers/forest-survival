package me.func.forest.user.listener.prepare

import me.func.forest.user.User

fun interface PrepareUser {

    fun execute(user: User)

}