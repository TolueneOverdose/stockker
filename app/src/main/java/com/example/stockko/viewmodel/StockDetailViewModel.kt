package com.example.stockko.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockko.data.model.StockHistoricalData
import com.example.stockko.data.repository.StockRepository
import com.example.stockko.data.model.StockItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class StockDetailState(
    val stock: StockItem? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)


class StockDetailViewModel : ViewModel() {
    private val repository = StockRepository()

    private val _stockState = MutableStateFlow(StockDetailState(isLoading = true))
    val stockState: StateFlow<StockDetailState> = _stockState.asStateFlow()

    private val _chartData = MutableStateFlow<List<StockHistoricalData>>(emptyList())
    val chartData: StateFlow<List<StockHistoricalData>> = _chartData.asStateFlow()

    private val _timeRange = MutableStateFlow("1M")
    val timeRange: StateFlow<String> = _timeRange.asStateFlow()

    private val mockStocks = mapOf(
        "AAPL" to StockItem(
            symbol = "AAPL",
            name = "Apple Inc.",
            price = "$184.92",
            change = "+2.35",
            changePercent = "+1.29%",
            marketCap = "2.85T",
            sector = "Technology"
        ),
        "MSFT" to StockItem(
            symbol = "MSFT",
            name = "Microsoft Corporation",
            price = "$416.78",
            change = "+3.21",
            changePercent = "+0.78%",
            marketCap = "3.1T",
            sector = "Technology"
        ),
        "GOOG" to StockItem(
            symbol = "GOOG",
            name = "Alphabet Inc.",
            price = "$178.46",
            change = "-1.24",
            changePercent = "-0.69%",
            marketCap = "2.2T",
            sector = "Technology"
        ),
        "AMZN" to StockItem(
            symbol = "AMZN",
            name = "Amazon.com Inc.",
            price = "$183.92",
            change = "+1.87",
            changePercent = "+1.03%",
            marketCap = "1.9T",
            sector = "Consumer Cyclical"
        ),
        "TSLA" to StockItem(
            symbol = "TSLA",
            name = "Tesla, Inc.",
            price = "$172.63",
            change = "-4.92",
            changePercent = "-2.77%",
            marketCap = "550.3B",
            sector = "Automotive"
        ),
        "META" to StockItem(
            symbol = "META",
            name = "Meta Platforms, Inc.",
            price = "$471.35",
            change = "+5.68",
            changePercent = "+1.22%",
            marketCap = "1.2T",
            sector = "Technology"
        ),
        "NVDA" to StockItem(
            symbol = "NVDA",
            name = "NVIDIA Corporation",
            price = "$950.02",
            change = "+15.38",
            changePercent = "+1.64%",
            marketCap = "2.34T",
            sector = "Technology"
        ),
        "BRK.A" to StockItem(
            symbol = "BRK.A",
            name = "Berkshire Hathaway Inc.",
            price = "$624,950.00",
            change = "+2,750.00",
            changePercent = "+0.44%",
            marketCap = "917.2B",
            sector = "Financial Services"
        ),
        "JPM" to StockItem(
            symbol = "JPM",
            name = "JPMorgan Chase & Co.",
            price = "$196.52",
            change = "+0.89",
            changePercent = "+0.46%",
            marketCap = "565.8B",
            sector = "Financial Services"
        ),
        "JNJ" to StockItem(
            symbol = "JNJ",
            name = "Johnson & Johnson",
            price = "$152.87",
            change = "-0.54",
            changePercent = "-0.35%",
            marketCap = "367.5B",
            sector = "Healthcare"
        )
    )

    fun loadStockDetails(symbol: String) {
        viewModelScope.launch {
            _stockState.value = StockDetailState(isLoading = true)

            try {
                // Get stock from mock data instead of API
                val stock = getMockStock(symbol)

                if (stock != null) {
                    _stockState.value = StockDetailState(stock = stock)

                    // Load historical data for the chart
                    loadChartData(symbol)
                } else {
                    _stockState.value = StockDetailState(
                        errorMessage = "Could not load stock information for $symbol"
                    )
                }
            } catch (e: Exception) {
                _stockState.value = StockDetailState(
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    private fun getMockStock(symbol: String): StockItem? {
        return mockStocks[symbol] ?: generateDefaultStock(symbol)
    }

    private fun generateDefaultStock(symbol: String): StockItem {
        val random = Random()
        val price = 100 + random.nextDouble() * 200
        val change = -5 + random.nextDouble() * 10
        val changePercent = (change / price) * 100

        return StockItem(
            symbol = symbol,
            name = "$symbol Corporation",
            price = "$%.2f".format(price),
            change = "%+.2f".format(change),
            changePercent = "%+.2f%%".format(changePercent),
            marketCap = "%.1fB".format(price * 10),
            sector = "Misc"
        )
    }


    private fun loadChartData(symbol: String) {
        viewModelScope.launch {
            try {
                _chartData.value = generateMockChartData(timeRange.value)
            } catch (e: Exception) {
                // If chart data loading fails, we still show the stock info
                // but with an empty chart
                _chartData.value = emptyList()
            }
        }
    }


    fun setTimeRange(range: String) {
        if (_timeRange.value != range) {
            _timeRange.value = range
            _stockState.value.stock?.let { stock ->
                loadChartData(stock.symbol)
            }
        }
    }

    private fun generateMockChartData(range: String): List<StockHistoricalData> {
        val currentStock = _stockState.value.stock ?: return emptyList()
        val currentPrice = currentStock.price.replace("$", "").toDoubleOrNull() ?: 0.0
        val dataPoints = when (range) {
            "1D" -> 24
            "1W" -> 7
            "1M" -> 30
            "3M" -> 90
            "1Y" -> 365
            "5Y" -> 60
            else -> 30
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val stepSize = when (range) {
            "1D" -> 0 // For 1D, we'll use hours instead
            "1W" -> 1
            "1M" -> 1
            "3M" -> 3
            "1Y" -> 7
            "5Y" -> 30
            else -> 1
        }

        return List(dataPoints) { index ->
            val adjustedCalendar = Calendar.getInstance()

            if (range == "1D") {
                // For 1D, go back by hours
                adjustedCalendar.add(Calendar.HOUR_OF_DAY, -(dataPoints - index - 1))
            } else {
                adjustedCalendar.add(Calendar.DAY_OF_YEAR, -((dataPoints - index - 1) * stepSize))
            }

            val dateStr = dateFormat.format(adjustedCalendar.time)

            val randomFactor = 0.02 // 2% random variation
            val trendFactor = (index.toDouble() / dataPoints) // Trend towards current price
            val startPrice = currentPrice * 0.8 // Start 20% lower than current price
            val priceRange = currentPrice - startPrice

            val basePrice = startPrice + (priceRange * trendFactor)
            val randomVariation = (Math.random() - 0.5) * randomFactor * currentPrice
            val price = basePrice + randomVariation

            StockHistoricalData(
                date = dateStr,
                open = price * 0.998, // Slightly lower than close
                close = price,
                high = price * 1.005, // Slightly higher than close
                low = price * 0.995, // Slightly lower than close
                volume = (100000 + (Math.random() * 1000000)).toLong()
            )
        }
    }
}