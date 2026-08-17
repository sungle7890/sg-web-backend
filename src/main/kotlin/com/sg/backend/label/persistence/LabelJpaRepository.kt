package com.sg.backend.label.persistence

import org.springframework.data.jpa.repository.JpaRepository

/**
 * Spring Data JPA repository. Add query methods here.
 */
interface LabelJpaRepository : JpaRepository<LabelEntity, Long>
