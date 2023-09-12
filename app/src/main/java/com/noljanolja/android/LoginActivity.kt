package com.noljanolja.android

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.noljanolja.android.common.navigation.NavigationManager
import com.noljanolja.android.common.network.ConnectivityObserver
import com.noljanolja.android.common.network.NetworkConnectivityObserver
import com.noljanolja.android.ui.theme.NoljanoljaTheme
import org.koin.android.ext.android.inject

class LoginActivity : ComponentActivity() {
    private val navigationManager: NavigationManager by inject()
    private lateinit var connectivityObserver: ConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectivityObserver = NetworkConnectivityObserver(this)
        setContent {
            NoljanoljaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    AuthScreen(navigationManager)
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = maxOf(newOverride.fontScale, 1.2f)
        applyOverrideConfiguration(newOverride)

        super.attachBaseContext(newBase)
    }
}