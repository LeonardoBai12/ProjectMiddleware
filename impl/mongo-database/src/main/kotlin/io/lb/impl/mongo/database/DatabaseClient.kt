package io.lb.impl.mongo.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.UuidRepresentation
import java.io.FileInputStream
import java.util.Properties
import kotlinx.serialization.json.Json

/**
 * Object class representing a database client.
 */
internal object DatabaseClient {
    internal val json = Json { ignoreUnknownKeys = true }

    fun client(embedded: Boolean): MongoClient {
        val connection = if (embedded) {
            val properties = Properties()
            val fileInputStream = FileInputStream("local.properties")
            properties.load(fileInputStream)
            properties.getProperty("mongodb.connection")
        } else {
            System.getenv("MONGODB_CONNECTION")
        }
        val serverApi = ServerApi.builder()
            .version(ServerApiVersion.V1)
            .build()
        val mongoClientSettings = MongoClientSettings.builder()
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .applyConnectionString(ConnectionString(connection))
            .applyToSslSettings {
                it.enabled(true)
                it.invalidHostNameAllowed(true)
            }
            .serverApi(serverApi)
            .build()

        return MongoClient.create(mongoClientSettings)
    }
}

fun MongoClient.database(): MongoDatabase {
    val database = getDatabase("Middleware")

    database.run {
        runBlocking {
            runCommand(Document("ping", 1))
        }
        println("Pinged your deployment. You successfully connected to MongoDB!")
    }

    return database
}
