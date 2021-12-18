package me.reidj.forest.knowledge

import me.reidj.forest.user.User

enum class Knowledge(private val picture: String, private val message: String) {

    APPLE("apple", "Новый предмет"),
    FLINT("flint", "Новый предмет"),
    HEAL("heal", "Новый предмет"),
    TOXIC("red_mushroom", "Новый предмет"),
    STONE("stone", "Новый предмет"),
    COLD("cold", "Новая земля"),
    HOT("hot", "Новая земля"),;

    fun tryGive(user: User) {
        if (user.stat!!.knowledge.contains(this))
            return
        me.reidj.forest.channel.ModHelper.banner(user, picture, message)
        user.stat!!.knowledge.add(this)
    }
}