package com.hg.anDGS;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainDGS extends DGSActivity {
	// Game Order values
	public static final String GO_DEFAULT = "";
	public static final String GO_NOORDER = "0";
	public static final String GO_LASTMOVED = "1";
	public static final String GO_MOVES = "2";
	public static final String GO_PRIO = "3";
	public static final String GO_TIMELEFT = "4";
	// play states left button right button
	protected static final int LOGON_DGS = 0; // blank Cancel
	protected static final int LOGGED_ON = 1;
	protected static final int GET_STATUS_LIST = 2; // blank Cancel
	protected static final int GET_SGF = 3; // Skip All Skip
	protected static final int DISPLAY_BOARD = 4; // Skip All Skip
	protected static final int DOWNLOAD_SGF = 5;
	protected static final int SEND_MOVE = 6; // Skip All Skip
	protected static final int DISPLAY_STATUS = 7;
	protected static final int GET_RUNNING_GAME_LIST = 8;
	protected static final int GET_TEAM_GAME_LIST = 9;
	protected static final int GET_FINISHED_GAME_LIST = 10;
	protected static final int GET_OBSERVED_GAME_LIST = 11;
	protected static final int DISPLAY_GAME_LIST = 12;
	//	protected static final int SEND_INVITE = 13;
	protected static final int GET_MSG = 14;
	protected static final int SEND_MSG = 15;
	protected static final int GET_INFO = 16;
	protected static final int GET_BULLETIN = 17;
	protected static final int GET_WROOM_LIST = 18;
	protected static final int GET_WROOM_INFO = 19;
	protected static final int DISPLAY_WROOM_LIST = 20;
	protected static final int DISPLAY_WROOM_INFO = 21;
	protected static final int JOIN_WROOM_GAME = 22;
	protected static final int DISPLAY_NORMAL_MSG = 23;
	protected static final int DISPLAY_RESULT_MSG = 24;
	protected static final int DISPLAY_INVITATION_MSG = 25;
	protected static final int DISPLAY_DISPUTED_MSG = 26;
	protected static final int DISPLAY_BULLETIN = 27;
	// view types
	protected static final int HELP_VIEW = 0;
	protected static final int GET_PREFS = 1;

	protected static final int SAVED_GAMES = 3;
	protected static final int NEW_GAME = 4;
	protected static final int RECOV_GAME = 5;
	protected static final int GRINDER = 6;
	protected static final int FWD_PREFS = 7;
	protected static final int GAME_BOARD = 8;
    protected static final int TUTORIAL_VIEW = 9;

	protected static final int PLAY_VIEW = 11;
	protected static final int DOWN_LOAD_VIEW = 12;
	protected static final int DISPLAY_MSG_VIEW = 13;
	protected static final int STATUS_VIEW = 14;
	protected static final int GAMES_VIEW = 15;
	protected static final int INVITE_VIEW = 16;
	protected static final int MESSAGE_USER_VIEW = 17;
	protected static final int DISPLAY_INFO_VIEW = 18;
	protected static final int STORED_MOVES_VIEW = 19;
	protected static final int GET_USER_GRAPH_VIEW = 20;
	protected static final int FIND_USER_VIEW = 21;
	protected static final int PHRASE_VIEW = 22;
	protected static final int ERROR_HISTORY_VIEW = 23;
	protected static final int ERROR_HISTORY_DELETE = 24;
	protected static final int UTILITY_PHRASE_VIEW = 25;

	// top and pull down menu types
	private static final int MENU_HELP = 0;
	private static final int MENU_CREDITS = 1;
	private static final int MENU_PREFS = 2;
	private static final int MENU_NOTIFIER = 3;
	private static final int MENU_TUTORIAL = 4;
	
	protected static final int WHITE_COLOR = 0xffffffff;
	protected static final int BLACK_COLOR = 0xff000000;
	protected static final int GREEN_COLOR = 0xff88ff88;
	protected static final int LIGHT_GREY_COLOR = 0xffdddddd; 
	protected static final int BOARD_COLOR = 0xffe6a240;  // original: 0xffff9933;
	protected static final int TRANSPARENT_COLOR = 0x00000000;
	protected static final int label_color_l = WHITE_COLOR; // Color.LTGRAY;
	protected static final int label_color_m = Color.GRAY;
	protected static final int label_color_d = BLACK_COLOR; // Color.DKGRAY;
	protected static final int disabled_button_color = 0xffdddddd;  // light grey
	protected static final int enabled_button_color = 0xffaaaaaa;   //Color.BLACK;

	protected static final long BUTTON_DELAY = 300;
    ArrayAdapter<String> buttonList;
    private List<String> buttonItems;
    private ListView client_buttons;   // rename main_buttons later
    private final int BUTTONS_HOME = 0;
    private final int BUTTONS_EDIT = 1;
    private final int BUTTONS_CLIENT = 2;  // rename BUTTONS_CLIENT later
    private final int BUTTONS_GAMELISTS = 3;
    private final int BUTTONS_USERACTIONS = 4;
    private final int BUTTONS_UTILITIES = 5;
    private int buttonsType = BUTTONS_CLIENT;

	private TextView tmPrefs;
	private TextView tmCredits;
	private TextView tmToggleNotifier;
	private TextView tmTutorial;
	private TextView tmHelp;
	private TextView mainDesc;
	private TextView connStatus;
	private TextView version_txt;

	private EditText utilityCommentEditText;

	private int connState;
    private String DGSUser;
    private String DGSPass;
	private String myUserId;
	private String lastUId = "";
	private String lastUserId = "";
	private String lastUserName = "";
	private String lastRegisterDate = "";
	private String defaultDir = PrefsDGS.DEFAULT_DIR;
	private String ServerURL;
	private boolean movePlayed = false;
	private String moveString = "";
	private String msgString = "";
	private StringBuilder msgsSeen = new StringBuilder();
	private String boardLayout = PrefsDGS.DYNAMIC;
	private String currentBoardLayout = PrefsDGS.PORTRAIT;
	private String defaultEditMode = GameBoardOptions.BROWSE;
	private String gameId = "0";
	private String moveId = "0";
	private String gameOrder = "0";
	private String game_action = "0";
	private String game_status = "UNKNOWN";
	private String handicap = "0";
	private String msgId = "0";
	private String msgType = "";
	private String oldSubj = "";
	private String statusResult = "";
	private String [] statusList = null;
	String [] score_data = null;
	private int gameIndex = -1;
	private String timeLeft = "";
	private int msgIndex = -1;
	private int bulletinIndex = -1;
	private int mpgIndex = -1;
	private boolean autoClient = false;
	private boolean actionStoredMoves = false;
	private boolean gameNotes = false;
	private String gameNoteTxt = "";
	private boolean returnToStatus = false;
	private long autoPlayInterval = GameBoardOptions.DEFAUTOPLAYINTERVAL;
	private boolean autoPlayPause = true;
	private boolean oneShot;
	private String sgf = "";
	private StoredMoves storMov = StoredMoves.getInstance();
	private ErrorHistory errHist = ErrorHistory.getInstance();

	private boolean checkedFileSystem = false;
	private boolean recoveryFile = false;
	private DGSThread mThread = null;
	private boolean needToStartClient = false;  // Used in startup
	private boolean needToOpenFile = false;     // Used in startup
	private String recov_text;
	private String theme;
	private String myLocale;
	private boolean startedByNotifier = false;
	private boolean autoStartNotifier = true;
	private String inFileName = null;
	private boolean localFile = true;
    private Uri fileData;
	private DownloadFile dlThread = null;
	ActivityManager am;
    private NotificationManager mNM;
	private ContextThemeWrapper ctw;
    private CommonStuff commonStuff = new CommonStuff();
    private CommonFileStuff commonFileStuff = new CommonFileStuff();
	static long lastStatusTime = 0;
	static OkHttpClient okHTTPclient = null;
	static String loginURL;
    // debug flags
	private int dbg = 0;
	public static boolean dbgMain = false;
    public static boolean dbgNotifier = false;
	public static boolean dbgThread = false;
	public static boolean dbgStatus = false;
	public static boolean dbgStdMov = false;
	/*
    static Context context;
	public MainDGS(Context c) {
		super();
		this.context = c;
	}
	public static Context getMyContext() {
		return context;
	}
	*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		//int orientation = display.getOrientation();
		SharedPreferences prefs = getSharedPreferences("MainDGS", 0); //getPreferences(0);
		DGSUser = prefs.getString("com.hg.anDGS.DGSUser", "");  // user and password need defined to start client
        myUserId = DGSUser;
        lastUserId = myUserId;
        DGSPass = prefs.getString("com.hg.anDGS.DGSPass", "");
		if (DGSPass.length() > 16) DGSPass = DGSPass.substring(0, 16);
        gameOrder = prefs.getString("com.hg.anDGS.GameOrder", "0");
        ServerURL = prefs.getString("com.hg.anDGS.ServerURL", PrefsDGS.DGS_URL);
		boardLayout = prefs.getString("com.hg.anDGS.BoardLayout", PrefsDGS.DYNAMIC);
        defaultEditMode = prefs.getString("com.hg.anDGS.DefaultEditMode", GameBoardOptions.BROWSE);
        autoClient = prefs.getBoolean("com.hg.anDGS.AutoPlay", false);
        gameNotes = prefs.getBoolean("com.hg.anDGS.GameNotes", false);
        autoPlayPause = prefs.getBoolean("com.hg.anDGS.AutoPlayPause", true);
        autoPlayInterval = prefs.getLong("com.hg.anDGS.AutoPlayInterval", GameBoardOptions.DEFAUTOPLAYINTERVAL);
        theme = prefs.getString("com.hg.anDGS.Theme", PrefsDGS.DEFAULT_THEME);
		myLocale = prefs.getString("com.hg.anDGS.Locale", "");
		defaultDir = prefs.getString("com.hg.anDGS.DefaultDir", PrefsDGS.DEFAULT_DIR);
		autoStartNotifier = prefs.getBoolean("com.hg.anDGS.AutoStartNotifier", false);
		dbg = prefs.getInt("com.hg.anDGS.Debug", 0);
		//  displayTopMenu = prefs.getBoolean("com.hg.anDGS.DisplayTopMenu", true);
		if (boardLayout == null) {
			boardLayout = PrefsDGS.DYNAMIC; 
		}
		loginURL = ServerURL + "login.php";

        oneShot = false;
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			startedByNotifier = extras.getBoolean("DGSNOTIFIERSTART", false);
			oneShot = startedByNotifier;
		}

		fileData = intent.getData();
		needToOpenFile = fileData != null;
		if (needToOpenFile) {
			oneShot = true;
			localFile = !fileData.getScheme().startsWith("http");
            //Toast.makeText(this, "Uri:" + fileData.toString(), Toast.LENGTH_LONG).show();
            // Toast.makeText(this, "localFile:" + localFile + "Scheme:" + fileData.getScheme(), Toast.LENGTH_LONG).show();
			if (localFile) {
				inFileName = fileData.getPath();
				assert inFileName != null;
				if (inFileName.startsWith("/root")) {
                    inFileName=inFileName.substring(5);
                }
			} else {
				inFileName = fileData.getLastPathSegment();
			}
		}
		
		this.setTheme(commonStuff.getCommonStyle(theme));
		setOrientationViews();
		
		tmPrefs.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						doPrefs();
					}
				});
			}
		});
		
		tmCredits.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						doCredits();
					}
				});
			}
		});
		
		tmToggleNotifier.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						doToggleNotifier();
					}
				});
			}
		});
				
        tmTutorial.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						doTutorial();
					}
				});
			}
		});
        
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

        am = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
        mNM = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		ctw = new ContextThemeWrapper(this, commonStuff.getCommonTheme(theme));
		
		//boolean bError = false;
		try {
			//WebViewDatabase webViewDB = WebViewDatabase.getInstance( this );
			//if (webViewDB == null) bError = true;
			WebView oww = new WebView( this );
			oww.clearCache( true );
		} catch( Throwable anyhing ) {
			//bError = true;
		}

		try {
			ComponentName comp = new ComponentName(this, this.getClass());
			PackageInfo pinfo;
			pinfo = this.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
			version_txt.setText(pinfo.versionName);
		} catch (NameNotFoundException e) {
			version_txt.setText("Unknown");
		}
/*
		if (isRecoveryFile()) {
			recoveryFile = true;
			recov_text = getString(R.string.recover_game);
		} else {
			recoveryFile = false;
			recov_text = " ";
		}
*/
		recoveryFile = false;
		recov_text = getString(R.string.Nothing);

        if (startedByNotifier) {
            autoClient = true;
            needToStartClient = true;
            needToOpenFile=false;
            inFileName=null;
        } else if (inFileName != null) {
            autoClient = false;
            needToStartClient = false;
        } else {
            needToOpenFile = false;
            needToStartClient = autoClient;
        }

        actionStoredMoves = autoClient;
        if (actionStoredMoves) {
            if (!storMov.areStoredMovesLoaded()) {
                storMov.loadStoredMoves();
            }
        }

        if (autoClient) {
            connStatus.setText(R.string.Waiting);
        } else {
            connStatus.setText(R.string.blank5);
        }
		connState = LOGON_DGS;

		buttonItems = new ArrayList<>();
        buttonItems.clear();
        buttonList = new ArrayAdapter<>(this,
                R.layout.button_row, buttonItems);
        client_buttons.setAdapter(buttonList);
		displayButtons(BUTTONS_HOME);

		if (dbgMain) {
			errHist.writeErrorHistory("MainDGS.onCreate, startedByNotifier:" + startedByNotifier
					+ ", oneShot:" + oneShot
					+ ", autoClient:" + autoClient
					+ ", needToStartClient:" + needToStartClient);
			if (needToOpenFile) {
				errHist.writeErrorHistory("MainDGS.onCreate, localFile:" + localFile
						+ ", Scheme:" + fileData.getScheme()
						+ ", inFileName:" + inFileName);
				errHist.writeErrorHistory("MainDGS.onCreate, fileData:" + fileData.toString());
			}
		}
	}
	
	@Override
	protected void onStart(){
	    super.onStart();
	    setDebugFlags();
        if(!commonFileStuff.isDirectory(commonFileStuff.ANDGS_DIR)) {
            if (commonFileStuff.makeDirectory (commonFileStuff.getFullDirName(commonFileStuff.ANDGS_DIR))) {
				commonFileStuff.migrateFile(commonFileStuff.RECOVERYFILE, defaultDir, commonFileStuff.ANDGS_DIR);
				commonFileStuff.migrateFile(commonFileStuff.SMFILENAME, defaultDir, commonFileStuff.ANDGS_DIR);
			} else {
            	if (!checkedFileSystem) {
					checkedFileSystem = true;
					final Intent helpIntent = commonStuff.helpDGS (commonStuff.HELP_FILE_PERMISSIONS, this);
					helpIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
					startActivityForResult(helpIntent, HELP_VIEW);
				}
			}
        }
		if (!storMov.areStoredMovesLoaded()) {
            storMov.loadStoredMoves();
		}
		errHist.checkLoadErrorHistoryData();

		boolean notifierRunning = commonStuff.isNotifierRunning(am);
		if (notifierRunning) {
			tmToggleNotifier.setText(getString(R.string.StopNotifier));
		} else {
			if (autoStartNotifier && !oneShot) {
					doToggleNotifier();
			} else
				tmToggleNotifier.setText(getString(R.string.StartNotifier));
		}
/*
		errHist.writeErrorHistory("MainDGS.onStart, startedByNotifier:" + startedByNotifier
				+ ", oneShot:" + oneShot
				+ ", autoClient:" + autoClient
				+ ", needToStartClient:" + needToStartClient
				+ ", notifierRunning:" + notifierRunning);
*/
        if (needToOpenFile && inFileName != null) {
            needToOpenFile = false;
            if (localFile) {
                startGameBoard("", inFileName);
            } else {
                dlThread = new DownloadFile(fileData, mDLHandler);
                dlThread.start();
                if (dlThread != null)
                    dlThread.getFile();
            }
	    } else {
	    	boolean newRecoveryFile = commonFileStuff.isRecoveryFile();
			if (!(newRecoveryFile == recoveryFile)) {
				if (newRecoveryFile) {
					recoveryFile = true;
					recov_text = getString(R.string.recover_game);
				} else {
					recoveryFile = false;
					recov_text = getString(R.string.Nothing);
				}
			}
            if (needToStartClient) {
				connState = LOGON_DGS;
                startClient();
            }
	    }
	}

	@Override
	public void onResume() {
		super.onResume();
		/*
        boolean notifierRunning = commonStuff.isNotifierRunning(am);
        errHist.writeErrorHistory("MainDGS.onResume, startedByNotifier:" + startedByNotifier
                + ", oneShot:" + oneShot
                + ", autoClient:" + autoClient
                + ", needToStartClient:" + needToStartClient
                + ", notifierRunning:" + notifierRunning);
        */
        adjustNotifierText();
	}
	
	@Override
	public void onPause() {
		super.onPause();
    }

    @Override
    public void onStop(){
	    /*
        boolean notifierRunning = commonStuff.isNotifierRunning(am);
        errHist.writeErrorHistory("MainDGS.onStop, startedByNotifier:" + startedByNotifier
                + ", oneShot:" + oneShot
                + ", autoClient:" + autoClient
                + ", needToStartClient:" + needToStartClient
                + ", notifierRunning:" + notifierRunning);
        */
        try {
            if (storMov.areStoredMovesLoaded()) {
                storMov.checkSaveStoredMoves();
            }
        }  catch (Exception ignored) {
        }
        try {
            errHist.checkSaveErrorHistoryData();
        }  catch (Exception ignored) {
        }
        super.onStop();
    }
	
	@Override
	public void onDestroy() {
        boolean notifierRunning = commonStuff.isNotifierRunning(am);
        if (dbgMain) {
            errHist.writeErrorHistory("MainDGS.onDestroy, startedByNotifier:" + startedByNotifier
                    + ", oneShot:" + oneShot
                    + ", autoClient:" + autoClient
                    + ", needToStartClient:" + needToStartClient
                    + ", notifierRunning:" + notifierRunning);
        }
		try {
			if (storMov.areStoredMovesLoaded()) {
				storMov.checkSaveStoredMoves();
			}
		}  catch (Exception ignored) {
		}
		try {
			errHist.checkSaveErrorHistoryData();
		}  catch (Exception ignored) {
		}
        doExitAppRestartNotifier();
	    super.onDestroy();
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig)
    {       
         super.onConfigurationChanged(newConfig);
         /*  Original homedgs class
         if (boardLayout.contentEquals(PrefsDGS.DYNAMIC)) {
        	finish();  
			Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName() );
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
			startActivity(i);
         }
         */
        if (boardLayout.contentEquals(PrefsDGS.DYNAMIC)) {
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            if (commonStuff.getScrOrientation(this) == Configuration.ORIENTATION_PORTRAIT) {
                setPortraitViews ();
            } else {
                setLandscapeViews ();
            }
        }
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
                if (buttonsType == BUTTONS_HOME) {
					moveTaskToBack(true);
                    return true;
                } else {
					displayParentButtons(buttonsType);
                    return true;
                }
            default:
        }
        return super.onKeyUp(keyCode, event);
    }

    private void setDebugFlags() {
		// 1=dbgMain, 10=dbgNotifier, 100=dbgThread, 1000=dbgStatus, 10,000=dbgStdMov
		if (dbg>0) {
			int dbgVal = dbg;
			int nxt = dbgVal/10;
			int dbgRem = dbgVal-(nxt*10);
			dbgMain = dbgRem>0;
			dbgVal = nxt;
			nxt = dbgVal/10;
			dbgRem = dbgVal-(nxt*10);
			dbgNotifier = dbgRem>0;
			dbgVal = nxt;
			nxt = dbgVal/10;
			dbgRem = dbgVal-(nxt*10);
			dbgThread = dbgRem>0;
			dbgVal = nxt;
			nxt = dbgVal/10;
			dbgRem = dbgVal-(nxt*10);
			dbgStatus = dbgRem>0;
			dbgVal = nxt;
			nxt = dbgVal/10;
			dbgRem = dbgVal-(nxt*10);
			dbgStdMov = dbgRem>0;
		}
	}
    
    @SuppressLint("SourceLockedOrientationActivity")
	private void setOrientationViews() {
    	if (boardLayout.contentEquals(PrefsDGS.PORTRAIT)) {
        	setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        	setPortraitViews ();
        } else if (boardLayout.contentEquals(PrefsDGS.LANDSCAPE)) {
        	setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        	setLandscapeViews ();
        } else {
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        	if (commonStuff.getScrOrientation(this) == Configuration.ORIENTATION_PORTRAIT) {
        		setPortraitViews ();
        	} else {
        		setLandscapeViews ();
        	}
        }
    }
    
    private void setPortraitViews () {
		currentBoardLayout = PrefsDGS.PORTRAIT;
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int display_width = (metrics.heightPixels < metrics.widthPixels) ? metrics.heightPixels : metrics.widthPixels;
		if (display_width > 300) {
			setContentView(R.layout.main);
			tmPrefs = (TextView) findViewById(R.id.mainTMPrefs);
			tmCredits = (TextView) findViewById(R.id.mainTMCredits);
			tmToggleNotifier = (TextView) findViewById(R.id.mainTMToggleNotifier);
			tmTutorial = (TextView) findViewById(R.id.mainTMTutorial);
			tmHelp = (TextView) findViewById(R.id.mainTMHelp);
			mainDesc = (TextView) findViewById(R.id.mainDesc);
			connStatus = (TextView) findViewById(R.id.mainStatus);
			client_buttons = (ListView) findViewById(R.id.mainButtons);
			version_txt = (TextView) findViewById(R.id.versionTxt);
			
		} else {
			setContentView(R.layout.mainsm);
			tmPrefs = (TextView) findViewById(R.id.mainTMPrefsSM);
			tmCredits = (TextView) findViewById(R.id.mainTMCreditsSM);
			tmToggleNotifier = (TextView) findViewById(R.id.mainTMStartNotifierSM);
			tmTutorial = (TextView) findViewById(R.id.mainTMTutorialSM);
			tmHelp = (TextView) findViewById(R.id.mainTMHelpSM);
			mainDesc = (TextView) findViewById(R.id.mainDescSM);
			connStatus = (TextView) findViewById(R.id.mainStatusSM);
			client_buttons = (ListView) findViewById(R.id.mainButtonsSM);
			version_txt = (TextView) findViewById(R.id.versionTxtSM);
		}
    }
    
    private void setLandscapeViews () {
		currentBoardLayout = PrefsDGS.LANDSCAPE;
		setContentView(R.layout.mainsm);
		tmPrefs = (TextView) findViewById(R.id.mainTMPrefsSM);
		tmCredits = (TextView) findViewById(R.id.mainTMCreditsSM);
		tmToggleNotifier = (TextView) findViewById(R.id.mainTMStartNotifierSM);
		tmTutorial = (TextView) findViewById(R.id.mainTMTutorialSM);
		tmHelp = (TextView) findViewById(R.id.mainTMHelpSM);
		mainDesc = (TextView) findViewById(R.id.mainDescSM);
		connStatus = (TextView) findViewById(R.id.mainStatusSM);
		client_buttons = (ListView) findViewById(R.id.mainButtonsSM);
		version_txt = (TextView) findViewById(R.id.versionTxtSM);
    }

	private void displayButtons (int buttType) {
        buttonsType = buttType;
        switch (buttType) {
            case BUTTONS_HOME:
                displayHomeButtons();
                break;
            case BUTTONS_EDIT:
                displayEditButtons();
                break;
            case BUTTONS_CLIENT:
                displayClientButtons();
                break;
            case BUTTONS_GAMELISTS:
                displayGameListsButtons();
                break;
            case BUTTONS_USERACTIONS:
                displayUserActionsButtons();
                break;
            case BUTTONS_UTILITIES:
                displayUtilitiesButtons();
                break;
            default:
                buttonsType = BUTTONS_CLIENT;
                displayClientButtons();
        }
    }

    private void displayParentButtons (int buttType) {
        switch (buttType) {
            case BUTTONS_HOME:
            case BUTTONS_EDIT:
            case BUTTONS_CLIENT:
            case BUTTONS_UTILITIES:
                displayButtons(BUTTONS_HOME);
                break;
            case BUTTONS_GAMELISTS:
            case BUTTONS_USERACTIONS:
            default:
                displayButtons(BUTTONS_CLIENT);
        }
    }

	private void flashButton(View v) {
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
	}

	private void displayHomeButtons () {
        buttonItems.clear();
        buttonItems.addAll(Arrays.asList(
                getString(R.string.DGSClient),
                getString(R.string.gameEditor),
                getString(R.string.utilities),
                getString(R.string.doneButton)
        ));
        buttonList.notifyDataSetChanged();
        mainDesc.setText(R.string.help);
        connStatus.setText(R.string.blank5);
		client_buttons.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				switch (position) {
					case 0:
					    startClient();
						break;
					case 1:
						displayButtons(BUTTONS_EDIT);
						break;
					case 2:
                        displayButtons(BUTTONS_UTILITIES);
						break;
                    case 3:
                        moveTaskToBack(true);
						//testUtilityComment();
                        break;
					default:
						connStatus.setText("clicked: " + position);
				}
			}
		});
	}

	private void displayEditButtons () {
        buttonItems.clear();
        buttonItems.addAll(Arrays.asList(
				getString(R.string.savedGame),
				getString(R.string.newGame),
				getString(R.string.grinderTitle),
				recov_text,
				getString(R.string.returnButton)
        ));
        buttonList.notifyDataSetChanged();
        mainDesc.setText(R.string.blank5);
		client_buttons.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				switch (position) {
                    case 0:
                        startSavedGames();
                        break;
                    case 1:
                        startNewGame();
                        break;
                    case 2:
                        startGrinder();
                        break;
                    case 3:
                        if (recoveryFile) {
                            startRecovery();
                        }
                        break;
					case 4:
						displayButtons(BUTTONS_HOME);
						break;
					default:
						connStatus.setText("clicked: " + position);
				}
			}
		});
	}

	private void displayClientButtons() {
        buttonItems.clear();
        buttonItems.addAll(Arrays.asList(
				getString(R.string.playButton),
				getString(R.string.Status),
				getString(R.string.gameLists),
				getString(R.string.userActions),
				getString(R.string.returnButton)
        ));
        buttonList.notifyDataSetChanged();
        mainDesc.setText(R.string.blank5);
		client_buttons.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				switch (position) {
					case 0:
						flashButton(v);
						CheckForMovesClick();
						break;
					case 1:
						flashButton(v);
						StatusClick();
						break;
					case 2:
						displayButtons(BUTTONS_GAMELISTS);
						break;
					case 3:
						displayButtons(BUTTONS_USERACTIONS);
						break;
					case 4:
                        displayButtons(BUTTONS_HOME);
						break;
					default:
						connStatus.setText("clicked: " + position);
				}
			}
		});
	}

	private void displayGameListsButtons () {
        buttonItems.clear();
        buttonItems.addAll(Arrays.asList(
				getString(R.string.waitingRoom),
				getString(R.string.runningGames),
				getString(R.string.finishedGames),
				getString(R.string.observedGames),
				getString(R.string.teamGames),
				getString(R.string.returnButton)
        ));
        buttonList.notifyDataSetChanged();
        mainDesc.setText(R.string.blank5);
		client_buttons.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				switch (position) {
					case 0:
						flashButton(v);
						WroomClick();
						break;
					case 1:
						flashButton(v);
						RunningGamesClick();
						break;
					case 2:
						flashButton(v);
						FinishedGamesClick();
						break;
					case 3:
						flashButton(v);
						ObservedGamesClick();
						break;
					case 4:
						flashButton(v);
						TeamGamesClick();
						break;
					default:
						displayButtons(BUTTONS_CLIENT);
				}
			}
		});
	}

	private void displayUserActionsButtons () {
        buttonItems.clear();
        buttonItems.addAll(Arrays.asList(
				getString(R.string.findUser),
				getString(R.string.Info),
				getString(R.string.InviteUser),
				getString(R.string.MessageUser),
				getString(R.string.GetUserGraph),
                getString(R.string.downLoadGame),
                getString(R.string.returnButton)
        ));
        buttonList.notifyDataSetChanged();
        mainDesc.setText(R.string.blank5);
		client_buttons.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				switch (position) {
					case 0:
						flashButton(v);
						FindUserClick();
						break;
					case 1:
						flashButton(v);
						GetInfoClick();
						break;
					case 2:
						flashButton(v);
						InviteClick();
						break;
					case 3:
						flashButton(v);
						MessageClick();
						break;
					case 4:
						flashButton(v);
						GetUserGraphClick();
						break;
                    case 5:
                        flashButton(v);
                        DownloadClick();
                        break;
					default:
						displayButtons(BUTTONS_CLIENT);
				}
			}
		});
	}

	private void displayUtilitiesButtons () {
        buttonItems.clear();
        buttonItems.addAll(Arrays.asList(
				getString(R.string.PredictedMoves),
				getString(R.string.editPhrases),
				getString(R.string.ErrorHistory),
				getString(R.string.returnButton)
        ));
        buttonList.notifyDataSetChanged();
        mainDesc.setText(R.string.blank5);
		client_buttons.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				switch (position) {
					case 0:
						StoredMovesClick();
						break;
					case 1:
                        EditPhrasesClick();
						break;
					case 2:
						ErrorHistoryDataClick();
						break;
					default:
						displayButtons(BUTTONS_HOME);
				}
			}
		});
	}

	private void startClient() {
		if (DGSUser.contentEquals("") || DGSPass.contentEquals("")) {
			final Intent helpIntent = commonStuff.helpDGS (commonStuff.HELP_FILE_PERMISSIONS, this);
			helpIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
			startActivityForResult(helpIntent, FWD_PREFS);
		} else {
            boolean notifierRunning = commonStuff.isNotifierRunning(am);
            /*
            errHist.writeErrorHistory("MainDGS.startClient, startedByNotifier:" + startedByNotifier
                    + ", oneShot:" + oneShot
                    + ", autoClient:" + autoClient
                    + ", needToStartClient:" + needToStartClient
                    + ", notifierRunning:" + notifierRunning);
            */

			if (mThread == null) {
				mThread = new DGSThread(this,ServerURL, myUserId, DGSPass, mHandler, (oneShot || autoClient));
				mThread.start();
			} else if (!mThread.isAlive()) {
				mThread.start();
			} else {
				restoreStatus(true, true, false, BUTTONS_CLIENT);
			}
			needToStartClient = false;
            displayButtons(BUTTONS_CLIENT);
            if (autoClient || oneShot) {
                CheckForMovesClick();
            }
            // TODO handle oneShot
		}
	}
	
	private void startSavedGames() {
		final Intent sgfGameIntent = new Intent(MainDGS.this,
				SavedGames.class);
		sgfGameIntent.putExtra("SGF", "SGF");
		sgfGameIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
		startActivityForResult(sgfGameIntent, SAVED_GAMES);
	}
	
	private void startNewGame() {
		final Intent newGameIntent = new Intent(MainDGS.this,
				NewGame.class);
		newGameIntent.putExtra("SGF", "SGF");
		newGameIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
		startActivityForResult(newGameIntent, NEW_GAME);
	}
	
	private void startGrinder() {
		final Intent grinderIntent = new Intent(MainDGS.this,
				GrinderMain.class);
		grinderIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
		startActivityForResult(grinderIntent, GRINDER);
	}
	
	private void startRecovery() {
		final Intent recovIntent = new Intent(MainDGS.this,
				GameBoard.class);
		recovIntent.putExtra("SGF", "");
		recovIntent.putExtra("FILE", commonFileStuff.getFullFileName(commonFileStuff.ANDGS_DIR, commonFileStuff.RECOVERYFILE));
		recovIntent.putExtra("GAMEACTION",BoardManager.GA_PLAY);
		recovIntent.putExtra("GAMESTATUS",BoardManager.GS_PLAY);
		recovIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
		startActivityForResult(recovIntent, RECOV_GAME);
	}
	
	private void startGameBoard(String sgf, String file) {
		final Intent recovIntent = new Intent(MainDGS.this,
				GameBoard.class);
		recovIntent.putExtra("SGF", sgf);
		recovIntent.putExtra("FILE", file);
		recovIntent.putExtra("GAMEACTION",BoardManager.GA_PLAY);
		recovIntent.putExtra("GAMESTATUS",BoardManager.GS_PLAY);
		recovIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
		startActivityForResult(recovIntent, GAME_BOARD);
	}
	
	private final DownloadHandler mDLHandler = new DownloadHandler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOADOK:
				String sgf = (String) msg.obj;
				startGameBoard(sgf, Environment.getExternalStorageDirectory().getName()
                        + File.separator
                        + defaultDir
                        + File.separator
                        + inFileName);  // need to supply a filename in case of a save
				break;
			case DOWNLOADERROR:
				String e = (String) msg.obj;
				Toast.makeText(ctw, "Download error:" + e, Toast.LENGTH_LONG).show();
				break;
			}	
		}
	};

	 public boolean onPrepareOptionsMenu(Menu menu) {
		 menu.clear();
		 MenuItem prefs_menu = menu.add(0, MENU_PREFS, 0,getString(R.string.Preferences));
		 prefs_menu.setIcon(R.drawable.ic_menu_preferences);
		 MenuItem credit_menu = menu.add(0, MENU_CREDITS, 0, getString(R.string.Credits));
		 credit_menu.setIcon(R.drawable.ic_menu_allfriends);
		 if (commonStuff.isNotifierRunning(am)) {
			 MenuItem notifier_menu = menu.add(0, MENU_NOTIFIER, 0, getString(R.string.StopNotifier));
			 notifier_menu.setIcon(R.drawable.ic_menu_stop);
		 } else {
			 MenuItem notifier_menu = menu.add(0, MENU_NOTIFIER, 0, getString(R.string.StartNotifier));
			 notifier_menu.setIcon(R.drawable.ic_menu_notifications);
		 }
		 
		 MenuItem tutorial_menu = menu.add(0, MENU_TUTORIAL, 0, getString(R.string.Tutorial));
		 tutorial_menu.setIcon(R.drawable.ic_menu_directions);  
		 MenuItem help_menu = menu.add(0, MENU_HELP, 0, getString(R.string.Help));
		 help_menu.setIcon(R.drawable.ic_menu_help);
		 /*
		 MenuItem settings_menu = menu.add(0, MENU_SETTINGS, 0,"Settings");
		 settings_menu.setIcon(android.R.drawable.ic_menu_preferences);
		 */
		 return super.onPrepareOptionsMenu(menu);
		 }
	 
	 public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
		 case MENU_PREFS:
			 doPrefs();
			 break;
		 case MENU_CREDITS:
			 doCredits();
			 break;
		 case MENU_HELP:
			 doHelp();
			 break;
		 case MENU_NOTIFIER:
			 doToggleNotifier();
			 break;
		 case MENU_TUTORIAL:
			 doTutorial();
			 break;
		 default:
				// nothing 
		 }
		 return false;
	 }
	 
	 private void doPrefs() {
		 final Intent prefsIntent = new Intent(MainDGS.this, PrefsDGS.class);
		 prefsIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
		 startActivityForResult(prefsIntent, GET_PREFS);
	 }
	 
	 private void doCredits() {
		 final Intent creditIntent = commonStuff.helpDGS (commonStuff.HELP_CREDITS, this);
		 creditIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
		 startActivityForResult(creditIntent, HELP_VIEW);
	 }

    private void adjustNotifierText() {
        if (commonStuff.isNotifierRunning(am)) {
            tmToggleNotifier.setText(getString(R.string.StopNotifier));
        } else {
            tmToggleNotifier.setText(getString(R.string.StartNotifier));
        }
    }
	 
	 private void doToggleNotifier() {
         if (commonStuff.isNotifierRunning(am)) {
             tmToggleNotifier.setText(getString(R.string.StartNotifier));
             // need to tel notifier to not restart before we stop it.
			 commonStuff.startStopNotifier(true, MainDGS.this, false, false);
			 commonStuff.startStopNotifier(false, MainDGS.this, false, false);
		 } else {
             tmToggleNotifier.setText(getString(R.string.StopNotifier));
			 commonStuff.startStopNotifier(true, MainDGS.this, true, false);
		 }
	 }

    private void doExitAppRestartNotifier() {
        if (commonStuff.isNotifierRunning(am)) {
			commonStuff.startStopNotifier(false, MainDGS.this, true, false);
        }
    }
	 
	 private void doTutorial() {
		 String [] buttonTexts = new String [2];
         buttonTexts[0] = getString(R.string.no);
         buttonTexts[1] = getString(R.string.yes);
         final Intent msgIntent = new Intent(this, MsgView.class);
         msgIntent.putExtra("TITLE", getString(R.string.Tutorial));
         msgIntent.putExtra("ButtonTexts", buttonTexts);
         msgIntent.putExtra("MsgId", "0");
         msgIntent.putExtra("MsgText", getString(R.string.GoToTutorial));
         msgIntent.putExtra("MsgHelpType", commonStuff.HELP_HELP);
         msgIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
         startActivityForResult(msgIntent, TUTORIAL_VIEW);
	 }
	 
	 private void doHelp() {
		 final Intent helpIntent = commonStuff.helpDGS (commonStuff.HELP_MAIN, this);
		 helpIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
		 startActivityForResult(helpIntent, HELP_VIEW);
	 }

