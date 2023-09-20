package com.noljanolja.android.features.home.root

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.noljanolja.android.R
import com.noljanolja.android.features.home.CheckinViewModel
import com.noljanolja.android.features.home.conversations.ConversationsScreen
import com.noljanolja.android.features.home.play.playlist.PlayListScreen
import com.noljanolja.android.features.home.play.playscreen.PlayVideoActivity
import com.noljanolja.android.features.home.play.playscreen.composable.getAccount
import com.noljanolja.android.features.home.play.playscreen.composable.getTokenFromAccount
import com.noljanolja.android.features.home.play.search.SearchVideosViewModel
import com.noljanolja.android.features.home.root.banner.EventBannerDialog
import com.noljanolja.android.features.home.utils.click
import com.noljanolja.android.features.home.utils.isNavItemSelect
import com.noljanolja.android.features.home.wallet.WalletScreen
import com.noljanolja.android.features.shop.main.ShopScreen
import com.noljanolja.android.ui.theme.colorBackground
import com.noljanolja.android.util.findActivity
import com.noljanolja.android.util.getErrorMessage
import com.noljanolja.android.util.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = getViewModel(),
    checkinViewModel: CheckinViewModel,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val showBanners by viewModel.eventBannersFlow.collectAsStateWithLifecycle()
    val checkinProgresses by checkinViewModel.checkinProgressFlow.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val readAllConversations by viewModel.readAllConversations.collectAsStateWithLifecycle()

    // Search screen
    val searchVideosViewModel: SearchVideosViewModel = getViewModel()
    val uiState by searchVideosViewModel.uiStateFlow.collectAsStateWithLifecycle()
    val searchKeys by searchVideosViewModel.searchKeys.collectAsStateWithLifecycle()
    var showSearchVideo by rememberSaveable { mutableStateOf(false) }

    // Video detail screen
    val density = LocalDensity.current

    // Request youtube scope
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
        .requestIdToken(stringResource(id = R.string.web_client_id))
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
            context.startActivity(PlayVideoActivity.createIntent(context, id))
            if (it.autoComment || it.autoLike || it.autoSubscribe) {
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
            startDestination = HomeNavigationItem.ChatItem.route,
            modifier = Modifier.padding(contentPadding),
        ) {
            addNavigationGraph(
                navController,
                checkinViewModel,
                onSearchVideo = {
                    showSearchVideo = true
                }
            )
        }
    }

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
    onSearchVideo: () -> Unit,
) {
    composable(HomeNavigationItem.ChatItem.route) {
        ConversationsScreen()
    }
    composable(HomeNavigationItem.WatchItem.route) {
        PlayListScreen(
            onSearchVideo = onSearchVideo
        )
    }
    composable(HomeNavigationItem.WalletItem.route) {
        WalletScreen(checkinViewModel = checkinViewModel, onUseNow = {
            HomeNavigationItem.StoreItem.click(navController)
        })
    }
    composable(HomeNavigationItem.StoreItem.route) {
        ShopScreen()
    }
}

@Composable
fun HomeBottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    isReadAllConversations: Boolean,
) {
    val items = listOf(
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
                    item.click(navController)
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