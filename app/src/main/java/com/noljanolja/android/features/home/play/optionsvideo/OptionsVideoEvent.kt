package com.noljanolja.android.features.home.play.optionsvideo

import com.noljanolja.android.features.common.ShareContact
import com.noljanolja.core.video.domain.model.Video

sealed interface OptionsVideoEvent {
    data class ShareVideo(val video: Video, val shareContact: ShareContact?) : OptionsVideoEvent
}