//  TODO  the client stuff

    private void sendToDGS(String item) {
        String url;
        url = ServerURL + item;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
        restoreStatus(true, true, false, BUTTONS_CLIENT);
    }

    private void StoredMovesClick() {
        String [] sm;
        if (!storMov.areStoredMovesLoaded()) {
            statusList = new String [1];
            statusList[0] = getString(R.string.noPredictedMoves);
        } else {
            statusList = storMov.getAllStoredMoves();
            if (statusList.length < 1) {
                statusList = new String [1];
                statusList[0] = getString(R.string.NoGames);
            } else {
                for (int i = 0; i < statusList.length; i++) {
                    String[] e = statusList[i].split("!");
                    if (e.length == 3) {
                        long timeStamp;
                        try {
                            timeStamp = Long.parseLong(e[1], 10);
                        } catch (NumberFormatException ex) {
                            timeStamp = System.currentTimeMillis();
                        }
                        Date dt = new Date(timeStamp);
                        statusList[i] = e[0] + "! " + dt.toLocaleString() + "! " + e[2];
                    }
                }
            }
        }
        displayStatus(STORED_MOVES_VIEW, commonStuff.HELP_STOREDMOVES, getString(R.string.PredictedMoves));
    }

	private void EditPhrasesClick() {
        final Intent phraseIntent = new Intent(MainDGS.this, PhraseView.class);
        phraseIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
        startActivityForResult(phraseIntent, PHRASE_VIEW);
    }

    private void ErrorHistoryDataClick() {
        errHist.checkLoadErrorHistoryData();
        statusList = errHist.getCompressAllErrorHistoryData();
        if (statusList.length < 1) {
            statusList = new String [1];
            statusList[0] = getString(R.string.noErrors);
        } else {
            for (int i = 0; i < statusList.length; i++) {  //Make timestamp printable
                long timeStamp = 0;
                int p = 0;
                if (statusList[i].length() > 0) {
                    p = statusList[i].indexOf("!");
                    if (p<0) {
                        p = 0;
                    } else {
                        String e = statusList[i].substring(0,p);
                        try {
                            timeStamp = Long.parseLong(e, 10);
                        } catch (NumberFormatException ex) {
                            timeStamp = 0;
                        }
                    }
                }
                Date dt = new Date(timeStamp);
                statusList[i] = dt.toLocaleString() + " " + statusList[i].substring(p);
            }
        }
        displayStatus(ERROR_HISTORY_VIEW, commonStuff.HELP_ERRORHISTORY, getString(R.string.ErrorHistory));
    }

    private void DownloadClick() {
        if (connState == LOGGED_ON) {
            autoClient = false;
            returnToStatus = false;
            final EditText input = new EditText(ctw);
            input.setText(gameId);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            new AlertDialog.Builder(ctw)
                    .setTitle(R.string.SetDGSGameNumber)
                    .setMessage(R.string.EnterNumber)
                    .setView(input)
                    .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            gameId = "";
                            restoreStatus(true,true, false, BUTTONS_USERACTIONS); }})
                    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            gameId = input.getText().toString();
                            connState = DOWNLOAD_SGF;
                            mThread.getGame(gameId,"0","0","0","0","0",false);
                        }
                    }).show();
        }
    }

    private void FindUserClick() {
        if (connState == LOGGED_ON) {
            autoClient = false;
            returnToStatus = false;
            movePlayed = false;
			final Intent findUIntent = new Intent(this,FindUserView.class);
			findUIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
			startActivityForResult(findUIntent, FIND_USER_VIEW);
        }
    }

    private void GetUserGraphClick() {
        if (connState == LOGGED_ON) {
            autoClient = false;
            returnToStatus = false;
            movePlayed = false;
            GetUserGraph(lastUId, lastUserId, lastRegisterDate);
        }
    }

    private void GetUserGraph(String uId, String userId, String regDate) {
        final Intent msgIntent = new Intent(this, GetUserGraphView.class);
        msgIntent.putExtra("TOUID", uId);
        msgIntent.putExtra("TOUSERID", userId);
        msgIntent.putExtra("REGISTRATIONDATE", regDate);
        msgIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
        startActivityForResult(msgIntent, GET_USER_GRAPH_VIEW);
    }

    private void MessageClick() {
        if (connState == LOGGED_ON) {
            autoClient = false;
            returnToStatus = false;
            movePlayed = false;
            MessageUser(lastUId, lastUserId, "", "");
        }
    }

    private void MessageUser(String uId, String userId, String msgId, String oSubj) {
        final Intent msgIntent = new Intent(this, MessageUserView.class);
        msgIntent.putExtra("TOUID", uId);
        msgIntent.putExtra("TOUSERID", userId);
        msgIntent.putExtra("FORMSGID", msgId);
        msgIntent.putExtra("OLDSUBJECT", oSubj);
        msgIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
        startActivityForResult(msgIntent, MESSAGE_USER_VIEW);
    }

    private void GetInfoClick() {
        if (connState == LOGGED_ON) {
            autoClient = false;
            returnToStatus = false;
            movePlayed = false;
			final EditText input = new EditText(ctw);
			input.setText(lastUserId);
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			new AlertDialog.Builder(ctw)
					.setTitle(R.string.prefsUser)
					.setMessage(R.string.SetValue)
					.setView(input)
					.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							restoreStatus(true, true, false, BUTTONS_USERACTIONS); }})
					.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							connState = GET_INFO;
							String s = input.getText().toString().trim();
							if (myUserId.contentEquals(s) || s.contentEquals("")) {
								lastUserId = myUserId;
								mThread.getInfoMyUser();
							} else {
								lastUserId = s;
								mThread.getInfoUser(lastUserId);
							}
						}
					}).show();
        }
    }

    private void InviteClick() {
        if (connState == LOGGED_ON) {
            autoClient = false;
            returnToStatus = false;
            movePlayed = false;
            InviteUser();
        }
    }

    private void InviteUser() {
        //  this uses screen scrape
        final Intent inviteIntent = new Intent(this,InviteUserView.class);
        inviteIntent.putExtra("MYUSERID", myUserId);
        inviteIntent.putExtra("TOUSERID", lastUserId);
        inviteIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
        startActivityForResult(inviteIntent, INVITE_VIEW);
        //
    }

    private void ObservedGamesClick() {
        if (connState == LOGGED_ON) {
            autoClient = false;
            returnToStatus = false;
            movePlayed = false;
            connState = GET_OBSERVED_GAME_LIST;
            mThread.getShowGamesList(DGSThread.GAMES_OBSERVED);
        }
    }

    private void FinishedGamesClick() {
        if (connState == LOGGED_ON) {
            autoClient = false;
            returnToStatus = false;
            movePlayed = false;
            connState = GET_FINISHED_GAME_LIST;
            mThread.getShowGamesList(DGSThread.GAMES_FINISHED);
        }
    }

    private void RunningGamesClick() {
        if (connState == LOGGED_ON) {
            autoClient = false;
            returnToStatus = false;
            movePlayed = false;
            connState = GET_RUNNING_GAME_LIST;
            mThread.getShowGamesList(DGSThread.GAMES_RUNNING);
        }
    }

    private void TeamGamesClick() {
        if (connState == LOGGED_ON) {
            autoClient = false;
            returnToStatus = false;
            movePlayed = false;
            connState = GET_TEAM_GAME_LIST;
            mThread.getShowGamesList(DGSThread.GAMES_MULTIPLAYER);
        }
    }

    private void StatusClick() {
        if (connState == LOGGED_ON) {
            autoClient = false;
            actionStoredMoves = false;
            returnToStatus = true;
            getStatus();
        }
    }

    private void WroomClick() {
        if (connState == LOGGED_ON) {
            autoClient = false;
            returnToStatus = false;
            movePlayed = false;
            connState = GET_WROOM_LIST;
            mThread.getWroomList();
        }
    }

    private void CheckForMovesClick() {
        if (connState == LOGGED_ON) {
            autoClient = true;
            actionStoredMoves = true;
            if (!storMov.areStoredMovesLoaded ()) {
                storMov.loadStoredMoves();
            }
            returnToStatus = false;
            msgsSeen = new StringBuilder("");
            getStatus();
        }
    }
    /*
     * public void onResume() {
     *
     * }
     */

    private void restoreStatus(boolean ok, boolean more, boolean resetNotif, int buttons) {
        autoClient = false;
        returnToStatus = false;
        connState = LOGGED_ON;
        displayButtons(buttons);
        if (actionStoredMoves) {
            if (storMov.areStoredMovesLoaded()) {
                storMov.checkSaveStoredMoves();
            }
            actionStoredMoves = false;
        }
        if (!more && oneShot) {
            try {
                Thread.sleep(600);   // allow any message to server to be sent before exiting
            } catch (InterruptedException ignore) { }

            cleanUpAndReturn();
        } else {
            // reset notifications counters
            if (ok) {
                connStatus.setText(R.string.Ok);
            }
			if (resetNotif) {
				if (commonStuff.isNotifierRunning(am)) {
					commonStuff.startStopNotifier(true, this, true, true);
				}
			}
        }
    }

    private void gotoStatusLine(int inx) {
        statusResult = statusList[inx];
        if (statusResult.length() < 10)  { // TODO log error      10 is arbitrary
            restoreStatus(true, true, false, BUTTONS_CLIENT);
        } else
        if (statusResult.startsWith("G"))  {
            if (!gotoGame())
                restoreStatus(true, true, false, BUTTONS_CLIENT);
        } else if (statusResult.startsWith("M"))  {
            if (!gotoMsg())
                restoreStatus(true, true, false, BUTTONS_CLIENT);
        } else if (statusResult.startsWith("B"))  {
            if (!gotoBulletin())
                restoreStatus(true, true, false, BUTTONS_CLIENT);
        } else if (statusResult.startsWith("MPG"))  {
            if (!gotoMpg())
                restoreStatus(true, true, false, BUTTONS_CLIENT);
        } else {
            restoreStatus(true, true, false, BUTTONS_CLIENT);
        }
    }

    private void doNextAction() {
        if (autoClient) {
            gotoNext();
        } else if (returnToStatus) {
            getStatus();
        } else {
            restoreStatus(true, false, false, BUTTONS_CLIENT);
        }
    }

    private void gotoNext() {
        if (!gotoNextGame())
            if (!gotoNextMsg())
                if (!gotoNextBulletin())
                    if (!gotoNextMpg())
                        if (movePlayed && autoClient) {
                            getStatus();
                        } else {
                            restoreStatus(true, false, true, BUTTONS_CLIENT);
                        }
    }

    private Boolean gotoNextGame() {
        if (gameIndex < 0)
            return false;
		if (gameIndex < statusList.length) {
			while (!statusList[gameIndex].startsWith("G")) {
				gameIndex++;
				if (gameIndex >= statusList.length)
					return false;
			}
			statusResult = statusList[gameIndex];
			gameIndex++;
		} else {
			return false;
		}
        return gotoGame();
    }

    // ## G,game_id,'opponent_handle',player_color,'lastmove_date','time_remaining',game_action,game_status,move_id,tournament_id,shape_id,game_type,game_prio,'opponent_lastaccess_date',handicap

    private boolean gotoGame() {
        gameId = "0";
        timeLeft = "";
        game_action = "0";
        game_status = "UNKNOWN";
        moveId = "-1";
        handicap = "0";
        String [] elements;
        elements = statusResult.split(",");
        if (elements.length < 15)
            return false;
        gameId = elements[1].trim();
        // opponent_handle = elements[2].trim();
        //player_color = elements[3].trim();
        //lastmove_date = elements[4].trim();
        timeLeft = elements[5].trim();
        game_action = elements[6].trim();
        game_status = elements[7].trim();
        moveId = elements[8].trim();
        //tournament_id = elements[9].trim();
        //shape_id = elements[10].trim();
        //game_type = elements[11].trim();
        //game_prio = elements[12].trim();
        //opponent_lastaccess_date = elements[13].trim();
        handicap = elements[14].trim();
        if (!gameId.contentEquals("0")) {
            connState = GET_SGF;
            mThread.getGame(gameId, moveId, game_action, game_status, handicap, timeLeft, gameNotes);
            return true;
        }
        return false;
    }

    private boolean gotoNextMsg() {
        if (msgIndex < 0)
            return false;
        while (msgIndex < statusList.length
                && (!statusList[msgIndex].startsWith("M") && !statusList[msgIndex].startsWith("MPG")))
            msgIndex++;
        if (msgIndex >= statusList.length)
            return false;
        statusResult = statusList[msgIndex];
        msgIndex++;
        return gotoMsg();
    }

    // ## M,message_id,folder_id,type,'sender','subject','date'

    private boolean gotoMsg() {
        @SuppressWarnings("unused")
        String otherUid;
        msgId = "0";
        String [] elements;
        elements = statusResult.split(",");
        if (elements.length < 7)
            return false;
        msgId = elements[1].trim();
        if (autoClient) {
            if (msgsSeen.toString().contains(">" + msgId + "<")) { // skip if already seen
                return false;
            }
        }
        // msgFolder = elements[2].trim();  // - 2 = NEW-folder - 3 = REPLY-folder
        msgType = elements[3].trim();
//		otherUid = elements[4].trim();
        oldSubj = elements[5].trim();
//		date = elements[6].trim();

        if (!msgId.contentEquals("0")) {
            connState = GET_MSG;
            msgString = " id=" + msgId;
            mThread.getMSG(msgId, msgType, "1");  // get and move to read
            return true;
        }
        return false;
    }

    private boolean gotoNextBulletin() {
        if (bulletinIndex < 0)
            return false;
        while (bulletinIndex < statusList.length && !statusList[bulletinIndex].startsWith("B"))
            bulletinIndex++;
        if (bulletinIndex >= statusList.length)
            return false;
        statusResult = statusList[bulletinIndex];
        bulletinIndex++;
        return gotoBulletin();
    }

    // ## B,bulletin_id,target_type,category,'publish_time','expire_time','subject'

    private boolean gotoBulletin() {
        String [] elements;
        elements = statusResult.split(",");
        if (elements.length < 7)
            return false;
        String bulletinId = elements[1].trim();
        //target_type = elements[2].trim();
        //category = elements[3].trim();
        //publish_time = elements[4].trim();
        //expire_time = elements[5].trim();
        //subject = elements[6].trim();

        if (!bulletinId.contentEquals("0")) {
            connState = GET_BULLETIN;
            msgString = " id=" + bulletinId;
            mThread.getBulletin(bulletinId);
            return true;
        }
        return false;
    }

    private boolean gotoNextMpg() {
        mpgIndex = -1;  // TODO for it to fail
        if (mpgIndex < 0)
            return false;
        while (mpgIndex < statusList.length && !statusList[mpgIndex].startsWith("MPG"))
            mpgIndex++;
        if (mpgIndex >= statusList.length)
            return false;
        statusResult = statusList[mpgIndex];
        mpgIndex++;
        return gotoMpg();
    }

    // ## MPG,game_id,game_type,ruleset,size,lastchanged_date,ready_to_start

    private boolean gotoMpg() {  // TODO  These are mpg negotiations
        String mpgId = "0";
        String [] elements;
        elements = statusResult.split(",");
        if (elements.length < 7)
            return false;
        mpgId = elements[1].trim();
        String mpgType = elements[2].trim();
        //ruleset = elements[3].trim();
        //size = elements[4].trim();
        //lastchanged_date = elements[5].trim();
        //ready_to_start = elements[6].trim();

        if (!mpgId.contentEquals("0")) {
            connState = GET_MSG;
            msgString = " id=" + mpgId;
            mThread.getMSG(mpgId, mpgType, "1");  // get and move to read  // a different get...
            return true;
        }
        return false;
    }

    private void sendDGShandicap(String gid, String mov, String msg, boolean gameNotes, String note) {
        movePlayed = true;
        connState = SEND_MOVE;
        moveString = "H["+mov+"]";
        mThread.sendHandicap(gid, mov, msg, gameNotes, note);
    }

    private void sendDGSresign(String gid, String movid, String colr, String msg, boolean gameNotes, String note) {
        movePlayed = true;
        connState = SEND_MOVE;
        moveString = colr+" "+getString(R.string.Resign)+" ";
        mThread.sendResign(gid, movid, msg, gameNotes, note);
    }

    private void sendDGSmove(String gid, String movid, String colr, String mov, String msg, boolean gameNotes, String note) {
        movePlayed = true;
        connState = SEND_MOVE;
        moveString = colr+"["+mov+"]";
        if (mov.contentEquals("")) {
            mThread.sendPass(gid, movid, msg, gameNotes, note);
        } else {
            mThread.sendMove(gid, movid, mov, msg, gameNotes, note);
        }
    }

    private void sendDGSscore(String gid, String movid, String mov, String msg) {
        movePlayed = true;
        connState = SEND_MOVE;
        mThread.sendScore(gid, movid, mov, msg);
    }

    private void acceptDGSscore(String gid, String movid, String msg) {
        movePlayed = true;
        connState = SEND_MOVE;
        mThread.sendAcceptScore(gid, movid, msg);
    }

    private void deleteDGSgame(String gid, String movid, String msg) {
        movePlayed = true;
        connState = SEND_MOVE;
        mThread.deleteDGSgame(gid, movid, msg);
    }

    private void sendDGSgameNotes(String gid, String note) {
        movePlayed = true;
        connState = SEND_MOVE;
        moveString = "";
        mThread.sendGameNote(gid, note);
    }

    private void sendAcceptInvitation() {
		utilityCommentEditText = new EditText(ctw);
		utilityCommentEditText.setText("");
		utilityCommentEditText.setHint(getString(R.string.Comment));
		utilityCommentEditText.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				final Intent phraseIntent = new Intent(ctw, PhraseView.class);
				phraseIntent.putExtra("BOARDLAYOUT", boardLayout);
				startActivityForResult(phraseIntent, UTILITY_PHRASE_VIEW);
				return true;
			}});
        new AlertDialog.Builder(ctw)
                .setTitle(R.string.accept)
                .setView(utilityCommentEditText)
                .setNegativeButton(R.string.skipButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        doNextAction(); }})
                .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        connState = SEND_MSG;
                        mThread.sendAcceptInvitation(msgId,utilityCommentEditText.getText().toString().trim());
                    }
                }).show();
    }
