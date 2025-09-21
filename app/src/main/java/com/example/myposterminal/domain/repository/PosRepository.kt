package com.example.myposterminal.domain.repository

import com.example.myposterminal.domain.model.PosResult

interface PosRepository {
    suspend fun sendTransaction(data: ByteArray): PosResult

    suspend fun getMerchantId(): String

    suspend fun getHmacKey(): ByteArray

    suspend fun getServerPublicKey(): String
}