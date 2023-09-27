package com.noljanolja.android.features.auth.updateprofile.components

import android.app.DatePickerDialog
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.noljanolja.android.ui.composable.TextField
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DoBInput(
    modifier: Modifier,
    focusManager: FocusManager,
    label: String,
    dob: LocalDate?,
    onDoBChange: (LocalDate) -> Unit,
) {
    val dobInteractionSource = remember { MutableInteractionSource() }

    if (dobInteractionSource.collectIsPressedAsState().value) {
        focusManager.clearFocus(true)
        ShowDatePicker(dob, onDoBChange)
    }

    TextField(
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        value = dob?.format(DateTimeFormatter.ISO_LOCAL_DATE).orEmpty(),
        onValueChange = { },
        label = { Text(label) },
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = LocalContentColor.current,
        ),
        singleLine = true,
        readOnly = true,
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
            focusedLabelColor = MaterialTheme.colorScheme.secondary
        ),
        interactionSource = dobInteractionSource,
        contentPadding = TextFieldDefaults.textFieldWithLabelPadding(start = 0.dp, end = 0.dp)
    )
}

@Composable
private fun ShowDatePicker(
    selectedDate: LocalDate?,
    onDateSelect: (LocalDate) -> Unit,
) {
    with(selectedDate ?: LocalDate.now()) {
        DatePickerDialog(
            LocalContext.current,
            { _, year, month, dayOfMonth ->
                onDateSelect(LocalDate.of(year, month + 1, dayOfMonth))
            },
            year,
            monthValue - 1,
            dayOfMonth
        ).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
    }
}