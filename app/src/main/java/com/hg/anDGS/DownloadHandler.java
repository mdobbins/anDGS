package com.hg.anDGS;

import android.os.Handler;

class DownloadHandler extends Handler {
	static final int DOWNLOADERROR = 1;
	static final int DOWNLOADOK = 2;
	
	void downloadError(String rslt)
	{
		sendMessage(obtainMessage(DOWNLOADERROR,rslt));
	}
	
	void downloadOk(String rslt)
	{
		sendMessage(obtainMessage(DOWNLOADOK,rslt));
	}
}
