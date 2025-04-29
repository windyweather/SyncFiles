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

    /*
        This constructor never used
     */
    public SyncFileOperation() {
        //SetUpImages();
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

    public String sName;
    public String sSourcePath;
    public String sDestinationPath;
    public int intSize;
    public String sOperation; //
    public String Status;


    public static boolean bImagesGood = false;
    public static boolean bImagesTried = false;
    public static Image folderCollapseImage;
    public static Image folderExpandImage;
    public static Image fileImage;


    /*
        Read in the images for the tree
     */
    private void SetUpImages() {

        if ( !bImagesGood && bImagesTried ) {
            return;
        }
        bImagesTried = true;
        try {

            folderExpandImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("File_Folder-open.svg")) );
            folderExpandImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("File_Folder-open.svg")));
            fileImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("File_Text-x-generic.svg")));

            printSysOut("Tree Images Found");
            bImagesGood = true;
        } catch (Exception e) {
            printSysOut("Tree Images Failed");
        }
    }

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

        //SetUpImages();
        //super(file.toString());
        this.fullPath = file.toString();
        sSourcePath = fullPath;
        sName = String.valueOf(file.getFileName());
        //printSysOut("SyncFileOperation - :"+ sName+ ":"+sSourcePath);

        sDestinationPath = "None here yet";

        //test if this is a directory and set the icon

        if (Files.isDirectory(file)) {
            this.isDirectory = true;
            intSize = 0;
            if ( bImagesGood ) {
                //super.setGraphic(new ImageView(folderExpandImage));
                printSysOut("SyncFileOperation - folder Graphic set");
            }
        } else {
            this.isDirectory = false;
            File aFUckingFile = new File(file.toString());
            intSize = (int)aFUckingFile.length();
            if ( bImagesGood ) {
                //super.setGraphic(new ImageView(fileImage));
                printSysOut("SyncFileOperation - file Graphic set");
            }

        }

        sOperation = SFO_COPY;
        Status = SFO_PEND;



    } // end of constructor
} // end of SyncFileOperation class
