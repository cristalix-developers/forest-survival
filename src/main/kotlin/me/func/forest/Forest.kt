package me.func.forest

import clepto.bukkit.B
import lombok.Getter
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.CoreApi
import ru.cristalix.core.map.IMapService
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.core.stats.IStatService
import ru.cristalix.core.stats.PlayerScope
import ru.cristalix.core.stats.UserManager
import ru.cristalix.core.stats.impl.StatService
import ru.cristalix.core.stats.impl.network.StatServiceConnectionData

class Forest : JavaPlugin(), Listener {

    private val statScope = PlayerScope("forest", Stat::class.java)

    @Getter
    lateinit var instance: Forest
    lateinit var userManager: UserManager<User>

    override fun onEnable() {
        instance = this
        B.plugin = instance

        B.events(this)

        val info = IRealmService.get().currentRealmInfo
        info.status = RealmStatus.GAME_STARTED_CAN_JOIN
        info.readableName = "Лес"
        info.groupName = "Лес"

        val core = CoreApi.get()
        val statService = StatService(core.platformServer, StatServiceConnectionData.fromEnvironment())
        core.registerService(IStatService::class.java, statService)

        statService.useScopes(statScope)

        userManager = statService.registerUserManager(
            { context -> User(context.uuid, context.name, context.getData(statScope)) },
            { user: User, context -> context.store(statScope, user.stat) }
        )
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.sendMessage("Hello")
    }
}