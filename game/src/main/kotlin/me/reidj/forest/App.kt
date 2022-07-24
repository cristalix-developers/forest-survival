package me.reidj.forest

import PlayerDataManager
import clepto.cristalix.Cristalix
import clepto.cristalix.WorldMeta
import dev.implario.bukkit.platform.Platforms
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import me.func.mod.Anime
import me.func.mod.Kit
import me.func.mod.util.command
import me.func.mod.util.listener
import me.reidj.forest.channel.item.ItemManager
import me.reidj.forest.client.ClientSocket
import me.reidj.forest.clock.ClockInject
import me.reidj.forest.clock.GameTimer
import me.reidj.forest.craft.CraftManager
import me.reidj.forest.drop.ResourceManager
import me.reidj.forest.user.User
import me.reidj.forest.user.listener.CancelEvents
import me.reidj.forest.user.listener.PlayerListener
import me.reidj.forest.weather.ZoneManager
import net.minecraft.server.v1_12_R1.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.CoreApi
import ru.cristalix.core.inventory.IInventoryService
import ru.cristalix.core.inventory.InventoryService
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.core.scoreboard.IScoreboardService
import ru.cristalix.core.scoreboard.ScoreboardService
import java.util.*

private const val PROJECT_NAME = "Лес"

lateinit var app: App
lateinit var clientSocket: ClientSocket

class App : JavaPlugin() {

    lateinit var worldMeta: WorldMeta

    lateinit var spawn: Location
    lateinit var start: Location

    val userMap = mutableMapOf<UUID, User>()

    override fun onEnable() {
        app = this

        // Подкючение к Netty сервису / Управляет конфигами, кастомными пакетами, всей data
        val forestServiceHost: String = getEnv("FOREST_SERVICE_HOST", "127.0.0.1")
        val forestServicePort: Int = getEnv("FOREST_SERVICE_PORT", "14653").toInt()
        val forestServicePassword: String = getEnv("FOREST_SERVICE_PASSWORD", "12345")

        clientSocket = ClientSocket(
            forestServiceHost,
            forestServicePort,
            forestServicePassword,
            Cristalix.getRealmString()
        )
        clientSocket.connect()

        Platforms.set(PlatformDarkPaper())

        // Регистрация Core сервисов
        CoreApi.get().run {
            registerService(IInventoryService::class.java, InventoryService())
            registerService(IScoreboardService::class.java, ScoreboardService())
        }

        Anime.include(Kit.STANDARD, Kit.NPC)

        // Загрузка карты
        worldMeta = MapLoader().load("prod")!!
        spawn = worldMeta.getLabel("guide_end")
        start = worldMeta.getLabel("guide_pre")

        // Конфигурация реалма
        val info = IRealmService.get().currentRealmInfo
        info.status = RealmStatus.WAITING_FOR_PLAYERS
        info.groupName = PROJECT_NAME
        IScoreboardService.get().serverStatusBoard.displayName = "§fЛес"

        command("exp") { player, args ->
            getUser(player)?.giveExperience(args[0].toInt())
        }

        // Регистрация меню крафтов
        CraftManager()

        // Регистрация палаток
        TentManipulator()

        // Регистрация обработчиков событий
        listener(PlayerListener(), CancelEvents(), ItemManager(), ResourceManager(), PlayerDataManager())

        // Начало игрового времени и добавление временных собитий
        GameTimer(listOf(ZoneManager(), object : ClockInject {
            override fun run() {
                Bukkit.getLogger().info("Total entities: " + getWorld().livingEntities.size)
                Bukkit.getLogger().info("Total players: " + Bukkit.getOnlinePlayers().size)
                Bukkit.getLogger()
                    .info("TPS: ${MinecraftServer.SERVER.tps1.average} ${MinecraftServer.SERVER.tps15.average}")
            }

            override fun doEvery(): Int {
                return 100
            }
        })).runTaskTimer(this, 0, 1)
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers()
            .map { getUser(it) }
            .forEach {
                val dot = it!!.player!!.location

                it.ifTent { _ ->
                    it.saveInventory(it.stat.tentInventory, it.tentInventory)
                    it.tent!!.remove()
                }
                it.saveInventory(it.stat.playerInventory, it.player!!.inventory)

                it.stat.exit = ru.cristalix.core.math.V3(dot.x, dot.y, dot.z)
            }
    }

    fun getUser(uuid: UUID): User? = userMap[uuid]

    fun getUser(player: Player): User? = getUser(player.uniqueId)

    fun getWorld(): World = worldMeta.world

    fun getNMSWorld(): net.minecraft.server.v1_12_R1.World = worldMeta.world.handle

    private fun getEnv(name: String, defaultValue: String): String {
        var field = System.getenv(name)
        if (field == null || field.isEmpty()) {
            println("No $name environment variable specified!")
            field = defaultValue
        }
        return field
    }
}