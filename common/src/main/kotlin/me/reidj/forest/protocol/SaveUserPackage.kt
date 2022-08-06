package me.reidj.forest.protocol

import ru.cristalix.core.network.CorePackage
import me.reidj.forest.data.Stat
import java.util.*

/**
 * @project : forest
 * @author : Рейдж
 **/
data class SaveUserPackage(var uuid: UUID, var stat: Stat): CorePackage()
