package com.sg.backend.label.dto

import com.sg.backend.label.Label
import java.time.Instant

/** Request body for creating a label */
data class CreateLabelRequest(
    val text: String = "",
)

/** Response body for a label */
data class LabelResponse(
    val id: Long,
    val text: String,
    val createdAt: Instant,
) {
    companion object {
        fun from(label: Label) = LabelResponse(
            id = label.id,
            text = label.text,
            createdAt = label.createdAt,
        )
    }
}
