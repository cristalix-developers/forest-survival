package protocol

import ru.cristalix.core.network.CorePackage

/**
 * @project : forest
 * @author : Рейдж
 **/
data class BulkSaveUserPackage(var packages: List<SaveUserPackage>): CorePackage()
