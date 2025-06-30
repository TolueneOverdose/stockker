package com.example.stockko.ui.components


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.compose.ui.platform.LocalContext
import com.example.stockko.data.model.StockHistoricalData

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
        legend.isEnabled = true
        setTouchEnabled(true)
        setScaleEnabled(true)
        setPinchZoom(true)
        setDrawGridBackground(false)

        // Configure X axis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
            labelCount = 5
            textColor = Color.GRAY
        }

        // Configure left Y axis
        axisLeft.apply {
            setDrawGridLines(true)
            textColor = Color.GRAY
        }

        // Configure right Y axis
        axisRight.apply {
            isEnabled = false
        }

        // Animation
        animateX(1000)
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

    // Configure the dataset
    val dataSet = LineDataSet(entries, "Stock Price").apply {
        color = if (entries.first().y < entries.last().y) {
            Color.GREEN
        } else {
            Color.RED
        }
        lineWidth = 2f
        setDrawCircles(false)
        setDrawValues(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        cubicIntensity = 0.2f
        fillAlpha = 65
        fillColor = color
        setDrawFilled(true)
    }

    chart.xAxis.valueFormatter = IndexAxisValueFormatter(
        data.map { it.date.substringAfterLast('-') } // Show only day from the date
    )

    // Set the LineData to the chart
    chart.data = LineData(dataSet)

    // Refresh the chart
    chart.invalidate()
}