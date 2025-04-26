package net.windyweather.syncfiles;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static net.windyweather.syncfiles.SyncFilesController.printSysOut;

public class SyncFilesApp extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SyncFilesApp.class.getResource("syncfiles-view.fxml"));


        Scene scene = new Scene(fxmlLoader.load(), 1100, 600);

        SyncFilesController sfController = fxmlLoader.getController();

        stage.setTitle("Sync Files Application");

        /*
            Stick a program icon on the window
         */
        try {
            Image imgIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("sync-icon-flip-240.png")) );
            stage.getIcons().add(imgIcon);

        } catch ( Exception e ) {
            printSysOut( e.toString() );
        }

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