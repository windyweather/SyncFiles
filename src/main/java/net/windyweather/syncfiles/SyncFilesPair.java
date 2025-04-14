package net.windyweather.syncfiles;

/*
    The Pair stores all the information about the source and destination folder tree paths
     and the options that control copying files between the folder trees.
 */

public class SyncFilesPair {

    String sPairName = null;
    String sStatus = null;
    String sFilePathOne = null;
    String sFilePathTwo = null;
    String sExcludeFileTypes = null;
    boolean bSubfolders = false;
    boolean bVerifyCopied = false;
    boolean bVerifyNotCopied = false;
    boolean bRecoverVerifyFailure = false;
    boolean bOverrideReadOnly = false;


    public SyncFilesPair() {

    }

    public SyncFilesPair( String spairname ) {
        sPairName = spairname;
    }
}
