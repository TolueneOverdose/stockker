package com.example.stockko.data.repository

// data/repository/StockRepository.kt

import com.example.stockko.data.network.StockApi
import com.example.stockko.ui.screen.StockItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class StockRepository {
    private val apiService = StockApi.api

    suspend fun getStock(symbol: String): StockItem? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getGlobalQuote(symbol = symbol)
            response.globalQuote?.let { quote ->
                val changeValue = quote.change.toDoubleOrNull() ?: 0.0
                val changePercent = quote.changePercent
                    .replace("%", "")
                    .toDoubleOrNull() ?: 0.0

                StockItem(
                    symbol = quote.symbol,
                    name = getCompanyName(quote.symbol),
                    price = "$${quote.price}",
                    priceChange = changeValue,
                    percentChange = changePercent
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getCompanyName(symbol: String): String {
        return try {
            val overview = apiService.getCompanyOverview(symbol = symbol)
            overview.name ?: symbol
        } catch (e: Exception) {
            symbol
        }
    }

    suspend fun searchStocks(query: String): List<StockItem> = withContext(Dispatchers.IO) {
        try {
            if (query.isBlank()) return@withContext emptyList()

            val response = apiService.searchStocks(keywords = query)
            val searchResults = response.bestMatches?.map { match ->
                StockItem(
                    symbol = match.symbol,
                    name = match.name,
                    price = "N/A",
                    priceChange = 0.0,
                    percentChange = 0.0
                )
            } ?: emptyList()

            return@withContext if (searchResults.isEmpty()) getMockSearchResults(query) else searchResults
        } catch (e: Exception) {
            getMockSearchResults(query)
        }
    }

    suspend fun getTrendingStocks(): List<StockItem> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTopGainersLosers()
            response.mostActivelyTraded?.map { stock ->
                val changeValue = stock.changeAmount.toDoubleOrNull() ?: 0.0
                val changePercent = stock.changePercentage
                    .replace("%", "")
                    .toDoubleOrNull() ?: 0.0

                StockItem(
                    symbol = stock.ticker,
                    name = getCompanyName(stock.ticker),
                    price = "$${stock.price}",
                    priceChange = changeValue,
                    percentChange = changePercent
                )
            }?.take(5) ?: emptyList()
        } catch (e: IOException) {
            emptyList()
        }
    }

    suspend fun getTopGainers(): List<StockItem> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTopGainersLosers()
            val topGainersList = response.topGainers?.map { stock ->
                val changeValue = stock.changeAmount.toDoubleOrNull() ?: 0.0
                val changePercent = stock.changePercentage
                    .replace("%", "")
                    .toDoubleOrNull() ?: 0.0

                StockItem(
                    symbol = stock.ticker,
                    name = getCompanyName(stock.ticker),
                    price = "$${stock.price}",
                    priceChange = changeValue,
                    percentChange = changePercent
                )
            } ?: emptyList()

            return@withContext if (topGainersList.isEmpty()) getMockTopGainers() else topGainersList
        } catch (e: Exception) {
            getMockTopGainers()
        }
    }

    suspend fun getTopLosers(): List<StockItem> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTopGainersLosers()
            val topLosersList = response.topLosers?.map { stock ->
                val changeValue = stock.changeAmount.toDoubleOrNull() ?: 0.0
                val changePercent = stock.changePercentage
                    .replace("%", "")
                    .toDoubleOrNull() ?: 0.0

                StockItem(
                    symbol = stock.ticker,
                    name = getCompanyName(stock.ticker),
                    price = "$${stock.price}",
                    priceChange = changeValue,
                    percentChange = changePercent
                )
            } ?: emptyList()

            return@withContext if (topLosersList.isEmpty()) getMockTopLosers() else topLosersList
        } catch (e: Exception) {
            getMockTopLosers()
        }
    }

    private fun getMockTopGainers(): List<StockItem> {
        return listOf(
            StockItem("NVDA", "NVIDIA Corporation", "$916.75", 41.42, 4.73),
            StockItem("AMD", "Advanced Micro Devices, Inc.", "$178.35", 7.89, 4.63),
            StockItem("SPOT", "Spotify Technology S.A.", "$325.46", 12.75, 4.08),
            StockItem("CRM", "Salesforce, Inc.", "$274.88", 9.34, 3.52),
            StockItem("ADBE", "Adobe Inc.", "$512.20", 15.62, 3.15),
            StockItem("PYPL", "PayPal Holdings, Inc.", "$72.65", 2.14, 3.03),
            StockItem("INTC", "Intel Corporation", "$34.82", 0.98, 2.89),
            StockItem("UBER", "Uber Technologies, Inc.", "$75.33", 2.11, 2.88),
            StockItem("DIS", "The Walt Disney Company", "$118.45", 3.25, 2.82),
            StockItem("SQ", "Block, Inc.", "$87.65", 2.32, 2.72)
        )
    }

    private fun getMockTopLosers(): List<StockItem> {
        return listOf(
            StockItem("NFLX", "Netflix, Inc.", "$625.32", -32.75, -4.98),
            StockItem("TSLA", "Tesla, Inc.", "$167.42", -7.48, -4.28),
            StockItem("COIN", "Coinbase Global, Inc.", "$195.73", -8.25, -4.04),
            StockItem("SHOP", "Shopify Inc.", "$62.45", -2.35, -3.63),
            StockItem("SNAP", "Snap Inc.", "$12.14", -0.42, -3.35),
            StockItem("ZM", "Zoom Video Communications, Inc.", "$58.36", -1.89, -3.14),
            StockItem("RIVN", "Rivian Automotive, Inc.", "$8.75", -0.27, -2.99),
            StockItem("LCID", "Lucid Group, Inc.", "$2.93", -0.08, -2.66),
            StockItem("RBLX", "Roblox Corporation", "$38.27", -0.96, -2.45),
            StockItem("ABNB", "Airbnb, Inc.", "$142.68", -3.42, -2.34)
        )
    }

    private fun getMockSearchResults(query: String): List<StockItem> {
        val normalizedQuery = query.lowercase()
        val allMockStocks = listOf(
            StockItem("AAPL", "Apple Inc.", "$186.41", 1.25, 0.67),
            StockItem("MSFT", "Microsoft Corporation", "$417.22", 2.36, 0.57),
            StockItem("GOOGL", "Alphabet Inc.", "$178.25", -1.54, -0.86),
            StockItem("GOOG", "Alphabet Inc. Class C", "$179.35", -1.12, -0.62),
            StockItem("AMZN", "Amazon.com Inc.", "$183.75", 0.95, 0.52),
            StockItem("META", "Meta Platforms Inc.", "$496.18", 3.42, 0.69),
            StockItem("NVDA", "NVIDIA Corporation", "$875.33", 12.75, 1.48),
            StockItem("TSLA", "Tesla Inc.", "$174.90", -5.28, -2.93),
            StockItem("NFLX", "Netflix Inc.", "$625.32", -32.75, -4.98),
            StockItem("ADBE", "Adobe Inc.", "$512.20", 15.62, 3.15),
            StockItem("INTC", "Intel Corporation", "$34.82", 0.98, 2.89),
            StockItem("AMD", "Advanced Micro Devices Inc.", "$178.35", 7.89, 4.63),
            StockItem("CRM", "Salesforce Inc.", "$274.88", 9.34, 3.52),
            StockItem("JPM", "JPMorgan Chase & Co.", "$195.73", -0.84, -0.43),
            StockItem("BAC", "Bank of America Corporation", "$37.92", 0.23, 0.61),
            StockItem("WFC", "Wells Fargo & Company", "$59.18", -0.45, -0.76),
            StockItem("C", "Citigroup Inc.", "$63.42", 0.28, 0.44),
            StockItem("V", "Visa Inc.", "$275.54", 1.67, 0.61),
            StockItem("MA", "Mastercard Incorporated", "$458.12", 2.34, 0.51),
            StockItem("PYPL", "PayPal Holdings Inc.", "$72.65", 2.14, 3.03),
            StockItem("GS", "Goldman Sachs Group Inc.", "$462.78", -1.25, -0.27),
            StockItem("MS", "Morgan Stanley", "$96.45", 0.32, 0.33),
            StockItem("WMT", "Walmart Inc.", "$62.93", 0.34, 0.54),
            StockItem("COST", "Costco Wholesale Corporation", "$718.31", 5.15, 0.72),
            StockItem("TGT", "Target Corporation", "$142.56", -0.87, -0.61),
            StockItem("HD", "The Home Depot Inc.", "$342.78", 2.12, 0.62),
            StockItem("LOW", "Lowe's Companies Inc.", "$215.67", 1.45, 0.68),
            StockItem("SBUX", "Starbucks Corporation", "$78.92", -0.34, -0.43),
            StockItem("MCD", "McDonald's Corporation", "$267.43", 0.98, 0.37),
            StockItem("T", "AT&T Inc.", "$17.85", 0.15, 0.85),
            StockItem("VZ", "Verizon Communications Inc.", "$40.65", -0.28, -0.68),
            StockItem("TMUS", "T-Mobile US Inc.", "$162.78", 1.45, 0.90),
            StockItem("JNJ", "Johnson & Johnson", "$151.92", -0.85, -0.56),
            StockItem("PFE", "Pfizer Inc.", "$28.45", -0.32, -1.11),
            StockItem("MRNA", "Moderna Inc.", "$128.67", 3.56, 2.85),
            StockItem("UNH", "UnitedHealth Group Incorporated", "$526.78", 2.34, 0.45),
            StockItem("XOM", "Exxon Mobil Corporation", "$118.67", 0.87, 0.74),
            StockItem("CVX", "Chevron Corporation", "$155.34", 1.23, 0.80),
            StockItem("COP", "ConocoPhillips", "$112.56", 0.92, 0.82),
            StockItem("DIS", "The Walt Disney Company", "$118.45", 3.25, 2.82),
            StockItem("CMCSA", "Comcast Corporation", "$42.67", 0.23, 0.54)
        )
        return allMockStocks.filter { stock ->
            stock.symbol.lowercase().contains(normalizedQuery) ||
                    stock.name.lowercase().contains(normalizedQuery)
        }
    }

    fun getDemoStocks(): List<StockItem> {
        return listOf(
            StockItem("AAPL", "Apple Inc.", "$186.41", 1.25, 0.67),
            StockItem("MSFT", "Microsoft Corporation", "$417.22", 2.36, 0.57),
            StockItem("GOOGL", "Alphabet Inc.", "$178.25", -1.54, -0.86),
            StockItem("AMZN", "Amazon.com Inc.", "$183.75", 0.95, 0.52),
            StockItem("TSLA", "Tesla Inc.", "$174.90", -5.28, -2.93),
            StockItem("META", "Meta Platforms Inc.", "$496.18", 3.42, 0.69),
            StockItem("NVDA", "NVIDIA Corporation", "$875.33", 12.75, 1.48),
            StockItem("JPM", "JPMorgan Chase & Co.", "$195.73", -0.84, -0.43),
            StockItem("V", "Visa Inc.", "$275.54", 1.67, 0.61),
            StockItem("WMT", "Walmart Inc.", "$62.93", 0.34, 0.54)
        )
    }
}