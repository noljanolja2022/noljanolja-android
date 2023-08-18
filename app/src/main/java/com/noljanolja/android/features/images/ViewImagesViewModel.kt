package com.noljanolja.android.features.images

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch

class ViewImagesViewModel : BaseViewModel() {
    fun handleEvent(event: ViewImagesEvent) {
        launch {
            when (event) {
                ViewImagesEvent.Back -> back()
            }
        }
    }
}