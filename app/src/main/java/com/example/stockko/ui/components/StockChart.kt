package com.example.stockko.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import androidx.compose.ui.platform.LocalContext
import com.example.stockko.data.model.StockHistoricalData
import java.text.DecimalFormat

/**
 * Stock Chart component using MPAndroidChart library
 *
 * @param chartData List of historical stock data points
 * @param modifier Modifier for the chart
 */
@Composable
fun StockChart(chartData: List<StockHistoricalData>, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { context ->
            createLineChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = { chart ->
            updateChartWithData(chart, chartData)
        }
    )
}

private fun createLineChart(context: Context): LineChart {
    return LineChart(context).apply {
        description.isEnabled = false
        legend.isEnabled = false
        setTouchEnabled(true)
        setScaleEnabled(true)
        setPinchZoom(true)
        setDrawGridBackground(false)
        setBackgroundColor(Color.TRANSPARENT)

        // Improve margins and padding
        setViewPortOffsets(60f, 40f, 60f, 80f)

        // Disable highlighting
        isHighlightPerTapEnabled = false
        isHighlightPerDragEnabled = false

        // Configure X axis with better styling
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawAxisLine(true)
            axisLineColor = Color.parseColor("#E0E0E0")
            axisLineWidth = 1f
            granularity = 1f
            labelCount = 6
            textColor = Color.parseColor("#757575")
            textSize = 11f
            typeface = Typeface.DEFAULT
            yOffset = 10f
        }

        // Configure left Y axis with currency formatting
        axisLeft.apply {
            setDrawGridLines(true)
            setDrawAxisLine(false)
            gridColor = Color.parseColor("#F0F0F0")
            gridLineWidth = 0.5f
            textColor = Color.parseColor("#757575")
            textSize = 11f
            typeface = Typeface.DEFAULT
            setLabelCount(6, false)
            xOffset = 10f
            valueFormatter = object : ValueFormatter() {
                private val format = DecimalFormat("$#,##0.00")
                override fun getFormattedValue(value: Float): String {
                    return format.format(value)
                }
            }
        }

        // Configure right Y axis
        axisRight.apply {
            isEnabled = false
        }

        // Smooth animation
        animateX(800)
    }
}

/**
 * Updates the chart with new data
 */
private fun updateChartWithData(chart: LineChart, data: List<StockHistoricalData>) {
    if (data.isEmpty()) return

    // Create entries for the line chart
    val entries = data.mapIndexed { index, item ->
        Entry(index.toFloat(), item.close.toFloat())
    }

    // Determine trend colors
    val isPositiveTrend = entries.first().y < entries.last().y
    val lineColor = if (isPositiveTrend) {
        Color.parseColor("#4CAF50") // Material Green
    } else {
        Color.parseColor("#F44336") // Material Red
    }
    val fillColor = if (isPositiveTrend) {
        Color.parseColor("#E8F5E8") // Light green
    } else {
        Color.parseColor("#FFEBEE") // Light red
    }

    // Configure the dataset with improved styling
    val dataSet = LineDataSet(entries, "Stock Price").apply {
        color = lineColor
        lineWidth = 3f
        setDrawCircles(false)
        setDrawValues(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        cubicIntensity = 0.15f
        fillAlpha = 30
        setFillColor(fillColor)
        setDrawFilled(true)

        // Add highlight styling
        setDrawHorizontalHighlightIndicator(false)
        setDrawVerticalHighlightIndicator(false)
    }

    // Format X-axis labels to show cleaner dates
    chart.xAxis.valueFormatter = IndexAxisValueFormatter(
        data.map { dateString ->
            // Extract day and month from date string (assuming YYYY-MM-DD format)
            val parts = dateString.date.split("-")
            if (parts.size >= 3) {
                "${parts[2]}/${parts[1]}" // DD/MM format
            } else {
                dateString.date.substringAfterLast('-') // Fallback to original
            }
        }
    )

    // Set the LineData to the chart
    chart.data = LineData(dataSet)

    // Optimize chart display
    chart.setVisibleXRangeMaximum(30f) // Show max 30 data points at once
    chart.moveViewToX(entries.size.toFloat()) // Move to latest data

    // Refresh the chart
    chart.invalidate()
}