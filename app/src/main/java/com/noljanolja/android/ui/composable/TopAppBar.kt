package com.noljanolja.android.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.noljanolja.android.R
import com.noljanolja.android.extensions.setVisibility
import com.noljanolja.android.ui.theme.textColor
import com.noljanolja.android.util.Constant

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
    hintSearch: String,
    searchFieldBackground: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f),
    onSearchFieldClick: () -> Unit,
    leadingIcon: ImageVector? = null,
    leadingIconTint: Color? = null,
    onLeadingIconClick: () -> Unit = {},
    icon: ImageVector? = null,
    iconTint: Color? = null,
    onIconClick: () -> Unit = {},
    textColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f),
    avatar: String? = null,
    onAvatarClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .padding(vertical = 12.dp, horizontal = Constant.DefaultValue.PADDING_VIEW_SCREEN.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon?.let {
            AppIconButton(
                modifier = Modifier,
                onClick = onLeadingIconClick,
                tint = leadingIconTint ?: textColor(),
                icon = it
            )
        }
        SearchBarViewOnly(
            modifier = Modifier
                .weight(1f)
                .clickable { onSearchFieldClick.invoke() },
            iconTint = iconTint,
            textColor = textColor,
            hint = hintSearch,
            background = searchFieldBackground
        )
        icon?.let {
            AppIconButton(
                modifier = Modifier,
                onClick = onIconClick,
                tint = iconTint ?: textColor(),
                icon = it
            )
        } ?: run {
            MarginHorizontal(13)
        }
        OvalAvatar(
            modifier = Modifier.clickable { onAvatarClick() },
            avatar = avatar,
            size = 24.dp,
            radius = 24.dp
        )
    }
}
