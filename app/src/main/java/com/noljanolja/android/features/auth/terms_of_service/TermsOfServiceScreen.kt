package com.noljanolja.android.features.auth.terms_of_service

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.InfoDialog
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.ScaffoldWithRoundedContent
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.util.secondaryTextColor
import org.koin.androidx.compose.getViewModel

@Composable
fun TermsOfServiceScreen(
    viewModel: TermsOfServiceViewModel = getViewModel(),
) {
    TermsOfServiceScreenContent(
        viewModel::handleEvent,
    )
}

@Composable
fun TermsOfServiceScreenContent(
    handleEvent: (TermsOfServiceEvent) -> Unit,
) {
    val compulsoryTerms = remember {
        mutableStateMapOf(
            1 to false,
            2 to false,
            3 to false,
        )
    }
    val optionalTerms = remember {
        mutableStateMapOf(
            1 to false,
        )
    }
    val showHelperDialog = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        ScaffoldWithRoundedContent(heading = {
            TermsHeading()
        }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    SizeBox(height = 50.dp)
                    Text(
                        stringResource(R.string.tos_compulsory).uppercase(),
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 5.dp
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    TermRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        termTitle = stringResource(R.string.tos_compulsory_item_title_1),
                        checked = compulsoryTerms.getOrDefault(1, false),
                        onChecked = { compulsoryTerms[1] = it },
                    )

                    TermRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        termTitle = stringResource(R.string.tos_compulsory_item_title_2),
                        checked = compulsoryTerms.getOrDefault(2, false),
                        onChecked = { compulsoryTerms[2] = it },
                        onClicked = {
                            handleEvent(TermsOfServiceEvent.Detail(2))
                        },
                    )

                    TermRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        termTitle = stringResource(R.string.tos_optional_item_title_1),
                        checked = compulsoryTerms.getOrDefault(3, false),
                        onChecked = { compulsoryTerms[3] = it },
                        onClicked = {
                            handleEvent(TermsOfServiceEvent.Detail(3))
                        },
                    )
                    Expanded()
                }

                PrimaryButton(
                    onClick = { handleEvent(TermsOfServiceEvent.Continue) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 42.dp),
                    isEnable = compulsoryTerms.all { it.value },
                    text = stringResource(id = R.string.common_next).uppercase()
                )
            }
        }
    }

    InfoDialog(
        content = stringResource(R.string.tos_description),
        isShown = showHelperDialog.value,
        dismissText = stringResource(R.string.common_ok),
        onDismiss = { showHelperDialog.value = false },
    )
}

@Composable
private fun TermRow(
    modifier: Modifier,
    termTitle: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
    onClicked: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        TermCheckBox(
            modifier = Modifier.padding(end = 8.dp),
            checked = checked,
            onChecked = onChecked,
        )

        Text(
            termTitle,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.secondaryTextColor()),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (onClicked != null) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier
                    .clickable { onClicked.invoke() }
                    .padding(start = 8.dp)
                    .size(24.dp),
            )
        }
    }
}

@Composable
private fun TermCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onChecked: ((Boolean) -> Unit) = {},
) {
    val checkBoxIcon = if (checked) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank
    Icon(
        checkBoxIcon,
        contentDescription = null,
        modifier = modifier
            .size(24.dp)
            .clickable { onChecked(!checked) },
        tint = with(MaterialTheme.colorScheme) { if (checked) primary else outline }
    )
}

@Composable
private fun TermsHeading() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .width(66.dp)
                .height(62.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            stringResource(id = R.string.common_login),
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 40.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        Text(
            text = stringResource(id = R.string.welcome_noljanolja),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
