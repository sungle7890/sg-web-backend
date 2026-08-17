package com.sg.backend.label.persistence

import com.sg.backend.label.Label
import com.sg.backend.label.LabelRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import java.time.Instant

/**
 * JPA implementation adapter for LabelRepository (the domain interface).
 * The service layer still depends only on LabelRepository, so controller/service
 * code stays unchanged even when the storage mechanism changes.
 * (The in-memory -> Postgres swap is fully contained here.)
 */
@Repository
class JpaLabelRepository(
    private val jpa: LabelJpaRepository,
) : LabelRepository {

    override fun findAll(): List<Label> =
        jpa.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
            .map(LabelEntity::toDomain)

    override fun create(text: String): Label =
        jpa.save(LabelEntity(text = text, createdAt = Instant.now())).toDomain()

    override fun deleteById(id: Long): Boolean {
        if (!jpa.existsById(id)) return false
        jpa.deleteById(id)
        return true
    }
}
