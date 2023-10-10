package com.hg.anDGS;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import net.sf.gogui.go.GoColor;

public class PlayDGS extends DGSActivity implements BoardUpdate{
	// play states left button right button
	// DISPLAY_BOARD Skip All Skip
	// ZOOM_BOARD blank Unzoom
	// MADE_MOVE Ok Undo
	
	protected static final int HELP_VIEW = 0;
	protected static final int GAMEINFO_VIEW = 1;
	protected static final int PHRASE_VIEW = 2;
	private static final int MENU_HELP = 0;
	private static final int MENU_DOWN_LOAD = 1;
	private static final int MENU_PASS = 2;
	private static final int MENU_RESIGN = 3;
	private static final int MENU_GAMEINFO = 4;
	
	protected static final int DAME_POINTS = 0; 
	protected static final int NEUTRAL_POINTS = 1;
	protected static final int WHITE_STONES = 2;
	protected static final int BLACK_STONES = 3;
	protected static final int WHITE_DEAD = 4;
	protected static final int BLACK_DEAD = 5;
	protected static final int WHITE_TERRITORY = 6;
	protected static final int BLACK_TERRITORY = 7;
	protected static final int SCORE_DATA_SIZE = 8;
	
	protected static final int LAST_DEL_MOVE = 10;
	
	BoardManager bm;

