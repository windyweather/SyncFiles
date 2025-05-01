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
    static public final String SFO_COPY = "COPY";
    static public final String SFO_COPY_VERIFY = "COPY/VERIFY";
    static public final String SFO_VERIFY_NOCOPY = "VERIFY ONLY";
    static public final String SFO_NONE = "NONE";
    /*
        Status Constants
     */
    static public final String SFO_PEND = "PENDING";
    static public final String SFO_COPIED = "COPIED";
    static public final String SFO_COPYVERIFY = "COPIED/VERIFIED";
    static public final String SFO_VERFIED = "VERIFIED";
    static public final String SFO_RECOVERED = "RECOVERED";
    static public final String SFO_DONE = "DONE";

    public String sName;
    public String sSourcePath;
    public String sDestinationPath;
    public long fileSize;
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
    public long getFileSize() { return fileSize; }
    public String getSOperation() { return sOperation; }
    public String getStatus() { return Status;}

    public SyncFileOperation(Path file, String sSourceBase, String sDestPath ) {

        //SetUpImages();
        //super(file.toString());
        this.fullPath = file.toString();
        sSourcePath = fullPath;

        String sSourceTail = sSourcePath.substring( sSourceBase.length() );

        sDestinationPath = sDestPath + sSourceTail;


        sName = String.valueOf(file.getFileName());

        //printSysOut(String.format("SynchFileOperation : %s -> %s ", sSourcePath, sDestinationPath));
        //printSysOut("SyncFileOperation - :"+ sName+ ":"+sSourcePath);

        //test if this is a directory and set the icon

        if (Files.isDirectory(file)) {
            this.isDirectory = true;
            fileSize = 0;
            if ( bImagesGood ) {
                //super.setGraphic(new ImageView(folderExpandImage));
                printSysOut("SyncFileOperation - folder Graphic set");
            }
        } else {
            this.isDirectory = false;
            if ( bImagesGood ) {
                //super.setGraphic(new ImageView(fileImage));
                printSysOut("SyncFileOperation - file Graphic set");
            }

        }

        /*
            If the destination file exists, and has a modification date the same or later than
            the source file, then leave it alone. sOperation == SFO_NONE
            Later we will fix with Verify stuff
         */
        fileSize = 0;
        File fDestFile = new File( sDestinationPath);
        File fSource = new File (file.toString() );
        //printSysOut(String.format("SyncFileOperation - %s -> %s", fSource.getAbsoluteFile().toString(), fDestFile.getAbsoluteFile().toString() ));
        if ( fDestFile.isFile() && fDestFile.exists() ) {
            long srcLastModified = fSource.lastModified();
            long dstLastModified = fDestFile.lastModified();
            if ( dstLastModified >= srcLastModified ) {
                sOperation = SFO_NONE;
                Status = SFO_DONE;
            } else {
                sOperation = SFO_COPY;
                Status = SFO_PEND;
            }

        } else {
            sOperation = SFO_COPY;
            Status = SFO_PEND;
        }

    if (sOperation.equals(SFO_COPY) && !this.isDirectory ) {
        fileSize = fSource.length();
    }
    //printSysOut("SyncFileOperation "+sOperation+" "+Status);
    } // end of constructor
} // end of SyncFileOperation class
