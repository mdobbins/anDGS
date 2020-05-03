package com.hg.anDGS;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.net.Uri;

class DownloadFile extends Thread {
	private final int DO_NOTHING = 0;
	private final int DO_GET_FILE = 1;
	
	private Uri sourceUri; 
	private DownloadHandler dlHandler;
	private TextHelper th = new TextHelper();
	private int doEvt = DO_NOTHING;
	
	DownloadFile (Uri source, DownloadHandler handler) {
		sourceUri = source;
		dlHandler = handler;
	}
	
	public void run() {
		 while (true) {
	        	switch (doEvt) {
	        	case DO_NOTHING:
		    		try {
		    			sleep(60000);
		    		} catch (InterruptedException ignore) {
		    		}
		    		break;
	        	case DO_GET_FILE:
	        		URL url;
	        		try {
						url = new URL(sourceUri.toString());
					} catch (MalformedURLException e) {
						dlHandler.downloadError(e.toString());
						return;
					}
					try {
						URLConnection ucon = url.openConnection();
						InputStream is = ucon.getInputStream();
	                    String sgf = th.GetText(is);
	                    dlHandler.downloadOk(sgf);
					} catch (IOException e) {
						dlHandler.downloadError(e.toString());
						return;
					}
					return; // only get 1 file
	        	default:
	        		doEvt = DO_NOTHING;
	        	}
		 }
	}
	
	void getFile() {
		doEvt = DO_GET_FILE;
		this.interrupt();
	}

}
