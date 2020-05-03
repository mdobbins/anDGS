package com.hg.anDGS;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class GameBoardOptions extends DGSActivity {
	
	public static final long DEFAUTOPLAYINTERVAL = 1;
	public static final String AUTOPLAY = "autoplay";
	public static final String BROWSE = "browse";
	public static final String EDIT = "edit";
	public static final String MARKUP = "markup";
	public static final String GUESSMOVE = "guessmove";

	protected static final int DEFAULTNUMPREVPLAY = -5;
	protected static final int DEFAULTNUMPREVEDIT = -9;
	protected static final int DEFAULTMINNUMPREV = -9;
	protected static final int DEFAULTMAXNUMPREV = 99;
	protected static final int DEFAULTSKIPMOVES = 10;
	protected static final int DEFAULTMINSKIPMOVES = 2;
	protected static final int HELP_VIEW = 0;
	private static final int MENU_HELP = 0;
	protected static final int WHITE_COLOR = 0xffffffff;
	protected static final int GREEN_COLOR = 0xff88ff88;
	
	private TextView tmHelp;

	private Spinner play_mode_spinner;
	private TextView vPlayModeFlash;
	private LinearLayout play_mode_label;
	private TextView vSkipMoves;
	private TextView vSkipMovesFlash;
	private LinearLayout vSkipMovesLabel;
	private TextView vEditNumPrev;
	private TextView vEditNumPrevFlash;
	private LinearLayout vEditNumPrevLabel;
	private TextView playopts_done_button;
	private EditText vAutoPlayInterval;
	private TextView vAutoPlayIntervalFlash;
	private LinearLayout vAutoPlayIntervalLabel;
	private CheckBox autoPlayPauseCB;
	private TextView vAutoPlayPauseFlash;
	private LinearLayout autoPlayPauseLabel;
	private CheckBox autoPlaySoundCB;
	private TextView vAutoPlaySoundFlash;
	private LinearLayout autoPlaySoundLabel;
	
	private String boardLayout = PrefsDGS.PORTRAIT;
	private String theme;
	private String game_mode = BROWSE;
	private int skipMoves = DEFAULTSKIPMOVES;
	private int editNumPrev = DEFAULTNUMPREVEDIT;
	private long autoPlayInterval = DEFAUTOPLAYINTERVAL;
	private boolean autoPlayPause = true;
	private boolean autoPlaySound = false;
	private ContextThemeWrapper ctw;
	private CommonStuff commonStuff = new CommonStuff();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	game_mode = extras.getString("MODE");    
        	skipMoves = extras.getInt("SKIPMOVES", DEFAULTSKIPMOVES);
        	editNumPrev = extras.getInt("NUMPREV", DEFAULTMINNUMPREV);
            autoPlayInterval = extras.getLong("AUTOPLAYINTERVAL",DEFAUTOPLAYINTERVAL);
            autoPlayPause = extras.getBoolean("AUTOPLAYPAUSE", true);
            autoPlaySound = extras.getBoolean("AUTOPLAYSOUND", false);
            boardLayout = extras.getString("BOARDLAYOUT");
        }
        
 		SharedPreferences prefs = getSharedPreferences("MainDGS", 0);
		theme = prefs.getString("com.hg.anDGS.Theme", PrefsDGS.DEFAULT_THEME);

        if (boardLayout == null) {
			boardLayout = PrefsDGS.PORTRAIT;
		}
        if (game_mode == null ) {
        	game_mode = BROWSE;
        }
