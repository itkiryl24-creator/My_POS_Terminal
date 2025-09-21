package com.example.myposterminal.domain.utils

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun computeHmacSHA256(key: ByteArray, data: ByteArray): ByteArray {
    val mac = Mac.getInstance("HmacSHA256")
    val keySpec = SecretKeySpec(key, "HmacSHA256")
    mac.init(keySpec)
    return mac.doFinal(data)
}