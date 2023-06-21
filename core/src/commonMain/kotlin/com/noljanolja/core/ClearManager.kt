package com.noljanolja.core

import com.noljanolja.core.db.Noljanolja
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin

class ClearManager(
    private val noljanolja: Noljanolja,
    private val client: HttpClient,
) {
    fun clearAll() {
        val provider =
            client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>()
                .firstOrNull()
        provider?.clearToken()
        with(noljanolja) {
            authQueries.delete()
            commentQueries.deleteAll()
            memberInfoQueries.deleteAll()
            messageQueries.deleteAll()
            participantQueries.deleteAll()
            searchTextQueries.deleteAll()
            userQueries.deleteAll()
            conversationQueries.deleteAll()
            videoQueries.deleteAll()
            videoCategoryQueries.deleteAll()
            videoChannelQueries.deleteAll()
        }
    }
}