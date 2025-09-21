package com.example.myposterminal.domain.utils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.SecureRandom
import java.util.UUID

private const val TAG_PAN: Byte = 0x10
private const val TAG_AMOUNT: Byte = 0x20
private const val TAG_TXID: Byte = 0x30
private const val TAG_MERCHANT: Byte = 0x40


fun serializeTransactionTLV(amount: Int , merId: String): ByteArray {
    val panBytes = generateMaskedPan().toByteArray(Charsets.UTF_8)
    val txIdBytes = generateTransactionId().toByteArray(Charsets.UTF_8)
    val merchantBytes = merId.toByteArray(Charsets.UTF_8)

    val payloadSize =
        1 + 2 + panBytes.size +
                1 + 2 + 4 +
                1 + 2 + txIdBytes.size +
                1 + 2 + merchantBytes.size

    val buffer = ByteBuffer.allocate(payloadSize)
    buffer.order(ByteOrder.LITTLE_ENDIAN)


    buffer.put(TAG_PAN)
    buffer.putShort(panBytes.size.toShort())
    buffer.put(panBytes)


    buffer.put(TAG_AMOUNT)
    buffer.putShort(4)
    buffer.putInt(amount)


    buffer.put(TAG_TXID)
    buffer.putShort(txIdBytes.size.toShort())
    buffer.put(txIdBytes)


    buffer.put(TAG_MERCHANT)
    buffer.putShort(merchantBytes.size.toShort())
    buffer.put(merchantBytes)
    return buffer.array()
}

private fun generateMaskedPan(): String {
    val rnd = SecureRandom()

    val first4 = (1..4).map { rnd.nextInt(10) }.joinToString("")

    val last4 = (1..4).map { rnd.nextInt(10) }.joinToString("")

    return first4 + "*".repeat(8) + last4
}

private fun generateTransactionId(): String {
    return UUID.randomUUID().toString() + "-" + System.currentTimeMillis()
}

