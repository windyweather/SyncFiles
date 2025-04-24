package net.windyweather.syncfiles;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

/*
    Encodes a file operation between a source and destination file path.
    The only file operations we do here in Sync are to COPY a file.
    The older programs did Delete, Find Duplicates etc. We leave those for
    the normal file explorer. Experience indicates that the other features are
    used so infrequently that it's not worth coding them.
    Woops. I forgot verify copy, and verify not copied.
 */
public class SyncFileOperation extends TreeItem<String> {
    public SyncFileOperation() {

    }

    /*
        Operation constants
     */
    static final String SFO_COPY = "COPY";
    static final String SFO_COPY_VERIFY = "COPY/VERIFY";
    static final String SFO_VERIFY_NOCOPY = "VERIFY ONLY";
    /*
        Status Constants
     */
    static final String SFO_PEND = "PENDING";
    static final String SFO_COPIED = "COPIED";
    static final String SFO_COPYVERIFY = "COPIED/VERIFIED";
    static final String SFO_VERFIED = "VERIFIED";
    static final String SFO_RECOVERED = "RECOVERED";

    String sName;
    String sSourcePath;
    String sDestinationPath;
    int intSize;
    String sOperation; //
    String Status;

    /*
    public static Image folderCollapseImage = new Image(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("net.windyweather.synchtestgui/File_Folder.svg")));
    public static Image folderExpandImage = new Image(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("net.windyweather.synchtestgui/File_Folder-open.svg")));
    public static Image fileImage = new Image(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("net.windyweather.synchtestgui/File_Text-x-generic.svg")));
    */

    private void printSysOut( String str ) {
        System.out.println( str );
    }

    //this stores the full path to the file or directory
    private String fullPath;

    public String getFullPath() {
        return (this.fullPath);
    }

    private boolean isDirectory;

    public boolean isDirectory() {
        return (this.isDirectory);
    }

    public String getSName() { return sName; }
    public String getSSourcePath () { return sSourcePath; }
    public String getSDestinationPath () { return sDestinationPath; }
    public int getIntSize() { return intSize; }
    public String getSOperation() { return sOperation; }
    public String getStatus() { return Status;}

    public SyncFileOperation(Path file) {

        //super(file.toString());
        this.fullPath = file.toString();
        sSourcePath = fullPath;
        sName = String.valueOf(file.getFileName());
        printSysOut("SyncFileOperation - :"+ sName+ ":"+sSourcePath);

        sDestinationPath = "None here yet";

        //test if this is a directory and set the icon

        if (Files.isDirectory(file)) {
            this.isDirectory = true;
            intSize = 0;
            //this.setGraphic(new ImageView(folderCollapseImage));
        } else {
            this.isDirectory = false;
            File aFUckingFile = new File(file.toString());
            intSize = (int)aFUckingFile.length();
            //this.setGraphic(new ImageView(fileImage));
            //if you want different icons for different file types this is where you'd do it
        }

        sOperation = SFO_COPY;
        Status = SFO_PEND;



    } // end of constructor
} // end of SyncFileOperation class
