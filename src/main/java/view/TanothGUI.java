package view;

import controller.TanothGUIController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws IOException {
        stage = primaryStage;
        fxmlLoader = new FXMLLoader(getClass().getResource("/view/resources/fxml/TanothGUI.fxml"));
        Parent root = fxmlLoader.load();
        Platform.setImplicitExit(false);
        SwingUtilities.invokeLater(this::addAppToTray);
        Scene scene = new Scene(root, 500, 260);
        scene.getRoot().requestFocus();
        primaryStage.setTitle("Tanboth");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
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
            Image image = ImageIO.read(this.getClass().getResource("/view/resources/images/tanothico.png"));
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
