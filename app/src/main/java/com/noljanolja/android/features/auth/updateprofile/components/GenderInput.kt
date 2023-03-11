package com.noljanolja.android.features.auth.updateprofile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.*
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.noljanolja.android.R
import com.noljanolja.core.user.domain.model.Gender

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GenderInput(
    modifier: Modifier,
    label: String,
    gender: String?,
    genders: List<String>,
    onGenderChange: (String) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val displayGenders = remember {
        mutableStateMapOf(
            Gender.MALE.name to context.getString(R.string.update_profile_gender_male),
            Gender.FEMALE.name to context.getString(R.string.update_profile_gender_female),
            Gender.OTHER.name to context.getString(R.string.update_profile_gender_other),
        )
    }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            value = displayGenders[gender].orEmpty(),
            onValueChange = { },
            label = { Text(label) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = LocalContentColor.current,
            ),
            singleLine = true,
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            genders.forEach { gender ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onGenderChange(gender)
                    },
                ) {
                    Text(
                        displayGenders[gender].orEmpty(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}