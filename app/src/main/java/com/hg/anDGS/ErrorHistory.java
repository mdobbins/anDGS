package com.hg.anDGS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Michael on 9/8/2017.
 */

public class ErrorHistory {
    private static ErrorHistory instance = new ErrorHistory();
    private final Boolean lockIt = true;
    private CommonFileStuff commonFileStuff = new CommonFileStuff();
    private final int maxErrLen = 159;

    static ErrorHistory getInstance() {
        return instance;
    }

    private ErrorHistory() { }

    private static String [] errorHistoryData = new String [0];
    private static boolean errorHistoryLoaded = false;
    private static boolean errorHistoryChanged = false;

    void checkLoadErrorHistoryData() {
        if (!isErrorHistoryDataLoaded()) {
            loadErrorHistoryData();
        }
    }

    void writeErrorHistory(String txt) {
        checkLoadErrorHistoryData();
        pushNewErrorHistoryData(stripNL(txt));
        checkSaveErrorHistoryData();
    }

    private void loadErrorHistoryData() {
        synchronized(lockIt) {
            String stmv = readErrorHistoryData();
            String [] stmvLines;
            stmvLines = stmv.split("\n");
            int numLines = stmvLines.length;
            if (errorHistoryData.length<numLines) {
                errorHistoryData = new String[(numLines + 10)];
            }
            for (int i = 0; i< errorHistoryData.length; i++) {
                if (i<numLines) {
                    errorHistoryData[i] = stmvLines[i];
                } else {
                    clearErrorHistoryDataInx(i);
                }
            }
            errorHistoryLoaded = true;
            errorHistoryChanged = false;
        }
    }

    private boolean isErrorHistoryDataLoaded() {
        synchronized(lockIt) {
            return errorHistoryLoaded;
        }
    }
/*
    String [] getAllErrorHistoryData () {
        synchronized(lockIt) {
            return getTheErrorHistoryData();
        }
    }
*/
    String [] getCompressAllErrorHistoryData () {
        synchronized(lockIt) {
            String [] newErrorHistoryData = getTheErrorHistoryData();
            if (newErrorHistoryData.length>0) {
                for (int i = 0; i < errorHistoryData.length; i++) {
                    if (i < newErrorHistoryData.length) {
                        errorHistoryData[i] = newErrorHistoryData[i];
                    } else {
                        clearErrorHistoryDataInx(i);
                    }
                }
            }
            return newErrorHistoryData;
        }
    }


    String getErrorHistoryDataInx (int inx) {
        synchronized(lockIt) {
            if (errorHistoryData!=null) {
                if (inx >= 0 && inx < errorHistoryData.length) {
                    if (errorHistoryData[inx] != null) {
                        return errorHistoryData[inx];
                    }
                }
            }
            return "";
        }
    }

    void checkSaveErrorHistoryData() {
        if (isErrorHistoryDataLoaded()) {
            synchronized (lockIt) {
                if (errorHistoryChanged) {
                    writeErrorHistoryData(getTheErrorHistoryData());
                    errorHistoryChanged = false;
                }
            }
        }
    }

    private void pushNewErrorHistoryData(String dataIn) {
        int len = maxErrLen;
        if (dataIn.length() < len) len = dataIn.length();
        String data = System.currentTimeMillis() + "!" + dataIn.substring(0,len);
        // move old down 1
        int inx = -1;
        int i = 0;
        boolean foundIt = false;
        while (i< errorHistoryData.length && !foundIt) {
            if (errorHistoryData[i] == null) {  // Dont know how we skipped one.
                errorHistoryData[i] = "";
            }
            if (errorHistoryData[i].contentEquals("")) {
                foundIt = true;
                inx = i;
            }
            i++;
        }
        String [] ehData = errorHistoryData;
        if (inx < 0 ) {   // no rempty space
            if (errorHistoryData.length < commonFileStuff.numberErrorHistoryEntries) {  //  expand table
                errorHistoryData = new String [commonFileStuff.numberErrorHistoryEntries];
                errorHistoryData[0] = data;
                for (i=0; i< (errorHistoryData.length-1); i++) {
                    if (i<ehData.length) {
                        errorHistoryData[i+1] = ehData[i];
                    } else {
                        errorHistoryData[i+1] = "";
                    }
                }
            } else {  //pushoff the end
                errorHistoryData = new String [(ehData.length)];
                errorHistoryData[0] = data;
                for (i=0; i< (errorHistoryData.length-1); i++) {
                    errorHistoryData[i+1] = ehData[i];
                }
            }
        } else {  // push to empty space
            errorHistoryData = new String [(ehData.length)];
            errorHistoryData[0] = data;
            for (i=0; i< (errorHistoryData.length-1); i++) {
                if (i<inx) {
                    errorHistoryData[i+1] = ehData[i];
                } else if (i>inx) {
                    errorHistoryData[i] = ehData[i];
                }
            }
        }
    }

    private String stripNL(String data) {
        return data.replace('\n', (char) 135);
    }

    // internal methods

    private String readErrorHistoryData () {
        String fName = commonFileStuff.getFullFileName(commonFileStuff.ANDGS_DIR, commonFileStuff.HDFILENAME);
        String fdata = "";
        InputStream in;
        File f = new File(fName);
        try {
            in = new FileInputStream(f);
            TextHelper th = new TextHelper();
            fdata = th.GetText(in);
        } catch (FileNotFoundException ignore) { }
        return fdata;
    }

    private void writeErrorHistoryData (String [] strehd) {
        try {
            String fName = commonFileStuff.getFullFileName(commonFileStuff.ANDGS_DIR, commonFileStuff.HDFILENAME);
            File aFile = new File(fName);
            File dir = aFile.getParentFile();
            if (dir != null && !dir.isDirectory()) commonFileStuff.makeDirectory(dir);
            FileOutputStream fos = new FileOutputStream(aFile);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            try {
                for (String s : strehd) {
                    bw.write(s);
                    bw.newLine();
                }
            } catch (IOException e) {
                try {
                    bw.close();
                } catch (IOException ignored) { }
                return;
            }
            try {
                bw.flush();
                bw.close();
            } catch (IOException ignore) { }
        } catch (FileNotFoundException ignore) { }
    }

    private String [] getTheErrorHistoryData () {
        int cntLines = 0;
        // cleanup and count entries
        if (errorHistoryData.length>0) {
            for (int i = 0; i < errorHistoryData.length; i++) {
                if (errorHistoryData[i]==null) {
                    clearErrorHistoryDataInx(i);
                }
                if (!errorHistoryData[i].equals("")) {
                    cntLines++;
                }
            }
        }
        String [] errorHistoryList = new String [cntLines];
        int inx = 0;
        if (errorHistoryData.length>0) {
            for (String errorHistoryDatum : errorHistoryData) {
                if (!errorHistoryDatum.equals("")) {
                    if (inx >= errorHistoryList.length) {
                        break;
                    } else {
                        errorHistoryList[inx] = errorHistoryDatum;
                    }
                    inx++;
                }
            }
        }
        return errorHistoryList;
    }

    void clearErrorHistoryDataInx(int inx) {
        if (inx>-1 && inx< errorHistoryData.length) {
            errorHistoryChanged = true;
            errorHistoryData[inx] = "";
        }
    }

    void clearErrorHistoryDataAll() {
        for (int i = 0; i < errorHistoryData.length; i++) {
            errorHistoryChanged = true;
            errorHistoryData[i] = "";
        }
    }

    int errorHistoryDataLength () {
        return errorHistoryData.length;
    }

}
