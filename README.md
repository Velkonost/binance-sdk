# Binance SDK

A Kotlin Multiplatform library for interacting with the Binance cryptocurrency exchange API. This SDK provides a clean and type-safe interface for both REST and WebSocket operations, supporting spot and futures trading.

## Features

- **Multiplatform Support**: Works on all platforms supported by Kotlin Multiplatform (JVM, Native, JS)
- **Futures Trading**: Full support for Binance Futures API, including:
  - Position management
  - Balance queries
  - Real-time market data
  - Historical kline data
- **WebSocket Integration**: Real-time market data streams with:
  - Automatic reconnection
  - Connection state management
  - Efficient subscription handling
- **Type Safety**: Strongly typed models and responses
- **Coroutines Support**: Asynchronous operations using Kotlin Coroutines
- **Flow API**: Reactive streams for real-time data using Kotlin Flow
- **Error Handling**: Comprehensive error handling with custom exceptions

## Installation

Add the following to your `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.velkonost:binance-sdk:1.0.0")
}
```

## Quick Start

1. Initialize the SDK with your API credentials:

```kotlin
// Initialize the SDK
BinanceSDK.setup(
    apiKey = "your_api_key",
    apiSecret = "your_api_secret"
)
```

2. Use the SDK to interact with the Binance API:

```kotlin
// Check API connectivity
BinanceSDK.General.ping()

// Get futures account balance
val balance = BinanceSDK.Futures.getBalance("USDT")

// Get open positions
val positions = BinanceSDK.Futures.getOpenPositions()

// Subscribe to real-time market data
BinanceSDK.Socket.startListenUpdates("BTCUSDT", KlineInterval.Minute1)
```

## Key Features in Detail

### Futures Trading

The SDK provides comprehensive support for futures trading operations:

```kotlin
// Get account balance for a specific asset
val balance = BinanceSDK.Futures.getBalance("USDT")

// Get all open positions
val positions = BinanceSDK.Futures.getOpenPositions()

// Get available trading symbols
val symbols = BinanceSDK.Futures.getSymbols()

// Get historical kline data
val klines = BinanceSDK.Futures.getKlines(
    symbol = "BTCUSDT",
    interval = KlineInterval.Minute1,
    start = "4 day ago"
)
```

### WebSocket Operations

Real-time market data is available through WebSocket connections:

```kotlin
// Start listening to connection state
BinanceSDK.Socket.startListenConnections()

// Subscribe to updates for a specific symbol
BinanceSDK.Socket.startListenUpdates(
    symbol = "BTCUSDT",
    interval = KlineInterval.Minute1
)

// Subscribe to updates for all available symbols
BinanceSDK.Socket.startListenAllSymbolsUpdates(
    delayBetweenLaunches = 1000L,
    interval = KlineInterval.Minute1,
    logConnectionsState = true
) { update ->
    // Handle symbol update
    println("Symbol: ${update.symbol}, Price: ${update.kline.close}")
}

// Stop listening to updates
BinanceSDK.Socket.stopListenUpdates("BTCUSDT")
```

### Error Handling

The SDK provides comprehensive error handling:

```kotlin
try {
    val balance = BinanceSDK.Futures.getBalance("USDT")
} catch (e: BinanceSDKException) {
    when (e) {
        is BinanceSDKNotInitializedException -> {
            // SDK not initialized
        }
        else -> {
            // Handle other SDK exceptions
        }
    }
}
```

## Data Models

The SDK provides strongly typed models for all API responses:

- `Kline`: Candlestick data with open, high, low, close prices and volume
- `FuturesBalance`: Account balance information for futures trading
- `FuturesOpenPosition`: Detailed information about open positions
- `SymbolUpdate`: Real-time market data updates

## Best Practices

1. **Initialization**: Always initialize the SDK before use:
   ```kotlin
   BinanceSDK.setup(apiKey, apiSecret)
   ```

2. **Error Handling**: Use try-catch blocks to handle potential exceptions:
   ```kotlin
   try {
       // SDK operations
   } catch (e: BinanceSDKException) {
       // Handle exceptions
   }
   ```

3. **WebSocket Management**: 
   - Monitor connection state using `startListenConnections()`
   - Close unused connections with `stopListenUpdates()`
   - Use appropriate intervals for your use case

4. **Rate Limiting**: Be aware of Binance API rate limits and implement appropriate throttling

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For support, please open an issue in the GitHub repository.
