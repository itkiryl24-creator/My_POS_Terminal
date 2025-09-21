package com.example.myposterminal.domain.useCase

import com.example.myposterminal.domain.model.PosResult
import com.example.myposterminal.domain.repository.PosRepository
import com.example.myposterminal.domain.utils.computeHmacSHA256
import com.example.myposterminal.domain.utils.encryptAesGcm
import com.example.myposterminal.domain.utils.encryptAesKeyWithRsa
import com.example.myposterminal.domain.utils.generateAes256Key
import com.example.myposterminal.domain.utils.getServerPublicKey
import com.example.myposterminal.domain.utils.serializeTransactionTLV
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class TransferUseCase @Inject constructor(
    private val repository: PosRepository,
) {
    suspend operator fun invoke(coins: Int): PosResult {
        val tLVData = serializeTransactionTLV(coins, repository.getMerchantId())

        val hmacKey = repository.getHmacKey()

        val hmac = computeHmacSHA256(hmacKey, tLVData)

        val aes256Key = generateAes256Key()

        val (iv, encryptedTLVData) = encryptAesGcm(aes256Key, tLVData)

        val encryptedAesKey = encryptAesKeyWithRsa(aes256Key, getServerPublicKey(repository.getServerPublicKey()))

        val bodyWithoutHeader = ByteBuffer.allocate(
            encryptedAesKey.size + iv.size + hmac.size + encryptedTLVData.size
        ).apply {
            put(encryptedAesKey)
            put(iv)
            put(hmac)
            put(encryptedTLVData)
        }.array()

        val header = ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .apply {
                put(0x01)
                put(0x01)
                putShort(bodyWithoutHeader.size.toShort())
            }.array()

        val finalPacket = header + bodyWithoutHeader

        val result = repository.sendTransaction(finalPacket)

        return result
    }
}