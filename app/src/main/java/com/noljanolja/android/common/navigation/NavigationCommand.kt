package com.noljanolja.android.common.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavOptions

sealed interface NavigationCommand {
    val arguments: List<NamedNavArgument>
    val options: NavOptions?
    val destination: String
    fun createDestination(): String = destination
    data class FinishWithResults(
        val data: Map<String, Any>,
    ) : NavigationCommand {
        override val arguments: List<NamedNavArgument> = listOf()
        override val options: NavOptions? = null
        override val destination: String = "back"

        companion object {
            const val FORGOT_FINISH_AUTH = "finishAuth"
        }
    }
}
