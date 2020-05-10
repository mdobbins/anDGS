package com.hg.anDGS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.System;

class StoredMoves {
	private static StoredMoves instance = new StoredMoves();
    private final Boolean lockIt = true;
    private CommonFileStuff commonFileStuff = new CommonFileStuff();
    private ErrorHistory errHist = ErrorHistory.getInstance();

	static StoredMoves getInstance() {
		return instance;
	}

	private StoredMoves() { }

    private static String [] storedMovesData = new String [10];
    private static String [] storedMovesTimeStamp = new String [10];
    private static String [] storedMovesGid = new String [10];
    private static boolean storedMovesLoaded = false;
    private static boolean storedMovesChanged = false;

    void loadStoredMoves() {
        synchronized(lockIt) {
            String stmv = readStoredMoves();
            String [] stmvLines;
            int numLines;
            if (stmv.length()<4) {
                stmvLines = null;
                numLines = 0;
            } else {
                stmvLines = stmv.split("\n");
                numLines = stmvLines.length;
            }
            if (storedMovesGid.length<numLines) {
                storedMovesGid = new String[(numLines + 10)];
                storedMovesTimeStamp = new String[(numLines + 10)];
                storedMovesData = new String[(numLines + 10)];
            }
            for (int i=0; i< storedMovesGid.length; i++) {
                if (i<numLines) {
                    String[] line = stmvLines[i].split("!");
                    if (line.length==3) {
                        if (validTimeStamp(line[1])) {  // not timed out entry
                            storedMovesGid[i] = line[0];
                            storedMovesTimeStamp[i] = line[1];
                            storedMovesData[i] = line[2];
                        } else {
                            clearStoredMovesInx(i);
                        }
                    } else if (line.length==2) {
                        storedMovesGid[i] = line[0];
                        storedMovesTimeStamp[i] = Long.toString(System.currentTimeMillis());
                        storedMovesData[i] = line[1];
                    } else {
                        clearStoredMovesInx(i);
                    }
                } else {
                    clearStoredMovesInx(i);
                }
            }
            storedMovesLoaded = true;
            storedMovesChanged = false;
        }
    }

    boolean areStoredMovesLoaded () {
        synchronized(lockIt) {
            return storedMovesLoaded;
        }
    }

    String [] getAllStoredMoves () {
        synchronized(lockIt) {
            return getTheMoves();
        }
    }

    void checkSaveStoredMoves() {
        synchronized(lockIt) {
            if (storedMovesChanged) {
                writeStoredMoves(getTheMoves());
                storedMovesChanged = false;
            }
        }
    }

    boolean isStoredMove (String gameId, String cMovId, String cClr, String cMov) {
        synchronized(lockIt) {
            int i = findStoredMovesInx(gameId);
            if (i>-1) {
                if (checkStoredMoveInfo(i, cMovId, cClr, cMov)) {
                    return true;
                } else {
                    clearStoredMovesInx(i);
                }
            }
            return false;
        }
    }

    void clearStoredMove (String gameId) {
        synchronized(lockIt) {
            int i = findStoredMovesInx(gameId);
            if (i > -1) {
                clearStoredMovesInx(i);
            }
        }
    }

    void newStoredMoveGame(String movs, String gameId) {
        synchronized(lockIt) {
            int i = findStoredMovesInx(gameId);
            if (i>-1) {
                clearStoredMovesInx(i);
            }
            i = createNewStoredMoveGame();
            if (!movs.contentEquals("")) {
                if (i>-1) {
                    storedMovesChanged = true;
                    storedMovesGid[i] = gameId;
                    storedMovesTimeStamp[i] = Long.toString(System.currentTimeMillis());
                    storedMovesData[i] = movs;
                }
            }
        }
    }

    // internal methods

