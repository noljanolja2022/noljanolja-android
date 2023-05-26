package com.noljanolja.android.features.setting.more

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.common.navigation.NavigationDirections

class AppInfoViewModel : BaseViewModel() {
    fun handleEvent(event: AppInfoEvent) {
        launch {
            when (event) {
                AppInfoEvent.Back -> back()
                AppInfoEvent.AboutUs -> {
                    navigationManager.navigate(NavigationDirections.AboutUs)
                }
            }
        }
    }
}