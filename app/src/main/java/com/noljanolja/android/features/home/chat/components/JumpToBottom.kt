//package com.noljanolja.android.features.home.chat.components
//
//import androidx.compose.animation.core.animateDp
//import androidx.compose.animation.core.updateTransition
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.offset
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowDownward
//import androidx.compose.material3.ExtendedFloatingActionButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//
//val JumpToBottomThreshold = 56.dp
//
//@Composable
//fun JumpToBottom(
//    enabled: Boolean,
//    onClicked: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    // Show Jump to Bottom button
//    val transition = updateTransition(enabled)
//    val bottomOffset by transition.animateDp { if (!it) (-32).dp else 32.dp }
//
//    if (bottomOffset > 0.dp) {
//        ExtendedFloatingActionButton(
//            icon = {
//                Icon(
//                    Icons.Filled.ArrowDownward,
//                    contentDescription = null,
//                    modifier = Modifier.height(18.dp),
//                )
//            },
//            text = {
//                Text(text = "Jump to bottom")
//            },
//            onClick = onClicked,
//            containerColor = MaterialTheme.colorScheme.surface,
//            contentColor = MaterialTheme.colorScheme.primary,
//            modifier = modifier
//                .offset(x = 0.dp, y = -bottomOffset)
//                .height(36.dp)
//        )
//    }
//}
//
//@Preview
//@Composable
//fun JumpToBottomPreview() {
//    JumpToBottom(enabled = true, onClicked = {})
//}
