package com.noljanolja.android.features.auth.terms_of_service

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
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
import com.noljanolja.android.ui.composable.InfoDialog
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.ScaffoldWithRoundedContent
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
    event: (TermsOfServiceEvent) -> Unit,
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
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Spacer(modifier = Modifier.weight(1F))
                    Text(
                        stringResource(R.string.tos_compulsory).uppercase(),
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 5.dp
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )

                    TermRow(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        termTitle = stringResource(R.string.tos_compulsory_item_title_1),
                        checked = compulsoryTerms.getOrDefault(1, false),
                        onChecked = { compulsoryTerms[1] = it },
                        onClicked = {}
                    )

                    TermRow(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        termTitle = stringResource(R.string.tos_compulsory_item_title_2),
                        checked = compulsoryTerms.getOrDefault(2, false),
                        onChecked = { compulsoryTerms[2] = it },
                        onClicked = {},
                    )

                    TermRow(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        termTitle = stringResource(R.string.tos_compulsory_item_title_3),
                        checked = compulsoryTerms.getOrDefault(3, false),
                        onChecked = { compulsoryTerms[3] = it },
                        onClicked = {},
                    )

                    Spacer(modifier = Modifier.weight(1F))

                    Text(
                        stringResource(R.string.tos_optional).uppercase(),
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 5.dp
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )

                    TermRow(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        termTitle = stringResource(R.string.tos_optional_item_title_1),
                        checked = optionalTerms.getOrDefault(1, false),
                        onChecked = { optionalTerms[1] = it },
                        onClicked = {},
                    )
                    Spacer(modifier = Modifier.weight(1F))
                }

                PrimaryButton(
                    onClick = { event(TermsOfServiceEvent.Continue) },
                    modifier = Modifier.fillMaxWidth()
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
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = 15.dp),
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (onClicked != null) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 8.dp).size(24.dp),
                )
            }
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
        modifier = modifier.size(24.dp).clickable { onChecked(!checked) },
        tint = with(MaterialTheme.colorScheme) { if (checked) primary else outline }
    )
}

@Composable
private fun TermsHeading() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.width(66.dp).height(62.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            stringResource(id = R.string.common_login),
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 40.sp
            )
        )
        Text(
            text = stringResource(id = R.string.welcome_noljanolja),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
