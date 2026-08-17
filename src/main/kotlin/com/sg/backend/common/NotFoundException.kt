package com.sg.backend.common

/** Thrown when a resource cannot be found (-> HTTP 404) */
class NotFoundException(message: String) : RuntimeException(message)
