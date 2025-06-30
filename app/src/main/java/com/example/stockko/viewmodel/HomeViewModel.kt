package com.example.stockko.viewmodel


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockko.data.repository.StockRepository
import kotlinx.coroutines.launch
import com.example.stockko.ui.screen.HomeState
import com.example.stockko.ui.screen.StockItem

class HomeViewModel(
    private val repository: StockRepository = StockRepository()
) : ViewModel() {

    var state = mutableStateOf(HomeState())
        private set

    // List to store recent searches
    private val recentSearches = mutableListOf<String>()

    // Store top gainers and losers
    private val topGainers = mutableListOf<StockItem>()
    private val topLosers = mutableListOf<StockItem>()

    init {
        // Load trending stocks, top gainers, and top losers on initialization
        loadTrendingStocks()
        loadTopGainersAndLosers()
    }

    private fun loadTrendingStocks() {
        viewModelScope.launch {
            try {
                val trendingStocks = repository.getTrendingStocks()
                state.value = state.value.copy(trendingStocks = trendingStocks)
            } catch (e: Exception) {
                // If there's an error, we'll just leave the trending stocks empty
                // The UI will show demo stocks in this case
            }
        }
    }

    private fun loadTopGainersAndLosers() {
        viewModelScope.launch {
            try {
                // Load top gainers
                val gainers = repository.getTopGainers()
                topGainers.clear()
                topGainers.addAll(gainers)

                // Load top losers
                val losers = repository.getTopLosers()
                topLosers.clear()
                topLosers.addAll(losers)

                state.value = state.value.copy(
                    topGainers = gainers.take(5),  // Show only 5 in the home screen
                    topLosers = losers.take(5)     // Show only 5 in the home screen
                )
            } catch (e: Exception) {
            }
        }
    }

    fun searchStock(query: String) {
        // Update the search query immediately
        state.value = state.value.copy(
            searchQuery = query,
            isSearching = query.isNotEmpty()
        )

        // If query is empty, just reset the search state
        if (query.isBlank()) {
            state.value = state.value.copy(
                searchResults = emptyList(),
                isSearching = false,
                resultText = "Enter a stock symbol to start"
            )
            return
        }

        // Perform the search
        viewModelScope.launch {
            try {
                // Get search results from repository
                val results = repository.searchStocks(query.uppercase())

                // Add to recent searches if not already present
                if (results.isNotEmpty() && !recentSearches.contains(query)) {
                    recentSearches.add(0, query)
                    // Keep only the latest 5 searches
                    if (recentSearches.size > 5) {
                        recentSearches.removeAt(recentSearches.size - 1)
                    }
                }

                // Update the state with search results
                state.value = state.value.copy(
                    searchResults = results,
                    isSearching = false,
                    recentSearches = recentSearches.toList()
                )
            } catch (e: Exception) {
                // Handle error
                state.value = state.value.copy(
                    isSearching = false,
                    resultText = "Error: ${e.message}"
                )
            }
        }
    }

    fun clearSearch() {
        state.value = state.value.copy(
            searchQuery = "",
            searchResults = emptyList(),
            isSearching = false,
            resultText = "Enter a stock symbol to start"
        )
    }

    fun navigateToStockDetail(symbol: String) {
        if (!recentSearches.contains(symbol)) {
            recentSearches.add(0, symbol)
            if (recentSearches.size > 5) {
                recentSearches.removeAt(recentSearches.size - 1)
            }
            state.value = state.value.copy(recentSearches = recentSearches.toList())
        }
    }

    fun getAllTopGainers(): List<StockItem> {
        return topGainers.toList()
    }

    // Return all top losers for the StockListScreen
    fun getAllTopLosers(): List<StockItem> {
        return topLosers.toList()
    }


}