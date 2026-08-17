package com.sg.backend.label.persistence

import com.sg.backend.label.Label
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

/**
 * Label JPA entity (table: labels).
 * Kept separate from the domain model (Label) to confine persistence concerns
 * to the persistence package.
 */
@Entity
@Table(name = "labels")
class LabelEntity(
    @Column(nullable = false, length = 200)
    var text: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    /** Convert entity -> domain model */
    fun toDomain(): Label = Label(
        id = requireNotNull(id) { "persisted entity must have an id" },
        text = text,
        createdAt = createdAt,
    )
}
