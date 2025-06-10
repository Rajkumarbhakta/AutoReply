package com.rkbapps.autoreply.utils

enum class ChooseContactType {
    INCLUDE,
    EXCLUDE;

    companion object {
        fun fromString(type: String): ChooseContactType {
            return when (type) {
                "include" -> INCLUDE
                "exclude" -> EXCLUDE
                else -> throw IllegalArgumentException("Unknown type: $type")
            }
        }
    }
}