package me.reidj.forest.user.listener.prepare

import clepto.cristalix.Cristalix
import me.reidj.forest.user.User
import org.bukkit.Bukkit

object SetupScoreBoard : PrepareUser {
    override fun execute(user: User) {
        val objective = Cristalix.scoreboardService().getPlayerObjective(user.stat.uuid, user.stat.uuid.toString())

        objective.displayName = "Тайга"

        objective.startGroup("Игрок")
            .record("Уровень") { "§b${user.level} §fур." }
            .record("У/С") { "§c${user.stat.kills}§f/${user.stat.deaths}" }
            .record("Жертв") { "${user.stat.killMobs}" }
        objective.startGroup("Мир")
            .record("Онлайн") { "§b${Bukkit.getOnlinePlayers().size}" }

        Cristalix.scoreboardService().setCurrentObjective(user.stat.uuid, user.stat.uuid.toString())
    }
}