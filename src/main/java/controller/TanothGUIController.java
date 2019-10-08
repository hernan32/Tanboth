package controller;

import com.esotericsoftware.minlog.Log;
import configuration.ConfigurationSingleton;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.game_parser.GameParser;
import model.game_parser.adventure_parser.AdventureParser;
import model.game_parser.adventure_parser.adventure.AdventureAttributes;
import model.game_parser.adventure_parser.adventure.AdventureCriteria;
import model.game_parser.adventure_parser.adventure.criterias.TimeCriteria;
import model.game_parser.adventure_parser.adventure.exception.AdventureRunningException;
import model.game_parser.adventure_parser.adventure.exception.FightResultException;
import model.game_parser.adventure_parser.adventure.exception.IllusionCaveRunningException;
import model.game_parser.adventure_parser.adventure.exception.IllusionDisabledException;
import model.game_parser.equipment_parser.EquipmentParser;
import model.game_parser.game.GameAttributes;
import model.game_parser.game.exception.TimeOutException;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;

public class TanothGUIController {
    //Model
    private GameParser gameParser;
    private GameAttributes gameAttributes;

    //View
    @FXML
    private TextArea fxMainTextArea;
    @FXML
    private Button fxTray;
    @FXML
    private Label fxStatus;
    private SystemTray tray;
    private TrayIcon trayIcon;

    //Configuration
    private ConfigurationSingleton configuration = ConfigurationSingleton.getInstance();


    private enum Status {
        STARTED, STOPPED
    }

    public TanothGUIController() throws IOException, InterruptedException {
        gameParser = new GameParser();
        gameAttributes = gameParser.getGameAttributes();
        if (ConfigurationSingleton.getInstance().getProperty(ConfigurationSingleton.Property.debugMode).equals("OFF"))
            Log.set(Log.LEVEL_NONE);
    }

