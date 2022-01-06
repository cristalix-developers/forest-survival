package me.reidj.forest.channel

import clepto.bukkit.B
import me.reidj.forest.user.User
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.util.*

object ModHelper {

    private val BARRIER = CraftItemStack.asNMSCopy(ItemStack(Material.BARRIER))
    private val delay = ArrayList<UUID>()

    fun error(user: User, message: String) {
        user.player?.closeInventory()
        ModTransfer()
            .item(BARRIER)
            .string("Ошибка!")
            .string(message)
            .send("itemtitle", user)
    }

    fun indicator(int: Int, location: Location) {
        org.bukkit.Bukkit.getOnlinePlayers().forEach {
            ModTransfer()
                .double(location.x)
                .double(location.y)
                .double(location.z)
                .integer(int)
                .send("bonfire-new", me.reidj.forest.app.getUser(it)!!)
        }
    }

    fun highlight(user: User, message: String) {
        ModTransfer().string(message).send("highlight", user)
    }

    fun updateTemperature(user: User) {
        ModTransfer()
            .double(user.stat.temperature)
            .send("temperature-update", user)
    }

    fun banner(user: User, path: String, content: String) {
        val uuid = user.stat.uuid
        if (delay.contains(uuid)) {
            B.postpone(10 * 20) { banner(user, path, content) }
            return
        }
        B.postpone(9 * 20) { delay.remove(uuid) }
        delay.add(uuid)
        ModTransfer()
            .string(path)
            .string(content)
            .send("banner-new", user)
    }

    fun waterAmountUpdate(user: User) {
        ModTransfer()
            .integer(user.stat.waterAmount)
            .send("water-level", user)
    }
}