package com.noljanolja.android.features.auth.updateprofile

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.noljanolja.android.R
import com.noljanolja.android.features.auth.updateprofile.components.AvatarInput
import com.noljanolja.android.features.auth.updateprofile.components.DoBInput
import com.noljanolja.android.features.auth.updateprofile.components.GenderInput
import com.noljanolja.android.features.auth.updateprofile.components.NameInput
import com.noljanolja.android.ui.composable.ErrorDialog
import com.noljanolja.android.ui.composable.LoadingDialog
import com.noljanolja.core.user.domain.model.Gender
import kotlinx.datetime.toKotlinLocalDate
import org.koin.androidx.compose.getViewModel
import java.time.LocalDate

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun UpdateProfileScreen(
    viewModel: UpdateProfileViewModel = getViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    UpdateProfileContent(
        uiState = uiState,
        handleEvent = viewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileContent(
    uiState: UpdateProfileUiState,
    handleEvent: (UpdateProfileEvent) -> Unit,
) {
    val genders =
        remember { mutableStateListOf(Gender.MALE.name, Gender.FEMALE.name, Gender.OTHER.name) }
    var showAvatarInputDialog by rememberSaveable { mutableStateOf(false) }
    var avatar by rememberSaveable { mutableStateOf<Uri?>(null) }
    val maxNameLength = rememberSaveable { 20 }
    var name by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf<String?>(null) }
    var dob by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    val isRegisterEnable = name.isNotBlank()

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

//    when (registerViewState) {
//        is ViewState.Error -> {
//            loading = false
//            error = registerViewState.cause
//        }
//        is ViewState.Loading -> {
//            loading = true
//            error = null
//        }
//        is ViewState.Idle -> {
//            loading = false
//            error = null
//        }
//        else -> {
//            loading = false
//            error = null
//        }
//    }

    LaunchedEffect(avatar) {
        avatar?.let {
            context.contentResolver.openInputStream(it)?.let {
                handleEvent(UpdateProfileEvent.UploadAvatar(it.readBytes()))
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { focusManager.clearFocus(true) }
                ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.BottomEnd,
                ) {
                    AsyncImage(
                        avatar ?: R.drawable.placeholder_avatar,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(32.dp))
                            .clickable { showAvatarInputDialog = true },
                        contentScale = ContentScale.Crop,
                    )
                    Image(
                        Icons.Outlined.Camera,
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { showAvatarInputDialog = true }
                            .background(MaterialTheme.colorScheme.secondary, CircleShape),
                        contentScale = ContentScale.Inside,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                NameInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    focusManager = focusManager,
                    label = stringResource(R.string.update_profile_name),
                    name = name,
                    maxNameLength = maxNameLength,
                    onNameChange = { if (it.trim().length <= maxNameLength) name = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    DoBInput(
                        modifier = Modifier.weight(1f),
                        focusManager = focusManager,
                        label = stringResource(R.string.update_profile_dob),
                        dob = dob,
                        onDoBChange = { dob = it }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    GenderInput(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.update_profile_gender),
                        gender = gender,
                        genders = genders,
                        onGenderChange = { gender = it },
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        handleEvent(
                            UpdateProfileEvent.Update(
                                name.trim(),
                                dob?.toKotlinLocalDate(),
                                gender?.let { Gender.valueOf(it) }
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                    enabled = isRegisterEnable
                ) {
                    Text(text = stringResource(R.string.common_ok))
                }
            }
        }
    }

    AvatarInput(
        isShown = showAvatarInputDialog,
        onAvatarInput = { uri ->
            uri?.let { avatar = it }
            showAvatarInputDialog = false
        },
    )

    LoadingDialog(
        title = stringResource(R.string.common_continue),
        isLoading = uiState.loading,
    )
    uiState.error?.let {
        ErrorDialog(
            showError = true,
            title = stringResource(R.string.common_error_title),
            description = it.message
                ?: stringResource(R.string.common_error_description),
            onDismiss = { handleEvent(UpdateProfileEvent.DismissError) }
        )
    }
}