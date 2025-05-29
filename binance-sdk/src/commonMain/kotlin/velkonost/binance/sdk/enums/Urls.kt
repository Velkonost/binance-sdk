package velkonost.binance.sdk.enums

internal enum class Urls(val value: String) {
    Api("https://api.binance.com/api/"),
    MarginApi("https://api.binance.com/sapi/"),
    Futures("https://fapi.binance.com/fapi/"),
    FuturesData("https://fapi.binance.com/futures/data/")

}

internal enum class SocketUrls(val value: String) {
    Stream("wss://stream.binance.com:9443/ws/"),
    FStream("wss://fstream.binance.com/ws/"),
    DStream("wss://dstream.binance.com/ws/"),
    VStream("wss://vstream.binance.com/ws/")
}