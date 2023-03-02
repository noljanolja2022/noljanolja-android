package com.noljanolja.android.common.user.data.datasource

import com.noljanolja.android.common.user.data.model.CommonResponse
import com.noljanolja.android.common.user.data.model.GetMeResponse
import com.noljanolja.android.common.user.data.model.request.PushTokensRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*

@OptIn(InternalAPI::class)
class UserApi(private val client: HttpClient) {

    suspend fun getMe(): GetMeResponse {
        return client.get("$END_POINT/users/me").body()
    }

    suspend fun pushTokens(pushTokensRequest: PushTokensRequest): CommonResponse {
        return client.post("$END_POINT/push-tokens") {
            body = pushTokensRequest
        }.body()
    }

    companion object {
        const val END_POINT = "http://34.64.110.104/api/v1"
    }
}
