package me.func.forest.channel

import me.func.forest.user.User
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

object ModHelper {

    private val BARRIER = CraftItemStack.asNMSCopy(ItemStack(Material.BARRIER))

    fun error(user: User, message: String) {
        user.player.closeInventory()
        ModTransfer()
            .item(BARRIER)
            .string("Ошибка!")
            .string(message)
            .send("itemtitle", user)
    }

}