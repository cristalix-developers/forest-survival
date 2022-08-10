package me.reidj.forest.user.prepare

import me.reidj.forest.user.User

fun interface PrepareUser {

    fun execute(user: User)

}