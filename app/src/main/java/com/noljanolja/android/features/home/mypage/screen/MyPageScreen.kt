package com.noljanolja.android.features.home.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.d2brothers.firebase_auth.model.displayIdentity
import com.noljanolja.android.R
import com.noljanolja.android.common.composable.CommonTopAppBar
import com.noljanolja.android.common.composable.FullSizeLoading
import com.noljanolja.android.common.composable.ListTile
import com.noljanolja.android.common.composable.RoundedListTile

@Composable
fun MyPageScreen(
    viewModel: MyPageViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsState()
    MyPageContent(
        uiState,
        handleEvent = {
            viewModel.handleEvent(it)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageContent(
    uiState: MyPageUIState,
    handleEvent: (MyPageEvent) -> Unit,
) {
    FullSizeLoading(
        uiState is MyPageUIState.Loading,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CommonTopAppBar(
                    title = stringResource(id = R.string.my_page),
                )
            },
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 20.dp),
            ) {
                GreetingSection(
                    modifier = Modifier.padding(top = 18.dp)
                        .height(52.dp),
                    name = (uiState as? MyPageUIState.Loaded)?.user.displayIdentity(),
                    onClick = {
                        handleEvent(MyPageEvent.GoToMyInfo)
                    },
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp, bottom = 16.dp)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.onBackground),

                )
                RoundedListTile(
                    modifier = Modifier.padding(vertical = 8.dp),
                    title = {
                        Text(
                            text = stringResource(id = R.string.service_guide),
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    },
                    leadingDrawable = R.drawable.ic_headset,
                    trailingDrawable = R.drawable.ic_forward,
                ) {}

                RoundedListTile(
                    modifier = Modifier.padding(vertical = 8.dp),
                    title = {
                        Text(
                            text = stringResource(id = R.string.custom_service_center),
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                    },
                    description = {
                        Text(
                            text = stringResource(id = R.string.telephone_number_service_center),
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.outline,
                            ),
                        )
                    },
                    leadingDrawable = R.drawable.ic_headset,
                ) {}
            }
        }
    }
}

@Composable
private fun GreetingSection(
    modifier: Modifier = Modifier,
    name: String,
    onClick: () -> Unit,
) {
    ListTile(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(id = R.string.hello_user, name),
                style = TextStyle(
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        },
        trailingDrawable = R.drawable.ic_forward,
        onClick = onClick,
    )
}
