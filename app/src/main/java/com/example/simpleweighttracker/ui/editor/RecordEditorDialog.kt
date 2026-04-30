package com.example.simpleweighttracker.ui.editor

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.simpleweighttracker.R
import com.example.simpleweighttracker.ui.WeightTrackerFormatters
import com.example.simpleweighttracker.ui.WeightUiState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordEditorDialog(
    uiState: WeightUiState,
    onDateChanged: (LocalDate) -> Unit,
    onMeasuredWeightChanged: (String) -> Unit,
    onMeasuredWeightFocused: () -> Unit,
    onMeasuredWeightUnfocused: () -> Unit,
    onClothesWeightChanged: (String) -> Unit,
    onClothesWeightFocused: () -> Unit,
    onClothesWeightUnfocused: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val formState = uiState.formState
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerStateWithDate(formState.date)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = datePickerState.selectedDateMillis
                            ?.let(WeightTrackerFormatters::utcMillisToLocalDate)
                            ?: formState.date
                        onDateChanged(selectedDate)
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DateSelector(
                    date = formState.date,
                    onOpenPicker = { showDatePicker = true }
                )

                OutlinedTextField(
                    value = formState.measuredWeightInput,
                    onValueChange = onMeasuredWeightChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                onMeasuredWeightFocused()
                            } else {
                                onMeasuredWeightUnfocused()
                            }
                        },
                    label = { Text(stringResource(R.string.measured_weight_label)) },
                    suffix = { Text("kg") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    isError = formState.measuredWeightError != null,
                    supportingText = {
                        Text(stringResourceOrEmpty(formState.measuredWeightError))
                    }
                )

                OutlinedTextField(
                    value = formState.clothesWeightInput,
                    onValueChange = onClothesWeightChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                onClothesWeightFocused()
                            } else {
                                onClothesWeightUnfocused()
                            }
                        },
                    label = { Text(stringResource(R.string.clothes_weight_label)) },
                    suffix = { Text("kg") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    isError = formState.clothesWeightError != null,
                    supportingText = {
                        Text(
                            formState.clothesWeightError?.let { errorResId ->
                                stringResource(errorResId)
                            } ?: stringResource(R.string.clothes_weight_supporting_text)
                        )
                    }
                )

                NetWeightPreviewCard(
                    preview = formState.netWeightPreview,
                    generalError = formState.generalError
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    stringResource(
                        if (formState.isEditing) {
                            R.string.action_update
                        } else {
                            R.string.action_save
                        }
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun DateSelector(
    date: LocalDate,
    onOpenPicker: () -> Unit
) {
    Column {
        OutlinedButton(
            onClick = onOpenPicker,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(WeightTrackerFormatters.formatFullDate(date))
        }
    }
}

@Composable
private fun NetWeightPreviewCard(
    preview: Double?,
    generalError: Int?
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.net_weight_preview_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = preview?.let(WeightTrackerFormatters::formatWeight) ?: "-- kg",
                style = MaterialTheme.typography.headlineMedium,
                color = if (preview != null && preview > 0.0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            if (generalError != null) {
                Text(
                    text = stringResource(generalError),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun stringResourceOrEmpty(@StringRes resId: Int?): String {
    return resId?.let { stringResource(it) } ?: ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun rememberDatePickerStateWithDate(
    date: LocalDate
): DatePickerState {
    val initialDateMillis = remember(date) {
        WeightTrackerFormatters.localDateToUtcMillis(date)
    }
    return rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis
    )
}
