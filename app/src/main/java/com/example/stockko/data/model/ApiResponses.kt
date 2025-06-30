package com.example.stockko.data.model


import com.google.gson.annotations.SerializedName

/**
 * Response model for Global Quote API
 */
data class GlobalQuoteResponse(
    @SerializedName("Global Quote")
    val globalQuote: GlobalQuote? = null
)

data class GlobalQuote(
    @SerializedName("01. symbol")
    val symbol: String,
    @SerializedName("02. open")
    val open: String,
    @SerializedName("03. high")
    val high: String,
    @SerializedName("04. low")
    val low: String,
    @SerializedName("05. price")
    val price: String,
    @SerializedName("06. volume")
    val volume: String,
    @SerializedName("07. latest trading day")
    val latestTradingDay: String,
    @SerializedName("08. previous close")
    val previousClose: String,
    @SerializedName("09. change")
    val change: String,
    @SerializedName("10. change percent")
    val changePercent: String
)

/**
 * Response model for Symbol Search API
 */
data class SearchResponse(
    @SerializedName("bestMatches")
    val bestMatches: List<StockMatch>? = null
)

data class StockMatch(
    @SerializedName("1. symbol")
    val symbol: String,
    @SerializedName("2. name")
    val name: String,
    @SerializedName("3. type")
    val type: String,
    @SerializedName("4. region")
    val region: String,
    @SerializedName("5. marketOpen")
    val marketOpen: String,
    @SerializedName("6. marketClose")
    val marketClose: String,
    @SerializedName("7. timezone")
    val timezone: String,
    @SerializedName("8. currency")
    val currency: String,
    @SerializedName("9. matchScore")
    val matchScore: String
)

/**
 * Response model for Company Overview API
 */
data class CompanyOverviewResponse(
    @SerializedName("Symbol")
    val symbol: String? = null,
    @SerializedName("Name")
    val name: String? = null,
    @SerializedName("Description")
    val description: String? = null,
    @SerializedName("Exchange")
    val exchange: String? = null,
    @SerializedName("Industry")
    val industry: String? = null,
    @SerializedName("PERatio")
    val peRatio: String? = null,
    @SerializedName("MarketCapitalization")
    val marketCap: String? = null,
    @SerializedName("DividendYield")
    val dividendYield: String? = null,
    @SerializedName("52WeekHigh")
    val weekHigh52: String? = null,
    @SerializedName("52WeekLow")
    val weekLow52: String? = null
)


data class TopGainersLosersResponse(
    @SerializedName("metadata")
    val metadata: String? = null,
    @SerializedName("last_updated")
    val lastUpdated: String? = null,
    @SerializedName("top_gainers")
    val topGainers: List<GainerLoser>? = null,
    @SerializedName("top_losers")
    val topLosers: List<GainerLoser>? = null,
    @SerializedName("most_actively_traded")
    val mostActivelyTraded: List<GainerLoser>? = null
)

data class GainerLoser(
    @SerializedName("ticker")
    val ticker: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("change_amount")
    val changeAmount: String,
    @SerializedName("change_percentage")
    val changePercentage: String,
    @SerializedName("volume")
    val volume: String
)