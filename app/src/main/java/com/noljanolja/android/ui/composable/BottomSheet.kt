package com.noljanolja.android.ui.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.noljanolja.android.ui.theme.*

/**
 * Created by tuyen.dang on 12/19/2023.
 */

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun BottomSheetMessage(
    sheetState: ModalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
    iconMessage: ImageVector? = null,
    iconTint: Color = Orange300,
    title: String? = null,
    message: String,
    buttonTitle: String? = null,
    onConfirmClick: () -> Unit = {}
) {
    ModalBottomSheetLayout(
        sheetContent = {
            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                iconMessage?.let {
                    MarginVertical(20)
                    Icon(
                        modifier = Modifier.size(40.dp),
                        imageVector = it,
                        tint = iconTint,
                        contentDescription = null
                    )
                }
                title?.let {
                    MarginVertical(20)
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium.withBold().copy(
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
                MarginVertical(5)
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                )
                buttonTitle?.let {
                    MarginVertical(50)
                    ButtonRadius(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        title = it,
                        bgColor = MaterialTheme.colorScheme.primary,
                        textColor = Color.Black,
                        onClick = onConfirmClick
                    )
                }
            }
        },
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp
        )
    ) {
    }
}
 