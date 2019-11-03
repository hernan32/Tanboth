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

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public TanothGUIController() throws IOException, InterruptedException {
        gameParser = new GameParser();
        gameAttributes = gameParser.getGameAttributes();
        bot = new BotLogic(this, gameParser);
        if (!ConfigSingleton.getInstance().getOption(ConfigSingleton.Option.debugMode))
            Log.set(Log.LEVEL_NONE);
    }

    @FXML
    public void initialize() throws IOException {
        ConfigSingleton configuration = ConfigSingleton.getInstance();
        fxAccountInfo.setText(String.format("Server: %s / Account: %s", configuration.getProperty(ConfigSingleton.Property.serverNumber), configuration.getProperty(ConfigSingleton.Property.user)));
        fxTray.setDefaultButton(false);
        fxStatus.textProperty().bind(bot.getBotTask().messageProperty());
        bot.getBotThread().start();
    }

    public void collectData() throws IOException, InterruptedException, TimeOutException, FightResultException, AdventureRunningException, IllusionDisabledException, IllusionCaveRunningException, WorkingException, RewardResultException {
        AdventureParser adventureParser = gameParser.getAdventureParser();
        EquipmentParser equipmentParser = gameParser.getEquipmentParser();
        Log.warn("getAdventuresMadeToday");
        gameAttributes.setAdventuresMade(adventureParser.getAdventuresMadeToday());
        Log.warn("getFreeAdventuresPerDay");
        gameAttributes.setFreeAdventures(adventureParser.getFreeAdventuresPerDay());
        Log.warn("getBossMapMadeToday");
        gameAttributes.setBossMapMade(adventureParser.getBossMapMadeToday());
        Log.warn("getInventorySpace");
        gameAttributes.setInventorySpaces(equipmentParser.getInventorySpace());
    }

    public String getQuestDescription(int difficulty, int duration, int exp, int fightChance, int gold, int questID) {
        return "[Difficulty: " + difficulty + " / " +
                "Duration: " + duration + " / " +
                "Exp: " + exp + " / " +
                "Chance: " + fightChance + " / " +
                "Gold: " + gold + " / " +
                "Quest ID: " + questID + " / " +
                "Finishing at: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameParser.getGameAttributes().getSleep())) + "]";
    }


    public void setMainContentText(int adventuresMade, int freeAdventures, int bossMapMade, int inventorySpaces, String questStatus) {
        setTextArea(getBaseText(adventuresMade, freeAdventures, bossMapMade, inventorySpaces) +
                "Quest Status: " + questStatus
        );
    }

    public void setMainContentText(String content) {
        setTextArea(content);
    }

    private String getBaseText(int adventuresMade, int freeAdventures, int bossMapMade, int inventorySpaces) {
        return "Today Adventures: " + adventuresMade + " / " + freeAdventures + "\n" +
                "Today Boss Map: " + bossMapMade + " / " + 1 + "\n" +
                "Free Inventory Spaces: " + inventorySpaces + "\n";
    }

    private void setTextArea(String mainContentText) {
        Log.warn("Current Status:\n------------------------\n" + mainContentText + "\n------------------------");
        Platform.runLater(() -> fxMainTextArea.setText(mainContentText));
    }

    @FXML
    private void closeProgram() {
        Platform.exit();
        tray.remove(trayIcon);
        bot.getBotThread().stop();
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
