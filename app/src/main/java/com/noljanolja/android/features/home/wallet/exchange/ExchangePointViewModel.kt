package com.noljanolja.android.features.home.wallet.exchange

import androidx.lifecycle.viewModelScope
import com.noljanolja.android.R
import com.noljanolja.android.common.base.BaseViewModel
import com.noljanolja.android.common.base.launch
import com.noljanolja.android.util.showToast
import com.noljanolja.core.exchange.domain.domain.ExchangeBalance
import com.noljanolja.core.loyalty.domain.model.MemberInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class ExchangePointViewModel : BaseViewModel() {
    val memberInfoFlow = coreManager.getMemberInfo().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MemberInfo()
    )

    private val _myBalanceFlow = MutableStateFlow(ExchangeBalance())
    val myBalanceFlow = _myBalanceFlow.asStateFlow()

    init {
        launch {
            refreshBalance()
        }
    }

    fun handleEvent(event: ExchangeEvent) {
        launch {
            when (event) {
                ExchangeEvent.Back -> back()
                ExchangeEvent.Convert -> convert()
            }
        }
    }

    private suspend fun refreshBalance() {
        coreManager.getExchangeBalance().getOrNull()?.let {
            _myBalanceFlow.emit(it)
        }
    }

    private suspend fun convert() {
        val result = coreManager.convertPoint()
        if (result.isSuccess) {
            refreshBalance()
            coreManager.refreshMemberInfo()
            context.showToast(context.getString(R.string.convert_success))
        } else {
            result.exceptionOrNull()?.let {
                sendError(it)
            } ?: context.showToast(context.getString(R.string.convert_error))
        }
    }
}