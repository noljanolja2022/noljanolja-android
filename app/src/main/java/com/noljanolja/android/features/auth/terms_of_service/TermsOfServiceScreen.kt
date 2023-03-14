package com.noljanolja.android.features.auth.terms_of_service

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Help
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
import org.koin.androidx.compose.getViewModel
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.InfoDialog
import com.noljanolja.android.ui.composable.ScaffoldWithRoundedContent
import com.noljanolja.android.util.secondaryTextColor

@Composable
fun TermsOfServiceScreen(
    viewModel: TermsOfServiceViewModel = getViewModel(),
) {
    TermsOfServiceScreenContent(
        viewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceScreenContent(
    event: (TermsOfServiceEvent) -> Unit,
) {
    val compulsoryTerms = remember {
        mutableStateMapOf(
            1 to false,
            2 to false,
            3 to false,
            4 to false,
        )
    }
    val optionalTerms = remember {
        mutableStateMapOf(
            1 to false,
            2 to false,
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
                Box(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    contentAlignment = Alignment.TopEnd,
                ) {
                    Text(
                        stringResource(R.string.tos_title),
                        modifier = Modifier.fillMaxWidth().padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 32.dp,
                            bottom = 8.dp,
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    IconButton(
                        onClick = { showHelperDialog.value = true },
                        modifier = Modifier.padding(4.dp),
                    ) {
                        Icon(Icons.Default.Help, contentDescription = null)
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        stringResource(R.string.tos_compulsory),
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )

                    TermRow(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        termTitle = stringResource(R.string.tos_compulsory_item_title_1),
                        checked = compulsoryTerms.getOrDefault(1, false),
                        onChecked = { compulsoryTerms[1] = it },
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

                    TermRow(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        termTitle = stringResource(R.string.tos_compulsory_item_title_4),
                        checked = compulsoryTerms.getOrDefault(4, false),
                        onChecked = { compulsoryTerms[4] = it },
                        onClicked = {},
                    )

                    Text(
                        stringResource(R.string.tos_optional),
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )

                    TermRow(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        termTitle = stringResource(R.string.tos_optional_item_title_1),
                        checked = optionalTerms.getOrDefault(1, false),
                        onChecked = { optionalTerms[1] = it },
                        onClicked = {},
                    )

                    TermRow(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        termTitle = stringResource(R.string.tos_optional_item_title_2),
                        checked = optionalTerms.getOrDefault(2, false),
                        onChecked = { optionalTerms[2] = it },
                        onClicked = {},
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                        .background(MaterialTheme.colorScheme.surface),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        TermCheckBox(
                            modifier = Modifier.padding(start = 16.dp),
                            checked = compulsoryTerms.all { it.value } && optionalTerms.all { it.value },
                            onChecked = {
                                val checked =
                                    compulsoryTerms.all { it.value } && optionalTerms.all { it.value }
                                compulsoryTerms.keys.forEach {
                                    compulsoryTerms[it] = !checked
                                }
                                optionalTerms.keys.forEach {
                                    optionalTerms[it] = !checked
                                }
                            },
                        )
                        Text(
                            stringResource(R.string.tos_read_and_agree),
                            modifier = Modifier.padding(start = 8.dp, end = 24.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Button(
                        onClick = { event(TermsOfServiceEvent.Continue) },
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                        enabled = compulsoryTerms.all { it.value },
                    ) {
                        Text(text = stringResource(R.string.tos_agree_and_continue))
                    }
                }
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
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = 16.dp),
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

        Divider(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun TermCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onChecked: ((Boolean) -> Unit) = {},
) {
    val checkboxId = if (checked) R.drawable.ic_checked else R.drawable.ic_uncheck
    Image(
        painter = painterResource(id = checkboxId),
        contentDescription = null,
        modifier = modifier.size(24.dp).clickable { onChecked(!checked) },
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
