package com.example.stockko.data.network



import com.example.stockko.data.model.CompanyOverviewResponse
import com.example.stockko.data.model.GlobalQuoteResponse
import com.example.stockko.data.model.SearchResponse
import com.example.stockko.data.model.StockHistoricalData
import com.example.stockko.data.model.TopGainersLosersResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.stockko.BuildConfig

interface StockApiService {

    @GET("query")
    suspend fun getGlobalQuote(
        @Query("function") function: String = "GLOBAL_QUOTE",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String = StockApi.API_KEY
    ): GlobalQuoteResponse

    @GET("query")
    suspend fun searchStocks(
        @Query("function") function: String = "SYMBOL_SEARCH",
        @Query("keywords") keywords: String,
        @Query("apikey") apiKey: String = StockApi.API_KEY
    ): SearchResponse

    @GET("query")
    suspend fun getCompanyOverview(
        @Query("function") function: String = "OVERVIEW",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String = StockApi.API_KEY
    ): CompanyOverviewResponse

    @GET("query")
    suspend fun getTopGainersLosers(
        @Query("function") function: String = "TOP_GAINERS_LOSERS",
        @Query("apikey") apiKey: String = StockApi.API_KEY
    ): TopGainersLosersResponse

    @GET("query")
    suspend fun getHistoricalData(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("symbol") symbol: String,
        @Query("outputsize") outputSize: String = "compact",
        @Query("apikey") apiKey: String = StockApi.API_KEY
    ): List<StockHistoricalData>
}

object StockApi {
    private const val BASE_URL = "https://www.alphavantage.co/"
    //
    val API_KEY: String
        get() = BuildConfig.ALPHA_VANTAGE_API_KEY

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val api: StockApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StockApiService::class.java)
    }
}
