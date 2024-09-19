package velkonost.binance.sdk.enums

enum class KlineInterval(val value: String) {
    Minute1("1m"),
    Minute3("3m"),
    Minute5("5m"),
    Minute15("15m"),
    Minute30("30m"),
    Hour1("1h"),
    Hour2("2h"),
    Hour4("4h"),
    Hour6("6h"),
    Hour8("8h"),
    Hour12("12h"),
    Day1("1d"),
    Day3("3d"),
    Week1("1w"),
    Month1("1M")
}