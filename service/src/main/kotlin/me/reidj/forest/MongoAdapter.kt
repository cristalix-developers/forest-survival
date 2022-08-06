package me.reidj.forest

import com.mongodb.ClientSessionOptions
import com.mongodb.async.client.MongoClient
import com.mongodb.async.client.MongoClients
import com.mongodb.async.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOneModel
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.WriteModel
import com.mongodb.session.ClientSession
import me.reidj.forest.data.Stat
import org.bson.Document
import ru.cristalix.core.GlobalSerializers
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * @project : forest
 * @author : Рейдж
 **/
open class MongoAdapter(dbUrl: String, dbName: String, collection: String) {

    private val upsert = UpdateOptions().upsert(true)

    private var data: MongoCollection<Document>

    private val mongoClient: MongoClient
    private val session: ClientSession

    init {
        val future = CompletableFuture<ClientSession>()
        mongoClient = MongoClients.create(dbUrl).apply {
            startSession(ClientSessionOptions.builder().causallyConsistent(true).build()) { response, throwable ->
                if (throwable != null) future.completeExceptionally(throwable) else future.complete(response)
            }
        }
        data = mongoClient.getDatabase(dbName).getCollection(collection)
        session = future.get(10, TimeUnit.SECONDS)
    }

    fun find(uuid: UUID) = CompletableFuture<Stat?>().apply {
        data.find(session, Filters.eq("uuid", uuid.toString())).first { result: Document?, _: Throwable? ->
            try {
                complete(readDocument(result))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun findAll(): CompletableFuture<Map<UUID, Stat>> {
        val future = CompletableFuture<Map<UUID, Stat>>()
        val documentFindIterable = data.find()
        val map = ConcurrentHashMap<UUID, Stat>()
        documentFindIterable.forEach({ document: Document ->
            val obj: Stat? = readDocument(document)
            if (obj != null)
                map[obj.uuid] = obj
        }) { _: Void, _: Throwable -> future.complete(map) }
        return future
    }

    private fun readDocument(document: Document?) =
        if (document == null) null else GlobalSerializers.fromJson(document.toJson(), Stat::class.java)

    fun save(stat: Stat) = save(listOf(stat))

    fun save(stats: List<Stat>) {
        mutableListOf<WriteModel<Document>>().apply {
            stats.forEach {
                add(
                    UpdateOneModel(
                        Filters.eq("uuid", it.uuid.toString()),
                        Document("\$set", Document.parse(GlobalSerializers.toJson(it))),
                        upsert
                    )
                )
            }
        }.run {
            if (isNotEmpty())
                data.bulkWrite(session, this) { _, throwable: Throwable? -> handle(throwable) }
        }
    }

    private fun handle(throwable: Throwable?) = throwable?.printStackTrace()
}