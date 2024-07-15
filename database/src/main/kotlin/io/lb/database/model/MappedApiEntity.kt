package io.lb.database.model

import java.util.UUID

data class MappedApiEntity(
    val uuid: UUID,
    val originalApi: OriginalApiEntity,
    val name: String,
    val routes: List<MappedApiEntity> = listOf()
)
