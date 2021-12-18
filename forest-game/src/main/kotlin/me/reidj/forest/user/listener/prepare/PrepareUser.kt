package me.reidj.forest.user.listener.prepare

import me.reidj.forest.user.User

fun interface PrepareUser {

    fun execute(user: User)

}