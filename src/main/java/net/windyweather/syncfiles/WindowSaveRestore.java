package net.windyweather.syncfiles;

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

public class WindowSaveRestore {
    static SimpleStringProperty organizationName = new SimpleStringProperty("");
    static SimpleStringProperty userDirectory = new SimpleStringProperty("");
    static SimpleStringProperty appName = new SimpleStringProperty("");
    static SimpleStringProperty sXMLWindowPath = new SimpleStringProperty("");

    SimpleDoubleProperty dPosX;
    SimpleDoubleProperty dPosY;
    SimpleDoubleProperty dSizeX;
    SimpleDoubleProperty dSizeY;

    WindowSaveRestore() {
        dPosX = new SimpleDoubleProperty( 0);
        dPosY = new SimpleDoubleProperty( 0);
        dSizeX = new SimpleDoubleProperty( 0);
        dSizeY = new SimpleDoubleProperty( 0);
    }

    public SimpleDoubleProperty dPosXProperty() { return dPosX; }
    public SimpleDoubleProperty dPosYProperty() { return dPosY; }
    public SimpleDoubleProperty dSizeXProperty() { return dSizeX; }
    public SimpleDoubleProperty dSizeYProperty() { return dSizeY; }

    public void setdPosX( SimpleDoubleProperty x ) {
        this.dPosX = x;
    }

    public void setdPosY( SimpleDoubleProperty y ) {
        this.dPosY = y;
    }

    public void setdSizeX( SimpleDoubleProperty width ) {
        this.dSizeX = width;
    }

    public void setdSizey( SimpleDoubleProperty height ) {
        this.dSizeY = height;
    }




    /*
        Save the Window shape. The path is saved from the Restore call
        so we don't need to do that again.
     */
    static void SaveWindowPosSize(Stage stage) {
        XMLEncoder encoder = null;
        /*
            Just save ourselves, after we load up the stage [window] values
            here. Woops. Nope Gotta make a copy since data is not static.
         */
        WindowSaveRestore winThing = new WindowSaveRestore();
        winThing.dPosX.setValue( stage.getX() );
        winThing.dPosY.setValue( stage.getY() );
        winThing.dSizeX.setValue( stage.getWidth() );
        winThing.dSizeY.setValue( stage.getHeight() );

        try{
            encoder=new XMLEncoder(new BufferedOutputStream(new FileOutputStream(sXMLWindowPath.get())));
            encoder.setPersistenceDelegate( WindowSaveRestore.class,
                    new DefaultPersistenceDelegate( new String[]{"dPosX", "dPosY", "dSizeX", "dSizeY" } ) );

        }catch(Exception exception){
            printSysOut( String.format("SaveWindowPosSize: Error Creating or Opening the xml file %s", sXMLWindowPath.get() ) );
            return;
        }

        encoder.writeObject(winThing);

        printSysOut( String.format("SaveWindowPosSize: Window pos [%.0f,%.0f] to %s",
                winThing.dPosX.get(), winThing.dPosY.get(),  sXMLWindowPath.get() ) );
        encoder.close();
    }

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
            WindowSaveRestore winStuff = (WindowSaveRestore) decoder.readObject();
            decoder.close();
            printSysOut(String.format("Window Pos/Size [%.0f,%.0f] [%.0f,%.0f]", winStuff.dPosX.getValue(), winStuff.dPosY.getValue(),
                    winStuff.dSizeX.getValue(), winStuff.dSizeY.getValue()));

            /*
                Restore the actual window position and size
             */
            stage.setX(winStuff.dPosX.getValue());
            stage.setY(winStuff.dPosY.getValue());
            stage.setWidth(winStuff.dSizeX.getValue());
            stage.setHeight(winStuff.dSizeY.getValue());

        } catch (Exception e) {
            printSysOut(String.format("Window Pos/Size Not Restored %s", sXMLWindowPath.get() ));
        }

    }

}
