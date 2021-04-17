package me.func.forest

import ru.cristalix.core.stats.player.PlayerWrapper
import java.util.*

class User(uuid: UUID, name: String, var stat: Stat?) : PlayerWrapper(uuid, name) {
    init {
        if (stat == null) {
            stat = Stat(1.0, 1, 1.0, 1, 1.0)
        }
    }
}