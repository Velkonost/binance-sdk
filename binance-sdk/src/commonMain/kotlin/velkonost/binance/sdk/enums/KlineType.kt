package velkonost.binance.sdk.enums

/**
 * Enum representing the different types of candlestick (kline) data available in the Binance API.
 * This is used to specify which market type's data should be retrieved.
 *
 * @property value The numeric identifier used in API requests
 */
internal enum class KlineType(val value: Int) {
    /**
     * Represents candlestick data from the spot market.
     */
    Spot(1),

    /**
     * Represents candlestick data from the USDT-margined futures market.
     */
    Futures(2),

    /**
     * Represents candlestick data from the coin-margined futures market.
     */
    FuturesCoin(3)
}