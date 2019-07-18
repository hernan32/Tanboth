package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Adventure;
import model.AdventureRunningException;
import model.GameParser;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class TanothGUIController extends Task<Void> {
    //Connection References
    private final String propFileName = "config.properties";
    private GameParser game;
    //FXML References
    @FXML
    private TextArea fxMainTextArea;
    @FXML
    private Button fxTray;
    //Stage References
    private SystemTray tray;
    private TrayIcon trayIcon;
    private Stage stage;
    //Tanoth Attributes
    private int adventuresMade;
    private int freeAdventures;
    private int inventorySpaces;
    private String questStatus;
    //Tanoth Controller References
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
        while (true) {
            updateMainContent();
            if (questStatus.equals("Stopped.")) {
                if (adventuresMade < freeAdventures) {
                    System.out.println("adventuresMade < freeAdventures");
                    //startBot();
                } else {
                    System.out.println("adventuresMade => freeAdventures");
                    refreshTimer = 3600;
                }
            }
            System.out.println("Timer : " + refreshTimer);
            TimeUnit.SECONDS.sleep(refreshTimer);
        }
    }

    @FXML
    public void initialize() {
        System.out.println("TEST FLAG");
        fxTray.setDefaultButton(false);
        TanothGUIController controller = this;
        new Thread(controller).start();
    }

    private void updateMainContent() throws IOException, InterruptedException {
        inventorySpaces = game.getInventorySpace();

        try {
            adventuresMade = game.getAdventuresMadeToday();
            questStatus = "Stopped.";
        } catch (AdventureRunningException ex) {
            adventuresMade = -1;
            questStatus = "Running.";
            setMainContentText("Can't get information when - Quest Running -");
        }
        try {
            freeAdventures += game.getFreeAdventuresPerDay();
            questStatus = "Stopped.";
        } catch (AdventureRunningException ex) {
            freeAdventures = -1;
            questStatus = "Running.";
            setMainContentText("Can't get information when - Quest Running -");
        }

        setMainContentText(adventuresMade, freeAdventures, inventorySpaces, questStatus);
        fxMainTextArea.setText(mainContentText);
    }

    public void startBot() throws IOException, InterruptedException {
        Adventure activeAdventure;
        try {
            activeAdventure = game.startAdventureByCriteria("EXP");
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
