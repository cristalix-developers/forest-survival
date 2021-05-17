package me.func.forest.knowledge

import me.func.forest.user.User

enum class Knowledge(private val picture: String, private val message: String) {

    COLD("cold", "Новая земля"),
    HOT("hot", "Новая земля"),;

    fun tryGive(user: User) {
        if (user.stat!!.knowledge.contains(this))
            return
        me.func.forest.channel.ModHelper.banner(user, picture, message)
        user.stat!!.knowledge.add(this)
    }
}