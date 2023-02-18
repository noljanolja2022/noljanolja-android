package com.d2brothers.firebase_auth

class AuthConfig private constructor(
    val region: String?,
    val googleClientId: String,
) {
    companion object {
        lateinit var instance: AuthConfig
        fun init(
            region: String?,
            googleClientId: String,
        ) {
            instance = AuthConfig(region, googleClientId)
        }
    }
}
