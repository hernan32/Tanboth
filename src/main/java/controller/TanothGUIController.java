package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.GameParser;
import model.adventure.Adventure;
import model.adventure.Criteria;
import model.adventure.criterias.GoldCriteria;
import model.adventure.exception.AdventureRunningException;
import model.exception.TimeOutException;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class TanothGUIController extends Task<Void> {
    //Connection
    private final String propFileName = "config.properties";
    private GameParser game;
    //FXML
    @FXML
    private TextArea fxMainTextArea;
    @FXML
    private Button fxTray;
    @FXML
    private Label fxStatus;
    //Stage
    private SystemTray tray;
    private TrayIcon trayIcon;
    private Stage stage;
    //Tanoth Attributes
    private int adventuresMade;
    private int freeAdventures;
    private int inventorySpaces;
    private String questStatus;
    //Controller
    private String mainContentText;
    private int refreshTimer = 10; //Seconds

    public TanothGUIController() throws IOException, InterruptedException {
        Properties prop = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config/" + propFileName);
        prop.load(inputStream);
        String user = prop.getProperty("user");
        String password = prop.getProperty("password");
        String serverURL = prop.getProperty("serverURL");
        String serverNumber = prop.getProperty("serverNumber");
        game = new GameParser(user, password, serverURL, serverNumber);
    }

    @Override
    protected Void call() throws Exception {
        String status = "";
        while (true) {
            updateMainContent();
            if (questStatus.equals("Stopped.")) {
                if (adventuresMade < freeAdventures) {
                    //startBot();
                    status = "Free quest available. Starting Quest...";
                } else {
                    status = "No free quest available. Waiting for new quests...";
                    refreshTimer = 3600;
                }
            }
            setCurrentStatus(status + " (" + refreshTimer + " Seconds)");
            TimeUnit.SECONDS.sleep(refreshTimer);
        }
    }

    @FXML
    public void initialize() {
        fxTray.setDefaultButton(false);
        TanothGUIController controller = this;
        new Thread(controller).start();
    }

    private void updateMainContent() throws IOException, InterruptedException {
        inventorySpaces = game.getInventorySpace();
        try {
            adventuresMade = game.getAdventuresMadeToday();
            freeAdventures = game.getFreeAdventuresPerDay();
            questStatus = "Stopped.";
            setMainContentText(adventuresMade, freeAdventures, inventorySpaces, questStatus);
        } catch (AdventureRunningException ex) {
            freeAdventures = -1;
            adventuresMade = -1;
            questStatus = "Running.";
            setMainContentText("Can't get information when - Quest Running -");
            setCurrentStatus("Waiting... (" + refreshTimer + " Seconds)");
        }
        fxMainTextArea.setText(mainContentText);
    }

    public void startBot() throws IOException, InterruptedException {
        Adventure activeAdventure;
        try {
            Criteria criteria = new GoldCriteria();
            activeAdventure = game.startAdventureByCriteria(criteria);
            questStatus = "[Difficulty: " + activeAdventure.getDifficulty() + " / " +
                    "Duration: " + activeAdventure.getDuration() + " / " +
                    "Exp: " + activeAdventure.getExperience() + " / " +
                    "Chance: " + activeAdventure.getFightChance() + " / " +
                    "Gold: " + activeAdventure.getGold() + " / " +
                    "Quest ID: " + activeAdventure.getQuestID() + "]";

            setMainContentText(adventuresMade + 1, freeAdventures, inventorySpaces, questStatus);
            fxMainTextArea.setText(mainContentText);
            refreshTimer = activeAdventure.getDuration() + 60;

        } catch (AdventureRunningException ex) {
            setMainContentText("Can't get information when - Quest Running -");
            refreshTimer = 10;
            fxMainTextArea.setText(mainContentText);
        } catch (TimeOutException ex) {
            setMainContentText("Time Out. Reconnecting...");
            game.reconnect();
            refreshTimer = 10;
            fxMainTextArea.setText(mainContentText);
        }
    }

    private void setMainContentText(int adventuresMade, int freeAdventures, int inventorySpaces, String questStatus) {
        mainContentText = "Today Adventures: " + adventuresMade + " / " + freeAdventures + "\n" +
                "Free Inventory Spaces: " + inventorySpaces + "\n" +
                "Adventure Status: " + questStatus;
    }

    private void setMainContentText(String content) {
        mainContentText = content;
    }

    private void setCurrentStatus(String status) {
        fxStatus.setText("Current Status: " + status);
    }

    @FXML
    private void closeProgram() {
        Platform.exit();
        tray.remove(trayIcon);
    }

    @FXML
    private void minimizeToTray() {
        stage = (Stage) fxTray.getScene().getWindow();
        stage.hide();
    }

    public void setTrayData(SystemTray tray, TrayIcon trayIcon) {
        this.tray = tray;
        this.trayIcon = trayIcon;
    }


}
