package me.reidj.forest.user

import clepto.bukkit.B
import dev.implario.kensuke.KensukeSession
import dev.implario.kensuke.impl.bukkit.IBukkitKensukeUser
import me.reidj.forest.app
import me.reidj.forest.channel.ModHelper
import me.reidj.forest.channel.ModTransfer
import me.reidj.forest.item.Item
import me.reidj.forest.item.ItemList
import net.minecraft.server.v1_12_R1.Packet
import net.minecraft.server.v1_12_R1.PlayerConnection
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.metadata.FixedMetadataValue
import java.util.*
import java.util.function.Consumer
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object LevelHelper {
    fun exp2level(exp: Int): Int {
        return when (exp) {
            0 -> 0
            in 1..4 -> 1
            in 5..99 -> 2
            in 100..999 -> 3
            in 1000..4999 -> 4
            in 5000..999999 -> 5
            else -> 6
        }
    }

    fun level2exp(level: Int): Int {
        return when (level) {
            0 -> 0
            1 -> 5
            2 -> 100
            3 -> 1000
            4 -> 5000
            5 -> 100000
            else -> 10000000
        }
    }
}

const val MAX_TEMPERATURE = 41.2
const val AVR_TEMPERATURE = 36.6
const val MIN_TEMPERATURE = 29.0
const val CRITICAL_MIN_TEMPERATURE = 31.0
const val CRITICAL_MAX_TEMPERATURE = 39.0

class User(session: KensukeSession, stat: Stat?) : IBukkitKensukeUser {

    private var connection: PlayerConnection? = null
    var level = 0
    lateinit var tentInventory: Inventory
    var tent: ArmorStand? = null

    var stat: Stat
    private var player: Player? = null
    override fun setPlayer(p0: Player?) {
        if (p0 != null) {
            player = p0
        }
    }

    private var session: KensukeSession
    override fun getSession() = session

    override fun getPlayer() = player

    init {
        if (stat == null) {
            this.stat = Stat(
                UUID.fromString(session.userId),
                false,
                20.0,
                0,
                0,
                0,
                0,
                0,
                36.6,
                3,
                0,
                null,
                null,
                mutableListOf(),
                mutableListOf(),
                mutableListOf()
            )
        } else {
            if (stat.tentInventory == null)
                stat.tentInventory = mutableListOf()
            this.stat = stat
        }
        this.session = session

        level = LevelHelper.exp2level(this.stat.exp)

        // Выдача вещей игроку
        B.postpone(5) {
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
            if (nms.tag != null && nms.tag.hasKey("code"))
                items.add(Item(ItemList.valueOf(nms.tag.getString("code")), nms.asBukkitMirror().getAmount(), slot))
        }
    }

    fun watchTutorial(): Boolean {
        return stat.tutorial
    }

    fun sendPacket(packet: Packet<*>) {
        if (connection == null)
            connection = (player as CraftPlayer).handle.playerConnection
        connection?.sendPacket(packet)
    }

    private fun putItem(inventory: MutableList<Item>, toPut: Inventory) {
        inventory.forEach {
            val node = it.itemList.item.clone()
            node.setAmount(it.amount)
            toPut.setItem(it.slot, node)
        }
    }

    fun giveExperience(exp: Int) {
        stat.exp += exp
        val currentLevel = LevelHelper.exp2level(stat.exp)

        ModTransfer()
            .integer(currentLevel)
            .integer(LevelHelper.level2exp(currentLevel) - (LevelHelper.level2exp(currentLevel) - stat.exp))
            .integer(LevelHelper.level2exp(currentLevel))
            .send("exp-level", this)

        if (currentLevel != level) {
            level = currentLevel
            if (level < 1)
                return
            ModHelper.banner(this, currentLevel.toString(), "Получен $level уровень")
        }
    }

    fun hasLevel(level: Int): Boolean {
        return level <= this.level
    }

    fun changeTemperature(dx: Double) {
        stat.temperature += dx

        val temperature = stat.temperature

        if (abs(temperature - AVR_TEMPERATURE) < 0.05)
            return

        if (temperature < CRITICAL_MIN_TEMPERATURE)
            player?.damage(0.06)
        if (temperature > CRITICAL_MAX_TEMPERATURE)
            player?.damage(0.07)

        ModHelper.updateTemperature(this)

        if (temperature < MIN_TEMPERATURE || temperature > MAX_TEMPERATURE) {
            stat.temperature = min(max(MIN_TEMPERATURE, temperature), MAX_TEMPERATURE)
            return
        }
    }

    fun normalizeTemperature(step: Double) {
        val temperature = stat.temperature

        if (temperature < AVR_TEMPERATURE)
            changeTemperature(step)
        else if (temperature > AVR_TEMPERATURE)
            changeTemperature(-step)
    }

    fun ifTent(consumer: Consumer<Location>) {
        val tent = stat.place
        if (tent != null)
            consumer.accept(Location(app.getWorld(), tent.x, tent.y + 2, tent.z))
    }

    fun spawn() {
        if (watchTutorial()) {
            val exit = stat.exit
            if (exit != null)
                player?.teleport(Location(app.getWorld(), exit.x, exit.y, exit.z))
            else
                player?.teleport(app.spawn)
        }
    }

    fun showTent(location: Location) {
        val spawnLocation = location.clone().add(0.0, 2.0, 0.0)
        spawnLocation.yaw = (Math.random() * 180).toFloat()
        tent = location.world.spawnEntity(spawnLocation, EntityType.ARMOR_STAND) as ArmorStand
        tent!!.setMetadata("owner", FixedMetadataValue(app, stat.uuid.toString()))
        tent!!.helmet = ItemList.valueOf("TENT" + stat.placeLevel).item
        tent!!.isVisible = false
    }
}