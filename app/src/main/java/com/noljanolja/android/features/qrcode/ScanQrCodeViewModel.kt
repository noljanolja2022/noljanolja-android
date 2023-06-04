package com.noljanolja.android.features.qrcode

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections

class ScanQrCodeViewModel : BaseViewModel() {

    fun handleEvent(event: ScanQrCodeEvent) {
        launch {
            when (event) {
                ScanQrCodeEvent.Back -> back()
                is ScanQrCodeEvent.ParseQRSuccess -> parseQRSuccess(event.result)
            }
        }
    }

    private fun parseQRSuccess(result: String) {
        launch {
            navigationManager.navigate(
                NavigationDirections.FinishWithResults(
                    mapOf("qrCode" to result)
                )
            )
        }
    }
}