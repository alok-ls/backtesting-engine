
## Overview

This backtesting engine is designed to evaluate the historical performance of trading strategies using real stock market data. The system simulates portfolio management, tracks positions, and computes comprehensive performance metrics to help assess strategy effectiveness.

## Features

- **Multi-Strategy Backtesting**: Test multiple strategies simultaneously and compare results
- **Portfolio Management**: Realistic portfolio simulation with position sizing and rebalancing
- **Performance Analytics**: Comprehensive metrics including Sharpe ratio, maximum drawdown, volatility, and more
- **Factor Analysis**: Market beta calculation using OLS regression
- **Visualization**: Automated chart generation for returns and drawdowns
- **Data Export**: Market returns and logs saved to CSV for further analysis
- **Interactive UI**: Optional GUI for viewing results and sector allocations
- **Logging System**: Detailed logs of backtest execution and decisions

## Strategies Implemented

### 1. EMA Strategy (Exponential Moving Average Crossover)
**Type**: Technical Analysis - Momentum Strategy

**Logic**: 
- Generates buy signals when a short-term EMA crosses above a long-term EMA (bullish crossover)
- Generates sell signals when a short-term EMA crosses below a long-term EMA (bearish crossover)
- Holds positions between crossover events

**Default Parameters**:
- Short window: 5 days
- Long window: 20 days

**Use Case**: Captures trending market movements and momentum shifts

### 2. SMA Strategy (Simple Moving Average Crossover)
**Type**: Technical Analysis - Trend Following

**Logic**:
- Similar to EMA but uses simple moving averages
- Buy when short-term SMA crosses above long-term SMA
- Sell when short-term SMA crosses below long-term SMA
- Equal weighting of all prices in the window period

**Default Parameters**:
- Short window: 20 days
- Long window: 100 days

**Use Case**: Identifies long-term trends with less sensitivity to recent price changes

### 3. Value Strategy (Fundamental Analysis)
**Type**: Fundamental Analysis - Value Investing

**Logic**:
- Selects stocks with the lowest P/E (Price-to-Earnings) ratios
- Holds the top X% of stocks by value metric
- Rebalances daily based on fundamental data
- Filters out stocks with negative or missing P/E ratios

**Default Parameters**:
- Top percentile: 10% (holds cheapest 10% of stocks by P/E)

**Use Case**: Exploits value anomalies by investing in undervalued stocks

## 📈 Performance Metrics

The engine calculates and displays the following metrics for each strategy:

### 1. **Average Annual Return** (%)
- **What it measures**: The annualized rate of return over the backtest period
- **Interpretation**: 
  - Higher values indicate better performance
  - Compare against benchmark (S&P 500 typically returns ~10% annually)
  - Must be evaluated alongside risk metrics

### 2. **Volatility** (%)
- **What it measures**: Annualized standard deviation of daily returns
- **Interpretation**: 
  - Represents the strategy's risk level
  - Higher values mean more price fluctuation
  - Lower volatility is generally preferred for given return
  - Typical stocks: 15-25%, market: ~15%

### 3. **Sharpe Ratio**
- **What it measures**: Risk-adjusted returns (excess return per unit of risk)
- **Interpretation**: 
  - Higher is better
  - \> 1.0: Good risk-adjusted returns
  - \> 2.0: Excellent risk-adjusted returns
  - \> 3.0: Exceptional (rare in practice)
  - Compares strategies with different risk profiles

### 4. **Maximum Drawdown** (%)
- **What it measures**: Largest peak-to-trough decline during the backtest period
- **Interpretation**: 
  - Represents worst-case scenario loss
  - Lower (less negative) is better
  - Important for risk management and capital preservation
  - -20% is moderate, -40% is severe, -50%+ is extreme

### 5. **Beta** (β)
- **What it measures**: Strategy's sensitivity to market movements
- **Calculated using**: OLS regression vs market returns
- **Interpretation**: 
  - β = 1.0: Moves with the market
  - β > 1.0: More volatile than market (amplifies market moves)
  - β < 1.0: Less volatile than market
  - β ≈ 0: Market-neutral strategy

### 6. **Treynor Ratio**
- **What it measures**: Risk-adjusted return per unit of systematic risk (beta)
- **Interpretation**: 
  - Higher is better
  - Similar to Sharpe but uses beta instead of total volatility
  - Useful for diversified portfolios where unsystematic risk is diversified away

### 7. **Alpha** (α)
- **What it measures**: Excess return above what would be expected given the strategy's beta
- **Interpretation**: 
  - Positive alpha: Strategy outperforms market on risk-adjusted basis
  - Zero alpha: Returns match market expectations
  - Negative alpha: Strategy underperforms
  - The "holy grail" of active management

### 8. **R-Squared** (R²)
- **What it measures**: Proportion of strategy returns explained by market movements
- **Interpretation**: 
  - 0.0 to 1.0 scale
  - High R² (>0.7): Strategy closely tracks market
  - Low R² (<0.3): Strategy is market-independent
  - Helps assess diversification benefits

