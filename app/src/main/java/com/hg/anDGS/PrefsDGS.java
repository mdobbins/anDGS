package com.hg.anDGS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import yuku.ambilwarna.AmbilWarnaDialog;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PrefsDGS extends DGSActivity {
	public static final String PORTRAIT = "portrait";
	public static final String LANDSCAPE = "landscape";
	public static final String DYNAMIC = "dynamic";
	public static final String NOSOUND = "none";
	public static final String DEFAULTSOUND = "default";
	public static final String STONESOUND = "stone";
	public static final String ZOOM7X7 = "Zoom7X7";
	public static final String ZOOM9X9 = "Zoom9X9";
	public static final String ZOOM11X11 = "Zoom11X11";
	public static final String ZOOM13X13 = "Zoom13X13";
	public static final String DPADCONTROL = "Dpad";
	public static final String ONETOUCH = "Touch1";
	public static final String SLIDE = "Slide10pct";
	public static final String NO_COORD = "NO";
	public static final String EDIT_COORD = "EDIT";
	public static final String PLAY_COORD = "PLAY";
	public static final String ALL_COORD = "ALL";
	public static final String BG_WHITE = "WHITE";
	public static final String BG_PLAIN = "PLAIN";
	public static final String BG_WOOD = "WOOD";
	public static final String BG_CUSTOM = "CUSTOM";
	public static final String STONE_MONO = "MONO";
	public static final String STONE_CLAM = "CLAM";
	public static final String DGS_URL = "https://www.dragongoserver.net/";
	// public static final String DEFAULT_DIR = "sgfs";
	public static final String BLACKONWHITE = "BlackOnWhite";  // obsolete
	public static final String WHITEONBLACK = "WhiteOnBlack";
	public static final String WHITEONGREEN = "WhiteOnGreen";
    public static final String GREENONWHITE = "GreenOnWhite";
	public static final String BLUEONWHITE = "BlueOnWhite";   // obsolete
	public static final String WHITEONBLUE = "WhiteOnBlue";
	public static final String METALLICGREY = "MetallicGrey";
    public static final String METALLICGREYINVERSE = "MetallicGreyInverse";
	public static final String DEFAULT_THEME = METALLICGREY;
	public final String DEFAULT_LINE_WIDTH = "1.0";

	private TextView tmHelp;

	private TextView vUser;
	private TextView vUserFlash;
	private LinearLayout vUserLabel;
	private TextView vPasswrd;
	private TextView vPasswrdFlash;
	private LinearLayout vPasswrdLabel;
	private TextView prefs_layout_text;
	private TextView vLayoutFlash;
	private LinearLayout prefs_layout_Label;
	private String[] layout_items;
	private TextView prefs_theme_text;
	private TextView vThemeFlash;
	private LinearLayout prefs_theme_Label;
	private String[] theme_items;
	private TextView prefs_locale_text;
	private TextView vLocaleFlash;
	private LinearLayout prefs_locale_Label;
	private TextView prefs_move_control_text;
	private TextView vMoveControlFlash;
	private LinearLayout prefs_move_control_Label;
	private String[] move_control_items;
	private TextView boardCoord_text;
	private TextView vBoardCoordFlash;
	private LinearLayout boardCoordLabel;
	private String[] boardCoord_items;
	private ImageView boardBG_image;
	private TextView boardBG_text;
	private TextView vBoardBGFlash;
	private LinearLayout boardBGLabel;
	private String[] boardBG_items;
	private ImageView boardStone_whiteImage;
	private ImageView boardStone_blackImage;
	private TextView boardStone_text;
	private TextView vBoardStoneFlash;
	private LinearLayout boardStoneLabel;
	private String[] boardStone_items;
	private TextView prefs_editMode_text;
	private TextView vEditModeFlash;
	private LinearLayout prefs_editMode_Label;
	private String[] editMode_items;
	private TextView prefs_gameOrder_text;
	private TextView vGameOrderFlash;
	private LinearLayout prefs_gameOrder_Label;
	private String[] gameOrder_items;
	private TextView vSkipMoves;
	private TextView vSkipMovesFlash;
	private LinearLayout vSkipMovesLabel;
	private TextView vEditNumPrev;
	private TextView vEditNumPrevFlash;
	private LinearLayout vEditNumPrevLabel;
	private TextView vPlayNumPrev;
	private TextView vPlayNumPrevFlash;	
	private CheckBox editDisplayTopMenuCB;
	private TextView vEditDisplayTopMenuFlash;
	private LinearLayout editDisplayTopMenuLabel;
	private CheckBox playDisplayTopMenuCB;
	private TextView vPlayDisplayTopMenuFlash;
	private LinearLayout playDisplayTopMenuLabel;
	private CheckBox playGameNotesCB;
	private TextView vPlayGameNotesFlash;
	private LinearLayout playGameNotesLabel;
	private LinearLayout vPlayNumPrevLabel;
	private TextView prefs_sound_text;
	private TextView vSoundFlash;
	private LinearLayout prefs_sound_Label;
	private String[] sound_items;
	private CheckBox autoClientCB;
	private TextView vAutoClientFlash;
	private LinearLayout autoClientLabel;
	private CheckBox autoStartNotifierCB;
	private TextView vAutoStartNotifierFlash;
	private LinearLayout autoStartNotifierLabel;
	private TextView vNotifierInterval;
	private TextView vNotifierIntervalFlash;
	private LinearLayout vNotifierIntervalLabel;
	private CheckBox notifierVibrateCB;
	private TextView vNotifierVibrateFlash;
	private LinearLayout notifierVibrateLabel;
	private CheckBox notifyAllDetailsCB;
	private TextView vNotifyAllDetailsFlash;
	private LinearLayout notifyAllDetailsLabel;
	private TextView vAutoPlayInterval;
	private TextView vAutoPlayIntervalFlash;
	private LinearLayout vAutoPlayIntervalLabel;
	private CheckBox keepScreenOnCB;
	private TextView keepScreenOnFlash;
	private LinearLayout keepScreenOnLabel;
	private CheckBox autoPlayPauseCB;
	private TextView vAutoPlayPauseFlash;
	private LinearLayout autoPlayPauseLabel;
	private CheckBox autoPlaySoundCB;
	private TextView vAutoPlaySoundFlash;
	private LinearLayout autoPlaySoundLabel;
	private TextView vDefaultDir;
	private TextView vDefaultDirFlash;
	private LinearLayout vDefaultDirLabel;
	private TextView vServerURL;
	private TextView vServerURLFlash;
	private LinearLayout vServerURLLabel;
	private TextView vScaleLines;
	private TextView vScaleLinesFlash;
	private LinearLayout vScaleLinesLabel;
	private TextView vDBG;
	private TextView vDBGFlash;
	private LinearLayout vDBGLabel;
	protected static final int HELP_VIEW = 0;
	private static final int MENU_HELP = 0;
	private TextView prefs_done_button;
	private String boardLayout = PORTRAIT;
	private String currentBoardLayout = PORTRAIT;
	private String theme = BLACKONWHITE;
	private String myLocale = "";
	private int locale_index = 0;
	private String[] locale_items;
	private ArrayList<String> language_items = new ArrayList<>();
    private String user = "";
    private String passwd = "";
    private String serverURL = DGS_URL;
    private String sgfDir = "/bad init";  // TO DO cleanup - remove
	private String moveControl = ZOOM7X7;
	private String boardCoordTxt = NO_COORD;
	private String boardBGTxt = BG_WOOD;
	private String boardStoneTxt = STONE_CLAM;
	private String editMode = GameBoardOptions.BROWSE;
	private String gameOrder = MainDGS.GO_NOORDER;
	private String makeSound = NOSOUND;
	private long notifierInterval = DGSNotifier.DEFNOTIFIERINTERVAL;
	private boolean notifierChanged = false;
	private int skipMoves = GameBoardOptions.DEFAULTSKIPMOVES;
	private int customBGvalue = MainDGS.LIGHT_GREY_COLOR;
	private int editNumPrev = GameBoardOptions.DEFAULTNUMPREVEDIT;
	private int playNumPrev = GameBoardOptions.DEFAULTNUMPREVPLAY;
	private long autoPlayInterval = GameBoardOptions.DEFAUTOPLAYINTERVAL;
	private float scaleLines = 1;
	private int dbg = 0;
	private ContextThemeWrapper ctw;
	private CommonStuff commonStuff = new CommonStuff();
	private CommonFileStuff commonFileStuff = new CommonFileStuff();
	
    /** Called when the activity is first created. */
    @SuppressLint("SourceLockedOrientationActivity")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	currentBoardLayout = extras.getString("BOARDLAYOUT");
        }
        
		SharedPreferences prefs = getSharedPreferences("MainDGS", 0);
		user = prefs.getString("com.hg.anDGS.DGSUser", "");
		passwd = prefs.getString("com.hg.anDGS.DGSPass", "");
		if (passwd.length() > 16) passwd = passwd.substring(0, 16);
		sgfDir = commonFileStuff.getSgfDirName(); //prefs.getString("com.hg.anDGS.DefaultDir", DEFAULT_DIR);
		serverURL = prefs.getString("com.hg.anDGS.ServerURL", DGS_URL);
		boardLayout = prefs.getString("com.hg.anDGS.BoardLayout", DYNAMIC);
		theme = prefs.getString("com.hg.anDGS.Theme", DEFAULT_THEME);
		myLocale = prefs.getString("com.hg.anDGS.Locale", "");
		moveControl = prefs.getString("com.hg.anDGS.MoveControl", ZOOM7X7);
		boardCoordTxt = prefs.getString("com.hg.anDGS.BoardCoord", NO_COORD);
		boardBGTxt = prefs.getString("com.hg.anDGS.BoardBackground", BG_WOOD);
		customBGvalue = prefs.getInt("com.hg.anDGS.CustomBoardBackground", MainDGS.LIGHT_GREY_COLOR);
		boardStoneTxt = prefs.getString("com.hg.anDGS.BoardStone", STONE_CLAM);
		boolean keepScreenOn = prefs.getBoolean("com.hg.anDGS.KeepScreenOn", true);
		boolean autoPlay = prefs.getBoolean("com.hg.anDGS.AutoPlay", false);
		boolean autoStartNotifier = prefs.getBoolean("com.hg.anDGS.AutoStartNotifier", false);
		boolean autoPlayPause = prefs.getBoolean("com.hg.anDGS.AutoPlayPause", true);
		boolean autoPlaySound = prefs.getBoolean("com.hg.anDGS.AutoPlaySound", false);
		notifierInterval = prefs.getLong("com.hg.anDGS.Interval", DGSNotifier.DEFNOTIFIERINTERVAL);
		makeSound = prefs.getString("com.hg.anDGS.Sound", STONESOUND);
		editMode = prefs.getString("com.hg.anDGS.DefaultEditMode", GameBoardOptions.EDIT);
		gameOrder = prefs.getString("com.hg.anDGS.GameOrder", MainDGS.GO_NOORDER);
		boolean notifierVibrate = prefs.getBoolean("com.hg.anDGS.Vibrate", false);
		boolean notifyAllDetails = prefs.getBoolean("com.hg.anDGS.NotifyFailure", false);
		boolean notifyPermanent = prefs.getBoolean("com.hg.anDGS.NotifyPermanent", false);
		autoPlayInterval = prefs.getLong("com.hg.anDGS.AutoPlayInterval", GameBoardOptions.DEFAUTOPLAYINTERVAL);
		skipMoves = prefs.getInt("com.hg.anDGS.SkipMoves", GameBoardOptions.DEFAULTSKIPMOVES);
		editNumPrev = prefs.getInt("com.hg.anDGS.EditNumPrev", GameBoardOptions.DEFAULTNUMPREVEDIT);
		playNumPrev = prefs.getInt("com.hg.anDGS.PlayNumPrev", GameBoardOptions.DEFAULTNUMPREVPLAY);
		boolean editDisplayTopMenu = prefs.getBoolean("com.hg.anDGS.EditDisplayTopMenu", true);
		boolean playDisplayTopMenu = prefs.getBoolean("com.hg.anDGS.PlayDisplayTopMenu", true);
		boolean playGameNotes = prefs.getBoolean("com.hg.anDGS.GameNotes", false);
		scaleLines = prefs.getFloat("com.hg.anDGS.ScaleLines", 1);
		dbg = prefs.getInt("com.hg.anDGS.Debug", 0);

        if (myLocale == null) {
        	Configuration config = getBaseContext().getResources().getConfiguration();
        	myLocale = config.locale.getLanguage();
        }
        // backward compatibility issues
		if (boardLayout.contentEquals("small")) {
			boardLayout = PORTRAIT;
			if (moveControl.contains("Zoom")) {
				moveControl = ZOOM7X7;
			}
		}
		if (boardLayout.contentEquals("large")) {
			boardLayout = PORTRAIT;
			if (moveControl.contains("Zoom")) {
				moveControl = ZOOM9X9;
			}
		}
		if (boardLayout.contentEquals("large7")) {
			boardLayout = PORTRAIT;
			if (moveControl.contains("Zoom")) {
				moveControl = ZOOM7X7;
			}
		}
		if (boardLayout.contentEquals("landscape9")) {
			boardLayout = LANDSCAPE;
			if (moveControl.contains("Zoom")) {
				moveControl = ZOOM9X9;
			}
		}
		if (boardLayout.contentEquals("landscape7")) {
			boardLayout = LANDSCAPE;
			if (moveControl.contains("Zoom")) {
				moveControl = ZOOM7X7;
			}
		}
        
        this.setTheme(commonStuff.getCommonStyle(theme));
