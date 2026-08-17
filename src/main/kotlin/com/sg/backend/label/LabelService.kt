package com.sg.backend.label

import com.sg.backend.common.NotFoundException
import org.springframework.stereotype.Service

/**
 * Label use cases. Business rules such as validation live here.
 */
@Service
class LabelService(
    private val repository: LabelRepository,
) {
    fun list(): List<Label> = repository.findAll()

    fun create(rawText: String): Label {
        val text = rawText.trim()
        require(text.isNotEmpty()) { "라벨 내용은 비어 있을 수 없습니다." }
        require(text.length <= MAX_LENGTH) { "라벨 내용은 최대 ${MAX_LENGTH}자입니다." }
        return repository.create(text)
    }

    fun delete(id: Long) {
        if (!repository.deleteById(id)) {
            throw NotFoundException("라벨(id=$id)을 찾을 수 없습니다.")
        }
    }

    companion object {
        const val MAX_LENGTH = 200
    }
}
