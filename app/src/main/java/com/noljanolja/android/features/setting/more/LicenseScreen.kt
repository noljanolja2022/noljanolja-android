package com.noljanolja.android.features.setting.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.theme.darkText
import org.koin.androidx.compose.getViewModel

@Composable
fun LicenseScreen(
    viewModel: AppInfoViewModel = getViewModel()
) {
    LicenseContent(handleEvent = viewModel::handleEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LicenseContent(
    handleEvent: (AppInfoEvent) -> Unit,
) {
    Scaffold(topBar = {
        CommonTopAppBar(
            centeredTitle = true,
            title = stringResource(id = R.string.setting_license_title),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.darkText(),
            onBack = {
                handleEvent(AppInfoEvent.Back)
            }
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(
                    top = 12.dp,
                    bottom = 24.dp,
                    start = 16.dp,
                    end = 16.dp,
                )
        ) {
            Text(
                licenses,
                modifier = Modifier
                    .weight(1F)
                    .verticalScroll(rememberScrollState()),
                style = MaterialTheme.typography.bodyMedium
            )
            SizeBox(height = 24.dp)
            PrimaryButton(text = "About us".uppercase(), modifier = Modifier.fillMaxWidth()) {

            }
        }
    }
}

const val licenses = "KakaoLink\n" +
        "Copyright 2014-2018 Kakao Corp.\n" +
        "Apache License 2.0\n" +
        "AFNetworking\n" +
        "Copyright (c) 2011-2016 Alamofire Software Foundation (http://alamofire.org/)\n" +
        "The MIT License (MIT)\n" +
        "Bolts\n" +
        "Copyright (c) 2013-present, Facebook, Inc. All rights reserved.\n" +
        "BSD License\n" +
        "couchbase-lite-ios\n" +
        "Couchbase, Inc. Community Edition License Agreement\n" +
        "DeviceKit\n" +
        "Copyright (c) 2015 Dennis Weissmann\n" +
        "The MIT License (MIT)\n" +
        "FBSDKCoreKit\n" +
        "Copyright (c) 2014-present, Facebook, Inc. All rights reserved.\n" +
        "Facebook License\n" +
        "FirebaseCore\n" +
        "Apache License 2.0\n" +
        "FirebaseMessaging\n" +
        "Apache License 2.0\n" +
        "FLAnimatedImage\n" +
        "Copyright (c) 2014 Flipboard\n"