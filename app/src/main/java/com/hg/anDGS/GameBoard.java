package com.hg.anDGS;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.sf.gogui.go.GoColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class GameBoard extends DGSActivity implements BoardUpdate {
	protected static final int HELP_VIEW = 1;
	protected static final int COMMENT_VIEW = 2;
	protected static final int GET_OPTS = 3;
	protected static final int MARKUP_OPTS = 4;
	protected static final int GAMEINFO_VIEW = 5;
	
	private static final int MENU_HELP = 1;
	private static final int MENU_SAVE = 2;
	private static final int MENU_END_GAME = 3;
	private static final int MENU_OPTIONS = 4;
	private static final int MENU_GAMEINFO = 5;
	
	private BoardManager bm = null;

	private TableLayout topMenuLayout;
	private TextView tmEndGame;
	private TextView tmInfo;
	private TextView tmSave;
	private TextView tmMode;
	private String[] tmModeItems;
	private int tmModeSelected = 1;
	private TextView tmHelp;
	
	private TextView recInfo;
	private TextView moveInfo;
	private FrameLayout boardSwitcher;
	private ImageView rec_right_button;
	private ImageView rec_left_button;
	private ImageView rec_far_right_button;
	private ImageView rec_far_left_button;
	private ImageView rec_mid_left_button;
	private ImageView rec_mid_right_button;
	private TextView recComment;
	private int display_width = 240;
	private int display_length = 300;
	private float display_scale;
	private float scaleLines;

	private int playState = BoardManager.UNINITIALIZED;
	private int numMovesToSkip = GameBoardOptions.DEFAULTSKIPMOVES;

	private String mSGF = "";
	private String mFileName = "recovery";
	private File recovery;
	private String pFileName = null; 
	private String boardLayout = PrefsDGS.PORTRAIT;
	private String theme;
	private String move_control = PrefsDGS.ZOOM7X7;
	private boolean keepScreenOn = false;
	private boolean displayTopMenu = true;
	private String boardCoordTxt = PrefsDGS.NO_COORD;
	private String boardBGTxt = PrefsDGS.BG_WOOD;
	private String boardStoneTxt = PrefsDGS.STONE_CLAM;
	private int customBGvalue = MainDGS.BOARD_COLOR;
	private int numPrev = 0;
	private String defaultDir = null;
	private String defaultEditMode = GameBoardOptions.BROWSE;
	private String game_action = "0";
	private String game_status = "UNKNOWN";
	private String handicap = "0";
	private String timeLeft = "";
	private String game_mode = "";
	private boolean grinder = false;   // came from grinder go back when done
	private long autoPlayInterval = GameBoardOptions.DEFAUTOPLAYINTERVAL;
	private Boolean autoPlayPause = true;
	private Boolean autoPlaySound = false;
	private String version_text = "";
	private ContextThemeWrapper ctw;
	private CommonStuff commonStuff = new CommonStuff();
	private CommonFileStuff commonFileStuff = new CommonFileStuff();
	private ErrorHistory errHist = ErrorHistory.getInstance();
		
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences prefs = getSharedPreferences("MainDGS", 0);
		theme = prefs.getString("com.hg.anDGS.Theme", PrefsDGS.DEFAULT_THEME);
		move_control = prefs.getString("com.hg.anDGS.MoveControl", PrefsDGS.ZOOM7X7);
		boardCoordTxt = prefs.getString("com.hg.anDGS.BoardCoord", PrefsDGS.NO_COORD);
		boardBGTxt = prefs.getString("com.hg.anDGS.BoardBackground", PrefsDGS.BG_WOOD);
		customBGvalue = prefs.getInt("com.hg.anDGS.CustomBoardBackground", MainDGS.BOARD_COLOR);
		boardStoneTxt = prefs.getString("com.hg.anDGS.BoardStone", PrefsDGS.STONE_CLAM);
		numMovesToSkip = prefs.getInt("com.hg.anDGS.SkipMoves", GameBoardOptions.DEFAULTSKIPMOVES); 
		numPrev = prefs.getInt("com.hg.anDGS.EditNumPrev", 0);
		defaultDir = commonFileStuff.getSgfDirName();
		defaultEditMode = prefs.getString("com.hg.anDGS.DefaultEditMode", GameBoardOptions.EDIT);
		autoPlayPause = prefs.getBoolean("com.hg.anDGS.AutoPlayPause", true);
		autoPlaySound = prefs.getBoolean("com.hg.anDGS.AutoPlaySound", false);
		keepScreenOn = prefs.getBoolean("com.hg.anDGS.KeepScreenOn", false);
		displayTopMenu = prefs.getBoolean("com.hg.anDGS.EditDisplayTopMenu", true);
		autoPlayInterval = prefs.getLong("com.hg.anDGS.AutoPlayInterval", GameBoardOptions.DEFAUTOPLAYINTERVAL);
		scaleLines = prefs.getFloat("com.hg.anDGS.ScaleLines", 1);

		Bundle extras = getIntent().getExtras();
		String g_mode = null;
		if (extras != null) {
			mSGF = extras.getString("SGF");
			mFileName = extras.getString("FILE");
			game_action = extras.getString("GAMEACTION");
			game_status = extras.getString("GAMESTATUS");
			handicap = extras.getString("HANDICAP");
			timeLeft = extras.getString("TIMELEFT");
			g_mode = extras.getString("MODE");
			autoPlayInterval = extras.getLong("AUTOPLAYINTERVAL", autoPlayInterval);
			autoPlayPause = extras.getBoolean("AUTOPLAYPAUSE", autoPlayPause);
			autoPlaySound = extras.getBoolean("AUTOPLAYSOUND", autoPlaySound);
			boardLayout = extras.getString("BOARDLAYOUT");
		}
		
		if (boardLayout == null) {
			boardLayout = PrefsDGS.PORTRAIT;
		}
		if (game_action == null) {
			game_action = BoardManager.GA_PLAY;
		}
		if (game_status == null) {
			game_status = "UNKNOWN";
		}
		if (handicap == null) {
			handicap = "0";
		}
		if (timeLeft == null) {
			timeLeft = "";
		}
		if (move_control == null) {
			move_control = PrefsDGS.ZOOM7X7;
		}
				
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int flg = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		if (keepScreenOn) {
			flg |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		}
		getWindow().setFlags(flg, flg);
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		display_width = Math.min(metrics.heightPixels, metrics.widthPixels);
		Log.w("GameBoard", "display_width="+display_width);
		display_length = Math.max(metrics.heightPixels, metrics.widthPixels);
		display_scale = metrics.density;
		
		this.setTheme(commonStuff.getCommonStyle(theme));
		ctw = new ContextThemeWrapper(this, commonStuff.getCommonTheme(theme));
		
		if (boardLayout.contains(PrefsDGS.LANDSCAPE)) {
			//setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			if ((display_length - display_width) < 4*48) {
				setContentView(R.layout.gameboardlssm);
				rec_far_left_button = findViewById(R.id.recFarLeftButtonLSsm);
				rec_left_button = findViewById(R.id.recLeftButtonLSsm);
				rec_mid_left_button = findViewById(R.id.recMidLeftButtonLSsm);
				rec_mid_right_button = findViewById(R.id.recMidRightButtonLSsm);
				rec_right_button = findViewById(R.id.recRightButtonLSsm);
				rec_far_right_button = findViewById(R.id.recFarRightButtonLSsm);
				recComment = findViewById(R.id.recCommentLSsm);
				recInfo = findViewById(R.id.recInfoLSsm);
				moveInfo = findViewById(R.id.recMoveInfoLSsm);
				boardSwitcher = findViewById(R.id.recBoardGridLSsm);
				topMenuLayout = findViewById(R.id.recTopMenuLSsm);
				tmEndGame = findViewById(R.id.recTMPassLSsm);
				tmInfo = findViewById(R.id.recTMInfoLSsm);
				tmSave = findViewById(R.id.recTMSaveLSsm);
				tmMode = findViewById(R.id.recTMModeLSsm);
				tmHelp = findViewById(R.id.recTMHelpLSsm);
			} else if ((display_length - display_width) < 6*48) {
				setContentView(R.layout.gameboardls);
				rec_far_left_button = findViewById(R.id.recFarLeftButtonLS);
				rec_left_button = findViewById(R.id.recLeftButtonLS);
				rec_mid_left_button = findViewById(R.id.recMidLeftButtonLS);
				rec_mid_right_button = findViewById(R.id.recMidRightButtonLS);
				rec_right_button = findViewById(R.id.recRightButtonLS);
				rec_far_right_button = findViewById(R.id.recFarRightButtonLS);
				recComment = findViewById(R.id.recCommentLS);
				recInfo = findViewById(R.id.recInfoLS);
				moveInfo = findViewById(R.id.recMoveInfoLS);
				boardSwitcher = findViewById(R.id.recBoardGridLS);
				topMenuLayout = findViewById(R.id.recTopMenuLS);
				tmEndGame = findViewById(R.id.recTMPassLS);
				tmInfo = findViewById(R.id.recTMInfoLS);
				tmSave = findViewById(R.id.recTMSaveLS);
				tmMode = findViewById(R.id.recTMModeLS);
				tmHelp = findViewById(R.id.recTMHelpLS);
			} else {
				setContentView(R.layout.gameboardlstb);
				rec_far_left_button = findViewById(R.id.recFarLeftButtonLStb);
				rec_left_button = findViewById(R.id.recLeftButtonLStb);
				rec_mid_left_button = findViewById(R.id.recMidLeftButtonLStb);
				rec_mid_right_button = findViewById(R.id.recMidRightButtonLStb);
				rec_right_button = findViewById(R.id.recRightButtonLStb);
				rec_far_right_button = findViewById(R.id.recFarRightButtonLStb);
				recComment = findViewById(R.id.recCommentLStb);
				recInfo = findViewById(R.id.recInfoLStb);
				moveInfo = findViewById(R.id.recMoveInfoLStb);
				boardSwitcher = findViewById(R.id.recBoardGridLStb);
				topMenuLayout = findViewById(R.id.recTopMenuLStb);
				tmEndGame = findViewById(R.id.recTMPassLStb);
				tmInfo = findViewById(R.id.recTMInfoLStb);
				tmSave = findViewById(R.id.recTMSaveLStb);
				tmMode = findViewById(R.id.recTMModeLStb);
				tmHelp = findViewById(R.id.recTMHelpLStb);
			}
		} else {
			//setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setContentView(R.layout.gameboard);
			rec_far_left_button = findViewById(R.id.recFarLeftButton);
			rec_left_button = findViewById(R.id.recLeftButton);
			rec_mid_left_button = findViewById(R.id.recMidLeftButton);
			rec_mid_right_button = findViewById(R.id.recMidRightButton);
			rec_right_button = findViewById(R.id.recRightButton);
			rec_far_right_button = findViewById(R.id.recFarRightButton);
			recComment = findViewById(R.id.recComment);
			recInfo = findViewById(R.id.recInfo);
			moveInfo = findViewById(R.id.recMoveInfo);
			boardSwitcher = findViewById(R.id.recBoardGrid);
			topMenuLayout = findViewById(R.id.recTopMenu);
			tmEndGame = findViewById(R.id.recTMPass);
			tmInfo = findViewById(R.id.recTMInfo);
			tmSave = findViewById(R.id.recTMSave);
			tmMode = findViewById(R.id.recTMMode);
			tmHelp = findViewById(R.id.recTMHelp);
		}
		
		tmEndGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						doEndGame();
					}
				});
			}
		});
		
		tmSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						doSave();
					}
				});
			}
		});
		
		tmInfo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						doInfo();
					}
				});
			}
		});
		
		// tmMode 
		tmMode.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
				builder.setTitle(ctw.getString(R.string.select)); 
				ArrayAdapter<String> sel_adapter = new ArrayAdapter<String>(ctw,
					android.R.layout.simple_list_item_1, tmModeItems);
				sel_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
				builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item >= 0 && item <= 5 && item != tmModeSelected) {  // 6 cancel
							tmModeSelected = item;
							final Handler handler = new Handler();
							handler.post(new Runnable() { 
								public void run() {
									doModeSelected();	
								}
							});
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		
		tmModeItems = new String[] {getString(R.string.autoplay), getString(R.string.browse), getString(R.string.edit), getString(R.string.guessmove), getString(R.string.markup), getString(R.string.Options), getString(R.string.Cancel)};     
        doSetModeSelected();
        
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
		
		if (g_mode == null) {
			g_mode = defaultEditMode;
		} 
		game_mode = g_mode;
		/*
		if (game_mode.contentEquals(GameBoardOptions.BROWSE)
   			 || game_mode.contentEquals(GameBoardOptions.AUTOPLAY)) {
			tmEndGame.setText(R.string.blank5);
		} else {
			tmEndGame.setText(R.string.Pass);
		}
		*/
		if (game_mode.contentEquals(GameBoardOptions.AUTOPLAY)
				|| game_mode.contentEquals(GameBoardOptions.GUESSMOVE)) {
			grinder = true;
		} 
		
		if (!displayTopMenu) {
			topMenuLayout.removeAllViews();
		}
				
		rec_far_left_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						do_rec_far_left_button();
					}
				});
		}});
		
		rec_far_left_button.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						do_rec_far_left_button_long();
					}
				});
				return true;
		}});
