# 📈 Stock Monitor

A desktop-based **Stock Monitor** application built with **JavaFX** and **Maven** that allows users to track real-time stock prices, monitor market movements, and create custom price alerts. The application features an intuitive graphical interface and periodically fetches stock data from the Yahoo Finance API.

---

## ✨ Features

* 📊 Track multiple stock symbols simultaneously
* 🔄 Automatic stock price refresh
* 🚨 Create custom price alerts
* 📈 View current and previous stock prices
* 🎨 Modern JavaFX user interface
* ⚡ Real-time data fetched from Yahoo Finance
* 🔍 Search and filter tracked stocks
* 🖥️ Responsive dashboard built using FXML

---

## 🛠️ Technologies Used

* Java 11
* JavaFX 17
* Maven
* JSON (org.json)
* Java HTTP Client
* Yahoo Finance API

---

## 📂 Project Structure

```text
StockMonitor/
│── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/stockmonitor/
│   │   │       ├── Main.java
│   │   │       ├── model/
│   │   │       │   ├── Stock.java
│   │   │       │   └── Alert.java
│   │   │       ├── service/
│   │   │       │   ├── StockService.java
│   │   │       │   └── AlertManager.java
│   │   │       ├── ui/
│   │   │       │   └── DashboardController.java
│   │   │       └── util/
│   │   │           └── ApiClient.java
│   │   └── resources/
│   │       ├── css/
│   │       └── fxml/
│── pom.xml
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

* Java JDK 11 or above
* Maven 3.8+
* Internet connection (for fetching stock prices)

---

## 📥 Clone the Repository

```bash
git clone https://github.com/your-username/StockMonitor.git
cd StockMonitor
```

---

## ▶️ Build the Project

```bash
mvn clean install
```

---

## ▶️ Run the Application

```bash
mvn javafx:run
```

Alternatively, execute the generated JAR after building the project.

---

## 📦 Dependencies

The project uses the following major dependencies:

* JavaFX Controls
* JavaFX FXML
* org.json
* Java HTTP Client

All dependencies are managed through Maven.

---

## 🏗️ Project Architecture

```
User Interface (JavaFX)
          │
          ▼
Dashboard Controller
          │
          ▼
Business Services
 ├── StockService
 └── AlertManager
          │
          ▼
API Client
          │
          ▼
Yahoo Finance API
```

---

## 📋 Core Components

### Main

* Launches the JavaFX application.
* Loads the dashboard UI.

### DashboardController

* Handles user interactions.
* Displays stock information.
* Manages alerts and dashboard updates.

### StockService

* Retrieves stock prices.
* Periodically refreshes market data.
* Maintains tracked stocks.

### AlertManager

* Stores user-defined alerts.
* Continuously checks alert conditions.
* Triggers notifications when conditions are met.

### ApiClient

* Connects to the Yahoo Finance API.
* Sends HTTP requests.
* Parses JSON responses.

### Model Classes

* **Stock** – Represents stock information.
* **Alert** – Represents user-defined price alerts.

---

## 🎯 Learning Outcomes

This project demonstrates:

* JavaFX GUI development
* MVC design pattern
* Maven project management
* REST API integration
* HTTP Client usage
* JSON parsing
* Multithreading and scheduled tasks
* Object-Oriented Programming
* Event-driven programming

---

## 🔮 Future Enhancements

* 📉 Interactive stock charts
* ⭐ Favorite/watchlist support
* 🔔 Desktop notifications
* 🌙 Dark mode
* 💾 Local database integration
* 📊 Historical stock analysis
* 🔐 User authentication
* 📱 Responsive UI improvements

---

## 👨‍💻 Author

Harshit Singh

---

## 📄 License

This project is intended for educational and learning purposes.
