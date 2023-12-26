package com.noljanolja.android.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithRoundedContent(
    roundedCornerShape: RoundedCornerShape = RoundedCornerShape(
        topStart = 40.dp,
        topEnd = 40.dp,
    ),
    backgroundBottomColor: Color? = null,
    background: @Composable (() -> Unit)? = null,
    heading: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Scaffold() { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            background?.invoke()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (background == null) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            Color.Transparent
                        }
                    )
                    .padding(padding)
            ) {
                heading.invoke()
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = roundedCornerShape,
                    colors = CardDefaults.cardColors(
                        containerColor = backgroundBottomColor
                            ?: MaterialTheme.colorScheme.background
                    )
                ) {
                    content.invoke()
                }
            }
        }
    }
}

@Composable
fun ScaffoldWithCircleBgRoundedContent(
    roundedCornerShape: RoundedCornerShape = RoundedCornerShape(
        topStart = 40.dp,
        topEnd = 40.dp,
    ),
    backgroundBottomColor: Color? = null,
    heading: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    ScaffoldWithRoundedContent(
        background = {
//            Image(
//                painter = painterResource(R.drawable.bg_with_circle),
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.FillWidth,
//                contentDescription = null
//            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
            )
        },
        backgroundBottomColor = backgroundBottomColor,
        heading = heading,
        content = content,
        roundedCornerShape = roundedCornerShape
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithCircleBackground(
    content: @Composable () -> Unit = {},
    roundedCornerShape: RoundedCornerShape = RoundedCornerShape(
        topStart = 40.dp,
        topEnd = 40.dp,
    ),
) {
    Scaffold() { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(R.drawable.bg_with_circle),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth,
                contentDescription = null
            )
            content()
        }
    }
}