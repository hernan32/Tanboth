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
import model.adventure.exception.FightResultException;
import model.exception.TimeOutException;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;

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
    private int sleep = 60; //Seconds
    private boolean questStarted = false;

    enum Status {
        STARTED, STOPPED
    }

    public TanothGUIController() throws IOException, InterruptedException {
        game = new GameParser();
    }

    private Task questChecker = new Task() {
        @Override
        protected Object call() throws Exception {
            String status;
            while (true) {
                Log.warn("------------- > Loop Start");
                Log.info("Collecting Data...");
                collectData();
                if (!questStarted) {
                    Log.info("Stopped == TRUE");
                    if (adventuresMade < freeAdventures) {
                        Log.info("adventuresMade < freeAdventures == TRUE  -> ¡Starting Bot!");
                        startBot();
                        status = "Free quest available. Starting Quest...";
                    } else {
                        sleep = getSecondsToNextQuestRefresh();
                        Log.info("adventuresMade < freeAdventures == FALSE  -> Restarting at: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(sleep)));
                        status = "No free quest available. Restarting at: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(sleep));
                    }
                } else status = "Adventure already running. Waiting for stop. Refreshing... ";
                this.updateMessage(status + " (" + sleep + " Seconds)");
                Log.warn("------------- > Loop End");
                Log.warn("## Sleep Thread: " + TimeUnit.SECONDS.toMillis(sleep) + " millis / " + sleep + " sec");
                Thread.sleep(TimeUnit.SECONDS.toMillis(sleep));
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

    private void collectData() throws IOException, InterruptedException {
        try {
            if (questStarted) {
                game.getFightResult();
            }
            inventorySpaces = game.getInventorySpace();
            Log.info("Getting Inventory Space: Done.");
            adventuresMade = game.getAdventuresMadeToday();
            Log.info("Getting Adventures Made: Done.");
            freeAdventures = game.getFreeAdventuresPerDay();
            Log.info("Getting Free Adventures: Done.");
            questStarted = false;
            setMainContentText(adventuresMade, freeAdventures, inventorySpaces, Status.STOPPED.toString());
            Log.info("[Quest: " + adventuresMade + " / " + freeAdventures + "] [Spaces: " + inventorySpaces + "] [Status: " + Status.STOPPED.toString() + "]");
        } catch (AdventureRunningException ex) {
            freeAdventures = -1;
            adventuresMade = -1;
            questStarted = true;
            Log.info("[Quest: " + adventuresMade + " / " + freeAdventures + "] [Spaces: " + inventorySpaces + "] [Status: " + Status.STARTED.toString() + "]");
            setMainContentText("Can't get information when - Quest Running -");
        } catch (TimeOutException ex) {
            setMainContentText("Time Out. Reconnecting...");
            Log.warn("Time Out. Reconnecting...");
            game.reconnect();
            collectData();
        } catch (FightResultException ex) {
            Log.info(ex.getMessage());
        }
    }

    private void startBot() throws IOException, InterruptedException {
        Adventure activeAdventure;
        try {
            Criteria criteria = new TimeCriteria();
            activeAdventure = game.startAdventureByCriteria(criteria);
            questStarted = true;
            String questDescription = "[Difficulty: " + activeAdventure.getDifficulty() + " / " +
                    "Duration: " + activeAdventure.getDuration() + " / " +
                    "Exp: " + activeAdventure.getExperience() + " / " +
                    "Chance: " + activeAdventure.getFightChance() + " / " +
                    "Gold: " + activeAdventure.getGold() + " / " +
                    "Quest ID: " + activeAdventure.getQuestID() + " / " +
                    "Finishing at: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(activeAdventure.getDuration())) + "]";

            setMainContentText(adventuresMade + 1, freeAdventures, inventorySpaces, questDescription);
            sleep = activeAdventure.getDuration() + 20; // 20 Sec. Delay

        } catch (AdventureRunningException ex) {
            setMainContentText("Can't get information when - Quest Running -");
            sleep = 60;
        } catch (TimeOutException ex) {
            setMainContentText("Time Out. Reconnecting...");
            Log.warn("Reconnecting...");
            game.reconnect();
            startBot();
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
            Log.info("Updating Main Content Text Area... (FX Thread)");
            fxMainTextArea.setText(mainContentText);
        });
    }

    @FXML
    private void closeProgram() {
        Platform.exit();
        tray.remove(trayIcon);
        questChecker.cancel();
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

    private int getSecondsToNextQuestRefresh() {
        LocalTime timeNow = LocalTime.now();
        LocalTime refreshTime = LocalTime.parse("19:00:00");
        int secondsRefresh;
        Log.info(Boolean.toString(timeNow.isBefore(refreshTime)));
        if (timeNow.isBefore(refreshTime)) {
            secondsRefresh = (int) SECONDS.between(timeNow, refreshTime) + 120;
        } else {
            secondsRefresh = (int) Math.abs(SECONDS.between(timeNow, LocalTime.parse("23:59:59"))) + (19 * 60 * 60) + 120;
        }
        return secondsRefresh;
    }

}
