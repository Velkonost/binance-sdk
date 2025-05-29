package velkonost.binance.sdk.enums

internal enum class ApiVersion(val value: String) {
    Public("v1"),
    Private("v3"),
    MarginV2("v2"),
    MarginV3("v3"),
    MarginV4("v4"),
    FuturesV1("v1"),
    FuturesV2("v2")
}