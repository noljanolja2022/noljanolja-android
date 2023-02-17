package com.noljanolja.android.features.home.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noljanolja.android.R
import com.noljanolja.android.common.composable.CommonTopAppBar
import com.noljanolja.android.common.composable.ListTile
import com.noljanolja.android.common.composable.RoundedListTile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CommonTopAppBar(
                title = stringResource(id = R.string.my_page),
            )
        }) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(20.dp)
        ) {
            GreetingSection(stringResource(id = R.string.app_name))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp)
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.onBackground)

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
                        )
                    )
                },
                leadingDrawable = R.drawable.ic_headset,
                trailingDrawable = R.drawable.ic_forward
            ) {}

            RoundedListTile(
                modifier = Modifier.padding(vertical = 8.dp),
                title = {
                    Text(
                        text = stringResource(id = R.string.custom_service_center),
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                description = {
                    Text(
                        text = stringResource(id = R.string.telephone_number_service_center),
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    )
                },
                leadingDrawable = R.drawable.ic_headset
            ) {}

        }
    }
}

@Composable
private fun GreetingSection(name: String) {
    ListTile(
        title = {
            Text(
                text = stringResource(id = R.string.hello) + " $name!",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
            )
        },
        trailingDrawable = R.drawable.ic_forward
    ) {}
}