package me.func.forest.drop

import me.func.forest.drop.booty.Booty
import me.func.forest.drop.booty.DefaultBooty
import me.func.forest.drop.dropper.DropItem
import me.func.forest.drop.dropper.Dropper
import me.func.forest.drop.generator.DelayGenerator
import me.func.forest.drop.generator.Generator
import org.bukkit.Location
import org.bukkit.Material.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.item.Items

enum class Resources(val title: String, val item: ItemStack, private val dropper: Dropper, val generator: Generator, private val booty: Booty) {

    STONE(
        "Камень",
        Items.builder()
            .displayName("Камень")
            .type(FIREWORK_CHARGE)
            .build(),
        DropItem, DelayGenerator(Pair(CARPET, 0), 10), DefaultBooty
    ), BUSH(
        "Куст",
        Items.builder()
            .displayName("Куст")
            .type(APPLE)
            .build(),
        DropItem, DelayGenerator(Pair(CARPET, 1), 20), DefaultBooty
    ),;

    fun booty(location: Location, player: Player) {
        booty.get(this, location, player)
    }

    fun drop(location: Location, player: Player) {
        dropper.drop(this, location, player)
    }

    fun generate(location: Location) {
        generator.generate(this, location)
    }

}