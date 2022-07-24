package me.reidj.forest.drop.block

import me.reidj.forest.app
import me.reidj.forest.channel.item.ItemList
import me.reidj.forest.channel.item.ItemList.*
import me.reidj.forest.drop.booty.BonfireBooty
import me.reidj.forest.drop.booty.Booty
import me.reidj.forest.drop.booty.DropThenGenerate
import me.reidj.forest.drop.booty.ReplaceThenGenerate
import me.reidj.forest.drop.dropper.DropItem
import me.reidj.forest.drop.dropper.Dropper
import me.reidj.forest.drop.dropper.RandomItemDrop
import me.reidj.forest.drop.generator.BonfireGenerator
import me.reidj.forest.drop.generator.DelayGenerator
import me.reidj.forest.drop.generator.Generator
import org.bukkit.Location
import org.bukkit.entity.Player

enum class BlockUnit (
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
        DelayGenerator(STONE2, 20), DropThenGenerate
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
        DropItem, DelayGenerator(RED_MUSHROOM2, 60), DropThenGenerate
    ),
    SHELL(
        "Ракушка", 4, SHELL2,
        DropItem, DelayGenerator(SHELL2, 300), DropThenGenerate
    ),
    IRON(
        "Железо", 5, IRON2,
        RandomItemDrop(2, IRON2),
        DelayGenerator(IRON2, 20), DropThenGenerate
    ),
    TOTEM(
        "Тотем", 7, SKULL1,
        RandomItemDrop(3, STICK1),
        DelayGenerator(TOTEM2, 300), DropThenGenerate
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