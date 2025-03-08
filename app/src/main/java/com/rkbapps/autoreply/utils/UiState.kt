package com.rkbapps.autoreply.utils

data class UiState<T> (
    val data: T? = null,
    val message: String? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false
)