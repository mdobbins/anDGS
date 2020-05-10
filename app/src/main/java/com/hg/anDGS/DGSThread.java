package com.hg.anDGS;

import android.content.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class DGSThread extends Thread
{
	private String dgsURL;
	private String qsURL;
	private String sgfURL;
	private String qdURL;
	private volatile boolean mLoggedOn = false;
	private static final int DO_NOTHING = 0;
	private static final int DO_LOGON = 1;
	private static final int DO_SKIP_LOGON = 2;
	private static final int DO_SEND_HANDICAP = 3;
	private static final int DO_SEND_MOVE = 4;
	private static final int DO_SEND_PASS = 5;
	private static final int DO_SEND_RESIGN = 6;
	private static final int DO_SEND_SCORE = 7;
	private static final int DO_SEND_ACCEPTSCORE = 8;
	private static final int DO_SEND_MSG = 9;
	private static final int DO_GET_STATUS_LIST = 10;
	private static final int DO_GET_MOVES = 11;
	private static final int DO_GET_GAME = 12;
	private static final int DO_GET_MSG = 13;
	private static final int DO_GET_BULLETIN = 14;
	private static final int DO_GET_GAMES_LIST = 15;
	private static final int DO_GET_INFO_MY_USER = 16;
	private static final int DO_GET_INFO_USER = 176;
	private static final int DO_MOVE_MSG = 18;
	private static final int DO_SEND_NOTE = 19;
	private static final int DO_DELETE_GAME = 20;
	private static final int DO_GET_WROOM_LIST = 21;
	private static final int DO_GET_WROOM_INFO = 22;
	private static final int DO_JOIN_WROOM_GAME = 23;
	private static final int DO_MARK_BULLETIN = 24;
	
	static final int GAMES_RUNNING = 0;
	static final int GAMES_FINISHED = 1;
	static final int GAMES_OBSERVED = 2;
	static final int GAMES_MULTIPLAYER= 3;
	
	private int doEvt;
	private Map<String,String> HTTPparams1 = new HashMap<String, String>();
	private Map<String,String> HTTPparams2 = new HashMap<String, String>();
	private int HTTPparamsUse = 1;
	private String doString1 = "";
	private String doString2 = "";
	private String mUser;
	private String mPass;
	private String games;
	private String gameId = "";
	private String moveId = "";
	private String game_action = "";
	private String game_status = "";
	private String handicap = "";
	private String timeLeft = "";
	private String msgId = "";
	private String msgType = "";
	private boolean gameNotes = false;
	private String bulletinId = "";
//	private String mpgId = "";
//	private String user = "";
	private Context ctxt;
	private MsgHandler mHandler;
	private CommonStuff commonStuff = new CommonStuff();
	private ErrorHistory errHist = ErrorHistory.getInstance();

	/**
	 * Volatile stop flag used to coordinate state between the two threads
	 * involved in this example.
	 */
	private volatile boolean mStopped = false;
	
	/**
	 * Synchronizes access to mMethod to prevent an unlikely race condition
	 * when stopDownload() is called before mMethod has been committed.
	 */
//	private Object lock = new Object();

	DGSThread(Context ctx, String serverURL, String user, String pass, MsgHandler handler, boolean firstLogon)
	{
		ctxt = ctx;
		mUser = user;
		mPass = pass;
		if (mPass.length() > 16) mPass = mPass.substring(0, 16);
		dgsURL = serverURL;
		qsURL = dgsURL + "quick_status.php";
		sgfURL = dgsURL + "sgf.php";
		qdURL = dgsURL + "quick_do.php";
		mHandler = handler;
		mLoggedOn = false;
		if (firstLogon) {
			doEvt = DO_LOGON;
		} else {
			doEvt = DO_SKIP_LOGON;
		}
	}

	public void run()
	{
		String rslt;
		String [] sgfGameRslt = new String[9]; // sgf, gameId, moveId, game_action, game_status, handicap, timeLeft, score/note, s/n value
		String [] msgRslt = new String[3];
		if (MainDGS.dbgThread) {
			errHist.writeErrorHistory("DGSThread, begin run");
		}
		if (doEvt != DO_SKIP_LOGON) {
            doEvt = DO_LOGON;
        }
        mLoggedOn = false;
        mStopped = false;

        while (!mStopped) {
        	if (!mLoggedOn && doEvt != DO_NOTHING) {
        		if (doEvt == DO_SKIP_LOGON) {
        			mHandler.returnLogonRslt("Ok");  // allow the client to activate the buttons
        		} else {
	        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_LOGGING_ON);
	    			logonToDGS(mUser, mPass);
        		}
	        }
        	switch (doEvt) {
			case DO_NOTHING:
        		if (mLoggedOn) {
        			try {
						yield();
    	    			sleep(120000);  // 2 minutes
    	    		} catch (InterruptedException e) {
    	    			break; // some action
    	    		}
					mLoggedOn = false;  // 2 minutes idle, need to logon again
        		} else {
        			try {
						yield();
    	    			sleep(600000);  // 10 minutes
    	    		} catch (InterruptedException e) {
    	    			break; // some action
    	    		}
        		}	
	    		break;
        	case DO_SEND_HANDICAP:
			case DO_SEND_MOVE:
					doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_SENDING_MOVE);
    			rslt = doHTTPreq(doString1);
    			if (gameNotes) {
	    			mHandler.returnDGSThreadStatus(""+MsgHandler.TS_SENDING_NOTES);
	    			HTTPparamsUse = 2;
	    			rslt = doHTTPreq(doString2);
					HTTPparamsUse = 1;
				}
    			mHandler.returnMoveSent(rslt);
        		break;
			case DO_SEND_PASS:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_SENDING_MOVE);
        		rslt = doHTTPreq(doString1);
        		if (gameNotes) {
	    			mHandler.returnDGSThreadStatus(""+MsgHandler.TS_SENDING_NOTES);
					HTTPparamsUse = 2;
					rslt = doHTTPreq(doString2);
					HTTPparamsUse = 1;
				}
				mHandler.returnPassSent(rslt); 
				break;
        	case DO_SEND_RESIGN:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_SENDING_MOVE);
        		rslt = doHTTPreq(doString1);
        		if (gameNotes) {
	    			mHandler.returnDGSThreadStatus(""+MsgHandler.TS_SENDING_NOTES);
					HTTPparamsUse = 2;
					rslt = doHTTPreq(doString2);
					HTTPparamsUse = 1;
    			}
				mHandler.returnResignSent(rslt);  
				break;
        	case DO_SEND_SCORE: 
        	case DO_SEND_ACCEPTSCORE:
        	case DO_DELETE_GAME:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_SENDING_MOVE);
    			rslt = doHTTPreq(doString1);
    			mHandler.returnMoveSent(rslt);
        		break;
        	case DO_SEND_NOTE:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_SENDING_NOTES);
    			rslt = doHTTPreq(doString1);
    			mHandler.returnNoteSent(rslt);
        		break;
        	case DO_SEND_MSG:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_SENDING_MESSAGE);
        		rslt = doHTTPreq(doString1);
				mHandler.returnMessageSent(rslt);
   				break; 
        	case DO_GET_STATUS_LIST:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_GETTING_STATUS_LIST);
				games = doHTTPreq(doString1);
				long prevStatusTime = MainDGS.lastStatusTime;
				Date dt = new Date(prevStatusTime);
				MainDGS.lastStatusTime = System.currentTimeMillis();
				if (MainDGS.dbgStatus) {
					String prevStatusDate = dt.toLocaleString();
					errHist.writeErrorHistory("DGSThread, sent get status: "
							+ "prevStatusTime: " + prevStatusDate
							+ ", interval: " + commonStuff.timeDiff(MainDGS.lastStatusTime,prevStatusTime));
				}
        		//if (games.contains("'G',") || games.contains("'M',")){
        		mHandler.returnListRslt(games);
        		//} else {
        		//	mHandler.returnListRslt("None");
        		//}
        		break;
        	case DO_GET_INFO_MY_USER:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_SENDING);
    			rslt = doHTTPreq(doString1);
    			mHandler.returnInfoMyUser(rslt);
        		break;
        	case DO_GET_INFO_USER:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_SENDING);
    			rslt = doHTTPreq(doString1);
    			mHandler.returnInfoUser(rslt);
        		break;
         	case DO_GET_MSG:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_GETTING_MESSAGE);
        		rslt = doHTTPreq(doString1);
        		msgRslt[0] = rslt;
        		msgRslt[1] = msgId;
        		msgRslt[2] = msgType;
   				mHandler.returnMSG(msgRslt);
   				break; 
        	case DO_MOVE_MSG:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_SENDING);
        		rslt = doHTTPreq(doString1);
				mHandler.returnMovedMSG(rslt);
   				break; 
        	case DO_MARK_BULLETIN:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_SENDING);
        		rslt = doHTTPreq(doString1);
				mHandler.returnMarkedBulletin(rslt);
   				break;
        	case DO_GET_BULLETIN:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_SENDING);
        		rslt = doHTTPreq(doString1);
        		msgRslt[0] = rslt;
        		msgRslt[1] = bulletinId;
   				mHandler.returnBulletin(msgRslt); 
   				break;
        	case DO_GET_GAME:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_GETTING_GAME);
        		rslt = doHTTPreq(doString1);
				sgfGameRslt[0] = rslt;
				sgfGameRslt[1] = gameId;
				sgfGameRslt[2] = moveId;
				sgfGameRslt[3] = game_action;
				sgfGameRslt[4] = game_status;
				sgfGameRslt[5] = handicap;
				sgfGameRslt[6] = timeLeft;
        		if (game_action.contentEquals(BoardManager.GA_SCORING)) {
					mHandler.returnDGSThreadStatus(""+MsgHandler.TS_GETTING_SCORE);
					HTTPparamsUse = 2;
					rslt = doHTTPreq(doString2);
					HTTPparamsUse = 1;
					sgfGameRslt[7] = "score";
					sgfGameRslt[8] = rslt;

        		} else {
        			if (gameNotes) {
    					mHandler.returnDGSThreadStatus(""+MsgHandler.TS_GETTING_NOTES);
						HTTPparamsUse = 2;
    					rslt = doHTTPreq(doString2);
						HTTPparamsUse = 1;
    					sgfGameRslt[7] = "" + gameNotes;
    					sgfGameRslt[8] = rslt;
    				} else {
    					sgfGameRslt[7] = "" + false;
    					sgfGameRslt[8] = "";
    				}
        		}
        		mHandler.returnSGF(sgfGameRslt);
        		break; 
        	case DO_GET_GAMES_LIST:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_GETTING_GAME_LIST);
        		rslt = doHTTPreq(doString1);
				mHandler.returnShowGamesListRslt(rslt);
   				break;
        	case DO_GET_WROOM_LIST:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_GETTING_WROOM_LIST);
        		rslt = doHTTPreq(doString1);
				mHandler.returnShowWroomListRslt(rslt);
   				break;
        	case DO_GET_WROOM_INFO:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_GETTING_WROOM_INFO);
        		rslt = doHTTPreq(doString1);
				mHandler.returnShowWroomInfoRslt(rslt);
   				break;
        	case DO_JOIN_WROOM_GAME:
        		doEvt = DO_NOTHING;
        		mHandler.returnDGSThreadStatus(""+MsgHandler.TS_JOINING_WROOM_GAME);
        		rslt = doHTTPreq(doString1);
				mHandler.returnJoinWroomGameRslt(rslt);
   				break;
			case DO_LOGON:
			case DO_SKIP_LOGON:
			default:
        		doEvt = DO_NOTHING;
        	}
        }
		mLoggedOn = false;
		if (MainDGS.dbgThread) {
			errHist.writeErrorHistory("DGSThread, end run");
		}
	}

	private Map<String,String> getParams(){
		Map<String,String> params = new HashMap<String, String>();
		params.clear();
		if (HTTPparamsUse == 1) {
			params.putAll(HTTPparams1);
			HTTPparams1.clear();
		}
		else if (HTTPparamsUse == 2) {
			params.putAll(HTTPparams2);
			HTTPparams2.clear();
		}
		return params;
	}

	private String doHTTPreq(String baseURL) {
		String rsp = commonStuff.executeHTTPreq(baseURL, getParams());
		if (rsp.startsWith("#Failed: ")) {
			errHist.writeErrorHistory("DGSThread, " + rsp);
		}
		return rsp;
	}

	private void logonToDGS(String DGSUser, String DGSPass) {
		String rslt = commonStuff.executeLogonToDGS(DGSUser, DGSPass);
		if (rslt.contains("Ok")) {
			mLoggedOn = true;
			mHandler.returnLogonRslt("Ok");
		} else {
			mHandler.returnLogonRslt(rslt);
			mLoggedOn = false;
		}
	}

	void getInfoMyUser() {
		HTTPparams1.clear();
		HTTPparams1.put("obj","user");
		HTTPparams1.put("cmd","info");
		doString1 = qdURL;
		doEvt = DO_GET_INFO_MY_USER;
		this.interrupt();
	}
	
	void getInfoUid(String uId) {
		HTTPparams1.clear();
		HTTPparams1.put("obj","user");
		HTTPparams1.put("cmd","info");
		HTTPparams1.put("uid",uId);
		doString1 = qdURL;
		doEvt = DO_GET_INFO_USER;
		this.interrupt();
	}
	
	void getInfoUser(String user) {
		HTTPparams1.clear();
		HTTPparams1.put("obj","user");
		HTTPparams1.put("cmd","info");
		HTTPparams1.put("user",user);
		doString1 = qdURL;
		doEvt = DO_GET_INFO_USER;
		this.interrupt();
	}
	
	void getStatusList(String ordr) {
		HTTPparams1.clear();
		//HTTPparams1.put("userid",commonStuff.encodeIt(mUser));
		//HTTPparams1.put("passwd",commonStuff.encodeIt(mPass));
		HTTPparams1.put("order",ordr);
		HTTPparams1.put("no_cache","0");
		HTTPparams1.put("version","2");
		doString1 = qsURL;
		doEvt = DO_GET_STATUS_LIST;
		this.interrupt();
	}
	
	void getGame(String m_gid, String m_moveId, String m_game_action, String m_game_status, String m_handicap, String m_timeLeft, boolean m_gameNotes) {
		gameId = m_gid;
		moveId = m_moveId;
		game_action = m_game_action;
		game_status = m_game_status;
		handicap = m_handicap;
		timeLeft = m_timeLeft;
		gameNotes = m_gameNotes;
		HTTPparams1.clear();
		HTTPparams1.put("gid",gameId);
		HTTPparams1.put("owned_comments","1");
		HTTPparams1.put("quick_mode","1");
		doString1 = sgfURL;
		if (game_action.contentEquals(BoardManager.GA_SCORING)) {
			HTTPparams2.clear();
			HTTPparams2.put("obj","game");
			HTTPparams2.put("cmd","status_score");
			HTTPparams2.put("gid",gameId);
			doString2 = qdURL;
		} else {
			if (gameNotes) {
				HTTPparams2.clear();
				HTTPparams2.put("obj","game");
				HTTPparams2.put("cmd","get_notes");
				HTTPparams2.put("gid",gameId);
				doString2 = qdURL;
			} else {
				HTTPparams2.clear();
				doString2 = "";
			}
		}
		doEvt = DO_GET_GAME;
		this.interrupt();
	}
	
	void getShowGamesList(int listType) {
		HTTPparams1.clear();
		HTTPparams1.put("obj","game");
		HTTPparams1.put("cmd","list");
		HTTPparams1.put("lstyle","json");
		HTTPparams1.put("with","user_id");
		doString1 = qdURL;
		switch (listType) {
		case GAMES_MULTIPLAYER:
			HTTPparams1.put("view","running");
			HTTPparams1.put("limit","all");
			HTTPparams1.put("filter_mpg","1");
			break;
		case GAMES_OBSERVED:
			HTTPparams1.put("view","observe");
			HTTPparams1.put("limit","25");
			break;
		case GAMES_FINISHED:
			HTTPparams1.put("view","finished");
			HTTPparams1.put("limit","25");
			break;
		case GAMES_RUNNING:
		default:
			HTTPparams1.put("view","running");
			HTTPparams1.put("limit","25");
		}
		doEvt = DO_GET_GAMES_LIST;
		this.interrupt();
	}
	
	void sendHandicap(String m_gid, String h_movs, String msg, boolean m_gameNotes, String note) {
		gameId = m_gid;
		moveId = "0";
		gameNotes = m_gameNotes;
		HTTPparams1.clear();
		HTTPparams1.put("obj","game");
		HTTPparams1.put("cmd","set_handicap");
		HTTPparams1.put("gid",gameId);
		HTTPparams1.put("move_id",moveId);
		HTTPparams1.put("msg",msg);
		HTTPparams1.put("move",h_movs);
		doString1 = qdURL;
		addNotes2Request(note);
		doEvt = DO_SEND_HANDICAP;
		this.interrupt();
	}
	
	void sendGameNote(String m_gid, String note) {
		gameId = m_gid;
		HTTPparams1.clear();
		HTTPparams1.put("obj","game");
		HTTPparams1.put("cmd","save_notes");
		HTTPparams1.put("gid",gameId);
		if (note.contentEquals("")) {
			HTTPparams1.put("notes","");
		} else {
			HTTPparams1.put("notes",note);
		}
		doString1 = qdURL;
		HTTPparams2.clear();
		doString2 = "";
		doEvt = DO_SEND_NOTE;
		this.interrupt();
	}
	
	void sendMove(String m_gid, String m_movid, String mov, String msg, boolean m_gameNotes, String note) {
		gameId = m_gid;
		moveId = m_movid;
		gameNotes = m_gameNotes;
		HTTPparams1.clear();
		HTTPparams1.put("obj","game");
		HTTPparams1.put("cmd","move");
		HTTPparams1.put("gid",gameId);
		HTTPparams1.put("move_id",moveId);
		HTTPparams1.put("move",mov);
		HTTPparams1.put("msg",msg);
		doString1 = qdURL;
		addNotes2Request(note);
		doEvt = DO_SEND_MOVE;
		this.interrupt();
	}
	
	void sendPass(String m_gid, String m_movid, String msg, boolean m_gameNotes, String note) {
		gameId = m_gid;
		moveId = m_movid;
		gameNotes = m_gameNotes;
		HTTPparams1.clear();
		HTTPparams1.put("obj","game");
		HTTPparams1.put("cmd","move");
		HTTPparams1.put("gid",gameId);
		HTTPparams1.put("move_id",moveId);
		HTTPparams1.put("move","pass");
		HTTPparams1.put("msg",msg);
		doString1 = qdURL;
		addNotes2Request(note);
		doEvt = DO_SEND_PASS;
		this.interrupt();
	}
	
	void sendResign(String m_gid, String m_movid, String msg, boolean m_gameNotes, String note) {
		gameId = m_gid;
		moveId = m_movid;
		gameNotes = m_gameNotes;
		HTTPparams1.clear();
		HTTPparams1.put("obj","game");
		HTTPparams1.put("cmd","resign");
		HTTPparams1.put("gid",gameId);
		HTTPparams1.put("move_id",moveId);
		HTTPparams1.put("msg",msg);
		doString1 = qdURL;
		addNotes2Request(note);
		doEvt = DO_SEND_RESIGN;
		this.interrupt();
	}
	
	void sendScore(String m_gid, String m_movid, String mov, String msg) {
		gameId = m_gid;
		moveId = m_movid;
		HTTPparams1.clear();
		HTTPparams1.put("obj","game");
		HTTPparams1.put("cmd","score");
		HTTPparams1.put("gid",gameId);
		HTTPparams1.put("move_id",moveId);
		HTTPparams1.put("toggle","uniq");
		HTTPparams1.put("move",mov);
		HTTPparams1.put("msg",msg);
		doString1 = qdURL;
		HTTPparams2.clear();
		doString2 = "";
		doEvt = DO_SEND_SCORE;
		this.interrupt();
	}
	
	void sendAcceptScore(String m_gid, String m_movid, String msg) {
		gameId = m_gid;
		moveId = m_movid;
		HTTPparams1.clear();
		HTTPparams1.put("obj","game");
		HTTPparams1.put("cmd","score");
		HTTPparams1.put("gid",gameId);
		HTTPparams1.put("move_id",moveId);
		HTTPparams1.put("move","");
		HTTPparams1.put("agree","1");
		HTTPparams1.put("msg",msg);
		doString1 = qdURL;
		HTTPparams2.clear();
		doString2 = "";
		doEvt = DO_SEND_SCORE;
		this.interrupt();
	}
	
	void deleteDGSgame(String m_gid, String m_movid, String msg) {
		gameId = m_gid;
		moveId = m_movid;
		HTTPparams1.clear();
		HTTPparams1.put("obj","game");
		HTTPparams1.put("cmd","delete");
		HTTPparams1.put("gid",gameId);
		HTTPparams1.put("move_id",moveId);
		HTTPparams1.put("msg",msg);
		doString1 = qdURL;
		HTTPparams2.clear();
		doString2 = "";
		doEvt = DO_DELETE_GAME;
		this.interrupt();
	}
	
	void sendMSG(String msg) {
		HTTPparams1.clear();
		HTTPparams1.put("obj","message");
		HTTPparams1.put("cmd","send_msg");
		HTTPparams1.put("msg",msg);
		doString1 = qdURL;
		HTTPparams2.clear();
		doString2 = "";
		doEvt = DO_SEND_MSG;
		this.interrupt();
	}

	void sendInvitation(String inv) {  // TODO lookup and fix this,
		HTTPparams1.clear();
		HTTPparams1.put("obj","message");
		HTTPparams1.put("cmd","send_inv");
		HTTPparams1.put("msg",inv);   // TODO
		doString1 = qdURL;
		HTTPparams2.clear();
		doString2 = "";
		doEvt = DO_SEND_MSG;
		this.interrupt();
	}

	void sendAcceptInvitation(String m_mid, String msg) {
		msgId = m_mid;
		HTTPparams1.clear();
		HTTPparams1.put("obj","message");
		HTTPparams1.put("cmd","accept_inv");
		HTTPparams1.put("mid",m_mid);
		HTTPparams1.put("msg",msg);
		doString1 = qdURL;
		HTTPparams2.clear();
		doString2 = "";
		doEvt = DO_SEND_MSG;
		this.interrupt();
	}
	
	void sendDeclineInvitation(String m_mid, String msg) {
		msgId = m_mid;
		HTTPparams1.clear();
		HTTPparams1.put("obj","message");
		HTTPparams1.put("cmd","decline_inv");
		HTTPparams1.put("mid",m_mid);
		HTTPparams1.put("msg",msg);
		doString1 = qdURL;
		HTTPparams2.clear();
		doString2 = "";
		doEvt = DO_SEND_MSG;
		this.interrupt();
	}
	
	void getMSG(String m_mid, String m_msgType, String fldr) {
		msgId = m_mid;
		msgType = m_msgType;
		HTTPparams1.clear();
		HTTPparams1.put("obj","message");
		HTTPparams1.put("cmd","info");
		HTTPparams1.put("mid",m_mid);
		HTTPparams1.put("with","user_id");
		if (!fldr.contentEquals("0")) {
			HTTPparams1.put("folder",fldr);
		}
		doString1 = qdURL;
		HTTPparams2.clear();
		doString2 = "";
		doEvt = DO_GET_MSG;
		this.interrupt();
	}
	
	void moveMSG(String m_mid, String m_folder) {
		msgId = m_mid;
		HTTPparams1.clear();
		HTTPparams1.put("obj","message");
		HTTPparams1.put("cmd","move_msg");
		HTTPparams1.put("mid",m_mid);
		HTTPparams1.put("folder",m_folder);
		doString1 = qdURL;
		HTTPparams2.clear();
		doString2 = "";
		doEvt = DO_MOVE_MSG;
		this.interrupt();
	}
	
	void getBulletin(String m_bid) {
		bulletinId = m_bid;
		HTTPparams1.clear();
		HTTPparams1.put("obj","bulletin");
		HTTPparams1.put("cmd","info");
		HTTPparams1.put("bid",m_bid);
		HTTPparams1.put("with","user_id");
		doString1 = qdURL;
		HTTPparams2.clear();
		doString2 = "";
		doEvt = DO_GET_BULLETIN;
		this.interrupt();
	}
	
	void markReadBulletin(String m_bid) {
		bulletinId = m_bid;
		HTTPparams1.clear();
		HTTPparams1.put("obj","bulletin");
		HTTPparams1.put("cmd","mark_read");
		HTTPparams1.put("bid",m_bid);
		doString1 = qdURL;
		HTTPparams2.clear();
		doString2 = "";
		doEvt = DO_MARK_BULLETIN;
		this.interrupt();
	}
	
	void getWroomList() {
		HTTPparams1.clear();
		HTTPparams1.put("obj","wroom");
		HTTPparams1.put("cmd","list");
		HTTPparams1.put("with","user_id");
		HTTPparams1.put("filter_suitable","1");
		HTTPparams1.put("lstyle","json");
		doString1 = qdURL;
		HTTPparams2.clear();
		doString2 = "";
		doEvt = DO_GET_WROOM_LIST; 
		this.interrupt();
	}
	
	void getWroomInfo(String m_wrid) {
		gameId = m_wrid;
		HTTPparams1.clear();
		HTTPparams1.put("obj","wroom");
		HTTPparams1.put("cmd","info");
		HTTPparams1.put("with","user_id");
		HTTPparams1.put("wrid",m_wrid);
		doString1 = qdURL;
		HTTPparams2.clear();
		doString2 = "";
		doEvt = DO_GET_WROOM_INFO; 
		this.interrupt();
	}
	
	void joinWroomGame(String m_wrid) {
		gameId = m_wrid;
		HTTPparams1.clear();
		HTTPparams1.put("obj","wroom");
		HTTPparams1.put("cmd","join");
		HTTPparams1.put("wrid",m_wrid);
		doString1 = qdURL;
		HTTPparams2.clear();
		doString2 = "";
		doEvt = DO_JOIN_WROOM_GAME; 
		this.interrupt();
	}

	private void addNotes2Request(String note) {
		if (gameNotes) {
			HTTPparams2.clear();
			HTTPparams2.put("obj","game");
			HTTPparams2.put("cmd","save_notes");
			HTTPparams2.put("gid",gameId);
			if (note.contentEquals("")) {
				HTTPparams2.put("notes","");
			} else {
				HTTPparams2.put("notes",note);
			}
			doString2 = qdURL;
		} else {
			HTTPparams2.clear();
			doString2 = "";
		}
	}
		
	private int findStr(String msg, String s, int inx, boolean skip) {
		inx = msg.indexOf(s, inx);
		if (inx < 0) return inx;
		if (skip) {
			inx = inx + s.length();
		}
		return inx;
	}
	
	
	private String stripMarkup(String msg) {
		int inx;
		int inx2 = 0;
		StringBuilder sb = new StringBuilder();
		while ((inx=findStr(msg, "<", inx2, false)) > -1) {
			sb.append(msg.substring(inx2, inx));
			inx2 = findStr(msg, ">", inx, true);
		}
        if (inx2 > -1 && inx2 < msg.length()) {
        	sb.append(msg.substring(inx2));
        }
		return sb.toString();
	}
	
	@SuppressWarnings("unused")
	private String editMsg(String msg) {
		if (msg.contains("<BR>")) msg = msg.replaceAll("<BR>", "\n");
		if (msg.contains("<")) msg = stripMarkup(msg);
		if (msg.contains("&quot;")) msg = msg.replaceAll("&quot;", "\"");
		if (msg.contains("&#34;")) msg = msg.replaceAll("&#34;", "\"");
		if (msg.contains("&amp;")) msg = msg.replaceAll("&amp;", "&");
		if (msg.contains("&#38;")) msg = msg.replaceAll("&#38;", "&");
		if (msg.contains("&apos;")) msg = msg.replaceAll("&apos;", "'");
		if (msg.contains("&#039;")) msg = msg.replaceAll("&#039;", "'");
		if (msg.contains("&lt;")) msg = msg.replaceAll("&lt;", "<");
		if (msg.contains("&#60;")) msg = msg.replaceAll("&#60;", "<");
		if (msg.contains("&gt;")) msg = msg.replaceAll("&gt;", ">");
		if (msg.contains("&#62;")) msg = msg.replaceAll("&#62;", ">");
		if (msg.contains("&nbsp;")) msg = msg.replaceAll("&nbsp;", " ");
		if (msg.contains("&#160;")) msg = msg.replaceAll("&#160;", " ");
		return msg;
	}
	
	/**
	 * This method is to be called from a separate thread.  That is, not the
	 * one executing run().  When it exits, the download thread should be on
	 * its way out (failing a connect or read call and cleaning up).
	 */
	void stopThread()
	{
		/* As we've written this method, calling it from multiple threads would
		 * be problematic. */
		if (mStopped)
			return;

		/* Too late! */
		if (!isAlive())
			return;

		/* Flag to instruct the downloading thread to halt at the next
		 * opportunity. */
		mStopped = true;

		/* Interrupt the blocking thread.  This won't break out of a blocking
		 * I/O request, but will break out of a wait or sleep call.  While in
		 * this case we know that no such condition is possible, it is always a
		 * good idea to include an interrupt to avoid assumptions about the
		 * thread in question. */
		this.interrupt();
		
//		mHandler.sendStopped();	

	}
}