package net.windyweather.syncfiles;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

import static net.windyweather.syncfiles.SyncFilesApp.*;

public class SyncFilesController {
    public SplitPane splitPaneOutsideContainer;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to SyncFiles Application!");
    }

    /*
Put some text in the status line to say what's up
*/
    public void setStatus( String sts ) {

        //txtStatus.setText( sts );
    }
    //
    // Do this in one place so we can easily turn it off later
    //
    public static void printSysOut( String str ) {
        System.out.println(str);
    }

    @FXML
    /*
        Called to initialize the controller
     */
    void initialize(){
        SetUpStuff();
    }
    /*
        Called from App to set things up
     */
    public void SetUpStuff(){
    }

    /*
    break out the close stuff here so we can call it from two places
     */
    void AppCloseStuffToDo() {
        printSysOut( "SSController: AppCloseStuffToDo - save your stuff here" );
        // write the Pairs List
        // Window pos / size are saved in SSApplication
        //SavePairsList();

        printSysOut("AppCloseStuffToDo: Save Window Pos/Size");

        /*
            The place this was done in the App class didn't work
            if a menu item or button closed the app.
         */
        Stage stage = (Stage)splitPaneOutsideContainer.getScene().getWindow();
        Preferences preferences = Preferences.userRoot().node(NODE_NAME);
        preferences.putDouble(WINDOW_POSITION_X, stage.getX());
        preferences.putDouble(WINDOW_POSITION_Y, stage.getY());
        preferences.putDouble(WINDOW_WIDTH, stage.getWidth());
        preferences.putDouble(WINDOW_HEIGHT, stage.getHeight());
    }

    /*
        Call when app closing by the Window X button, we hope
     */
    public void closeApplication() {
        printSysOut( "App closing");
        AppCloseStuffToDo();
    }

}