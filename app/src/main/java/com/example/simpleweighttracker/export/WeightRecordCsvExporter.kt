package com.example.simpleweighttracker.export

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.simpleweighttracker.model.WeightRecord
import java.io.File
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object WeightRecordCsvExporter {
    const val MimeType = "text/csv"

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    private val fileNameFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
    private val decimalFormatter = DecimalFormat(
        "0.0##",
        DecimalFormatSymbols(Locale.US)
    )

    fun createDefaultFileName(now: LocalDateTime = LocalDateTime.now()): String {
        return "weight_records_${now.format(fileNameFormatter)}.csv"
    }

    fun export(
        context: Context,
        records: List<WeightRecord>,
        headers: WeightRecordCsvHeaders
    ): Boolean {
        return runCatching {
            val fileName = createDefaultFileName()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                exportToDownloads(
                    context = context,
                    fileName = fileName,
                    records = records,
                    headers = headers
                )
            } else {
                exportToAppDocuments(
                    context = context,
                    fileName = fileName,
                    records = records,
                    headers = headers
                )
            }
        }.isSuccess
    }

    fun writeCsv(
        records: List<WeightRecord>,
        headers: WeightRecordCsvHeaders,
        outputStream: OutputStream
    ) {
        OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
            writer.write(buildCsv(records = records, headers = headers))
        }
    }

    fun buildCsv(
        records: List<WeightRecord>,
        headers: WeightRecordCsvHeaders
    ): String {
        return buildString {
            appendCsvRow(
                headers.date,
                headers.measuredWeight,
                headers.clothesWeight,
                headers.netWeight
            )
            records
                .sortedWith(compareBy<WeightRecord> { it.date }.thenBy { it.createdAt })
                .forEach { record ->
                    appendCsvRow(
                        record.date.format(dateFormatter),
                        decimalFormatter.format(record.measuredWeight),
                        decimalFormatter.format(record.clothesWeight),
                        decimalFormatter.format(record.netWeight)
                    )
                }
        }
    }

    private fun exportToDownloads(
        context: Context,
        fileName: String,
        records: List<WeightRecord>,
        headers: WeightRecordCsvHeaders
    ) {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, MimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = requireNotNull(
            contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        )
        val outputStream = requireNotNull(contentResolver.openOutputStream(uri))
        writeCsv(records = records, headers = headers, outputStream = outputStream)
    }

    private fun exportToAppDocuments(
        context: Context,
        fileName: String,
        records: List<WeightRecord>,
        headers: WeightRecordCsvHeaders
    ) {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?: context.filesDir
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, fileName)
        file.outputStream().use { outputStream ->
            writeCsv(records = records, headers = headers, outputStream = outputStream)
        }
    }

    private fun StringBuilder.appendCsvRow(vararg values: String) {
        append(values.joinToString(separator = ",") { value -> value.toCsvValue() })
        appendLine()
    }

    private fun String.toCsvValue(): String {
        val escaped = replace("\"", "\"\"")
        return if (escaped.any { char -> char == ',' || char == '"' || char == '\n' || char == '\r' }) {
            "\"$escaped\""
        } else {
            escaped
        }
    }
}

data class WeightRecordCsvHeaders(
    val date: String,
    val measuredWeight: String,
    val clothesWeight: String,
    val netWeight: String
)
