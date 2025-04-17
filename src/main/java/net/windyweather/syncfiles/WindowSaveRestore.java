package net.windyweather.syncfiles;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.beans.DefaultPersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static net.windyweather.syncfiles.SyncFilesController.printSysOut;


/*
    The class that does the work
 */
public class WindowSaveRestore {
    static public SimpleStringProperty organizationName = new SimpleStringProperty("");
    static public SimpleStringProperty userDirectory = new SimpleStringProperty("");
    static public SimpleStringProperty appName = new SimpleStringProperty("");
    static public SimpleStringProperty sXMLWindowPath = new SimpleStringProperty("");


    /*
        Save the Window shape. The path is saved from the Restore call
        so we don't need to do that again.
     */
    static void SaveWindowPosSize(Stage stage) {
        XMLEncoder encoder = null;
        /*
            Just save WindowPosSize after we load it up from the stage
         */
        printSysOut("SaveWindowPosSize - setup WinThing");
        WindowPosSize winThing = new WindowPosSize();
        winThing.setdPosX( new SimpleDoubleProperty(stage.getX() ) );
        winThing.setdPosY( new SimpleDoubleProperty(stage.getY() ) );
        winThing.setdSizeX( new SimpleDoubleProperty(stage.getWidth() ) );
        winThing.setdSizeY( new SimpleDoubleProperty(stage.getHeight() ) );
        printSysOut("SaveWindowPosSize - create Encoder");
        try{
            encoder=new XMLEncoder(new BufferedOutputStream(new FileOutputStream(sXMLWindowPath.get())));
            if ( false ) {
                encoder.setPersistenceDelegate(WindowSaveRestore.class,
                        new DefaultPersistenceDelegate(new String[]{"dPosX", "dPosY", "dSizeX", "dSizeY"}));
            }

        }catch(Exception exception){
            printSysOut( String.format("SaveWindowPosSize: Error Creating or Opening the xml file %s", sXMLWindowPath.get() ) );
            return;
        }
        //WindowPosSize[] wpsary = new WindowPosSize[] {winThing};
        printSysOut("SaveWindowPosSize - call encoder.WriteObject");
        encoder.writeObject(winThing);

        printSysOut( String.format("SaveWindowPosSize: Window pos [%.0f,%.0f] to %s",
                winThing.dPosX.get(), winThing.dPosY.get(),  sXMLWindowPath.get() ) );
        encoder.close();
    }

     /*
        Restore the window shape from the XML file
      */
    static void RestoreWindowPosSize( Stage stage, String organ, String sappName ) {
        /*
            Keep these for save later.
         */
        organizationName.setValue( organ );
        String currentUsersHomeDir = System.getProperty("user.home");
        userDirectory.setValue( currentUsersHomeDir );
        appName.setValue( sappName );

        sXMLWindowPath = new SimpleStringProperty(currentUsersHomeDir + File.separator + "."+organ
                                            + File.separator + sappName+"Window.xml");

        // Use XMLDecoder to read the XML file in.
        try {
            printSysOut("RestoreWindowPosSize");
            final XMLDecoder decoder = new XMLDecoder(new FileInputStream(sXMLWindowPath.get()));
            WindowPosSize winThing = (WindowPosSize) decoder.readObject();
            decoder.close();
            printSysOut(String.format("Window Pos/Size [%.0f,%.0f] [%.0f,%.0f]", winThing.dPosX.getValue(), winThing.dPosY.getValue(),
                    winThing.dSizeX.getValue(), winThing.dSizeY.getValue()));

            /*
                Restore the actual window position and size
             */
            stage.setX(winThing.dPosX.getValue());
            stage.setY(winThing.dPosY.getValue());
            stage.setWidth(winThing.dSizeX.getValue());
            stage.setHeight(winThing.dSizeY.getValue());

        } catch (Exception e) {
            printSysOut(String.format("Window Pos/Size Not Restored %s e: %s", sXMLWindowPath.get(), e.toString() ));
        }

    }

}
