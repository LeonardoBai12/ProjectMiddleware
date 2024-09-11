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

/**
 * Object class representing a database client.
 */
internal object DatabaseClient {
    private val client = client()

    /**
     * The MongoDB database.
     */
    val database: MongoDatabase = client.database()

    /**
     * Function to close the database client.
     */
    internal fun close() {
        client.close()
    }

    private fun client(): MongoClient {
        val connection: String = System.getenv("MONGODB_CONNECTION")
        val serverApi = ServerApi.builder()
            .version(ServerApiVersion.V1)
            .build()
        val mongoClientSettings = MongoClientSettings.builder()
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .applyConnectionString(ConnectionString(connection))
            .serverApi(serverApi)
            .build()

        return MongoClient.create(mongoClientSettings)
    }

    private fun MongoClient.database(): MongoDatabase {
        val database = getDatabase("LB12")

        database.run {
            runBlocking {
                runCommand(Document("ping", 1))
            }
            println("Pinged your deployment. You successfully connected to MongoDB!")
        }

        return database
    }
}
