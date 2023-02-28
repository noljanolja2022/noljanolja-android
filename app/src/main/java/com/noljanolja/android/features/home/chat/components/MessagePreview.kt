//package com.noljanolja.android.features.home.chat.components
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import coil.compose.AsyncImage
//
//
//@Composable
//fun MessagePreview(
//    modifier: Modifier,
//    preview: Pair<Any, MessageType>,
//    onPreviewClosed: () -> Unit,
//) {
//    Box(
//        modifier = modifier
//            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
//            .clickable(
//                indication = null,
//                interactionSource = remember { MutableInteractionSource() },
//            ) {},
//        contentAlignment = Alignment.TopEnd,
//    ) {
//        when (preview.second) {
//            MessageType.Sticker -> {
//                StickerMessagePreview(
//                    modifier = Modifier.fillMaxSize().padding(16.dp),
//                    sticker = preview.first as Sticker,
//                )
//            }
//            else -> {}
//        }
//        IconButton(
//            onClick = onPreviewClosed,
//        ) {
//            Icon(
//                painterResource(R.drawable.ic_close_line),
//                contentDescription = null,
//                tint = MaterialTheme.colorScheme.onSecondary,
//            )
//        }
//    }
//}
//
//@Composable
//private fun StickerMessagePreview(
//    modifier: Modifier = Modifier,
//    sticker: Sticker,
//) {
//    AsyncImage(
//        sticker.imageFile,
//        contentDescription = null,
//        modifier = modifier,
//    )
//}