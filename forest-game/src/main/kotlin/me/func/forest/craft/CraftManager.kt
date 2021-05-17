package me.func.forest.craft

import clepto.bukkit.B
import me.func.forest.app
import me.func.forest.channel.ModHelper
import org.bukkit.ChatColor
import org.bukkit.Material
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

                CraftItem.values().filter { user.hasLevel(it.minLevel) }.forEach { item ->
                    val itemWithLore = item.to.item.clone()
                    val pairs = item.from

                    itemWithLore.lore = listOf(
                        *itemWithLore.lore?.map { "§7${it}" }!!.toTypedArray(),
                        "", "§7Необходимые ресурсы §f㨃§7: ",
                        *pairs.map { " §bx${it.second} §f${ChatColor.stripColor(it.first.item.itemMeta.displayName)}" }
                            .toTypedArray()
                    )

                    contents.add('X', ClickableItem.of(itemWithLore) {
                        val inventory = player.inventory

                        pairs.forEach {
                            if (!inventory.contains(it.first.item.getType(), it.second)) {
                                ModHelper.error(
                                    user,
                                    "Нет `§c${ChatColor.stripColor(it.first.item.itemMeta.displayName)}§f`!"
                                )
                                return@of
                            }
                        }

                        // Отнятие у игрока предметов
                        pairs.forEach { pair ->
                            val clone = pair.first.item.clone()
                            clone.amount = pair.second
                            inventory.removeItem(clone)
                        }
                        inventory.addItem(item.to.item)
                    })
                }
                contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
            }
        }).build()

    init {
        B.regConsumerCommand({ player, _ -> menu.open(player) }, "craft", "")
    }
}