package com.hg.anDGS;

import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

class CommonFileStuff {

    final String ANDGS_DIR = "anDGS";
    final String RECOVERYFILE = "recovery";
    final String SMFILENAME = "predictedmoves";
	final String HDFILENAME = "errorhistory";
	final int numberErrorHistoryEntries = 54;
	final File  envDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

	// final File  envDir1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
	// final File  envDir2 = new File(Environment.getDataDirectory().getAbsolutePath());
	// final File  envDir3 = new File(Environment.getRootDirectory().getAbsolutePath());

	boolean isDirectory (String aDirName) {
		File path = new File(envDir, aDirName);
		return path.isDirectory();
	}

    boolean isRecoveryFile() {
        String dirName = getFullDirName(ANDGS_DIR);
        File dirFile = new File(dirName);
        if (!dirFile.isDirectory()) {
            makeDirectory(dirFile);
        }
        String filName = 	getFullFileName(ANDGS_DIR, RECOVERYFILE);
        File recovery = new File(filName);
        return recovery.isFile();
    }

	void migrateFile(String theFile, String oldDir, String newDir) {
		File dirOldFile = getFullDirFile(oldDir);
		if (!dirOldFile.isDirectory()) {
			makeDirectory(dirOldFile);
		}
		File filOldFile = getFullFile(oldDir, theFile);
		if (filOldFile.isFile()) {
			String dirNewName = getFullDirName(newDir);
			File dirNewFile = new File(dirNewName);
			if (!dirNewFile.isDirectory()) {
				makeDirectory(dirNewFile);
			}
			File filNewFile = getFullFile(newDir, theFile);
			if (!filNewFile.isFile()) {
				moveFile(filOldFile.getAbsolutePath(), filNewFile.getAbsolutePath());
			}
		}
	}

    void moveFile (String filOldName, String filNewName) {
        try {
            String[] command = {"mv", filOldName, filNewName};
            @SuppressWarnings("unused")
            Process process = Runtime.getRuntime().exec(command);
        } catch (IOException ignore) { }
    }
	
	boolean makeDirectory (String dirName) {
		File f = new File(dirName);
		return makeDirectory(f);
	}

	boolean makeDirectory (File f) {
		try {
			if (!f.exists()) {
				if (!f.mkdirs()) {
					//Toast.makeText(MainDGS.ctw, "No " + f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
					return false;
				} else {
					//Toast.makeText(MainDGS.ctw, "Yes " + f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
					return true;
				}
			} else {
				//Toast.makeText(MainDGS.ctw, "Exist " + f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
				return true;
			}
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	String getFullDirName(String theDir) {
		File path = getFullDirFile(theDir);
		return path.getAbsolutePath();
	}

	File getFullDirFile(String theDir) {
		File path;
		if (theDir.startsWith("/")) {
			path = new File(theDir);
		} else {
			path = new File(envDir, theDir);
		};
		if(!path.isDirectory()) {
			makeDirectory(path.getAbsolutePath());
		}
		return path;
	}
	
	String getFullFileName(String theDir, String fileName) {
		return getFullFile(theDir,fileName).getAbsolutePath();
	}

	File  getFullFile(String theDir, String fileName) {
		return new File(getFullDirFile(theDir),fileName);
	}

}
