package velkonost.binance.sdk.model

import velkonost.binance.sdk.data.model.response.SymbolPositionResponse
import velkonost.binance.sdk.enums.MarginType

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

internal fun List<SymbolPositionResponse>.toFuturesOpenPositions(): List<FuturesOpenPosition> =
    map { it.toFuturesOpenPosition() }

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