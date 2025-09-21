package com.example.myposterminal.domain.utils

import android.content.Context
import android.util.Base64
import com.example.myposterminal.data.storage.Config
import java.security.KeyFactory
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

fun generateAes256Key(): SecretKey {
    val keyGen = KeyGenerator.getInstance("AES")
    keyGen.init(256)

    return keyGen.generateKey()
}

fun encryptAesGcm(aesKey: SecretKey, plaintext: ByteArray): Pair<ByteArray, ByteArray> {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val iv = ByteArray(12)
    SecureRandom().nextBytes(iv)
    val spec = GCMParameterSpec(128, iv)
    cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec)
    val encrypted = cipher.doFinal(plaintext)
    return iv to encrypted
}

fun encryptAesKeyWithRsa(aesKey: SecretKey, serverPublicKey: PublicKey): ByteArray {
    val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
    val oaepParams = OAEPParameterSpec(
        "SHA-256",
        "MGF1",
        MGF1ParameterSpec.SHA256,
        PSource.PSpecified.DEFAULT
    )
    cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey,oaepParams)
    return cipher.doFinal(aesKey.encoded)
}

suspend fun getServerPublicKey(rawKey: String): PublicKey {
    val decoded = Base64.decode(rawKey, Base64.DEFAULT)
    val keySpec = X509EncodedKeySpec(decoded)
    val keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePublic(keySpec)
}
