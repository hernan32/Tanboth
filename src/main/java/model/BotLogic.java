package model;

import controller.TanothGUIController;
import javafx.concurrent.Task;
import model.game.parser.GameParser;

import java.io.IOException;

public class BotLogic {

    private final Thread botThread;
    private final Task botTask;

    public BotLogic(TanothGUIController controller, GameParser gameParser) throws IOException {
        botTask = new BotTask(gameParser, controller);
        botThread = new Thread(botTask);
    }

    public Task getBotTask() {
        return botTask;
    }

    public Thread getBotThread() {
        return botThread;
    }

}
