package com.hg.anDGS;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GrinderMain extends DGSActivity {

	protected static final int HELP_VIEW = 0;
	protected static final int BROWSE_VIEW = 1;
	protected static final int GRIND_VIEW = 2;
	private static final int MENU_HELP = 0;
	protected static final int WHITE_COLOR = 0xffffffff;
	protected static final int GREEN_COLOR = 0xff88ff88;
	
	private TextView tmHelp;
	private TextView tmTitle;
	
	private TextView grinder_browse_button;
	private TextView grinder_go_button;
	private TextView grinder_cont_button;
	
	private TextView target;
	private TextView vTargetFlash;
	private LinearLayout targetLabel;
	private Spinner mode_spinner;
	private TextView vModeFlash;
	private LinearLayout mode_label;
	private TextView autoPlayInterval;
	private TextView vAutoPlayIntervalFlash;
	private LinearLayout autoPlayIntervalLabel;
	private CheckBox autoPlayPauseCB;
	private TextView vAutoPlayPauseFlash;
	private LinearLayout autoPlayPauseLabel;
	private CheckBox autoPlaySoundCB;
	private TextView vAutoPlaySoundFlash;
	private LinearLayout autoPlaySoundLabel;
	
	private String boardLayout = PrefsDGS.PORTRAIT;
	private String theme;
	private String timeLeft = "";
	private String mSGF = "";
	private String mFileName = "";
	private String mTarget = "";
	private long mAutoPlayInterval = GameBoardOptions.DEFAUTOPLAYINTERVAL;
	private boolean mAutoPlayPause = true;
	private boolean mAutoPlaySound = false;
	private String move_control = PrefsDGS.ZOOM7X7;
	private List<String> gameList = new ArrayList<String>();
	private int currentGameInx = -1;
	private ContextThemeWrapper ctw;
	private CommonStuff commonStuff = new CommonStuff();
	private CommonFileStuff commonFileStuff = new CommonFileStuff();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	boardLayout = extras.getString("BOARDLAYOUT");
        }

		SharedPreferences prefs = getSharedPreferences("MainDGS", 0);
		theme = prefs.getString("com.hg.anDGS.Theme", PrefsDGS.DEFAULT_THEME);
		move_control = prefs.getString("com.hg.anDGS.MoveControl", PrefsDGS.ZOOM7X7);
		mAutoPlayPause = prefs.getBoolean("com.hg.anDGS.AutoPlayPause", true);
		mAutoPlaySound = prefs.getBoolean("com.hg.anDGS.AutoPlaySound", false);
		mAutoPlayInterval = prefs.getLong("com.hg.anDGS.AutoPlayInterval", GameBoardOptions.DEFAUTOPLAYINTERVAL);

    	if (boardLayout == null) {
			boardLayout = PrefsDGS.PORTRAIT;
		}
        if (move_control == null) {
			move_control = PrefsDGS.ZOOM7X7;
		}
