package com.noljanolja.android.ui.screen.home

import androidx.lifecycle.ViewModel
import com.noljanolja.android.ui.screen.base.launch
import com.noljanolja.android.ui.screen.navigation.NavigationDirections
import com.noljanolja.android.ui.screen.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : ViewModel() {

    fun goToTestScreen() {
        launch {
            navigationManager.navigate(NavigationDirections.HomeItem4)
        }
    }
}