package net.windyweather.syncfiles;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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


    /*
        Directory for storing settings, including Pairs in XML file
     */
    private String sWindyWeatherDir;
    private String sXMLPairsListPath;

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

    /*
        Save and Restore SyncFilesPair from pairObservableList to XML
     */
    void SavePairsList() {

        XMLEncoder encoder = null;
        /*
            Copy the ObservableList to just a List
         */
        List<SyncFilesPair> listOfPairs = new ArrayList<SyncFilesPair>(100);
        listOfPairs.addAll(pairObservableList);

        try{
            encoder=new XMLEncoder(new BufferedOutputStream(new FileOutputStream( sXMLPairsListPath )));
        }catch(FileNotFoundException fileNotFound){
            printSysOut( String.format("SavePairsList: While Creating or Opening the xml file %s", sXMLPairsListPath) );
        }
        /*
            Write each pair out and then close the file
         */
        if ( false ) {
            for (SyncFilesPair pair : pairObservableList) {
                assert encoder != null;
                encoder.writeObject(pair);
            }
        }
        assert encoder != null;
        encoder.writeObject( listOfPairs );

        printSysOut( String.format("SavePairsList: stored %d pairs to %s", listOfPairs.size(), sXMLPairsListPath));
        encoder.close();
    }


    /*
        Restore the pairs from the XML file if its there
     */

    void RestorePairsList()  {
        if ( true ) {
            // Use XMLDecoder to read the XML file in.
            List<SyncFilesPair> listFromXML = List.of();
            try {
                printSysOut("RestorePairsList");
                final XMLDecoder decoder = new XMLDecoder(new FileInputStream(sXMLPairsListPath));
                listFromXML = (List<SyncFilesPair>) decoder.readObject();
                decoder.close();
                printSysOut(String.format("%d pairs restored", pairObservableList.size()));

            } catch (Exception e) {
                printSysOut(String.format("Pairs Not Restored %s", sXMLPairsListPath));
            }
            printSysOut(String.format("RestorePairsList %d pairs restored", listFromXML.size() ));

            if (!listFromXML.isEmpty())
            {
                pairObservableList.addAll( listFromXML );
                setStatus(String.format("%d pairs restored", listFromXML.size()));
            }
        }
    }


    @FXML
    /*
        Called to initialize the controller
     */
    void initialize() throws FileNotFoundException {
        SetUpStuff();
    }
    /*
        Called from App to set things up
     */
    public void SetUpStuff()  {
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

        //System.getProperty("user.dir")
        String currentUsersHomeDir = System.getProperty("user.home");
        sWindyWeatherDir = currentUsersHomeDir + File.separator + ".windyweather";

        File fWW = new File(sWindyWeatherDir);
        boolean bWWCreated = fWW.mkdir();

        if (bWWCreated) {
            printSysOut(String.format("Config Directory Created: %s", sWindyWeatherDir ) );
        } else {
            printSysOut(String.format("Config Directory Exists: %s", sWindyWeatherDir ) );
        }
        /*
            Setup the pairs list file path
         */
        sXMLPairsListPath = sWindyWeatherDir + File.separator + "SyncFilesPairsList.xml";

        RestorePairsList();

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

        SyncFilesPair[] somePairs =
                {
                new SyncFilesPair( "1st Pair", "</string>"),
                new SyncFilesPair( "2nd Pair", "none"),
                new SyncFilesPair( "3rd Pair", "none"),
                new SyncFilesPair( "4th Pair", "none"),
                new SyncFilesPair( "5th Pair", "none"),
                new SyncFilesPair( "6th Pair", "<none>"),
                new SyncFilesPair( "7th Pair", "none"),
                new SyncFilesPair( "8th Pair", "none"),
                new SyncFilesPair( "9th Pair", "none"),
                new SyncFilesPair( "10th Pair", "none")
                };

        int idx = 0;
        for ( SyncFilesPair aPair : somePairs ) {

            String sName = aPair.sPairName.getValue();
            printSysOut(String.format("String.valueOf(aPair.sPairName) : %s", sName));
            aPair.sFilePathOne = new SimpleStringProperty("D:\\YourGameHere\\ScreenShots\\"+sName);
            aPair.sFilePathTwo = new SimpleStringProperty("E:\\BackupOfScreenShots\\"+sName);
            aPair.sExcludeFileTypes = new SimpleStringProperty( ".Some;.Excluded;.Types;."+sName);
            aPair.bSubFolders = new SimpleBooleanProperty( (idx % 5) == 0 );
            aPair.bVerifyCopied = new SimpleBooleanProperty( (idx % 6) == 0 );
            aPair.bVerifyNotCopied = new SimpleBooleanProperty( (idx % 7) == 0 );
            aPair.bRecoverVerifyFailure = new SimpleBooleanProperty( (idx % 9) == 0 );
            aPair.bOverrideReadOnly = new SimpleBooleanProperty( (idx % 8) == 0 );

            idx++;

        }

        pairObservableList.addAll(somePairs);

    }


    public void OnNewPair(ActionEvent actionEvent) {
        printSysOut("OnNewPair");
        SomeTestPairData();
    }

    public void OnAboutApp(ActionEvent actionEvent) throws IOException {

        printSysOut("onAboutApplication - launch about dialog");

        Stage stageOfUs = (Stage) tvPairTable.getScene().getWindow();
        Stage stage = new Stage();

        FXMLLoader fxmlloader = new FXMLLoader( AboutDialog.class.getResource("about-dialog.fxml"));
        Scene aboutScene = new Scene( fxmlloader.load() );
        AboutDialog aboutControl = (AboutDialog) fxmlloader.getController();

        Parent root = FXMLLoader.load(
                Objects.requireNonNull(AboutDialog.class.getResource("about-dialog.fxml")));
        stage.setScene(new Scene(root));
        stage.setTitle("About Sync Files");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.initOwner( stageOfUs );

         printSysOut("onAbout - show about dialog");
        stage.show();




    }

    public void OnOpenPair(ActionEvent actionEvent) {
    }

    public void OnPairMoveUp(ActionEvent actionEvent) {
        int idx = tvPairTable.getSelectionModel().getSelectedIndex();
        if ( idx == -1 ) {
            setStatus("Select Item");
            return;
        }
        int num = pairObservableList.size();

        if ( idx == 0 ) {
            setStatus( "Already First");
            printSysOut("OnPairMoveUp - already at top");
            return;
        }
        SyncFilesPair aPair = pairObservableList.get(idx);
        pairObservableList.remove(idx);
        pairObservableList.add( idx-1, aPair );
        SelectAndFocusIndex(idx-1);
        setStatus( "Moved Up");
    }

    public void OnPairMoveDown(ActionEvent actionEvent) {
        int idx = tvPairTable.getSelectionModel().getSelectedIndex();
        if ( idx == -1 ) {
            setStatus("Select Item");
            return;
        }
        if ( (idx+1) == pairObservableList.size() ) {
            // already at bottom so we are done
            printSysOut(String.format("OnMovePairDown - idx %d already at bottom", idx ) );
            SelectAndFocusIndex(idx);
            setStatus("Already Last");
            return;
        }
        printSysOut(String.format("OnMovePairDown - moving idx %d Down one item", idx ) );
        SyncFilesPair pair = pairObservableList.get(idx);
        pairObservableList.remove(idx);
        pairObservableList.add( idx+1, pair);
        SelectAndFocusIndex( idx+1);
        setStatus("Moved Down");
    }

    public void OnPairMoveTop(ActionEvent actionEvent) {

        int idx = tvPairTable.getSelectionModel().getSelectedIndex();
        if ( idx == -1 ) {
            setStatus("Select Item");
            return;
        }
        if ( idx == 0 ) {
            setStatus( "Already First");
            printSysOut("OnPairMoveTop - already first");
            return;
        }
        printSysOut(String.format("OnMovePairTop - moving idx %d to first", idx ) );
        SyncFilesPair pair = pairObservableList.get(idx);
        pairObservableList.remove(idx);
        pairObservableList.addFirst(pair );
        SelectAndFocusIndex( 0);
        setStatus("Moved First");
    }
    public void OnPairMoveBottom(ActionEvent actionEvent) {
        int idx = tvPairTable.getSelectionModel().getSelectedIndex();
        if ( idx == -1 ) {
            setStatus("Select Item");
            return;
        }
        int num = pairObservableList.size();

        if ( idx == (num-1) ) {
            setStatus( "Already Last");
            printSysOut("OnPairMoveBottom - already last");
            return;
        }
        SyncFilesPair aPair = pairObservableList.get(idx);
        pairObservableList.remove(idx);
        pairObservableList.addLast( aPair );
        SelectAndFocusIndex( num-1);
        setStatus( "Moved Last");

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
        Window wParent = tvPairTable.getScene().getWindow();
        cnfrmAlert.initOwner( wParent);
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
        SavePairsList();
        setStatus("Pairs Saved");
    }


}