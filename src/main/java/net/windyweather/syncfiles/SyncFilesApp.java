package net.windyweather.syncfiles;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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