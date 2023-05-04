package com.noljanolja.android.features.home.play.playscreen.composable

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.noljanolja.android.R
import com.noljanolja.android.common.base.launchInMainIO
import com.noljanolja.android.ui.composable.BackPressHandler
import com.noljanolja.android.ui.composable.CircleAvatar
import com.noljanolja.android.ui.composable.SizeBox
import com.noljanolja.android.util.findActivity
import com.noljanolja.core.user.domain.model.User
import org.koin.androidx.compose.get

@Composable
fun CommentInput(
    me: User,
    onSend: (String, String) -> Unit,
    onError: (Throwable) -> Unit = {},
) {
    var comment by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
    val activity = context.findActivity()!!
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
        .requestIdToken(stringResource(id = R.string.web_client_id)).requestScopes(YOUTUBE_SCOPE).build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    val googleSignInLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)
                getTokenFromAccount(
                    activity,
                    account,
                    onError = { error ->
                        onError(error)
                    }
                ) { token ->
                    onSend(comment, token)
                    comment = ""
                    focusManager.clearFocus()
                }
            }
        )

    var lastFocusState by remember { mutableStateOf(false) }
    BackPressHandler(lastFocusState) {
        focusManager.clearFocus()
    }
    val send = {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) {
            val newAccount = if (account.isExpired) {
                val task = googleSignInClient.silentSignIn()
                task.getResult(ApiException::class.java)
            } else {
                account
            }
            getTokenFromAccount(
                activity,
                newAccount,
                onError = { error ->
                    onError(error)
                }
            ) { token ->
                onSend(comment, token)
                comment = ""
                focusManager.clearFocus()
            }
        } else {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleAvatar(user = me, size = 24.dp)
        SizeBox(width = 12.dp)
        Box(
            modifier = Modifier.weight(1f)
                .height(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(start = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { state ->
                        lastFocusState = state.isFocused
                    },
                value = comment,
                onValueChange = { comment = it },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    send.invoke()
                }),
                cursorBrush = SolidColor(LocalContentColor.current),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            if (comment.isEmpty()) {
                Text(
                    stringResource(id = R.string.video_detail_enter_comment),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }

        AnimatedVisibility(visible = lastFocusState) {
            val enable = comment.isNotBlank()
            IconButton(
                onClick = send,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .then(Modifier.size(27.dp))
                    .clip(CircleShape)
                    .background(
                        color = if (enable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                    .padding(3.dp),
                enabled = enable
            ) {
                Icon(
                    Icons.Filled.Send,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }
    }
}

private fun getTokenFromAccount(
    activity: Activity,
    account: GoogleSignInAccount,
    onError: (Throwable) -> Unit,
    onTokenResult: (String) -> Unit,
) {
    launchInMainIO(onError = onError) {
        if (!GoogleSignIn.hasPermissions(
                GoogleSignIn.getLastSignedInAccount(activity),
                YOUTUBE_SCOPE
            )
        ) {
            GoogleSignIn.requestPermissions(
                activity,
                GOOGLE_REQUEST_PERMISSION_CODE,
                GoogleSignIn.getLastSignedInAccount(activity),
                YOUTUBE_SCOPE
            )
        }

        val token = GoogleAuthUtil.getToken(
            activity,
            account.account!!,
            "oauth2:https://www.googleapis.com/auth/youtube.force-ssl",
        )
        onTokenResult.invoke(token)
    }
}

private const val GOOGLE_REQUEST_PERMISSION_CODE = 1000
private val YOUTUBE_SCOPE = Scope("https://www.googleapis.com/auth/youtube.force-ssl")