### 9. **Signal Accuracy** (%)
- **What it measures**: Percentage of signals that result in profitable trades
- **Interpretation**: 
  - Above 50%: Strategy has predictive power
  - Should not be the only metric (large wins can offset low accuracy)
  - Useful for strategy validation and debugging

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/alok-ls/alok_backtesting_engine.git
   cd alok_backtesting_engine
   ```

2. **Build the project**
   ```bash
   mvn clean compile
   ```

### Running the Backtest

```bash
mvn clean compile
java -cp target/classes com.alok.Main
```

### Expected Output

The backtest will generate:
- **Console logs**: Progress updates and key metrics
- **`out/backtest.log`**: Detailed execution log
- **`out/market_returns.csv`**: Market return data
- **Charts** (PNG files):
  - `EMAStrategy_CumulativeReturns.png`
  - `EMAStrategy_Drawdowns.png`
  - `SMAStrategy_CumulativeReturns.png`
  - `SMAStrategy_Drawdowns.png`
  - `ValueStrategy_CumulativeReturns.png`
  - `ValueStrategy_Drawdowns.png`
  - `CumulativeReturns_Comparison.png`
  - `Drawdowns_Comparison.png`
- **GUI Windows** (if enabled):
  - Performance metrics comparison table
  - Sector allocation reports for each strategy

### Configuration Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `stockCsvPath` | String | `src/main/resources/daily_stock_data.csv` | Path to historical stock data |
| `outDir` | String | `out` | Output directory for results |
| `initialCapital` | double | 1,000,000 | Starting portfolio value |
| `positionSize` | double | 0.15 | Position size as fraction (0.15 = 15%) |
| `emaShortWindow` | int | 5 | EMA short window (days) |
| `emaLongWindow` | int | 20 | EMA long window (days) |
| `smaShortWindow` | int | 20 | SMA short window (days) |
| `smaLongWindow` | int | 100 | SMA long window (days) |
| `valueTopPercentile` | double | 10.0 | Top percentile for value strategy (%) |
| `enableUI` | boolean | true | Display GUI windows |

## 📖 Understanding the Output( After running the code you will get a log file in out folder for analysis)

### Log File Analysis 
The `backtest.log` file contains:
- Initialization parameters
- Data loading progress
- Position entries and exits (if enabled)
- Performance summary
- Error messages and warnings

### Chart Interpretation

#### Cumulative Returns Chart
- **X-axis**: Time (dates)
- **Y-axis**: Cumulative return (%)
- **Interpretation**: 
  - Upward slope = positive returns
  - Steeper slope = faster growth
  - Flat periods = no gains
  - Compare multiple strategies to see relative performance

#### Drawdown Chart
- **X-axis**: Time (dates)
- **Y-axis**: Drawdown from peak (%, negative values)
- **Interpretation**: 
  - Depth: How much strategy lost from peak
  - Duration: How long to recover
  - Frequency: How often drawdowns occur
  - Lower drawdowns = more stable strategy

### Comparison Charts
The combined charts overlay all three strategies, allowing direct performance comparison:
- **Green line**: Often shows EMA strategy
- **Blue line**: Often shows SMA strategy
- **Red line**: Often shows Value strategy

## Project Structure

```
alok_backtesting_engine/
├── pom.xml                          # Maven configuration
├── src/main/
│   ├── java/com/alok/
│   │   ├── Main.java                # Entry point
│   │   ├── app/
│   │   │   ├── App.java             # Main application logic
│   │   │   └── AppConfig.java       # Configuration management
│   │   ├── backtest/
│   │   │   ├── Backtester.java      # Core backtesting engine
│   │   │   └── PortfolioManager.java # Position and capital management
│   │   ├── data/
│   │   │   └── DataLoader.java      # CSV data loading and preprocessing
│   │   ├── evaluation/
│   │   │   ├── FactorRegression.java # Beta/Alpha calculation
│   │   │   └── SignalAccuracy.java  # Signal validation
│   │   ├── model/
│   │   │   └── StockData.java       # Data model for stock records
│   │   ├── performance/
│   │   │   └── PerformanceMetrics.java # Metrics calculation
│   │   ├── strategy/
│   │   │   ├── Strategy.java        # Strategy interface
│   │   │   ├── EMAStrategy.java     # EMA crossover implementation
│   │   │   ├── SmaCrossoverStrategy.java # SMA crossover implementation
│   │   │   └── FundamentalStrategy.java  # Value strategy implementation
│   │   ├── ui/
│   │   │   └── ReportUI.java        # GUI reporting
│   │   ├── util/
│   │   │   └── Log.java             # Logging utility
│   │   └── visualization/
│   │       └── Plotter.java         # Chart generation
│   └── resources/
│       └── daily_stock_data.csv     # Historical stock data
└── out/                              # Generated output (created at runtime)
    ├── backtest.log
    ├── market_returns.csv
    └── *.png (charts)
```

## Future Enhancements

- **Mean Reversion**: Buy oversold, sell overbought
- **Momentum**: Buy recent winners, sell recent losers
- **Machine Learning**: Random Forest, XGBoost, Neural Networks
- **Stop-Loss Orders**: Automatic position exits on adverse moves
- **Portfolio Constraints**: Sector limits, concentration limits, turnover limits

## Technologies Used

- **Java 17**: Core programming language
- **Maven**: Build automation and dependency management
- **OpenCSV 5.9**: CSV parsing and writing
- **Apache Commons Math 3.6.1**: Statistical computations and regression
- **JFreeChart 1.5.4**: Chart generation and visualization
- **SLF4J 2.0.13**: Logging framework

## Data Format

The input CSV file (`daily_stock_data.csv`) should have the following format:

```csv
symbol,date,open,high,low,close,volume,pe_ratio,sector
AAPL,2020-01-02,297.15,300.58,296.40,300.35,33911864,24.5,Technology
AAPL,2020-01-03,297.97,300.38,296.07,297.43,36028992,24.3,Technology
...
```

**Required Fields**:
- `symbol`: Stock ticker symbol
- `date`: Trading date (YYYY-MM-DD)
- `close`: Closing price
- `pe_ratio`: Price-to-Earnings ratio (for fundamental strategy)
- `sector`: Stock sector (for allocation analysis)

**Optional Fields**:
- `open`, `high`, `low`: Price data (currently unused but available for future enhancements)
- `volume`: Trading volume (currently unused but available for future enhancements)
