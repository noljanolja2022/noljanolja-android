package com.noljanolja.android.features.referral

import com.noljanolja.android.common.base.*

class ReferralViewModel : BaseViewModel() {

    fun handleEvent(event: ReferralEvent) {
        launch {
            when (event) {
                ReferralEvent.Back -> back()
            }
        }
    }
}