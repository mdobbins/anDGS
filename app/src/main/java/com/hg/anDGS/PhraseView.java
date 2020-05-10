package com.hg.anDGS;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PhraseView extends DGSActivity implements OnClickListener {

    private List<String> listEntries = new ArrayList<String>();
    protected static final int HELP_VIEW = 0;
	private static final int MENU_HELP = 0;
	private static final String EMPTY_PHRASE = ".";

	private String boardLayout = PrefsDGS.PORTRAIT;
	private String [] phraseList = null;
	private String phrases = null;
	private ContextThemeWrapper ctw;
	private String theme;
	private CommonStuff commonStuff = new CommonStuff();
	private CommonFileStuff commonFileStuff = new CommonFileStuff();

    /** Called when the activity is first created. */
    public void onCreate(Bundle icicle) {
         super.onCreate(icicle);
 		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 
		 Bundle extras = getIntent().getExtras();
	        if (extras != null) {
	        	boardLayout = extras.getString("BOARDLAYOUT");
	        }

  		SharedPreferences prefs = getSharedPreferences("MainDGS", 0);
		theme = prefs.getString("com.hg.anDGS.Theme", PrefsDGS.DEFAULT_THEME);
		phrases = prefs.getString("com.hg.anDGS.Phrases", EMPTY_PHRASE);
		if (!phrases.contentEquals(EMPTY_PHRASE)) {  // migrate to files
			commonFileStuff.writePhrasesData(phrases);
			SharedPreferences.Editor editor = getSharedPreferences("MainDGS", 0).edit();
			editor.putString("com.hg.anDGS.Phrases", EMPTY_PHRASE);
			editor.commit();
		} else {
			phrases = commonFileStuff.readPhrasesData();
		}

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
 		this.setTheme(commonStuff.getCommonStyle(theme));
 		setContentView(R.layout.statusview);
		ctw = new ContextThemeWrapper(this, commonStuff.getCommonTheme(theme));

		TextView tmHelp = findViewById(R.id.statusTMHelp);
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

		TextView statusTitleView = findViewById(R.id.statusTitle);
		statusTitleView.setText(R.string.Comment);
		ListView statusListView = findViewById(R.id.statusList);
		
 		listEntries.clear();
        
        try {
             Thread.sleep(10);
        } catch (InterruptedException ignore) {
        }
        if (phrases == null) phrases = EMPTY_PHRASE;
    	phraseList = phrases.split("\n");
		for (String s : phraseList) {
			listEntries.add(s.trim());
		}

        ArrayAdapter<String> displayList = new ArrayAdapter<String>(this,
                  R.layout.file_row, listEntries); 
        
        statusListView.setAdapter(displayList);
        statusListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				final int indx = position;
		    	final String s = phraseList[indx];
		    	v.setVisibility(View.INVISIBLE);
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
		        
		        final EditText input = new EditText(ctw);
			    input.setText(phraseList[position]);
			    new AlertDialog.Builder(ctw)
			    .setTitle(R.string.Comment)
			    .setMessage(R.string.EditComment)
			    .setView(input)
			    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	phraseList[indx] = input.getText().toString();
			    	if (!s.contentEquals(phraseList[indx])) {
			    		phrases = "";
						for (String value : phraseList) {
							if (!value.contentEquals(""))     // remove any empty phrases
								phrases = phrases + value + "\n";
						}
			    		if (!phraseList[phraseList.length-1].contentEquals(EMPTY_PHRASE))
			    			phrases = phrases + EMPTY_PHRASE;
						commonFileStuff.writePhrasesData(phrases);
			    	}
			        Bundle rslts = new Bundle();
			     	rslts.putString("PHRASE",phraseList[indx]); 
			 		Intent mIntent = new Intent();
			 		mIntent.putExtras(rslts);
			 		setResult(RESULT_OK, mIntent);
			 		finish();
			    }})
			    .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	// do nothing
			    }})
			    .show();
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
		 if (item.getItemId() == MENU_HELP) {
			 doHelp();
		 }
		 return false;
	 }
	 
	 private void doHelp() {
		final Intent helpIntent = commonStuff.helpDGS (commonStuff.HELP_PHRASES, this);
		helpIntent.putExtra("BOARDLAYOUT", boardLayout);
		startActivityForResult(helpIntent, HELP_VIEW);   
	 }

	 public void onClick(View v) {
		}
}
