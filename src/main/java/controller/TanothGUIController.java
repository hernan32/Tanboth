package controller;

import com.esotericsoftware.minlog.Log;
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
import model.adventure.criterias.TimeCriteria;
import model.adventure.exception.AdventureRunningException;
import model.exception.TimeOutException;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class TanothGUIController {
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
    //Tanoth Attributes
    private int adventuresMade;
    private int freeAdventures;
    private int inventorySpaces;
    private String questStatus;
    private int refreshTimer = 60; //Seconds

    public TanothGUIController() throws IOException, InterruptedException {
        Properties prop = new Properties();
        //Connection
        String propFileName = "config.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config/" + propFileName);
        if (inputStream != null) {
            prop.load(inputStream);
        }
        String user = prop.getProperty("user");
        String password = prop.getProperty("password");
        String serverURL = prop.getProperty("serverURL");
        String serverNumber = prop.getProperty("serverNumber");
        game = new GameParser(user, password, serverURL, serverNumber);
    }

    private Task questChecker = new Task() {
        @Override
        protected Object call() throws Exception {
            String status = "";
            while (true) {

                Log.warn("Inicio de Ciclo");
                Log.info("UPDATING MAIN CONTENT");
                try {
                    collectData();
                } catch (AdventureRunningException ex) {
                    status = ex.getMessage();
                }
                if (questStatus.equals("Stopped.")) {
                    Log.info("QUEST STOPPED");
                    if (adventuresMade < freeAdventures) {
                        Log.info("STARTING BOT");
                        startBot();
                        status = "Free quest available. Starting Quest...";
                    } else {
                        status = "No free quest available. Waiting for new quests...";
                        refreshTimer = 3600;
                    }
                }
                Log.info(status.toUpperCase());
                this.updateMessage(status + " (" + refreshTimer + " Seconds)");
                Log.warn("Fin de ciclo");
                Thread.sleep(TimeUnit.SECONDS.toMillis(1/*refreshTimer*/));
            }
        }
    };


    @FXML
    public void initialize() {
        fxTray.setDefaultButton(false);
        Thread questCheckerThread = new Thread(questChecker);
        fxStatus.textProperty().bind(questChecker.messageProperty());
        questCheckerThread.start();
    }

    private void collectData() throws IOException, InterruptedException, AdventureRunningException {
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
            throw new AdventureRunningException("Waiting...");
        }
    }

    private void startBot() throws IOException, InterruptedException {
        Adventure activeAdventure;
        try {
            Criteria criteria = new TimeCriteria();
            activeAdventure = game.startAdventureByCriteria(criteria);
            questStatus = "[Difficulty: " + activeAdventure.getDifficulty() + " / " +
                    "Duration: " + activeAdventure.getDuration() + " / " +
                    "Exp: " + activeAdventure.getExperience() + " / " +
                    "Chance: " + activeAdventure.getFightChance() + " / " +
                    "Gold: " + activeAdventure.getGold() + " / " +
                    "Quest ID: " + activeAdventure.getQuestID() + " / " +
                    "Finishing at: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(activeAdventure.getDuration())) + "]";

            setMainContentText(adventuresMade + 1, freeAdventures, inventorySpaces, questStatus);
            refreshTimer = activeAdventure.getDuration() + 20; // 20 Sec. Delay

        } catch (AdventureRunningException ex) {
            setMainContentText("Can't get information when - Quest Running -");
            refreshTimer = 10;
        } catch (TimeOutException ex) {
            setMainContentText("Time Out. Reconnecting...");
            Log.warn("RECONNECTING");
            game.reconnect();
            refreshTimer = 10;
        }
    }

    private void setMainContentText(int adventuresMade, int freeAdventures, int inventorySpaces, String questStatus) {
        //Controller
        String mainContentText = "Today Adventures: " + adventuresMade + " / " + freeAdventures + "\n" +
                "Free Inventory Spaces: " + inventorySpaces + "\n" +
                "Adventure Status: " + questStatus;
        setTextArea(mainContentText);
    }

    private void setMainContentText(String content) {
        setTextArea(content);
    }

    private void setTextArea(String mainContentText) {
        Platform.runLater(() -> {
            Log.info("RUN LATER ## UPDATED TEXTAREA");
            fxMainTextArea.setText(mainContentText);
        });
    }

    @FXML
    private void closeProgram() {
        Platform.exit();
        tray.remove(trayIcon);
    }

    @FXML
    private void minimizeToTray() {
        Stage stage = (Stage) fxTray.getScene().getWindow();
        stage.hide();
    }

    public void setTrayData(SystemTray tray, TrayIcon trayIcon) {
        this.tray = tray;
        this.trayIcon = trayIcon;
    }


}
