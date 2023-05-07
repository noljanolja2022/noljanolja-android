package com.noljanolja.android.ui.composable

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FullSizeWithBottomSheet(
    sheetShape: Shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    modalSheetState: BottomSheetScaffoldState,
    sheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    BottomSheetScaffold(
        scaffoldState = modalSheetState,
        sheetShape = sheetShape,
        sheetContent = sheetContent,
        sheetPeekHeight = 0.dp
    ) {
        content.invoke()
    }
}
