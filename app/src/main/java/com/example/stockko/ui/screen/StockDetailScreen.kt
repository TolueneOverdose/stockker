package com.example.stockko.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockko.ui.components.StockChart
import androidx.compose.ui.text.style.TextAlign
import com.example.stockko.data.model.StockHistoricalData
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

    // Dark gradient background
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F172A),
            Color(0xFF1E293B),
            Color(0xFF334155)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Floating background elements
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-50).dp, y = 100.dp)
                .background(
                    Color(0xFF3B82F6).copy(alpha = 0.1f),
                    CircleShape
                )
                .blur(50.dp)
        )

        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = 250.dp, y = 400.dp)
                .background(
                    Color(0xFF10B981).copy(alpha = 0.1f),
                    CircleShape
                )
                .blur(40.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Custom Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = onBackPressed,
                    modifier = Modifier.size(48.dp),
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = symbol,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }

            if (stockState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = Color(0xFF3B82F6),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading market data...",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            stockState.errorMessage?.let { errorMessage ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF991B1B).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFEF4444),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(24.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            stockState.stock?.let { stock ->
                Spacer(modifier = Modifier.height(8.dp))

                // Hero Price Section with Glassmorphism
                HeroStockCard(stock)

                Spacer(modifier = Modifier.height(32.dp))

                // Chart Section with new design
                ChartSection(
                    timeRange = timeRange,
                    chartData = chartData,
                    onRangeSelected = { viewModel.setTimeRange(it) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Info section
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    StockInfoCard(stock)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun HeroStockCard(stock: com.example.stockko.data.model.StockItem) {
    val isPositive = stock.priceChange >= 0
    val trendColor = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444)
    val trendIcon = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown

    // Glassmorphism effect
    val cardGradient = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.15f),
            Color.White.copy(alpha = 0.05f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color.Black.copy(alpha = 0.5f)
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardGradient)
                    .padding(32.dp)
            ) {
                Column {
                    // Company name with new styling
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(trendColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stock.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f),
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Price display with dramatic styling
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = stock.price,
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = (-1).sp
                            )

                            Text(
                                text = "Current Price",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Trend indicator with floating design
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = trendColor.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.shadow(8.dp, RoundedCornerShape(20.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = trendIcon,
                                    contentDescription = null,
                                    tint = trendColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${if (stock.priceChange >= 0) "+" else ""}${stock.priceChange}",
                                        color = trendColor,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${if (stock.percentChange >= 0) "+" else ""}${stock.percentChange}%",
                                        color = trendColor,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartSection(
    timeRange: String,
    chartData: List<StockHistoricalData>,
    onRangeSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        // Section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Market Analysis",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Interactive price chart",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Time range selector with pill design
        ModernTimeRangeSelector(
            selectedRange = timeRange,
            onRangeSelected = onRangeSelected
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Chart container with glassmorphism
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .shadow(16.dp, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.08f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                if (chartData.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = Color(0xFF3B82F6),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Loading chart...",
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
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
}

@Composable
private fun ModernTimeRangeSelector(
    selectedRange: String,
    onRangeSelected: (String) -> Unit
) {
    val timeRanges = listOf("1D", "1W", "1M", "3M", "1Y", "5Y")

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            timeRanges.forEach { range ->
                val isSelected = range == selectedRange

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected)
                                Color(0xFF3B82F6)
                            else
                                Color.Transparent
                        )
                        .clickable { onRangeSelected(range) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = range,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}