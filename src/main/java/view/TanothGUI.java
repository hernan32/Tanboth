package view;

import controller.TanothGUIController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class TanothGUI extends Application {

    private Stage stage;
    private SystemTray tray;
    private TrayIcon trayIcon;
    private FXMLLoader fxmlLoader;
    private double xOffset = 0;
    private double yOffset = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws IOException {
        this.stage = stage;
        fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TanothGUI.fxml"));
        Parent root = fxmlLoader.load();
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        Platform.setImplicitExit(false);
        SwingUtilities.invokeLater(this::addAppToTray);
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        //set Stage boundaries to the lower right corner of the visible bounds of the main screen
        this.stage.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 500);
        this.stage.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - 260);
        Scene scene = new Scene(root, 500, 265);
        javafx.scene.image.Image icon = new javafx.scene.image.Image(this.getClass().getResourceAsStream("/images/tanothicon.png"));
        stage.getIcons().add(icon);
        scene.getRoot().requestFocus();
        stage.setTitle("Tanboth");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setResizable(false);
    }

    /**
     * Sets up a system tray icon for the application. (by jewelsea)
     */
    private void addAppToTray() {
        try {
            // ensure awt toolkit is initialized.
            Toolkit.getDefaultToolkit();

            // app requires system tray support, just exit if there is no support.
            if (!SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            // set up a system tray icon.
            tray = SystemTray.getSystemTray();
            Image image = ImageIO.read(this.getClass().getResource("/images/tanothtrayicon.png"));
            trayIcon = new TrayIcon(image);

            // if the user double-clicks on the tray icon, show the main app stage.
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            // if the user selects the default menu item (which includes the app name),
            // show the main app stage.
            MenuItem openItem = new MenuItem("Open");
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            // the convention for tray icons seems to be to set the default icon for opening
            // the application stage in a bold font.
            Font defaultFont = Font.decode(null);
            Font boldFont = defaultFont.deriveFont(Font.BOLD);
            openItem.setFont(boldFont);

            // to really exit the application, the user must go to the system tray icon
            // and select the exit option, this will shutdown JavaFX and remove the
            // tray icon (removing the tray icon will also shut down AWT).
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(event -> {
                Platform.exit();
                removeAppToTray();
            });

            // setup the popup menu for the application.
            final PopupMenu popup = new PopupMenu();
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            // add the application tray icon to the system tray.
            tray.add(trayIcon);
            //pass data to controller
            TanothGUIController controller = fxmlLoader.getController();
            controller.setTrayData(tray, trayIcon);
        } catch (AWTException | IOException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }

    private void removeAppToTray() {
        tray.remove(trayIcon);
    }

    /**
     * Shows the application stage and ensures that it is brought ot the front of all stages.
     */
    private void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }
}
