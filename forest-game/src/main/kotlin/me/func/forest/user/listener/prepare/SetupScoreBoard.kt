package me.func.forest.user.listener.prepare

import clepto.cristalix.Cristalix
import me.func.forest.user.User
import org.bukkit.Bukkit
import kotlin.math.max

class SetupScoreBoard : PrepareUser {
    override fun execute(user: User) {
        val objective = Cristalix.scoreboardService().getPlayerObjective(user.uuid, user.uuid.toString())

        objective.displayName = "Выжить"

        objective.startGroup("Игрок")
            .record("Уровень") { "§b${user.level} §fур." }
            .record("Жизней") {
                "§c${"❤".repeat(max(1, user.stat!!.heart))}§7${
                    "❤".repeat(
                        max(
                            0,
                            user.stat!!.maxHeart - user.stat!!.heart
                        )
                    )
                }"
            }
            .record("Часов") { "§a${(user.stat!!.timeAlive / 1000 / 3600).toInt()}" }
            .record("У/С") { "§c${user.stat!!.kills}§f/${user.stat!!.deaths}" }
            .record("Жерт") { "${user.stat!!.killMobs}" }

        objective.startGroup("Мир")
            .record("Онлайн") { "§b${Bukkit.getOnlinePlayers().size}" }

        Cristalix.scoreboardService().setCurrentObjective(user.uuid, user.uuid.toString())
    }
}