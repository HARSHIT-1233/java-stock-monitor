package com.stockmonitor.ui;

import com.stockmonitor.model.Alert;
import com.stockmonitor.model.Stock;
import com.stockmonitor.service.AlertManager;
import com.stockmonitor.service.StockService;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DashboardController {

    @FXML private StackPane rootPane;
    
    @FXML private Circle statusIndicator;
    @FXML private Label lblNetworkStatus;
    
    @FXML private TextField txtSearch;
    @FXML private TextField txtAddSymbol;
    @FXML private TableView<Stock> stockTable;
    @FXML private TableColumn<Stock, String> colSymbol;
    @FXML private TableColumn<Stock, Double> colPrice;
    @FXML private TableColumn<Stock, String> colChange;
    @FXML private TableColumn<Stock, String> colTime;

    @FXML private ComboBox<String> comboAlertSymbol;
    @FXML private ComboBox<Alert.Condition> comboCondition;
    @FXML private TextField txtTargetPrice;

    @FXML private ListView<Alert> listActiveAlerts;
    @FXML private ListView<Alert> listTriggeredAlerts;

    private StockService stockService;
    private AlertManager alertManager;

    private final ObservableList<Stock> stockObservableList = FXCollections.observableArrayList();
    private final ObservableList<Alert> activeAlertsList = FXCollections.observableArrayList();
    private final ObservableList<Alert> triggeredAlertsList = FXCollections.observableArrayList();

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    public void initialize() {
        stockService = new StockService();
        alertManager = new AlertManager(stockService);

        setupTableView();
        setupListCells();
        setupSearchFilter();

        comboCondition.setItems(FXCollections.observableArrayList(Alert.Condition.values()));
        comboCondition.getSelectionModel().selectFirst();
        
        // Input validation for target price button disablement (Optional enhancement)
        // Handled via simple exception checking in handleSetAlert for now

        setupBackgroundThreads();
    }
    
    private void setupSearchFilter() {
        FilteredList<Stock> filteredData = new FilteredList<>(stockObservableList, p -> true);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(stock -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return stock.getSymbol().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        stockTable.setItems(filteredData);
    }

    private void setupTableView() {
        colSymbol.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Price formatting
        colPrice.setCellFactory(column -> new TableCell<Stock, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Stock stock = getTableView().getItems().get(getIndex());
                    setText(String.format("$%.2f", price));
                    
                    if (stock.getPriceChange() > 0) {
                        setStyle("-fx-text-fill: #00e676; -fx-font-weight: bold;"); // Green
                    } else if (stock.getPriceChange() < 0) {
                        setStyle("-fx-text-fill: #ff5252; -fx-font-weight: bold;"); // Red
                    } else {
                        setStyle("-fx-text-fill: white;");
                    }
                }
            }
        });

        // Change formatting
        colChange.setCellValueFactory(cellData -> {
            Stock stock = cellData.getValue();
            double pct = stock.getChangePercent();
            String prefix = pct > 0 ? "+" : "";
            return new javafx.beans.property.SimpleStringProperty(String.format("%s%.2f%%", prefix, pct));
        });
        
        colChange.setCellFactory(column -> new TableCell<Stock, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    if (item.startsWith("+")) {
                        setStyle("-fx-text-fill: #00e676;");
                    } else if (item.startsWith("-")) {
                        setStyle("-fx-text-fill: #ff5252;");
                    } else {
                        setStyle("-fx-text-fill: #8c8c9b;");
                    }
                }
            }
        });

        // Time formatting
        colTime.setCellValueFactory(cellData -> {
            if (cellData.getValue().getTimestamp() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getTimestamp().format(timeFormatter));
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
    }

    private void setupListCells() {
        listActiveAlerts.setItems(activeAlertsList);
        listTriggeredAlerts.setItems(triggeredAlertsList);

        // Custom Cell Factory for Active Alerts
        listActiveAlerts.setCellFactory(param -> new ListCell<Alert>() {
            @Override
            protected void updateItem(Alert alert, boolean empty) {
                super.updateItem(alert, empty);
                if (empty || alert == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox();
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    Label lblDesc = new Label(alert.toString());
                    lblDesc.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                    
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    
                    Button btnDelete = new Button("✖");
                    btnDelete.getStyleClass().add("btn-danger-small");
                    btnDelete.setOnAction(e -> {
                        alertManager.getActiveAlerts().remove(alert);
                        activeAlertsList.remove(alert);
                        showToast("Alert removed", false);
                    });
                    
                    hbox.getChildren().addAll(lblDesc, spacer, btnDelete);
                    setGraphic(hbox);
                }
            }
        });

        // Custom Cell Factory for Triggered Alerts
        listTriggeredAlerts.setCellFactory(param -> new ListCell<Alert>() {
            @Override
            protected void updateItem(Alert alert, boolean empty) {
                super.updateItem(alert, empty);
                if (empty || alert == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox(5);
                    vbox.setStyle("-fx-background-color: rgba(255, 82, 82, 0.15); -fx-background-radius: 5px; -fx-padding: 8px; -fx-border-color: #ff5252; -fx-border-radius: 5px;");
                    
                    HBox topBox = new HBox(5);
                    Label lblIcon = new Label("🔔");
                    Label lblTitle = new Label("ALERT: " + alert.getSymbol());
                    lblTitle.setStyle("-fx-text-fill: #ff5252; -fx-font-weight: bold;");
                    topBox.getChildren().addAll(lblIcon, lblTitle);
                    
                    Label lblDesc = new Label("Trig: " + alert.getCondition().getSymbol() + " " + alert.getTargetPrice());
                    lblDesc.setStyle("-fx-text-fill: white;");
                    
                    String timeStr = alert.getTriggeredTime() != null ? alert.getTriggeredTime().format(timeFormatter) : "N/A";
                    Label lblTime = new Label(timeStr);
                    lblTime.setStyle("-fx-text-fill: #8c8c9b; -fx-font-size: 11px;");
                    
                    vbox.getChildren().addAll(topBox, lblDesc, lblTime);
                    setGraphic(vbox);
                }
            }
        });
    }

    private void setupBackgroundThreads() {
        stockService.setOnUpdateCallback(stock -> {
            Platform.runLater(() -> {
                Optional<Stock> existing = stockObservableList.stream()
                        .filter(s -> s.getSymbol().equals(stock.getSymbol()))
                        .findFirst();

                if (existing.isPresent()) {
                    int index = stockObservableList.indexOf(existing.get());
                    stockObservableList.set(index, stock);
                } else {
                    stockObservableList.add(stock);
                }
                
                if (!comboAlertSymbol.getItems().contains(stock.getSymbol())) {
                    comboAlertSymbol.getItems().add(stock.getSymbol());
                }
                
                stockTable.refresh();
                updateLiveStatus(true);
            });
        });

        stockService.setOnErrorCallback(err -> {
            Platform.runLater(() -> {
                updateLiveStatus(false);
            });
        });

        alertManager.setOnAlertTriggeredCallback(alert -> {
            Platform.runLater(() -> {
                activeAlertsList.remove(alert); // Remove from active when triggered
                triggeredAlertsList.add(0, alert);
                showToast("ALERT! " + alert.getSymbol() + " condition met!", true);
            });
        });

        stockService.startPolling();
        alertManager.startChecking();
    }

    @FXML
    private void handleAddStock() {
        String symbol = txtAddSymbol.getText().trim().toUpperCase();
        if (!symbol.isEmpty()) {
            stockService.addStock(symbol);
            txtAddSymbol.clear();
            showToast("Added stock: " + symbol, false);
            // Re-assert status temporarily
            updateLiveStatus(true);
        }
    }

    @FXML
    private void handleSetAlert() {
        String symbol = comboAlertSymbol.getValue();
        if (symbol == null || symbol.trim().isEmpty()) {
            showToast("Error: Select a stock first", true);
            return;
        }

        try {
            double price = Double.parseDouble(txtTargetPrice.getText().trim());
            Alert.Condition condition = comboCondition.getValue();
            Alert alert = new Alert(symbol, condition, price);
            alertManager.addAlert(alert);
            activeAlertsList.add(alert);
            
            txtTargetPrice.clear();
            showToast("Alert generated for " + symbol, false);
        } catch (NumberFormatException e) {
            showToast("Error: Invalid price format", true);
        }
    }

    @FXML
    private void handleRefresh() {
        // Manually trigger a UI refresh / data fetch cycle internally
        showToast("Refreshing data...", false);
        stockTable.refresh();
    }

    private void updateLiveStatus(boolean isOk) {
        if (isOk) {
            statusIndicator.setFill(Color.web("#00e676"));
            lblNetworkStatus.setText("Live");
            lblNetworkStatus.setStyle("-fx-text-fill: #00e676; -fx-font-weight: bold;");
        } else {
            statusIndicator.setFill(Color.web("#ff5252"));
            lblNetworkStatus.setText("Disconnected");
            lblNetworkStatus.setStyle("-fx-text-fill: #ff5252; -fx-font-weight: bold;");
        }
    }
    
    /**
     * Native custom Toast Notification overlay
     */
    private void showToast(String message, boolean isError) {
        Label toast = new Label(message);
        toast.getStyleClass().add("toast");
        if (isError) {
            toast.getStyleClass().add("toast-error");
        } else {
            toast.getStyleClass().add("toast-success");
        }
        
        StackPane.setAlignment(toast, Pos.TOP_CENTER);
        StackPane.setMargin(toast, new javafx.geometry.Insets(20, 0, 0, 0));
        
        rootPane.getChildren().add(toast);
        
        // Slide in animation
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), toast);
        slideIn.setFromY(-50);
        slideIn.setToY(0);
        slideIn.play();
        
        // Hide after some time
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> {
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), toast);
            slideOut.setFromY(0);
            slideOut.setToY(-50);
            slideOut.setOnFinished(ev -> rootPane.getChildren().remove(toast));
            slideOut.play();
        });
        delay.play();
    }

    public void shutdown() {
        stockService.stopPolling();
        alertManager.stopChecking();
    }
}
