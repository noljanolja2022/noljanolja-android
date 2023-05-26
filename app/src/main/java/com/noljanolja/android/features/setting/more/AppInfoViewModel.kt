package com.noljanolja.android.features.setting.more

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch

class AppInfoViewModel : BaseViewModel() {
    fun handleEvent(event: AppInfoEvent) {
        launch {
            when (event) {
                AppInfoEvent.Back -> back()
            }
        }
    }
}