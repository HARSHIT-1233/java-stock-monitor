# Stock Monitor

A real-time stock price monitoring application built with Java and JavaFX. Track multiple stock symbols and receive alerts when prices reach specified thresholds.

## Features

- **Real-Time Monitoring**: Monitor stock prices with automatic polling every 5 seconds
- **Multiple Stock Tracking**: Add and track multiple stock symbols simultaneously
- **Price Change Tracking**: View price changes and percentage changes for each stock
- **Alert System**: Set custom price alerts to receive notifications when thresholds are met
- **JavaFX GUI**: User-friendly desktop interface built with JavaFX
- **Responsive Design**: 800x600 window with intuitive dashboard layout

## Project Structure

```
StockMonitor/
├── pom.xml                           # Maven project configuration
├── src/
│   └── main/
│       ├── java/com/stockmonitor/
│       │   ├── Main.java             # Application entry point
│       │   ├── model/
│       │   │   ├── Stock.java        # Stock data model
│       │   │   └── Alert.java        # Alert configuration model
│       │   ├── service/
│       │   │   ├── StockService.java # Core stock management service
│       │   │   └── AlertManager.java # Alert handling service
│       │   ├── ui/
│       │   │   └── DashboardController.java # JavaFX controller
│       │   └── util/
│       │       └── ApiClient.java    # External API client
│       └── resources/
│           └── fxml/
│               └── dashboard.fxml    # JavaFX layout file
└── target/                           # Compiled output
```

## Technology Stack

- **Language**: Java 11
- **UI Framework**: JavaFX 17.0.6 (LTS)
- **Build Tool**: Maven 3.x
- **JSON Processing**: org.json 20231013
- **IDE**: Eclipse, IntelliJ IDEA, or Visual Studio Code

## Requirements

- Java 11 or higher
- Maven 3.6 or higher
- Internet connection (for API calls)

## Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd StockMonitor
   ```

2. Install dependencies:
   ```bash
   mvn clean install
   ```

## Building

Build the project using Maven:

```bash
mvn clean package
```

This will compile the Java source code and package it into a JAR file in the `target/` directory.

## Running

### Using Maven:

```bash
mvn javafx:run
```

### Using Java directly (after building):

```bash
java -jar target/stock-monitor-1.0-SNAPSHOT.jar
```

## Usage

1. Launch the application
2. Enter a stock symbol (e.g., AAPL, GOOGL, MSFT) in the input field
3. Click "Add Stock" to start monitoring the symbol
4. View real-time price updates in the dashboard
5. (Optional) Set price alerts for specific stocks
6. Alerts will trigger when the stock price crosses the threshold you set

## API Integration

The application uses an external API to fetch real-time stock prices. The `ApiClient` class handles all API communication with automatic error handling and retry logic.

## Project Components

### Core Services

- **StockService**: Manages stock tracking, polling, and update callbacks
  - Runs background polling every 5 seconds
  - Maintains thread-safe stock data using ConcurrentHashMap
  - Supports custom callbacks for updates and errors

- **AlertManager**: Handles alert configuration and triggering
  - Creates and manages price alerts
  - Evaluates alert conditions against current prices

### Models

- **Stock**: Represents a stock with:
  - Symbol, current price, previous price
  - Price change calculations
  - Timestamp tracking

- **Alert**: Represents alert configuration:
  - Stock symbol, threshold price
  - Alert type (above/below price)

### UI Components

- **DashboardController**: Main JavaFX controller
  - Displays stock list with prices and changes
  - Provides UI for adding stocks and setting alerts
  - Handles user interactions

## Configuration

Edit `pom.xml` to adjust:

- **Java Version**: Change `maven.compiler.source` and `maven.compiler.target` properties
- **JavaFX Version**: Modify the `javafx.version` property
- **Dependencies**: Add or update libraries in the `<dependencies>` section

## Development Notes

- The application uses daemon threads for background polling to ensure clean shutdown
- Thread-safe collections (ConcurrentHashMap) are used for concurrent access
- Price changes are tracked to calculate percentage changes
- All timestamp updates use LocalDateTime for consistency

## Troubleshooting

### "Cannot find fxml/dashboard.fxml"
Ensure the FXML file exists in `src/main/resources/fxml/dashboard.fxml` and the project is built correctly.

### API Connection Issues
Check your internet connection and verify the API endpoint is accessible.

### JavaFX Module Issues
Ensure JavaFX modules are properly configured in your IDE:
- IntelliJ: Add JavaFX library to project structure
- Eclipse: Add JavaFX library to build path
- VS Code: Ensure appropriate launch configuration

## License

[Add your license information here]

## Contributing

[Add contribution guidelines here]

## Support

For issues and questions, please open an issue in the repository.
