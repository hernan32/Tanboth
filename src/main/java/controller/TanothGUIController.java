package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import model.Adventure;
import model.AdventureRunningException;
import model.GameParser;

import java.awt.*;
import java.io.IOException;


public class TanothGUIController extends Task<Boolean> {
    //Connection References
    private GameParser game;
    //FXML References
    @FXML
    private TextArea fxMainTextArea;
    @FXML
    private Button fxTray;
    @FXML
    private FlowPane fxFlowPane;
    //Stage References
    private SystemTray tray;
    private TrayIcon trayIcon;
    private Stage stage;
    //Tanoth Attributes
    private int adventuresMade;
    private int freeAdventures;
    private int inventorySpaces;
    private String questStatus = "Stopped.";
    //Tanoth Controller References
    private String mainContentText;
    private int refrestTimer = 60; //Seconds


    public TanothGUIController() throws IOException, InterruptedException {
        game = new GameParser("Knobbers", "35413880");
    }

    @Override
    protected Boolean call() throws Exception {
        while (adventuresMade <= freeAdventures) {
            if (questStatus.equals("Stopped.")) {
                startBot();
            }
        }
        return true;
    }

    @FXML
    public void initialize() throws IOException, InterruptedException {
        System.out.println("TEST FLAG");
        updateMainContentText();
        fxTray.setDefaultButton(false);
        TanothGUIController controller = this;
        //new Thread(controller).start();
    }

    private void updateMainContentText() throws IOException, InterruptedException {
        inventorySpaces = game.getInventorySpace();

        try {
            adventuresMade = game.getAdventuresMadeToday();
        } catch (AdventureRunningException ex) {
            adventuresMade = -1;
            questStatus = "Running.";
        }
        try {
            freeAdventures += game.getFreeAdventuresPerDay();
        } catch (AdventureRunningException ex) {
            freeAdventures = -1;
            questStatus = "Running.";
        }

        setMainContentText(adventuresMade, freeAdventures, inventorySpaces, questStatus);
        fxMainTextArea.setText(mainContentText);
    }

    public void startBot() throws IOException, InterruptedException {
        Adventure activeAdventure;
        try {
            activeAdventure = game.startAdventureByCriteria("GOLD");
            questStatus = "[Difficulty: " + activeAdventure.getDifficulty() + " / " +
                    "Duration: " + activeAdventure.getDuration() + " / " +
                    "Exp: " + activeAdventure.getExperience() + " / " +
                    "Chance: " + activeAdventure.getFightChance() + " / " +
                    "Gold: " + activeAdventure.getGold() + " / " +
                    "Quest ID: " + activeAdventure.getQuestID() + "]";

            setMainContentText(adventuresMade, freeAdventures, inventorySpaces, questStatus);
            updateMainContentText();
            refrestTimer = activeAdventure.getDuration() + 60;

        } catch (AdventureRunningException ex) {
            setMainContentText(adventuresMade, freeAdventures, inventorySpaces, "Running.");
            refrestTimer = 60;
            updateMainContentText();
        }
    }

    private void setMainContentText(int adventuresMade, int freeAdventures, int inventorySpaces, String questStatus) {
        mainContentText = "Today Adventures: " + adventuresMade + " / " + freeAdventures + "\n" +
                "Free Inventory Spaces: " + inventorySpaces + "\n" +
                "Adventure Status: " + questStatus;
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
