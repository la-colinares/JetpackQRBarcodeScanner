package com.lacolinares.jetpackqrbarcodescanner.model

fun interface BarcodeTypeModel {

    fun encodeToString(): String

    data class Wifi(
        val networkName: String? = null,
        val password: String? = null,
        val type: AuthType? = null
    ): BarcodeTypeModel{

        enum class AuthType {
            WEP,
            WPA,
            OPEN {
                override fun toString(): String {
                    return "No Password"
                }
            }
        }

        override fun encodeToString(): String = buildString {
            networkName?.let {
                append("N:$it;")
            }
            password?.let {
                append("P:$it;")
            }
            type?.let {
                append("T:$it;")
            }
        }
    }

    data class Url(val title: String, val url: String): BarcodeTypeModel{
        override fun encodeToString(): String = "T:$title;U:$url;"
    }
}