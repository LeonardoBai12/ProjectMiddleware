package io.lb.mongo

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

object DatabaseClient {
    private val client = client()
    val database: MongoDatabase = client.database()

    internal fun close() {
        client.close()
    }

    private fun client(): MongoClient {
        val properties = Properties()
        val fileInputStream = FileInputStream("local.properties")
        properties.load(fileInputStream)

        val connection = properties.getProperty("mongodb.connection")
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
