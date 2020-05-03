package com.hg.anDGS;

import android.os.Handler;

class GameBoardHandler extends Handler {
	static final int FWD = 0;
	static final int BKW = 1;
	
	void moveForward()
	{
		sendMessage(obtainMessage(FWD));
	}
	
	void moveBackward()
	{
		sendMessage(obtainMessage(BKW));
	}
}
