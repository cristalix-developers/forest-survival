package me.func.forest.drop

import me.func.forest.app
import me.func.forest.drop.booty.BonfireBooty
import me.func.forest.drop.booty.Booty
import me.func.forest.drop.booty.DropThenGenerate
import me.func.forest.drop.booty.ReplaceThenGenerate
import me.func.forest.drop.dropper.DropItem
import me.func.forest.drop.dropper.Dropper
import me.func.forest.drop.dropper.RandomItemDrop
import me.func.forest.drop.generator.BonfireGenerator
import me.func.forest.drop.generator.DelayGenerator
import me.func.forest.drop.generator.Generator
import me.func.forest.item.ItemList
import me.func.forest.item.ItemList.*
import org.bukkit.Location
import org.bukkit.entity.Player

enum class Resources(
    val title: String,
    val exp: Int,
    val item: ItemList,
    private val dropper: Dropper,
    val generator: Generator,
    private val booty: Booty
) {
    STONE(
        "Камень", 1, STONE1,
        RandomItemDrop(1, FLINT1),
        DelayGenerator(STONE2, 10), DropThenGenerate
    ),
    BUSH(
        "Куст", 1, APPLE1,
        RandomItemDrop(3, APPLE1, STICK1, HEAL1, STRING1),
        DelayGenerator(FULL_BUSH2, 20), ReplaceThenGenerate(EMPTY_BUSH2)
    ),
    GRIB(
        "Гриб", 2, MUSHROOM2,
        DropItem, DelayGenerator(MUSHROOM2, 20), DropThenGenerate
    ),
    TOXIC(
        "Мухомор", 2, RED_MUSHROOM2,
        DropItem, DelayGenerator(RED_MUSHROOM2, 20), DropThenGenerate
    ),
    SHELL(
        "Ракушка", 4, SHELL2,
        DropItem, DelayGenerator(SHELL2, 20), DropThenGenerate
    ),
    TOTEM(
        "Тотем", 7, SKULL1,
        RandomItemDrop(3, STICK1),
        DelayGenerator(TOTEM2, 20), DropThenGenerate
    ),
    FIRE(
        "Костер", 0, BONFIRE_OFF2,
        DropItem, BonfireGenerator, BonfireBooty
    ), ;

    fun booty(location: Location, player: Player) {
        booty.get(this, location, app.getUser(player)!!)
    }

    fun drop(location: Location, player: Player) {
        dropper.drop(this.item, location, player)
    }

    fun generate(location: Location) {
        generator.generate(this, location)
    }

}