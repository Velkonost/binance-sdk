package velkonost.binance.sdk.enums

/**
 * Enum representing the types of margin available for futures trading on Binance.
 * The margin type determines how the account's available balance is used for position management.
 */
enum class MarginType {
    /**
     * Isolated margin mode, where a specific amount of balance is allocated to each position.
     * This mode provides better risk management by limiting potential losses to the allocated margin.
     */
    Isolated,

    /**
     * Cross margin mode, where the entire account balance is used as margin for all positions.
     * This mode allows for more efficient use of available balance but carries higher risk.
     */
    Cross
}