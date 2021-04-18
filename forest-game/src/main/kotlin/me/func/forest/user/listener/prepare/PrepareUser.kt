package me.func.forest.user.listener.prepare

import me.func.forest.user.User

@FunctionalInterface
interface PrepareUser {

    fun execute(user: User)

}