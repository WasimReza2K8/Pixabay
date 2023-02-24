package com.example.core.exception

sealed class Exceptions(message: String) : RuntimeException(message) {
    object PhotoDetailNotFoundException : Exceptions("Photo detail not found")
}
