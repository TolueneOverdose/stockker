package com.example.stockko.ui.screen


import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockko.ui.components.StockChart
import com.example.stockko.ui.components.StockInfoCard
import com.example.stockko.viewmodel.StockDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailScreen(
    symbol: String,
    onBackPressed: () -> Unit,
    viewModel: StockDetailViewModel
) {
    val stockState by viewModel.stockState.collectAsState()
    val chartData by viewModel.chartData.collectAsState()
    val timeRange by viewModel.timeRange.collectAsState()

    LaunchedEffect(symbol) {
        viewModel.loadStockDetails(symbol)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = symbol,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            if (stockState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            stockState.errorMessage?.let { errorMessage ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            stockState.stock?.let { stock ->
                StockHeader(stock)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Price Chart",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        TimeRangeSelector(
                            selectedRange = timeRange,
                            onRangeSelected = { viewModel.setTimeRange(it) }
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            if (chartData.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else {
                                StockChart(
                                    chartData = chartData,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                StockInfoCard(stock)
            }
        }
    }
}

@Composable
private fun StockHeader(stock: com.example.stockko.data.model.StockItem) {
    val priceColor = if (stock.priceChange >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stock.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stock.price,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (stock.priceChange >= 0) "+" else ""}${stock.priceChange}",
                        color = priceColor,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${if (stock.percentChange >= 0) "+" else ""}${stock.percentChange}%",
                        color = priceColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeRangeSelector(
    selectedRange: String,
    onRangeSelected: (String) -> Unit
) {
    val timeRanges = listOf("1D", "1W", "1M", "3M", "1Y", "5Y")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        timeRanges.forEach { range ->
            val isSelected = range == selectedRange
            val backgroundColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surface
            val textColor = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .clickable { onRangeSelected(range) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = range,
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}