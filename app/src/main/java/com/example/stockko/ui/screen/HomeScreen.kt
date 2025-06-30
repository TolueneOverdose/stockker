package com.example.stockko.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import android.util.Log

data class HomeState(
    val searchQuery: String = "",
    val resultText: String = "Enter a stock symbol to start",
    val isSearching: Boolean = false,
    val recentSearches: List<String> = emptyList(),
    val trendingStocks: List<StockItem> = emptyList(),
    val topGainers: List<StockItem> = emptyList(),
    val topLosers: List<StockItem> = emptyList(),
    val searchResults: List<StockItem> = emptyList()
)

data class StockItem(
    val symbol: String,
    val name: String,
    val price: String,
    val priceChange: Double,
    val percentChange: Double
)

@Composable
fun HomeScreen(
    state: HomeState,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    onStockClick: (String) -> Unit,
    onViewAllGainers: () -> Unit,
    onViewAllLosers: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Header
        Text(
            text = "Stockko",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Enhanced Search Bar
        SearchBar(
            query = state.searchQuery,
            onQueryChange = onSearch,
            onClearQuery = onClearSearch,
            isSearching = state.isSearching,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Content based on search state
        if (state.searchQuery.isEmpty()) {
            LazyColumn {
                // Recent Searches Section
                item {
                    Text(
                        text = "Recent Searches",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (state.recentSearches.isEmpty()) {
                    item {
                        Text(
                            text = "No recent searches",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                } else {
                    // Use individual items for recent searches instead of a nested LazyColumn
                    items(state.recentSearches) { search ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSearch(search) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )

                            Text(
                                text = search,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .weight(1f)
                            )

                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Top Gainers Section
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Top Gainers",
                            tint = Color(0xFF4CAF50)
                        )

                        Text(
                            text = "Top Gainers",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onViewAllGainers() }
                        )
                    }
                    val displayGainers = if (state.topGainers.isEmpty()) {
                        Log.d("StockViewModel", "state.topGainers contains data: ${state.topGainers}")
                        remember {
                            listOf(
                                StockItem("NVDA", "NVIDIA Corp.", "$123.45", 8.25, 7.15),
                                StockItem("AMD", "Advanced Micro Devices", "$165.30", 6.82, 4.30),
                                StockItem("ASML", "ASML Holding", "$981.55", 15.30, 1.58),
                                StockItem("MRVL", "Marvell Technology", "$72.16", 3.18, 4.61),
                                StockItem("AVGO", "Broadcom Inc.", "$1205.32", 23.67, 2.00)
                            )
                        }
                    } else {
                        state.topGainers
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(displayGainers, key = { it.symbol }) { stock ->
                            StockCard(stock = stock, onClick = { onStockClick(stock.symbol) })
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Top Losers Section
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingDown,
                            contentDescription = "Top Losers",
                            tint = Color(0xFFE53935)
                        )

                        Text(
                            text = "Top Losers",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onViewAllLosers() }
                        )
                    }

                    val displayLosers = if (state.topLosers.isEmpty()) {
                        remember {
                            listOf(
                                StockItem("TSLA", "Tesla Inc.", "$168.42", -12.83, -7.08),
                                StockItem("META", "Meta Platforms", "$476.20", -15.60, -3.17),
                                StockItem("INTC", "Intel Corp.", "$31.05", -1.62, -4.96),
                                StockItem("CRWD", "CrowdStrike Holdings", "$305.18", -21.45, -6.57),
                                StockItem("NFLX", "Netflix Inc.", "$627.40", -13.22, -2.06)
                            )
                        }
                    } else {
                        state.topLosers
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(displayLosers, key = { it.symbol }) { stock ->
                            StockCard(stock = stock, onClick = { onStockClick(stock.symbol) })
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Trending Stocks",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Trending Stocks",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                // Trending Stocks items
                item {
                    val displayStocks = state.trendingStocks.ifEmpty {
                        remember {
                            listOf(
                                StockItem("AAPL", "Apple Inc.", "$186.41", 1.25, 0.67),
                                StockItem("MSFT", "Microsoft Corp.", "$417.22", 2.36, 0.57),
                                StockItem("GOOGL", "Alphabet Inc.", "$178.25", -1.54, -0.86),
                                StockItem("AMZN", "Amazon.com Inc.", "$183.75", 0.95, 0.52),
                                StockItem("TSLA", "Tesla Inc.", "$174.90", -5.28, -2.93)
                            )
                        }
                    }

                    Column {
                        displayStocks.forEach { stock ->
                            StockListItem(stock = stock, onClick = { onStockClick(stock.symbol) })
                        }
                    }
                }
            }
        } else {
            // Show search results
            if (state.isSearching) {
                LoadingIndicator()
            } else if (state.searchResults.isEmpty()) {
                EmptySearchResults(query = state.searchQuery)
            } else {
                SearchResultsList(
                    results = state.searchResults,
                    onStockClick = onStockClick
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF0F2F5),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )

            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search stocks...", color = Color.Gray) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )

            if (isSearching) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp),
                    strokeWidth = 2.dp
                )
            } else if (query.isNotEmpty()) {
                IconButton(onClick = onClearQuery) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun StockCard(
    stock: StockItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF9FAFB)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stock.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stock.symbol.take(1),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = stock.name,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stock.price,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF000000)
            )

            val changeColor = if (stock.priceChange >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
            val changePrefix = if (stock.priceChange >= 0) "+" else ""

            Text(
                text = "$changePrefix${stock.priceChange} (${changePrefix}${stock.percentChange}%)",
                style = MaterialTheme.typography.bodySmall,
                color = changeColor
            )
        }
    }
}
@Composable
fun StockListItem(
    stock: StockItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF6F8FA)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stock.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = stock.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stock.price,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                val changeColor = if (stock.priceChange >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
                val changePrefix = if (stock.priceChange >= 0) "+" else ""

                Text(
                    text = "$changePrefix${stock.priceChange} (${changePrefix}${stock.percentChange}%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = changeColor
                )
            }
        }
    }
}

@Composable
fun SearchResultsList(
    results: List<StockItem>,
    onStockClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Search Results",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn {
            items(results) { stock ->
                StockListItem(stock = stock, onClick = { onStockClick(stock.symbol) })
            }
        }
    }
}

@Composable
fun EmptySearchResults(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No results found for '$query'",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Try another stock symbol or company name",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}