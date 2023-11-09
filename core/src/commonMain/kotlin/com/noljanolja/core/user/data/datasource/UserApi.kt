package com.noljanolja.core.user.data.datasource

import com.noljanolja.core.base.ResponseWithoutData
import com.noljanolja.core.user.data.model.request.AddReferralCodeRequest
import com.noljanolja.core.user.data.model.request.FindContactRequest
import com.noljanolja.core.user.data.model.request.InviteFriendRequest
import com.noljanolja.core.user.data.model.request.PushTokensRequest
import com.noljanolja.core.user.data.model.request.SyncUserContactsRequest
import com.noljanolja.core.user.data.model.request.UpdateAvatarRequest
import com.noljanolja.core.user.data.model.request.UpdateUserRequest
import com.noljanolja.core.user.data.model.response.AddReferralResponse
import com.noljanolja.core.user.data.model.response.GetCheckinProgressesResponse
import com.noljanolja.core.user.data.model.response.GetMeResponse
import com.noljanolja.core.user.data.model.response.GetUsersResponse
import com.noljanolja.core.user.data.model.response.UpdateAvatarResponse
import com.noljanolja.core.user.data.model.response.UpdateUserResponse
import com.noljanolja.core.utils.BASE_URL
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class UserApi(private val client: HttpClient) {

    suspend fun getMe(): GetMeResponse {
        return client.get("$BASE_URL/api/v1/users/me").body()
    }

    suspend fun pushTokens(pushTokensRequest: PushTokensRequest): ResponseWithoutData {
        return client.post("$BASE_URL/api/v1/push-tokens") {
            setBody(pushTokensRequest)
        }.body()
    }

    suspend fun syncUserContacts(request: SyncUserContactsRequest): GetUsersResponse {
        return client.post("$BASE_URL/api/v1/users/me/contacts") {
            setBody(request)
        }.body()
    }

    suspend fun updateUser(request: UpdateUserRequest): UpdateUserResponse {
        return client.put("$BASE_URL/api/v1/users/me") {
            setBody(request)
        }.body()
    }

    suspend fun updateAvatar(request: UpdateAvatarRequest): UpdateAvatarResponse {
        return client.post("$BASE_URL/api/v1/users/me") {
            header(HttpHeaders.Accept, ContentType.MultiPart.FormData)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("field", "AVATAR")
                        append(
                            "files",
                            request.files,
                            Headers.build {
                                append(HttpHeaders.ContentType, request.type)
                                append(HttpHeaders.ContentLength, request.files.size.toLong())
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=${request.name}"
                                )
                            }
                        )
                    },
                )
            )
        }.body()
    }

    suspend fun getContacts(page: Int): GetUsersResponse {
        return client.get("$BASE_URL/api/v1/users/me/contacts?page=$page").body()
    }

    suspend fun findContacts(request: FindContactRequest): GetUsersResponse {
        return client.get("$BASE_URL/api/v1/users") {
            url {
                request.friendId?.let {
                    parameters.append("friendId", it)
                }
                request.phoneNumber?.let {
                    parameters.append("phoneNumber", it)
                }
            }
        }.body()
    }

    suspend fun inviteFriend(request: InviteFriendRequest): ResponseWithoutData {
        return client.post("$BASE_URL/api/v1/users/me/contacts/invite") {
            setBody(request)
        }.body()
    }

    suspend fun checkin(): ResponseWithoutData {
        return client.post("$BASE_URL/api/v1/users/me/checkin").body()
    }

    suspend fun getCheckinProgress(): GetCheckinProgressesResponse {
        return client.get("$BASE_URL/api/v1/users/me/checkin-progresses").body()
    }

    suspend fun addReferralCode(request: AddReferralCodeRequest): AddReferralResponse {
        return client.put("$BASE_URL/api/v1/users/me/referral") {
            setBody(request)
        }.body()
    }
}
