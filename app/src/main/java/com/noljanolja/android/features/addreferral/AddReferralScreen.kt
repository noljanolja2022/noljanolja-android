package com.noljanolja.android.features.addreferral

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.*
import com.noljanolja.android.ui.theme.*
import com.noljanolja.android.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.androidx.compose.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddReferralScreen(
    viewModel: AddReferralViewModel = getViewModel(),
) {
    val context = LocalContext.current
    var receivePoint by remember { mutableStateOf<Long?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val showBottomSheet = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
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
    LaunchedEffect(receivePoint) {
        scope.launch {
            if (receivePoint != null) {
                showBottomSheet.show()
            } else {
                showBottomSheet.hide()
            }
        }
    }
    AddReferralContent(
        handleEvent = viewModel::handleEvent
    )
    BottomSheetMessage(
        sheetState = showBottomSheet,
        iconMessage = ImageVector.vectorResource(id = R.drawable.ic_point),
        iconTint = Orange300,
        title = "+ $receivePoint",
        message = stringResource(id = R.string.referral_receive_point, receivePoint.orZero()),
        buttonTitle = stringResource(id = R.string.common_ok),
        onConfirmClick = {
            viewModel.handleEvent(AddReferralEvent.GoToMain)
            receivePoint = null
        }
    )
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