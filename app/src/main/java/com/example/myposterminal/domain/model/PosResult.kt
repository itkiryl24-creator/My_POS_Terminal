package com.example.myposterminal.domain.model

sealed interface PosResult {
    data class Success(val message: String): PosResult
    data class Error(val error: String) : PosResult
}