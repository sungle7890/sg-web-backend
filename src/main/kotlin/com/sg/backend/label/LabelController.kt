package com.sg.backend.label

import com.sg.backend.label.dto.CreateLabelRequest
import com.sg.backend.label.dto.LabelResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Label REST API.
 *   GET    /api/labels        list all
 *   POST   /api/labels        create
 *   DELETE /api/labels/{id}   delete
 */
@RestController
@RequestMapping("/api/labels")
class LabelController(
    private val service: LabelService,
) {
    @GetMapping
    fun list(): List<LabelResponse> =
        service.list().map(LabelResponse::from)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateLabelRequest): LabelResponse =
        LabelResponse.from(service.create(request.text))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