/*		
		rec_left_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						do_rec_left_button();
					}
				});
		}});
		
		rec_left_button.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
            	do_long_rec_left_button();
            	return true;
             }
        });
*/		
		rec_left_button.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					final Handler handler = new Handler();
					handler.post(new Runnable() { 
						public void run() {	
							do_start_skip_backward();
						}
					});
					return true;

				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					final Handler handler2 = new Handler();
					handler2.post(new Runnable() { 
						public void run() {	
							do_stop_skip_backward();
						}
					});
					return false;

				}
				return false;
			}
		});
		
		//down
		rec_mid_left_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						do_rec_mid_left_button();
					}
				});
		}});
		
		//up
		rec_mid_right_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						do_rec_mid_right_button();
					}
				});
		}});
/*
		rec_right_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				do_rec_right_button();
		}});
		
		rec_right_button.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
            	do_long_rec_right_button();
            	return true;
             }
        });
*/		
		rec_right_button.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					final Handler handler = new Handler();
					handler.post(new Runnable() { 
						public void run() {	
							do_start_skip_forward();
						}
					});
					return true;

				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					final Handler handler2 = new Handler();
					handler2.post(new Runnable() { 
						public void run() {	
							do_stop_skip_forward();
						}
					});
					return false;

				}
				return false;
			}
		});
		
		rec_far_right_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						do_rec_far_right_button();
					}
				});
		}});
		
		rec_far_right_button.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						do_rec_far_right_button_long();
					}
				});
				return true;
		}});
		
		recComment.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						do_recComment();
					}
				});
		}});
		
		try {
			ComponentName comp = new ComponentName(this, this.getClass());
			PackageInfo pinfo;
			pinfo = this.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
			version_text = "v" + pinfo.versionCode;
		} catch (NameNotFoundException e) {
			version_text = "Unknown";
		}
		
		playState = BoardManager.FULL_BOARD;
		rec_left_button.setBackgroundColor(MainDGS.disabled_button_color);
		rec_right_button.setBackgroundColor(MainDGS.disabled_button_color);
		rec_far_left_button.setBackgroundColor(MainDGS.disabled_button_color);
		rec_far_right_button.setBackgroundColor(MainDGS.disabled_button_color);
		rec_mid_left_button.setBackgroundColor(MainDGS.disabled_button_color);
		rec_mid_right_button.setBackgroundColor(MainDGS.disabled_button_color);
		
		if (isRecoveryFile()) {
			if (mFileName.endsWith(commonFileStuff.RECOVERYFILE)) {
				recoverFile(mFileName);
			} else {
				DialogInterface.OnClickListener recovButtonListener = new DialogInterface.OnClickListener(){
	                public void onClick(DialogInterface arg0, int arg1) {
	                	recoverFile(commonFileStuff.getFullFileName(commonFileStuff.ANDGS_DIR, commonFileStuff.RECOVERYFILE));
	                }};
	           DialogInterface.OnClickListener newButtonListener = new DialogInterface.OnClickListener(){
	                public void onClick(DialogInterface arg0, int arg1) {
	            		final Handler handler = new Handler();
	            		handler.post(new Runnable() { 
	            			public void run() {
								recovery.delete();
								openTheGame();
	            			}
	            		});
	                }};
	                DialogInterface.OnClickListener cancelButtonListener = new DialogInterface.OnClickListener(){
		                public void onClick(DialogInterface arg0, int arg1) {
		        			Intent mIntent = new Intent();
		        			setResult(RESULT_OK, mIntent);
		        			finish();
		                }};
	           final TextView tv = new TextView(ctw);
	           tv.setText(pFileName);
	           new AlertDialog.Builder(ctw)
	           .setTitle(getString(R.string.UnsavedChanges))
	           .setMessage(getString(R.string.RecoverOrNew))
	           .setView(tv)
	           .setPositiveButton(getString(R.string.Recover), recovButtonListener)
	           .setNeutralButton(getString(R.string.New), newButtonListener)
	           .setNegativeButton(R.string.Cancel, cancelButtonListener)
	           .show();  
			}
		} else {
			final Handler handler = new Handler();
			handler.post(new Runnable() { 
				public void run() {	
					openTheGame();
				}
			});
		}
	}
	
	private void openTheGame() {
		bm = new BoardManager(
				ctw,
				this,
				boardSwitcher,
				boardBGTxt,
				customBGvalue,
				boardStoneTxt,
				mSGF, 
				game_action,
				game_status,
				handicap,
				timeLeft,
				mFileName, 
				defaultDir,
				move_control,
				boardCoordTxt,
				defaultEditMode, 
				game_mode, 
				version_text,
				numPrev,
				display_width,
				display_length,
				display_scale,
				scaleLines,
				autoPlayInterval,
				autoPlayPause, 
				autoPlaySound,
				false, // once
				null   // no score data
				);
	}
	
	@Override
	public void onPause(){
		if (bm != null) 
			try {
				bm.saveChanges();
			} catch (Exception ignore) {
			}
		super.onPause();
	}
	
	@Override
	public void onStop(){
		if (bm != null) 
			try {
				bm.saveChanges();
			}  catch (Exception ignore) {
			}
		super.onStop();
	}

	@Override
	public void onDestroy(){
		if (bm != null) 
			try {
				bm.saveChanges();
			} catch (Exception ignore) {
			}
		super.onDestroy();
	}

	@Override
	public void onUserLeaveHint(){
		if (bm != null) {
			try {
				bm.saveChanges();
			} catch (Exception ignore) {
			}
		}
		super.onUserLeaveHint();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if (bm != null) 
			try {
				bm.saveChanges();
			} catch (Exception ignore) {
			}
		super.onSaveInstanceState(savedInstanceState);
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (bm != null) 
			try {
				bm.saveChanges();
			} catch (Exception ignore) {
			}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public boolean onKeyUp (int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (playState == BoardManager.ZOOM_BOARD) { // Unzoom
				// unzoom board
				try {
					bm.unzoom();
				} catch (Exception e) {
					cleanUpAndReturn();
				}
				return true;
			} else {
				cleanUpAndReturn();
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
	
	private void cleanUpAndReturn() {
		if (bm != null) 
			try {
				bm.saveChanges();
			} catch (Exception ignore) {
			}
		Bundle rslts = new Bundle();
		rslts.putBoolean("GRINDING",false);
		Intent mIntent = new Intent();
		mIntent.putExtras(rslts);
		setResult(RESULT_OK, mIntent);
		finish();
	}
	
/*	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch  (keyCode) {
//		 case KeyEvent.KEYCODE_DPAD_CENTER: {
//			 return true;
//			}
		 case KeyEvent.KEYCODE_DPAD_LEFT: {
			 if (dpad_control) break;
			 do_rec_left_button();
			 return true;
		 	}
		 case KeyEvent.KEYCODE_DPAD_RIGHT: {
			 if (dpad_control) break;
			 do_rec_right_button();
			 return true;
		 	}
		 case KeyEvent.KEYCODE_DPAD_UP: {
			 if (dpad_control) break;
			 do_rec_mid_right_button();
			 return true;
		 	}
		 case KeyEvent.KEYCODE_DPAD_DOWN: {
			 if (dpad_control) break;
			 do_rec_mid_left_button();
 		 	 return true;
		 	}
		 case KeyEvent.KEYCODE_BACK: {
			 if (unSavedChanges) {
				 saveRecovery();
			 }
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
//		 case KeyEvent.KEYCODE_DPAD_CENTER: {
//			 do_recComment();
//			 return true;
//			}
		 case KeyEvent.KEYCODE_DPAD_LEFT: {
			 if (dpad_control) break;
			 return true;
		 	}
		 case KeyEvent.KEYCODE_DPAD_RIGHT: {
			 if (dpad_control) break;
			 return true;
		 	}
		 case KeyEvent.KEYCODE_DPAD_UP: {
			 if (dpad_control) break;
			 return true;
		 	}
		 case KeyEvent.KEYCODE_DPAD_DOWN: {
			if (dpad_control) break;
		 	return true;
		 	}
		 case KeyEvent.KEYCODE_BACK: {
			if (timer != null) timer.cancel();
			Bundle rslts = new Bundle();
			rslts.putBoolean("GRINDING",false);
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
*/
	private void do_rec_far_left_button() {
		if (playState == BoardManager.FULL_BOARD) {
			bm.go_back_n_moves(numMovesToSkip);
		}
	}
	
	private void do_rec_far_left_button_long() {
		if (playState == BoardManager.FULL_BOARD) {
			bm.go_first_node();
		}
	}
/*
	private void do_rec_left_button() {
		if (playState == BoardManager.DISPLAY_BOARD) {
			bm.go_prev_node();
		}
	}
	
	private void do_long_rec_left_button() {
		if (playState == BoardManager.DISPLAY_BOARD) {
			bm.go_prev_variation();
		}
	}
*/	
	private void do_start_skip_backward() {
		if (playState == BoardManager.FULL_BOARD) {
			bm.start_skip_prev_node();
		}
	}
	
	private void do_stop_skip_backward() {
		if (playState == BoardManager.FULL_BOARD) {
			bm.stop_skip_prev_node();
		}
	}

//down
	private void do_rec_mid_left_button() {
		if (playState == BoardManager.FULL_BOARD) {
			bm.go_next_neighbor();
		}
	}

//up
	private void do_rec_mid_right_button() {
		if (playState == BoardManager.FULL_BOARD) {
			bm.go_prev_neighbor();
		}
	}
/*
	private void do_rec_right_button() {
		if (playState == BoardManager.DISPLAY_BOARD) {
			bm.go_next_node();
		}
	}
	
	private void do_long_rec_right_button() {  // go to end of variation
		if (playState == BoardManager.DISPLAY_BOARD) {
			bm.go_next_variation();
		}
	}
*/	
	private void do_start_skip_forward() {
		if (playState == BoardManager.FULL_BOARD) {
			bm.start_skip_next_node();
		}
	}
	
	private void do_stop_skip_forward() {
		if (playState == BoardManager.FULL_BOARD) {
			bm.stop_skip_next_node();
		}
	}

	private void do_rec_far_right_button_long() {
		if (playState == BoardManager.FULL_BOARD) {
			bm.go_last_node();
		} else {
			// display the large board
			bm.unzoom();
		}
	}

	private void do_rec_far_right_button() {
		if (playState == BoardManager.FULL_BOARD) {
			bm.go_forward_n_moves(numMovesToSkip);
		} else {
			// display the large board
			bm.unzoom();
		}
	}
	
	private void do_recComment() {
		    final Intent commentIntent = new Intent(GameBoard.this,
					CommentView.class);
		    String s = recComment.getText().toString();
			commentIntent.putExtra("CommentText", s);
			commentIntent.putExtra("BOARDLAYOUT", boardLayout);
			startActivityForResult(commentIntent, COMMENT_VIEW);
	}

	private boolean isRecoveryFile() {
		File dFile = commonFileStuff.getFullDirFile(commonFileStuff.ANDGS_DIR);
		if (!dFile.isDirectory()) {
			return false;
		}
		recovery = commonFileStuff.getFullFile(commonFileStuff.ANDGS_DIR, commonFileStuff.RECOVERYFILE);
		return recovery.isFile();
	}
	
	private void recoverFile(final String fName) {
		final Handler handler = new Handler();
		handler.post(new Runnable() {
			public void run() {	
				StringBuilder sb = new StringBuilder(50);
				String s;
				int i;
				InputStream in;
				File f = new File(fName);
				try {
					in = new FileInputStream(f);
					if (fName.endsWith("recovery")) {
						while (true) {
							try {
								i = in.read();
							} catch (IOException e) {
								errHist.writeErrorHistory("GameB0ard IOException:" + e.toString());
								Toast.makeText(ctw, "Read "+fName+" failed "+e, Toast.LENGTH_LONG).show();
								try {
									in.close();
								} catch (IOException ignore) { }
								return;
							}
							if (i == '\n') break;
							sb.append((char) i);
						}
						
						s = sb.toString();
						if (s.contains(":")) {
							i = s.indexOf(":");
							mFileName = s.substring(0, i);
							s = s.substring(i);
							if (s.contains(GameBoardOptions.EDIT)) {
								game_mode = GameBoardOptions.EDIT;
							} else if (s.contains(GameBoardOptions.GUESSMOVE)) {
								game_mode = GameBoardOptions.GUESSMOVE;
							} else if (s.contains(GameBoardOptions.MARKUP)) {
								game_mode = GameBoardOptions.MARKUP;
							} else {
								game_mode = GameBoardOptions.BROWSE;
							}
							doSetModeSelected();
						} else {  // pre 1.17 recovery file
							mFileName = s;
						}
					}
					TextHelper th = new TextHelper();
					mSGF = th.GetText(in);
					openTheGame();
				} catch (FileNotFoundException e1) {
                    errHist.writeErrorHistory("GameB0ard FileNotFoundException:" + e1.toString());
					Toast.makeText(ctw, fName + getString(R.string.NotFound) + e1, Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	private void refreshButtons(int buttons) {
		//rec_left_button.setText(R.string.back);
		rec_left_button.setImageResource(R.drawable.ic_media_back);
		//rec_far_left_button.setText(R.string.first);
		rec_far_left_button.setImageResource(R.drawable.ic_media_rew);
		//rec_right_button.setText(R.string.forward);
		rec_right_button.setImageResource(R.drawable.ic_media_play);
		if ((buttons & BoardManager.PARENT_NODE) > 0) {
			rec_left_button.setBackgroundColor(MainDGS.enabled_button_color);
			rec_far_left_button.setBackgroundColor(MainDGS.enabled_button_color);
		} else {
			rec_left_button.setBackgroundColor(MainDGS.disabled_button_color);
			rec_far_left_button.setBackgroundColor(MainDGS.disabled_button_color);
		}
		if ((buttons & BoardManager.CHILD_NODE) > 0) {
			rec_right_button.setBackgroundColor(MainDGS.enabled_button_color);
		} else {
			rec_right_button.setBackgroundColor(MainDGS.disabled_button_color);
		}
		if (game_mode.contentEquals(GameBoardOptions.AUTOPLAY)) {
			if ((buttons & BoardManager.AUTOPLAY_RUNNING) > 0) { 
				//rec_far_right_button.setText(R.string.stop);
				rec_far_right_button.setImageResource(R.drawable.btn_radio_off);
			} else {
				//rec_far_right_button.setText(R.string.go);
				rec_far_right_button.setImageResource(R.drawable.btn_radio_on);
			}
			rec_far_right_button.setBackgroundColor(MainDGS.enabled_button_color);
		} else if (game_mode.contentEquals(GameBoardOptions.GUESSMOVE)) {
			if (grinder) {
				//rec_far_right_button.setText(R.string.retrn);
				rec_far_right_button.setImageResource(R.drawable.ic_menu_revert);
				rec_far_right_button.setBackgroundColor(MainDGS.enabled_button_color);
			} else if ((buttons & BoardManager.LAST_NODE) == 0) {
				//rec_far_right_button.setText(R.string.last);
				rec_far_right_button.setImageResource(R.drawable.ic_media_ff);
				rec_far_right_button.setBackgroundColor(MainDGS.enabled_button_color);
			} else {
				//rec_far_right_button.setText(R.string.last);
				rec_far_right_button.setImageResource(R.drawable.ic_media_ff);
				rec_far_right_button.setBackgroundColor(MainDGS.disabled_button_color);
			}
		} else {
			if ((buttons & BoardManager.LAST_NODE) == 0) {
				//rec_far_right_button.setText(R.string.last);
				rec_far_right_button.setImageResource(R.drawable.ic_media_ff);
				rec_far_right_button.setBackgroundColor(MainDGS.enabled_button_color);
			} else {
				//rec_far_right_button.setText(R.string.last);
				rec_far_right_button.setImageResource(R.drawable.ic_media_ff);
				rec_far_right_button.setBackgroundColor(MainDGS.disabled_button_color);
			}
		}
		//rec_mid_left_button.setText(R.string.down);
		rec_mid_left_button.setImageResource(R.drawable.ic_media_down);
		rec_mid_left_button.setBackgroundColor(MainDGS.disabled_button_color);
		//rec_mid_right_button.setText(R.string.up);
		rec_mid_right_button.setImageResource(R.drawable.ic_media_up);
		rec_mid_right_button.setBackgroundColor(MainDGS.disabled_button_color);
		if ((buttons & BoardManager.NEXT_NEIGHBOR) > 0) {
			rec_mid_left_button.setBackgroundColor(MainDGS.enabled_button_color);
		}
		if ((buttons & BoardManager.PREV_NEIGHBOR) > 0) {
			rec_mid_right_button.setBackgroundColor(MainDGS.enabled_button_color);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bundle extras;
		String s;
		switch (requestCode) {
		case COMMENT_VIEW:
			if (resultCode == RESULT_OK) {
				extras = data.getExtras();
				if (extras != null) {
					s = extras.getString("CommentText");
				} else {
					s = null;
				}
				if (s != null) {
					bm.set_comment(s);
			    	recComment.setText(s);
				}
			}
			break;
		case GET_OPTS:
			if (resultCode == RESULT_OK) {
				int editNumPrev;
				extras = data.getExtras();
				if (extras != null) {
					numMovesToSkip = extras.getInt("SKIPMOVES", GameBoardOptions.DEFAULTSKIPMOVES);
					editNumPrev = extras.getInt("NUMPREV", GameBoardOptions.DEFAULTMINNUMPREV);
					autoPlayInterval = extras.getLong("AUTOPLAYINTERVAL", GameBoardOptions.DEFAUTOPLAYINTERVAL);
					autoPlayPause = extras.getBoolean("AUTOPLAYPAUSE", true);
					autoPlaySound = extras.getBoolean("AUTOPLAYSOUND", false);
					s = extras.getString("MODE");
				} else {
					numMovesToSkip = GameBoardOptions.DEFAULTSKIPMOVES;
					editNumPrev = GameBoardOptions.DEFAULTMINNUMPREV;
					autoPlayInterval = GameBoardOptions.DEFAUTOPLAYINTERVAL;
					autoPlayPause = true;
					autoPlaySound = false;
					s = null;
				}
				if (editNumPrev != numPrev) {
					numPrev = editNumPrev;
					bm.updateNumPrev(numPrev); 
				}
				doMode(s);
				doSetModeSelected();
			}
			break;
		case MARKUP_OPTS:
			if (resultCode == RESULT_OK) {
				extras = data.getExtras();
				int markIndicator;
				int addIndicator;
				String p_label;
				if (extras != null) {
					markIndicator = extras.getInt("MARKTYPE", BoardManager.M_NONE);
					addIndicator = extras.getInt("ADDTYPE", BoardManager.M_NONE);
					p_label = extras.getString("POINTLABEL");
				} else {
					markIndicator = BoardManager.M_NONE;
					addIndicator = BoardManager.M_NONE;
					p_label = "";
				}
				bm.set_markup(markIndicator, addIndicator, p_label);
			}
			break;
		case HELP_VIEW:
		case GAMEINFO_VIEW:
		default:
		}  
	}
	
	private void doMode(String s) {
		if (s != null) {
			if (!s.contentEquals(game_mode)) {
				bm.set_mode(s, autoPlayInterval, autoPlayPause, autoPlaySound);
				game_mode = s;
		    	if (game_mode.contentEquals(GameBoardOptions.BROWSE)
		    			 || game_mode.contentEquals(GameBoardOptions.AUTOPLAY)) {
		    		tmEndGame.setText(R.string.blank5);
		    	} else {
		    		tmEndGame.setText(R.string.Pass);
		    	}
				refreshButtons(bm.getButtonState());
			}
		}		
	}
	
	private void doSetModeSelected() { // from other than popup
		int pos;
		if (game_mode.contentEquals(GameBoardOptions.MARKUP)) {
	     	pos = 4;
	    } else if (game_mode.contentEquals(GameBoardOptions.GUESSMOVE)) {
	    	pos = 3;
	    } else if (game_mode.contentEquals(GameBoardOptions.EDIT)) {
	    	pos = 2;
	    } else if (game_mode.contentEquals(GameBoardOptions.BROWSE)) {
	    	pos = 1;
	    } else if (game_mode.contentEquals(GameBoardOptions.AUTOPLAY)) {
	    	pos = 0;	
	    } else {
	    	pos = 1;
	    }
		tmModeSelected = pos;
    	tmMode.setText(tmModeItems[pos]);
	}
	
	private void doModeSelected () {  // set by popup
		String s = GameBoardOptions.BROWSE;
		int pos = tmModeSelected;
    	switch (pos) {
    	case 0: s = GameBoardOptions.AUTOPLAY; break;
    	case 1: s = GameBoardOptions.BROWSE; break;
    	case 2: s = GameBoardOptions.EDIT; break;
    	case 3: s = GameBoardOptions.GUESSMOVE; break;
    	case 4: s = GameBoardOptions.MARKUP; break;
    	case 5: doOptions(); return;
    	default:
    	}
    	doMode(s);
    	tmMode.setText(tmModeItems[pos]);
	}
		
	private void doEndGame() { 
		if (game_mode.contentEquals(GameBoardOptions.BROWSE)
				|| game_mode.contentEquals(GameBoardOptions.AUTOPLAY)) return;
		if (game_action.contains(BoardManager.GA_SCORING)) {
			game_action = BoardManager.GA_PLAY;
			game_status = BoardManager.GS_PLAY;
			bm.changeGameState(game_action, game_status);
			tmEndGame.setText(R.string.EndGame);
			// now finish
		} else {
			final String[] items = {ctw.getString(R.string.Pass), ctw.getString(R.string.Resign), ctw.getString(R.string.Score), ctw.getString(R.string.cancelButton)};
			AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
			builder.setTitle(ctw.getString(R.string.select)); 
			ArrayAdapter<String> sel_adapter = new ArrayAdapter<String>(ctw,
	                android.R.layout.simple_list_item_1, items);
	        sel_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
	        builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	switch (item) {
			    	case 0: 
			    		bm.doPass();
			    		break;	
			    	case 1: 
			    		if (!game_mode.contentEquals(GameBoardOptions.EDIT)) break;
			    		bm.doResign();
			    		break;
			    	case 2: 
			    		if (!game_mode.contentEquals(GameBoardOptions.EDIT)) break;
			    		game_action = BoardManager.GA_SCORING;
			    		game_status = BoardManager.GS_SCORE;
			    		bm.changeGameState(game_action, game_status);
			    		tmEndGame.setText(R.string.FinishScore);
			    		break;
			    	default: // cancel
			    		 // do nothing
			    	}
			    }
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
	
	private void doInfo() {
		String gi = bm.getGameInfo();
		String [] buttonTexts = new String [1];
		buttonTexts[0] = getString(R.string.doneButton);
		final Intent giIntent = new Intent(this, MsgView.class);
		giIntent.putExtra("TITLE", getString(R.string.GameInfo));
		giIntent.putExtra("ButtonTexts", buttonTexts);
		giIntent.putExtra("MsgText", gi); 
		giIntent.putExtra("MsgHelpType", commonStuff.HELP_GAMEINFO);
		giIntent.putExtra("BOARDLAYOUT", boardLayout);
		startActivityForResult(giIntent, GAMEINFO_VIEW);
	}
	
	private void doSave() {
		if (mFileName.contentEquals("")) {
			 if (bm != null) mFileName = bm.getGameFileName();
		}
		if (bm != null) bm.saveTheGame(mFileName);
	}
	
	private void doHelp() {
		 final Intent helpIntent = commonStuff.helpDGS (commonStuff.HELP_EDIT, this);
		 helpIntent.putExtra("BOARDLAYOUT", boardLayout);
		 startActivityForResult(helpIntent, HELP_VIEW);
	}
	 
	 private void doOptions() {
		 final Intent prefsIntent = new Intent(GameBoard.this, GameBoardOptions.class);
			prefsIntent.putExtra("LAYOUT", boardLayout);
			prefsIntent.putExtra("MODE", game_mode);
			prefsIntent.putExtra("SKIPMOVES", numMovesToSkip);
			prefsIntent.putExtra("NUMPREV", numPrev);
			prefsIntent.putExtra("AUTOPLAYPAUSE", autoPlayPause);
			prefsIntent.putExtra("AUTOPLAYSOUND", autoPlaySound);
			prefsIntent.putExtra("AUTOPLAYINTERVAL", autoPlayInterval);
			prefsIntent.putExtra("BOARDLAYOUT", boardLayout);
			startActivityForResult(prefsIntent, GET_OPTS);
	 }
	
	 public boolean onPrepareOptionsMenu(Menu menu) {
		 menu.clear();
		 MenuItem gameinfo_menu = menu.add(0, MENU_GAMEINFO, 0, getString(R.string.GameInfo));
		 gameinfo_menu.setIcon(R.drawable.ic_menu_info_details);
		 MenuItem options_menu = menu.add(0, MENU_OPTIONS, 0, getString(R.string.Options));
		 options_menu.setIcon(R.drawable.ic_menu_preferences);
		 MenuItem save_menu = menu.add(0, MENU_SAVE, 0, getString(R.string.Save));
		 save_menu.setIcon(R.drawable.ic_menu_save);
		 MenuItem pass_menu;
		 if (!game_action.contains(BoardManager.GA_SCORING)) {
			 pass_menu = menu.add(0, MENU_END_GAME, 0, getString(R.string.EndGame));
		 } else {
			 pass_menu = menu.add(0, MENU_END_GAME, 0, getString(R.string.FinishScore));
		 }
		 pass_menu.setIcon(R.drawable.ic_menu_forward);
		 MenuItem help_menu = menu.add(0, MENU_HELP, 0, getString(R.string.Help));
		 help_menu.setIcon(R.drawable.ic_menu_help);
		 return true;
		 }
	 
	 public boolean onOptionsItemSelected(MenuItem item) {
		 
		 switch (item.getItemId()) {
		 case MENU_OPTIONS:
			 doOptions();
			 break;
		 case MENU_SAVE:
			 doSave();
			 break;
		 case MENU_END_GAME:
			 doEndGame();
			 break;
		 case MENU_HELP:
			 doHelp();
			 break;
		 case MENU_GAMEINFO:
			 doInfo();
			 break;
		 default:
				// nothing 
		 }
		 return false;
	 }

	public void displayComment(String comment) {
		recComment.setText(comment);
	}

	public void displayFullButtons(int buttonState, int ps) {
		playState = ps;
		refreshButtons(buttonState);
	}

	public void displayGameInfo(String info) {
		recInfo.setText(info);	
	}

	public void displayMoveInfo(String info) {
		moveInfo.setText(info);
	}
	

	public void displayZoomButtons(int buttonState, int ps) {
		playState = ps;
		rec_left_button.setBackgroundColor(MainDGS.disabled_button_color);
		rec_right_button.setBackgroundColor(MainDGS.disabled_button_color);
		rec_far_left_button.setBackgroundColor(MainDGS.disabled_button_color);
		//rec_far_right_button.setText(R.string.unzoomButton);
		rec_far_right_button.setImageResource(R.drawable.btn_zoom_page_normal);
		rec_far_right_button.setBackgroundColor(MainDGS.enabled_button_color);
		rec_mid_left_button.setBackgroundColor(MainDGS.disabled_button_color);
		rec_mid_right_button.setBackgroundColor(MainDGS.disabled_button_color);
	}

	public void finishGrinding(int buttons) {
		if (grinder) {
			Bundle rslts = new Bundle();
			rslts.putBoolean("GRINDING",true);
			Intent mIntent = new Intent();
			mIntent.putExtras(rslts);
			setResult(RESULT_OK, mIntent);
			finish();
		} else {
			refreshButtons(buttons);
		}
	}

	public void requestMark(int mType, int aType, String pLabel, GoColor mvColor) {
		final Intent muIntent = new Intent(GameBoard.this,
				MarkUpView.class);
		muIntent.putExtra("MARKTYPE", mType);
		muIntent.putExtra("ADDTYPE", aType);
		muIntent.putExtra("POINTLABEL", pLabel);
		if (mvColor == GoColor.WHITE) {
			muIntent.putExtra("GRAPHICNONE", BoardManager.getGraphic(BoardManager.W)); 
			muIntent.putExtra("GRAPHICMARK", BoardManager.getGraphic(BoardManager.WX));
			muIntent.putExtra("GRAPHICTRIANGLE", BoardManager.getGraphic(BoardManager.WT));
			muIntent.putExtra("GRAPHICCIRCLE", BoardManager.getGraphic(BoardManager.WC)); 
			muIntent.putExtra("GRAPHICSQUARE", BoardManager.getGraphic(BoardManager.WS)); 
			muIntent.putExtra("GRAPHICTERRW", BoardManager.getGraphic(BoardManager.W)); 
			muIntent.putExtra("GRAPHICTERRB", BoardManager.getGraphic(BoardManager.WB)); 
		} else if (mvColor == GoColor.BLACK) {
			muIntent.putExtra("GRAPHICNONE", BoardManager.getGraphic(BoardManager.B)); 
			muIntent.putExtra("GRAPHICMARK", BoardManager.getGraphic(BoardManager.BX));
			muIntent.putExtra("GRAPHICTRIANGLE", BoardManager.getGraphic(BoardManager.BT));
			muIntent.putExtra("GRAPHICCIRCLE", BoardManager.getGraphic(BoardManager.BC)); 
			muIntent.putExtra("GRAPHICSQUARE", BoardManager.getGraphic(BoardManager.BS)); 
			muIntent.putExtra("GRAPHICTERRW", BoardManager.getGraphic(BoardManager.BW)); 
			muIntent.putExtra("GRAPHICTERRB", BoardManager.getGraphic(BoardManager.B)); 
		} else {
			muIntent.putExtra("GRAPHICNONE", BoardManager.getGraphic(BoardManager.BLNK)); 
			muIntent.putExtra("GRAPHICMARK", BoardManager.getGraphic(BoardManager.AX));
			muIntent.putExtra("GRAPHICTRIANGLE", BoardManager.getGraphic(BoardManager.AT));
			muIntent.putExtra("GRAPHICCIRCLE", BoardManager.getGraphic(BoardManager.AC)); 
			muIntent.putExtra("GRAPHICSQUARE", BoardManager.getGraphic(BoardManager.AS)); 
			muIntent.putExtra("GRAPHICTERRW", BoardManager.getGraphic(BoardManager.AW)); 
			muIntent.putExtra("GRAPHICTERRB", BoardManager.getGraphic(BoardManager.AB)); 
		}
		muIntent.putExtra("GRAPHICADDEMPTY", BoardManager.getGraphic(BoardManager.BLNK)); 
		muIntent.putExtra("GRAPHICADDW", BoardManager.getGraphic(BoardManager.W)); 
		muIntent.putExtra("GRAPHICADDB", BoardManager.getGraphic(BoardManager.B));
		muIntent.putExtra("BOARDLAYOUT", boardLayout);
		startActivityForResult(muIntent, MARKUP_OPTS);
	}
	
	public void setScoringUpdated (boolean val) {
	}

	public void moveMade(int state) {
	}
}
