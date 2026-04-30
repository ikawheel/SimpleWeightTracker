package com.example.simpleweighttracker.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.simpleweighttracker.model.GraphRange
import com.example.simpleweighttracker.model.WeightRecord
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.ceil
import kotlin.math.floor

private val MovingAverageColor = Color(0xFFFFA726)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightTrackerApp(
    viewModel: WeightTrackerViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by rememberSaveable { mutableStateOf(AppTab.List) }
    var showEditorDialog by rememberSaveable { mutableStateOf(false) }

    MaterialTheme {
        val navigationBackground = MaterialTheme.colorScheme.surfaceContainer
        val statusBarBackground = MaterialTheme.colorScheme.primary

        if (showEditorDialog) {
            RecordEditorDialog(
                uiState = uiState,
                onDateChanged = viewModel::updateDate,
                onMeasuredWeightChanged = viewModel::updateMeasuredWeight,
                onMeasuredWeightFocused = viewModel::clearMeasuredWeightDefaultOnFocus,
                onMeasuredWeightUnfocused = viewModel::restoreMeasuredWeightDefaultOnBlur,
                onClothesWeightChanged = viewModel::updateClothesWeight,
                onClothesWeightFocused = viewModel::clearClothesWeightDefaultOnFocus,
                onClothesWeightUnfocused = viewModel::restoreClothesWeightDefaultOnBlur,
                onSave = {
                    if (viewModel.saveRecord()) {
                        showEditorDialog = false
                    }
                },
                onDismiss = {
                    viewModel.cancelEditing()
                    showEditorDialog = false
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = navigationBackground,
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(navigationBackground)
                                .navigationBarsPadding()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .background(navigationBackground)
                                    .padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AppTab.entries.forEach { tab ->
                                    val selected = selectedTab == tab
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .background(navigationBackground)
                                            .clickable { selectedTab = tab }
                                            .padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = tab.title,
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (selected) {
                                                    MaterialTheme.colorScheme.onSurface
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                }
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .height(2.dp)
                                                    .width(36.dp)
                                                    .background(
                                                        color = if (selected) {
                                                            MaterialTheme.colorScheme.primary
                                                        } else {
                                                            androidx.compose.ui.graphics.Color.Transparent
                                                        },
                                                        shape = RoundedCornerShape(999.dp)
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                when (selectedTab) {
                    AppTab.List -> RecordsScreen(
                        records = uiState.records,
                        contentPadding = innerPadding,
                        onAdd = {
                            viewModel.startCreating()
                            showEditorDialog = true
                        },
                        onInsertDebugData = viewModel::insertDebugRecords,
                        onDeleteAllData = viewModel::deleteAllRecords,
                        onEdit = { record ->
                            viewModel.startEditing(record)
                            showEditorDialog = true
                        },
                        onDelete = viewModel::deleteRecord
                    )

                    AppTab.Chart -> ChartScreen(
                        uiState = uiState,
                        contentPadding = innerPadding,
                        onGraphRangeSelected = viewModel::selectGraphRange
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(statusBarBackground)
            )
        }
    }
}

private enum class AppTab(
    val title: String
) {
    List(title = "一覧"),
    Chart(title = "グラフ")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordEditorDialog(
    uiState: WeightUiState,
    onDateChanged: (java.time.LocalDate) -> Unit,
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
                    Text("決定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("キャンセル")
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
                    label = { Text("体重計の値") },
                    suffix = { Text("kg") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    isError = formState.measuredWeightError != null,
                    supportingText = {
                        Text(formState.measuredWeightError ?: "")
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
                    label = { Text("服の重さ") },
                    suffix = { Text("kg") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    isError = formState.clothesWeightError != null,
                    supportingText = {
                        Text(formState.clothesWeightError ?: "次回入力時はこの値が初期値になります")
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
                Text(if (formState.isEditing) "更新する" else "保存する")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        }
    )
}

@Composable
private fun DateSelector(
    date: java.time.LocalDate,
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
    generalError: String?
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
                text = "記録体重プレビュー",
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
                    text = generalError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun RecordsScreen(
    records: List<WeightRecord>,
    contentPadding: PaddingValues,
    onAdd: () -> Unit,
    onInsertDebugData: () -> Unit,
    onDeleteAllData: () -> Unit,
    onEdit: (WeightRecord) -> Unit,
    onDelete: (WeightRecord) -> Unit
) {
    var pendingDelete by remember { mutableStateOf<WeightRecord?>(null) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("記録を削除しますか") },
            text = {
                Text(
                    "日付：${WeightTrackerFormatters.formatFullDate(pendingDelete!!.date)}"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(pendingDelete!!)
                        pendingDelete = null
                    }
                ) {
                    Text("削除")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("キャンセル")
                }
            }
        )
    }

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("全データを削除しますか") },
            text = {
                Text("この操作は取り消せません。保存済みの体重記録をすべて削除します。")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteAllData()
                        showDeleteAllDialog = false
                    }
                ) {
                    Text("全削除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 220.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (records.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "記録がありません",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                items(records, key = WeightRecord::id) { record ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${WeightTrackerFormatters.formatFullDate(record.date)} ${WeightTrackerFormatters.formatTime(record.updatedAt)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = WeightTrackerFormatters.formatWeight(record.netWeight),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = { onEdit(record) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                    ) {
                                        Text("編集")
                                    }
                                    TextButton(
                                        onClick = { pendingDelete = record },
                                        modifier = Modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                                        contentPadding = PaddingValues(start = 8.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
                                    ) {
                                        Text("削除")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp,
            shadowElevation = 12.dp,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAdd,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("体重を記入")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onInsertDebugData,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("デバッグ投入")
                    }
                    Button(
                        onClick = { showDeleteAllDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("データ全削除")
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartScreen(
    uiState: WeightUiState,
    contentPadding: PaddingValues,
    onGraphRangeSelected: (GraphRange) -> Unit
) {
    val chartDateRange = remember(uiState.records, uiState.selectedGraphRange) {
        buildChartDateRange(
            records = uiState.records,
            graphRange = uiState.selectedGraphRange
        )
    }
    val trendPerMonth = remember(uiState.dailyChartData) {
        calculateMonthlyTrendPerMonth(uiState.dailyChartData)
    }
    val dailyPoints = uiState.dailyChartData.map { point ->
        ChartPoint(
            date = point.date,
            value = point.netWeight,
            secondaryValue = point.movingAverage
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WeightChart(
            modifier = Modifier.weight(1f),
            points = dailyPoints,
            dateRange = chartDateRange,
            trendLabel = trendPerMonth?.let { monthlyTrend ->
                "体重増減の傾向 ${WeightTrackerFormatters.formatMonthlyTrend(monthlyTrend)}"
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            GraphRange.entries.forEach { graphRange ->
                GraphRangeButton(
                    graphRange = graphRange,
                    selected = uiState.selectedGraphRange == graphRange,
                    onClick = { onGraphRangeSelected(graphRange) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GraphRangeButton(
    graphRange: GraphRange,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(999.dp)
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .height(36.dp)
            .background(backgroundColor, shape)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = graphRange.label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WeightChart(
    modifier: Modifier = Modifier,
    points: List<ChartPoint>,
    dateRange: ChartDateRange,
    trendLabel: String?
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (points.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("記録がありません")
            }
            ChartXAxis(
                dateRange = dateRange,
                modifier = Modifier.padding(start = 44.dp)
            )
            return@Column
        }

        val allValues = buildList {
            addAll(points.map(ChartPoint::value))
            addAll(points.mapNotNull(ChartPoint::secondaryValue))
        }
        val rawMin = allValues.minOrNull() ?: 0.0
        val rawMax = allValues.maxOrNull() ?: rawMin
        val initialChartMinHalf = floor(rawMin * 2.0).toInt().coerceAtLeast(0)
        val initialChartMaxHalf = ceil(rawMax * 2.0).toInt().coerceAtLeast(initialChartMinHalf)
        val chartMinHalf = if (initialChartMinHalf == initialChartMaxHalf) {
            (initialChartMinHalf - 1).coerceAtLeast(0)
        } else {
            initialChartMinHalf
        }
        val chartMaxHalf = if (initialChartMinHalf == initialChartMaxHalf) {
            initialChartMaxHalf + 1
        } else {
            initialChartMaxHalf
        }
        val chartMin = chartMinHalf / 2.0
        val chartMax = chartMaxHalf / 2.0
        val chartTicks = (chartMinHalf..chartMaxHalf)
            .map { halfStep -> halfStep / 2.0 }
            .asReversed()
        val dottedChartTicks = buildDottedQuarterTicks(
            chartMin = chartMin,
            chartMax = chartMax
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .width(36.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                chartTicks.forEach { tick ->
                    Text(
                        text = WeightTrackerFormatters.formatAxisValue(tick),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
            )

            WeightChartCanvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                points = points,
                chartMin = chartMin.toDouble(),
                chartMax = chartMax.toDouble(),
                dateRange = dateRange,
                chartTicks = chartTicks,
                dottedChartTicks = dottedChartTicks
            )
        }

        ChartXAxis(
            dateRange = dateRange,
            modifier = Modifier.padding(start = 44.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (points.any { it.secondaryValue != null }) {
                    LegendItem(
                        color = MaterialTheme.colorScheme.primary,
                        label = "記録体重"
                    )
                LegendItem(
                    color = MovingAverageColor,
                    label = "7日移動平均"
                )
                }
            }

            if (trendLabel != null) {
                Text(
                    text = trendLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun WeightChartCanvas(
    modifier: Modifier,
    points: List<ChartPoint>,
    chartMin: Double,
    chartMax: Double,
    dateRange: ChartDateRange,
    chartTicks: List<Double>,
    dottedChartTicks: List<Double>
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MovingAverageColor
    val outlineColor = MaterialTheme.colorScheme.outlineVariant
    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        val primaryStrokeWidth = with(density) { 2.dp.toPx() }
        val secondaryStrokeWidth = with(density) { 2.4.dp.toPx() }
        val gridStroke = with(density) { 1.dp.toPx() }
        val integerGridStroke = with(density) { 2.dp.toPx() }
        val dottedGridEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(
                with(density) { 3.dp.toPx() },
                with(density) { 4.dp.toPx() }
            ),
            phase = 0f
        )
        val topPadding = with(density) { 8.dp.toPx() }
        val bottomPadding = with(density) { 10.dp.toPx() }

        val left = 0f
        val right = size.width
        val top = topPadding
        val bottom = size.height - bottomPadding
        val width = right - left
        val height = bottom - top
        val span = (chartMax - chartMin).coerceAtLeast(0.1)

        val dateSpanDays = ChronoUnit.DAYS
            .between(dateRange.start, dateRange.end)
            .coerceAtLeast(1L)

        fun xPosition(date: LocalDate): Float {
            val elapsedDays = ChronoUnit.DAYS
                .between(dateRange.start, date)
                .coerceIn(0L, dateSpanDays)
            return left + width * (elapsedDays.toFloat() / dateSpanDays.toFloat())
        }

        fun yPosition(value: Double): Float {
            val ratio = ((value - chartMin) / span).toFloat()
            return bottom - (ratio * height)
        }

        dottedChartTicks.forEach { tick ->
            val y = yPosition(tick)
            drawLine(
                color = outlineColor.copy(alpha = 0.45f),
                start = Offset(left, y),
                end = Offset(right, y),
                strokeWidth = gridStroke,
                pathEffect = dottedGridEffect
            )
        }

        chartTicks.forEach { tick ->
            val y = yPosition(tick)
            val stroke = if (tick % 1.0 == 0.0) {
                integerGridStroke
            } else {
                gridStroke
            }
            drawLine(
                color = outlineColor.copy(alpha = 0.7f),
                start = Offset(left, y),
                end = Offset(right, y),
                strokeWidth = stroke
            )
        }

        drawLine(
            color = outlineColor,
            start = Offset(left, top),
            end = Offset(left, bottom),
            strokeWidth = gridStroke
        )
        drawLine(
            color = outlineColor,
            start = Offset(left, bottom),
            end = Offset(right, bottom),
            strokeWidth = gridStroke
        )

        val primaryOffsets = points.map { point ->
            Offset(xPosition(point.date), yPosition(point.value))
        }

        drawSeries(primaryOffsets, color = primaryColor, strokeWidth = primaryStrokeWidth)

        val secondarySegments = mutableListOf<List<Offset>>()
        var currentSegment = mutableListOf<Offset>()

        points.forEach { point ->
            val secondaryValue = point.secondaryValue
            if (secondaryValue == null) {
                if (currentSegment.isNotEmpty()) {
                    secondarySegments.add(currentSegment.toList())
                }
                currentSegment = mutableListOf()
            } else {
                currentSegment.add(Offset(xPosition(point.date), yPosition(secondaryValue)))
            }
        }

        if (currentSegment.isNotEmpty()) {
            secondarySegments.add(currentSegment.toList())
        }

        secondarySegments.forEach { offsets ->
            drawSeries(
                offsets = offsets,
                color = secondaryColor,
                strokeWidth = secondaryStrokeWidth
            )
        }
    }
}

private fun buildDottedQuarterTicks(
    chartMin: Double,
    chartMax: Double
): List<Double> {
    val minQuarter = ceil(chartMin * 4.0).toInt()
    val maxQuarter = floor(chartMax * 4.0).toInt()
    return (minQuarter..maxQuarter)
        .filter { quarterStep -> quarterStep % 2 != 0 }
        .map { quarterStep -> quarterStep / 4.0 }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSeries(
    offsets: List<Offset>,
    color: androidx.compose.ui.graphics.Color,
    strokeWidth: Float
) {
    if (offsets.isEmpty()) {
        return
    }

    if (offsets.size == 1) {
        drawCircle(
            color = color,
            radius = strokeWidth,
            center = offsets.first()
        )
        return
    }

    val path = Path().apply {
        moveTo(offsets.first().x, offsets.first().y)
        offsets.drop(1).forEach { offset ->
            lineTo(offset.x, offset.y)
        }
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

@Composable
private fun ChartXAxis(
    dateRange: ChartDateRange,
    modifier: Modifier = Modifier
) {
    val labels = remember(dateRange) {
        val dateSpanDays = ChronoUnit.DAYS
            .between(dateRange.start, dateRange.end)
            .coerceAtLeast(0L)
        when (dateSpanDays) {
            0L -> listOf(WeightTrackerFormatters.formatShortDate(dateRange.start))
            1L -> listOf(
                WeightTrackerFormatters.formatShortDate(dateRange.start),
                WeightTrackerFormatters.formatShortDate(dateRange.end)
            )
            else -> listOf(
                WeightTrackerFormatters.formatShortDate(dateRange.start),
                WeightTrackerFormatters.formatShortDate(dateRange.start.plusDays(dateSpanDays / 2L)),
                WeightTrackerFormatters.formatShortDate(dateRange.end)
            )
        }
    }

    when (labels.size) {
        1 -> Box(
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                text = labels.first(),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        2 -> Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = labels[0],
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = labels[1],
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = labels[2],
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun LegendItem(
    color: androidx.compose.ui.graphics.Color,
    label: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color, shape = CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private data class ChartPoint(
    val date: LocalDate,
    val value: Double,
    val secondaryValue: Double? = null
)

private data class ChartDateRange(
    val start: LocalDate,
    val end: LocalDate
)

private fun buildChartDateRange(
    records: List<WeightRecord>,
    graphRange: GraphRange
): ChartDateRange {
    val fallbackEnd = LocalDate.now()
    val end = records.maxByOrNull { record -> record.date.toEpochDay() }?.date ?: fallbackEnd
    val start = when (graphRange) {
        GraphRange.OneMonth -> end.minusMonths(1)
        GraphRange.ThreeMonths -> end.minusMonths(3)
        GraphRange.SixMonths -> end.minusMonths(6)
        GraphRange.OneYear -> end.minusYears(1)
        GraphRange.All -> records.minByOrNull { record -> record.date.toEpochDay() }?.date ?: end
    }
    return ChartDateRange(start = start, end = end)
}

private fun calculateMonthlyTrendPerMonth(
    points: List<com.example.simpleweighttracker.model.DailyWeightPoint>
): Double? {
    if (points.size < 2) {
        return null
    }

    val baseEpochDay = points.first().date.toEpochDay().toDouble()
    val xs = points.map { point -> point.date.toEpochDay().toDouble() - baseEpochDay }
    val ys = points.map { point -> point.netWeight }
    val meanX = xs.average()
    val meanY = ys.average()
    val numerator = xs.indices.sumOf { index ->
        (xs[index] - meanX) * (ys[index] - meanY)
    }
    val denominator = xs.sumOf { x ->
        val diff = x - meanX
        diff * diff
    }

    if (denominator == 0.0) {
        return null
    }

    val slopePerDay = numerator / denominator
    val slopePerMonth = slopePerDay * 30.44
    return WeightTrackerFormatters.roundToTwoDecimals(slopePerMonth)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun rememberDatePickerStateWithDate(
    date: java.time.LocalDate
): androidx.compose.material3.DatePickerState {
    val initialDateMillis = remember(date) {
        WeightTrackerFormatters.localDateToUtcMillis(date)
    }
    return androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis
    )
}