    private String readStoredMoves () {
        String fName = commonFileStuff.getFullFileName(commonFileStuff.ANDGS_DIR, commonFileStuff.SMFILENAME);
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

    private void writeStoredMoves (String [] strmov) {
        try {
            File aFile = commonFileStuff.getFullFile(commonFileStuff.ANDGS_DIR, commonFileStuff.SMFILENAME);
            File dir = aFile.getParentFile();
            if (dir != null && !dir.isDirectory()) commonFileStuff.makeDirectory(dir);
            FileOutputStream fos = new FileOutputStream(aFile);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            try {
                for (String s : strmov) {
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

    private String [] getTheMoves () {
        int cntLines = 0;
        // cleanup and count entries
        if (storedMovesGid.length>0) {
            for (int i = 0; i < storedMovesGid.length; i++) {
                if (!validTimeStamp(storedMovesTimeStamp[i])) {  // timed out entry
                    clearStoredMovesInx(i);
                }
                if (storedMovesGid[i]==null) {
                    clearStoredMovesInx(i);
                }
                if (!storedMovesGid[i].equals("")) {
                    cntLines++;
                }
            }
        }
        String [] smGamesList = new String [cntLines];
        int inx = 0;
        if (storedMovesGid.length>0) {
            for (int i = 0; i < storedMovesGid.length; i++) {
                if (!storedMovesGid[i].equals("")) {
                    if (inx >= smGamesList.length) {
                        break;
                    } else {
                        smGamesList[inx] = storedMovesGid[i] + "!"
                                + storedMovesTimeStamp[i] + "!"
                                + storedMovesData[i];
                    }
                    inx++;
                }
            }
        }
        return smGamesList;
    }

    private boolean validTimeStamp(String ts) {
        final long second = 1000;
        final long minute = 60*second;
        final long hour = 60*minute;
        final long day = 24*hour;
        final long validTime = 30*day;
        long timeStamp;
        try {
            timeStamp = Long.parseLong(ts, 10);
        } catch (NumberFormatException ex) {
            return false;
        }
        return (System.currentTimeMillis()-timeStamp)<validTime;
    }
	// search for the gameId
	private int findStoredMovesInx(String gameId) {
		int inx = -1;
		int i = 0;
		boolean foundIt = false;
        if (storedMovesGid.length>0) {
            while (i < storedMovesGid.length && !foundIt) {
                if (storedMovesGid[i] == null) {
                    clearStoredMovesInx(i);
                } else if (storedMovesGid[i].contentEquals(gameId)) {
                    foundIt = true;
                    inx = i;
                }
                i++;
            }
        }
		return inx;
	}

	private int createNewStoredMoveGame() {
		int inx = -1;
		int i = 0;
		boolean foundIt = false;
		while (i< storedMovesGid.length && !foundIt) {
			if (storedMovesGid[i].contentEquals("")) {
				foundIt = true;
				inx = i;
			}
			i++;
		}
		if (inx < 0) {  // no space, expand table
			String [] smGid = storedMovesGid;
            String [] smTS = storedMovesTimeStamp;
			String [] smData = storedMovesData;
			storedMovesGid = new String [(smGid.length+10)];
            storedMovesTimeStamp = new String [(smGid.length+10)];
			storedMovesData = new String [(smGid.length+10)];
			inx = smGid.length;  // use the first new space
			for (i=0; i< storedMovesGid.length; i++) {
				if (i<smGid.length) {
					storedMovesGid[i] = smGid[i];
                    storedMovesTimeStamp[i] = smTS[i];
					storedMovesData[i] = smData[i];
				} else {
					storedMovesGid[i] = "";
                    storedMovesTimeStamp[i] = "";
					storedMovesData[i] = "";
				}
			}
		}
		return inx;
	}

	// clear the entry
	private void clearStoredMovesInx(int inx) {
        if (storedMovesGid.length>0) {
            if (inx > -1 && inx < storedMovesGid.length) {
                storedMovesChanged = true;
                storedMovesGid[inx] = "";
                storedMovesTimeStamp[inx] = "";
                storedMovesData[inx] = "";
            }
        }
	}

	// verify  move exists, but don't remove
	private boolean checkStoredMoveInfo(int inx, String cMovID, String cClr, String cMov) {
		String [] movInfo = getStoredMoveInfo(inx);
        if (movInfo != null) {
            if (movInfo.length == 3) {
                if (movInfo[0].contentEquals(cMovID)
                        && movInfo[1].contentEquals(cClr)
                        && (movInfo[2].contentEquals(cMov) || cMov.contentEquals(""))) {
                    return true;
                }
            }
        }
		clearStoredMovesInx(inx);
		return false;
	}

    //  get the first move value
    private String [] getStoredMoveInfo(int inx) {
        String [] e = storedMovesData[inx].split(";");
        if (e.length > 0) {
            return e[0].split(",");
        }
        return null;
    }

    //  remove first move and get and remove the first move response
    private String [] removeStoredMoveInfo(int inx) {
        String [] rtn = null;
        String [] e = storedMovesData[inx].split(";");
        if (e.length < 2) {
            clearStoredMovesInx(inx);
        } else {
            rtn = e[1].split(",");
            if (e.length == 2) {   // no more pairs of moves
                clearStoredMovesInx(inx);
            } else {
                int i = storedMovesData[inx].indexOf(";");
                if (i > -1) {
                    i = storedMovesData[inx].indexOf(";", i + 1) + 1;  // everything after second ;
                    if (i > -1 && i < storedMovesData[inx].length()) {
                        storedMovesChanged = true;
                        storedMovesData[inx] = storedMovesData[inx].substring(i);
                        storedMovesTimeStamp[inx] = Long.toString(System.currentTimeMillis());
                    } else {
                        clearStoredMovesInx(inx);
                    }
                } else {
                    clearStoredMovesInx(inx);
                }
            }
        }
        return rtn;
    }

    // verify  move, remove first 2 pair and return second pair if no match remove all stored moves for game
	private String [] takeVerifyStoredMoveEntry(int inx, String cMovID, String cClr, String cMov) {
		String [] movInfo = getStoredMoveInfo(inx);
        if (movInfo != null) {
            if (movInfo.length == 3) {
                if (movInfo[0].contentEquals(cMovID)
                        && movInfo[1].contentEquals(cClr)
                        && movInfo[2].contentEquals(cMov)) {
                    movInfo = removeStoredMoveInfo(inx);
                    return movInfo;
                }
            }
        }
		clearStoredMovesInx(inx);
		return null;
	}

    String [] findTakeStoredMove(String gameId, String cMovId, String sgf) {
        synchronized(lockIt) {
            String [] rtnVal = null;
            String lastMov = sgf.substring(sgf.lastIndexOf(";"));
            int i = findStoredMovesInx(gameId);
            if (i>-1) {
                String cClr = lastMov.substring(1,2);
                String cMov = lastMov.substring(3,5);
                if (lastMov.contains("C[")) {   // let user see comments
                    clearStoredMovesInx(i);
                } else {
                    rtnVal = takeVerifyStoredMoveEntry(i, cMovId, cClr, cMov);
                }
            }
            if (MainDGS.dbgStdMov) {
                String rtn;
                if (rtnVal == null) {
                    rtn = "null";
                } else {
                    rtn = rtnVal[0]+","+rtnVal[1]+","+rtnVal[2];
                }
                errHist.writeErrorHistory("StoredMoves.findTakeStoredMove, Gid: " + gameId
                        + ", MovId: " + cMovId + ", found: " + i + ", Last mov: "
                        + lastMov + ", rtn Val: " + rtn);
            }
            return rtnVal;
        }
    }
}
