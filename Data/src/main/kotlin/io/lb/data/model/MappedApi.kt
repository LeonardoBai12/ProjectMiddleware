package io.lb.data.model

import java.util.UUID

data class MappedApi(
    val uuid: UUID = UUID.randomUUID(),
    val originalApi: OriginalApi,
    val name: String
)
