// package com.noljanolja.android.features.home.chat.components
//
// import android.net.Uri
// import androidx.activity.compose.rememberLauncherForActivityResult
// import androidx.activity.result.contract.ActivityResultContracts
// import androidx.compose.foundation.Image
// import androidx.compose.foundation.background
// import androidx.compose.foundation.layout.*
// import androidx.compose.foundation.shape.CircleShape
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.Text
// import androidx.compose.material3.TextButton
// import androidx.compose.runtime.Composable
// import androidx.compose.runtime.mutableStateOf
// import androidx.compose.runtime.remember
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.draw.clip
// import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.graphics.ColorFilter
// import androidx.compose.ui.graphics.painter.Painter
// import androidx.compose.ui.layout.ContentScale
// import androidx.compose.ui.res.painterResource
// import androidx.compose.ui.unit.dp
//
// private enum class FileSelector {
//    CALL,
//    FILE,
//    NONE,
// }
//
// @Composable
// fun ExtraTable(
//    modifier: Modifier,
//    onCall: () -> Unit,
//    onDocumentSelected: (List<Uri>) -> Unit,
// ) {
//    val selector = remember { mutableStateOf(FileSelector.NONE) }
//
//    Row(
//        modifier = modifier,
//        horizontalArrangement = Arrangement.SpaceEvenly,
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        ExtraButton(
//            onClick = { selector.value = FileSelector.CALL },
//            label = "Camera",
//            icon = painterResource(R.drawable.ic_extra_phone),
//            color = Color(0xFF76D22D)
//        )
//        ExtraButton(
//            onClick = { selector.value = FileSelector.FILE },
//            label = "File",
//            icon = painterResource(R.drawable.ic_extra_file),
//            color = Color(0xFF3199E4),
//        )
//    }
//
//    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
//        selector.value = FileSelector.NONE
//        it?.let {
//            onDocumentSelected(listOf(it))
//        }
//    }
//
//    when (selector.value) {
//        FileSelector.CALL -> {
//            onCall()
//        }
//        FileSelector.FILE -> {
//            fileLauncher.launch(arrayOf("*/*"))
//        }
//        else -> {}
//    }
//
// }
//
// @Composable
// private fun ExtraButton(
//    label: String,
//    icon: Painter,
//    color: Color,
//    onClick: () -> Unit,
// ) {
//    TextButton(
//        onClick = onClick,
//        shape = CircleShape,
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Image(
//                icon,
//                contentDescription = null,
//                contentScale = ContentScale.Inside,
//                modifier = Modifier.size(64.dp)
//                    .clip(CircleShape)
//                    .background(color, CircleShape),
//                colorFilter = ColorFilter.tint(Color.Companion.White),
//            )
//            Text(
//                label,
//                modifier = Modifier.padding(top = 4.dp),
//                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurface)
//            )
//        }
//    }
// }