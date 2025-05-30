package velkonost.binance.sdk.enums

/**
 * Enum representing the different API versions available for Binance endpoints.
 * Each version corresponds to a specific set of endpoints and features.
 *
 * @property value The version identifier used in API endpoint URLs
 */
internal enum class ApiVersion(val value: String) {
    /**
     * Version 1 of the public API endpoints.
     * Used for basic market data and trading operations.
     */
    Public("v1"),

    /**
     * Version 3 of the private API endpoints.
     * Used for authenticated operations requiring API key.
     */
    Private("v3"),

    /**
     * Version 2 of the margin trading API.
     * Used for margin trading operations.
     */
    MarginV2("v2"),

    /**
     * Version 3 of the margin trading API.
     * Used for advanced margin trading features.
     */
    MarginV3("v3"),

    /**
     * Version 4 of the margin trading API.
     * Used for the latest margin trading features.
     */
    MarginV4("v4"),

    /**
     * Version 1 of the futures trading API.
     * Used for basic futures trading operations.
     */
    FuturesV1("v1"),

    /**
     * Version 2 of the futures trading API.
     * Used for advanced futures trading features.
     */
    FuturesV2("v2")
}