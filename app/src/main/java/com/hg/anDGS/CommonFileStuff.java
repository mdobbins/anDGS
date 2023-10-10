package com.hg.anDGS;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

class CommonFileStuff {

    final String ANDGS_DIR = "anDGS";
    final String RECOVERYFILE = "recovery";
    final String SMFILENAME = "predictedmoves";
	final String HDFILENAME = "errorhistory";
	final String PHFILENAME = "phrases";
	final int numberErrorHistoryEntries = 54;
	static File envDir;
	static File sgfPath;
	static File envDir28;
	final boolean oldStructure = true;
	final boolean newStructure = false;
	Context tContext;

	private void setupExtDirs(Context context) {
		// final File  envDir1 = new File(MainDGS.ctw.getExternalFilesDir(null).getAbsolutePath());
		// final File  envDir1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		// final File  envDir2 = new File(Environment.getDataDirectory().getAbsolutePath());
		// final File  envDir3 = new File(Environment.getRootDirectory().getAbsolutePath());
		try {
			envDir = new File(context.getExternalFilesDir(null).getAbsolutePath());
		} catch (Exception exception) {
			envDir = new File("/");
		}

		try {
			envDir28 = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		} catch (Exception exception) {
			envDir28 = new File("/");
		}
	}

	String getExtDirName () {
		String name;
		try {
			name = envDir.getAbsolutePath();
		} catch (Exception e) {
			name = "/bad";
			e.printStackTrace();
		}
		return name;
	}

	String getSgfDirName () {
		String name;
		try {
			name = sgfPath.getAbsolutePath();
		} catch (Exception e) {
			name = "/bad";
			e.printStackTrace();
		}
		return name;
	}

	boolean isRecoveryFile() {
		String dirName = getFullDirName(ANDGS_DIR, newStructure);
		File dirFile = new File(dirName);
		if (!dirFile.isDirectory()) {
			makeDirectory(dirFile);
		}
		String filName = 	getFullFileName(ANDGS_DIR, RECOVERYFILE);
		File recovery = new File(filName);
		return recovery.isFile();
	}

	boolean isDirectorySetup(Context context, String savedSgfDir) {
		tContext = context;
		setupExtDirs(context);
		boolean rtnVal = false;
		File oldPath = new File(envDir28, ANDGS_DIR);
		File newPath = new File(envDir, ANDGS_DIR);
		File oldSgfPath = getFullDirFile(savedSgfDir, oldStructure);
		String oldSgfName = oldSgfPath.getName();
		if (oldSgfName == null || oldSgfName.contentEquals("") || oldSgfName.contentEquals("/")) {
			oldSgfName = "sgfs";
		}
		sgfPath = new File(envDir, oldSgfName);
		if (sgfPath.getAbsolutePath().contentEquals(oldSgfPath.getAbsolutePath())) {
			// we migrated before and are done
			return true;
		}
		if (oldSgfPath.isDirectory()) {
			// move from the sgfs directory to the andgs directory
			if (!oldPath.isDirectory()) {
				if (makeDirectory(oldPath)) {
					if (oldPath.canWrite()) {
						migrateFile(RECOVERYFILE, savedSgfDir, newPath.getAbsolutePath());
						migrateFile(SMFILENAME, savedSgfDir, newPath.getAbsolutePath());
						rtnVal = true;
					}
				}
			}
		}
		// try to migrate from pre 20 to post 28 file structure
		if (oldPath.isDirectory()) {
			if (newPath.isDirectory()) {
			// new directory is there from before, we are done
				rtnVal = newPath.canWrite();
			} else
			if (envDir.canWrite()) {
				moveFile (oldPath.getAbsolutePath(), envDir.getAbsolutePath());
				moveFile (oldSgfPath.getAbsolutePath(), envDir.getAbsolutePath());
				rtnVal = true;
			}
		}
		if (!envDir.isDirectory()) {
			if (makeDirectory(envDir)) {
				rtnVal = envDir.canWrite();
			}
		} else {
			rtnVal = true;
		}
		if (!sgfPath.isDirectory()) {
			if (makeDirectory(sgfPath)) {
				rtnVal = sgfPath.canWrite();
			}
		} else {
			rtnVal = true;
		}

		return rtnVal;
	}

