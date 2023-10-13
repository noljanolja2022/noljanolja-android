package com.noljanolja.android.common.error

import kotlin.Result

object ValidEmailFailure : Throwable()
object CannotFindUsersFailure : Throwable()
object ValidPhoneFailure : Throwable()
object PhoneNotAvailableFailure : Throwable()
object QrNotValidFailure : Throwable()
object UnexpectedFailure : Throwable("UnexpectedFailure")

fun <T> Result<T>.exceptionOrUnDefined() = exceptionOrNull() ?: UnexpectedFailure
