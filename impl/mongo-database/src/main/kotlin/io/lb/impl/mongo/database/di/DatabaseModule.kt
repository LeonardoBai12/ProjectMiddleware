package io.lb.impl.mongo.database.di

import io.lb.common.data.service.DatabaseService
import io.lb.impl.mongo.database.DatabaseClient
import io.lb.impl.mongo.database.service.DatabaseServiceImpl

private val database = DatabaseClient.client(false)
fun provideDatabaseService(): DatabaseService = DatabaseServiceImpl(database)
