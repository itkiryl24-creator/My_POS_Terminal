package com.example.myposterminal.data.storage

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object Config {
    val Context.keysDataStore by preferencesDataStore(name = "keys_store")
    private val HMAC_KEY = stringPreferencesKey("HMAC_KEY")
    private val SERVER_PUBLIC_KEY = stringPreferencesKey("SERVER_PUBLIC_KEY")
    private val MERCHANT_ID = stringPreferencesKey("MERCHANT_ID")

    suspend fun getHmacKey(context: Context): ByteArray {
        val prefs = context.keysDataStore.data.first()
        val base64 = prefs[HMAC_KEY] ?: "MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="
        return Base64.decode(base64, Base64.DEFAULT)
    }

    suspend fun getServerPublicKey(context: Context): String {
        val prefs = context.keysDataStore.data.first()
        return prefs[SERVER_PUBLIC_KEY] ?: ""
    }

    suspend fun getMerchantId(context: Context): String {
        val prefs = context.keysDataStore.data.first()
        return prefs[MERCHANT_ID] ?: "MERCHANT-1234"
    }
    suspend fun updateHmacKey(context: Context, base64: String) {
        context.keysDataStore.edit { prefs ->
            prefs[HMAC_KEY] = base64
        }
    }
    suspend fun initializeDefaults(context: Context) {
        context.keysDataStore.edit { prefs ->
            if (prefs[HMAC_KEY] == null) prefs[HMAC_KEY] = """BmMU+cT4KAStJnStlE9vTztlvpxeQgnvk8O8lUdoADo\="""
            if (prefs[SERVER_PUBLIC_KEY] == null) prefs[SERVER_PUBLIC_KEY] = """MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAotrvl7yBWgbsFvtxOTw7mEV7dyeA77A0E3oG1frd2lRGWmEaowlg2V3Bdmc/CNy5MoaGaf73mcYKW5wc+1KMb99VCq7UZr+1erDf66RziSbIE9cRfIqRVk+P2sapEtNsruEKHbnFeczTTZWqUpbwevrvmaKVhfWf46bchB67D2ATlg+7Th1lzej/n9ZHTwhq2eyPWdED4UBKgl8EHXiXBPzzN0wvktSfqFFkwgRvKi5t6EUIQ4NGeRXLut7/Lfu/ih5Ds9FNADG0lN505bhZS98wI2jmTRobIrH7cE63/Byd0WxhOcBdp2oxNOwUj2XWyKuIj9kFkVw+HWtXmh9RiQIDAQAB"""
            if (prefs[MERCHANT_ID] == null) prefs[MERCHANT_ID] = "MERCHANT-1234"
        }
    }
}
