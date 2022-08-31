package me.reidj.forest

import me.reidj.forest.protocol.BulkSaveUserPackage
import me.reidj.forest.protocol.SaveUserPackage
import me.reidj.forest.protocol.StatPackage
import ru.cristalix.core.CoreApi
import ru.cristalix.core.microservice.MicroServicePlatform
import ru.cristalix.core.microservice.MicroserviceBootstrap
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.permissions.IPermissionService
import ru.cristalix.core.permissions.PermissionService

/**
 * @project : forest
 * @author : Рейдж
 **/

fun main() {
    MicroserviceBootstrap.bootstrap(MicroServicePlatform(4))

    val mongoAdapter = MongoAdapter(System.getenv("db_url"), System.getenv("db_data"), "data")

    val clientSocket = ISocketClient.get()

    clientSocket.capabilities(StatPackage::class, SaveUserPackage::class)

    CoreApi.get().registerService(IPermissionService::class.java, PermissionService(clientSocket))

    clientSocket.addListener(StatPackage::class.java) { realm, pckg ->
        println("Received UserInfoPackage from ${realm.realmName} for ${pckg.uuid}")
        mongoAdapter.find(pckg.uuid).thenAccept {
            pckg.stat = it
            clientSocket.forward(realm, pckg)
        }
    }
    clientSocket.addListener(SaveUserPackage::class.java) { realm, pckg ->
        pckg.stat.run {
            println("Received SaveUserPackage from ${realm.realmName} for $this")
            mongoAdapter.save(this)
        }
    }
    clientSocket.addListener(BulkSaveUserPackage::class.java) { realm, pckg ->
        println("Received BulkSaveUserPackage from ${realm.realmName}")
        mongoAdapter.save(pckg.packages.map { it.stat })
    }
}