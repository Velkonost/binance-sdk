package velkonost.binance.sdk.model

import velkonost.binance.sdk.data.model.response.SymbolPositionResponse
import velkonost.binance.sdk.enums.MarginType
import java.math.BigDecimal

data class FuturesOpenPosition(
    val symbol: String,
    val amount: BigDecimal,
    val breakEvenPrice: BigDecimal,
    val unRealizedProfit: BigDecimal,
    val liquidationPrice: BigDecimal,
    val leverage: BigDecimal,
    val marginType: MarginType,
    val amountUSDT: BigDecimal,
)

internal fun List<SymbolPositionResponse>.toFuturesOpenPositions(): List<FuturesOpenPosition> = map { it.toFuturesOpenPosition() }
internal fun SymbolPositionResponse.toFuturesOpenPosition() = FuturesOpenPosition(
    symbol = symbol,
    amount = BigDecimal(positionAmount),
    breakEvenPrice = BigDecimal(breakEvenPrice),
    unRealizedProfit = BigDecimal(unRealizedProfit),
    liquidationPrice = BigDecimal(liquidationPrice),
    leverage = BigDecimal(leverage),
    marginType = MarginType.Isolated.takeIf { isolated } ?: MarginType.Cross,
    amountUSDT = BigDecimal(isolatedWallet)
)