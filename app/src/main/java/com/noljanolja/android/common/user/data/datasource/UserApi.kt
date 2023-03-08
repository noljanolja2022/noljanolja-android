package com.noljanolja.android.common.user.data.datasource

import com.noljanolja.android.common.user.data.model.CommonResponse
import com.noljanolja.android.common.user.domain.model.request.PushTokensRequest
import com.noljanolja.android.common.user.domain.model.request.SyncUserContactsRequest
import com.noljanolja.android.common.user.domain.model.response.GetMeResponse
import com.noljanolja.android.common.user.domain.model.response.SyncUserContactsResponse
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

class UserApi(private val client: HttpClient) {

    suspend fun getMe(): GetMeResponse {
        return client.get("$END_POINT/users/me").body()
    }

    suspend fun pushTokens(pushTokensRequest: PushTokensRequest): CommonResponse {
        return client.post("$END_POINT/push-tokens") {
            setBody(pushTokensRequest)
        }.body()
    }

    suspend fun syncUserContacts(request: SyncUserContactsRequest): SyncUserContactsResponse {
        return client.post("$END_POINT/users/me/contacts") {
            setBody(request)
        }.body()
    }

    companion object {
        const val END_POINT = "http://34.64.110.104/api/v1"
    }
}
