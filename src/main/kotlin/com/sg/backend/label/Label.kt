package com.sg.backend.label

import java.time.Instant

/**
 * Label domain model.
 * Currently a plain data class with in-memory storage.
 * When Postgres + JPA is introduced later, promote this class to an entity
 * (@Entity) or map it to a separate entity.
 */
data class Label(
    val id: Long,
    val text: String,
    val createdAt: Instant,
)
