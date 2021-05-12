package me.func.forest.craft

import clepto.bukkit.B
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
            override fun init(player: Player?, contents: InventoryContents?) {
                contents?.setLayout(
                    "XXXXXXXXX",
                    "XXXXXXXXX",
                    "XXXXXXXXX"
                )

                CraftItem.values().forEach { item ->

                    val itemWithLore = item.to.item.clone()

                    val lore = mutableListOf("")
                    lore.addAll(item.from.map { "x${it.second} ${it.first.item.itemMeta.displayName}" })

                    itemWithLore.itemMeta.lore = lore

                    contents?.add('X', ClickableItem.of(itemWithLore) {
                        player?.inventory!!.addItem(item.to.item)
                    })
                }
                contents?.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
            }
        }).build()

    init {
        B.regConsumerCommand({ player, _ -> menu.open(player) }, "craft", "")
    }
}