	private TableLayout topMenuLayout;
	private TextView tmPassAccept;
	private TextView tmResign;
	private TextView tmInfo;
	private TextView tmDownLoad;
	private TextView tmHelp;
	private TextView playerInfo;
	private TextView moveInfo;
	private TextView playerComment;
	private LinearLayout playerNoteLayout;
	private TextView playerNote;
	private FrameLayout boardSwitcher;
	private TextView play_right_button;
	private TextView play_left_button;
	private int display_width;
	private int display_length;
	private float display_scale;
	private float scaleLines;
	private int playState;
	private boolean scoreUpdated = false;
	private boolean scoreAccepted = false;
	private String boardLayout = PrefsDGS.PORTRAIT;
    private String move_control = PrefsDGS.ZOOM7X7;
    private String boardCoordTxt = PrefsDGS.NO_COORD;
	private String boardBGTxt = PrefsDGS.BG_WOOD;
	private int customBGvalue = MainDGS.BOARD_COLOR;
	private String boardStoneTxt = PrefsDGS.STONE_CLAM;
	private int numPrev = 0;
	private String defaultDir = null;
	private String timeLeft = "";
	private String sgf = "";
	private String gameId = "0";
	private String moveId = "-1";
	private int moveNo = -1;
	private String game_action = "0";
	private String game_status = "UNKNOWN";
	private String handicap = "0";
	private String [] score_data = null;
	private String curr_note = "";
	private String orig_note = "";
	private boolean gameNotes = false;
	private boolean resign_game = false;
	private boolean delete_game = false;
	private String theme;
	private ContextThemeWrapper ctw;
	private CommonStuff commonStuff = new CommonStuff();
	private CommonFileStuff commonFileStuff = new CommonFileStuff();
	private StoredMoves storMov = StoredMoves.getInstance();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			sgf = extras.getString("SGF");
			gameId = extras.getString("GID");
			moveId = extras.getString("MOVEID");
			game_action = extras.getString("GAMEACTION");
			game_status = extras.getString("GAMESTATUS");
			handicap = extras.getString("HANDICAP");
			timeLeft = extras.getString("TIMELEFT");
			curr_note = extras.getString("NOTE");
			gameNotes = extras.getBoolean("GAMENOTES");
			score_data = extras.getStringArray("SCOREDATA");
			boardLayout = extras.getString("BOARDLAYOUT");
		}
		moveNo = Integer.parseInt(moveId);
		if (curr_note == null) curr_note = "";
		orig_note = curr_note;
		if (score_data == null) {
			score_data = new String [SCORE_DATA_SIZE];
			score_data[DAME_POINTS] = ""; 
			score_data[NEUTRAL_POINTS] = "";
			score_data[WHITE_STONES] = "";
			score_data[BLACK_STONES] = "";
			score_data[WHITE_DEAD] = "";
			score_data[BLACK_DEAD] = "";
			score_data[WHITE_TERRITORY] = "";
			score_data[BLACK_TERRITORY] = "";

		}
		
		SharedPreferences prefs = getSharedPreferences("MainDGS", 0);
        theme = prefs.getString("com.hg.anDGS.Theme", PrefsDGS.DEFAULT_THEME);
		move_control = prefs.getString("com.hg.anDGS.MoveControl", PrefsDGS.ZOOM7X7);
		boardCoordTxt = prefs.getString("com.hg.anDGS.BoardCoord", PrefsDGS.NO_COORD);
		boardBGTxt = prefs.getString("com.hg.anDGS.BoardBackground", PrefsDGS.BG_WOOD);
		customBGvalue = prefs.getInt("com.hg.anDGS.CustomBoardBackground", MainDGS.BOARD_COLOR);
		boardStoneTxt = prefs.getString("com.hg.anDGS.BoardStone", PrefsDGS.STONE_CLAM);
		numPrev = prefs.getInt("com.hg.anDGS.PlayNumPrev", 0);
		defaultDir = commonFileStuff.getSgfDirName();
        boolean keepScreenOn = prefs.getBoolean("com.hg.anDGS.KeepScreenOn", false);
        boolean displayTopMenu = prefs.getBoolean("com.hg.anDGS.PlayDisplayTopMenu", true);
		scaleLines = prefs.getFloat("com.hg.anDGS.ScaleLines", 1);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int flg = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		if (keepScreenOn) {
			flg |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		}
		getWindow().setFlags(flg, flg);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		display_width = Math.min(metrics.heightPixels, metrics.widthPixels);
		display_length = Math.max(metrics.heightPixels, metrics.widthPixels);
		display_scale = metrics.density;
		
		this.setTheme(commonStuff.getCommonStyle(theme));
		ctw = new ContextThemeWrapper(this, commonStuff.getCommonTheme(theme));
		
		if (boardLayout.contains("landscape")) {
			//setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setContentView(R.layout.playls);
			play_left_button = findViewById(R.id.playLeftButtonLS);
			play_right_button = findViewById(R.id.playRightButtonLS);
			playerComment = findViewById(R.id.playerCommentLS);
			playerNoteLayout = findViewById(R.id.playerNoteLSLayout);
			playerNote = findViewById(R.id.playerNoteLS);
			playerInfo = findViewById(R.id.playerInfoLS);
			moveInfo = findViewById(R.id.playMoveInfoLS);
			boardSwitcher = findViewById(R.id.boardGridLS);
			topMenuLayout = findViewById(R.id.playTopMenuLS);
			tmPassAccept = findViewById(R.id.playTMPassLS);
			tmResign = findViewById(R.id.playTMResignLS);
			tmInfo = findViewById(R.id.playTMInfoLS);
			tmDownLoad = findViewById(R.id.playTMDownLoadLS);
			tmHelp = findViewById(R.id.playTMHelpLS);
		} else {
			//setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setContentView(R.layout.play);
			play_left_button = findViewById(R.id.playLeftButton);
			play_right_button = findViewById(R.id.playRightButton);
			playerComment = findViewById(R.id.playerComment);
			playerNoteLayout = findViewById(R.id.playerNoteLayout);
			playerNote = findViewById(R.id.playerNote);
			playerInfo = findViewById(R.id.playerInfo);
			moveInfo = findViewById(R.id.playMoveInfo);
			boardSwitcher = findViewById(R.id.boardGrid);
			topMenuLayout = findViewById(R.id.playTopMenu);
			tmPassAccept = findViewById(R.id.playTMPass);
			tmResign = findViewById(R.id.playTMResign);
			tmInfo = findViewById(R.id.playTMInfo);
			tmDownLoad = findViewById(R.id.playTMDownLoad);
			tmHelp = findViewById(R.id.playTMHelp);
		}
		
		tmPassAccept.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						doPassAccept();
					}
				});
			}
		});
		
		tmResign.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						doResign();
					}
				});
			}
		});
		if (moveNo <= LAST_DEL_MOVE) {
			tmResign.setText(R.string.ResignDelete);
		 } else {
			 tmResign.setText(R.string.Resign);
		 }
		
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
		
		tmDownLoad.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						doDownLoad();
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
		
		if (!displayTopMenu) {
			topMenuLayout.removeAllViews();
		}

		play_left_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						switch (playState) {
						case BoardManager.FULL_BOARD: // Skip or Submit
							if (scoreUpdated || scoreAccepted) {
								playState = BoardManager.UNINITIALIZED;
								sendDGSresult();
							} else {
								playState = BoardManager.UNINITIALIZED;
								cleanUpAndReturn("SKIP");
							}
							break;
						case BoardManager.CANNOT_MOVE:
							playState = BoardManager.UNINITIALIZED;
							cleanUpAndReturn("SKIP");
							break;
						case BoardManager.ZOOM_BOARD: // blank
							// do nothing
							break;
						case BoardManager.MADE_MOVE: // Submit
							playState = BoardManager.UNINITIALIZED;
							sendDGSresult();
							break;
						default:
						}
					}
				});
			}
		});

		play_right_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						switch (playState) {
						case BoardManager.FULL_BOARD: // SkipAll or resume playing
							if (scoreUpdated) {
								changeToGaPlay();
							} else {
								playState = BoardManager.UNINITIALIZED;
								cleanUpAndReturn("SKIPALL");
							}
							break;
						case BoardManager.CANNOT_MOVE:
							playState = BoardManager.UNINITIALIZED;
							cleanUpAndReturn("SKIPALL"); 
							break;
						case BoardManager.ZOOM_BOARD: // Unzoom
							// playState will be set from BoardManager to DISPLAY_BOARD
							bm.unzoom();
							break;
						case BoardManager.MADE_MOVE: // Undo
							// playState will be set from BoardManager to DISPLAY_BOARD
							if (scoreAccepted) {
								playState = BoardManager.FULL_BOARD;
								scoreAccepted = false;
								bm.unDoScoreAccepted();
							} else	if (delete_game) {
								delete_game = false;
								bm.unDoDelete();
							} else if (resign_game) {
								resign_game = false;
								bm.doResign();  // if it was resigned, will clear it.
							} else {
								bm.unDoMove();
							}
							break;
						default:
						}
					}
				});
			}
		});

		playerComment.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ((playState == BoardManager.MADE_MOVE) && (bm.movesMade < 2)) { // only alloe comments on first move
					final EditText input = new EditText(ctw);
				    input.setText(playerComment.getText());
				    new AlertDialog.Builder(ctw)
				    .setTitle(R.string.Comment)
				    .setMessage(R.string.EditComment)
				    .setView(input)
				    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String s = input.getText().toString();
							setPlayerComment(s);
                            }})
					.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}})
				    .show();
				}
		}});
		
		playerComment.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				if ((playState == BoardManager.MADE_MOVE) && (bm.movesMade < 2)) {
					final Intent phraseIntent = new Intent(PlayDGS.this, PhraseView.class);
					phraseIntent.putExtra("BOARDLAYOUT", boardLayout);
					startActivityForResult(phraseIntent, PHRASE_VIEW); 
				}
				return true;
		}});
		
		playerNote.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final EditText input = new EditText(ctw);
			    input.setText(playerNote.getText());
			    new AlertDialog.Builder(ctw)
			    .setTitle(R.string.GameNotes)
			    .setMessage(R.string.EditComment)
			    .setView(input)
			    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	String s = input.getText().toString();
			    	curr_note = s;
			    	playerNote.setText(s); }})
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }})
			    .show();
			}
		});

		playerNote.setText(curr_note);
		if (!gameNotes) {
			playerNoteLayout.removeAllViews();
		}
		
		if (displayTopMenu) {
			if (game_action.contentEquals(BoardManager.GA_SCORING)) {
				tmPassAccept.setText(R.string.AcceptScore);
			} else {
				tmPassAccept.setText(R.string.Pass);
			}
		}
		play_left_button.setText(R.string.skipButton);
		//play_left_button.setBackgroundDrawable(null);
		play_right_button.setText(R.string.skipAllButton);
		//play_right_button.setBackgroundDrawable(null);
		final Handler handler = new Handler();
		handler.post(new Runnable() { 
			public void run() {	
				openTheGame();
			}
		});

	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);  
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
					cleanUpAndReturn("SKIPALL");
				}
				return true;
			} else {
				cleanUpAndReturn("SKIPALL");
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	/*
	 * public void onResume() {
	 * 
	 * }
	 */
	private void openTheGame() {
		bm = new BoardManager(
			ctw,
			this, 
			boardSwitcher, 
			boardBGTxt,
			customBGvalue,
			boardStoneTxt,
			sgf, 
			game_action,
			game_status,
			handicap,
			timeLeft,
			"", // mFileName, 
			defaultDir,
			move_control,
			boardCoordTxt,
			GameBoardOptions.EDIT, // defaultEditMode, 
			GameBoardOptions.EDIT, //game_mode, 
			"none",  //version_text,
			numPrev,
			display_width,
			display_length,
			display_scale,
			scaleLines,
			1,  //autoPlayInterval,
			false,  //autoPlayPause, 
			false,  //autoPlaySound,
			true, // once
			score_data
			);
	}
	
	private void sendDGSresult() {
		if (game_action.contentEquals(BoardManager.GA_SET_HA)) {
			sendDGShandicap();
		} else if (game_action.contentEquals(BoardManager.GA_PLAY)) {
			sendDGSmove();
		} else if (game_action.contentEquals(BoardManager.GA_SCORING)) {
			if (scoreAccepted) {
				acceptDGSscore();
			} else {
				sendDGSscore();
			}
		} else {
			// something else happened
			cleanUpAndReturn("SKIP");
		}
	}
	
	private void sendDGShandicap() {
		gameNotes = gameNotes && !orig_note.contentEquals(curr_note); // enabled and a change in the note contents
		String h_movs = bm.get_handicap();
		String msg = bm.get_comment();
		Bundle rslts = new Bundle();
		rslts.putString("RESULT", "HANDICAP");
		rslts.putString("MOV",h_movs);
		rslts.putString("MSG",msg);
		rslts.putString("NOTE",curr_note);
		rslts.putBoolean("GAMENOTES", gameNotes);
    	rslts.putString("GID",gameId);
    	rslts.putString("MOVEID",moveId);
		Intent mIntent = new Intent();
		mIntent.putExtras(rslts);
		setResult(RESULT_OK, mIntent);
		finish();
	}
	
	private void sendDGSmove() {
		gameNotes = gameNotes && !orig_note.contentEquals(curr_note); // enabled and a change in the note contents
		String result, mov, colr, msg;
		if (delete_game) {
			result = "DELETE";
		} else if (resign_game) {
			result = "RESIGNED";
		} else {
			result = "MOVED";
		}
	   	mov = bm.getMoveCoord(false);
		colr = bm.getMoveColor(false);
		msg = bm.get_comment();
        storMov.newStoredMoveGame(bm.getCurrentPredictedMoves(moveId), gameId);

		Bundle rslts = new Bundle();
		rslts.putString("RESULT", result); 
    	rslts.putString("COLOR",colr);
    	rslts.putString("MOV",mov);
    	rslts.putString("MSG",msg);
    	rslts.putString("NOTE", curr_note);
		rslts.putBoolean("GAMENOTES", gameNotes);
    	rslts.putString("GID",gameId);
    	rslts.putString("MOVEID",moveId);
		Intent mIntent = new Intent();
		mIntent.putExtras(rslts);
		setResult(RESULT_OK, mIntent);
		finish();
	}
	
	private void sendDGSscore() {
		String scoreDiff = bm.getScoreDifference();
		/*
		String ds = "BLACK_STONES:"+score_data[PlayDGS.BLACK_STONES]
				  + "\nWHITE_STONES:" + score_data[PlayDGS.WHITE_STONES]
				  + "\nBLACK_DEAD:"+score_data[PlayDGS.BLACK_DEAD]
				  + "\nWHITE_DEAD:" + score_data[PlayDGS.WHITE_DEAD]
				  + "\nBLACK_TERRITORY:" + score_data[PlayDGS.BLACK_TERRITORY]
				  + "\nWHITE_TERRITORY" + score_data[PlayDGS.WHITE_TERRITORY]
				  + "\nNEUTRAL_POIN:" + score_data[PlayDGS.NEUTRAL_POINTS]
				  + "\nDAME_POINTS:" + score_data[PlayDGS.DAME_POINTS]
				  + "\nDIFF:" + scoreDiff;
		 new AlertDialog.Builder(ctw)
		    .setTitle("")
		    .setMessage(ds)
		    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	;  
			    }
			    }).show();
		*/
		gameNotes = false;  // no game notes during scoring
		String msg = bm.get_comment();
		Bundle rslts = new Bundle();
		rslts.putString("RESULT", "SCORE");
		rslts.putString("MOV", scoreDiff);  // things that changed from original score information
		rslts.putString("MSG",msg);
		rslts.putString("NOTE", curr_note);
		rslts.putBoolean("GAMENOTES", gameNotes);
    	rslts.putString("GID",gameId);
    	rslts.putString("MOVEID",moveId);
		Intent mIntent = new Intent();
		mIntent.putExtras(rslts);
		setResult(RESULT_OK, mIntent);
		finish();
	}
	
	private void acceptDGSscore() {
		gameNotes = false;  // no game notes during scoring
		String msg = bm.get_comment();
		Bundle rslts = new Bundle();
		rslts.putString("RESULT", "ACCEPTSCORE");
		rslts.putString("MSG",msg);
		rslts.putString("NOTE", curr_note);
		rslts.putBoolean("GAMENOTES", gameNotes);
    	rslts.putString("GID",gameId);
    	rslts.putString("MOVEID",moveId);
		Intent mIntent = new Intent();
		mIntent.putExtras(rslts);
		setResult(RESULT_OK, mIntent);
		finish();
	}
	
	private void cleanUpAndReturn(String result) {
		gameNotes = gameNotes && !orig_note.contentEquals(curr_note); // enabled and a change in the note contents
		Bundle rslts = new Bundle();
    	rslts.putString("RESULT",result); 
    	rslts.putString("COLOR","");
    	rslts.putString("MOV","");
    	rslts.putString("MSG","");
    	rslts.putString("NOTE", curr_note);
		rslts.putBoolean("GAMENOTES", gameNotes);
    	rslts.putString("GID",gameId);
    	rslts.putString("MOVEID",moveId);
		Intent mIntent = new Intent();
		mIntent.putExtras(rslts);
		setResult(RESULT_OK, mIntent);
		finish();
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	switch(requestCode) {
    	case PHRASE_VIEW:
    		String s;
    		Bundle extras;
    		if (resultCode == RESULT_OK) {
				try {
					extras = data.getExtras();
					if (extras != null) {
						s = extras.getString("PHRASE");
					} else {
						s = "";
					}
					setPlayerComment(s);
				} catch (Exception ignored) {
				}
    		}    		
    		if (gameNotes) {
    			playerNote.setText(curr_note);
    		}
    		break;
    	case HELP_VIEW:
    	case GAMEINFO_VIEW:
    	default:
    		if (gameNotes) {
    			playerNote.setText(curr_note);
    		}
    		break;
    	}
    }
	
	private void doPassAccept() {
		if (playState != BoardManager.FULL_BOARD) return;
		playState = BoardManager.MADE_MOVE;
		setPlayerComment("");
		if (game_action.contentEquals(BoardManager.GA_SCORING)) {
			scoreAccepted = true;
			bm.doScoreAccepted();
		} else {
			bm.doPass();
		}
	}
	
	private void changeToGaPlay() {
		scoreUpdated = false;
		tmPassAccept.setText(R.string.Pass);
		game_action = BoardManager.GA_PLAY;
		game_status = BoardManager.GS_PLAY;
		bm.changeGameState(game_action, game_status);
	}
	
	private void deleteTheGame() {
		if (game_action.equals(BoardManager.GA_SCORING)) {
			changeToGaPlay();
		}
		delete_game = true;
		bm.doDelete();
		playState = BoardManager.MADE_MOVE;
		setPlayerComment("");
		bm.redisplayFullBoard();
	}
	
	private void resignTheGame() {
		if (game_action.equals(BoardManager.GA_SCORING)) {
			changeToGaPlay();
		}
		resign_game = true;
		bm.doResign();
		playState = BoardManager.MADE_MOVE;
		setPlayerComment("");
		bm.redisplayFullBoard();
	}
	
	private void doResign() {
		if (playState != BoardManager.FULL_BOARD) return;
		if (moveNo <= LAST_DEL_MOVE) {
			final String[] items = {ctw.getString(R.string.Resign),
					ctw.getString(R.string.Delete),
					ctw.getString(R.string.cancelButton)};
			AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
			builder.setTitle(ctw.getString(R.string.select)); 
			ArrayAdapter<String> sel_adapter = new ArrayAdapter<String>(ctw,
	                android.R.layout.simple_list_item_1, items);
	        sel_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
	        builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	switch (item) {    	
			    	case 0: 
			    		resignTheGame();
			    		break;	    		
			    	case 1: 
			    		deleteTheGame();
			    		break;		    	
			    	default: // cancel
			    	}
			    }
			});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			resignTheGame();
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
		startActivityForResult(giIntent, GAMEINFO_VIEW);
	}
	
	 private void doDownLoad() {
		 if (playState != BoardManager.FULL_BOARD) return;
		 Bundle rslts = new Bundle();
		 rslts.putString("RESULT","DOWNLOAD"); 
		 rslts.putString("COLOR","");
		 rslts.putString("PREV","");
		 rslts.putString("MOV","");
		 rslts.putString("MSG","");
		 rslts.putString("GID",gameId);
		 Intent mIntent = new Intent();
		 mIntent.putExtras(rslts);
		 setResult(RESULT_OK, mIntent);
		 finish();
	 }
	 
	 private void doHelp() {
		 final Intent helpIntent = commonStuff.helpDGS (commonStuff.HELP_PLAY, this);
		 startActivityForResult(helpIntent, HELP_VIEW);
	 }
	
	 public boolean onPrepareOptionsMenu(Menu menu) {
		 menu.clear();
		 MenuItem gameinfo_menu = menu.add(0, MENU_GAMEINFO, 0, getString(R.string.GameInfo));
		 gameinfo_menu.setIcon(R.drawable.ic_menu_info_details);
		 MenuItem resign_menu;
		 if (moveNo <= LAST_DEL_MOVE) {
			 resign_menu = menu.add(0, MENU_RESIGN, 0, getString(R.string.ResignDelete));
		 } else {
			 resign_menu = menu.add(0, MENU_RESIGN, 0, getString(R.string.Resign));
		 }
		 resign_menu.setIcon(R.drawable.ic_menu_close_clear_cancel);
		 MenuItem pass_menu;
		 if (game_action.contentEquals(BoardManager.GA_SCORING)) {
			 pass_menu = menu.add(0, MENU_PASS, 0, getString(R.string.AcceptScore));
		 } else {
			 pass_menu = menu.add(0, MENU_PASS, 0, getString(R.string.Pass));
		 }
		 pass_menu.setIcon(R.drawable.ic_menu_forward);
		 MenuItem down_load_menu = menu.add(0, MENU_DOWN_LOAD, 0, getString(R.string.downLoadGame));
		 down_load_menu.setIcon(R.drawable.ic_menu_upload);
		 MenuItem help_menu = menu.add(0, MENU_HELP, 0, getString(R.string.Help));
		 help_menu.setIcon(R.drawable.ic_menu_help);
		 return true;
		 }
	 
	 public boolean onOptionsItemSelected(MenuItem item) {
		 
		 switch (item.getItemId()) {
		 case MENU_HELP:
			 doHelp();
			 break;
		 case MENU_DOWN_LOAD:
			 doDownLoad();
			 break;
		 case MENU_RESIGN:
			 doResign();
			 break;
		 case MENU_PASS:
			 doPassAccept();
			 break;
		 case MENU_GAMEINFO:
			 doInfo();
			 break;
		 default:
				// nothing 
		 }
		 return false;
	 }

	 private void setPlayerComment(String comment) {
		 bm.set_comment(comment);
		 playerComment.setText(comment);
	 }

	public void displayComment(String comment) {
		playerComment.setText(comment);
	}

	public void displayFullButtons(int buttonState, int ps) {
		playState = ps;
		if (playState == BoardManager.FULL_BOARD) {
			if (scoreUpdated || scoreAccepted) {
				play_left_button.setText(R.string.okButton);     // Submit
				play_right_button.setText(R.string.ResumePlaying);  // 
				//play_right_button.setBackgroundDrawable(null);
				//play_right_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_menu_revert));
			} else {
				play_left_button.setText(R.string.skipButton);
				play_right_button.setText(R.string.skipAllButton);
				//play_right_button.setBackgroundDrawable(null);
			}
		} else if (playState == BoardManager.MADE_MOVE) {
			play_left_button.setText(R.string.okButton);     // Submit
			play_right_button.setText(R.string.undoButton);  //  use an undo image as a background and null text
			//play_right_button.setBackgroundDrawable(null);
			//play_right_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_menu_revert));
		}
	}

	public void displayGameInfo(String info) {
		playerInfo.setText(info);
	}

	public void displayMoveInfo(String info) {
		moveInfo.setText(info);
	}

	public void displayZoomButtons(int buttonState, int ps) {
		playState = ps;
		play_left_button.setText(R.string.blankButton);
		play_right_button.setText(R.string.unzoomButton);  //(null);
	}

	public void finishGrinding(int buttonState) {
	}
	
	public void requestMark(int mType, int aType, String pLabel,
			GoColor mvColor) {
	}
	
	public void setScoringUpdated (boolean val) {
		scoreUpdated = val;
		displayFullButtons(0, BoardManager.MADE_MOVE);
	}

}
