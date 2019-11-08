package model;

import com.esotericsoftware.minlog.Log;
import configuration.ConfigSingleton;
import controller.TanothGUIController;
import javafx.concurrent.Task;
import model.game_parser.GameParser;
import model.game_parser.adventure_parser.AdventureParser;
import model.game_parser.adventure_parser.adventure.AdventureAttributes;
import model.game_parser.adventure_parser.adventure.criterias.TimeCriteria;
import model.game_parser.adventure_parser.adventure.exception.*;
import model.game_parser.equipment_parser.EquipmentParser;
import model.game_parser.game.GameAttributes;
import model.game_parser.game.exception.TimeOutException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;

public class BotLogic {

    private Thread botThread;
    private Task botTask;
    private ConfigSingleton cfg = ConfigSingleton.getInstance();

    public BotLogic(TanothGUIController controller, GameParser gameParser) throws IOException {
        botTask = new Task() {
            @Override
            protected Object call() throws Exception {
                AdventureParser adventureParser = gameParser.getAdventureParser();
                EquipmentParser equipmentParser = gameParser.getEquipmentParser();
                GameAttributes gameAttributes = gameParser.getGameAttributes();
                String status = "";
                final int SECURE_DELAY = 60;

                while (true) {
                    try {
                        status = "";
                        Log.warn("COLLECT DATA: STARTING");
                        controller.collectData();
                        Log.warn("COLLECT DATA: DONE");
                        if ((cfg.getOption(ConfigSingleton.Option.autoIncreaseStats))) {
                            gameParser.getUserParser().increaseStats();
                        }
                        if (gameAttributes.getAdventuresMade() < gameAttributes.getFreeAdventures()) {
                            Log.warn("ADV < FREE: DONE (Starting Adventure)");
                            AdventureAttributes adventure;
                            if (cfg.getOption(ConfigSingleton.Option.autoSellItems))
                                equipmentParser.sellItemsFromInventory(cfg.getOption(ConfigSingleton.Option.sellEpics));
                            adventure = adventureParser.startAdventureByCriteria(new TimeCriteria());
                            Log.warn("Adventure Started: DONE");
                            gameAttributes.setSleep(adventure.getDuration() + SECURE_DELAY);
                            String questDescription = controller.getQuestDescription(adventure.getDifficulty(), adventure.getDuration(), adventure.getExperience(), adventure.getFightChance(), adventure.getGold(), adventure.getQuestID());
                            gameAttributes.setAdventuresMade(gameAttributes.getAdventuresMade());
                            controller.setMainContentText(gameAttributes.getAdventuresMade(), gameAttributes.getFreeAdventures(), gameAttributes.getBossMapMade(), gameAttributes.getInventorySpaces(), gameAttributes.getDungeonsMade(), gameAttributes.getFreeDungeons(), questDescription);
                            status = String.format("Quest (Nº %d). Busy until: %s", gameAttributes.getAdventuresMade() + 1, DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameAttributes.getSleep())));
                        } else {
                            Log.warn("ADV >= FREE: DONE");
                            Log.warn(gameAttributes.getDungeonsMade() + " / " + gameAttributes.getFreeDungeons());
                            if (gameAttributes.getDungeonsMade() < gameAttributes.getFreeDungeons()) {
                                Log.warn("DUNGEON < FREE: DONE (Starting Dungeon)");
                                gameParser.getAdventureParser().startDungeon();
                                Log.warn("Dungeon Started: DONE");
                                gameAttributes.setSleep(SECURE_DELAY);
                                Log.warn("Delay: " + gameAttributes.getSleep());
                                status = "Dungeon. Busy until: ";
                            } else {
                                Log.warn("DUNGEON >= FREE : DONE");
                                if (gameAttributes.getBossMapMade() == 0) {
                                    Log.warn("BOSS = 0: DONE (Starting Illusion Cave)");
                                    gameParser.getAdventureParser().startIllusionCave();
                                    Log.warn("Illusion Cave Started: DONE");
                                    gameAttributes.setSleep(adventureParser.getIllusionCaveSeconds() + SECURE_DELAY / 2);
                                    Log.warn("Delay: " + gameAttributes.getSleep());
                                    status = "Illusion Cave. Busy until: ";
                                } else {
                                    Log.warn("BOSS != 0: DONE");
                                    gameAttributes.setSleep(getSecondsToNextQuestRefresh());
                                    status = "Nothing to do. Waiting refresh: ";
                                }
                            }
                            status += DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameAttributes.getSleep()));
                            controller.setMainContentText(gameAttributes.getAdventuresMade(), gameAttributes.getFreeAdventures(), gameAttributes.getBossMapMade(), gameAttributes.getInventorySpaces(), gameAttributes.getDungeonsMade(), gameAttributes.getFreeDungeons(), String.format("[%s]", status));
                        }
                    } catch (TimeOutException ex) {
                        Log.warn("Time Out.");
                        controller.setMainContentText("Time Out. Reconnecting...");
                        gameParser.reconnect();
                        gameAttributes.setSleep(15);
                    } catch (WorkingException ex) {
                        Log.warn("Working.");
                        gameAttributes.setSleep(adventureParser.getWorkingSeconds() + SECURE_DELAY);
                        controller.setMainContentText("Can't get information when Working.");
                        status = String.format("Working. Busy until: %s %s", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameAttributes.getSleep())), status);
                    } catch (FightResultException | RewardResultException ex) {
                        Log.warn("Fight/Reward Result.");
                        gameAttributes.setSleep(SECURE_DELAY);
                        gameParser.getAdventureParser().getResult();
                        status = String.format("Processing Result. Busy until: %s %s", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameAttributes.getSleep())), status);
                    } catch (AdventureRunningException ex) {
                        Log.warn("Adventure Running.");
                        gameAttributes.setSleep(adventureParser.getQuestSeconds() + SECURE_DELAY);
                        status = String.format("Quest (Already Running). Busy until: %s %s", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now().plusSeconds(gameAttributes.getSleep())), status);
                        controller.setMainContentText(String.format("Can't get information when Quest Running. Reconnecting in %d Seconds.", gameAttributes.getSleep()));
                    } catch (IllusionCaveRunningException ex) {
                        Log.warn("Illusion Cave Running.");
                        gameAttributes.setSleep(adventureParser.getIllusionCaveSeconds() + SECURE_DELAY);
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
        };
        botThread = new Thread(botTask);
    }

    public Task getBotTask() {
        return botTask;
    }

    public Thread getBotThread() {
        return botThread;
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