	private void migrateFile(String theFile, String oldDir, String newDir) {
		File dirOldFile = getFullDirFile(oldDir, oldStructure);
		if (!dirOldFile.isDirectory()) {
			return; // no old directory
		}
		File filOldFile = getFullFile(oldDir, theFile, oldStructure);
		if (filOldFile.isFile()) {
			File dirNewFile = new File(newDir);
			if (!dirNewFile.isDirectory()) {
				if (!makeDirectory(dirNewFile)) {
					return;
				}
			}
			File filNewFile = getFullFile(newDir, theFile, newStructure);
			if (!filNewFile.isFile()) { // doesn't already exist
				moveFile(filOldFile.getAbsolutePath(), filNewFile.getAbsolutePath());
			}
		}
	}

    void moveFile (String filOldName, String filNewName) {
        try {
            String[] command = {"mv", filOldName, filNewName};
            @SuppressWarnings("unused")
            Process process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
			Toast.makeText(tContext, "moveFile exception: " + e.toString(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
    }
	
	boolean makeDirectory (String dirName) {
		File f = new File(dirName);
		return makeDirectory(f);
	}

	boolean makeDirectory (File f) {
		try {
			if (!f.exists()) {
				//Toast.makeText(MainDGS.ctw, "No " + f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
				//Toast.makeText(MainDGS.ctw, "Yes " + f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
				return f.mkdirs();
			} else {
				//Toast.makeText(MainDGS.ctw, "Exist " + f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
				return true;
			}
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	private String getFullDirName(String theDir, boolean fileStructure) {
		File path = getFullDirFile(theDir, fileStructure);
		return path.getAbsolutePath();
	}

	File getFullDirFile(String theDir) {
		return getFullDirFile(theDir, newStructure);
	}

	private File getFullDirFile(String theDir, boolean fileStructure) {
		File path;
		if (theDir.startsWith("/")) {
			path = new File(theDir);
		} else {
			if (fileStructure) {  // == oldStructure
				path = new File(envDir28, theDir);
			} else {
				path = new File(envDir, theDir);
			}
		}
		if(!path.isDirectory()) {
			if (!makeDirectory(path.getAbsolutePath())) {
				path = new File("/");
			}
		}
		return path;
	}

	String getFullFileName(String theDir, String fileName) {
		return getFullFileName(theDir, fileName, newStructure);
	}
	
	private String getFullFileName(String theDir, String fileName, boolean fileStructure) {
		return getFullFile(theDir,fileName, fileStructure).getAbsolutePath();
	}

	File  getFullFile(String theDir, String fileName) {
		return getFullFile(theDir, fileName, newStructure);
	}

	private File  getFullFile(String theDir, String fileName, boolean fileStructure) {
		return new File(getFullDirFile(theDir, fileStructure),fileName);
	}

	String readPhrasesData() {
		String fName = getFullFileName(ANDGS_DIR, PHFILENAME);
		File f = new File(fName);
		File dir = f.getParentFile();
		if (dir != null && !dir.isDirectory()) makeDirectory(dir);
		String fdata = "."; // initially a phrase with only a period
		InputStream in;
		try {
			in = new FileInputStream(f);
			TextHelper th = new TextHelper();
			fdata = th.GetText(in);
		} catch (FileNotFoundException ignore) { }
		return fdata;
	}

	void writePhrasesData(String phrasedat) {
		try {
			String fName = getFullFileName(ANDGS_DIR, PHFILENAME);
			File f = new File(fName);
			File dir = f.getParentFile();
			if (dir != null && !dir.isDirectory()) makeDirectory(dir);
			FileOutputStream fos = new FileOutputStream(f);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			try {
				bw.write(phrasedat);
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

}
