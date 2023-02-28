//package com.noljanolja.android.features.home.chat.components
//
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.rounded.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.tooling.preview.Preview
//
//@Composable
//fun ChatTopBar(
//    title: String,
//    onBack: () -> Unit = {},
//) {
//    SmallTopAppBar(
//        navigationIcon = {
//            IconButton(onClick = onBack) {
//                Icon(Icons.Rounded.ArrowBack, contentDescription = null)
//            }
//        },
//        title = { Text(text = title) },
//        colors = TopAppBarDefaults.smallTopAppBarColors(
//            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
//        )
//    )
//}
//
//@Preview
//@Composable
//fun ChatTopBarDarkThemePreview() {
//    AppTheme(isDarkTheme = true) {
//        ChatTopBar("Chat")
//    }
//}
//
//@Preview
//@Composable
//fun ChatTopBarLightThemePreview() {
//    AppTheme(isDarkTheme = false) {
//        ChatTopBar("Chat")
//    }
//}