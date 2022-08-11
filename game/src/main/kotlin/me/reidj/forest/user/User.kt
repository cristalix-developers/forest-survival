package me.reidj.forest.user

import me.func.mod.util.after
import me.reidj.forest.app
import me.reidj.forest.channel.ModHelper
import me.reidj.forest.channel.item.ItemList
import me.reidj.forest.data.Item
import me.reidj.forest.data.Knowledge
import me.reidj.forest.data.Stat
import me.reidj.forest.user.prepare.TutorialLoader
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.metadata.FixedMetadataValue
import java.util.function.Consumer
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

const val MAX_TEMPERATURE = 41.2
const val AVR_TEMPERATURE = 36.6
const val MIN_TEMPERATURE = 29.0
const val CRITICAL_MIN_TEMPERATURE = 31.0
const val CRITICAL_MAX_TEMPERATURE = 39.0

class User(stat: Stat) {

    var stat: Stat

    lateinit var tentInventory: Inventory
    lateinit var inventory: Inventory

    var level = 0
    var tent: ArmorStand? = null

    var player: Player? = null

    init {
        this.stat = stat

        // Выдача вещей игроку
        after(5) {
            val placeLevel = this.stat.placeLevel

            tentInventory = Bukkit.createInventory(player, placeLevel * 9, "Палатка $placeLevel УР.")

            putItem(this.stat.tentInventory, tentInventory)
            putItem(this.stat.playerInventory, player!!.inventory)

            ifTent { showTent(it) }
        }
    }

    fun saveInventory(items: MutableList<Item>, inventory: Inventory) {
        for (slot in 0 until inventory.size) {
            val nms = CraftItemStack.asNMSCopy(inventory.getItem(slot))
            if (nms.tag != null && nms.tag.hasKeyOfType("code", 8))
                items.add(
                    Item(
                        me.reidj.forest.data.ItemList.valueOf(nms.tag.getString("code")),
                        nms.asBukkitMirror().getAmount(),
                        slot
                    )
                )
        }
    }

    fun watchTutorial() = stat.tutorial

    fun knowledgeTryGive(knowledge: Knowledge) {
        if (stat.knowledge.contains(knowledge))
            return
        ModHelper.banner(this, knowledge.picture, knowledge.message)
        stat.knowledge.add(knowledge)
    }

    private fun putItem(inventory: MutableList<Item>, toPut: Inventory) = inventory.forEach {
        val node = ItemList.valueOf(it.itemList.name).item.clone()
        node.setAmount(it.amount)
        toPut.setItem(it.slot, node)
    }

    fun hasLevel(level: Int) = level <= this.level

    fun changeTemperature(dx: Double) {
        stat.temperature += dx

        val temperature = stat.temperature

        if (abs(temperature - AVR_TEMPERATURE) < 0.05)
            return

        player?.damage(if (temperature < CRITICAL_MIN_TEMPERATURE && temperature < CRITICAL_MAX_TEMPERATURE) 0.06 else 0.07)

        ModHelper.updateTemperature(this)

        if (temperature < MIN_TEMPERATURE || temperature > MAX_TEMPERATURE) {
            stat.temperature = min(max(MIN_TEMPERATURE, temperature), MAX_TEMPERATURE)
            return
        }
    }

    fun normalizeTemperature(step: Double) = changeTemperature(if (stat.temperature < AVR_TEMPERATURE) step else -step)

    fun ifTent(consumer: Consumer<Location>) =
        stat.place?.let { consumer.accept(Location(app.getWorld(), it.x, it.y + 2, it.z)) }

    fun spawn() {
        if (watchTutorial()) {
            val exit = stat.exit
            player?.teleport(if (exit == null) app.spawn else Location(app.getWorld(), exit.x, exit.y, exit.z))
        } else {
            TutorialLoader.execute(this)
        }
    }

    fun showTent(location: Location) {
        location.clone().add(0.0, 2.0, 0.0).run {
            yaw = (Math.random() * 180).toFloat()
            (world.spawnEntity(this, EntityType.ARMOR_STAND) as ArmorStand).run {
                setMetadata("owner", FixedMetadataValue(app, stat.uuid.toString()))
                helmet = ItemList.valueOf("TENT" + stat.placeLevel).item
                isVisible = false
                tent = this
            }
        }
    }
}