package com.ikeansoft.simpleweighttracker.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ikeansoft.simpleweighttracker.R
import com.ikeansoft.simpleweighttracker.ui.WeightUiState

@Composable
internal fun MovingAverageSettingsScreen(
    uiState: WeightUiState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onMovingAverageDaysChanged: (Int) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var daysInput by rememberSaveable(uiState.chartColorSettings.movingAverageDays) {
        mutableStateOf(uiState.chartColorSettings.movingAverageDays.toString())
    }
    val inputState = buildMovingAverageDaysInputState(daysInput)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(
            onClick = onBack,
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
        ) {
            Text(stringResource(R.string.action_back))
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_moving_average),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = daysInput,
                    onValueChange = { input ->
                        daysInput = normalizeMovingAverageDaysInput(input)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.moving_average_days_label)) },
                    suffix = { Text(stringResource(R.string.day_suffix)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    isError = inputState.errorResId != null,
                    supportingText = {
                        Text(stringResourceOrEmpty(inputState.errorResId))
                    }
                )

                Button(
                    onClick = {
                        inputState.value?.let { days ->
                            focusManager.clearFocus()
                            onMovingAverageDaysChanged(days)
                        }
                    },
                    enabled = inputState.errorResId == null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(stringResource(R.string.action_save))
                }
            }
        }
    }
}

@Composable
private fun stringResourceOrEmpty(@StringRes resId: Int?): String {
    return resId?.let { stringResource(it) } ?: ""
}
