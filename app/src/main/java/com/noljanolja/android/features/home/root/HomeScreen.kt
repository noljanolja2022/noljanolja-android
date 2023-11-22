package com.noljanolja.android.features.home.root

import android.util.*
import androidx.activity.compose.*
import androidx.activity.result.contract.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import androidx.lifecycle.compose.*
import androidx.navigation.*
import androidx.navigation.compose.*
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.*
import com.noljanolja.android.*
import com.noljanolja.android.BuildConfig
import com.noljanolja.android.R
import com.noljanolja.android.features.home.*
import com.noljanolja.android.features.home.conversations.*
import com.noljanolja.android.features.home.friends.*
import com.noljanolja.android.features.home.play.playlist.*
import com.noljanolja.android.features.home.play.playscreen.*
import com.noljanolja.android.features.home.play.playscreen.composable.*
import com.noljanolja.android.features.home.root.banner.*
import com.noljanolja.android.features.home.utils.*
import com.noljanolja.android.features.home.wallet.*
import com.noljanolja.android.features.shop.main.*
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.androidx.compose.*
import kotlin.system.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = getViewModel(),
    checkinViewModel: CheckinViewModel,
) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        MyApplication.isHomeShowed = true
    }
    val scope = rememberCoroutineScope()
    val showBanners by viewModel.eventBannersFlow.collectAsStateWithLifecycle()
    val checkinProgresses by checkinViewModel.checkinProgressFlow.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val readAllConversations by viewModel.readAllConversations.collectAsStateWithLifecycle()

    // Request youtube scope
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
        .requestIdToken(context.getClientId())
        .requestScopes(YOUTUBE_FORCE_SCOPE, YOUTUBE_SCOPE, YOUTUBE_PARTNER_SCOPE)
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    val onTokenResult = { token: String ->
        viewModel.handleEvent(HomeEvent.AutoAction(token))
    }
    val onError: (Throwable) -> Unit = { error: Throwable ->
        Log.e("Request scope error", error.toString())
    }

    val googleSignInLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { result ->
                scope.launch {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    val account = task.getAccount(onError) ?: return@launch
                    getTokenFromAccount(
                        context.findActivity()!!,
                        account,
                        onError = { error ->
                            onError(error)
                        },
                        onTokenResult = onTokenResult
                    )
                }
            }
        )

    val requestYoutubeScope = suspend {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) {
            val newAccount = if (account.isExpired) {
                googleSignInClient.silentSignIn().getAccount(onError)
            } else {
                account
            }
            if (newAccount != null) {
                getTokenFromAccount(
                    context.findActivity()!!,
                    newAccount,
                    onError = { error ->
                        onError(error)
                    },
                    onTokenResult = onTokenResult
                )
            }
        } else {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    LaunchedEffect(key1 = viewModel.errorFlow) {
        launch {
            viewModel.errorFlow.collect {
                context.showToast(context.getErrorMessage(it))
            }
        }
    }

    LaunchedEffect(viewModel.eventPromotedVideoFlow) {
        viewModel.eventPromotedVideoFlow.collectLatest {
            val id = it.video.id
            context.startActivity(
                PlayVideoActivity.createIntent(
                    context,
                    id,
                    isInPictureInPictureMode = true
                )
            )
            if (it.autoComment || it.autoLike || it.autoSubscribe) {
                // TODO : Remove when login google
                delay(200)
                requestYoutubeScope.invoke()
            }
        }
    }

    Scaffold(
        bottomBar = {
            HomeBottomBar(
                navController = navController,
                isReadAllConversations = readAllConversations,
            )
        },
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeNavigationItem.WatchItem.route,
            modifier = Modifier.padding(contentPadding),
        ) {
            addNavigationGraph(
                navController,
                checkinViewModel,
            )
        }
    }

    InfoDialog(
        content = stringResource(R.string.coming_soon),
        isShown = !BuildConfig.DEBUG,
        dismissText = stringResource(R.string.common_exit),
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        onDismiss = {
            exitProcess(0)
        },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )
    )
    showBanners.takeIf { it.isNotEmpty() }?.let {
        EventBannerDialog(eventBanners = it, checkinProgresses = checkinProgresses, onCheckIn = {
            HomeNavigationItem.WalletItem.click(navController)
        }, onCloseBanner = {
            viewModel.handleEvent(HomeEvent.CloseBanner(it.id))
        }, onDismissRequest = {
            viewModel.handleEvent(HomeEvent.CancelBanner)
        })
    }
}

private fun NavGraphBuilder.addNavigationGraph(
    navController: NavHostController,
    checkinViewModel: CheckinViewModel,
) {
    composable(HomeNavigationItem.ChatItem.route) {
        ConversationsScreen()
    }
    composable(HomeNavigationItem.WatchItem.route) {
        PlayListScreen()
    }
    composable(HomeNavigationItem.WalletItem.route) {
        WalletExchangeScreen()
    }
    composable(HomeNavigationItem.StoreItem.route) {
        ShopScreen()
    }
    composable(HomeNavigationItem.FriendsItem.route) {
        FriendsScreen()
    }
}

@Composable
fun HomeBottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    isReadAllConversations: Boolean,
) {
    val items = listOf(
        HomeNavigationItem.FriendsItem,
        HomeNavigationItem.ChatItem,
        HomeNavigationItem.WatchItem,
        HomeNavigationItem.WalletItem,
        HomeNavigationItem.StoreItem,
    )
    NavigationBar(
        modifier = modifier,
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorBackground(),
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = item.isNavItemSelect(navController = navController)
            val label = stringResource(item.label)
            NavigationBarItem(
                icon = {
                    if (index != 0 || isReadAllConversations) {
                        Icon(
                            item.icon,
                            label,
                            modifier = Modifier
                                .padding(2.dp)
                                .size(24.dp)
                        )
                    } else {
                        Box {
                            Icon(item.icon, label)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.error.copy(alpha = 0.5F)
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(2.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.error
                                    )
                            )
                        }
                    }
                },
                label = { Text(label, maxLines = 1) },
                selected = isSelected,
                onClick = {
                    if (BuildConfig.DEBUG) {
                        item.click(navController)
                    }
                },
                colors = with(MaterialTheme.colorScheme) {
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = primary,
                        selectedTextColor = primary,
                        unselectedIconColor = outline,
                        unselectedTextColor = outline,
                        indicatorColor = MaterialTheme.colorScheme.background
                    )
                }
            )
        }
    }
}

private val YOUTUBE_FORCE_SCOPE = Scope("https://www.googleapis.com/auth/youtube.force-ssl")
private val YOUTUBE_SCOPE = Scope("https://www.googleapis.com/auth/youtube")
private val YOUTUBE_PARTNER_SCOPE = Scope("https://www.googleapis.com/auth/youtubepartner")