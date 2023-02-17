package com.noljanolja.android.common.navigation

import kotlinx.coroutines.flow.MutableSharedFlow

class NavigationManager {
    val commands = MutableSharedFlow<NavigationCommand>()

    suspend fun navigate(
        direction: NavigationCommand,
    ) {
        commands.emit(direction)
    }
}
