package com.example.myposterminal.data.api

import com.example.myposterminal.data.model.HmacKeyResponse
import com.example.myposterminal.data.model.TransactionResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PosApiService {

    @POST("/pos")
    suspend fun sendTransaction(
        @Body packet: RequestBody
    ): Response<TransactionResponse>

    @POST("/pos")
    suspend fun requestNewHmacKey(
        @Body header: RequestBody
    ): Response<HmacKeyResponse>
}