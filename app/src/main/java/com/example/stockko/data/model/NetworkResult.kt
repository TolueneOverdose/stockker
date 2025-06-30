package com.example.stockko.data.model


sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()

    companion object {
        fun <T> success(data: T): NetworkResult<T> = Success(data)
        fun error(message: String): NetworkResult<Nothing> = Error(message)
        fun loading(): NetworkResult<Nothing> = Loading
    }
}

inline fun <T, R> NetworkResult<T>.fold(
    onSuccess: (T) -> R,
    onError: (String) -> R,
    onLoading: () -> R
): R {
    return when (this) {
        is NetworkResult.Success -> onSuccess(data)
        is NetworkResult.Error -> onError(message)
        is NetworkResult.Loading -> onLoading()
    }
}