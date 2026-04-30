package com.example.simpleweighttracker.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.Path
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
import kotlin.math.ceil
import kotlin.math.floor

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChartScreen(
    uiState: WeightUiState,
    contentPadding: PaddingValues,
    onGraphRangeSelected: (GraphRange) -> Unit
) {
    val trendPerMonth = remember(uiState.dailyChartData) {
        calculateMonthlyTrendPerMonth(uiState.dailyChartData)
    }
    val dailyPoints = uiState.dailyChartData.map { point ->
        ChartPoint(
            label = WeightTrackerFormatters.formatShortDate(point.date),
            value = point.netWeight,
            secondaryValue = point.movingAverage
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GraphRange.entries.forEach { graphRange ->
                FilterChip(
                    selected = uiState.selectedGraphRange == graphRange,
                    onClick = { onGraphRangeSelected(graphRange) },
                    label = { Text(graphRange.label) }
                )
            }
        }

        WeightChart(
            title = "日ごとの最低記録体重",
            subtitle = "7日移動平均を重ねて表示します。",
            points = dailyPoints,
            trendLabel = trendPerMonth?.let { monthlyTrend ->
                "線形回帰 ${WeightTrackerFormatters.formatMonthlyTrend(monthlyTrend)}"
            }
        )
    }
}

@Composable
private fun WeightChart(
    title: String,
    subtitle: String,
    points: List<ChartPoint>,
    trendLabel: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (points.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("記録がありません")
                }
                return@Column
            }

            val allValues = buildList {
                addAll(points.map(ChartPoint::value))
                addAll(points.mapNotNull(ChartPoint::secondaryValue))
            }
            val rawMin = allValues.minOrNull() ?: 0.0
            val rawMax = allValues.maxOrNull() ?: rawMin
            val initialChartMin = floor(rawMin).toInt().coerceAtLeast(0)
            val initialChartMax = ceil(rawMax).toInt().coerceAtLeast(initialChartMin)
            val chartMin = if (initialChartMin == initialChartMax) {
                (initialChartMin - 1).coerceAtLeast(0)
            } else {
                initialChartMin
            }
            val chartMax = if (initialChartMin == initialChartMax) {
                initialChartMax + 1
            } else {
                initialChartMax
            }
            val chartTicks = (chartMin..chartMax).toList().asReversed()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .width(52.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    chartTicks.forEach { tick ->
                        Text(
                            text = WeightTrackerFormatters.formatIntegerValue(tick),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                WeightChartCanvas(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    points = points,
                    chartMin = chartMin.toDouble(),
                    chartMax = chartMax.toDouble(),
                    chartTicks = chartTicks
                )
            }

            ChartXAxis(points = points)

            if (points.any { it.secondaryValue != null }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(
                        color = MaterialTheme.colorScheme.primary,
                        label = "記録体重"
                    )
                    LegendItem(
                        color = MaterialTheme.colorScheme.tertiary,
                        label = "7日移動平均"
                    )
                }
            }

            if (trendLabel != null) {
                Text(
                    text = trendLabel,
                    modifier = Modifier.fillMaxWidth(),
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
    chartTicks: List<Int>
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary
    val outlineColor = MaterialTheme.colorScheme.outlineVariant
    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        val primaryStrokeWidth = with(density) { 2.dp.toPx() }
        val secondaryStrokeWidth = with(density) { 2.4.dp.toPx() }
        val gridStroke = with(density) { 1.dp.toPx() }
        val topPadding = with(density) { 8.dp.toPx() }
        val bottomPadding = with(density) { 10.dp.toPx() }

        val left = 0f
        val right = size.width
        val top = topPadding
        val bottom = size.height - bottomPadding
        val width = right - left
        val height = bottom - top
        val span = (chartMax - chartMin).coerceAtLeast(0.1)

        fun xPosition(index: Int): Float {
            return if (points.size == 1) {
                left + width / 2f
            } else {
                left + width * (index / points.lastIndex.toFloat())
            }
        }

        fun yPosition(value: Double): Float {
            val ratio = ((value - chartMin) / span).toFloat()
            return bottom - (ratio * height)
        }

        chartTicks.forEach { tick ->
            val y = yPosition(tick.toDouble())
            drawLine(
                color = outlineColor.copy(alpha = 0.7f),
                start = Offset(left, y),
                end = Offset(right, y),
                strokeWidth = gridStroke
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

        val primaryOffsets = points.mapIndexed { index, point ->
            Offset(xPosition(index), yPosition(point.value))
        }

        drawSeries(primaryOffsets, color = primaryColor, strokeWidth = primaryStrokeWidth)

        val secondarySegments = mutableListOf<List<Offset>>()
        var currentSegment = mutableListOf<Offset>()

        points.forEachIndexed { index, point ->
            val secondaryValue = point.secondaryValue
            if (secondaryValue == null) {
                if (currentSegment.isNotEmpty()) {
                    secondarySegments.add(currentSegment.toList())
                }
                currentSegment = mutableListOf()
            } else {
                currentSegment.add(Offset(xPosition(index), yPosition(secondaryValue)))
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
private fun ChartXAxis(points: List<ChartPoint>) {
    val labels = remember(points) {
        when (points.size) {
            0 -> emptyList()
            1 -> listOf(points.first().label)
            2 -> listOf(points.first().label, points.last().label)
            else -> listOf(
                points.first().label,
                points[points.lastIndex / 2].label,
                points.last().label
            )
        }
    }

    when (labels.size) {
        1 -> Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = labels.first(),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        2 -> Row(
            modifier = Modifier.fillMaxWidth(),
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
            modifier = Modifier.fillMaxWidth(),
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
    val label: String,
    val value: Double,
    val secondaryValue: Double? = null
)

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
