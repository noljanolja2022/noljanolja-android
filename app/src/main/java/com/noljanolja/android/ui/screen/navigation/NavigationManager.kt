package com.noljanolja.android.ui.screen.navigation

import kotlinx.coroutines.flow.MutableSharedFlow

class NavigationManager {
    val commands = MutableSharedFlow<NavigationCommand>()

    suspend fun navigate(
        direction: NavigationCommand
    ) {
        commands.emit(direction)
    }
}