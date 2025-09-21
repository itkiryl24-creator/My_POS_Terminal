package com.example.myposterminal.data.model

data class TransactionResponse(
    val status: String,
    val authCode: String?,
    val timestamp: Long
)
