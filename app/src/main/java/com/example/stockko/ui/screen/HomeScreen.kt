package com.example.stockko.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.BorderStroke
import android.util.Log
import kotlin.math.sin

// Enhanced color palette
object StockkoColors {
    val DarkNavy = Color(0xFF0F172A)
    val Slate = Color(0xFF334155)
    val TechBlue = Color(0xFF3B82F6)
    val BullGreen = Color(0xFF10B981)
    val BearRed = Color(0xFFEF4444)
    val GlassBg = Color(0x1AFFFFFF)
    val GlassBorder = Color(0x33FFFFFF)
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xB3FFFFFF)
    val TextTertiary = Color(0x80FFFFFF)
    val FloatingBlue = Color(0x4D3B82F6)
    val FloatingPurple = Color(0x4D8B5CF6)
    val FloatingPink = Color(0x4DEC4899)
}

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
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Animated Background with Gradient
        AnimatedBackground()

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
        ) {
            // Hero Header Section
            HeroHeader()

            Spacer(modifier = Modifier.height(32.dp))

            // Glassmorphism Search Bar
            GlassmorphismSearchBar(
                query = state.searchQuery,
                onQueryChange = onSearch,
                onClearQuery = onClearSearch,
                isSearching = state.isSearching,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Content based on search state
            if (state.searchQuery.isEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Market Analysis Section
                    item {
                        MarketAnalysisSection(
                            recentSearches = state.recentSearches,
                            onSearch = onSearch
                        )
                    }

                    // Top Gainers Section
                    item {
                        val displayGainers = if (state.topGainers.isEmpty()) {
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

                        ModernStockSection(
                            title = "Top Gainers",
                            subtitle = "Leading market performers today",
                            icon = Icons.Default.TrendingUp,
                            iconColor = StockkoColors.BullGreen,
                            stocks = displayGainers,
                            onViewAll = onViewAllGainers,
                            onStockClick = onStockClick
                        )
                    }

                    // Top Losers Section
                    item {
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

                        ModernStockSection(
                            title = "Top Losers",
                            subtitle = "Stocks under pressure today",
                            icon = Icons.Default.TrendingDown,
                            iconColor = StockkoColors.BearRed,
                            stocks = displayLosers,
                            onViewAll = onViewAllLosers,
                            onStockClick = onStockClick
                        )
                    }

                    // Trending Portfolio Section
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

                        TrendingPortfolioSection(
                            stocks = displayStocks,
                            onStockClick = onStockClick
                        )
                    }
                }
            } else {
                // Search Results
                if (state.isSearching) {
                    ModernLoadingIndicator()
                } else if (state.searchResults.isEmpty()) {
                    ModernEmptySearchResults(query = state.searchQuery)
                } else {
                    ModernSearchResultsList(
                        results = state.searchResults,
                        onStockClick = onStockClick
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "background")

    val animatedOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )

    val animatedOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        StockkoColors.DarkNavy,
                        StockkoColors.Slate
                    )
                )
            )
    ) {
        // Floating blur elements
        Box(
            modifier = Modifier
                .offset(
                    x = (100 + animatedOffset1 * 200).dp,
                    y = (150 + sin(animatedOffset1 * 2 * Math.PI) * 100).dp
                )
                .size(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            StockkoColors.FloatingBlue,
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .blur(40.dp)
        )

        Box(
            modifier = Modifier
                .offset(
                    x = (250 + animatedOffset2 * 150).dp,
                    y = (400 + sin(animatedOffset2 * 3 * Math.PI) * 80).dp
                )
                .size(160.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            StockkoColors.FloatingPurple,
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .blur(50.dp)
        )

        Box(
            modifier = Modifier
                .offset(
                    x = (50 + animatedOffset1 * 180).dp,
                    y = (600 + sin(animatedOffset1 * 1.5 * Math.PI) * 120).dp
                )
                .size(100.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            StockkoColors.FloatingPink,
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .blur(35.dp)
        )
    }
}

@Composable
fun HeroHeader() {
    Column {
        Text(
            text = "Stockko",
            fontSize = 42.sp,
            fontWeight = FontWeight.Black,
            color = StockkoColors.TextPrimary,
            letterSpacing = (-2).sp
        )

        Text(
            text = "Market Intelligence Platform",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = StockkoColors.TextSecondary,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun GlassmorphismSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = StockkoColors.TechBlue.copy(alpha = 0.3f),
                spotColor = StockkoColors.TechBlue.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(24.dp),
        color = StockkoColors.GlassBg,
        border = BorderStroke(
            1.dp,
            StockkoColors.GlassBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = StockkoColors.TextSecondary,
                modifier = Modifier.size(24.dp)
            )

            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        "Search stocks & companies...",
                        color = StockkoColors.TextTertiary,
                        fontWeight = FontWeight.Medium
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedTextColor = StockkoColors.TextPrimary,
                    focusedTextColor = StockkoColors.TextPrimary,
                    cursorColor = StockkoColors.TechBlue
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            )

            if (isSearching) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = StockkoColors.TechBlue
                )
            } else if (query.isNotEmpty()) {
                IconButton(onClick = onClearQuery) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = StockkoColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun MarketAnalysisSection(
    recentSearches: List<String>,
    onSearch: (String) -> Unit
) {
    Column {
        Text(
            text = "Market Analysis",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = StockkoColors.TextPrimary,
            letterSpacing = (-1).sp
        )

        Text(
            text = "Your recent activity and insights",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = StockkoColors.TextSecondary,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        if (recentSearches.isEmpty()) {
            GlassmorphismCard {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        tint = StockkoColors.TechBlue,
                        modifier = Modifier.size(48.dp)
                    )

                    Text(
                        text = "Start Your Journey",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = StockkoColors.TextPrimary,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Text(
                        text = "Search for stocks to begin tracking your favorites",
                        fontSize = 14.sp,
                        color = StockkoColors.TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                recentSearches.forEach { search ->
                    GlassmorphismCard(
                        modifier = Modifier.clickable { onSearch(search) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = StockkoColors.TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )

                            Text(
                                text = search,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StockkoColors.TextPrimary,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .weight(1f)
                            )

                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = StockkoColors.TechBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernStockSection(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    stocks: List<StockItem>,
    onViewAll: () -> Unit,
    onStockClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )

                    Text(
                        text = title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = StockkoColors.TextPrimary,
                        letterSpacing = (-1).sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = StockkoColors.TextSecondary,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Surface(
                onClick = onViewAll,
                shape = RoundedCornerShape(16.dp),
                color = StockkoColors.TechBlue.copy(alpha = 0.2f),
                border = BorderStroke(
                    1.dp,
                    StockkoColors.TechBlue.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = "View All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = StockkoColors.TechBlue,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(stocks, key = { it.symbol }) { stock ->
                GlassmorphismStockCard(
                    stock = stock,
                    onClick = { onStockClick(stock.symbol) }
                )
            }
        }
    }
}

@Composable
fun GlassmorphismStockCard(
    stock: StockItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.5f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = StockkoColors.GlassBg,
        border = BorderStroke(
            1.dp,
            StockkoColors.GlassBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stock.symbol,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = StockkoColors.TextPrimary,
                        letterSpacing = (-0.5).sp
                    )

                    Text(
                        text = stock.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = StockkoColors.TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Status indicator
                val statusColor = if (stock.priceChange >= 0) StockkoColors.BullGreen else StockkoColors.BearRed
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(statusColor, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stock.price,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = StockkoColors.TextPrimary,
                letterSpacing = (-1).sp
            )

            val changeColor = if (stock.priceChange >= 0) StockkoColors.BullGreen else StockkoColors.BearRed
            val changePrefix = if (stock.priceChange >= 0) "+" else ""

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = changeColor.copy(alpha = 0.2f),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "$changePrefix${stock.priceChange} (${changePrefix}${stock.percentChange}%)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = changeColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun TrendingPortfolioSection(
    stocks: List<StockItem>,
    onStockClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Trending Portfolio",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = StockkoColors.TextPrimary,
            letterSpacing = (-1).sp
        )

        Text(
            text = "Most watched stocks this week",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = StockkoColors.TextSecondary,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            stocks.forEach { stock ->
                GlassmorphismListItem(
                    stock = stock,
                    onClick = { onStockClick(stock.symbol) }
                )
            }
        }
    }
}

@Composable
fun GlassmorphismListItem(
    stock: StockItem,
    onClick: () -> Unit
) {
    GlassmorphismCard(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Company Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                StockkoColors.TechBlue,
                                StockkoColors.FloatingPurple
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stock.symbol.take(1),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = stock.symbol,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = StockkoColors.TextPrimary
                )

                Text(
                    text = stock.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = StockkoColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stock.price,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = StockkoColors.TextPrimary,
                    letterSpacing = (-0.5).sp
                )

                val changeColor = if (stock.priceChange >= 0) StockkoColors.BullGreen else StockkoColors.BearRed
                val changePrefix = if (stock.priceChange >= 0) "+" else ""

                Text(
                    text = "$changePrefix${stock.priceChange} (${changePrefix}${stock.percentChange}%)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = changeColor
                )
            }
        }
    }
}

@Composable
fun GlassmorphismCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = StockkoColors.GlassBg,
        border = BorderStroke(
            1.dp,
            StockkoColors.GlassBorder
        )
    ) {
        content()
    }
}

@Composable
fun ModernSearchResultsList(
    results: List<StockItem>,
    onStockClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Search Results",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = StockkoColors.TextPrimary,
            letterSpacing = (-1).sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(results) { stock ->
                GlassmorphismListItem(
                    stock = stock,
                    onClick = { onStockClick(stock.symbol) }
                )
            }
        }
    }
}

@Composable
fun ModernEmptySearchResults(query: String) {
    GlassmorphismCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = StockkoColors.TextSecondary,
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = "No Results Found",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = StockkoColors.TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 24.dp)
            )

            Text(
                text = "We couldn't find any stocks matching '$query'",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = StockkoColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Try searching with a different symbol or company name",
                fontSize = 14.sp,
                color = StockkoColors.TextTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun ModernLoadingIndicator() {
    GlassmorphismCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = StockkoColors.TechBlue,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = "Analyzing Markets",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = StockkoColors.TextPrimary,
                modifier = Modifier.padding(top = 24.dp)
            )

            Text(
                text = "Fetching real-time stock data and insights",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = StockkoColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}