/*
	private void testUtilityComment() {
    	utilityCommentEditText = new EditText(ctw);
		utilityCommentEditText.setText("");
		utilityCommentEditText.setHint(getString(R.string.Comment));
		utilityCommentEditText.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				final Intent phraseIntent = new Intent(ctw, PhraseView.class);
				phraseIntent.putExtra("BOARDLAYOUT", boardLayout);
				startActivityForResult(phraseIntent, UTILITY_PHRASE_VIEW);
				return true;
			}});
		new AlertDialog.Builder(ctw)
				.setTitle(R.string.accept)
				.setView(utilityCommentEditText)
				.setNegativeButton(R.string.skipButton, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Toast.makeText(ctw, "Cancel testUtilityComment, value: " + utilityCommentEditText.getText().toString().trim(), Toast.LENGTH_LONG).show();
					}})
				.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Toast.makeText(ctw, "OK testUtilityComment, value: " + utilityCommentEditText.getText().toString().trim(), Toast.LENGTH_LONG).show();
					}
				}).show();
	}

 */

    private void sendDeclineInvitation() {
		utilityCommentEditText = new EditText(ctw);
		utilityCommentEditText.setText("");
		utilityCommentEditText.setHint(getString(R.string.Comment));
		utilityCommentEditText.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				final Intent phraseIntent = new Intent(ctw, PhraseView.class);
				phraseIntent.putExtra("BOARDLAYOUT", boardLayout);
				startActivityForResult(phraseIntent, UTILITY_PHRASE_VIEW);
				return true;
			}});
        new AlertDialog.Builder(ctw)
                .setTitle(R.string.decline)
                .setView(utilityCommentEditText)
                .setNegativeButton(R.string.skipButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        doNextAction(); }})
                .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        connState = SEND_MSG;
                        mThread.sendDeclineInvitation(msgId,utilityCommentEditText.getText().toString().trim());
                    }
                }).show();
    }

    private void DisplayUserInfo(String s) {
        JSONObject jo;
        try {
            jo = new JSONObject(s);
        } catch (JSONException e) {
            return;
        }
        StringBuilder msg = new StringBuilder(200);
        String eS;
        try { eS = jo.getString("error");
        } catch (JSONException e) { eS = ""; }
        if (!eS.contentEquals("")) {
            msg.append(s);
        } else {
            msg.append("id: ");
            try {
                lastUId = jo.getString("id");
                msg.append(lastUId);
            } catch (JSONException ignored) { lastUId = ""; }
            msg.append("\nUser Id: ");
            try {
                lastUserId = jo.getString("handle");
                msg.append(lastUserId);
            } catch (JSONException ignored) { lastUserId = ""; }
            msg.append("\nType: ");
            try {
                msg.append(jo.getString("type"));
            } catch (JSONException ignored) { }
            msg.append("\nName: ");
            try {
                lastUserName = jo.getString("name");
                msg.append(lastUserName);
            } catch (JSONException ignored) { lastUserName = ""; }
            msg.append("\nCountry: ");
            try {
                msg.append(jo.getString("country"));
            } catch (JSONException ignored) { }
            msg.append("\nVacation Left: ");
            try {
                msg.append(jo.getString("vacation_left"));
            } catch (JSONException ignored) { }
            msg.append("\nVacation On: ");
            try {
                msg.append(jo.getString("vacation_on"));
            } catch (JSONException ignored) { }
            msg.append("\nRegister Date: ");
            try {
                lastRegisterDate = jo.getString("register_date");
                msg.append(lastRegisterDate);
            } catch (JSONException ignored) { lastRegisterDate = ""; }
            msg.append("\nLast Access: ");
            try {
                msg.append(jo.getString("last_access"));
            } catch (JSONException ignored) { }
            msg.append("\nLast Quick Access: ");
            try {
                msg.append(jo.getString("last_quick_access"));
            } catch (JSONException ignored) { }
            msg.append("\nLast Move: ");
            try {
                msg.append(jo.getString("last_move"));
            } catch (JSONException ignored) { }
            msg.append("\nRating Status: ");
            try {
                msg.append(jo.getString("rating_status"));
            } catch (JSONException ignored) { }
            msg.append("\nRating: ");
            try {
                msg.append(jo.getString("rating"));
            } catch (JSONException ignored) { }
            msg.append("\nRating ELO: ");
            try {
                msg.append(jo.getString("rating_elo"));
            } catch (JSONException ignored) { }
            msg.append("\nRank: ");
            try {
                msg.append(jo.getString("rank"));
            } catch (JSONException ignored) { }
            msg.append("\nOpen Match: ");
            try {
                msg.append(jo.getString("open_match"));
            } catch (JSONException ignored) { }
            msg.append("\nGames Running: ");
            try {
                msg.append(jo.getString("games_running"));
            } catch (JSONException ignored) { }
            msg.append("\nGames Finished: ");
            try {
                msg.append(jo.getString("games_finished"));
            } catch (JSONException ignored) { }
            msg.append("\nGames Rated: ");
            try {
                msg.append(jo.getString("games_rated"));
            } catch (JSONException ignored) { }
            msg.append("\nGames Won: ");
            try {
                msg.append(jo.getString("games_won"));
            } catch (JSONException ignored) { }
            msg.append("\nGames Lost: ");
            try {
                msg.append(jo.getString("games_lost"));
            } catch (JSONException ignored) { }
            msg.append("\nGames Multi Player: ");
            try {
                msg.append(jo.getString("games_mpg"));
            } catch (JSONException ignored) { }
        }
        String [] buttonTexts = new String [4];
        buttonTexts[0] = getString(R.string.doneButton);
        buttonTexts[1] = getString(R.string.MessageUser);
        buttonTexts[2] = getString(R.string.InviteUser);
        buttonTexts[3] = getString(R.string.GetUserGraph);
        final Intent msgIntent = new Intent(this, MsgView.class);
        msgIntent.putExtra("TITLE", getString(R.string.Info));
        msgIntent.putExtra("ButtonTexts", buttonTexts);
        msgIntent.putExtra("MsgId", "0");
        msgIntent.putExtra("MsgText", msg.toString());
        msgIntent.putExtra("MsgHelpType", commonStuff.HELP_USERINFO);
        msgIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
        startActivityForResult(msgIntent, DISPLAY_INFO_VIEW);
    }


    private void displayMsg(String mid, String s, String [] buttonTexts, String title) {
        StringBuilder msg = new StringBuilder("");
        msgId = mid;
        JSONObject jo;
        try {
            jo = new JSONObject(s);
            String eS;
            @SuppressWarnings("unused")
            String m_mid = "";
            @SuppressWarnings("unused")
            String m_type = "";
            try {
                eS = jo.getString("error");
            } catch (JSONException e) { eS = ""; }
            if (!eS.contentEquals("")) {
                msg.append(s);
            } else {
                try {
                    m_mid = jo.getString("id");
                } catch (JSONException ignored) { }
                try {
                    m_type = jo.getString("type");
                } catch (JSONException ignored) { }
                msg.append(getString(R.string.Date)).append(": ");
                try {
                    msg.append(jo.getString("created_at"));
                } catch (JSONException ignored) { }
                msg.append("\n").append(getString(R.string.From)).append(" ");
                try {
                    JSONObject uo = jo.getJSONObject("user_from");
                    lastUId = uo.getString("id");
                    msg.append(uo.getString("name")).append(" (").append(uo.getString("handle")).append(")");
                } catch (JSONException ignored) { }
                msg.append("\n").append(getString(R.string.Subject)).append(" ");
                try {
                    msg.append(jo.getString("subject"));
                } catch (JSONException ignored) { }
                msg.append("\n").append(getString(R.string.message)).append(" ");
                try {
                    msg.append("\n").append(jo.getString("text"));
                } catch (JSONException ignored) { }
            }
        } catch (JSONException e) {
            msg.append(s);
        }
        final Intent msgIntent = new Intent(this, MsgView.class);
        msgIntent.putExtra("TITLE", title);
        msgIntent.putExtra("ButtonTexts", buttonTexts);
        msgIntent.putExtra("MsgId", msgId);
        msgIntent.putExtra("MsgText", msg.toString());
        msgIntent.putExtra("MsgHelpType", commonStuff.HELP_MSG);
        msgIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
        startActivityForResult(msgIntent, DISPLAY_MSG_VIEW);
        //if (m_type.contentEquals("NORMAL"))
        //	mThread.moveMSG(m_mid, "1");
    }

    private void displayBulletin(String mid, String s) {
        StringBuilder msg = new StringBuilder();
        msgId = mid;
        JSONObject jo;
        try {
            jo = new JSONObject(s);
            String eS;
            @SuppressWarnings("unused")
            String m_mid = "";
            @SuppressWarnings("unused")
            String m_type = "";
            try {
                eS = jo.getString("error");
            } catch (JSONException e) { eS = ""; }
            if (!eS.contentEquals("")) {
                msg.append(s);
            } else {
                try {
                    m_mid = jo.getString("id");
                } catch (JSONException ignored) { }
                try {
                    m_type = jo.getString("target_type");
                } catch (JSONException ignored) { }
                msg.append(getString(R.string.Date)).append(": ");
                try {
                    msg.append(jo.getString("time_published"));
                } catch (JSONException ignored) { }
                msg.append("\n").append(getString(R.string.From)).append(" ");
                try {
                    JSONObject uo = jo.getJSONObject("author");
                    lastUId = uo.getString("id");
                    msg.append(uo.getString("name")).append(" (").append(uo.getString("handle")).append(")");
                } catch (JSONException ignored) { }
                msg.append("\n").append(getString(R.string.Subject)).append(" ");
                try {
                    msg.append(jo.getString("subject"));
                } catch (JSONException ignored) { }
                msg.append("\n").append(getString(R.string.message)).append(" ");
                try {
                    msg.append("\n").append(jo.getString("text"));
                } catch (JSONException ignored) { }
            }
        } catch (JSONException e) {
            msg.append(s);
        }
        connState = DISPLAY_BULLETIN;
        String title = "Bulletin";  // TODO
        String [] buttonTexts;
        buttonTexts = new String [3];
        buttonTexts[0] = getString(R.string.doneButton);
        buttonTexts[1] = getString(R.string.skipButton);
        buttonTexts[2] = getString(R.string.skipAllButton);
        final Intent msgIntent = new Intent(this, MsgView.class);
        msgIntent.putExtra("TITLE", title);
        msgIntent.putExtra("ButtonTexts", buttonTexts);
        msgIntent.putExtra("MsgId", msgId);
        msgIntent.putExtra("MsgText", msg.toString());
        msgIntent.putExtra("MsgHelpType", commonStuff.HELP_MSG);
        msgIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
        startActivityForResult(msgIntent, DISPLAY_MSG_VIEW);
    }

    private void getStatus() {
        movePlayed = false;
        connState = GET_STATUS_LIST;
        mThread.getStatusList(gameOrder);
    }

    private void displayStatus(int vw, int ht, String title) {
        final Intent dispIntent = new Intent(this, StatusView.class);
        dispIntent.putExtra("TITLE", title);
        dispIntent.putExtra("HelpType", ht);
        dispIntent.putExtra("STATUSLIST", statusList);
        dispIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
        startActivityForResult(dispIntent, vw);
    }

    private void cleanUpAndReturn() {
    	/*
        try {
            mThread.stopThread();
            mThread.join(DGSNotifier.JOINWAIT);           //  TODO hangs here ???
        } catch (InterruptedException ignored) {
        }
        */
    	oneShot=false;
        Intent mIntent = new Intent();
        setResult(RESULT_OK, mIntent);
        finish();
    }

    public static String[] removeStringElement(String[] original, int element){
        if (element < 0 || element > original.length - 1) return original;
        String[] n = new String[original.length - 1];
        if (element > 0) {
            System.arraycopy(original, 0, n, 0, element);
            if (element < original.length - 1) System.arraycopy(original, element+1, n, element, original.length - element - 1);
        } else {
            System.arraycopy(original, 1, n, 0, original.length - 1);
        }
        return n;
    }

    private void processGamesList(String s) {
        // JSON parse response ->  <gid>,<uid>,<uname>
        JSONObject joGameList = null;
        JSONObject uoGameList;
        try {
            joGameList = new JSONObject(s);
        } catch (JSONException e) {
            s = getString(R.string.NoGames);
        }
        StringBuilder glmsg = new StringBuilder();
        String errString;
        String myid;
        String otherid;
        String othername;
        try {
            errString = joGameList != null ? joGameList.getString("error") : "";
        } catch (JSONException e) { errString = ""; }
        if (!errString.contentEquals("")) {
            glmsg.append(s);
        } else try {
            JSONArray games = joGameList != null ? joGameList.getJSONArray("list_result") : null;
            int numElements = joGameList != null ? joGameList.getInt("list_size") : 0;
            if (numElements < 1) {
                glmsg.append(getString(R.string.NoGames));
            } else
                for (int i = 0; i < numElements; i++) {
                    JSONObject je = games.getJSONObject(i);
                    try {
                        glmsg.append(je.getString("id"));  // add game-id
                    } catch (JSONException e) { glmsg.append("0"); }
                    glmsg.append(",");
                    try {
                        myid = je.getString("my_id");  //
                    } catch (JSONException e) { myid = "0"; }
                    try {
                        uoGameList = je.getJSONObject("black_user");
                        otherid = uoGameList.getString("id");
                        othername = uoGameList.getString("name");
                    } catch (JSONException e) { otherid = "0"; othername = getString(R.string.NotFound); }
                    if (myid.contentEquals(otherid) || otherid.contentEquals("0")) {
                        try {
                            uoGameList = je.getJSONObject("white_user");
                            otherid = uoGameList.getString("id");
                            othername = uoGameList.getString("name");
                        } catch (JSONException e) { otherid = "0"; othername = getString(R.string.NotFound); }
                    }
                    glmsg.append(otherid).append(",").append(othername);
                    glmsg.append("\n");
                }
        }  catch (JSONException e) { glmsg.append(s); }
        statusList = glmsg.toString().split("\n");
        String title = "";
        switch (connState) {
            case GET_RUNNING_GAME_LIST:
                title = getString(R.string.runningGames);
                break;
            case GET_FINISHED_GAME_LIST:
                title = getString(R.string.finishedGames);
                break;
            case GET_TEAM_GAME_LIST:
                title = getString(R.string.teamGames);
                break;
            case GET_OBSERVED_GAME_LIST:
                title = getString(R.string.observedGames);
                break;
        }
        connState = DISPLAY_GAME_LIST;
        displayStatus(GAMES_VIEW,commonStuff.HELP_GAMES,title);
    }

    @SuppressWarnings("unused")
    private String parseWroomEntry (JSONObject gameOffer, boolean summary) {
        String rslt;
        String wrId;
        String userid = "";
        String handle = "";
        String username = "";
        String userrating = "";
        String usercountry = "";
        String createdAt = "";
        String remainingOffers = "";
        String comment = "";
        String gameType = "";
        String players = "";
        String handicapType = "";
        String rated = "";
        String ruleSet = "";
        String gameSize = "";
        String gameKomi = "";
        String handicap = "";
        String handicapMode = "";
        String handicapAdjustment = "";
        String weekendClock = "";
        String timeMode = "";
        String timeLimit = "";
        String timeMain = "";
        String timeByomi = "";
        String timePeriods = "";
        String join = "";
        String joinWarn = "";
        String joinErr = "";
        String calcType = "";
        String calcColor = "";
        String calcHandicap = "";
        String calcKomi = "";

        try {
            wrId = gameOffer.getString("id");
        } catch (JSONException e) { wrId = "0"; }
        try {
            JSONObject userObject = gameOffer.getJSONObject("user");
            try {
                userid = userObject.getString("id");
            } catch (JSONException ignored) {  }
            try {
                handle = userObject.getString("handle");
            } catch (JSONException ignored) {  }
            try {
                username = userObject.getString("name");
            } catch (JSONException ignored) {  }
            try {
                userrating = userObject.getString("rating");
            } catch (JSONException ignored) {  }
            try {
                usercountry = userObject.getString("country");
            } catch (JSONException ignored) {  }
        } catch (JSONException ignored) {  }
        try {
            createdAt = gameOffer.getString("created_at");
        } catch (JSONException ignored) {  }
        try {
            remainingOffers = gameOffer.getString("count_offers");
        } catch (JSONException ignored) {  }
        try {
            comment = gameOffer.getString("comment");
        } catch (JSONException ignored) {  }
        try {
            gameType = gameOffer.getString("game_type");
        } catch (JSONException ignored) {  }
        try {
            players = gameOffer.getString("game_players");
        } catch (JSONException ignored) {  }
        try {
            handicapType = gameOffer.getString("handicap_type");
        } catch (JSONException ignored) {  }
        try {
            rated = gameOffer.getString("rated");
        } catch (JSONException ignored) {  }
        try {
            ruleSet = gameOffer.getString("ruleset");
        } catch (JSONException ignored) {  }
        try {
            gameSize = gameOffer.getString("size");
        } catch (JSONException ignored) {  }
        try {
            gameKomi = gameOffer.getString("komi");
        } catch (JSONException ignored) {  }
        try {
            handicap = gameOffer.getString("handicap");
        } catch (JSONException ignored) {  }
        try {
            handicapMode = gameOffer.getString("handicap_mode");
        } catch (JSONException ignored) {  }
        try {
            handicapAdjustment = gameOffer.getString("adjust_handicap");
        } catch (JSONException ignored) {  }
        try {
            weekendClock = gameOffer.getString("time_weekend_clock");
        } catch (JSONException ignored) {  }
        try {
            timeMode = gameOffer.getString("time_mode");
        } catch (JSONException ignored) {  }
        try {
            timeLimit = gameOffer.getString("time_limit");
        } catch (JSONException ignored) {  }
        try {
            timeMain = gameOffer.getString("time_main");
        } catch (JSONException ignored) {  }
        try {
            timeByomi = gameOffer.getString("time_byo");
        } catch (JSONException ignored) {  }
        try {
            timePeriods = gameOffer.getString("time_periods");
        } catch (JSONException ignored) {  }
        try {
            join = gameOffer.getString("join");
        } catch (JSONException ignored) {  }
        try {
            joinWarn = gameOffer.getString("join_warn");
        } catch (JSONException ignored) {  }
        try {
            joinErr = gameOffer.getString("join_err");
        } catch (JSONException ignored) {  }
        try {
            calcType = gameOffer.getString("calc_type");;
        } catch (JSONException ignored) {  }
        try {
            calcColor = gameOffer.getString("calc_color");
        } catch (JSONException ignored) {  }
        try {
            calcHandicap = gameOffer.getString("calc_handicap");
        } catch (JSONException ignored) {  }
        try {
            calcKomi = gameOffer.getString("calc_komi");
        } catch (JSONException ignored) {  }

        String rf;
        if (rated.contentEquals("1")) {
            rf = "Rated";
        } else {
            rf = "Free";
        }
        String we;
        if (weekendClock.contentEquals("1")) {
            we = "WeekendClock";
        } else {
            we = "NoWeekendClock";
        }
        if (summary) {
            if (join.contentEquals("1")) {
                rslt = wrId + ", " +
                        username + ", " + handle + ", " +
                        userrating + ", " +
                        comment + ", " +
                        gameType + ", " +
                        ruleSet + ", " +
                        gameSize + "x" + gameSize + ", " +
                        handicapMode + ", " +
                        rf + ", " +
                        timeLimit;
            }
            else {
                rslt = "";
            }
        } else {
            if (join.contentEquals("1")) {
                rslt = wrId + ", " +
                        username + ", " + handle + ", " +
                        userrating + ", " +
                        comment + ", " +
                        gameType + ", " +
                        ruleSet + ", " +
                        gameSize + "x" + gameSize + ", " +
                        calcColor + ", " +
                        "H" + calcHandicap + ", " +
                        "K" + calcKomi + ", " +
                        rf + ", " +
                        timeLimit + ", " +
                        we;
            }
            else {
                rslt = "0, may not join";
            }
        }
        return rslt;
    }

    private void processWroomList(String s) {
        JSONObject joWroomList = null;
        StringBuilder glmsg = new StringBuilder("");
        try {
            joWroomList = new JSONObject(s);
        } catch (JSONException e) {
            s = getString(R.string.NoGames);
        }
        String rslt;
        String errString;
        try {
            errString = joWroomList != null ? joWroomList.getString("error") : "";
        } catch (JSONException e) { errString = ""; }
        if (!errString.contentEquals("")) {
            glmsg.append(s);
        } else try {
            JSONArray entries = joWroomList != null ? joWroomList.getJSONArray("list_result") : null;
            int numElements = joWroomList != null ? joWroomList.getInt("list_size") : 0;
            if (numElements < 1) {
                glmsg.append(getString(R.string.NoGames));
            } else
                for (int i = 0; i < numElements; i++) {
                    JSONObject gameOffer = entries.getJSONObject(i);
                    rslt = parseWroomEntry(gameOffer, true);
                    if (!rslt.contentEquals(""))
                        glmsg.append(rslt).append("\n");
                }
        }  catch (JSONException e) { glmsg.append(s); }
        statusList = glmsg.toString().split("\n");
        connState = DISPLAY_WROOM_LIST;
        displayStatus(GAMES_VIEW,commonStuff.HELP_GAMES,getString(R.string.waitingRoom));
    }

    private void processWroomInfo(String s) {
        JSONObject joWroomInfo = null;
        try {
            joWroomInfo = new JSONObject(s);
        } catch (JSONException e) {
            s = getString(R.string.NoGames);
        }
        String rslt;
        String errString;
        try {
            errString = joWroomInfo != null ? joWroomInfo.getString("error") : "";
        } catch (JSONException e) { errString = ""; }
        if (!errString.contentEquals("")) {
            rslt = s;
        } else {
            rslt = parseWroomEntry(joWroomInfo, false);
        }
        String [] elements = statusResult.split(",");
        final String m_wrid = elements[0].trim();
        final String m_handle = elements[2].trim();
        connState = DISPLAY_WROOM_INFO;
        new AlertDialog.Builder(ctw)
                .setTitle(R.string.JoinGame)
                .setMessage(rslt)
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        restoreStatus(true, true, false, BUTTONS_CLIENT);
                    }})
                .setNeutralButton(R.string.Info, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        connState = GET_INFO;
                        mThread.getInfoUser(m_handle);
                    }})
                .setPositiveButton(R.string.JoinGame, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        connState = JOIN_WROOM_GAME;
                        mThread.joinWroomGame(m_wrid);
                    }
                })
                .show();
    }


    private final MsgHandler mHandler = new MsgHandler()
    {
        private String s;
        String errString = "";
        private String [] sgfGameRslt;
        private String [] msgRslt;

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case LOGON:
                    s = (String) msg.obj;
                    if (s == null) s = "";
                    if (s.contentEquals("Ok")) {
                        if (connState == LOGON_DGS) {
                            if (autoClient) {
                                msgsSeen = new StringBuilder("");
                                getStatus();
                            } else {
                                restoreStatus(true, true, false, BUTTONS_CLIENT);
                            }
                        } else {
                            connStatus.setText(getString(R.string.Ok));  // let the action continue
                        }
                    }  else {
                        connStatus.setText(getString(R.string.Failed)+s);
                        restoreStatus(false, true, false, BUTTONS_CLIENT);
                    }
                    break;

                case STATUSLIST:
                    if (connState != GET_STATUS_LIST) break;
                    gameIndex = 0;
                    msgIndex = 0;
                    bulletinIndex = 0;
                    mpgIndex = 0;
                    s = (String) msg.obj;
                    if (s == null) s = "";
                    statusList = s.split("\n");
                    boolean statusData = false;
                    for (int i = statusList.length - 1; i > -1; i--) {
                        if (statusList[i].startsWith("#") || statusList[i].startsWith(" ")) {  // remove all comments and empty lines
                            statusList = removeStringElement(statusList,i);
                        } else {
                            //if (!statusList[i].startsWith("[#"))  // keep warnings
                            statusData = true;
                        }
                    }
                    if (autoClient) {
                        if (statusData) {
                            gotoNext();
                        } else {
                            restoreStatus(true, !oneShot, false, BUTTONS_CLIENT);
                        }
                    } else if (returnToStatus){  // doing a STATUS command
                        if (!statusData) {
                            statusList = getString(R.string.EmptyStatus).split("\n");
                        }
                        connState = DISPLAY_STATUS;
                        displayStatus(STATUS_VIEW,commonStuff.HELP_STATUS,getString(R.string.Status));
                    } else {
                        restoreStatus(true, !oneShot, false, BUTTONS_CLIENT);
                    }
                    break;

                case GAMESLIST:
                    if (!(connState == GET_RUNNING_GAME_LIST ||
                            connState == GET_FINISHED_GAME_LIST ||
                            connState == GET_TEAM_GAME_LIST ||
                            connState == GET_OBSERVED_GAME_LIST)) break;
                    s = (String) msg.obj;
                    if (s == null) s = "";
                    if (s.contentEquals("")) {
                        s = getString(R.string.NoGames);
                    }
                    processGamesList(s);
                    break;

                case WROOMLISTRESULT:
                    if (connState != GET_WROOM_LIST) break;
                    s = (String) msg.obj;
                    if (s == null) s = "";
                    if (s.contentEquals("")) {
                        s = getString(R.string.NoGames);
                    }
                    processWroomList(s);
                    break;

                case WROOMINFORESULT:
                    if (connState != GET_WROOM_INFO) break;
                    s = (String) msg.obj;
                    if (s == null) s = "";
                    if (s.contentEquals("")) {
                        s = getString(R.string.NoGames);
                    }
                    processWroomInfo(s);
                    break;

                case JOINWROOMGAMERESULT:
                    if (connState != JOIN_WROOM_GAME) break;
                    s = (String) msg.obj;
                    JSONObject joJoinRslt;
                    try {
                        joJoinRslt = new JSONObject(s);
                        errString = joJoinRslt.getString("error");
                    } catch (JSONException e) {
                        errString = "";
                    }
                    if (!errString.contentEquals("")) {
                        connStatus.setText(errString);
                        restoreStatus(false, true, false, BUTTONS_CLIENT);
                        break;
                    }
                    restoreStatus(true, true, false, BUTTONS_CLIENT);
                    break;

                case SGF:
                    if (connState != GET_SGF && connState != DOWNLOAD_SGF) break;
                    sgfGameRslt = (String []) msg.obj;
                    if (sgfGameRslt == null) {
                        doNextAction();
                        break;
                    }
                    sgf = sgfGameRslt[0];
                    if (!sgf.startsWith("(")) {
                        connStatus.setText(sgf);
                        restoreStatus(false, true, false, BUTTONS_CLIENT);
                        break;
                    }
                    //connStatus.setText(sgfGameRslt[8]);  // testing, remove
                    s = sgfGameRslt[1];   // s == gid
                    moveId = sgfGameRslt[2];
                    game_action = sgfGameRslt[3];
                    game_status = sgfGameRslt[4];
                    handicap = sgfGameRslt[5];
                    timeLeft = sgfGameRslt[6];
                    gameNotes = false;
                    score_data = null;
                    gameNoteTxt = "";
                    String dame = "";
                    String neutral = "";
                    String white_stones = "";
                    String black_stones = "";
                    String white_dead = "";
                    String black_dead = "";
                    String white_territory = "";
                    String black_territory = "";
                    if (sgfGameRslt[7].contentEquals("score")) {
                        if (game_action.contentEquals(BoardManager.GA_SCORING)) {
                            score_data = new String [PlayDGS.SCORE_DATA_SIZE];
                            JSONObject joScoreRslt;
                            //String ruleset = "";
                            //String score = "";
                            try {
                                joScoreRslt = new JSONObject(sgfGameRslt[8]);
                                //ruleset = joScoreRslt.getString("ruleset");
                                //score = joScoreRslt.getString("score");
                                try {
                                    dame = joScoreRslt.getString("dame");
                                } catch (JSONException e) { dame = ""; }
                                try {
                                    neutral = joScoreRslt.getString("neutral");
                                } catch (JSONException e) { neutral = ""; }
                                try {
                                    white_stones = joScoreRslt.getString("white_stones");
                                } catch (JSONException e) { white_stones = ""; }
                                try {
                                    black_stones = joScoreRslt.getString("black_stones");
                                } catch (JSONException e) { black_stones = ""; }
                                try {
                                    white_dead = joScoreRslt.getString("white_dead");
                                } catch (JSONException e) { white_dead = ""; }
                                try {
                                    black_dead = joScoreRslt.getString("black_dead");
                                } catch (JSONException e) { black_dead = ""; }
                                try {
                                    white_territory = joScoreRslt.getString("white_territory");
                                } catch (JSONException e) { white_territory = ""; }
                                try {
                                    black_territory = joScoreRslt.getString("black_territory");
                                } catch (JSONException e) { black_territory = ""; }
                            } catch (JSONException ignored) {
                            }
                            score_data[PlayDGS.DAME_POINTS] = dame;
                            score_data[PlayDGS.NEUTRAL_POINTS] = neutral;
                            score_data[PlayDGS.WHITE_STONES] = white_stones;
                            score_data[PlayDGS.BLACK_STONES] = black_stones;
                            score_data[PlayDGS.WHITE_DEAD] = white_dead;
                            score_data[PlayDGS.BLACK_DEAD] = black_dead;
                            score_data[PlayDGS.WHITE_TERRITORY] = white_territory;
                            score_data[PlayDGS.BLACK_TERRITORY] = black_territory;

                        }
                    } else {
                        if (sgfGameRslt[7].contentEquals("true")) {
                            gameNotes = true;
                        }
                        JSONObject joGetNote;
                        try {
                            joGetNote = new JSONObject(sgfGameRslt[8]);
                            gameNoteTxt = joGetNote.getString("notes");
                        } catch (JSONException ignored) { }
                        if (gameNoteTxt == null) gameNoteTxt = "";
                    }

                    if (!s.contentEquals("0") && s.contentEquals(gameId)) {
                        if (connState == DOWNLOAD_SGF) {
                            final Intent sgfGameIntent = new Intent(MainDGS.this, GameBoard.class);
                            sgfGameIntent.putExtra("SGF", sgf); // key/value pair,
                            sgfGameIntent.putExtra("FILE", "");
                            sgfGameIntent.putExtra("GAMEACTION",BoardManager.GA_PLAY);
                            sgfGameIntent.putExtra("GAMESTATUS",BoardManager.GS_PLAY);
                            sgfGameIntent.putExtra("HANDICAP","0");
                            sgfGameIntent.putExtra("TIMELEFT", timeLeft);
                            sgfGameIntent.putExtra("MODE", defaultEditMode);
                            sgfGameIntent.putExtra("AUTOPLAYPAUSE", autoPlayPause);
                            sgfGameIntent.putExtra("AUTOPLAYINTERVAL", autoPlayInterval);
                            sgfGameIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
                            startActivityForResult(sgfGameIntent, DOWN_LOAD_VIEW);
                        } else {
                            if (actionStoredMoves) {
                                String [] nextMov = storMov.findTakeStoredMove(gameId, moveId, sgf);
                                if (nextMov != null) {
                                    sendDGSmove(gameId, nextMov[0], nextMov[1], nextMov[2], "", false, "");
                                    return;
                                }
                            } else {  // this came from Status thus clear stored move history
                                storMov.clearStoredMove(gameId);
                            }
                            connState = DISPLAY_BOARD;
                            final Intent playIntent = new Intent(MainDGS.this, PlayDGS.class);
                            playIntent.putExtra("SGF", sgf); // key/value pair,
                            playIntent.putExtra("GID", gameId);
                            playIntent.putExtra("MOVEID", moveId);
                            playIntent.putExtra("GAMEACTION",game_action);
                            playIntent.putExtra("GAMESTATUS",game_status);
                            playIntent.putExtra("HANDICAP",handicap);
                            playIntent.putExtra("TIMELEFT", timeLeft);
                            playIntent.putExtra("MODE", defaultEditMode);
                            playIntent.putExtra("AUTOPLAYPAUSE", autoPlayPause);
                            playIntent.putExtra("AUTOPLAYINTERVAL", autoPlayInterval);
                            playIntent.putExtra("GAMENOTES", gameNotes);
                            playIntent.putExtra("NOTE", gameNoteTxt);
                            playIntent.putExtra("SCOREDATA", score_data);
                            playIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
                            startActivityForResult(playIntent, PLAY_VIEW);
                        }
                    } else {
                        doNextAction();
                    }
                    break;

                case MOVESENT:
                case PASSSENT:
                case RESIGNSENT:
                case NOTESENT:
                    if (connState != SEND_MOVE) break;
                    s = (String) msg.obj;
                    JSONObject joMovRslt;
                    try {
                        joMovRslt = new JSONObject(s);
                        errString = joMovRslt.getString("error");
                    } catch (JSONException e) {
                        errString = "";
                    }
                    if (!errString.contentEquals("")) {
                        connStatus.setText(errString);
                        restoreStatus(false, true, false, BUTTONS_CLIENT);
                        break;
                    }
                    doNextAction();
                    break;

                case MSG:
                    if (connState != GET_MSG) break;
                    msgRslt = (String []) msg.obj;  // msg, msgId
                    if (msgRslt == null) {
                        doNextAction();
                        break;
                    }
                    if (autoClient) {
                        String midSrch = ">" + msgRslt[1] + "<";
                        if (msgsSeen.toString().contains(midSrch)) { // skip if already seen
                            doNextAction();
                            break;
                        }
                        msgsSeen.append(midSrch);
                    }
                    String [] buttonTexts;
                    if (msgType.contentEquals("NORMAL")) {
                        connState = DISPLAY_NORMAL_MSG;
                        buttonTexts = new String [4];
                        buttonTexts[0] = getString(R.string.doneButton);
                        buttonTexts[1] = getString(R.string.reply);
                        buttonTexts[2] = getString(R.string.Browser);
                        buttonTexts[3] = getString(R.string.skipAllButton);
                    } else if (msgType.contentEquals("RESULT")) {
                        connState = DISPLAY_RESULT_MSG;
                        buttonTexts = new String [3];
                        buttonTexts[0] = getString(R.string.doneButton);
                        buttonTexts[1] = getString(R.string.Browser);
                        buttonTexts[2] = getString(R.string.skipAllButton);
                    } else if (msgType.contentEquals("INVITATION")) {
                        connState = DISPLAY_INVITATION_MSG;
                        buttonTexts = new String [5];
                        buttonTexts[0] = getString(R.string.skipButton);
                        buttonTexts[1] = getString(R.string.accept);
                        buttonTexts[2] = getString(R.string.decline);
                        buttonTexts[3] = getString(R.string.Browser);
                        buttonTexts[4] = getString(R.string.skipAllButton);
                    } else if (msgType.contentEquals("DISPUTED")) {
                        connState = DISPLAY_DISPUTED_MSG;
                        buttonTexts = new String [5];
                        buttonTexts[0] = getString(R.string.skipButton);
                        buttonTexts[1] = getString(R.string.accept);
                        buttonTexts[2] = getString(R.string.decline);
                        buttonTexts[3] = getString(R.string.Browser);
                        buttonTexts[4] = getString(R.string.skipAllButton);
                    } else {
                        connState = DISPLAY_NORMAL_MSG;
                        buttonTexts = new String [1];
                        buttonTexts[0] = getString(R.string.doneButton);
                    }
                    displayMsg(msgRslt[1],msgRslt[0],buttonTexts,msgType);
                    break;

                case BULLETIN:
                    if (connState != GET_BULLETIN) break;
                    msgRslt = (String []) msg.obj;  // msg, msgId
                    if (msgRslt == null) {
                        doNextAction();
                        break;
                    }
                    displayBulletin(msgRslt[1],msgRslt[0]);
                    break;

                case MARKEDBULLETIN:
                    if (connState != DISPLAY_BULLETIN) break;
                    doNextAction();
                    break;

                case MESSAGESENT:
                    if (connState != SEND_MSG) break;
                    doNextAction();
                    break;

                case MOVEDMSG:
                    // do nothing for now
                    break;

                case INFOMYUSER:
                    if (connState != GET_INFO) break;
                    s = (String) msg.obj;
                    if (s == null) s = "";
                    DisplayUserInfo(s);
                    break;

                case INFOUSER:
                    if (connState != GET_INFO) break;
                    s = (String) msg.obj;
                    if (s == null) s = "";
                    DisplayUserInfo(s);
                    break;

                case THREADSTATUS:
                    s = (String) msg.obj;
                    if (s == null) s = "0";
                    int stat = Integer.parseInt(s);
                    s = "";
                    switch (stat) {
                        case TS_OK:
                            s = getString(R.string.Ok);
                            break;
                        case TS_LOGGING_ON:
                            s = getString(R.string.LoggingOn);
                            break;
                        case TS_SENDING:
                            s = getString(R.string.sending);
                            break;
                        case TS_SENDING_MOVE:
                            s = getString(R.string.sending) + moveString;
                            break;
                        case TS_SENDING_NOTES:
                            s = getString(R.string.sending) + " " + getString(R.string.GameNotes);
                            break;
                        case TS_SENDING_MESSAGE:
                            s = getString(R.string.sending);
                            break;
                        case TS_SENDING_SCORE:
                            s = getString(R.string.sending) + " " + getString(R.string.Score);
                            break;
                        case TS_GETTING_STATUS_LIST:
                            s = getString(R.string.GettingStatus);
                            break;
                        case TS_GETTING_MESSAGE:
                            s = getString(R.string.GettingMsg) + msgString;
                            break;
                        case TS_GETTING_GAME:
                            s = getString(R.string.GettingGame);
                            break;
                        case TS_GETTING_NOTES:
                            s = getString(R.string.GettingGame) + " " + getString(R.string.GameNotes);
                            break;
                        case TS_GETTING_GAME_LIST:
                            s = getString(R.string.GettingList);
                            break;
                        case TS_GETTING_SCORE:
                            s = getString(R.string.GettingGame) + " " + getString(R.string.Score);
                            break;
                        case TS_GETTING_WROOM_LIST:
                            s = getString(R.string.GettingList);
                            break;
                        case TS_GETTING_WROOM_INFO:
                            s = getString(R.string.GettingGame);
                            break;
                        case TS_JOINING_WROOM_GAME:
                            s = getString(R.string.JoiningGame);
                            break;
                        default:
                            s = getString(R.string.HelpUnknown);
                            break;
                    }
                    connStatus.setText(s);
                    break;


				case TIMEOUT:
					// TODO
				case STOPPED:
					/* not used */
				default:
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Bundle extras;
        String rslt = "";
        int inx = -1;
        switch (requestCode) {
            case GET_PREFS:
                boolean notifierChanged = false;
                SharedPreferences prefs = getSharedPreferences("MainDGS", 0); //getPreferences(0);
                String du = prefs.getString("com.hg.anDGS.DGSUser", "");  // user and password need defined to start client
                String dp = prefs.getString("com.hg.anDGS.DGSPass", "");
                String bl = prefs.getString("com.hg.anDGS.BoardLayout", PrefsDGS.PORTRAIT);
                String th = prefs.getString("com.hg.anDGS.Theme", PrefsDGS.DEFAULT_THEME);
                String lcl = prefs.getString("com.hg.anDGS.Locale", "");
                int dbgNew = prefs.getInt("com.hg.anDGS.Debug", 0);
                try {
                    extras = data.getExtras();
					assert extras != null;
					notifierChanged = extras.getBoolean("NOTIFIERCHANGED");
                    if (notifierChanged) {
                        if (commonStuff.isNotifierRunning(am)) {
                            doToggleNotifier(); // will stop the notifier
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ignore) {
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
                boolean restartit = !boardLayout.contentEquals(bl)
                        || !theme.contentEquals(th)
                        || !myLocale.contentEquals(lcl)
						|| !DGSUser.contentEquals(du)
						|| !DGSPass.contentEquals(dp)
                        || notifierChanged
						|| dbgNew != dbg;
                boardLayout = bl;
                if (restartit) {
                    finish();
                    Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
					assert i != null;
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
                break;
            case FWD_PREFS:
                final Intent prefsIntent = new Intent(MainDGS.this, PrefsDGS.class);
                prefsIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
                startActivityForResult(prefsIntent, GET_PREFS);
                break;
            case SAVED_GAMES:
            case NEW_GAME:
            case RECOV_GAME:
            case GRINDER:
                adjustNotifierText();
                boolean newRecoveryFile = commonFileStuff.isRecoveryFile();
                if (!(newRecoveryFile == recoveryFile)) {
                    if (newRecoveryFile) {
                        recoveryFile = true;
                        recov_text = getString(R.string.recover_game);
                    } else {
                        recoveryFile = false;
                        recov_text = getString(R.string.Nothing);
                    }
                }
                restoreStatus(false, true, false, BUTTONS_EDIT);
                break;
            case GAME_BOARD:
                adjustNotifierText();
                newRecoveryFile = commonFileStuff.isRecoveryFile();
                if (!(newRecoveryFile == recoveryFile)) {
                    if (newRecoveryFile) {
                        recoveryFile = true;
                        recov_text = getString(R.string.recover_game);
                    } else {
                        recoveryFile = false;
                        recov_text = getString(R.string.Nothing);
                    }
                }
                restoreStatus(false, true, false, BUTTONS_EDIT);
                break;
            case TUTORIAL_VIEW:
                if (resultCode == RESULT_OK) {
                    try {
                        extras = data.getExtras();
						assert extras != null;
						rslt = extras.getString("RESULT");
                    } catch (Exception ignored) {
                    }
                    if (rslt == null) rslt = "0";
                }
                if (Integer.parseInt(rslt) == 1) {
                    Locale slcl = Locale.getDefault();
                    String lang = slcl.getLanguage();
                    String url;
					if(lang.startsWith("en")) {
						url = "http://playgo.to/iwtg/en/";
					} else {
						url = "http://playgo.to/iwtg/";
                    }
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                break;
            case HELP_VIEW:
            default:
                adjustNotifierText();
                newRecoveryFile = commonFileStuff.isRecoveryFile();
                if (!(newRecoveryFile == recoveryFile)) {
                    if (newRecoveryFile) {
                        recoveryFile = true;
                        recov_text = getString(R.string.recover_game);
                    } else {
                        recoveryFile = false;
                        recov_text = getString(R.string.Nothing);
                    }
                }
                boolean oktype = buttonsType==BUTTONS_CLIENT || buttonsType==BUTTONS_USERACTIONS || buttonsType==BUTTONS_GAMELISTS;
                restoreStatus(oktype, true, false, buttonsType);  // TODO  ??
                break;
            // client returns
            case PLAY_VIEW:
                String colr = "", mov = "", msg = "", gid = "", movid = "";
                gameNotes = false;
                gameNoteTxt = "";
                if (resultCode == RESULT_OK) {
                    try {
                        extras = data.getExtras();
						if (extras == null) {
                            errHist.writeErrorHistory("MainDGS.onActivityResult, PLAY_VIEW, RESULT_OK, extras == null");
                        } else {
                            rslt = extras.getString("RESULT");
                            colr = extras.getString("COLOR");
                            mov = extras.getString("MOV");
                            msg = extras.getString("MSG");
                            gameNoteTxt = extras.getString("NOTE");
                            gameNotes = extras.getBoolean("GAMENOTES");
                            gid = extras.getString("GID");
                            movid = extras.getString("MOVEID");
                        }
                    } catch (Exception ignored) {
                    }
                    if (rslt == null) rslt = "";
                    if (colr == null) colr = "";
                    if (mov == null) mov = "";
                    if (msg == null) msg = "";
                    if (gameNoteTxt == null) gameNoteTxt = "";
                    if (gid == null) gid = "";
                    if (movid == null) movid = "";
                }
                boolean skipit = !gameId.contentEquals(gid);  // client restarted sgf and status list lost
                gameId = gid;
                if (rslt.contentEquals("HANDICAP")) {
                    sendDGShandicap(gid, mov, msg, gameNotes, gameNoteTxt);
                } else if (rslt.contentEquals("RESIGNED")) {
                    sendDGSresign(gid, movid, colr, msg, gameNotes, gameNoteTxt);
                } else if (rslt.contentEquals("MOVED")) {
                    sendDGSmove(gid, movid, colr, mov, msg, gameNotes, gameNoteTxt);
                } else if (rslt.contentEquals("SINGLE")) {
                    sendDGSmove(gid, "0", colr, mov, msg, gameNotes, gameNoteTxt);
                } else if (rslt.contentEquals("SCORE")) {
                    sendDGSscore(gid, movid, mov, msg);
                } else if (rslt.contentEquals("ACCEPTSCORE")) {
                    acceptDGSscore(gid, movid, msg);
                } else if (rslt.contentEquals("DELETE")) {
                    deleteDGSgame(gid, movid, msg);
                } else if (rslt.contentEquals("DOWNLOAD") && !skipit) {
                    final Intent sgfGameIntent = new Intent(this, GameBoard.class);
                    sgfGameIntent.putExtra("SGF", sgf); // key/value pair,
                    sgfGameIntent.putExtra("FILE", "");
                    sgfGameIntent.putExtra("GAMEACTION", BoardManager.GA_PLAY);
                    sgfGameIntent.putExtra("GAMESTATUS", "UNKNOWN");
                    sgfGameIntent.putExtra("HANDICAP", "0");
                    sgfGameIntent.putExtra("TIMELEFT", timeLeft);
                    sgfGameIntent.putExtra("MODE", defaultEditMode);
                    sgfGameIntent.putExtra("AUTOPLAYPAUSE", autoPlayPause);
                    sgfGameIntent.putExtra("AUTOPLAYINTERVAL", autoPlayInterval);
                    sgfGameIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
                    startActivityForResult(sgfGameIntent, DOWN_LOAD_VIEW);
                } else if (rslt.contentEquals("SKIPALL")) {
                    autoClient = false;
                    if (gameNotes && !gameNoteTxt.contentEquals("")) {
                        sendDGSgameNotes(gid, gameNoteTxt);
                    } else {
                        restoreStatus(true, true, false, BUTTONS_CLIENT);
                    }
                } else if (rslt.contentEquals("SKIP") && !skipit) {
                    if (gameNotes && !gameNoteTxt.contentEquals("")) {
                        sendDGSgameNotes(gid, gameNoteTxt);
                    } else {
                        gotoNext();
                    }
                } else {
                    getStatus();
                }
                break;
            case DISPLAY_MSG_VIEW:
                if (resultCode == RESULT_OK) {
                    try {
                        extras = data.getExtras();
						assert extras != null;
						rslt = extras.getString("RESULT");
                        msgId = extras.getString("MsgId");
                    } catch (Exception ignored) {
                    }
                    if (rslt == null) rslt = "0";
                    if (msgId == null) msgId = "0";
                }
                int bttn = Integer.parseInt(rslt);
                switch (connState) {
                    case DISPLAY_NORMAL_MSG:
                        switch (bttn) {
                            default:
                            case 0:  // done
                                doNextAction();
                                break;
                            case 1:  // reply
                                if (oldSubj.startsWith("'") && oldSubj.endsWith("'"))
                                    oldSubj = oldSubj.substring(1, oldSubj.length() - 1);
                                if (!oldSubj.startsWith("RE:"))
                                    oldSubj = "RE:" + oldSubj;
                                MessageUser("", "", msgId, oldSubj);
                                break;
                            case 2: // browser
                                sendToDGS("message.php?mode=ShowMessage&mid=" + msgId);
                                break;
                            case 3: // skip all
                                restoreStatus(true, true, false, BUTTONS_USERACTIONS);
                                break;
                        }
                        break;
                    case DISPLAY_RESULT_MSG:
                        switch (bttn) {
                            default:
                            case 0:  // done
                                doNextAction();
                                break;
                            case 1: // browser
                                sendToDGS("message.php?mode=ShowMessage&mid=" + msgId);
                                break;
                            case 2: // skip all
                                restoreStatus(true, true, false, BUTTONS_CLIENT);
                                break;
                        }
                        break;
                    case DISPLAY_INVITATION_MSG:
                        switch (bttn) {
                            default:
                            case 0:  // skip
                                doNextAction();
                                break;
                            case 1:  // accept
                                sendAcceptInvitation();
                                break;
                            case 2:  // reject
                                sendDeclineInvitation();
                                break;
                            case 3: // browser
                                sendToDGS("message.php?mode=ShowMessage&mid=" + msgId);
                                break;
                            case 4: // skip all
                                restoreStatus(true, true, false, BUTTONS_CLIENT);
                                break;
                        }
                        break;
                    case DISPLAY_DISPUTED_MSG:
                        switch (bttn) {
                            default:
                            case 0:  // skip
                                doNextAction();
                                break;
                            case 1:  // accept
                                sendAcceptInvitation();
                                break;
                            case 2:  // reject
                                sendDeclineInvitation();
                                break;
                            case 3: // browser
                                sendToDGS("message.php?mode=ShowMessage&mid=" + msgId);
                                break;
                            case 4: // skip all
                                restoreStatus(true, true, false, BUTTONS_CLIENT);
                                break;
                        }
                        break;
                    case DISPLAY_BULLETIN:
                        switch (bttn) {
                            default:
                            case 0:  // done
                                if (!msgId.contentEquals("0")) {
                                    mThread.markReadBulletin(msgId);
                                } else {
                                    doNextAction();
                                }
                                break;
                            case 1:  // skip
                                doNextAction();
                                break;
                            case 2: // skip all
                                restoreStatus(true, true, false, BUTTONS_CLIENT);
                                break;
                        }
                        break;
                    default:
                        break;
                }
                break;
            case GAMES_VIEW:
                if (resultCode == RESULT_OK) {
                    try {
                        extras = data.getExtras();
						assert extras != null;
						inx = extras.getInt("ITEM", -1);
                    } catch (Exception e) {
                        inx = -1;
                    }
                }
                if (inx < 0 || inx >= statusList.length) {
                    restoreStatus(true, true, false, BUTTONS_GAMELISTS);
                } else {
                    statusResult = statusList[inx];
                    // parse for gid,uid,name
                    gameId = "0";
                    lastUId = "";
                    lastUserId = "";
                    lastUserName = "";
                    String[] elements = statusResult.split(",");
                    if (connState == DISPLAY_WROOM_LIST) {
                        if (elements.length > 0) {
                            gameId = elements[0].trim();
                            connState = GET_WROOM_INFO;
                            mThread.getWroomInfo(gameId);
                        }
                        break;
                    }
                    if (elements.length > 2) {
                        gameId = elements[0].trim();
                        lastUId = elements[1].trim();
                        lastUserName = elements[2].trim();
                    }
                    gameIndex = -1;
                    msgIndex = -1;
                    bulletinIndex = -1;
                    mpgIndex = -1;
                    connState = LOGGED_ON;
                    connStatus.setText("OK");
                    autoClient = false;
                    // TODO resign from the running game list
            /*
            if (running game) {
                new AlertDialog.Builder(this, commonStuff.getWoStyle(theme))
                .setTitle(R.string.downLoadGame)
                .setMessage(gameId + " " + lastUserName)
                .setNegativeButton(R.string.Info, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        connState = GET_INFO;
                        mThread.getInfoUid(lastUId);
                        }})
                .setNeutralButton(R.string.Resign, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        sendDGSresign(gid, movid, colr, msg, false, "");
                        }})
                .setPositiveButton(R.string.downLoadGame, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        connState = DOWNLOAD_SGF;
                        mThread.getGame(gameId,"0","0","0","0","0",false);
                    }
                    }).show();
            } else {
            */
                    new AlertDialog.Builder(ctw)
                            .setTitle(R.string.downLoadGame)
                            .setMessage(gameId + " " + lastUserName)
                            .setNegativeButton(R.string.Info, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    connState = GET_INFO;
                                    mThread.getInfoUid(lastUId);
                                }
                            })
                            .setPositiveButton(R.string.downLoadGame, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    connState = DOWNLOAD_SGF;
                                    mThread.getGame(gameId, "0", "0", "0", "0", "0", false);
                                }
                            }).show();
                    //	}
                }
                break;
            case STATUS_VIEW:
                if (resultCode == RESULT_OK) {
                    try {
                        extras = data.getExtras();
						assert extras != null;
						inx = extras.getInt("ITEM", -1);
                    } catch (Exception e) {
                        inx = -1;
                    }
                }
                if (inx < 0 || inx >= statusList.length) {
                    restoreStatus(true, true, false, BUTTONS_CLIENT);
                } else {
                    gotoStatusLine(inx);
                }
                break;
            case DOWN_LOAD_VIEW:
                if (connState != DOWNLOAD_SGF) {  //downloaded from play view
                    if (gameId.contentEquals("0")) { // client restarted no sgf
                        restoreStatus(true, true, false, BUTTONS_CLIENT);
                        break;
                    }
                    connState = DISPLAY_BOARD;
                    try {
                        final Intent playIntent = new Intent(this, PlayDGS.class);
                        playIntent.putExtra("SGF", sgf); // key/value pair,
                        playIntent.putExtra("GID", gameId);
                        playIntent.putExtra("MOVEID", moveId);
                        playIntent.putExtra("GAMEACTION", game_action);
                        playIntent.putExtra("GAMESTATUS", game_status);
                        playIntent.putExtra("HANDICAP", handicap);
                        playIntent.putExtra("TIMELEFT", timeLeft);
                        playIntent.putExtra("MODE", defaultEditMode);
                        playIntent.putExtra("AUTOPLAYPAUSE", autoPlayPause);
                        playIntent.putExtra("AUTOPLAYINTERVAL", autoPlayInterval);
                        playIntent.putExtra("GAMENOTES", gameNotes);
                        playIntent.putExtra("NOTE", gameNoteTxt);
                        playIntent.putExtra("SCOREDATA", score_data);
                        playIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
                        startActivityForResult(playIntent, PLAY_VIEW);
                    } catch (Exception e) {
                        doNextAction();
                    }
                } else {
                    restoreStatus(true, true, false, BUTTONS_CLIENT);
                }
                break;
            case INVITE_VIEW:
                // TODO send invitation
                if (resultCode == RESULT_OK) {
                    String inviteString;
                    try {
                        extras = data.getExtras();
						assert extras != null;
						inviteString = extras.getString("InviteString");
                    } catch (Exception e) {
                        inviteString = "";
                    }
					assert inviteString != null;
					if (!inviteString.equals("")) {
                        connState = SEND_MSG;
                        mThread.sendInvitation(inviteString);  // TODO is this right
                        break;
                    }
                }
                restoreStatus(true, true, false, BUTTONS_USERACTIONS);
                break;
            case MESSAGE_USER_VIEW:
                if (resultCode == RESULT_OK) {
                    String msgString;
                    try {
                        extras = data.getExtras();
						assert extras != null;
						msgString = extras.getString("MessageString");
                    } catch (Exception e) {
                        msgString = "";
                    }
					assert msgString != null;
					if (!msgString.equals("")) {
                        connState = SEND_MSG;
                        mThread.sendMSG(msgString);
                        break;
                    }
                }
                restoreStatus(true, true, false, BUTTONS_USERACTIONS);
                break;
            case DISPLAY_INFO_VIEW:
                if (connState != GET_INFO) break;
                if (resultCode == RESULT_OK) {
                    try {
                        extras = data.getExtras();
						assert extras != null;
						rslt = extras.getString("RESULT");
                    } catch (Exception ignored) {
                    }
                    if (rslt == null) rslt = "";
                }
                if (rslt.contentEquals("3")) {  // show graph
                    GetUserGraph(lastUId, lastUserId, lastRegisterDate);
                } else if (rslt.contentEquals("2")) {  // invite
                    InviteUser();
                } else if (rslt.contentEquals("1")) { // Message User button
                    MessageUser(lastUId, lastUserId, "", "");
                } else {  // Done button
                    restoreStatus(true, true, false, BUTTONS_USERACTIONS);
                }
                break;
            case STORED_MOVES_VIEW:
                if (resultCode == RESULT_OK) {
                    try {
                        extras = data.getExtras();
                        inx = extras.getInt("ITEM", -1);
                    } catch (Exception e) {
                        inx = -1;
                    }
                    if (inx >= 0 && inx < statusList.length) {
                        String[] e = statusList[inx].split("!");
                        if (e.length > 1) {
                            gameId = e[0];
                            new AlertDialog.Builder(ctw)
                                    .setTitle(R.string.PredictedMoves)
                                    .setMessage(getString(R.string.Delete) + " " + gameId)
                                    .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            restoreStatus(false, true, false, BUTTONS_UTILITIES);
                                        }
                                    })
                                    .setPositiveButton(getString(R.string.Delete), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            storMov.clearStoredMove(gameId);
                                            restoreStatus(false, true, false, BUTTONS_UTILITIES);
                                        }
                                    }).show();
                            break;
                        }
                    }
                }
                restoreStatus(false, true, false, BUTTONS_UTILITIES);
                break;
            case ERROR_HISTORY_VIEW:
                if (resultCode == RESULT_OK) {
                    try {
                        extras = data.getExtras();
                        inx = extras.getInt("ITEM", -1);
                    } catch (Exception e) {
                        inx = -1;
                    }
                    if (inx >= 0 && inx < errHist.errorHistoryDataLength()) {
                        String e = errHist.getErrorHistoryDataInx(inx);
                        if (e.length() > 1) {
							String [] buttonTexts = new String [3];
							buttonTexts[0] = getString(R.string.Cancel);
							buttonTexts[1] = getString(R.string.deleteAll);
							buttonTexts[2] = getString(R.string.Delete);
							final Intent msgIntent = new Intent(this, MsgView.class);
							msgIntent.putExtra("TITLE", getString(R.string.ErrorHistory));
							msgIntent.putExtra("ButtonTexts", buttonTexts);
							msgIntent.putExtra("MsgId", ""+inx);
							msgIntent.putExtra("MsgText", getString(R.string.Delete) + e);
							msgIntent.putExtra("MsgHelpType", commonStuff.HELP_ERRORHISTORY);
							msgIntent.putExtra("BOARDLAYOUT", currentBoardLayout);
							startActivityForResult(msgIntent, ERROR_HISTORY_DELETE);
                            break;
                        }
                    }
                }
                restoreStatus(false, true, false, BUTTONS_UTILITIES);
                break;
			case ERROR_HISTORY_DELETE:
				if (resultCode == RESULT_OK) {
					try {
						extras = data.getExtras();
						rslt = extras.getString("RESULT");
						msgId = extras.getString("MsgId");
					} catch (Exception ignored) {
					}
					if (rslt == null) rslt = "0";
					if (msgId == null) msgId = "0";
				} else {
					rslt = "0";
					msgId = "0";
				}
				if (rslt.contains("2")) {  // delete
					int i = Integer.parseInt(msgId);
					errHist.clearErrorHistoryDataInx(i);
					restoreStatus(false, true, false, BUTTONS_UTILITIES);
				} else if (rslt.contains("1")) { // delete all
					errHist.clearErrorHistoryDataAll();
					restoreStatus(false, true, false, BUTTONS_UTILITIES);
				} else {  // cancel
					restoreStatus(false, true, false, BUTTONS_UTILITIES);
				}
				break;
            case GET_USER_GRAPH_VIEW:
                if (resultCode == RESULT_OK) {
                    String msgString;
                    try {
                        extras = data.getExtras();
                        msgString = extras.getString("MessageString");
                    } catch (Exception e) {
                        msgString = "";
                    }
                    if (!msgString.equals("")) {
                        sendToDGS(msgString);
                        break;
                    }
                }
                restoreStatus(true, true, false, BUTTONS_USERACTIONS);
                break;
            case FIND_USER_VIEW:
                if (resultCode == RESULT_OK) {
                    String msgString;
                    try {
                        extras = data.getExtras();
                        msgString = extras.getString("FindString");
                    } catch (Exception e) {
                        msgString = "";
                    }
                    if (!msgString.equals("")) {
                        sendToDGS(msgString);
                        break;
                    }
                }
                restoreStatus(true, true, false, BUTTONS_USERACTIONS);
                break;
            case PHRASE_VIEW:
                String s;
                if (resultCode == RESULT_OK) {
                    try {
                        extras = data.getExtras();
                        s = extras.getString("PHRASE");
                        if (!s.contentEquals("")) {
                            EditPhrasesClick();
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                }
                restoreStatus(false, true, false, BUTTONS_UTILITIES);
                break;
			case UTILITY_PHRASE_VIEW:
				if (resultCode == RESULT_OK) {
					try {
						extras = data.getExtras();
						s = extras.getString("PHRASE");
						utilityCommentEditText.setText(s);
					} catch (Exception ignored) {
					}
				}
				break;
        }
    }

}