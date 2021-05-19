package me.func.forest.channel

import clepto.bukkit.B
import me.func.forest.user.User
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.util.*

object ModHelper {

    private val BARRIER = CraftItemStack.asNMSCopy(ItemStack(Material.BARRIER))
    private val delay = ArrayList<UUID>()

    fun error(user: User, message: String) {
        user.player.closeInventory()
        ModTransfer()
            .item(BARRIER)
            .string("Ошибка!")
            .string(message)
            .send("itemtitle", user)
    }

    fun highlight(user: User, message: String) {
        ModTransfer().string(message).send("highlight", user)
    }

    fun updateTemperature(user: User) {
        ModTransfer()
            .double(user.stat!!.temperature)
            .send("temperature-update", user)
    }

    fun banner(user: User, path: String, content: String) {
        if (delay.contains(user.uuid)) {
            B.postpone(10 * 20) { banner(user, path, content) }
            return
        }
        B.postpone(9 * 20) { delay.remove(user.uuid) }
        delay.add(user.uuid)
        ModTransfer()
            .string(path)
            .string(content)
            .send("banner-new", user)
    }
}