package me.reidj.forest.listener

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.command
import me.reidj.forest.BARRIER
import me.reidj.forest.app
import me.reidj.forest.channel.item.ItemList
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.EquipmentSlot

class TentManipulator : Listener {

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
                                    Anime.itemTitle(player, BARRIER, "Мелкая палатка", null)
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

    @EventHandler
    fun PlayerInteractAtEntityEvent.handle() {
        if (hand == EquipmentSlot.OFF_HAND)
            return
        val entity = clickedEntity
        if (entity.hasMetadata("owner") && entity.getMetadata("owner")[0].asString() == player.uniqueId.toString())
            ModTransfer().send("tent-open", player)
    }
}