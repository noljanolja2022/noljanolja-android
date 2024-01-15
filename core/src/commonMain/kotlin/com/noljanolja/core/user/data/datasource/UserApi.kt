package com.noljanolja.core.user.data.datasource

import com.noljanolja.core.base.ResponseWithoutData
import com.noljanolja.core.user.data.model.request.*
import com.noljanolja.core.user.data.model.response.*
import com.noljanolja.core.utils.BASE_URL
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.*

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

    suspend fun sendPoint(request: SendPointRequest): SendPointResponse {
        request.run {
            return client.post("$BASE_URL/api/v1/transfer-point/${if (isRequestPoint) "request" else "send"}") {
                url {
                    if (toUserId.isNotBlank()) {
                        parameters.append("toUserId", toUserId)
                    }
                    parameters.append("points", points.toString())
                }
            }.body()
        }
    }

    suspend fun getPointConfig(): GetPointConfigResponse {
        return client.get("$BASE_URL/api/v1/reward/referral/configs").body()
    }

    suspend fun getNotifications(request: GetNotificationsRequest): GetNotificationsResponse {
        return client.get("$BASE_URL/api/v1/notification"){
            url {
                request.run {
                    parameters.append("page", page.toString())
                    parameters.append("pageSize", pageSize.toString())
                }
            }
        }.body()
    }

    suspend fun readNotification(request: ReadNotificationRequest): ResponseWithoutData {
        return client.post("$BASE_URL/api/v1/notification/${request.notificationID}/read").body()
    }

    suspend fun maskAllNotificationsIsRead(): ResponseWithoutData {
        return client.post("$BASE_URL/api/v1/notification/readAll").body()
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
