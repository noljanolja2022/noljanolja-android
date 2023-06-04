package com.noljanolja.android.features.qrcode

sealed interface ScanQrCodeEvent {
    data class ParseQRSuccess(val result: String) : ScanQrCodeEvent
    object Back : ScanQrCodeEvent
}