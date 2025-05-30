package velkonost.binance.sdk.model

import velkonost.binance.sdk.data.model.response.SymbolPositionResponse
import velkonost.binance.sdk.enums.MarginType

/**
 * Represents an open position in a futures trading account.
 * This model contains all relevant information about an active futures position.
 *
 * @property symbol The trading symbol (e.g., "BTCUSDT")
 * @property amount The size of the position in the base asset
 * @property breakEvenPrice The price at which the position breaks even
 * @property unRealizedProfit The current unrealized profit/loss of the position
 * @property liquidationPrice The price at which the position would be liquidated
 * @property leverage The leverage used for this position
 * @property marginType The type of margin used (Cross or Isolated)
 * @property amountUSDT The position size in USDT
 */
data class FuturesOpenPosition(
    val symbol: String,
    val amount: Double,
    val breakEvenPrice: Double,
    val unRealizedProfit: Double,
    val liquidationPrice: Double,
    val leverage: Double,
    val marginType: MarginType,
    val amountUSDT: Double,
)

/**
 * Converts a list of [SymbolPositionResponse] objects to a list of [FuturesOpenPosition] objects.
 * This extension function transforms API responses into the SDK's domain model.
 */
internal fun List<SymbolPositionResponse>.toFuturesOpenPositions(): List<FuturesOpenPosition> =
    map { it.toFuturesOpenPosition() }

/**
 * Converts a [SymbolPositionResponse] object to a [FuturesOpenPosition] object.
 * This extension function transforms the API response format into the SDK's domain model.
 */
internal fun SymbolPositionResponse.toFuturesOpenPosition() = FuturesOpenPosition(
    symbol = symbol,
    amount = positionAmount.toDouble(),
    breakEvenPrice = breakEvenPrice.toDouble(),
    unRealizedProfit = unRealizedProfit.toDouble(),
    liquidationPrice = liquidationPrice.toDouble(),
    leverage = leverage.toDouble(),
    marginType = MarginType.Isolated.takeIf { isolated } ?: MarginType.Cross,
    amountUSDT = isolatedWallet.toDouble()
)