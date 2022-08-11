package me.reidj.forest.listener

import me.func.mod.Anime
import me.func.mod.conversation.ModLoader
import me.func.mod.util.after
import me.func.protocol.Indicators
import me.reidj.forest.app
import me.reidj.forest.channel.ModHelper
import me.reidj.forest.clientSocket
import me.reidj.forest.protocol.SaveUserPackage
import me.reidj.forest.protocol.StatPackage
import me.reidj.forest.user.DefaultElements
import me.reidj.forest.user.User
import me.reidj.forest.util.Images
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.TimeUnit

/**
 * @project : forest
 * @author : Рейдж
 **/
class JoinEvent : Listener {

    @EventHandler
    fun AsyncPlayerPreLoginEvent.handle() {
        try {
            val statPckg = clientSocket.writeAndAwaitResponse<StatPackage>(StatPackage(uniqueId)).get(3L, TimeUnit.SECONDS)
            var stat = statPckg.stat
            if (stat == null) stat = DefaultElements.createNewUser(uniqueId)
            app.userMap[uniqueId] = User(stat)
        } catch (ex: Exception) {
            app.userMap.remove(uniqueId)
            disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Сейчас нельзя зайти на этот сервер");
            loginResult = AsyncPlayerPreLoginEvent.Result.KICK_OTHER;
            ex.printStackTrace()
        }
    }

    @EventHandler
    fun PlayerJoinEvent.handle() {
        val user = app.getUser(player)!!

        user.player = player

        player.gameMode = GameMode.SURVIVAL

        after {
            ModLoader.send("mod-bundle-1.0-SNAPSHOT.jar", player)
            ModHelper.updateTemperature(user)

            Anime.hideIndicator(player, Indicators.HEALTH, Indicators.EXP, Indicators.HUNGER, Indicators.ARMOR, Indicators.AIR_BAR, Indicators.VEHICLE)
            Anime.loadTextures(player, *Images.values().map { it.path() }.toTypedArray())

            user.spawn()
        }
        player.isOp = true
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        val uuid = player.uniqueId
        val user = app.userMap.remove(uuid) ?: return
        user.tent?.remove()
        clientSocket.write(SaveUserPackage(uuid, user.stat))
    }
}