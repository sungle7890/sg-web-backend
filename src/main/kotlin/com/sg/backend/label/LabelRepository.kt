package com.sg.backend.label

/**
 * Label storage abstraction.
 *
 * The service layer depends only on this interface, so the storage mechanism
 * (in-memory -> Postgres/JPA) can change without touching controller/service
 * code. (Extension point)
 */
interface LabelRepository {
    fun findAll(): List<Label>
    fun create(text: String): Label
    fun deleteById(id: Long): Boolean
}
