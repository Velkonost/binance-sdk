package velkonost.binance.sdk.enums

/**
 * Enum representing the available time intervals for candlestick (kline) data in the Binance API.
 * These intervals determine the granularity of market data, from minutes to months.
 *
 * @property value The string representation of the interval used in API requests
 */
enum class KlineInterval(val value: String) {
    /**
     * 1-minute candlestick interval
     */
    Minute1("1m"),

    /**
     * 3-minute candlestick interval
     */
    Minute3("3m"),

    /**
     * 5-minute candlestick interval
     */
    Minute5("5m"),

    /**
     * 15-minute candlestick interval
     */
    Minute15("15m"),

    /**
     * 30-minute candlestick interval
     */
    Minute30("30m"),

    /**
     * 1-hour candlestick interval
     */
    Hour1("1h"),

    /**
     * 2-hour candlestick interval
     */
    Hour2("2h"),

    /**
     * 4-hour candlestick interval
     */
    Hour4("4h"),

    /**
     * 6-hour candlestick interval
     */
    Hour6("6h"),

    /**
     * 8-hour candlestick interval
     */
    Hour8("8h"),

    /**
     * 12-hour candlestick interval
     */
    Hour12("12h"),

    /**
     * 1-day candlestick interval
     */
    Day1("1d"),

    /**
     * 3-day candlestick interval
     */
    Day3("3d"),

    /**
     * 1-week candlestick interval
     */
    Week1("1w"),

    /**
     * 1-month candlestick interval
     */
    Month1("1M")
}