package net.windyweather.syncfiles;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.prefs.Preferences;

import static net.windyweather.syncfiles.SyncFilesController.printSysOut;

public class SyncFilesApp extends Application {


    /*
        Coding strings for windows position / size in preferences
     */
    public static final String WINDOW_POSITION_X = "Window_Position_X";
    public static final String WINDOW_POSITION_Y = "Window_Position_Y";
    public static final String WINDOW_WIDTH = "Window_Width";
    public static final String WINDOW_HEIGHT = "Window_Height";
    private static final double DEFAULT_X = 10;
    private static final double DEFAULT_Y = 10;
    private static final double DEFAULT_WIDTH = 1000;
    private static final double DEFAULT_HEIGHT = 600;
    public static final String NODE_NAME = "SyncFilesApp";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SyncFilesApp.class.getResource("syncfiles-view.fxml"));


        Scene scene = new Scene(fxmlLoader.load(), 1100, 600);

        SyncFilesController sfController = fxmlLoader.getController();

        stage.setTitle("Sync Files Application");
        stage.setScene(scene);
        stage.setOnHiding( e ->sfController.closeApplication() );
        stage.show();

        /* very cool way to save and restore window size and position.
          David Bell's blog
          Found at: https://broadlyapplicable.blogspot.com/2015/02/javafx-restore-window-size-position.html
          Thanks David Bell from February 23, 2015,
          JavaFX Restore Window Size & Position
         */
        /*
         Pull the saved preferences and set the stage size and start location
         */
        Preferences pref = Preferences.userRoot().node(NODE_NAME);
        double x = pref.getDouble(WINDOW_POSITION_X, DEFAULT_X);
        double y = pref.getDouble(WINDOW_POSITION_Y, DEFAULT_Y);
        double width = pref.getDouble(WINDOW_WIDTH, DEFAULT_WIDTH);
        double height = pref.getDouble(WINDOW_HEIGHT, DEFAULT_HEIGHT);
        /*
            Remove these things. We will never use Prefs again.
            Prefs are not such a good idea since we don't know where
            they are stored and can't manage them with something like
            a File Explorer. We will use XML files instead.
         */
        pref.remove(WINDOW_POSITION_X);
        pref.remove(WINDOW_POSITION_Y);
        pref.remove(WINDOW_WIDTH);
        pref.remove(WINDOW_HEIGHT);

        if ( false ) {
            stage.setX(x);
            stage.setY(y);
            stage.setWidth(width);
            stage.setHeight(height);
        }

        /*
            Use our fancy new XML file reader to restore the window pos/size
         */
        WindowSaveRestore.RestoreWindowPosSize( stage, "windyweather", "SyncFiles");

        printSysOut("App Start") ;

    }

    public static void main(String[] args) {
        launch();
    }
}