package com.lacolinares.jetpackqrbarcodescanner.model

fun interface BarcodeTypeModel {

    fun getRawValue(): String

    data class Wifi(
        val networkName: String? = null,
        val password: String? = null,
        val type: AuthType? = null,
        val rawString: String? = null
    ): BarcodeTypeModel{

        enum class AuthType(val value: String) {
            WEP("WEP"),
            WPA("WPA"),
            OPEN("OPEN") {
                override fun toString(): String {
                    return "No Password"
                }
            }
        }
        override fun getRawValue(): String = rawString.orEmpty()
    }

    data class Url(
        val title: String? = null,
        val url: String? = null,
        val rawString: String? = null
    ): BarcodeTypeModel{
        override fun getRawValue(): String = rawString.orEmpty()
    }
}