package com.noljanolja.android.features.auth.otp

sealed interface OTPEvent {
    object DismissError : OTPEvent
    data class VerifyOTP(val verificationId: String, val otp: String) : OTPEvent
}