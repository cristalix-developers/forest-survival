package me.reidj.forest.craft

import me.func.mod.util.command
import me.reidj.forest.app
import me.reidj.forest.channel.ModHelper
import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.inventory.ClickableItem
import ru.cristalix.core.inventory.ControlledInventory
import ru.cristalix.core.inventory.InventoryContents
import ru.cristalix.core.inventory.InventoryProvider

class CraftManager {

    private val menu = ControlledInventory.builder()
        .title("Крафты")
        .rows(3)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XXXXXXXXX",
                    "XXXXXXXXX",
                    "XXXXXXXXX"
                )

                val user = app.getUser(player)!!

                CraftItem.values().sortedBy { it.minLevel }.forEach { item ->
                    val locked = !user.hasLevel(item.minLevel)
                    var itemWithLore = item.to.item.clone()
                    val pairs = item.from
                    val title = ChatColor.stripColor(itemWithLore.itemMeta.displayName)

                    if (locked) {
                        val meta = itemWithLore.itemMeta
                        meta.displayName = "§c$title"
                        itemWithLore.itemMeta = meta

                        val nms = CraftItemStack.asNMSCopy(itemWithLore)
                        nms.tag ?: NBTTagCompound()
                        nms.tag.setInt("color", -15658735)
                        itemWithLore = CraftItemStack.asCraftMirror(nms)

                        contents.add('X', ClickableItem.empty(itemWithLore))
                        contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
                        return@forEach
                    }

                    itemWithLore.lore = listOf(
                        *itemWithLore.lore?.map { "§7${it}" }?.toTypedArray() ?: arrayOf(),
                        "", "§7Необходимые ресурсы §f㨃§7: ",
                        *pairs.map { " §bx${it.second} §f${it.first.item.itemMeta.displayName}" }
                            .toTypedArray()
                    )

                    contents.add('X', ClickableItem.of(itemWithLore) {
                        if (locked)
                            return@of
                        val inventory = player.inventory

                        pairs.forEach {
                            if (!inventory.contains(it.first.item.getType(), it.second)) {
                                ModHelper.error(
                                    player,
                                    "Нет `§c${ChatColor.stripColor(it.first.item.itemMeta.displayName)}§f`!"
                                )
                                return@of
                            }
                        }

                        // Отнятие у игрока предметов
                        pairs.forEach { pair ->
                            val clone = pair.first.item.clone()
                            repeat(pair.second) { inventory.removeItem(clone) }
                        }
                        player.updateInventory()
                        inventory.addItem(item.to.item)
                    })
                }
                contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
            }
        }).build()

    init {
        command("craft") { player, _ -> menu.open(player) }
    }
}