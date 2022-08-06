package me.reidj.forest.protocol

import me.reidj.forest.data.Stat
import ru.cristalix.core.network.CorePackage
import java.util.*

/**
 * @project : forest
 * @author : Рейдж
 **/
data class StatPackage(var uuid: UUID): CorePackage() {
    var stat: Stat? = null
}
