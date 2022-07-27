package me.reidj.forest.command

import me.func.mod.util.command

/**
 * @project : forest
 * @author : Рейдж
 **/
object PlayerCommands {

    private val textureUrl = System.getenv("http://51.38.128.132/new.zip")
    private const val TEXTURE_HASH = "11180C188F11D8890132123"

    init {
        command("rp") { player, _ -> player.setResourcePack(textureUrl, TEXTURE_HASH) }
    }
}