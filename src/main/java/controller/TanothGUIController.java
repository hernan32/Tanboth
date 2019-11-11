package controller;

import com.esotericsoftware.minlog.Log;
import configuration.ConfigSingleton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.BotLogic;
import model.game_parser.GameParser;
import model.game_parser.adventure_parser.AdventureParser;
import model.game_parser.adventure_parser.adventure.exception.*;
import model.game_parser.equipment_parser.EquipmentParser;
import model.game_parser.game.GameAttributes;
import model.game_parser.game.exception.TimeOutException;
import model.game_parser.user_parser.UserParser;

import java.awt.*;
import java.io.IOException;
import java.net.ConnectException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class TanothGUIController {
    private GameParser gameParser;
    private GameAttributes gameAttributes;
    private BotLogic bot;

    @FXML
    private TextArea fxMainTextArea;
    @FXML
    private Button fxTray;
    @FXML
    private Label fxStatus;
    @FXML
    private Label fxAccountInfo;

    private SystemTray tray;
    private TrayIcon trayIcon;
    private boolean waitConnect;

    public TanothGUIController() throws IOException {
        if (!ConfigSingleton.getInstance().getOption(ConfigSingleton.Option.debugMode))
            Log.set(Log.LEVEL_NONE);
        waitConnect = true;
    }

    @FXML
    public void initialize() throws IOException {
        ConfigSingleton configuration = ConfigSingleton.getInstance();
        fxAccountInfo.setText(String.format("Server: %s / Account: %s", configuration.getProperty(ConfigSingleton.Property.serverNumber), configuration.getProperty(ConfigSingleton.Property.user)));
        fxTray.setDefaultButton(false);
        fxMainTextArea.setText("Waiting for connection.");
        Log.warn("Waiting for connection.");
        waitConnection();
    }

    private void waitConnection() {
        new Thread(() -> {
            int sleep = 0;
            while (waitConnect) {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(sleep));
                    gameParser = new GameParser();
                    gameAttributes = gameParser.getGameAttributes();
                    waitConnect = false;
                    bot = new BotLogic(this, gameParser);
                    Platform.runLater(() -> fxStatus.textProperty().bind(bot.getBotTask().messageProperty()));
                    bot.getBotThread().start();
                } catch (ConnectException ex) {
                    Log.warn("Connection Error. Waiting for connection.");
                    Platform.runLater(() -> fxStatus.setText("Error."));
                    waitConnect = true;
                    sleep = 60;
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void collectData() throws
            IOException, InterruptedException, TimeOutException, FightResultException, AdventureRunningException, IllusionDisabledException, IllusionCaveRunningException, WorkingException, RewardResultException {
        AdventureParser adventureParser = gameParser.getAdventureParser();
        EquipmentParser equipmentParser = gameParser.getEquipmentParser();
        UserParser userParser = gameParser.getUserParser();
        Log.warn("getAdventuresMadeToday");
        gameAttributes.setAdventuresMade(adventureParser.getAdventuresMadeToday());
        Log.warn("getFreeAdventuresPerDay");
        gameAttributes.setFreeAdventures(adventureParser.getFreeAdventuresPerDay());
        Log.warn("getBossMapMadeToday");
        gameAttributes.setBossMapMade(adventureParser.getBossMapMadeToday());
        Log.warn("getInventorySpace");
        gameAttributes.setInventorySpaces(equipmentParser.getInventorySpace());
        Log.warn("getBloodStones");
        gameAttributes.setBloodStones(userParser.getBloodStones());
        Log.warn("getDungeonsMadeToday");
        gameAttributes.setDungeonsMade(adventureParser.getDungeonsMadeToday());
        Log.warn("getFreeAdventuresPerDay");
        gameAttributes.setFreeDungeons(adventureParser.getFreeDungeonsPerDay());
    }

    public String getQuestDescription(int difficulty, int duration, int exp, int fightChance, int gold,
                                      int questID) {
        return "[Difficulty: " + difficulty + " / " +
                "Duration: " + duration + " / " +
                "Exp: " + exp + " / " +
                "Chance: " + fightChance + " / " +
                "Gold: " + gold + " / " +
                "Quest ID: " + questID + " / " +
                "Finishing at: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameParser.getGameAttributes().getSleep())) + "]";
    }


    public void setMainContentText(int adventuresMade, int freeAdventures, int bossMapMade, int inventorySpaces,
                                   int dungeonsMade, int freeDugenons, String questStatus) {
        setTextArea("Today Adventures: " + adventuresMade + " / " + freeAdventures + "\n" +
                "Today Illusion Cave: " + bossMapMade + " / " + 1 + "\n" +
                "Today Dungeon: " + dungeonsMade + " / " + freeDugenons + "\n" +
                "Free Inventory Spaces: " + inventorySpaces + " / 30" + "\n" +
                "BloodStones: " + gameAttributes.getBloodStones() + "\n" +
                "Quest Status: " + questStatus
        );
    }

    public void setMainContentText(String content) {
        setTextArea(content);
    }


    private void setTextArea(String mainContentText) {
        Log.warn("Current Status:\n------------------------\n" + mainContentText + "\n------------------------");
        Platform.runLater(() -> fxMainTextArea.setText(mainContentText));
    }

    @FXML
    private void closeProgram() {
        Platform.exit();
        tray.remove(trayIcon);
        bot.getBotThread().interrupt();
    }

    @FXML
    private void minimizeToTray() {
        Stage stage = (Stage) fxTray.getScene().getWindow();
        stage.hide();
    }

    public void setTrayData(SystemTray tray, TrayIcon trayIcon) throws IOException {
        this.tray = tray;
        this.trayIcon = trayIcon;
        ConfigSingleton configuration = ConfigSingleton.getInstance();
        trayIcon.setToolTip(String.format("Tanboth | Server: %s / Account: %s", configuration.getProperty(ConfigSingleton.Property.serverNumber), configuration.getProperty(ConfigSingleton.Property.user)));
    }


}
