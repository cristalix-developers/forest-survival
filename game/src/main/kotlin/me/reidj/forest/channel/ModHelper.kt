package me.reidj.forest.channel

import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.reidj.forest.data.Effect
import me.reidj.forest.effect.EffectType
import me.reidj.forest.user.User
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

object ModHelper {

    private val delay = ArrayList<UUID>()

    fun indicator(int: Int, location: Location) {
        org.bukkit.Bukkit.getOnlinePlayers().forEach {
            ModTransfer(location.x, location.y, location.z, int).send("bonfire-new", it)
        }
    }

    fun highlight(player: Player) = ModTransfer().send("highlight", player)

    fun updateTemperature(user: User) = ModTransfer(user.stat.temperature).send("temperature-update", user.player)

    fun updateWaterAmount(user: User) = ModTransfer(user.stat.water).send("forest:water-level", user.player)

    fun activeEffect(isNewEffect: Boolean, user: User, data: Effect) {
        if (isNewEffect) {
            user.stat.activeEffects.add(data)
        }
        val type = EffectType.valueOf(data.objectName)
        ModTransfer(
            data.uuid.toString(),
            "§3${type.title}§f",
            type.description,
            type.name.lowercase() + ".png",
            data.duration
        ).send("forest:active-effect", user.player)
    }

    fun banner(user: User, path: String, content: String) {
        val uuid = user.stat.uuid
        if (delay.contains(uuid)) {
            after(10 * 20) { banner(user, path, content) }
            return
        }
        after(10 * 20) { delay.remove(uuid) }
        delay.add(uuid)
        ModTransfer(path, content).send("banner-new", user.player)
    }
}