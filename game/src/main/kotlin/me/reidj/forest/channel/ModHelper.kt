package me.reidj.forest.channel

import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.reidj.forest.user.User
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

object ModHelper {

    private val BARRIER = CraftItemStack.asNMSCopy(ItemStack(Material.BARRIER))
    private val delay = ArrayList<UUID>()

    fun error(player: Player, message: String) {
        player.closeInventory()
        ModTransfer(BARRIER, "Ошибка", message).send("itemtitle", player)
    }

    fun indicator(int: Int, location: Location) {
        org.bukkit.Bukkit.getOnlinePlayers().forEach {
            ModTransfer(location.x, location.y, location.z, int).send("bonfire-new", it)
        }
    }

    fun highlight(player: Player, message: String) = ModTransfer(message).send("highlight", player)

    fun updateTemperature(user: User) = ModTransfer(user.stat.temperature).send("temperature-update", user.player)

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