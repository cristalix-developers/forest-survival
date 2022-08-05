package protocol

import ru.cristalix.core.network.CorePackage
import stat.Stat
import java.util.*

/**
 * @project : forest
 * @author : Рейдж
 **/
data class StatPackage(var uuid: UUID): CorePackage() {
    var stat: Stat? = null
}
