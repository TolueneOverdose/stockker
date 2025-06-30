package com.example.stockko.ui.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockko.data.model.NetworkResult
import com.example.stockko.data.repository.StockRepository
import com.example.stockko.ui.screen.StockItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StockViewModel(
    private val repository: StockRepository = StockRepository()
) : ViewModel() {

    // LiveData for UI states
    private val _searchResults = MutableLiveData<NetworkResult<List<StockItem>>>()
    val searchResults: LiveData<NetworkResult<List<StockItem>>> = _searchResults

    private val _trendingStocks = MutableLiveData<NetworkResult<List<StockItem>>>()
    val trendingStocks: LiveData<NetworkResult<List<StockItem>>> = _trendingStocks

    private val _topGainers = MutableLiveData<NetworkResult<List<StockItem>>>()
    val topGainers: LiveData<NetworkResult<List<StockItem>>> = _topGainers

    private val _topLosers = MutableLiveData<NetworkResult<List<StockItem>>>()
    val topLosers: LiveData<NetworkResult<List<StockItem>>> = _topLosers

    private val _stockDetails = MutableLiveData<NetworkResult<StockItem?>>()
    val stockDetails: LiveData<NetworkResult<StockItem?>> = _stockDetails

    // Debounce job for search queries
    private var searchJob: Job? = null

    /**
     * Load initial data when the app starts
     */
    init {
        loadTrendingStocks()
        loadTopGainers()
        loadTopLosers()
    }

    /**
     * Search for stocks with debounce to avoid too many API calls
     */
    fun searchStocks(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _searchResults.value = NetworkResult.success(emptyList())
            return
        }

        searchJob = viewModelScope.launch {
            _searchResults.value = NetworkResult.loading()
            // Debounce for 300ms
            delay(300)
            try {
                val results = repository.searchStocks(query)
                _searchResults.value = NetworkResult.success(results)
            } catch (e: Exception) {
                _searchResults.value = NetworkResult.error("Failed to search stocks: ${e.message}")
            }
        }
    }

    /**
     * Load trending stocks
     */
    fun loadTrendingStocks() {
        viewModelScope.launch {
            _trendingStocks.value = NetworkResult.loading()
            try {
                val stocks = repository.getTrendingStocks()
                if (stocks.isNotEmpty()) {
                    _trendingStocks.value = NetworkResult.success(stocks)
                } else {
                    // Fallback to demo data if API returns empty
                    val demoTrending = repository.getDemoStocks().take(5)
                    _trendingStocks.value = NetworkResult.success(demoTrending)
                }
            } catch (e: Exception) {
                _trendingStocks.value = NetworkResult.error("Failed to load trending stocks: ${e.message}")
                // Fallback to demo data on error
                val demoTrending = repository.getDemoStocks().take(5)
                _trendingStocks.value = NetworkResult.success(demoTrending)
            }
        }
    }

    /**
     * Load top gainers
     */
    fun loadTopGainers() {
        viewModelScope.launch {
            _topGainers.value = NetworkResult.loading()
            try {
                val gainers = repository.getTopGainers()
                _topGainers.value = if (gainers.isNotEmpty()) {
                    NetworkResult.success(gainers)
                } else {
                    // Fallback to demo data sorted by highest change percentage
                    val demoGainers = repository.getDemoStocks()
                        .sortedByDescending<StockItem, Double> { it.percentChange}
                        .take(10)
                    NetworkResult.success(demoGainers)
                }
            } catch (e: Exception) {
                _topGainers.value = NetworkResult.error("Failed to load top gainers: ${e.message}")
                // Fallback to demo data on error
                val demoGainers = repository.getDemoStocks()
                    .sortedByDescending<StockItem, Double> { it.percentChange }
                    .take(10)
                _topGainers.value = NetworkResult.success(demoGainers)
            }
        }
    }

    /**
     * Load top losers
     */
    fun loadTopLosers() {
        viewModelScope.launch {
            _topLosers.value = NetworkResult.loading()
            try {
                val losers = repository.getTopLosers()
                _topLosers.value = if (losers.isNotEmpty()) {
                    NetworkResult.success(losers)
                } else {
                    // Fallback to demo data sorted by lowest change percentage
                    val demoLosers = repository.getDemoStocks()
                        .sortedBy<StockItem, Double> { it.percentChange }
                        .take(10)
                    NetworkResult.success(demoLosers)
                }
            } catch (e: Exception) {
                _topLosers.value = NetworkResult.error("Failed to load top losers: ${e.message}")
                // Fallback to demo data on error
                val demoLosers = repository.getDemoStocks()
                    .sortedBy<StockItem, Double> { it.percentChange }
                    .take(10)
                _topLosers.value = NetworkResult.success(demoLosers)
            }
        }
    }

    /**
     * Get details for a specific stock
     */
    fun getStockDetails(symbol: String) {
        viewModelScope.launch {
            _stockDetails.value = NetworkResult.loading()
            try {
                val stock = repository.getStock(symbol)
                _stockDetails.value = NetworkResult.success(stock)
            } catch (e: Exception) {
                _stockDetails.value = NetworkResult.error("Failed to load stock details: ${e.message}")
            }
        }
    }

    /**
     * Refresh all data
     */
    fun refreshAllData() {
        loadTrendingStocks()
        loadTopGainers()
        loadTopLosers()
    }
}
