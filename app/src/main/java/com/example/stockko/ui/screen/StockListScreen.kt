package com.example.stockko.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    val isGainers = stockType == StockListType.TOP_GAINERS

    val title = if (isGainers) "Top Gainers" else "Top Losers"
    val icon = if (isGainers) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown
    val primaryColor = if (isGainers) Color(0xFF00E676) else Color(0xFFFF5252)
    val secondaryColor = if (isGainers) Color(0xFF66BB6A) else Color(0xFFEF5350)
    val emoji = if (isGainers) "🚀" else "📉"
    val backgroundGradient = if (isGainers) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFE8F5E8),
                Color(0xFFF1F8E9)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFEBEE),
                Color(0xFFFCE4EC)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient as Brush)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                // Custom cartoony top bar
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = primaryColor.copy(alpha = 0.1f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = onBackPressed,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Title section with emoji and gradient text
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                        }

                        // Trending icon with bouncy background
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            primaryColor.copy(alpha = 0.2f),
                                            secondaryColor.copy(alpha = 0.1f)
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Fun header card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Colorful circle indicator
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        color = primaryColor,
                                        shape = CircleShape
                                    )
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Showing all ${title.lowercase()}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF37474F),
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            // Fun badge
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = primaryColor.copy(alpha = 0.15f)
                                )
                            ) {
                                Text(
                                    text = "${stocks.size}",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                items(stocks) { stock ->
                    // Enhanced stock item cards
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 3.dp,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        onClick = { onStockClick(stock.symbol) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Colorful side indicator
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(40.dp)
                                    .background(
                                        color = primaryColor,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            // Stock content would go here
                            // This would be the StockListItem content
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stock.symbol,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF263238)
                                )
                                Text(
                                    text = stock.name,
                                    fontSize = 14.sp,
                                    color = Color(0xFF607D8B)
                                )
                            }

                            // Fun percentage badge
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = primaryColor.copy(alpha = 0.1f)
                                )
                            ) {
                                Text(
                                    text = "${if (stockType == StockListType.TOP_GAINERS) "+" else ""}${stock.percentChange}%",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Fun bottom padding with decorative element
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.6f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(5) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            color = primaryColor.copy(alpha = 0.3f),
                                            shape = CircleShape
                                        )
                                )
                                if (it < 4) Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}