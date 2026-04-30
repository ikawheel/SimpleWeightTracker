package com.example.simpleweighttracker.ui.chart

internal val DefaultRecordLineColorArgb = 0xFF1976D2.toInt()

internal val ChartLineColorPalette = listOf(
    ChartLineColorOption(colorArgb = 0xFF64B5F6.toInt()),
    ChartLineColorOption(colorArgb = 0xFF81C784.toInt()),
    ChartLineColorOption(colorArgb = 0xFFFFD54F.toInt()),
    ChartLineColorOption(colorArgb = 0xFFFFB74D.toInt()),
    ChartLineColorOption(colorArgb = 0xFFE57373.toInt()),
    ChartLineColorOption(colorArgb = 0xFFBA68C8.toInt()),
    ChartLineColorOption(colorArgb = DefaultRecordLineColorArgb),
    ChartLineColorOption(colorArgb = 0xFF2E7D32.toInt()),
    ChartLineColorOption(colorArgb = 0xFFFBC02D.toInt()),
    ChartLineColorOption(colorArgb = 0xFFFFA726.toInt()),
    ChartLineColorOption(colorArgb = 0xFFC62828.toInt()),
    ChartLineColorOption(colorArgb = 0xFF7B1FA2.toInt()),
    ChartLineColorOption(colorArgb = 0xFF0D47A1.toInt()),
    ChartLineColorOption(colorArgb = 0xFF1B5E20.toInt()),
    ChartLineColorOption(colorArgb = 0xFFF57F17.toInt()),
    ChartLineColorOption(colorArgb = 0xFFE65100.toInt()),
    ChartLineColorOption(colorArgb = 0xFF8E0000.toInt()),
    ChartLineColorOption(colorArgb = 0xFF4A148C.toInt())
)

internal data class ChartLineColorOption(
    val colorArgb: Int
)