/*
        if (currentBoardLayout.contains(LANDSCAPE)) {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
 */
        setContentView(R.layout.prefs);
		ctw = new ContextThemeWrapper(this, commonStuff.getCommonTheme(theme));

		tmHelp = findViewById(R.id.prefsTMHelp);
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
        
        prefs_done_button = findViewById(R.id.prefsDoneButton);
        prefs_done_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SharedPreferences.Editor editor = getSharedPreferences("MainDGS", 0).edit();
				editor.putString("com.hg.anDGS.DGSUser", vUser.getText().toString().trim());
				editor.putString("com.hg.anDGS.DGSPass", passwd.trim());
				editor.putString("com.hg.anDGS.DefaultDir", vDefaultDir.getText().toString().trim());
				editor.putString("com.hg.anDGS.ServerURL", vServerURL.getText().toString().trim());

				editor.putString("com.hg.anDGS.BoardLayout", boardLayout);
				
				editor.putString("com.hg.anDGS.Theme", theme);
				
				editor.putString("com.hg.anDGS.Locale", locale_items[locale_index]);
				
				editor.putString("com.hg.anDGS.MoveControl", moveControl);
				
				editor.putString("com.hg.anDGS.BoardCoord", boardCoordTxt);
				
				editor.putString("com.hg.anDGS.BoardBackground", boardBGTxt);
				editor.putInt("com.hg.anDGS.CustomBoardBackground", customBGvalue);
				
				editor.putString("com.hg.anDGS.BoardStone", boardStoneTxt);
				
            	editor.putBoolean("com.hg.anDGS.KeepScreenOn", keepScreenOnCB.isChecked());
				editor.putBoolean("com.hg.anDGS.AutoPlay", autoClientCB.isChecked());
				editor.putBoolean("com.hg.anDGS.AutoStartNotifier", autoStartNotifierCB.isChecked());
				editor.putBoolean("com.hg.anDGS.AdsEnabled", false); 
				editor.putBoolean("com.hg.anDGS.AutoPlayPause", autoPlayPauseCB.isChecked());
				editor.putBoolean("com.hg.anDGS.AutoPlaySound", autoPlaySoundCB.isChecked());
				editor.putBoolean("com.hg.anDGS.EditDisplayTopMenu", editDisplayTopMenuCB.isChecked());
				editor.putBoolean("com.hg.anDGS.PlayDisplayTopMenu", playDisplayTopMenuCB.isChecked());
				editor.putBoolean("com.hg.anDGS.GameNotes", playGameNotesCB.isChecked());

				try {
            		skipMoves = Integer.decode(vSkipMoves.getText().toString().trim());
            	} catch (Exception e) {
            		skipMoves = GameBoardOptions.DEFAULTSKIPMOVES;
            	}
            	if (skipMoves < GameBoardOptions.DEFAULTMINSKIPMOVES) skipMoves = GameBoardOptions.DEFAULTMINSKIPMOVES;
				editor.putInt("com.hg.anDGS.SkipMoves", skipMoves);
				
				try {
            		editNumPrev = Integer.decode(vEditNumPrev.getText().toString().trim());
            	} catch (Exception e) {
            		editNumPrev = GameBoardOptions.DEFAULTNUMPREVEDIT;
            	}
            	if (editNumPrev < GameBoardOptions.DEFAULTMINNUMPREV) editNumPrev = GameBoardOptions.DEFAULTMINNUMPREV;
            	if (editNumPrev > GameBoardOptions.DEFAULTMAXNUMPREV) editNumPrev = GameBoardOptions.DEFAULTMAXNUMPREV;
				editor.putInt("com.hg.anDGS.EditNumPrev", editNumPrev);
				
				try {
            		playNumPrev = Integer.decode(vPlayNumPrev.getText().toString().trim());
            	} catch (Exception e) {
            		playNumPrev = GameBoardOptions.DEFAULTNUMPREVPLAY;
            	}
            	if (playNumPrev < GameBoardOptions.DEFAULTMINNUMPREV) playNumPrev = GameBoardOptions.DEFAULTMINNUMPREV;
            	if (playNumPrev > GameBoardOptions.DEFAULTMAXNUMPREV) playNumPrev = GameBoardOptions.DEFAULTMAXNUMPREV;
				editor.putInt("com.hg.anDGS.PlayNumPrev", playNumPrev);

				try {
            		notifierInterval = Long.decode(vNotifierInterval.getText().toString().trim());
            	} catch (Exception e) {
            		notifierInterval = DGSNotifier.DEFNOTIFIERINTERVAL;
            	}
            	if (notifierInterval < DGSNotifier.MINNOTIFIERINTERVAL) notifierInterval = DGSNotifier.MINNOTIFIERINTERVAL;
				editor.putLong("com.hg.anDGS.Interval", notifierInterval);
				
				editor.putString("com.hg.anDGS.DefaultEditMode", editMode);
				
				editor.putString("com.hg.anDGS.GameOrder", gameOrder);
				
				editor.putString("com.hg.anDGS.Sound", makeSound);
				
				editor.putBoolean("com.hg.anDGS.Vibrate", notifierVibrateCB.isChecked());
				editor.putBoolean("com.hg.anDGS.NotifyFailure", notifyAllDetailsCB.isChecked());
				
				try {
            		autoPlayInterval = Long.decode(vAutoPlayInterval.getText().toString().trim());
            	} catch (Exception e) {
            		autoPlayInterval = GameBoardOptions.DEFAUTOPLAYINTERVAL;
            	}
				editor.putLong("com.hg.anDGS.AutoPlayInterval", autoPlayInterval);
				
				try {
					scaleLines = Float.parseFloat(vScaleLines.getText().toString().trim());
            	} catch (Exception e) {
            		scaleLines = 1;
            	}
				editor.putFloat("com.hg.anDGS.ScaleLines", scaleLines);
				editor.putInt("com.hg.anDGS.Debug", dbg);
				
				editor.apply();
             	
            	Bundle rslts = new Bundle();
				rslts.putBoolean("NOTIFIERCHANGED", notifierChanged);
            	Intent mIntent = new Intent();
                mIntent.putExtras(rslts);
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });
        
        autoClientCB = findViewById(R.id.prefsAutoClientCheckBox);
        vAutoClientFlash = findViewById(R.id.prefsAutoClientFlash);
        autoClientLabel = findViewById(R.id.prefsAutoClientLabel);
        autoClientLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vAutoClientFlash);
            	doSetAutoClient();
            }
        });

		autoStartNotifierCB = findViewById(R.id.prefsAutoStartNotifierCheckBox);
		vAutoStartNotifierFlash = findViewById(R.id.prefsAutoStartNotifierFlash);
		autoStartNotifierLabel = findViewById(R.id.prefsAutoStartNotifierLabel);
		autoStartNotifierLabel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				flashButton(vAutoStartNotifierFlash);
				doSetAutoStartNotifier();
			}
		});
        
        keepScreenOnCB = findViewById(R.id.prefsKeepScreenOnCheckBox);
        keepScreenOnFlash = findViewById(R.id.prefsKeepScreenOnFlash);
        keepScreenOnLabel = findViewById(R.id.prefsKeepScreenOnLabel);
        keepScreenOnLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(keepScreenOnFlash);
            	doSetKeepScreenOn();
            }
        });
        
        autoPlayPauseCB = findViewById(R.id.prefsAutoPlayPauseCheckBox);
        vAutoPlayPauseFlash = findViewById(R.id.prefsAutoPlayPauseFlash);
        autoPlayPauseLabel = findViewById(R.id.prefsAutoPlayPauseLabel);
        autoPlayPauseLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vAutoPlayPauseFlash);
            	doSetAutoPlayPause();
            }
        });
        
        autoPlaySoundCB = findViewById(R.id.prefsAutoPlaySoundCheckBox);
        vAutoPlaySoundFlash = findViewById(R.id.prefsAutoPlaySoundFlash);
        autoPlaySoundLabel = findViewById(R.id.prefsAutoPlaySoundLabel);
        autoPlaySoundLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vAutoPlaySoundFlash);
            	doSetAutoPlaySound();
            }
        });
        
        notifierVibrateCB = findViewById(R.id.prefsDGSNotifierVibrate);
        vNotifierVibrateFlash = findViewById(R.id.prefsDGSNotifierVibrateFlash);
        notifierVibrateLabel = findViewById(R.id.prefsDGSNotifierVibrateLabel);
        notifierVibrateLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vNotifierVibrateFlash);
            	doSetNotifierVibrate();
            }
        });
        
        notifyAllDetailsCB = findViewById(R.id.prefsDGSNotifyAllDetails);
        vNotifyAllDetailsFlash = findViewById(R.id.prefsDGSNotifyAllDetailsFlash);
        notifyAllDetailsLabel = findViewById(R.id.prefsDGSNotifyAllDetailsLabel);
        notifyAllDetailsLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vNotifyAllDetailsFlash);
            	doSetNotifyAllDetails();
            }
        });
        
        editDisplayTopMenuCB = findViewById(R.id.prefsEditDisplayTopMenuCheckBox);
        vEditDisplayTopMenuFlash = findViewById(R.id.prefsEditDisplayTopMenuFlash);
        editDisplayTopMenuLabel = findViewById(R.id.prefsEditDisplayTopMenuLabel);
        editDisplayTopMenuLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vEditDisplayTopMenuFlash);
            	doSetEditDisplayTopMenu();
            }
        });
        
        playDisplayTopMenuCB = findViewById(R.id.prefsPlayDisplayTopMenuCheckBox);
        vPlayDisplayTopMenuFlash = findViewById(R.id.prefsPlayDisplayTopMenuFlash);
        playDisplayTopMenuLabel = findViewById(R.id.prefsPlayDisplayTopMenuLabel);
        playDisplayTopMenuLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vPlayDisplayTopMenuFlash);
            	doSetPlayDisplayTopMenu();
            }
        });
        
        playGameNotesCB = findViewById(R.id.prefsPlayGameNotesCheckBox);
        vPlayGameNotesFlash = findViewById(R.id.prefsPlayGameNotesFlash);
        playGameNotesLabel = findViewById(R.id.prefsPlayGameNotesLabel);
        playGameNotesLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vPlayGameNotesFlash);
            	doSetPlayGameNotes();
            }
        });
        
        layout_items = new String[] {getString(R.string.portrait), getString(R.string.landscape), getString(R.string.dynamic), getString(R.string.Cancel)};
        prefs_layout_text = findViewById(R.id.prefsLayoutText);
        vLayoutFlash = findViewById(R.id.prefsLayoutFlash);
        prefs_layout_Label = findViewById(R.id.prefsLayoutLabel);
        prefs_layout_Label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vLayoutFlash);
            	doSetLayout();
            }
        });
        
        locale_items = getResources().getStringArray(R.array.my_locales_array);
        String [] l_i = getResources().getStringArray(R.array.my_languages_array);
		language_items.addAll(Arrays.asList(l_i));
        language_items.add(getString(R.string.Cancel));
        prefs_locale_text = findViewById(R.id.prefsLocaleText);
        vLocaleFlash = findViewById(R.id.prefsLocaleFlash);
        prefs_locale_Label = findViewById(R.id.prefsLocaleLabel);
        prefs_locale_Label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vLocaleFlash);
            	doSetLocale();
            }
        });
        
        theme_items = new String[] {getString(R.string.WhiteOnBlack),
                getString(R.string.WhiteOnBlue),
				getString(R.string.WhiteOnGreen),
                getString(R.string.MetallicGrey),
                getString(R.string.BlackOnWhite),
                getString(R.string.BlueOnWhite),
                getString(R.string.GreenOnWhite),
                getString(R.string.MetallicGreyInverse),
				getString(R.string.Cancel)};
        prefs_theme_text = findViewById(R.id.prefsThemeText);
        vThemeFlash = findViewById(R.id.prefsThemeFlash);
        prefs_theme_Label = findViewById(R.id.prefsThemeLabel);
        prefs_theme_Label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vThemeFlash);
            	doSetTheme();
            }
        });

        move_control_items = new String[] {getString(R.string.Zoom7x7), getString(R.string.Zoom9x9), getString(R.string.Zoom11x11), getString(R.string.Zoom13x13), getString(R.string.Dpad), getString(R.string.oneTouch), getString(R.string.Slide), getString(R.string.Cancel)};
        prefs_move_control_text = findViewById(R.id.prefsMoveControlText);
        vMoveControlFlash = findViewById(R.id.prefsMoveControlFlash);
        prefs_move_control_Label = findViewById(R.id.prefsMoveControlLabel);
        prefs_move_control_Label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vMoveControlFlash);
            	doSetMoveControl();
            }
        });
        
        boardCoord_items = new String[] {getString(R.string.no), getString(R.string.edit), getString(R.string.play), getString(R.string.all), getString(R.string.Cancel)};
        boardCoord_text = findViewById(R.id.prefsBoardCoordText);
        vBoardCoordFlash = findViewById(R.id.prefsBoardCoordFlash);
        boardCoordLabel = findViewById(R.id.prefsBoardCoordLabel);
        boardCoordLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vBoardCoordFlash);
            	doSetBoardCoord();
            }
        });
        
        boardBG_items = new String[] {getString(R.string.white), getString(R.string.Plain), getString(R.string.Wood), getString(R.string.Custom), getString(R.string.Cancel)};
        boardBG_image = findViewById(R.id.prefsBoardBGImage);
        boardBG_text = findViewById(R.id.prefsBoardBGText);
        vBoardBGFlash = findViewById(R.id.prefsBoardBGFlash);
        boardBGLabel = findViewById(R.id.prefsBoardBGLabel);
        boardBGLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vBoardBGFlash);
            	doSetBoardBG();
            }
        });

        boardStone_items = new String[] {getString(R.string.monochrome), getString(R.string.clamshell), getString(R.string.Cancel) };
        boardStone_whiteImage = findViewById(R.id.prefsBoardStoneWhiteImage);
        boardStone_blackImage = findViewById(R.id.prefsBoardStoneBlackImage);
        boardStone_text = findViewById(R.id.prefsBoardStoneText);
        vBoardStoneFlash = findViewById(R.id.prefsBoardStoneFlash);
        boardStoneLabel = findViewById(R.id.prefsBoardStoneLabel);
        boardStoneLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vBoardStoneFlash);
            	doSetBoardStone();
            }
        });

        editMode_items = new String[] {getString(R.string.autoplay), getString(R.string.browse), getString(R.string.edit), getString(R.string.guessmove), getString(R.string.markup), getString(R.string.Cancel) };
        prefs_editMode_text = findViewById(R.id.prefsEditModeText);
        vEditModeFlash = findViewById(R.id.prefsEditModeFlash);
    	prefs_editMode_Label = findViewById(R.id.prefsEditModeLabel);
        prefs_editMode_Label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vEditModeFlash);
            	doSetEditMode();
            }
        });
        
        gameOrder_items = new String[] {getString(R.string.statusPage), getString(R.string.noOrder), getString(R.string.lastMoved), getString(R.string.moves), getString(R.string.priority), getString(R.string.timeLeft), getString(R.string.Cancel) };
        prefs_gameOrder_text = findViewById(R.id.prefsGameOrderText);
        vGameOrderFlash = findViewById(R.id.prefsGameOrderFlash);
    	prefs_gameOrder_Label = findViewById(R.id.prefsGameOrderLabel);
        prefs_gameOrder_Label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vGameOrderFlash);
            	doSetGameOrder();
            }
        });

    	sound_items = new String[] {getString(R.string.none), getString(R.string.deflt), getString(R.string.stone), getString(R.string.Cancel) };
        prefs_sound_text = findViewById(R.id.prefsSoundText);
        vSoundFlash = findViewById(R.id.prefsSoundFlash);
     	prefs_sound_Label = findViewById(R.id.prefsSoundLabel);
        prefs_sound_Label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vSoundFlash);
            	doSetSound();
            }
        });
        
        vUser = findViewById(R.id.prefsDGSUser);
        vUserFlash = findViewById(R.id.prefsDGSUserFlash);
        vUserLabel = findViewById(R.id.prefsDGSUserLabel);
        vUserLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vUserFlash);
            	doSetUser();
            }
        });
        
        vPasswrd = findViewById(R.id.prefsDGSPassword);
        vPasswrdFlash = findViewById(R.id.prefsDGSPasswordFlash);
        vPasswrdLabel = findViewById(R.id.prefsDGSPasswordLabel);
        vPasswrdLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vPasswrdFlash);
            	doSetPasswrd();
            }
        });
        
        vSkipMoves = findViewById(R.id.prefsDGSSkipMoves);
        vSkipMovesFlash = findViewById(R.id.prefsDGSSkipMovesFlash);
        vSkipMovesLabel = findViewById(R.id.prefsDGSSkipMovesLabel);
        vSkipMovesLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vSkipMovesFlash);
            	doSkipMoves();
            }
        });
        
        vEditNumPrev = findViewById(R.id.prefsDGSEditNumPrev);
        vEditNumPrevFlash = findViewById(R.id.prefsDGSEditNumPrevFlash);
        vEditNumPrevLabel = findViewById(R.id.prefsDGSEditNumPrevLabel);
        vEditNumPrevLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vEditNumPrevFlash);
            	doSetEditNumPrev();
            }
        });
        
        vPlayNumPrev = findViewById(R.id.prefsDGSPlayNumPrev);
        vPlayNumPrevFlash = findViewById(R.id.prefsDGSPlayNumPrevFlash);
        vPlayNumPrevLabel = findViewById(R.id.prefsDGSPlayNumPrevLabel);
        vPlayNumPrevLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vPlayNumPrevFlash);
            	doSetPlayNumPrev();
            }
        });
        
        vNotifierInterval = findViewById(R.id.prefsDGSNotifierInterval);
        vNotifierIntervalFlash = findViewById(R.id.prefsDGSNotifierIntervalFlash);
        vNotifierIntervalLabel = findViewById(R.id.prefsDGSNotifierIntervalLabel);
        vNotifierIntervalLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vNotifierIntervalFlash);
            	doSetNotifierInterval();
            }
        });
        
        vAutoPlayInterval = findViewById(R.id.prefsAutoPlayInterval);
        vAutoPlayIntervalFlash = findViewById(R.id.prefsAutoPlayIntervalFlash);
        vAutoPlayIntervalLabel = findViewById(R.id.prefsAutoPlayIntervalLabel);
        vAutoPlayIntervalLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vAutoPlayIntervalFlash);
            	doSetAutoPlayInterval();
            }
        });
        
        vDefaultDir = findViewById(R.id.prefsDefaultDir);
        vDefaultDirFlash = findViewById(R.id.prefsDefaultDirFlash);
        vDefaultDirLabel = findViewById(R.id.prefsDefaultDirLabel);
        vDefaultDirLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vDefaultDirFlash);
            	doSetDefaultDir();
            }
        });
        
        vServerURL = findViewById(R.id.prefsDGSServerURL);
        vServerURLFlash = findViewById(R.id.prefsDGSServerURLFlash);
        vServerURLLabel = findViewById(R.id.prefsDGSServerURLLabel);
        vServerURLLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vServerURLFlash);
            	doSetServerURL();
            }
        });
        
        vScaleLines = findViewById(R.id.prefsScaleLines);
        vScaleLinesFlash = findViewById(R.id.prefsScaleLinesFlash);
        vScaleLinesLabel = findViewById(R.id.prefsScaleLinesLabel);
        vScaleLinesLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	flashButton(vScaleLinesFlash);
            	doSetLineWidth();
            }
        });

		vDBG = findViewById(R.id.prefsDGSdbg);
		vDBGFlash = findViewById(R.id.prefsDGSdbgFlash);
		vDBGLabel = findViewById(R.id.prefsDGSdbgLabel);
		vDBGLabel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				flashButton(vDBGFlash);
				doSetDBG();
			}
		});

        setBoardCoord();
       
        setBoardBGvalue();
        
        setBoardStonevalue();
        
        vUser.setText(user);
        vPasswrd.setText(starString(passwd));
        vDefaultDir.setText(sgfDir);
        vServerURL.setText(serverURL);
        
        setLayout();
        
        setLocale();
        
        setTheme();
        
        setMoveControl();

        setEditMode();
        
        setGameOrder();
        
        setMakeSound();
        
        keepScreenOnCB.setChecked(keepScreenOn);
        autoClientCB.setChecked(autoPlay);
		autoStartNotifierCB.setChecked(autoStartNotifier);
        autoPlayPauseCB.setChecked(autoPlayPause);
        autoPlaySoundCB.setChecked(autoPlaySound);
        notifierVibrateCB.setChecked(notifierVibrate);
        editDisplayTopMenuCB.setChecked(editDisplayTopMenu) ;
		playDisplayTopMenuCB.setChecked(playDisplayTopMenu);
		playGameNotesCB.setChecked(playGameNotes);
        vSkipMoves.setText(Integer.toString(skipMoves));
        vEditNumPrev.setText(Integer.toString(editNumPrev));
        vPlayNumPrev.setText(Integer.toString(playNumPrev));
        vNotifierInterval.setText(Long.toString(notifierInterval));
        notifyAllDetailsCB.setChecked(notifyAllDetails);
        vAutoPlayInterval.setText(Long.toString(autoPlayInterval));
        vScaleLines.setText(Float.toString(scaleLines));
		vDBG.setText(Integer.toString(dbg));
    }
            
    
    private String starString(String s) {
    	StringBuilder sb = new StringBuilder();
    	for (int i=0; i<s.length(); i++) {
    		sb.append("*");
    	}
    	return sb.toString();
    }
    
    private void flashButton(View v) {
    	v.setVisibility(View.INVISIBLE);          //setBackgroundColor(MainDGS.LIGHT_GREY_COLOR);
    	final View fv = v;
		final Handler handler = new Handler(); 
        Timer t = new Timer(); 
        t.schedule(new TimerTask() { 
        	public void run() { 
        		handler.post(new Runnable() { 
        			public void run() { 
        				fv.setVisibility(View.VISIBLE);    //setBackgroundColor(MainDGS.WHITE_COLOR);
        			} 
        		}); 
        	} 
        }, MainDGS.BUTTON_DELAY);
    }
    
    private void doSetUser() {
    	final EditText input = new EditText(ctw);
	    input.setText(vUser.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.prefsUser)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	vUser.setText(input.getText());
	    }})
		.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}})
	    .show();
    }
    
    private void doSetPasswrd() { 
    	final EditText input = new EditText(ctw);
    	input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
	    input.setText(passwd);
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.prefsPassword)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	passwd = input.getText().toString().trim();
			if (passwd.length() > 16) passwd = passwd.substring(0, 16);
	    	vPasswrd.setText(starString(passwd));
	    }})
		.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}})
	    .show();
    }
    
    private void doSetAutoClient() {
    	if (autoClientCB.isChecked()) {
    		autoClientCB.setChecked(false);
    	} else {
    		autoClientCB.setChecked(true);
    	}
    }

	private void doSetAutoStartNotifier() {
		if (autoStartNotifierCB.isChecked()) {
			autoStartNotifierCB.setChecked(false);
		} else {
			autoStartNotifierCB.setChecked(true);
		}
	}
 
    private void doSetKeepScreenOn() {
    	if (keepScreenOnCB.isChecked()) {
    		keepScreenOnCB.setChecked(false);
    	} else {
    		keepScreenOnCB.setChecked(true);
    	}
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
    
    private void doSetNotifierVibrate() {
    	if (notifierVibrateCB.isChecked()) {
    		notifierVibrateCB.setChecked(false);
    	} else {
    		notifierVibrateCB.setChecked(true);
    	}
		notifierChanged = true;
    }
    
    private void doSetNotifyAllDetails() {
    	if (notifyAllDetailsCB.isChecked()) {
    		notifyAllDetailsCB.setChecked(false);
    	} else {
    		notifyAllDetailsCB.setChecked(true);
    	}
		notifierChanged = true;
    }

    private void doSetEditDisplayTopMenu() {
    	if (editDisplayTopMenuCB.isChecked()) {
    		editDisplayTopMenuCB.setChecked(false);
    	} else {
    		editDisplayTopMenuCB.setChecked(true);
    	}
    }
    
    private void doSetPlayDisplayTopMenu() {
    	if (playDisplayTopMenuCB.isChecked()) {
    		playDisplayTopMenuCB.setChecked(false);
    	} else {
    		playDisplayTopMenuCB.setChecked(true);
    	}
    }
    
    private void doSetPlayGameNotes() {
    	if (playGameNotesCB.isChecked()) {
    		playGameNotesCB.setChecked(false);
    	} else {
    		playGameNotesCB.setChecked(true);
    	}
    }
      
    private void doSetBoardCoord() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(ctw.getString(R.string.select)); 
		ArrayAdapter<String> sel_adapter = new ArrayAdapter<>(ctw,
				R.layout.select_row, boardCoord_items);
		sel_adapter.setDropDownViewResource(R.layout.select_row);
		builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
            	case 0: 
            		boardCoordTxt = NO_COORD;
            		setBoardCoord();
            		break;
            	case 1: 
            		boardCoordTxt = EDIT_COORD;
            		setBoardCoord();
            		break;
            	case 2: 
            		boardCoordTxt = PLAY_COORD;
            		setBoardCoord();
            		break;
            	case 3: 
            		boardCoordTxt = ALL_COORD;
            		setBoardCoord();
            		break;
            	default: 
            	}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();	
    }
    
    private void setBoardCoord() {
        if (boardCoordTxt.contentEquals(ALL_COORD)) {
        	boardCoord_text.setText(boardCoord_items[3]);
        } else if (boardCoordTxt.contentEquals(PLAY_COORD)) {
        	boardCoord_text.setText(boardCoord_items[2]);	
        } else if (boardCoordTxt.contentEquals(EDIT_COORD)) {
        	boardCoord_text.setText(boardCoord_items[1]);	
        } else {
        	boardCoord_text.setText(boardCoord_items[0]);
        }  
    }
    
    private void doSetBoardBG() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(ctw.getString(R.string.select)); 
		ArrayAdapter<String> sel_adapter = new ArrayAdapter<>(ctw,
				R.layout.select_row, boardBG_items);
		sel_adapter.setDropDownViewResource(R.layout.select_row);
		builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
	        	case 0: 
	        		boardBGTxt = BG_WHITE;
	        		setBoardBGvalue();
	        		break;
	        	case 1: 
	        		boardBGTxt = BG_PLAIN;
	        		setBoardBGvalue();
	        		break;
	        	case 2: 
	        		boardBGTxt = BG_WOOD;
	        		setBoardBGvalue();
	        		break;  
	        	case 3: 
	        		boardBGTxt = BG_CUSTOM;
	        		setBoardBGvalue();
	            	new AmbilWarnaDialog(ctw, customBGvalue, new AmbilWarnaDialog.OnAmbilWarnaListener() {
	        			public void onOk(AmbilWarnaDialog dialog, int color) {
	        				// if (!callChangeListener(color)) return; // They don't want the value to be set
	        				customBGvalue = color;
	        				setBoardBGvalue();
	        			}
	
	        			public void onCancel(AmbilWarnaDialog dialog) {
	        				// nothing to do
	        			}
	
	        		}).show();
	        		break;
	        	default: 
	        	}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
    }
    
    private void setImageView(ImageView v, int r, int bg) {
        v.setMaxHeight(18);
        v.setMinimumHeight(18);
        v.setMaxWidth(18);
        v.setMinimumWidth(18);
        v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        v.setBackgroundColor(bg);
        v.setImageResource(r);
    }
    
    private void setBoardBGvalue() {
        if (boardBGTxt.contentEquals(BG_CUSTOM)) {
            boardBG_text.setText(boardBG_items[3]);
            setImageView(boardBG_image, 0, customBGvalue);
        } else if (boardBGTxt.contentEquals(BG_WOOD)) {
            boardBG_text.setText(boardBG_items[2]);
            setImageView(boardBG_image, R.drawable.wood1, MainDGS.TRANSPARENT_COLOR);
        } else if (boardBGTxt.contentEquals(BG_PLAIN)) {
            boardBG_text.setText(boardBG_items[1]);
            setImageView(boardBG_image, 0, MainDGS.BOARD_COLOR);
        } else {
        	boardBG_text.setText(boardBG_items[0]);
            setImageView(boardBG_image, 0, MainDGS.WHITE_COLOR);
        }
    }
    
    private void doSetBoardStone() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(ctw.getString(R.string.select)); 
		ArrayAdapter<String> sel_adapter = new ArrayAdapter<>(ctw,
				R.layout.select_row, boardStone_items);
		sel_adapter.setDropDownViewResource(R.layout.select_row);
		builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
            	case 0: 
            		boardStoneTxt = STONE_MONO;
            		setBoardStonevalue();
            		break;
            	case 1: 
            		boardStoneTxt = STONE_CLAM;
            		setBoardStonevalue();
            		break;
            	default: 
            	}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();   	
    }
    
    private void setBoardStonevalue() {
    	if (boardStoneTxt.contentEquals(STONE_CLAM)) {
    		boardStone_text.setText(boardStone_items[1]);
            setImageView(boardStone_whiteImage, R.drawable.w, MainDGS.BOARD_COLOR);
            setImageView(boardStone_blackImage, R.drawable.b, MainDGS.BOARD_COLOR);
        } else {
    		boardStone_text.setText(boardStone_items[0]);
            setImageView(boardStone_whiteImage, R.drawable.w1, MainDGS.BOARD_COLOR);
            setImageView(boardStone_blackImage, R.drawable.b1, MainDGS.BOARD_COLOR);
        }
    }

    private void doSetLayout() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(ctw.getString(R.string.select)); 
		ArrayAdapter<String> sel_adapter = new ArrayAdapter<>(ctw,
				R.layout.select_row, layout_items);
		sel_adapter.setDropDownViewResource(R.layout.select_row);
		builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
            	case 0: 
            		boardLayout = PORTRAIT;
            		setLayout();
            		break;
            	case 1: 
            		boardLayout = LANDSCAPE;
            		setLayout();
            		break;
            	case 2:
            		boardLayout = DYNAMIC;
            		setLayout();
            		break;
            	default: 
            	}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();  
    }
    
    private void setLayout() {
    	if (boardLayout.contentEquals(DYNAMIC)) {
        	prefs_layout_text.setText(layout_items[2]);
        } else
        if (boardLayout.contentEquals(LANDSCAPE)) {
        	prefs_layout_text.setText(layout_items[1]);
        } else {
        	prefs_layout_text.setText(layout_items[0]);
        }
    }
    
    private void doSetLocale() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(ctw.getString(R.string.select)); 
		ArrayAdapter<String> sel_adapter = new ArrayAdapter<>(ctw,
				R.layout.select_row, language_items);
		sel_adapter.setDropDownViewResource(R.layout.select_row);
		builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item >= 0 && item < locale_items.length) {
					locale_index = item;
					prefs_locale_text.setText(language_items.get(item));
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
    }
    
	private void setLocale() {
		prefs_locale_text.setText(language_items.get(locale_index));
	    for (int i=0; i < locale_items.length; ++i) {
	    	if (locale_items[i].contentEquals(myLocale)) {
	    		locale_index = i;
	    		prefs_locale_text.setText(language_items.get(i));
	    	}
	    }
	}
	    
    private void doSetTheme() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(ctw.getString(R.string.select)); 
		ArrayAdapter<String> sel_adapter = new ArrayAdapter<>(ctw,
				R.layout.select_row, theme_items);
		sel_adapter.setDropDownViewResource(R.layout.select_row);
		builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
            	case 0:
            		theme = WHITEONBLACK;
            		break;
				case 1:
					theme = WHITEONBLUE;
					break;
            	case 2:
            		theme = WHITEONGREEN;
            		break;
            	case 3:
            		theme = METALLICGREY;
            		break;
                case 4:
                    theme = BLACKONWHITE;
                    break;
                case 5:
                    theme = BLUEONWHITE;
                    break;
                case 6:
                    theme = GREENONWHITE;
                    break;
                case 7:
                    theme = METALLICGREYINVERSE;
                    break;
            	default:
                    theme = DEFAULT_THEME;
            	}
                setTheme();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
    }
    
    private void setTheme() {
        if (theme.contains(METALLICGREYINVERSE)) {
            prefs_theme_text.setText(theme_items[7]);
        } else if (theme.contains(GREENONWHITE)) {
            prefs_theme_text.setText(theme_items[6]);
        } else if (theme.contains(BLUEONWHITE)) {
            prefs_theme_text.setText(theme_items[5]);
        } else if (theme.contains(BLACKONWHITE)) {
			prefs_theme_text.setText(theme_items[4]);
		} else if (theme.contains(METALLICGREY)) {
        	prefs_theme_text.setText(theme_items[3]);
        } else if (theme.contains(WHITEONGREEN)) {
        	prefs_theme_text.setText(theme_items[2]);
		} else if (theme.contains(WHITEONBLUE)) {
			prefs_theme_text.setText(theme_items[1]);
        } else if (theme.contains(WHITEONBLACK)) {
        	prefs_theme_text.setText(theme_items[0]);
        } else {
            prefs_theme_text.setText(theme_items[0]);
        }
    }
    
    private void doSetMoveControl() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(ctw.getString(R.string.select)); 
		ArrayAdapter<String> sel_adapter = new ArrayAdapter<>(ctw,
				R.layout.select_row, move_control_items);
		sel_adapter.setDropDownViewResource(R.layout.select_row);
		builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
            	case 0: 
            		moveControl = ZOOM7X7;
            		setMoveControl();
            		break;
            	case 1: 
            		moveControl = ZOOM9X9;
            		setMoveControl();
            		break;
            	case 2: 
            		moveControl = ZOOM11X11;
            		setMoveControl();
            		break;
            	case 3: 
            		moveControl = ZOOM13X13;
            		setMoveControl();
            		break;
            	case 4: 
            		moveControl = DPADCONTROL;
            		setMoveControl();
            		break;
            	case 5: 
            		moveControl = ONETOUCH;
            		setMoveControl();
            		break;
            	case 6: 
            		moveControl = SLIDE;
            		setMoveControl();
            		break;
            	default: 
            	}
			}
		});
		AlertDialog alert = builder.create();
		alert.show(); 
    }
    
    private void setMoveControl() {
        if (moveControl.contentEquals(SLIDE)) {
         	prefs_move_control_text.setText(move_control_items[6]);
        } else if (moveControl.contentEquals(ONETOUCH)) {
         	prefs_move_control_text.setText(move_control_items[5]);
        } else if (moveControl.contentEquals(DPADCONTROL)) {
         	prefs_move_control_text.setText(move_control_items[4]);	
        } else if (moveControl.contentEquals(ZOOM13X13)) {
         	prefs_move_control_text.setText(move_control_items[3]);	
        } else if (moveControl.contentEquals(ZOOM11X11)) {
         	prefs_move_control_text.setText(move_control_items[2]);	
        } else if (moveControl.contentEquals(ZOOM9X9)) {
         	prefs_move_control_text.setText(move_control_items[1]);	
        } else {
        	prefs_move_control_text.setText(move_control_items[0]);
        }
    }
    
    private void doSetEditMode() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(ctw.getString(R.string.select)); 
		ArrayAdapter<String> sel_adapter = new ArrayAdapter<>(ctw,
				R.layout.select_row, editMode_items);
		sel_adapter.setDropDownViewResource(R.layout.select_row);
		builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
            	case 0: 
            		editMode = GameBoardOptions.AUTOPLAY;
            		setEditMode();
            		break;
            	case 1: 
            		editMode = GameBoardOptions.BROWSE;
            		setEditMode();
            		break;
            	case 2: 
            		editMode = GameBoardOptions.EDIT;
            		setEditMode();
            		break;
            	case 3: 
            		editMode = GameBoardOptions.GUESSMOVE;
            		setEditMode();
            		break;
            	case 4: 
            		editMode = GameBoardOptions.MARKUP;
            		setEditMode();
            		break;
            	default: 
            	}
			}
		});
		AlertDialog alert = builder.create();
		alert.show(); 
    }
    
    private void setEditMode() {
    	if (editMode.contentEquals(GameBoardOptions.MARKUP)) {
        	prefs_editMode_text.setText(editMode_items[4]);
        } else if (editMode.contentEquals(GameBoardOptions.GUESSMOVE)) {
        	prefs_editMode_text.setText(editMode_items[3]);
        } else if (editMode.contentEquals(GameBoardOptions.EDIT)) {
        	prefs_editMode_text.setText(editMode_items[2]);
        } else if (editMode.contentEquals(GameBoardOptions.BROWSE)) {
        	prefs_editMode_text.setText(editMode_items[1]);
        } else {
        	prefs_editMode_text.setText(editMode_items[0]);
        }
    }
    
    private void doSetGameOrder() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(ctw.getString(R.string.select)); 
		ArrayAdapter<String> sel_adapter = new ArrayAdapter<>(ctw,
				R.layout.select_row, gameOrder_items);
		sel_adapter.setDropDownViewResource(R.layout.select_row);
		builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
            	case 0: 
            		gameOrder = MainDGS.GO_DEFAULT;
            		setGameOrder();
            		break;
            	case 1: 
            		gameOrder = MainDGS.GO_NOORDER;
            		setGameOrder();
            		break;
            	case 2: 
            		gameOrder = MainDGS.GO_LASTMOVED;
            		setGameOrder();
            		break;
            	case 3: 
            		gameOrder = MainDGS.GO_MOVES;
            		setGameOrder();
            		break;
            	case 4: 
            		gameOrder = MainDGS.GO_PRIO;
            		setGameOrder();
            		break;
            	case 5: 
            		gameOrder = MainDGS.GO_TIMELEFT;
            		setGameOrder();
            		break;
            	default: 
            	}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();  
    }
    
    private void setGameOrder() {
    	if (gameOrder.contentEquals(MainDGS.GO_TIMELEFT)) {
        	prefs_gameOrder_text.setText(gameOrder_items[5]);
        } else if (gameOrder.contentEquals(MainDGS.GO_PRIO)) {
        	prefs_gameOrder_text.setText(gameOrder_items[4]);
        } else if (gameOrder.contentEquals(MainDGS.GO_MOVES)) {
        	prefs_gameOrder_text.setText(gameOrder_items[3]);
        } else if (gameOrder.contentEquals(MainDGS.GO_LASTMOVED)) {
        	prefs_gameOrder_text.setText(gameOrder_items[2]);
        } else if (gameOrder.contentEquals(MainDGS.GO_NOORDER)) {
        	prefs_gameOrder_text.setText(gameOrder_items[1]);
        } else {
        	prefs_gameOrder_text.setText(gameOrder_items[0]);
        }
    }
    
    private void doSetSound() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(ctw.getString(R.string.select)); 
		ArrayAdapter<String> sel_adapter = new ArrayAdapter<>(ctw,
				R.layout.select_row, sound_items);
		sel_adapter.setDropDownViewResource(R.layout.select_row);
		builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
            	case 0: 
            		makeSound = NOSOUND;
            		setMakeSound();
            		break;
            	case 1: 
            		makeSound = DEFAULTSOUND;
            		setMakeSound();
            		break;
            	case 2: 
            		makeSound = STONESOUND;
            		setMakeSound();
            		break;
            	default: 
            	}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
    }
    
    private void setMakeSound() {
        if (makeSound.contentEquals(STONESOUND)) {
        	prefs_sound_text.setText(sound_items[2]);
        } else if (makeSound.contentEquals(DEFAULTSOUND)) {
        	prefs_sound_text.setText(sound_items[1]);	
        } else {
        	prefs_sound_text.setText(sound_items[0]);
        }
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
		.setNegativeButton(R.string.deflt, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				vSkipMoves.setText(Integer.toString(GameBoardOptions.DEFAULTMINSKIPMOVES));
			}})
		.setNeutralButton(R.string.Cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}})
	    .show();
    }
    
    private void doSetEditNumPrev() {
    	final EditText input = new EditText(ctw);
    	input.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
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
		.setNegativeButton(R.string.deflt, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				editNumPrev = GameBoardOptions.DEFAULTNUMPREVEDIT;
			}})
		.setNeutralButton(R.string.Cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}})
	    .show();
    }
    
    private void doSetPlayNumPrev() {
    	final EditText input = new EditText(ctw);
    	input.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
	    input.setText(vPlayNumPrev.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.prefsPlayNumPrev)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				try {
					playNumPrev = Integer.decode(input.getText().toString().trim());
				} catch (Exception e) {
					playNumPrev = GameBoardOptions.DEFAULTNUMPREVPLAY;
				}
				if (playNumPrev < GameBoardOptions.DEFAULTMINNUMPREV) playNumPrev = GameBoardOptions.DEFAULTMINNUMPREV;
				if (playNumPrev > GameBoardOptions.DEFAULTMAXNUMPREV) playNumPrev = GameBoardOptions.DEFAULTMAXNUMPREV;
				vPlayNumPrev.setText(Integer.toString(playNumPrev));
	    	}})
		.setNegativeButton(R.string.deflt, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				editNumPrev = GameBoardOptions.DEFAULTNUMPREVEDIT;
			}})
		.setNeutralButton(R.string.Cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}})
	    .show();
    }
    
    private void doSetNotifierInterval() {
    	final EditText input = new EditText(ctw);
    	input.setInputType(InputType.TYPE_CLASS_NUMBER);
	    input.setText(vNotifierInterval.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.prefsNotifierInterval)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				long interval = Long.parseLong(input.getText().toString().trim());
				if (interval < DGSNotifier.MINNOTIFIERINTERVAL) {
					interval = DGSNotifier.MINNOTIFIERINTERVAL;
				}
				vNotifierInterval.setText(Long.toString(interval));
		}})
		.setNegativeButton(R.string.deflt, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				vNotifierInterval.setText(Long.toString(DGSNotifier.DEFNOTIFIERINTERVAL));
			}})
		.setNeutralButton(R.string.Cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}})
	    .show();
		notifierChanged = true;
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
		.setNegativeButton(R.string.deflt, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				vAutoPlayInterval.setText("1");
			}})
		.setNeutralButton(R.string.Cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}})
	    .show();
    }
    
    private void doSetDefaultDir() { 
    	final TextView input = new TextView(ctw);
	    input.setText(sgfDir);
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.SgfDir)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	// do nothing
	    }})
	    .show();
    }
    
    private void doSetServerURL() {
    	final EditText input = new EditText(ctw);
	    input.setText(vServerURL.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.prefsServerURL)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	vServerURL.setText(input.getText());
	    }})
	    .setNegativeButton(R.string.deflt, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	vServerURL.setText(DGS_URL);
	    }})
		.setNeutralButton(R.string.Cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}})
	    .show();
    }
    
    private void doSetLineWidth() {
    	final EditText input = new EditText(ctw);
	    input.setText(vScaleLines.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.prefsScaleLines)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	vScaleLines.setText(input.getText());
	    }})
	    .setNegativeButton(R.string.deflt, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	vScaleLines.setText(DEFAULT_LINE_WIDTH);
	    }})
		.setNeutralButton(R.string.Cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}})
	    .show();
    }

	private void doSetDBG() {
		final EditText input = new EditText(ctw);
		input.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
		input.setText(vDBG.getText());
		new AlertDialog.Builder(ctw)
				.setTitle(R.string.SetPreference)
				.setMessage(R.string.dbg)
				.setView(input)
				.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						try {
							dbg = Integer.decode(input.getText().toString().trim());
						} catch (Exception e) {
							dbg = 0;
						}
						vDBG.setText(Integer.toString(dbg));
					}})
				.setNegativeButton(R.string.deflt, new DialogInterface.OnClickListener() {
					@SuppressLint("SetTextI18n")
					public void onClick(DialogInterface dialog, int whichButton) {
						dbg = 0;
						vDBG.setText(Integer.toString(dbg));
					}})
				.setNeutralButton(R.string.Cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}})
				.show();
	}
    
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);  
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	/* do nothing
    	switch(requestCode) {
    	case HELP_VIEW:
    	default:
    		break;
    	}
    	 */
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
		 final Intent helpIntent = commonStuff.helpDGS (commonStuff.HELP_PREFS, this);
		 startActivityForResult(helpIntent, HELP_VIEW);
	 }

}
