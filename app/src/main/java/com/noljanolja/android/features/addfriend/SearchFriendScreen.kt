package com.noljanolja.android.features.addfriend

import android.telephony.PhoneNumberUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noljanolja.android.R
import com.noljanolja.android.common.base.UiState
import com.noljanolja.android.common.country.Countries
import com.noljanolja.android.common.country.DEFAULT_CODE
import com.noljanolja.android.common.country.getFlagEmoji
import com.noljanolja.android.common.error.ValidPhoneFailure
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.ErrorDialog
import com.noljanolja.android.ui.composable.ScaffoldWithUiState
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.ui.composable.rememberQrBitmapPainter
import com.noljanolja.android.ui.theme.disableBackgroundColor
import com.noljanolja.android.ui.theme.withBold
import com.noljanolja.android.util.getErrorDescription
import com.noljanolja.android.util.parseUserIdFromQr
import com.noljanolja.core.user.domain.model.User

@Composable
fun SearchFriendScreen(
    savedStateHandle: SavedStateHandle,
    addFriendViewModel: AddFriendViewModel,
) {
    val context = LocalContext.current
    val countryCode = savedStateHandle.get<String>("countryCode")
    val qrCode = savedStateHandle.get<String>("qrCode")
    var error by remember { mutableStateOf<Throwable?>(null) }
    LaunchedEffect(key1 = qrCode) {
        if (!qrCode.isNullOrBlank()) {
            addFriendViewModel.handleEvent(AddFriendEvent.SearchById(qrCode.parseUserIdFromQr()))
            savedStateHandle.remove<String>("qrCode")
        }
    }
    LaunchedEffect(addFriendViewModel.errorFlow) {
        addFriendViewModel.errorFlow.collect {
            error = it
        }
    }
    val uiState by addFriendViewModel.searchFriendUiStateFlow.collectAsStateWithLifecycle()
    SearchFriendContent(
        uiState = uiState,
        countryCode = countryCode,
        handleEvent = addFriendViewModel::handleEvent
    )
    error?.let {
        ErrorDialog(
            showError = true,
            title = stringResource(id = R.string.common_error_title),
            description = context.getErrorDescription(it)
        ) {
            error = null
        }
    }
}

@Composable
private fun SearchFriendContent(
    uiState: UiState<AddFriendUiData>,
    countryCode: String?,
    handleEvent: (AddFriendEvent) -> Unit,
) {
    ScaffoldWithUiState(uiState = uiState, topBar = {
        CommonTopAppBar(
            title = stringResource(id = R.string.add_friend_title),
            onBack = { handleEvent(AddFriendEvent.Back) },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            centeredTitle = true,
        )
    }) {
        val data = uiState.data ?: return@ScaffoldWithUiState
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            SearchPhone(
                countryCode = countryCode,
                openCountryList = { handleEvent(AddFriendEvent.OpenCountries) },
                onSubmit = {
                    handleEvent(AddFriendEvent.SearchByPhone(it))
                },
                onError = {
                    handleEvent(AddFriendEvent.ShowError(it))
                }
            )
            SizeBox(height = 18.dp)
            SearchQrAndContact(onSearchQr = {
                handleEvent(AddFriendEvent.ScanQrCode)
            })
            SizeBox(height = 20.dp)
            QrInformation(data.user)
        }
    }
}

@Composable
private fun SearchPhone(
    countryCode: String?,
    openCountryList: () -> Unit,
    onError: (Throwable) -> Unit,
    onSubmit: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val countryInteractionSource = remember { MutableInteractionSource() }
    if (countryInteractionSource.collectIsPressedAsState().value) {
        focusManager.clearFocus(true)
        openCountryList.invoke()
    }
    val country by remember {
        mutableStateOf(
            Countries.first {
                it.nameCode == (countryCode ?: DEFAULT_CODE)
            }
        )
    }
    var phone by rememberSaveable { mutableStateOf("") }
    val formattedPhoneNumber =
        PhoneNumberUtils.formatNumberToE164(phone.trim(), country.nameCode.uppercase())
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(
            modifier = Modifier.weight(1f).border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(10.dp)
            ).padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.clickable {
                    openCountryList.invoke()
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    country.getFlagEmoji(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.disableBackgroundColor()).padding(5.dp)
                )
                SizeBox(width = 6.dp)
                Text("+${country.phoneCode}", style = MaterialTheme.typography.bodyMedium)
                SizeBox(width = 5.dp)
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).padding(2.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            SizeBox(width = 12.dp)
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (phone.isEmpty()) {
                    Text(
                        stringResource(id = R.string.enter_phone_number),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = phone,
                    onValueChange = {
                        phone = it
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus(true) },
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
        SizeBox(12.dp)
        IconButton(
            onClick = {
                if (formattedPhoneNumber == null) {
                    onError(ValidPhoneFailure)
                } else {
                    onSubmit(formattedPhoneNumber)
                }
            },
            enabled = phone.isNotBlank(),
            modifier = Modifier.clip(CircleShape).size(36.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = MaterialTheme.disableBackgroundColor(),
                disabledContentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun SearchQrAndContact(
    onSearchQr: () -> Unit,
) {
    Row {
        SearchItem(
            title = stringResource(id = R.string.add_friend_search_by_qr),
            icon = ImageVector.vectorResource(id = R.drawable.ic_qr_scan),
            modifier = Modifier.weight(1f),
            onClick = onSearchQr
        )
        SizeBox(width = 12.dp)
        SearchItem(
            title = stringResource(id = R.string.add_friend_add_by_contact),
            icon = Icons.Default.Contacts,
            modifier = Modifier.weight(1f),
            onClick = {}
        )
    }
}

@Composable
private fun SearchItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clickable {
            onClick.invoke()
        },
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
            SizeBox(height = 12.dp)
            Text(text = title)
        }
    }
}

@Composable
private fun QrInformation(user: User) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.padding(start = 42.dp, end = 42.dp, top = 75.dp)
                .clip(RoundedCornerShape(20.dp)).matchParentSize().align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.primary)
        )
        Image(
            painter = painterResource(id = R.drawable.bg_qrfriend),
            contentDescription = null,
            modifier = Modifier.padding(end = 42.dp).fillMaxWidth().align(Alignment.TopStart),
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier.matchParentSize().align(Alignment.BottomCenter)
                .padding(top = 95.dp, bottom = 20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.bodyMedium.withBold(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.align(Alignment.End).padding(end = 88.dp)
            )
            SizeBox(height = 8.dp)
            Box(
                modifier = Modifier.weight(1F),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberQrBitmapPainter(user.getQrUrl()),
                    contentDescription = null,
                    modifier = Modifier.aspectRatio(1f)
                        .clip(RoundedCornerShape(20.dp)).background(Color.White).padding(10.dp),
                )
            }

            Image(
                painter = painterResource(id = R.drawable.ic_ppyy),
                contentDescription = null,
                modifier = Modifier.padding(8.dp).width(40.dp),
                contentScale = ContentScale.FillWidth
            )
            Text(
                text = stringResource(id = R.string.add_friend_scan_qr_to_add),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}