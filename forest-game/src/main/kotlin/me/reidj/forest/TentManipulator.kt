package me.reidj.forest

import clepto.bukkit.B
import me.reidj.forest.item.ItemList
import org.bukkit.Bukkit

class TentManipulator {

    init {
        B.regCommand({ player, args ->
            if (args.isNotEmpty()) {

                val user = app.getUser(player)!!

                user.ifTent {
                    when (args[0]) {
                        "chest" -> {
                            val chest = user.homeInventory
                            val chestLevel = user.stat!!.placeLevel
                            if (chest.size / 9 != chestLevel) {
                                if (chest.size / 9 > chestLevel) {
                                    me.reidj.forest.channel.ModHelper.error(user, "Мелкая палатка")
                                    return@ifTent
                                }
                                val newInventory = Bukkit.createInventory(chest.holder, chestLevel * 9)
                                newInventory.contents = chest.contents
                                user.homeInventory = newInventory
                            }
                            player.openInventory(user.homeInventory)
                        }
                        "hide" -> {
                            user.tent?.remove()
                            user.tent = null
                            user.stat!!.place = null
                            player.inventory.addItem(ItemList.valueOf("TENT" + user.stat!!.placeLevel).item)
                        }
                    }
                }
            }
            null
        }, "tent", "")
    }

}