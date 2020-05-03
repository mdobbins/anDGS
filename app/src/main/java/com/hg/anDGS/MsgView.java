package com.hg.anDGS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TableRow;
import android.widget.TextView;

public class MsgView extends DGSActivity {
	protected static final int HELP_VIEW = 0;
	private static final int MENU_HELP = 0;
	private String msgTitle = "";
	
	private TextView tmHelp;
	
	private TextView msgTitleView;
	private TextView msg_text_view;
	private TableRow buttonsRow;
	private String boardLayout = PrefsDGS.PORTRAIT;
	private String theme;
	private String msgId = "0";
	private String original_text = "";
	private String [] buttonTexts = null;
	private CommonStuff commonStuff = new CommonStuff();
	private int msgHelpType = commonStuff.HELP_HELP;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			msgTitle = extras.getString("TITLE");
			msgId = extras.getString("MsgId");
			original_text = extras.getString("MsgText");
			buttonTexts = extras.getStringArray("ButtonTexts");
			msgHelpType = extras.getInt("MsgHelpType", commonStuff.HELP_HELP);
			boardLayout = extras.getString("BOARDLAYOUT");
		}
		
 		SharedPreferences prefs = getSharedPreferences("MainDGS", 0);
		theme = prefs.getString("com.hg.anDGS.Theme", PrefsDGS.DEFAULT_THEME);

		if (boardLayout == null) {
			boardLayout = PrefsDGS.PORTRAIT;
		}
		if (msgId == null) {
			msgId = "0";
		}
		if (original_text == null) {
			original_text = "";
		}
		if (buttonTexts == null) {
			buttonTexts = new String [1];
			buttonTexts[0] = getString(R.string.skipButton);
		}
/*
		if (boardLayout.contains(PrefsDGS.LANDSCAPE)) {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
*/
		this.setTheme(commonStuff.getCommonStyle(theme));
		setContentView(R.layout.msgview);
        
		tmHelp = (TextView) findViewById(R.id.msgViewTMHelp);
		if (msgHelpType == commonStuff.HELP_HELP) {
			tmHelp.setText(" ");  // we don't have help for help!!
		} else {
			tmHelp.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					final Handler handler = new Handler();
					handler.post(new Runnable() { 
						public void run() {	
							doHelp();
						}
					});
				}
			});
		}
		
		msgTitleView = (TextView) findViewById(R.id.msgViewTitle);
		msgTitleView.setText(msgTitle);
		
		msg_text_view = (TextView) findViewById(R.id.msgTextView);
		if (msgTitle.contentEquals("RESULT")) {
			original_text = formatResultMsg(original_text);
			//msg_text_view.setAutoLinkMask(Linkify.WEB_URLS);  // do I need to add some conditions?? Maybe WebView will fix it all
		}
		msg_text_view.setText(original_text);
		
		buttonsRow = (TableRow) findViewById(R.id.msgButtonsRow);
		int n = buttonsRow.getChildCount() - 1;
		for (int i=n; i >= 0; i--) {
			TextView tv = (TextView) buttonsRow.getChildAt(i);
			if (i < buttonTexts.length) {
				tv.setText(buttonTexts[i]);
				if (n <= buttonTexts.length) tv.setTextSize(16);
			} else {
				buttonsRow.removeView(tv);
			}
		}	
	}
	
	public void buttonClick(View v) {
		String n = (String) v.getTag();
		Bundle rslts = new Bundle();
    	rslts.putString("RESULT", n);
    	rslts.putString("MsgId", msgId);
		Intent mIntent = new Intent();
		mIntent.putExtras(rslts);
        setResult(RESULT_OK, mIntent);
		finish();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch  (keyCode) {
		 case KeyEvent.KEYCODE_BACK: 
			 return true;
		 default:
		 }
		return super.onKeyDown(keyCode, event);
	}
	
	public boolean onKeyUp (int keyCode, KeyEvent event) {
		switch  (keyCode) {
		 case KeyEvent.KEYCODE_BACK: 
		 	Bundle rslts = new Bundle();
		 	rslts.putString("RESULT", "0");  
			Intent mIntent = new Intent();
			mIntent.putExtras(rslts);
			setResult(RESULT_OK, mIntent);
			finish();
			return true;
		 default:
		 }
		return super.onKeyUp(keyCode, event);
	}
	
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (msgHelpType == commonStuff.HELP_HELP) {
			return false;  // do not show the menu
		} else {
			menu.clear();
			MenuItem help_menu = menu.add(0, MENU_HELP, 0, getString(R.string.Help));
			help_menu.setIcon(R.drawable.ic_menu_help);
			return true;
		}
	}
	 
	 public boolean onOptionsItemSelected(MenuItem item) {
		 
		 switch (item.getItemId()) {
		 case MENU_HELP:
			 doHelp();  
			break;
		 default:
				// nothing 
		 }
		 return false;
	 }
	 
	 private void doHelp() {
		final Intent helpIntent = commonStuff.helpDGS (msgHelpType, this);
		helpIntent.putExtra("BOARDLAYOUT", boardLayout);
		startActivityForResult(helpIntent, HELP_VIEW); 
	 }
	 
	 private String formatResultMsg (String s) {
		 s = s.replace("<center>", "\n");
		 s = s.replace("</center>", "\n");
		 s = s.replace("<br>", "\n");
		 s = s.replace("<p>", "\n");
		 s = s.replace("</p>", "");
		 String s1;
		 String s2;
		 int i;
		 int j;
		 while ((i = s.indexOf("<")) >= 0) {  // remove all tags
			 j = s.indexOf(">", i);
			 if (j<0) break;
			 ++j;
			 s1 = s.substring(0, i);
			 if (j >= s.length()) {
				 s2 = "";
			 }
			 else {
				 s2 =  s.substring(j, s.length());
			 }
			 s = s1 + s2;
		 }
		return s;
	 }
}
