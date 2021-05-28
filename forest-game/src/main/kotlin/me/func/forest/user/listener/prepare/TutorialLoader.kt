package me.func.forest.user.listener.prepare

import clepto.bukkit.B
import me.func.forest.UrlSkinData
import me.func.forest.app
import me.func.forest.user.User
import org.bukkit.Bukkit
import ru.cristalix.npcs.data.NpcBehaviour
import ru.cristalix.npcs.server.Npc
import ru.cristalix.npcs.server.Npcs
import java.util.*

class TutorialLoader : PrepareUser {

    init {
        // При завершении туториала
        Bukkit.getMessenger().registerIncomingPluginChannel(app, "guide-end") { _, player, _ ->
            player.teleport(app.spawn)
            player.health = 10.0
            player.saturation = 5F
            val user = app.getUser(player)!!
            user.giveExperience(1)
            user.stat!!.tutorial = true
        }

        val faelan = UrlSkinData(UUID.fromString("6f3f4a2e-7f84-11e9-8374-1cb72caa35fd"))

        app.worldMeta.getLabels("npc").forEach { label ->
            Npcs.spawn(
                Npc.builder()
                    .location(label.clone().add(0.5, 0.0, 0.5))
                    .name("Ангелина")
                    .behaviour(NpcBehaviour.STARE_AT_PLAYER)
                    .onClick {
                        val user = app.getUser(it)!!
                        if (user.delayTicks > 0)
                            return@onClick
                        user.delayTicks = 10
                        B.postpone(10) { user.delayTicks -= 10 }
                        it.sendMessage(
                            """
                [ §eАнгелина §f]
    §7    Дорогой, как же хорошо,
    §7что мы переехали в новую квартиру,
    §7она конечно не в Москва-Сити,
    §7но доделаем ремонт и всё 
    §7будет отлично.
    §7
    §7    Я приготовила тебе самый
    §7вкусный §dтортик§7, поешь его
    §7перед улетом
                           """
                        )
                    }.skinUrl(faelan.url)
                    .skinDigest(faelan.digest)
                    .build()
            )
        }
    }

    override fun execute(user: User) {
        if (!user.watchTutorial())
            user.player.teleport(app.start)
    }
}