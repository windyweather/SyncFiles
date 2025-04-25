package net.windyweather.syncfiles;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
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

import org.codehaus.plexus.util.DirectoryScanner;


import static net.windyweather.syncfiles.SyncFilesApp.*;

/*
    Controller class for GUI - Pretty much the whole program is here
 */
public class SyncFilesController {


    /*
             Define the list that the TableView will watch
         */
    ObservableList<SyncFilesPair> pairObservableList = FXCollections.observableArrayList();

    public SplitPane splitPaneOutsideContainer;
    public TableView<SyncFilesPair> tvPairTable;
    public TableColumn<SyncFilesPair, String> tcPathPair;
    public TableColumn<SyncFilesPair, String> tcPairStatus;
    public Label lblStatus;
    public TreeTableView<SyncFileOperation> tvFileTree;
    public TreeTableColumn<SyncFileOperation, String> tcSourcePath;
    public TreeTableColumn<SyncFileOperation, Integer> tcFileSize;
    public TreeTableColumn<SyncFileOperation, String> tcActionPending;
    public TreeTableColumn<SyncFileOperation, String> tcStatus;

    public TextField txtPairName;
    public CheckBox chkExcludeFileTypes;
    public TextField txtExcludeFileTypes;
    public TextField txtFilePathOne;
    public TextField txtFilePathTwo;
    public CheckBox chkIncludeSubfolders;
    public CheckBox chkVerifyCopied;
    public CheckBox chkVerifyNotCopied;
    public CheckBox chkRecoverVerifyFailure;
    public CheckBox chkOverrideReadOnly;

    public Button btnTwoToOne;
    public Button btnOneToTwo;
    public Label lblLastSyncDateTime;
    public Label lblTotalBytes;
    public Label lblOperations;
    public Label lblProgress;


    /*
        Directory for storing settings, including Pairs in XML file
     */
    private String sWindyWeatherDir;
    private String sXMLPairsListPath;

    @FXML
    protected void onHelloButtonClick() {
        //welcomeText.setText("Welcome to SyncFiles Application!");
    }

    private  long longTotalBytes;
    private int intOperations;

    private SyncFilesPair theOpenPair;

    public SyncFilesController() {};

