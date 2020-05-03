package com.hg.anDGS;

import android.os.Handler;

abstract class MsgHandler extends Handler
{
	static final int LOGON = 0;
	static final int STATUSLIST = 1;
	static final int SGF = 2;
	static final int MOVESENT = 3;
	static final int STOPPED = 4;
	static final int MSG = 5;
	static final int PASSSENT = 6;
	static final int RESIGNSENT = 7;
	static final int GAMESLIST = 8;
	static final int MESSAGESENT = 9;
	static final int INFOMYUSER = 10;
	static final int INFOUSER = 11;
	static final int MOVEDMSG = 12;
	static final int THREADSTATUS = 13;
	static final int NOTESENT = 14;
	static final int BULLETIN = 15;
	static final int WROOMLISTRESULT = 16;
	static final int WROOMINFORESULT = 17;
	static final int JOINWROOMGAMERESULT = 18;
	static final int MARKEDBULLETIN = 19;
	static final int TIMEOUT = 20;
	
	// Thread statuses
	static final int TS_OK = 0;
	static final int TS_LOGGING_ON = 1;
	static final int TS_SENDING = 2;
	static final int TS_SENDING_MOVE = 3;
	static final int TS_SENDING_NOTES = 4;
	static final int TS_SENDING_MESSAGE = 5;
	static final int TS_SENDING_SCORE = 6;
	static final int TS_GETTING_STATUS_LIST = 7;
	static final int TS_GETTING_MESSAGE = 8;
	static final int TS_GETTING_GAME = 9;
	static final int TS_GETTING_NOTES = 10;
	static final int TS_GETTING_GAME_LIST = 11;
	static final int TS_GETTING_SCORE = 12;
	static final int TS_GETTING_WROOM_LIST = 13;
	static final int TS_GETTING_WROOM_INFO = 14;
	static final int TS_JOINING_WROOM_GAME = 15;
	static final int TS_CONNECTION_TIMEOUT = 16;

	void returnLogonRslt(String rslt)
	{
		sendMessage(obtainMessage(LOGON, rslt));	
	}

	void returnConnectionTimeOut(String rslt)
	{
		sendMessage(obtainMessage(TIMEOUT, rslt));
	}

	void returnListRslt(String rslt)
	{
		sendMessage(obtainMessage(STATUSLIST, rslt));
	}
	
	void returnShowGamesListRslt(String rslt)
	{
		sendMessage(obtainMessage(GAMESLIST, rslt));
	}

	void returnSGF(String [] rslt)
	{
		sendMessage(obtainMessage(SGF, rslt));
	}

	void returnMSG(String [] rslt)
	{
		sendMessage(obtainMessage(MSG, rslt));
	}
	
	void returnBulletin(String [] rslt)
	{
		sendMessage(obtainMessage(BULLETIN, rslt));
	}
	
	void returnMarkedBulletin(String rslt) { sendMessage(obtainMessage(MARKEDBULLETIN, rslt)); }
	
	void returnMoveSent(String rslt)
	{
		sendMessage(obtainMessage(MOVESENT, rslt));
	}
	
	void returnNoteSent(String rslt)
	{
		sendMessage(obtainMessage(NOTESENT, rslt));
	}
	
	void returnMovedMSG(String rslt)
	{
		sendMessage(obtainMessage(MOVEDMSG, rslt));
	}
	
	void returnPassSent(String rslt)
	{
		sendMessage(obtainMessage(PASSSENT, rslt));
	}
	
	void returnResignSent(String rslt)
	{
		sendMessage(obtainMessage(RESIGNSENT, rslt));
	}
	
	void returnMessageSent(String rslt)
	{
		sendMessage(obtainMessage(MESSAGESENT, rslt));
	}
	
	void returnInfoMyUser(String rslt)
	{
		sendMessage(obtainMessage(INFOMYUSER, rslt));
	}

	void returnInfoUser(String rslt)
	{
		sendMessage(obtainMessage(INFOUSER, rslt));
	}

	void returnSendStopped()
	{
		sendMessage(obtainMessage(STOPPED));
	}
	
	void returnDGSThreadStatus(String rslt)
	{
		sendMessage(obtainMessage(THREADSTATUS, rslt));
	}
	
	void returnShowWroomListRslt(String rslt)
	{
		sendMessage(obtainMessage(WROOMLISTRESULT, rslt));
	}
	
	void returnShowWroomInfoRslt(String rslt)
	{
		sendMessage(obtainMessage(WROOMINFORESULT, rslt));
	}
	
	void returnJoinWroomGameRslt(String rslt) { sendMessage(obtainMessage(JOINWROOMGAMERESULT, rslt)); }
}