/*
		if (boardLayout.contains(PrefsDGS.LANDSCAPE)) {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
*/
		this.setTheme(commonStuff.getCommonStyle(theme));
        setContentView(R.layout.playoptions);		
        ctw = new ContextThemeWrapper(this, commonStuff.getCommonTheme(theme));
        
		tmHelp = (TextView) findViewById(R.id.playOptsTMHelp);
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
        
        playopts_done_button = (TextView) findViewById(R.id.playOptsDoneButton);
        playopts_done_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
             	Bundle rslts = new Bundle();
            	String s; 
            	int i = (int) play_mode_spinner.getSelectedItemId();
            	switch (i) {
            	case 0: s = AUTOPLAY; break;
            	case 1: s = BROWSE; break;
            	case 2: s = EDIT; break;
            	case 3: s = GUESSMOVE; break;
            	case 4: s = MARKUP; break;
            	default: s = game_mode;
            	}
            	rslts.putString("MODE", s);
            	
            	try {
            		skipMoves = Integer.decode(vSkipMoves.getText().toString().trim());
            	} catch (Exception e) {
            		skipMoves = GameBoardOptions.DEFAULTSKIPMOVES;
            	}
            	if (skipMoves < GameBoardOptions.DEFAULTMINSKIPMOVES) skipMoves = GameBoardOptions.DEFAULTMINSKIPMOVES;
				rslts.putInt("SKIPMOVES", skipMoves);
				
				try {
            		editNumPrev = Integer.decode(vEditNumPrev.getText().toString().trim());
            	} catch (Exception e) {
            		editNumPrev = GameBoardOptions.DEFAULTNUMPREVEDIT;
            	}
            	if (editNumPrev < GameBoardOptions.DEFAULTMINNUMPREV) editNumPrev = GameBoardOptions.DEFAULTMINNUMPREV;
            	if (editNumPrev > GameBoardOptions.DEFAULTMAXNUMPREV) editNumPrev = GameBoardOptions.DEFAULTMAXNUMPREV;
            	rslts.putInt("NUMPREV", editNumPrev);
				
            	try {
            		autoPlayInterval = Long.decode(vAutoPlayInterval.getText().toString().trim());
            	} catch (Exception e) {
            		autoPlayInterval = DEFAUTOPLAYINTERVAL;
            	}
            	rslts.putLong("AUTOPLAYINTERVAL",autoPlayInterval);
            	rslts.putBoolean("AUTOPLAYPAUSE", autoPlayPauseCB.isChecked());
            	rslts.putBoolean("AUTOPLAYSOUND", autoPlaySoundCB.isChecked());
                Intent mIntent = new Intent();
                mIntent.putExtras(rslts);
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });
        
        String[] items = new String[] {getString(R.string.autoplay), getString(R.string.browse), getString(R.string.edit), getString(R.string.guessmove), getString(R.string.markup)};
        play_mode_spinner = (Spinner) findViewById(R.id.playModeSpinner);
        vPlayModeFlash = (TextView) findViewById(R.id.PlayModeFlash);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        play_mode_spinner.setAdapter(adapter);
        play_mode_label = (LinearLayout) findViewById(R.id.playModeLabel);
        play_mode_label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vPlayModeFlash);
            	doSetMode();
            }
        });
        
        vSkipMoves = (TextView) findViewById(R.id.playSkipMoves);
        vSkipMovesFlash = (TextView) findViewById(R.id.playSkipMovesFlash);
        vSkipMovesLabel = (LinearLayout) findViewById(R.id.playSkipMovesLabel);
        vSkipMovesLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vSkipMovesFlash);
            	doSkipMoves();
            }
        });
        
        vEditNumPrev = (TextView) findViewById(R.id.playEditNumPrev);
        vEditNumPrevFlash = (TextView) findViewById(R.id.playEditNumPrevFlash);
        vEditNumPrevLabel = (LinearLayout) findViewById(R.id.playEditNumPrevLabel);
        vEditNumPrevLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vEditNumPrevFlash);
            	doSetEditNumPrev();
            }
        });


        vAutoPlayInterval = (EditText) findViewById(R.id.playOptsAutoPlayInterval);
        vAutoPlayIntervalFlash = (TextView) findViewById(R.id.playOptsAutoPlayIntervalFlash);
        vAutoPlayIntervalLabel = (LinearLayout) findViewById(R.id.playOptsAutoPlayIntervalLabel);
        vAutoPlayIntervalLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vAutoPlayIntervalFlash);
            	doSetAutoPlayInterval();
            }
        });
        
        autoPlayPauseCB = (CheckBox) findViewById(R.id.playOptsAutoPlayPauseCheckBox);
        vAutoPlayPauseFlash = (TextView) findViewById(R.id.playOptsAutoPlayPauseFlash);
        autoPlayPauseLabel = (LinearLayout) findViewById(R.id.playOptsAutoPlayPauseLabel);
        autoPlayPauseLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vAutoPlayPauseFlash);
            	doSetAutoPlayPause();
            }
        });
        
        autoPlaySoundCB = (CheckBox) findViewById(R.id.playOptsAutoPlaySoundCheckBox);
        vAutoPlaySoundFlash = (TextView) findViewById(R.id.playOptsAutoPlaySoundFlash);
        autoPlaySoundLabel = (LinearLayout) findViewById(R.id.playOptsAutoPlaySoundLabel);
        autoPlaySoundLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vAutoPlaySoundFlash);
            	doSetAutoPlaySound();
            }
        });
        
        if (game_mode.contentEquals(MARKUP)) {
     	   play_mode_spinner.setSelection(4);
        } else if (game_mode.contentEquals(GUESSMOVE)) {
      	   play_mode_spinner.setSelection(3);
        } else if (game_mode.contentEquals(EDIT)) {
     	   play_mode_spinner.setSelection(2);
        } else if (game_mode.contentEquals(BROWSE)) {
     	   play_mode_spinner.setSelection(1);
        } else if (game_mode.contentEquals(AUTOPLAY)) {
     	   play_mode_spinner.setSelection(0);	
        } else {
     	   play_mode_spinner.setSelection(1);
        }
        vSkipMoves.setText(Integer.toString(skipMoves));
        vEditNumPrev.setText(Integer.toString(editNumPrev));
        vAutoPlayInterval.setText(Long.toString(autoPlayInterval));
        autoPlayPauseCB.setChecked(autoPlayPause);
        autoPlaySoundCB.setChecked(autoPlaySound);
   }
    
    private void flashButton(View v) {
    	v.setVisibility(View.INVISIBLE);  // v.setBackgroundColor(MainDGS.LIGHT_GREY_COLOR);
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
    
    private void doSkipMoves() {
    	final EditText input = new EditText(ctw);
    	input.setInputType(InputType.TYPE_CLASS_NUMBER);
	    input.setText(vSkipMoves.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.prefsSkipMoves)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	try {
        		skipMoves = Integer.decode(input.getText().toString().trim());
        	} catch (Exception e) {
        		skipMoves = GameBoardOptions.DEFAULTSKIPMOVES;
        	}
	    	if (skipMoves < GameBoardOptions.DEFAULTMINSKIPMOVES) skipMoves = GameBoardOptions.DEFAULTMINSKIPMOVES;
	    	vSkipMoves.setText(Integer.toString(skipMoves));
	    }})
		.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}})
	    .show();
    }
    
    private void doSetEditNumPrev() {
    	final EditText input = new EditText(ctw);
    	input.setInputType(InputType.TYPE_CLASS_NUMBER);
	    input.setText(vEditNumPrev.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.prefsEditNumPrev)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	try {
        		editNumPrev = Integer.decode(input.getText().toString().trim());
        	} catch (Exception e) {
        		editNumPrev = GameBoardOptions.DEFAULTNUMPREVEDIT;
        	}
	    	if (editNumPrev < GameBoardOptions.DEFAULTMINNUMPREV) editNumPrev = GameBoardOptions.DEFAULTMINNUMPREV;
	    	if (editNumPrev > GameBoardOptions.DEFAULTMAXNUMPREV) editNumPrev = GameBoardOptions.DEFAULTMAXNUMPREV;
	    	vEditNumPrev.setText(Integer.toString(editNumPrev));
	    }})
		.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}})
	    .show();
    }
    
    private void doSetAutoPlayInterval() { 
    	final EditText input = new EditText(ctw);
    	input.setInputType(InputType.TYPE_CLASS_NUMBER);
	    input.setText(vAutoPlayInterval.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.autoPlayInterval)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	vAutoPlayInterval.setText(input.getText());
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
    	play_mode_spinner.performClick();
    }
    
    public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);  
	}
    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch  (keyCode) {
		 case KeyEvent.KEYCODE_BACK: {
			 return true;
		 	}
		 default: {
			 //
		 	}
		 }
		return super.onKeyDown(keyCode, event);
	}
	
	public boolean onKeyUp (int keyCode, KeyEvent event) {
		switch  (keyCode) {
		 case KeyEvent.KEYCODE_BACK: {
			if (event.getKeyCode() == KeyEvent.FLAG_EDITOR_ACTION) return true;
			Bundle rslts = new Bundle();
			// no results
			Intent mIntent = new Intent();
			mIntent.putExtras(rslts);
			setResult(RESULT_OK, mIntent);
			finish();
			return true;
		 	}
		 default: {
			 //
		 	}
		 }
		return super.onKeyUp(keyCode, event);
	}
   
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	switch(requestCode) {
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
		 final Intent helpIntent = commonStuff.helpDGS (commonStuff.HELP_PLAY_OPTIONS, this);
		 helpIntent.putExtra("BOARDLAYOUT", boardLayout);
		 startActivityForResult(helpIntent, HELP_VIEW); 
	 }

}
