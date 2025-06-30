package com.example.stockko.data.model


data class StockItem(
    val symbol: String,
    val name: String,
    val price: String,
    val change: String,
    val changePercent: String,
    val marketCap: String,
    val sector: String,
    // Additional optional fields for more detailed information
    val open: String? = null,
    val high: String? = null,
    val low: String? = null,
    val volume: String? = null,
    val peRatio: String? = null,
    val dividend: String? = null,
    val yield: String? = null,
    val eps: String? = null,
    val beta: String? = null,
    val fiftyTwoWeekHigh: String? = null,
    val fiftyTwoWeekLow: String? = null
) {

    val isPositiveChange: Boolean
        get() = change.startsWith("+")
    val priceChange: Double
        get() = change.replace("+", "").toDoubleOrNull() ?: 0.0

    val percentChange: Double
        get() = changePercent.replace("%", "").replace("+", "").toDoubleOrNull() ?: 0.0
}


data class StockHistoricalData(
    val date: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)
//
//data class TimeSeriesResponse(
//    val metaData: MetaData? = null,
//    val timeSeriesData: Map<String, TimeSeriesDataPoint>? = null
//)
//
//
//data class MetaData(
//    val information: String? = null,
//    val symbol: String? = null,
//    val lastRefreshed: String? = null,
//    val outputSize: String? = null,
//    val timeZone: String? = null
//)
//
//data class TimeSeriesDataPoint(
//    val open: String? = null,
//    val high: String? = null,
//    val low: String? = null,
//    val close: String? = null,
//    val volume: String? = null
//)