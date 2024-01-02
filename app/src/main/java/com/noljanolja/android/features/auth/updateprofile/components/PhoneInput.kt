package com.noljanolja.android.features.auth.updateprofile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R
import com.noljanolja.android.ui.composable.Expanded
import com.noljanolja.android.ui.composable.CustomTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneInput(
    modifier: Modifier,
    focusManager: FocusManager,
    label: String,
    phone: String,
    onPhoneChange: (String) -> Unit,
) {
    Column(modifier = modifier) {
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text(label) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = LocalContentColor.current,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                focusedLabelColor = MaterialTheme.colorScheme.secondary,
                cursorColor = MaterialTheme.colorScheme.secondary
            ),
            contentPadding = TextFieldDefaults.textFieldWithLabelPadding(start = 0.dp, end = 0.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
        ) {
            Text(
                stringResource(R.string.update_profile_name_required),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
            )
            Expanded()
        }
    }
}