/*
		if (boardLayout.contains(PrefsDGS.LANDSCAPE)) {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
*/
		this.setTheme(commonStuff.getCommonStyle(theme));
        setContentView(R.layout.grinder);
        ctw = new ContextThemeWrapper(this, commonStuff.getCommonTheme(theme));

		tmHelp = findViewById(R.id.grinderTMHelp);
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

		tmTitle = findViewById(R.id.grinderTMTitle);
		tmTitle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Bundle rslts = new Bundle();
				Intent mIntent = new Intent();
				mIntent.putExtras(rslts);
				setResult(RESULT_CANCELED, mIntent);
				finish();
			}
		});

        grinder_browse_button = findViewById(R.id.grinderBrowseButton);
        grinder_browse_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent mIntent = new Intent(GrinderMain.this, SavedGames.class);
                mTarget = target.getText().toString();
                if (mTarget.contentEquals("")) mTarget = commonFileStuff.getSgfDirName();
                mIntent.putExtra("STARTDIR", mTarget);
                mIntent.putExtra("MODE", SavedGames.SELECT);
                startActivityForResult(mIntent, BROWSE_VIEW);
            }
        });
        
        grinder_go_button = findViewById(R.id.grinderGoButton);
        grinder_go_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doStartBeginning();
            }
        });
        
        grinder_cont_button = findViewById(R.id.grinderNextButton);
        grinder_cont_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (gameList.size() > 0) {
            		doNextGame();
            	} else {
            		doStartBeginning();
            	}
            }
        });
        
        target = findViewById(R.id.grinderTargetEdit);
        vTargetFlash = findViewById(R.id.grinderTargetFlash);
        targetLabel = findViewById(R.id.grinderTargetLabel);
       	targetLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vTargetFlash);
            	doSetTarget();
            }
        });
       	
        String[] items = new String[] {getString(R.string.autoplay), getString(R.string.browse), getString(R.string.edit), getString(R.string.guessmove)};
        mode_spinner = findViewById(R.id.grinderModeSpinner);
        vModeFlash = findViewById(R.id.grinderModeFlash);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mode_spinner.setAdapter(adapter);
       	mode_label = findViewById(R.id.grinderModeLabel);
       	mode_label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vModeFlash);
            	doSetMode();
            }
        });
        
        autoPlayPauseCB = findViewById(R.id.grinderAutoPlayPauseCheckBox);
        vAutoPlayPauseFlash = findViewById(R.id.grinderAutoPlayPauseFlash);
        autoPlayPauseLabel = findViewById(R.id.grinderAutoPlayPauseLabel);
        autoPlayPauseLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vAutoPlayPauseFlash);
            	doSetAutoPlayPause();
            }
        });
        
        autoPlaySoundCB = findViewById(R.id.grinderAutoPlaySoundCheckBox);
        vAutoPlaySoundFlash = findViewById(R.id.grinderAutoPlaySoundFlash);
        autoPlaySoundLabel = findViewById(R.id.grinderAutoPlaySoundLabel);
        autoPlaySoundLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vAutoPlaySoundFlash);
            	doSetAutoPlaySound();
            }
        });
        
        autoPlayInterval = findViewById(R.id.grinderAutoPlayIntervalEdit);
        vAutoPlayIntervalFlash = findViewById(R.id.grinderAutoPlayIntervalFlash);
        autoPlayIntervalLabel = findViewById(R.id.grinderAutoPlayIntervalLabel);
        autoPlayIntervalLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vAutoPlayIntervalFlash);
            	doSetAutoPlayInterval();
            }
        });
        
        mode_spinner.setSelection(0);
		autoPlayInterval.setText(Long.toString(mAutoPlayInterval));
        target.setText(commonFileStuff.getSgfDirName());
		autoPlayPauseCB.setChecked(mAutoPlayPause);
		autoPlaySoundCB.setChecked(mAutoPlaySound);
    }
    
    private void flashButton(View v) {
    	v.setVisibility(View.INVISIBLE); //v.setBackgroundColor(MainDGS.LIGHT_GREY_COLOR);
    	final View fv = v;
		final Handler handler = new Handler(); 
        Timer t = new Timer(); 
        t.schedule(new TimerTask() { 
        	public void run() { 
        		handler.post(new Runnable() { 
        			public void run() { 
        				fv.setVisibility(View.VISIBLE); // fv.setBackgroundColor(MainDGS.WHITE_COLOR);
        			} 
        		}); 
        	} 
        }, MainDGS.BUTTON_DELAY);
    }
    
    private void doSetAutoPlayInterval() { 
    	final EditText input = new EditText(ctw);
    	input.setInputType(InputType.TYPE_CLASS_NUMBER);
	    input.setText(autoPlayInterval.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.autoPlayInterval)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	autoPlayInterval.setText(input.getText());
	    }})
		.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}})
	    .show();
    }
    
    private void doSetTarget() { 
    	final EditText input = new EditText(ctw);
    	input.setInputType(InputType.TYPE_CLASS_NUMBER);
	    input.setText(target.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.selectDirOrFile)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	target.setText(input.getText());
	    }})
		.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}})
	    .show();
    }
    
    private void doSetAutoPlayPause() {
    	if (autoPlayPauseCB.isChecked()) {
    		autoPlayPauseCB.setChecked(false);
    	} else {
    		autoPlayPauseCB.setChecked(true);
    	}
    }
    
    private void doSetAutoPlaySound() {
    	if (autoPlaySoundCB.isChecked()) {
    		autoPlaySoundCB.setChecked(false);
    	} else {
    		autoPlaySoundCB.setChecked(true);
    	}
    }
    
    private void doSetMode() {
    	mode_spinner.performClick();
    }
    
    public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);  
	}
	
    private void doStartBeginning() {
        try {
    		mAutoPlayInterval = Long.decode(autoPlayInterval.getText().toString().trim());
    	} catch (Exception e) {
    		mAutoPlayInterval = GameBoardOptions.DEFAUTOPLAYINTERVAL;
    	}
    	mTarget = target.getText().toString();
    	setupGameList(mTarget);
    	doNextGame();
    }
    
    private void doNextGame() {
    	if (gameList.size() == 0) {
    		Toast.makeText(ctw, getString(R.string.NoFiles), Toast.LENGTH_LONG).show();
    	} else {
        	mFileName = getNextGame();
        	grindGame (mFileName);
    	}
    }
    

	private void setupGameList (String target) {
		currentGameInx = -1;
		File path = new File(target);
		if(path.isDirectory()) {
			if (path.listFiles() == null) {
				Toast.makeText(ctw, getString(R.string.NoFiles), Toast.LENGTH_LONG).show();
            } else {
            	for (File f : path.listFiles()){
            		if (f.isFile()) {
            			String fName = f.getName();
            			String end = fName.substring(fName.lastIndexOf(".")+1).toLowerCase(getResources().getConfiguration().locale);
            	    	if(end.equals("sgf")) {
            	    		gameList.add(f.getAbsolutePath());
            	    	}
            		}
               }
            }
		} else {
			gameList.add(target);
		}
	}
	
	private String getNextGame() {
		if (gameList.size() == 0) {
			Toast.makeText(ctw, getString(R.string.NoFiles), Toast.LENGTH_LONG).show();
			return "";
		}
		currentGameInx++;
		if (currentGameInx >= gameList.size()) currentGameInx = 0;
		//String s = "getting file:" + gameList.get(currentGameInx);  // debug
		//Toast.makeText(ctw, s, Toast.LENGTH_LONG).show(); //debug
		return gameList.get(currentGameInx);
	}
	
	private void grindGame (String fileName) {
		String mode; 
    	int i = (int) mode_spinner.getSelectedItemId();
    	switch (i) {
    	case 0: mode = GameBoardOptions.AUTOPLAY; break;
    	case 1: mode = GameBoardOptions.BROWSE; break;
    	case 2: mode = GameBoardOptions.EDIT; break;
    	case 3: mode = GameBoardOptions.GUESSMOVE; break;
     	default: mode = GameBoardOptions.AUTOPLAY; break;
    	}
		Intent mIntent = new Intent(GrinderMain.this, GameBoard.class);
        mIntent.putExtra("SGF", mSGF);
        mIntent.putExtra("FILE", fileName);
        mIntent.putExtra("TIMELEFT", timeLeft);
        mIntent.putExtra("GAMEACTION",BoardManager.GA_PLAY);
		mIntent.putExtra("GAMESTATUS",BoardManager.GS_PLAY);
        mIntent.putExtra("MODE", mode);
        mIntent.putExtra("AUTOPLAYPAUSE", autoPlayPauseCB.isChecked());
        mIntent.putExtra("AUTOPLAYSOUND", autoPlaySoundCB.isChecked());
    	mIntent.putExtra("AUTOPLAYINTERVAL",mAutoPlayInterval);
   		startActivityForResult(mIntent, GRIND_VIEW);
 	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	Bundle extras;
    	switch(requestCode) {
    	case BROWSE_VIEW:
    		if (resultCode == RESULT_OK) {
    			if (data != null) {
					extras = data.getExtras();
					if (extras != null) {
						mTarget = extras.getString("TARGETNAME");
						if (mTarget == null) mTarget = "";
						target.setText(mTarget);
					}
    			}
			}
    		break;
    	case GRIND_VIEW:
    		if (resultCode == RESULT_OK) {
    			try {
	    			if (data != null) {
						extras = data.getExtras();
						if (extras != null) {
							boolean apr = extras.getBoolean("GRINDING", false);
							if (apr) {
								mFileName = getNextGame();
				            	grindGame (mFileName);
							} // otherwise just hang out
						}
	    			}
    			}  catch (Exception ignore) {
    			}
			}
    		break;
    	case HELP_VIEW:
    	default:
    		break;
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
		 final Intent helpIntent = commonStuff.helpDGS (commonStuff.HELP_GRINDER, this);
		 startActivityForResult(helpIntent, HELP_VIEW);
	 }
}
