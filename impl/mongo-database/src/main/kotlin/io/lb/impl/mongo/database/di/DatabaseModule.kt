package io.lb.impl.mongo.database.di

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.lb.common.data.service.DatabaseService
import io.lb.impl.mongo.database.DatabaseClient
import io.lb.impl.mongo.database.service.DatabaseServiceImpl
import org.koin.dsl.module

val databaseModule = module {
    factory<MongoDatabase> { DatabaseClient.database }
    single<DatabaseService> { DatabaseServiceImpl(get()) }
}
