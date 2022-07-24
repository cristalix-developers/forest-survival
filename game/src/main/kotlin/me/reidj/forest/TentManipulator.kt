package me.reidj.forest

import me.func.mod.util.command
import me.reidj.forest.channel.item.ItemList
import org.bukkit.Bukkit

class TentManipulator {

    init {
        command("tent") { player, args ->
            if (args.isNotEmpty()) {

                val user = app.getUser(player)!!

                user.ifTent {
                    when (args[0]) {
                        "chest" -> {
                            val chest = user.tentInventory
                            val chestLevel = user.stat.placeLevel
                            if (chest.size / 9 != chestLevel) {
                                if (chest.size / 9 > chestLevel) {
                                    me.reidj.forest.channel.ModHelper.error(player, "Мелкая палатка")
                                    return@ifTent
                                }
                                val newInventory = Bukkit.createInventory(chest.holder, chestLevel * 9)
                                newInventory.contents = chest.contents
                                user.tentInventory = newInventory
                            }
                            player.openInventory(user.tentInventory)
                        }
                        "hide" -> {
                            user.tent?.remove()
                            user.tent = null
                            user.stat.place = null
                            player.inventory.addItem(ItemList.valueOf("TENT" + user.stat.placeLevel).item)
                        }
                    }
                }
            }
        }
    }
}