    private Task botTask = new Task() {
        @Override
        protected Object call() throws Exception {
            String status;

            while (true) {
                Log.warn("------------- > Loop Start");
                Log.info("Collecting Data...");
                collectData();
                if (Boolean.parseBoolean(configuration.getProperty(ConfigurationSingleton.Property.autoIncreaseStats))) {
                    gameParser.getUserParser().increaseStats();
                    Log.info("Increasing Stats: Done.");
                }
                if (!gameAttributes.isQuestStarted()) {
                    Log.info("Stopped == TRUE");
                    if (gameAttributes.getAdventuresMade() < gameAttributes.getFreeAdventures()) {
                        Log.info("adventuresMade < freeAdventures == TRUE  -> ¡Starting Bot!");
                        startBot();
                        status = "Free quest available. Starting Quest...";
                    } else {
                        gameAttributes.setSleep(getSecondsToNextQuestRefresh());
                        Log.info("adventuresMade < freeAdventures == FALSE  -> Restarting at: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameAttributes.getSleep())));
                        status = "No free quest available. Restarting at: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameAttributes.getSleep()));
                        if (gameAttributes.getBossMapMade() == 0) {
                            gameParser.getAdventureParser().startIllusionCave();
                            Log.info("Starting Boss Fights: Done");
                        }
                    }
                } else status = "Adventure already running. Waiting for stop. Refreshing... ";
                this.updateMessage(status + " (" + gameAttributes.getSleep() + " Seconds)");
                Log.warn("------------- > Loop End");
                Log.warn("## Sleep Thread: " + TimeUnit.SECONDS.toMillis(gameAttributes.getSleep()) + " millis / " + gameAttributes.getSleep() + " sec");
                Thread.sleep(TimeUnit.SECONDS.toMillis(gameAttributes.getSleep()));
            }
        }
    };


    @FXML
    public void initialize() {
        fxTray.setDefaultButton(false);
        Thread botThread = new Thread(botTask);
        fxStatus.textProperty().bind(botTask.messageProperty());
        botThread.start();
    }

    private void collectData() throws IOException, InterruptedException {
        AdventureParser adventureParser = gameParser.getAdventureParser();
        EquipmentParser equipmentParser = gameParser.getEquipmentParser();
        try {
            if (gameAttributes.isQuestStarted()) {
                adventureParser.getFightResult();
                Log.info("Getting Fight Result: Done.");
            }
            try {
                gameAttributes.setBossMapMade(adventureParser.getBossMapMadeToday());
                Log.info("Getting Boss Map Made: Done.");
            } catch (IllusionDisabledException e) {
                Log.info(e.getMessage());
                gameAttributes.setBossMapMade(-1);
            }
            gameAttributes.setInventorySpaces(equipmentParser.getInventorySpace());
            Log.info("Getting Inventory Space: Done.");
            gameAttributes.setAdventuresMade(adventureParser.getAdventuresMadeToday());
            Log.info("Getting Adventures Made: Done.");
            gameAttributes.setFreeAdventures(adventureParser.getFreeAdventuresPerDay());
            Log.info("Getting Free Adventures: Done.");
            gameAttributes.setQuestStarted(false);

            setMainContentText(gameAttributes.getAdventuresMade(), gameAttributes.getFreeAdventures(), gameAttributes.getBossMapMade(), gameAttributes.getInventorySpaces(), Status.STOPPED.toString());
            Log.info(gameAttributes.getAllAttributesToString() + " [Status: " + Status.STOPPED.toString() + "]");
        } catch (AdventureRunningException ex) {
            gameAttributes.setFreeAdventures(-1);
            gameAttributes.setAdventuresMade(-1);
            gameAttributes.setQuestStarted(true);
            Log.info("[Quest: " + gameAttributes.getAdventuresMade() + " / " + gameAttributes.getFreeAdventures() + "] [Boss Map: " + gameAttributes.getBossMapMade() + "/1] [Spaces: " + gameAttributes.getInventorySpaces() + "] [Status: " + Status.STARTED.toString() + "]");
            setMainContentText("Can't get information when - Quest Running -");
        } catch (TimeOutException ex) {
            Log.info(ex.getMessage());
            setMainContentText("Time Out. Reconnecting...");
            Log.warn("Time Out. Reconnecting...");
            gameParser.reconnect();
            collectData();
        } catch (FightResultException ex) {
            Log.info(ex.getMessage());
            collectData();
        } catch (IllusionCaveRunningException ex) {
            Log.info(ex.getMessage());
        }
    }

    private void startBot() throws IOException, InterruptedException {
        AdventureAttributes activeAdventure;
        AdventureParser adventureParser = gameParser.getAdventureParser();
        EquipmentParser equipmentParser = gameParser.getEquipmentParser();
        try {
            if (Boolean.parseBoolean(configuration.getProperty(ConfigurationSingleton.Property.autoIncreaseStats))) {
                equipmentParser.sellItemsFromInventory(Boolean.parseBoolean(configuration.getProperty(ConfigurationSingleton.Property.sellEpics)));
                Log.info("Selling Items (Epic: " + Boolean.parseBoolean(configuration.getProperty(ConfigurationSingleton.Property.sellEpics)) + "): Done.");
            }
            AdventureCriteria adventureCriteria = new TimeCriteria();
            activeAdventure = adventureParser.startAdventureByCriteria(adventureCriteria);
            gameAttributes.setQuestStarted(true);
            String questDescription = "[Difficulty: " + activeAdventure.getDifficulty() + " / " +
                    "Duration: " + activeAdventure.getDuration() + " / " +
                    "Exp: " + activeAdventure.getExperience() + " / " +
                    "Chance: " + activeAdventure.getFightChance() + " / " +
                    "Gold: " + activeAdventure.getGold() + " / " +
                    "Quest ID: " + activeAdventure.getQuestID() + " / " +
                    "Finishing at: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(activeAdventure.getDuration())) + "]";
            gameAttributes.setAdventuresMade(gameAttributes.getAdventuresMade() + 1);
            setMainContentText(gameAttributes.getAdventuresMade(), gameAttributes.getFreeAdventures(), gameAttributes.getBossMapMade(), gameAttributes.getInventorySpaces(), questDescription);
            gameAttributes.setSleep(activeAdventure.getDuration() + 20); // 20 Sec. Delay

        } catch (AdventureRunningException ex) {
            setMainContentText("Can't get information when - Quest Running -");
            gameAttributes.setSleep(60);
        } catch (TimeOutException ex) {
            setMainContentText("Time Out. Reconnecting...");
            Log.warn("Reconnecting...");
            gameParser.reconnect();
            startBot();
        } catch (IllusionCaveRunningException | FightResultException ex) {
            ex.printStackTrace();
        }
    }

    private void setMainContentText(int adventuresMade, int freeAdventures, int bossMapMade, int inventorySpaces, String questStatus) {
        //Controller
        String mainContentText = "Today Adventures: " + adventuresMade + " / " + freeAdventures + "\n" +
                "Today Boss Map: " + bossMapMade + " / " + 1 + "\n" +
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
        botTask.cancel();
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
        LocalTime refreshTime = LocalTime.parse(configuration.getProperty(ConfigurationSingleton.Property.resetTime));
        int secondsRefresh;
        Log.info("Actual Time is Before Refresh Time: " + timeNow.isBefore(refreshTime));
        if (timeNow.isBefore(refreshTime)) {
            secondsRefresh = (int) SECONDS.between(timeNow, refreshTime) + 120;
        } else {
            secondsRefresh = (int) Math.abs(SECONDS.between(timeNow, LocalTime.parse("23:59:59"))) + refreshTime.toSecondOfDay() + 120;

        }
        return secondsRefresh;
    }


}
