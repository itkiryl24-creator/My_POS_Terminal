package com.example.myposterminal.data.repository

import android.content.Context
import com.example.myposterminal.data.api.PosApiService
import com.example.myposterminal.data.formatTimestamp
import com.example.myposterminal.data.storage.Config
import com.example.myposterminal.data.storage.TransactionCounter
import com.example.myposterminal.domain.model.PosResult
import com.example.myposterminal.domain.repository.PosRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withTimeout
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class PosRepositoryImpl @Inject constructor(
    private val apiService: PosApiService,
    @param:ApplicationContext private val context: Context
) : PosRepository {
    override suspend fun sendTransaction(data: ByteArray): PosResult {
        val maxRetries = 2
        val mediaType = "application/octet-stream".toMediaTypeOrNull()
        val requestBody = data.toRequestBody(mediaType)
        repeat(maxRetries) { attempt ->
            try {
                val response = withTimeout(3000) {
                    apiService.sendTransaction(requestBody)
                }

                if (response.isSuccessful) {
                    val body = response.body()
                    val status =
                        if (body?.status.equals("APPROVED", true)) "APPROVED" else "DECLINED"
                    val authCode = body?.authCode
                    val timestamp = body?.timestamp ?: System.currentTimeMillis()
                    val txNumber = TransactionCounter.increment(context)
                    if (txNumber == 10) {
                        requestNewHmacKey()
                    }

                    return PosResult.Success(
                        message = "Status: $status, AuthCode: ${authCode ?: "N/A"}, Timestamp: ${
                            formatTimestamp(
                                timestamp
                            )
                        }"
                    )
                } else {
                    return PosResult.Error("HTTP error ${response.code()}")
                }

            } catch (e: Exception) {
                if (attempt == maxRetries) {
                    return PosResult.Error("Transaction failed after ${attempt + 1} attempts: ${e.localizedMessage}")
                }
            }
        }
        return PosResult.Error("Unexpected error")
    }

    override suspend fun getMerchantId(): String {
        return Config.getMerchantId(context)
    }

    override suspend fun getHmacKey(): ByteArray {
        return Config.getHmacKey(context)
    }

    override suspend fun getServerPublicKey(): String {
        return Config.getServerPublicKey(context)
    }


    suspend fun requestNewHmacKey() {
        val header = ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .apply {
                put(0x01)
                put(0x02)
                putShort(0)
            }.array()

        val mediaType = "application/octet-stream".toMediaTypeOrNull()
        val requestBody = header.toRequestBody(mediaType)

        val response = apiService.requestNewHmacKey(requestBody)

        Config.updateHmacKey(context, response.body()!!.hmacKey)
        TransactionCounter.reset(context)
    }
}
