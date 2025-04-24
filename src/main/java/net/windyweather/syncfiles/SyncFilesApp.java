package net.windyweather.syncfiles;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static net.windyweather.syncfiles.SyncFilesController.printSysOut;

public class SyncFilesApp extends Application {


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