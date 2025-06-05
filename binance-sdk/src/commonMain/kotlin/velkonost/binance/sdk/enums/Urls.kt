package velkonost.binance.sdk.enums

/**
 * Enum representing the base URLs for different Binance REST API services.
 * These URLs are used as the foundation for constructing complete API endpoints.
 *
 * @property value The base URL for the corresponding API service
 */
internal enum class Urls(val value: String) {
    /**
     * Base URL for the main Binance API endpoints.
     * Used for spot trading and general market data.
     */
    Api("https://api.binance.com/api/"),

    PApi("https://papi.binance.com/papi/"),

    /**
     * Base URL for the Binance margin trading API.
     * Used for margin trading operations.
     */
    MarginApi("https://api.binance.com/sapi/"),

    /**
     * Base URL for the Binance Futures API.
     * Used for futures trading operations.
     */
    Futures("https://fapi.binance.com/fapi/"),

    /**
     * Base URL for the Binance Futures data API.
     * Used for historical futures data and analytics.
     */
    FuturesData("https://fapi.binance.com/futures/data/")
}

/**
 * Enum representing the WebSocket URLs for different Binance real-time data streams.
 * These URLs are used for establishing WebSocket connections for live market data.
 *
 * @property value The WebSocket URL for the corresponding data stream
 */
internal enum class SocketUrls(val value: String) {
    /**
     * WebSocket URL for spot market data streams.
     * Used for real-time spot trading data.
     */
    Stream("wss://stream.binance.com:9443/ws/"),

    /**
     * WebSocket URL for futures market data streams.
     * Used for real-time futures trading data.
     */
    FStream("wss://fstream.binance.com/ws/"),

    /**
     * WebSocket URL for delivery futures market data streams.
     * Used for real-time delivery futures trading data.
     */
    DStream("wss://dstream.binance.com/ws/"),

    /**
     * WebSocket URL for vanilla options market data streams.
     * Used for real-time options trading data.
     */
    VStream("wss://vstream.binance.com/ws/")
}