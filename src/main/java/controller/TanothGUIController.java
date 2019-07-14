package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Adventure;
import model.AdventureRunningException;
import model.GameParser;

import java.awt.*;
import java.io.IOException;


public class TanothGUIController {
    //Tanoth Connection References
    private GameParser game;
    //TanothGUI References
    @FXML
    private TextArea fxMainTextArea;
    @FXML
    private javafx.scene.control.Button fxTray;
    Stage stage;
    //Tanoth Attributes
    private String adventuresMade = "";
    private String freeAdventures = "";
    private String invetorySpaces = "";
    private String questStatus = "Stopped.";
    //Tanoth Controller References
    private String mainContentText;
    //Tanoth View References
    SystemTray tray;
    TrayIcon trayIcon;

    public TanothGUIController() throws IOException, InterruptedException {
        game = new GameParser("Knobbers", "35413880");
    }

    @FXML
    public void initialize() throws IOException, InterruptedException {
        updateMainContentText();
        fxTray.setDefaultButton(false);
    }

    private void updateMainContentText() throws IOException, InterruptedException {
        adventuresMade = "";
        freeAdventures = "";
        invetorySpaces = "" + game.getInventorySpace();

        try {
            adventuresMade += game.getAdventuresMadeToday();
        } catch (AdventureRunningException ex) {
            adventuresMade = ex.getMessage();
            questStatus = "Running.";
        }
        try {
            freeAdventures += game.getFreeAdventuresPerDay();
        } catch (AdventureRunningException ex) {
            freeAdventures = ex.getMessage();
            questStatus = "Running.";
        }

        setMainContentText(adventuresMade, freeAdventures, invetorySpaces, questStatus);
        fxMainTextArea.setText(mainContentText);
    }

    public void startBot() throws IOException, InterruptedException {
        Adventure activeAdventure = null;
        try {
            activeAdventure = game.startAdventureByCriteria("GG");
            questStatus = "[Difficulty: " + activeAdventure.getDifficulty() + " / " +
                    "Duration: " + activeAdventure.getDuration() + " / " +
                    "Exp: " + activeAdventure.getExperience() + " / " +
                    "Chance: " + activeAdventure.getFightChance() + " / " +
                    "Gold: " + activeAdventure.getGold() + " / " +
                    "Quest ID: " + activeAdventure.getQuestID() + "]";

            setMainContentText(adventuresMade, freeAdventures, invetorySpaces, questStatus);
            updateMainContentText();

        } catch (AdventureRunningException ex) {
            setMainContentText(adventuresMade, freeAdventures, invetorySpaces, "Running.");
            updateMainContentText();
        }
    }

    private void setMainContentText(String adventuresMade, String freeAdventures, String inventorySpaces, String questStatus) {
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
