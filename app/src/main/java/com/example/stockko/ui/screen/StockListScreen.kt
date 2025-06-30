package com.example.stockko.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class StockListType {
    TOP_GAINERS,
    TOP_LOSERS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockListScreen(
    stockType: StockListType,
    stocks: List<StockItem>,
    onBackPressed: () -> Unit,
    onStockClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (title, icon, iconTint) = when (stockType) {
        StockListType.TOP_GAINERS -> Triple(
            "Top Gainers",
            Icons.AutoMirrored.Filled.TrendingUp,
            Color(0xFF4CAF50)
        )
        StockListType.TOP_LOSERS -> Triple(
            "Top Losers",
            Icons.AutoMirrored.Filled.TrendingDown,
            Color(0xFFE53935)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = title)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Showing all $title",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            items(stocks) { stock ->
                StockListItem(stock = stock, onClick = { onStockClick(stock.symbol) })
            }

            // Add some padding at the bottom
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}