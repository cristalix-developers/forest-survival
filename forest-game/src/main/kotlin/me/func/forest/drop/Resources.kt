package me.func.forest.drop

import me.func.forest.drop.booty.Booty
import me.func.forest.drop.booty.DropThenGenerate
import me.func.forest.drop.booty.ReplaceThenGenerate
import me.func.forest.drop.dropper.DropItem
import me.func.forest.drop.dropper.Dropper
import me.func.forest.drop.dropper.RandomItemDrop
import me.func.forest.drop.generator.DelayGenerator
import me.func.forest.drop.generator.Generator
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Material.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.formatting.Color.GREEN
import ru.cristalix.core.item.Items.builder

enum class Resources(
    val title: String,
    val item: ItemStack,
    private val dropper: Dropper,
    val generator: Generator<Pair<Material, Byte>>,
    private val booty: Booty
) {

    STONE(
        "Камень",
        builder()
            .displayName("Камень")
            .type(FIREWORK_CHARGE)
            .build(),
        RandomItemDrop(1,
            builder().displayName("Кремень").type(FLINT).build(),
        ), DelayGenerator(Pair(CARPET, 0), 10), DropThenGenerate
    ),
    BUSH(
        "Куст",
        builder()
            .displayName("Плод")
            .type(APPLE)
            .build(),
        RandomItemDrop( 3,
            builder().displayName("Плод").type(APPLE).build(),
            builder().displayName("Палка").type(STICK).build(),
            builder().displayName("Целебная трава").type(INK_SACK).color(GREEN).build(),
            builder().displayName("Нить").type(STRING).build(),
        ), DelayGenerator(Pair(CARPET, 1), 20), ReplaceThenGenerate(Pair(CARPET, 6))
    ),
    GRIB(
        "Гриб",
        builder()
            .displayName("Гриб")
            .type(BROWN_MUSHROOM)
            .build(),
        DropItem, DelayGenerator(BROWN_MUSHROOM, 20), DropThenGenerate
    ),
    TOXIC(
        "Мухомор",
        builder()
            .displayName("Мухомор")
            .type(RED_MUSHROOM)
            .build(),
        DropItem, DelayGenerator(RED_MUSHROOM, 20), DropThenGenerate
    ),
    SHELL(
        "Ракушка",
        builder()
            .displayName("Ракушка")
            .type(APPLE)
            .build(),
        DropItem, DelayGenerator(Pair(CARPET, 10), 20), DropThenGenerate
    ),
    TOTEM(
        "Тотем",
        builder()
            .displayName("Череп")
            .type(BONE)
            .build(),
        RandomItemDrop(3,
            builder().displayName("Палка").type(STICK).amount(2).build(),
        ), DelayGenerator(Pair(CARPET, 4), 20), DropThenGenerate
    ), ;

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