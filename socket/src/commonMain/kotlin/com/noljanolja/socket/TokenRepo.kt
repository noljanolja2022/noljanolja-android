package com.noljanolja.socket

interface TokenRepo {
    suspend fun getToken(): String?

    suspend fun refreshToken()
}