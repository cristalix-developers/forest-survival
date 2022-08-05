package me.reidj.forest.listener

import me.reidj.forest.app
import me.reidj.forest.clientSocket
import me.reidj.forest.user.DefaultElements
import me.reidj.forest.user.User
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import protocol.SaveUserPackage
import protocol.StatPackage
import java.util.concurrent.TimeUnit

/**
 * @project : forest
 * @author : Рейдж
 **/
object JoinEvent : Listener {

    @EventHandler
    fun AsyncPlayerPreLoginEvent.handle() {
        try {
            val statPckg = clientSocket.writeAndAwaitResponse<StatPackage>(StatPackage(uniqueId)).get(5L, TimeUnit.SECONDS)
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
    fun PlayerQuitEvent.handle() {
        val uuid = player.uniqueId
        val user = app.userMap.remove(uuid) ?: return
        clientSocket.write(SaveUserPackage(uuid, user.stat))
    }
}