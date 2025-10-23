package velkonost.binance.sdk

import io.ktor.client.plugins.logging.*
import kotlinx.serialization.Serializable

@Serializable
data class BinanceConfig(
    val apiKey: String,
    val apiSecret: String,
    val baseUrl: String = "https://fapi.binance.com",
    val timeoutMillis: Long = 30_000L,
    val maxRetries: Int = 5,
    val retryDelayMillis: Long = 3_000L,
    val logLevel: LogLevel = LogLevel.INFO,
    val webSocketConfig: WebSocketConfig = WebSocketConfig(),
    val rateLimitConfig: RateLimitConfig = RateLimitConfig()
) {
    init {
        require(apiKey.isNotBlank()) { "API key cannot be blank" }
        require(apiSecret.isNotBlank()) { "API secret cannot be blank" }
        require(timeoutMillis > 0) { "Timeout must be positive" }
        require(maxRetries >= 0) { "Max retries cannot be negative" }
    }
}

@Serializable
data class WebSocketConfig(
    val pingIntervalSeconds: Long = 5,
    val reconnectDelayMillis: Long = 5_000L,
    val maxReconnectAttempts: Int = 10,
    val bufferSize: Int = 64,
    val maxConcurrency: Int = 20
)

@Serializable
data class RateLimitConfig(
    val requestsPerMinute: Int = 1200,
    val weightPerMinute: Int = 6000,
    val enableThrottling: Boolean = true
)

// Builder для конфигурации
class BinanceConfigBuilder {
    private var apiKey: String = ""
    private var apiSecret: String = ""
    private var baseUrl: String = "https://fapi.binance.com"
    private var timeoutMillis: Long = 30_000L
    private var maxRetries: Int = 5
    private var retryDelayMillis: Long = 3_000L
    private var logLevel: LogLevel = LogLevel.INFO
    private var webSocketConfig: WebSocketConfig = WebSocketConfig()
    private var rateLimitConfig: RateLimitConfig = RateLimitConfig()

    fun apiKey(key: String) = apply { apiKey = key }
    fun apiSecret(secret: String) = apply { apiSecret = secret }
    fun baseUrl(url: String) = apply { baseUrl = url }
    fun timeoutMillis(timeout: Long) = apply { timeoutMillis = timeout }
    fun maxRetries(retries: Int) = apply { maxRetries = retries }
    fun retryDelayMillis(delay: Long) = apply { retryDelayMillis = delay }
    fun logLevel(level: LogLevel) = apply { logLevel = level }
    fun webSocketConfig(config: WebSocketConfig) = apply { webSocketConfig = config }
    fun rateLimitConfig(config: RateLimitConfig) = apply { rateLimitConfig = config }

    fun build(): BinanceConfig = BinanceConfig(
        apiKey = apiKey,
        apiSecret = apiSecret,
        baseUrl = baseUrl,
        timeoutMillis = timeoutMillis,
        maxRetries = maxRetries,
        retryDelayMillis = retryDelayMillis,
        logLevel = logLevel,
        webSocketConfig = webSocketConfig,
        rateLimitConfig = rateLimitConfig
    )
}