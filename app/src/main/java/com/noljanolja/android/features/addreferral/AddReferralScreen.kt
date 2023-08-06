package com.noljanolja.android.features.addreferral

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.InfoDialog
import com.noljanolja.android.ui.composable.PrimaryButton
import com.noljanolja.android.ui.composable.SecondaryButton
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.util.getErrorDescription
import com.noljanolja.android.util.orZero
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.getViewModel

@Composable
fun AddReferralScreen(
    viewModel: AddReferralViewModel = getViewModel(),
) {
    val context = LocalContext.current
    var receivePoint by remember { mutableStateOf<Long?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    LaunchedEffect(viewModel.receivePointEvent) {
        viewModel.receivePointEvent.collectLatest {
            receivePoint = it
        }
    }
    LaunchedEffect(viewModel.errorFlow) {
        viewModel.errorFlow.collectLatest {
            errorMessage = context.getErrorDescription(it)
        }
    }
    AddReferralContent(
        handleEvent = viewModel::handleEvent
    )
    InfoDialog(
        title = {
            Text("+ $receivePoint")
        },
        content = stringResource(id = R.string.referral_receive_point, receivePoint.orZero()),
        isShown = receivePoint != null,
        dismissText = stringResource(id = R.string.common_ok)
    ) {
        viewModel.handleEvent(AddReferralEvent.GoToMain)
        receivePoint = null
    }
    InfoDialog(
        title = {
            Text(stringResource(id = R.string.invalid_referral))
        },
        content = errorMessage,
        isShown = errorMessage.isNotBlank(),
        dismissText = stringResource(id = R.string.common_ok)
    ) {
        errorMessage = ""
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReferralContent(
    handleEvent: (AddReferralEvent) -> Unit,
) {
    var code by remember {
        mutableStateOf("")
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            SizeBox(height = 50.dp)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.CenterHorizontally)
            )
            SizeBox(height = 40.dp)
            Text(
                stringResource(id = R.string.referral_code),
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W700,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                stringResource(id = R.string.please_enter_referral_code),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            SizeBox(height = 15.dp)
            TextField(
                value = code,
                onValueChange = { code = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W700,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            SizeBox(height = 60.dp)
            Row(modifier = Modifier.fillMaxWidth()) {
                SecondaryButton(
                    text = stringResource(id = R.string.common_skip),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    handleEvent(AddReferralEvent.GoToMain)
                }
                SizeBox(width = 15.dp)
                PrimaryButton(
                    text = stringResource(id = R.string.common_ok),
                    isEnable = code.isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    handleEvent(AddReferralEvent.SendCode(code))
                }
            }
        }
    }
}