package com.noljanolja.android.ui.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.*
import com.noljanolja.android.R
import com.noljanolja.android.extensions.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopAppBar(
    title: String = "",
    leadingTitle: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    centeredTitle: Boolean = false,
    navigationIcon: ImageVector = Icons.Default.ArrowBack,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    onBack: (() -> Unit)? = null,
) {
    val colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = containerColor,
        titleContentColor = contentColor,
        actionIconContentColor = MaterialTheme.colorScheme.onBackground,
        navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
    )
    if (centeredTitle) {
        CenterAlignedTopAppBar(
            colors = colors,
            title = {
                CommonAppBarTitle(title = title, leadingTitle = leadingTitle, color = contentColor)
            },
            actions = actions,
            navigationIcon = {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(
                            navigationIcon,
                            contentDescription = null,
                            tint = contentColor,
                        )
                    }
                }
            },
        )
    } else {
        TopAppBar(
            colors = colors,
            title = {
                CommonAppBarTitle(title = title, leadingTitle = leadingTitle, color = contentColor)
            },
            actions = actions,
            navigationIcon = {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = contentColor
                        )
                    }
                }
            },
        )
    }
}

@Composable
fun CommonAppBarTitle(
    title: String,
    leadingTitle: @Composable (() -> Unit)? = null,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingTitle?.invoke()
        Text(
            text = title,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp,
                color = color
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Composable
fun CommonAppBarLogoTitle(
    modifier: Modifier = Modifier,
    titleFirstLine: AnnotatedString,
    titleSecondLine: AnnotatedString,
    firstIcon: ImageVector? = null,
    firstIconClickListener: () -> Unit = {},
    secondIcon: ImageVector? = null,
    secondIconClickListener: () -> Unit = {},
    thirdIcon: ImageVector? = null,
    thirdIconClickListener: () -> Unit = {}
) {
    ConstraintLayout(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        val (imgLogo,
            tvTitleFirstLine,
            tvTitleSecondLine,
            btnFirst,
            btnSecond,
            btnThird
        ) = createRefs()
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .constrainAs(imgLogo) {
                    top.linkTo(parent.top)
                    linkTo(
                        start = parent.start,
                        end = btnThird.start,
                        bias = 0.0f
                    )
                }
        )
        Text(
            text = titleFirstLine,
            modifier = Modifier
                .constrainAs(tvTitleFirstLine) {
                    top.linkTo(imgLogo.bottom, 5.dp)
                    start.linkTo(imgLogo.start)
                }
        )
        Text(
            text = titleSecondLine,
            modifier = Modifier
                .constrainAs(tvTitleSecondLine) {
                    top.linkTo(tvTitleFirstLine.bottom, 5.dp)
                    start.linkTo(tvTitleFirstLine.start)
                }
        )
        AppIconButton(
            onClick = firstIconClickListener,
            modifier = Modifier.constrainAs(btnFirst) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                visibility = setVisibility(firstIcon != null)
            },
            tint = textColor(),
            enabled = firstIcon != null,
            icon = firstIcon
        )
        AppIconButton(
            onClick = secondIconClickListener,
            modifier = Modifier.constrainAs(btnSecond) {
                top.linkTo(parent.top)
                end.linkTo(btnFirst.start)
                visibility = setVisibility(secondIcon != null)
            },
            tint = textColor(),
            enabled = secondIcon != null,
            icon = secondIcon
        )
        AppIconButton(
            onClick = thirdIconClickListener,
            modifier = Modifier
                .constrainAs(btnThird) {
                    top.linkTo(parent.top)
                    end.linkTo(btnSecond.start)
                    visibility = setVisibility(thirdIcon != null)
                },
            tint = textColor(),
            enabled = thirdIcon != null,
            icon = thirdIcon
        )
    }
}

@Composable
fun CommonAppBarSearch(
    modifier: Modifier = Modifier,
    onSearchFieldClick: () -> Unit,
    icon: ImageVector? = null,
    onIconClick: () -> Unit = {},
    avatar: String? = null,
    onAvatarClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .padding(vertical = 12.dp, horizontal = Constant.DefaultValue.PADDING_VIEW_SCREEN.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBarViewOnly(
            modifier = Modifier
                .weight(1f)
                .clickable { onSearchFieldClick.invoke() },
            hint = stringResource(id = R.string.search_videos),
        )
        icon?.let {
            AppIconButton(
                modifier = Modifier,
                onClick = onIconClick,
                tint = textColor(),
                icon = it
            )
        }
        avatar?.let {
            OvalAvatar(
                modifier = Modifier.clickable { onAvatarClick() },
                avatar = it,
                size = 24.dp,
                radius = 24.dp
            )
        }
    }
}
