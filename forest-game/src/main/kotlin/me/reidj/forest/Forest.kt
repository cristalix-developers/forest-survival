package me.reidj.forest

import clepto.bukkit.B
import clepto.cristalix.WorldMeta
import dev.implario.bukkit.platform.Platforms
import dev.implario.kensuke.KensukeSession
import dev.implario.kensuke.Scope
import dev.implario.kensuke.impl.bukkit.BukkitKensuke
import dev.implario.kensuke.impl.bukkit.BukkitUserManager
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import me.reidj.forest.clock.ClockInject
import me.reidj.forest.clock.GameTimer
import me.reidj.forest.craft.CraftManager
import me.reidj.forest.drop.ResourceManager
import me.reidj.forest.item.ItemList
import me.reidj.forest.item.ItemManager
import me.reidj.forest.user.Stat
import me.reidj.forest.user.User
import me.reidj.forest.user.WaterManager
import me.reidj.forest.user.listener.CancelEvents
import me.reidj.forest.user.listener.PlayerListener
import me.reidj.forest.weather.ZoneManager
import net.minecraft.server.v1_12_R1.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.CoreApi
import ru.cristalix.core.inventory.IInventoryService
import ru.cristalix.core.inventory.InventoryService
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.npcs.server.Npcs
import java.util.*


lateinit var app: Forest

class Forest : JavaPlugin() {

    private val statScope = Scope("forestt", Stat::class.java)
    private var userManager = BukkitUserManager(
        listOf(statScope),
        { session: KensukeSession, context -> User(session, context.getData(statScope)) },
        { user, context ->
            run {
                user.ifTent {
                    user.stat.placeInventory?.clear()
                    val items = mutableMapOf<ItemList, Int>()

                    user.homeInventory.forEach {
                        val nms = CraftItemStack.asNMSCopy(it)
                        if (nms.tag != null && nms.tag.hasKey("code")) {
                            val type = ItemList.valueOf(nms.tag.getString("code"))
                            if (items[type] != null)
                                items.replace(type, items[type]?.plus(it.amount) ?: 0)
                            else
                                items[type] = it.amount
                        }
                    }

                    user.stat.placeInventory = items.toList().toMutableList()
                    user.tent?.remove()
                }

                val dot = user.player!!.location

                user.stat.exit = ru.cristalix.core.math.V3(dot.x, dot.y, dot.z)

                user.stat.timeAlive += Date().time - user.stat.lastEntry
                context.store(statScope, user.stat)
            }
        }
    )

    lateinit var worldMeta: WorldMeta

    lateinit var spawn: Location
    lateinit var start: Location

    override fun onEnable() {
        B.plugin = this
        app = this
        Platforms.set(PlatformDarkPaper())
        Npcs.init(this)

        // Загрузка карты
        worldMeta = MapLoader().load("prod")!!
        spawn = worldMeta.getLabel("guide_end")
        start = worldMeta.getLabel("guide_pre")

        // Конфигурация реалма
        val info = IRealmService.get().currentRealmInfo
        info.status = RealmStatus.GAME_STARTED_CAN_JOIN
        info.readableName = "Лес"
        info.groupName = "Лес"

        // Регистрация сервисов
        val core = CoreApi.get()

        CoreApi.get().registerService(IInventoryService::class.java, InventoryService())

        // Подключение к сервису статистики
        val kensuke = BukkitKensuke.setup(app)
        kensuke.addGlobalUserManager(userManager)
        kensuke.globalRealm = IRealmService.get().currentRealmInfo.realmId.realmName
        userManager.isOptional = true

        B.regCommand({ player, _ ->
            val mob = player.world.spawnEntity(player.location, EntityType.ZOMBIE)
            mob.customName = "1"
            mob.isCustomNameVisible = true
            null
        }, "f", "")

        B.regCommand({ player, args ->
            getUser(player)?.giveExperience(args[0].toInt())
            null
        }, "exp", "")

        // Регистрация меню крафтов
        CraftManager()

        // Регистрация палаток
        TentManipulator()

        // Регистрация обработчиков событий
        B.events(PlayerListener(), CancelEvents(), ItemManager(), ResourceManager())

        // Начало игрового времени и добавление временных собитий
        GameTimer(listOf(ZoneManager(), WaterManager, object : ClockInject {
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

    fun getUser(player: Player): User? {
        return userManager.getUser(player)
    }

    fun getWorld(): World {
        return worldMeta.world
    }

    fun getNMSWorld(): net.minecraft.server.v1_12_R1.World {
        return worldMeta.world.handle
    }
}