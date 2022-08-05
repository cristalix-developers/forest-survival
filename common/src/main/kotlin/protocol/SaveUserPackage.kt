package protocol

import ru.cristalix.core.network.CorePackage
import stat.Stat
import java.util.*

/**
 * @project : forest
 * @author : Рейдж
 **/
data class SaveUserPackage(var uuid: UUID, var stat: Stat): CorePackage()
