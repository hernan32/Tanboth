package model;

import com.esotericsoftware.minlog.Log;
import configuration.ConfigSingleton;
import controller.TanothGUIController;
import javafx.concurrent.Task;
import model.game.attributes.GameAttributes;
import model.game.attributes.equipment.EquipmentAttributes;
import model.game.attributes.quest.QuestAttributes;
import model.game.attributes.quest.adventure.Adventure;
import model.game.attributes.quest.adventure.criterias.ExpCriteria;
import model.game.parser.GameParser;
import model.game.parser.exception.TimeOutException;
import model.game.parser.quest.QuestParser;
import model.game.parser.quest.exception.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;

public class BotTask extends Task {
    private final GameParser gameParser;
    private final TanothGUIController controller;
    private final ConfigSingleton cfg = ConfigSingleton.getInstance();
    private final int SECURE_DELAY = 60;

    public BotTask(GameParser gameParser, TanothGUIController controller) throws IOException {
        this.gameParser = gameParser;
        this.controller = controller;
    }

    @Override
    protected Object call() throws Exception {
        String status = "";
        this.updateMessage("Loading...");
        controller.setMainContentText("Loading...");
        QuestParser questParser = gameParser.getQuestParser();
        GameAttributes gameAttributes = gameParser.getGameAttributes();
        QuestAttributes questAttributes = gameAttributes.getQuestAttributes();
        EquipmentAttributes equipmentAttributes = gameAttributes.getEquipmentAttributes();

        while (true) {
            try {
                status = "";
                Log.warn("COLLECT DATA: STARTING");
                controller.collectData();
                Log.warn("COLLECT DATA: DONE");
                if ((cfg.getOption(ConfigSingleton.Option.autoIncreaseStats))) {
                    Log.warn("INCREASE STATS: STARTING");
                    increaseStats();
                    Log.warn("INCREASE STATS: DONE");
                }
                if (questAttributes.getAdventuresMade() < questAttributes.getFreeAdventures()) {
                    Log.warn("ADVENTURE: STARTING");
                    status = startAdventure();
                    Log.warn("ADVENTURE: DONE");
                } else {
                    if (questAttributes.getDungeonsMade() < questAttributes.getFreeDungeons()) {
                        Log.warn("DUNGEON: STARTING");
                        status = startDungeon();
                        Log.warn("DUNGEON: DONE");
                    } else {
                        if (questAttributes.getIllusionCaveMade() == 0) {
                            Log.warn("ILLUSIONCAVE : STARTING");
                            status = startIllusionCave();
                            Log.warn("ILLUSIONCAVE : DONE");
                        } else {
                            gameAttributes.setSleep(getSecondsToNextQuestRefresh());
                            status = "Nothing to do. Waiting refresh: ";
                        }
                    }
                    status += DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameAttributes.getSleep()));
                    controller.setMainContentText(questAttributes.getAdventuresMade(), questAttributes.getFreeAdventures(), questAttributes.getIllusionCaveMade(), equipmentAttributes.getInventoryUsedSpaces(), questAttributes.getDungeonsMade(), questAttributes.getFreeDungeons(), String.format("[%s]", status));
                }
            } catch (TimeOutException ex) {
                Log.warn("Time Out.");
                controller.setMainContentText("Time Out. Reconnecting...");
                gameParser.reconnect();
                gameAttributes.setSleep(15);
            } catch (WorkingException ex) {
                Log.warn("Working.");
                gameAttributes.setSleep(questParser.getWorkingSeconds() + SECURE_DELAY);
                controller.setMainContentText("Can't get information when Working.");
                status = String.format("Working. Busy until: %s %s", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameAttributes.getSleep())), status);
            } catch (FightResultException | RewardResultException ex) {
                Log.warn("Fight/Reward Result.");
                gameAttributes.setSleep(SECURE_DELAY);
                gameParser.getQuestParser().getResult();
                status = String.format("Processing Result. Busy until: %s %s", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameAttributes.getSleep())), status);
            } catch (AdventureRunningException ex) {
                Log.warn("Adventure Running.");
                gameAttributes.setSleep(questParser.getAdventureSeconds() + SECURE_DELAY);
                status = String.format("Quest (Already Running). Busy until: %s %s", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameAttributes.getSleep())), status);
                controller.setMainContentText(String.format("Can't get information when Quest Running. Reconnecting in %d Seconds.", gameAttributes.getSleep()));
            } catch (IllusionCaveRunningException ex) {
                Log.warn("Illusion Cave Running.");
                gameAttributes.setSleep(questParser.getIllusionCaveSeconds() + SECURE_DELAY);
                status = String.format("Illusion Cave. Busy until: %s %s", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameAttributes.getSleep())), status);
                controller.setMainContentText(String.format("Illusion Cave Running. Reconnecting in %d Seconds.", gameAttributes.getSleep()));
            } catch (IllusionDisabledException e) {
                Log.warn("Illusion Disabled.");
                gameAttributes.setSleep(SECURE_DELAY);
                status = String.format("Illusion Cave Disabled. Busy until: %s %s", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameAttributes.getSleep())), status);
            }
            Log.warn("THREAD SLEEPING: " + gameAttributes.getSleep() + "SEC");
            this.updateMessage(String.format("%s (%d Seconds)", status, gameAttributes.getSleep()));
            Thread.sleep(TimeUnit.SECONDS.toMillis(gameAttributes.getSleep()));
            Log.warn("FINISHED SLEEP - RESTARTING LOOP");
        }
    }

    private String startIllusionCave() throws IOException, InterruptedException, AdventureRunningException, TimeOutException, FightResultException, IllusionCaveRunningException, WorkingException, IllusionDisabledException, RewardResultException {
        gameParser.getQuestParser().startIllusionCave();
        gameParser.getGameAttributes().setSleep(gameParser.getQuestParser().getIllusionCaveSeconds() + SECURE_DELAY / 2);
        Log.warn("Delay: " + gameParser.getGameAttributes().getSleep());
        return "Illusion Cave. Busy until: ";
    }

    private String startDungeon() throws IOException, InterruptedException, RewardResultException, IllusionCaveRunningException, AdventureRunningException, WorkingException, IllusionDisabledException, TimeOutException, FightResultException {
        Log.warn(gameParser.getGameAttributes().getQuestAttributes().getDungeonsMade() + " / " + gameParser.getGameAttributes().getQuestAttributes().getFreeDungeons());
        gameParser.getQuestParser().startDungeon();
        gameParser.getGameAttributes().setSleep(SECURE_DELAY);
        Log.warn("Delay: " + gameParser.getGameAttributes().getSleep());
        return "Dungeon. Busy until: ";
    }

    private String startAdventure() throws InterruptedException, AdventureRunningException, IllusionCaveRunningException, IOException, TimeOutException, FightResultException, WorkingException, RewardResultException, IllusionDisabledException {
        GameAttributes gameAttributes = gameParser.getGameAttributes();
        QuestAttributes questAttributes = gameAttributes.getQuestAttributes();
        if (cfg.getOption(ConfigSingleton.Option.autoSellItems))
            gameParser.getEquipmentParser().sellItemsFromInventory(cfg.getOption(ConfigSingleton.Option.sellEpics));
        gameParser.getQuestParser().startAdventureByCriteria(new ExpCriteria());
        Adventure currentAdventure = questAttributes.getCurrentAdventure();
        gameAttributes.setSleep(currentAdventure.getDuration() + SECURE_DELAY);
        String questDescription = controller.getQuestDescription(currentAdventure.getDifficulty(), currentAdventure.getDuration(), currentAdventure.getExperience(), currentAdventure.getFightChance(), currentAdventure.getGold(), currentAdventure.getQuestID());
        questAttributes.setAdventuresMade(questAttributes.getAdventuresMade());
        controller.setMainContentText(questAttributes.getAdventuresMade(), questAttributes.getFreeAdventures(), questAttributes.getIllusionCaveMade(), gameAttributes.getEquipmentAttributes().getInventoryUsedSpaces(), questAttributes.getDungeonsMade(), questAttributes.getFreeDungeons(), questDescription);
        return String.format("Quest (Nº %d). Busy until: %s", questAttributes.getAdventuresMade() + 1, DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameAttributes.getSleep())));
    }

    private void increaseStats() throws IOException, InterruptedException, RewardResultException, AdventureRunningException, IllusionCaveRunningException, WorkingException, IllusionDisabledException, TimeOutException, FightResultException {
        gameParser.getUserParser().increaseStats();
    }

    private int getSecondsToNextQuestRefresh() {
        LocalTime timeNow = LocalTime.now();
        LocalTime refreshTime = LocalTime.parse(cfg.getProperty(ConfigSingleton.Property.resetTime));
        int secondsRefresh;
        if (timeNow.isBefore(refreshTime)) {
            secondsRefresh = (int) SECONDS.between(timeNow, refreshTime) + 120;
        } else {
            secondsRefresh = (int) Math.abs(SECONDS.between(timeNow, LocalTime.parse("23:59:59"))) + refreshTime.toSecondOfDay() + 120;

        }
        return secondsRefresh;
    }
}