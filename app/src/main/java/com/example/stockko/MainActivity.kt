package com.example.stockko
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stockko.ui.screen.HomeScreen
import com.example.stockko.ui.screen.StockDetailScreen
import com.example.stockko.ui.screen.StockListScreen
import com.example.stockko.ui.screen.StockListType
import com.example.stockko.ui.theme.StockkoTheme
import com.example.stockko.viewmodel.HomeViewModel
import com.example.stockko.viewmodel.StockDetailViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockkoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val homeViewModel: HomeViewModel = viewModel()
                    val stockDetailViewModel: StockDetailViewModel = viewModel()
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(
                                state = homeViewModel.state.value,
                                onSearch = { query -> homeViewModel.searchStock(query) },
                                onClearSearch = { homeViewModel.clearSearch() },
                                onStockClick = { symbol ->
                                    navController.navigate("stock_detail/$symbol")
                                    homeViewModel.navigateToStockDetail(symbol)
                                },
                                //these are lambda functions
                                onViewAllGainers = {
                                    navController.navigate("stock_list/gainers")
                                },
                                onViewAllLosers = {
                                    navController.navigate("stock_list/losers")
                                }
                            )
                        }
                        composable(
                            route = "stock_list/{type}",
                            arguments = listOf(
                                navArgument("type") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val type = backStackEntry.arguments?.getString("type") ?: "gainers"
                            val stockListType = when (type) {
                                "gainers" -> StockListType.TOP_GAINERS
                                "losers" -> StockListType.TOP_LOSERS
                                else -> StockListType.TOP_GAINERS
                            }
                            val stocks = when (stockListType) {
                                StockListType.TOP_GAINERS -> homeViewModel.getAllTopGainers()
                                StockListType.TOP_LOSERS -> homeViewModel.getAllTopLosers()
                            }
                            StockListScreen(
                                stockType = stockListType,
                                stocks = stocks,
                                onBackPressed = {
                                    navController.popBackStack()
                                },
                                onStockClick = { symbol ->
                                    navController.navigate("stock_detail/$symbol")
                                    homeViewModel.navigateToStockDetail(symbol)
                                }
                            )
                        }
                        composable(
                            route = "stock_detail/{symbol}",
                            arguments = listOf(
                                navArgument("symbol") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val symbol = backStackEntry.arguments?.getString("symbol") ?: ""
                            StockDetailScreen(
                                symbol = symbol,
                                onBackPressed = { navController.popBackStack() },
                                viewModel = stockDetailViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}