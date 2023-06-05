package com.noljanolja.android.features.shop

import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.core.loyalty.domain.model.MemberInfo

class ShopViewModel : BaseViewModel() {

}

data class ShopUiData(
    val memberInfo: MemberInfo
)