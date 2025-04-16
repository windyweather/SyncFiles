package net.windyweather.syncfiles;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Optional;
import java.util.prefs.Preferences;

import static net.windyweather.syncfiles.SyncFilesApp.*;

public class SyncFilesController {

    public Label lblStatus;
    /*
            Define the list that the TableView will watch
         */
    ObservableList<SyncFilesPair> pairObservableList = FXCollections.observableArrayList();


    public SplitPane splitPaneOutsideContainer;
    public TableView<SyncFilesPair> tvPairTable;
    public TableColumn<SyncFilesPair, String> tcPathPair;
    public TableColumn<SyncFilesPair, String> tcPairStatus;
    //private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        //welcomeText.setText("Welcome to SyncFiles Application!");
    }

    /*
Put some text in the status line to say what's up
*/
    public void setStatus( String sts ) {

        lblStatus.setText( sts );
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
        tcPairStatus.setStyle( "-fx-alignment: CENTER-RIGHT;");

        printSysOut("Set up CellValueFactories for Columns");

        tcPathPair.setCellValueFactory( new Callback<TableColumn.CellDataFeatures<SyncFilesPair, String>, ObservableValue<String>>() {
            public ObservableValue<String> call( TableColumn.CellDataFeatures<SyncFilesPair,
                    String> p) {
                //printSysOut("tcPathPair CellValueFactory called");
                return p.getValue().sPairNameProperty();
            }
        });


        tcPairStatus.setCellValueFactory( new Callback<TableColumn.CellDataFeatures<SyncFilesPair, String>, ObservableValue<String>>() {
            public ObservableValue<String> call( TableColumn.CellDataFeatures<SyncFilesPair,
                    String> p) {
                //printSysOut("tcPairStatus CellValueFactory called");
                return p.getValue().sPairStatusProperty();
            }
        });

        tvPairTable.setItems(pairObservableList);

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

    /*
    Make sure item of interest is selected and visible
    */
    private void SelectAndFocusIndex( int idx ) {
        tvPairTable.getSelectionModel().select(idx);
        if (!tvPairTable.isVisible() ){
            tvPairTable.getFocusModel().focus(idx);
            tvPairTable.scrollTo( idx);
        }
        tvPairTable.scrollTo( idx);
    }

    /*
        Load the list with some pair names/status
     */
    private void SomeTestPairData() {
        pairObservableList.addAll(
                new SyncFilesPair( "1st Pair", "none"),
                new SyncFilesPair( "2nd Pair", "none"),
                new SyncFilesPair( "3rd Pair", "none"),
                new SyncFilesPair( "4th Pair", "none"),
                new SyncFilesPair( "5th Pair", "none"),
                new SyncFilesPair( "6th Pair", "none"),
                new SyncFilesPair( "7th Pair", "none"),
                new SyncFilesPair( "8th Pair", "none"),
                new SyncFilesPair( "9th Pair", "none"),
                new SyncFilesPair( "10th Pair", "none")
        );
    }


    public void OnNewPair(ActionEvent actionEvent) {
        printSysOut("OnNewPair");
        SomeTestPairData();
    }

    public void OnAboutApp(ActionEvent actionEvent) {
    }

    public void OnOpenPair(ActionEvent actionEvent) {
    }

    public void OnPairMoveUp(ActionEvent actionEvent) {
    }

    public void OnPairMoveDown(ActionEvent actionEvent) {
    }

    public void OnPairMoveTop(ActionEvent actionEvent) {

        int idx = tvPairTable.getSelectionModel().getSelectedIndex();
        if ( idx == -1 ) {
            setStatus("Select Item");
            return;
        }
        if ( idx == 0 ) {
            setStatus( "Pair already at top");
            printSysOut("OnPairMoveTop - already at top");
            return;
        }
        printSysOut(String.format("OnMovePairTop - moving idx %d to top", idx ) );
        SyncFilesPair pair = pairObservableList.get(idx);
        pairObservableList.remove(idx);
        pairObservableList.addFirst(pair );
        SelectAndFocusIndex( 0);
        setStatus("Pair moved to top");
    }

    public void OnRemovePair(ActionEvent actionEvent) {

        int idx = tvPairTable.getSelectionModel().getSelectedIndex();
        if ( idx == -1 ) {
            setStatus("Select Item");
            return;
        }
        /*
            Confirm the user wants to do this
         */
        setStatus("Confirm or Cancel");
        Alert cnfrmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        cnfrmAlert.setTitle("Confirm Remove Pair?");
        cnfrmAlert.setHeaderText( "Confirm Remove Pair");
        cnfrmAlert.setContentText("Remove cannot be UnDone" );
        Optional<ButtonType> result = cnfrmAlert.showAndWait();
        if ( result.isEmpty() || result.get() != ButtonType.OK ) {
            setStatus( "Remove canceled");
            return;
        }
        /*
            Remove the pair...
         */
        pairObservableList.remove(idx);

        setStatus("Pair Removed");
    }

    public void OnPairOneToTwo(ActionEvent actionEvent) {
    }

    public void OnPairTwoToOne(ActionEvent actionEvent) {
    }

    public void OnSavePairs(ActionEvent actionEvent) {
    }
}