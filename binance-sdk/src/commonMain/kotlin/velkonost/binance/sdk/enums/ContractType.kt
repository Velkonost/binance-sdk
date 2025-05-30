package velkonost.binance.sdk.enums

/**
 * Enum representing the types of futures contracts available on Binance.
 * Different contract types have different characteristics and trading rules.
 *
 * @property value The string representation of the contract type used in API requests
 */
enum class ContractType(val value: String) {
    /**
     * Perpetual futures contracts that don't have an expiration date.
     * These contracts are continuously traded and settled through funding rate mechanisms.
     */
    Perpetual("perpetual"),
    CurrentQuarter("current_quarter"),
    NextQuarter("next_quarter"),
}