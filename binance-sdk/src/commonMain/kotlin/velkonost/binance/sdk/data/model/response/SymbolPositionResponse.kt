package velkonost.binance.sdk.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents detailed information about a position in a futures trading account.
 * This model contains comprehensive data about an open position, including
 * position size, entry price, unrealized profit/loss, and margin information.
 *
 * @property symbol The trading pair symbol (e.g., "BTCUSDT")
 * @property positionAmount The size of the position in the base asset
 * @property entryPrice The average entry price of the position
 * @property breakEvenPrice The price at which the position breaks even
 * @property markPrice The current mark price of the symbol
 * @property unRealizedProfit The current unrealized profit/loss
 * @property liquidationPrice The price at which the position would be liquidated
 * @property leverage The leverage used for this position
 * @property maxNotionalValue The maximum notional value allowed for the position
 * @property marginType The type of margin used (CROSSED or ISOLATED)
 * @property isolatedMargin The margin allocated for isolated margin positions
 * @property isAutoAddMargin Whether auto-margin replenishment is enabled
 * @property positionSide The side of the position (BOTH, LONG, or SHORT)
 * @property notional The notional value of the position
 * @property isolatedWallet The balance in the isolated margin wallet
 * @property updateTime The timestamp of the last position update
 * @property isolated Whether the position is using isolated margin
 * @property adlQuantile The Auto-Deleveraging (ADL) quantile of the position
 */
@Serializable
internal data class SymbolPositionResponse(
    val symbol: String,
    @SerialName("positionAmt")
    val positionAmount: String,
    val entryPrice: String,
    val breakEvenPrice: String,
    val markPrice: String,
    val unRealizedProfit: String,
    val liquidationPrice: String,
    val leverage: String,
    val maxNotionalValue: String,
    val marginType: String,
    val isolatedMargin: String,
    val isAutoAddMargin: String,
    val positionSide: String,
    val notional: Float,
    val isolatedWallet: String,
    val updateTime: Long,
    val isolated: Boolean,
    val adlQuantile: Int
)