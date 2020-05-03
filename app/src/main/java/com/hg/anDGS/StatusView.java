package com.hg.anDGS;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class StatusView extends DGSActivity implements OnClickListener {
    private List<String> listEntries = new ArrayList<String>();
    protected static final int HELP_VIEW = 0;
	private static final int MENU_HELP = 0;
    
	private String boardLayout = PrefsDGS.PORTRAIT;
	private String statusTitle = "";
	private CommonStuff commonStuff = new CommonStuff();
	private int helpType = commonStuff.HELP_STATUS;

	private String [] statusList = null;

    /** Called when the activity is first created. */
    public void onCreate(Bundle icicle) {
         super.onCreate(icicle);
 		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

         Bundle extras = getIntent().getExtras();
 		 if (extras != null) {
 			statusTitle = extras.getString("TITLE");
 			statusList = extras.getStringArray("STATUSLIST");
 			helpType = extras.getInt("HelpType",commonStuff.HELP_STATUS);
 			boardLayout = extras.getString("BOARDLAYOUT");
 		 }
 		
  		SharedPreferences prefs = getSharedPreferences("MainDGS", 0);
		String theme = prefs.getString("com.hg.anDGS.Theme", PrefsDGS.DEFAULT_THEME);
		this.setTheme(commonStuff.getCommonStyle(theme));
		setContentView(R.layout.statusview);

		if (boardLayout == null) {
			boardLayout = PrefsDGS.PORTRAIT;
		}
/*
 		if (boardLayout.contains(PrefsDGS.LANDSCAPE)) {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
*/
		TextView tmHelp = (TextView) findViewById(R.id.statusTMHelp);
		tmHelp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						doHelp();
					}
				});
			}
		});

		TextView statusTitleView = (TextView) findViewById(R.id.statusTitle);
		statusTitleView.setText(statusTitle);

		ListView statusListView = (ListView) findViewById(R.id.statusList);
 		listEntries.clear();     
        try {
             Thread.sleep(10);
        } catch (InterruptedException ignore) {
        }
       if (statusList != null) {
        	for (int i=0; i<statusList.length; i++) {
        		listEntries.add(statusList[i].replaceAll("'", ""));  
        	}
        }
        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
                  R.layout.file_row, listEntries);        
        statusListView.setAdapter(directoryList);
        statusListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				v.setVisibility(View.INVISIBLE);
		        Bundle rslts = new Bundle();
		     	rslts.putInt("ITEM",position); 
		 		Intent mIntent = new Intent();
		 		mIntent.putExtras(rslts);
		 		setResult(RESULT_OK, mIntent);
		 		final View fv = v;
				final Handler handler = new Handler(); 
		        Timer t = new Timer(); 
		        t.schedule(new TimerTask() { 
		        	public void run() { 
		        		handler.post(new Runnable() { 
		        			public void run() { 
		        				fv.setVisibility(View.VISIBLE); 
		        			} 
		        		}); 
		        	} 
		        }, MainDGS.BUTTON_DELAY);
		 		finish();
			}
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	switch(requestCode) {
    	case HELP_VIEW:
     		break;
    	default:
    	}
    }
    
	 public boolean onPrepareOptionsMenu(Menu menu) {
		 menu.clear();
		 MenuItem help_menu = menu.add(0, MENU_HELP, 0, getString(R.string.Help));
		 help_menu.setIcon(R.drawable.ic_menu_help);
		 return true;
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
		final Intent helpIntent = commonStuff.helpDGS (helpType, this);
		helpIntent.putExtra("BOARDLAYOUT", boardLayout);
		startActivityForResult(helpIntent, HELP_VIEW); 
	 }

	public void onClick(View v) {
	}
}