    /*
        Make the total bytes easily readable
     */
    private void SetTotalBytes() {

        String sTotalBytes = "";

        if ( longTotalBytes < 10000 )  {
            sTotalBytes = String.valueOf(longTotalBytes);
        } else if ( longTotalBytes < 10000000 ) {
            sTotalBytes = String.valueOf( longTotalBytes / 1024 )+" KB";
        } else if ( longTotalBytes < 10000000000L ) {
            sTotalBytes = String.valueOf( longTotalBytes / (1024*1024) )+" MB";
        } else if ( longTotalBytes < 10000000000000L ) {
            sTotalBytes = String.valueOf( longTotalBytes / ( 1024L*1024L*1024L) )+" GB";
        }
        lblTotalBytes.setText( sTotalBytes);
        lblOperations.setText( String.valueOf(intOperations));
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
        tcPairStatus.setStyle( "-fx-alignment: CENTER;");

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



        chkIncludeSubfolders.setSelected( true );

        txtFilePathOne.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue) {
                    System.out.println( String.format("Change FilePathOne? %s", txtFilePathOne.getText() ) );
                    btnOneToTwo.setDisable( false );
                }
            }
        });


        txtFilePathTwo.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue) {
                    System.out.println( String.format("Change FilePathTwo? %s", txtFilePathTwo.getText() ) );
                    btnTwoToOne.setDisable( false );
                }
            }
        });

       /*
            Property Value Factories for Tree Columns
         */
        tcSourcePath.setCellValueFactory( new TreeItemPropertyValueFactory<>("SSourcePath"));
        tcFileSize.setCellValueFactory( new TreeItemPropertyValueFactory<>("IntSize"));
        tcActionPending.setCellValueFactory( new TreeItemPropertyValueFactory<>( "SOperation"));
        tcStatus.setCellValueFactory( new TreeItemPropertyValueFactory<>("Status"));

        tcFileSize.setStyle("-fx-alignment: CENTER-RIGHT;");
        tcActionPending.setStyle("-fx-alignment: CENTER;");
        tcStatus.setStyle("-fx-alignment: CENTER;");


    } // end of SetUpStuff

    /*
    break out the close stuff here so we can call it from two places
     */
    void AppCloseStuffToDo() {
        printSysOut( "AppCloseStuffToDo - save stuff here" );
        /*
            write the Pairs List
         */
        SavePairsList();
        //setStatus("Pairs Saved");

        printSysOut("AppCloseStuffToDo: Save Window Pos/Size");

        /*
            The place this was done in the App class didn't work
            if a menu item or button closed the app.
         */
        Stage stage = (Stage)splitPaneOutsideContainer.getScene().getWindow();

        /*
            Call the shiny new Window XML Save
         */
        WindowSaveRestore.SaveWindowPosSize( stage );


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
        Add a new pair at the top and focus on it
     */
    private void ANewPair() {

        SyncFilesPair aPair = new SyncFilesPair( "New Pair", "none");

        String sName = aPair.sPairName.getValue();
        printSysOut(String.format("String.valueOf(aPair.sPairName) : %s", sName));
        aPair.sFilePathOne = new SimpleStringProperty("");
        aPair.sFilePathTwo = new SimpleStringProperty("");
        aPair.sExcludeFileTypes = new SimpleStringProperty("" + sName);
        aPair.bExcludeFileTypes = new SimpleBooleanProperty( false );
        aPair.bSubFolders = new SimpleBooleanProperty(true);
        aPair.bVerifyCopied = new SimpleBooleanProperty(false );
        aPair.bVerifyNotCopied = new SimpleBooleanProperty(false );
        aPair.bRecoverVerifyFailure = new SimpleBooleanProperty(false );
        aPair.bOverrideReadOnly = new SimpleBooleanProperty(false );

        pairObservableList.addFirst(aPair);
        SelectAndFocusIndex( 0 );

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
        ANewPair();
        setStatus("New Pair Created");
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

    /*
        Did the user screw up and leave unsaved changes in the GUI?
     */
    private boolean OpenPairUnsavedChanges() {

        if ( ( theOpenPair.sPairName.getValue().equals(txtPairName.getText() ) ) &&
                ( theOpenPair.bOverrideReadOnly.getValue() == chkOverrideReadOnly.isSelected() ) &&
                ( theOpenPair.bRecoverVerifyFailure.getValue() == chkRecoverVerifyFailure.isSelected() ) &&
                ( theOpenPair.bVerifyCopied.getValue() == chkVerifyCopied.isSelected() ) &&
                ( theOpenPair.bVerifyNotCopied.getValue() == chkVerifyNotCopied.isSelected() ) &&
                ( theOpenPair.bSubFolders.getValue() == chkIncludeSubfolders.isSelected() ) &&
                ( theOpenPair.sFilePathOne.getValue().equals( txtFilePathOne.getText() )) &&
                ( theOpenPair.sFilePathTwo.getValue().equals( txtFilePathTwo.getText() ))
        ) {
            return false; // No Unsaved changes
        }
        return true;
    }

    /*
        Open a pair in the right panel
     */
    public void OnOpenPair(ActionEvent actionEvent) {

        if ( theOpenPair != null && OpenPairUnsavedChanges() ) {
            /*
            Confirm the user wants to do this
            */
            setStatus("Unsaved Changes");
            Alert cnfrmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            Window wParent = tvPairTable.getScene().getWindow();
            cnfrmAlert.initOwner( wParent);
            cnfrmAlert.setTitle("Confirm Lose Unsaved Changes to Pair?");
            cnfrmAlert.setHeaderText( "Confirm Lose Unsaved Changes to Open Pair");
            cnfrmAlert.setContentText("Loss cannot be UnDone" );
            Optional<ButtonType> result = cnfrmAlert.showAndWait();
            if ( result.isEmpty() || result.get() != ButtonType.OK ) {
                setStatus( "Open canceled");
                return;
            }

        }

        int idx = tvPairTable.getSelectionModel().getSelectedIndex();
        if ( idx == -1 ) {
            setStatus("Select Item");
            return;
        }
        if ( theOpenPair != null ) {
            theOpenPair.sPairStatus.setValue( "Closed");
        }
        theOpenPair = pairObservableList.get(idx);

        txtPairName.setText( theOpenPair.sPairName.getValue() );
        txtFilePathOne.setText( theOpenPair.sFilePathOne.getValue() );
        txtFilePathTwo.setText( theOpenPair.sFilePathTwo.getValue() );
        chkExcludeFileTypes.setSelected( theOpenPair.bExcludeFileTypes.getValue() );
        txtExcludeFileTypes.setText( theOpenPair.sExcludeFileTypes.getValue() );
        chkIncludeSubfolders.setSelected( theOpenPair.bSubFolders.getValue() );
        chkVerifyCopied.setSelected( theOpenPair.bVerifyCopied.getValue() );
        chkVerifyNotCopied.setSelected( theOpenPair.bVerifyNotCopied.getValue() );
        chkRecoverVerifyFailure.setSelected( theOpenPair.bRecoverVerifyFailure.getValue() );
        chkOverrideReadOnly.setSelected( theOpenPair.bOverrideReadOnly.getValue() );

        theOpenPair.sPairStatus.setValue( "Open");
        setStatus("Pair Opened");
    }

    public void OnUpdatePair(ActionEvent actionEvent) {

        if ( theOpenPair == null ) {
            setStatus("No Pair is Open");
            return;
        }

        theOpenPair.sPairName.setValue( txtPairName.getText() );
        theOpenPair.sFilePathOne.setValue( txtFilePathOne.getText() );
        theOpenPair.sFilePathTwo.setValue( txtFilePathTwo.getText() );
        theOpenPair.sExcludeFileTypes.setValue( txtExcludeFileTypes.getText() );
        theOpenPair.bExcludeFileTypes.setValue( chkExcludeFileTypes.isSelected() );
        theOpenPair.bSubFolders.setValue( chkIncludeSubfolders.isSelected() );
        theOpenPair.bVerifyCopied.setValue( chkVerifyCopied.isSelected() );
        theOpenPair.bVerifyNotCopied.setValue( chkVerifyNotCopied.isSelected() );
        theOpenPair.bRecoverVerifyFailure.setValue( chkRecoverVerifyFailure.isSelected() );
        theOpenPair.bOverrideReadOnly.setValue( chkOverrideReadOnly.isSelected() );

        setStatus("Pair Updated");
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

    /*
        Recursively look through the source and find all the files
     */

    private void GetTreeChildren( TreeItem<SyncFileOperation> treeNode ) {
        /*
            Get the children of this node, then for each that is a folder,
            this again to get their children. Expand the whole tree.
         */
        SyncFileOperation sfo = treeNode.getValue();
        String sPath = sfo.getFullPath();
        printSysOut("GetTreeChildren : " + sPath);

        String[] saIncludeEverything = new String[]{"*.*", "*"};

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(saIncludeEverything);
        scanner.setCaseSensitive(false);
        scanner.setBasedir(new File(sPath));
        scanner.scan();

       /*
            Get a list of the source files we found
        */
        String[] sSourceFiles = scanner.getIncludedFiles();
        printSysOut("GetTreeChildren Scanner Found : " + String.valueOf(sSourceFiles.length));

        for (String sSourceFile : sSourceFiles) {
            String sDeeperPath = sPath + File.separator + sSourceFile;

            File aFile = new File(sDeeperPath);
            Path pDeeperPath = new File(sDeeperPath).toPath();

            printSysOut("GetTreeChildren Add File : " + sDeeperPath);
            SyncFileOperation sfoDeeper = new SyncFileOperation(pDeeperPath);
            TreeItem<SyncFileOperation> deepNode = new TreeItem<>(sfoDeeper);
            longTotalBytes += sfoDeeper.getIntSize();
            intOperations++;

            treeNode.getChildren().add(deepNode);

        }
        /*
            Look in subfolders?
         */
        if ( !chkIncludeSubfolders.isSelected() ) {
            /*
                Nope, we are done
             */
            return;
        }
        /*
            Scan all the dirs
         */
        String[] sSourceDirs = scanner.getIncludedDirectories();
        for (String sSourceDir : sSourceDirs) {
            String sDeeperPath = sPath + File.separator + sSourceDir;
            Path pDeeperPath = new File(sDeeperPath).toPath();

            printSysOut("GetTreeChildren Add Directory : " + sDeeperPath);
            SyncFileOperation sfoDeeper = new SyncFileOperation(pDeeperPath);

            TreeItem<SyncFileOperation> deepNode = new TreeItem<>(sfoDeeper);
            deepNode.setExpanded(true);

            treeNode.getChildren().add(deepNode);
            treeNode.setExpanded(true);
            /*
                A directory adds no bytes, but it does add an operation to
                create it, maybe.
             */
            intOperations++;

            GetTreeChildren(deepNode);
        }
    }




    public void OnPairOneToTwo(ActionEvent actionEvent) {


        printSysOut("OnPairOneToTwo");

        longTotalBytes = 0L;
        intOperations = 0;


        /*
            Load up the tree starting with the path in the PathOne
         */
        File aFile = new File( txtFilePathOne.getText() );
        Path pathPathOne = aFile.toPath();

        if ( !aFile.exists() ) {
            // nowhere....
        }

        SyncFileOperation root = new SyncFileOperation(pathPathOne);
        TreeItem<SyncFileOperation> treeNode = new TreeItem<> (root);
        treeNode.setExpanded(true);

        tvFileTree.setRoot( treeNode );
        longTotalBytes += root.getIntSize();
        intOperations++;
        /*
            If the root is a folder, then scan it all the way down
         */
        if ( root.isDirectory() ) {
            GetTreeChildren( treeNode );
        }
        /*
            Put a readable size in the display
         */
        SetTotalBytes();

    }

    public void OnPairTwoToOne(ActionEvent actionEvent) {
        printSysOut("OnPairTwoToOne");
    }

    public void OnSavePairs(ActionEvent actionEvent) {
        SavePairsList();
        setStatus("Pairs Saved");
    }


    public void OnClickFilePathOne(ActionEvent actionEvent) {
        txtFilePathOne.setText("D:\\zzSSATest\\BunchOfImages");

    }


    public void OnClickFilePathTwo(ActionEvent actionEvent) {
        txtFilePathTwo.setText("D:\\zzSSATest\\SourceTest\\Stuff Here\\Stuff_2024_12_01_11_06_41_327.png");

    }

    public void OnMenuSavePairs(ActionEvent actionEvent) {
        OnSavePairs( actionEvent );
    }

    public void OnMenuRemoveAllPairs(ActionEvent actionEvent) {

               /*
            Confirm the user wants to do this
         */
        setStatus("Confirm or Cancel");
        Alert cnfrmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        Window wParent = tvPairTable.getScene().getWindow();
        cnfrmAlert.initOwner( wParent);
        cnfrmAlert.setTitle("Confirm Remove All Pairs?");
        cnfrmAlert.setHeaderText( "Confirm Remove All Pairs");
        cnfrmAlert.setContentText("Remove All Pairs cannot be UnDone" );
        Optional<ButtonType> result = cnfrmAlert.showAndWait();
        if ( result.isEmpty() || result.get() != ButtonType.OK ) {
            setStatus( "Remove All Pairs canceled");
            return;
        }
        /*
            Remove ALL THE PAIRS...
         */
        pairObservableList.clear();

        setStatus("All Pairs Removed");
    }

    public void OnMenuCloseApp(ActionEvent actionEvent) {
        OnCloseButton(actionEvent );
    }

    public void OnMakeTestPairs(ActionEvent actionEvent) {

        /*
            Make some test pairs
         */
        SomeTestPairData();
        setStatus("Test pairs made");
    }


}