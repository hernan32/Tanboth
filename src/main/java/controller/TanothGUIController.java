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
import model.GameParser;
import model.adventure_parser.AdventureParser;
import model.adventure_parser.adventure.Adventure;
import model.adventure_parser.adventure.AdventureCriteria;
import model.adventure_parser.adventure.criterias.TimeCriteria;
import model.adventure_parser.adventure.exception.AdventureRunningException;
import model.adventure_parser.adventure.exception.FightResultException;
import model.adventure_parser.adventure.exception.IllusionCaveRunningException;
import model.adventure_parser.adventure.exception.IllusionDisabledException;
import model.equipment_parser.EquipmentParser;
import model.exception.TimeOutException;
import model.user_parser.UserParser;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;

public class TanothGUIController {
    private GameParser gameParser;
    private AdventureParser adventureParser;
    private EquipmentParser equipmentParser;
    private UserParser userParser;
    private LocalTime refreshTime = LocalTime.parse(ConfigurationSingleton.getInstance().getProperty(ConfigurationSingleton.Property.resetTime));
    private boolean autoSell = Boolean.parseBoolean(ConfigurationSingleton.getInstance().getProperty(ConfigurationSingleton.Property.autoSellItems));
    private boolean sellEpics = Boolean.parseBoolean(ConfigurationSingleton.getInstance().getProperty(ConfigurationSingleton.Property.sellEpics));
    private boolean autoIncreaseStats = Boolean.parseBoolean(ConfigurationSingleton.getInstance().getProperty(ConfigurationSingleton.Property.autoIncreaseStats));
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
    private int bossMapMade;
    private int sleep = 60; //Seconds
    private boolean questStarted = false;

    private enum Status {
        STARTED, STOPPED
    }

    public TanothGUIController() throws IOException, InterruptedException {
        gameParser = new GameParser();
        adventureParser = gameParser.getAdventureParser();
        equipmentParser = gameParser.getEquipmentParser();
        userParser = gameParser.getUserParser();
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
                if (autoIncreaseStats) {
                    userParser.increaseStats();
                    Log.info("Increasing Stats: Done.");
                }
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
                        if (bossMapMade == 0) {
                            adventureParser.startIllusionCave();
                            Log.info("Starting Boss Fights: Done");
                        }
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
        Thread botThread = new Thread(botTask);
        fxStatus.textProperty().bind(botTask.messageProperty());
        botThread.start();
    }

    private void collectData() throws IOException, InterruptedException {
        try {
            if (questStarted) {
                adventureParser.getFightResult();
                Log.info("Getting Fight Result: Done.");
            }
            try {
                bossMapMade = adventureParser.getBossMapMadeToday();
                Log.info("Getting Boss Map Made: Done.");
            } catch (IllusionDisabledException e) {
                Log.info(e.getMessage());
                bossMapMade = -1;
            }
            inventorySpaces = equipmentParser.getInventorySpace();
            Log.info("Getting Inventory Space: Done.");
            adventuresMade = adventureParser.getAdventuresMadeToday();
            Log.info("Getting Adventures Made: Done.");
            freeAdventures = adventureParser.getFreeAdventuresPerDay();
            Log.info("Getting Free Adventures: Done.");
            questStarted = false;
            setMainContentText(adventuresMade, freeAdventures, bossMapMade, inventorySpaces, Status.STOPPED.toString());
            Log.info("[Quest: " + adventuresMade + " / " + freeAdventures + "] [Boss Map: " + bossMapMade + "/1] [Spaces: " + inventorySpaces + "] [Status: " + Status.STOPPED.toString() + "]");
        } catch (AdventureRunningException ex) {
            freeAdventures = -1;
            adventuresMade = -1;
            questStarted = true;
            Log.info("[Quest: " + adventuresMade + " / " + freeAdventures + "] [Boss Map: " + bossMapMade + "/1] [Spaces: " + inventorySpaces + "] [Status: " + Status.STARTED.toString() + "]");
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
        Adventure activeAdventure;
        try {
            if (autoSell) {
                equipmentParser.sellItemsFromInventory(sellEpics);
                Log.info("Selling Items (Epic: " + sellEpics + "): Done.");
            }
            AdventureCriteria adventureCriteria = new TimeCriteria();
            activeAdventure = adventureParser.startAdventureByCriteria(adventureCriteria);
            questStarted = true;
            String questDescription = "[Difficulty: " + activeAdventure.getDifficulty() + " / " +
                    "Duration: " + activeAdventure.getDuration() + " / " +
                    "Exp: " + activeAdventure.getExperience() + " / " +
                    "Chance: " + activeAdventure.getFightChance() + " / " +
                    "Gold: " + activeAdventure.getGold() + " / " +
                    "Quest ID: " + activeAdventure.getQuestID() + " / " +
                    "Finishing at: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(activeAdventure.getDuration())) + "]";

            setMainContentText(adventuresMade + 1, freeAdventures, bossMapMade, inventorySpaces, questDescription);
            sleep = activeAdventure.getDuration() + 20; // 20 Sec. Delay

        } catch (AdventureRunningException ex) {
            setMainContentText("Can't get information when - Quest Running -");
            sleep = 60;
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
        int secondsRefresh;
        Log.info("Actual Time is Before Refresh Time: " + Boolean.toString(timeNow.isBefore(refreshTime)));
        if (timeNow.isBefore(refreshTime)) {
            secondsRefresh = (int) SECONDS.between(timeNow, refreshTime) + 120;
        } else {
            secondsRefresh = (int) Math.abs(SECONDS.between(timeNow, LocalTime.parse("23:59:59"))) + refreshTime.toSecondOfDay() + 120;

        }
        return secondsRefresh;
    }


}
