package net.windyweather.syncfiles;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

import static net.windyweather.syncfiles.SyncFilesApp.*;

public class SyncFilesController {
    public SplitPane splitPaneOutsideContainer;
    public TableView tvPairTable;
    public TableColumn tcPathPair;
    public TableColumn tcPairStatus;
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
        /*
            Lots of stuff to do here but first set the placeholder in case
            there are no pairs yet.
         */
        Label placeholder = new Label();
        placeholder.setTextAlignment( TextAlignment.CENTER);
        placeholder.setText("Use New Pair then fill out\nthe pair in the Right Panel and\n"+
                "use Save Pair.");
        tvPairTable.setPlaceholder(placeholder);

    }

    /*
    break out the close stuff here so we can call it from two places
     */
    void AppCloseStuffToDo() {
        printSysOut( "AppCloseStuffToDo - save stuff here" );
        /*
            write the Pairs List
         */

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

    public void OnCloseButton(ActionEvent actionEvent) {
        /*
            Closing the stage will trigger the other way that the
            app can be shut down, so no need to do this stuff twice.
         */
        //closeApplication();
        /*
          get the scene from any GUI item, and get window from that.
          Then that's the stage and call close on it.
         */
        Stage stage = (Stage) splitPaneOutsideContainer.getScene().getWindow();
        stage.close();
    }

    public void OnNewPair(ActionEvent actionEvent) {
    }
}