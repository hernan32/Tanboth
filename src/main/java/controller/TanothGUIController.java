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
import model.game.attributes.GameAttributes;
import model.game.parser.GameParser;
import model.game.parser.equipment.EquipmentParser;
import model.game.parser.exception.TimeOutException;
import model.game.parser.quest.QuestParser;
import model.game.parser.quest.exception.*;
import model.game.parser.user.UserParser;

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

    public TanothGUIController() {
        waitConnect = true;
    }

    @FXML
    public void initialize() throws IOException {
        if (!ConfigSingleton.getInstance().getOption(ConfigSingleton.Option.debugMode))
            Log.set(Log.LEVEL_NONE);
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
        QuestParser questParser = gameParser.getQuestParser();
        EquipmentParser equipmentParser = gameParser.getEquipmentParser();
        UserParser userParser = gameParser.getUserParser();
        Log.warn("getAdventuresMadeToday");
        questParser.getAdventuresMadeToday();
        Log.warn("getFreeAdventuresPerDay");
        questParser.getFreeAdventuresPerDay();
        Log.warn("getIllusionCaveMadeToday");
        questParser.getIllusionCaveMadeToday();
        Log.warn("getItems");
        equipmentParser.getItems();
        Log.warn("getBloodStones");
        userParser.getBloodStones();
        Log.warn("getDungeonsMadeToday");
        questParser.getDungeonsMadeToday();
        Log.warn("getFreeDungeonsPerDay");
        questParser.getFreeDungeonsPerDay();
        Log.warn("getUserAttributes");
        userParser.getUserAttributes();
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
                                   int dungeonsMade, int freeDungeons, String questStatus) {
        setTextArea("Today Adventures: " + adventuresMade + " / " + freeAdventures + "\n" +
                "Today Illusion Cave: " + bossMapMade + " / " + 1 + "\n" +
                "Today Dungeon: " + dungeonsMade + " / " + freeDungeons + "\n" +
                "Inventory Spaces: " + inventorySpaces + " / 30" + "\n" +
                "BloodStones: " + gameAttributes.getUserAttributes().getBloodStones() + "\n" +
                "Gold: " + gameAttributes.getUserAttributes().getGold() + "\n" +
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
