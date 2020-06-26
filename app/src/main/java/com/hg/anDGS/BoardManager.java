package com.hg.anDGS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import net.sf.gogui.game.ConstGameInfo;
import net.sf.gogui.game.ConstNode;
import net.sf.gogui.game.ConstSgfProperties;
import net.sf.gogui.game.Game;
import net.sf.gogui.game.GameTree;
import net.sf.gogui.game.MarkType;
import net.sf.gogui.game.Node;
import net.sf.gogui.game.SgfProperties;
import net.sf.gogui.game.StringInfo;
import net.sf.gogui.game.StringInfoColor;
import net.sf.gogui.go.Board;
import net.sf.gogui.go.ConstPointList;
import net.sf.gogui.go.CountScore;
import net.sf.gogui.go.GoColor;
import net.sf.gogui.go.GoPoint;
import net.sf.gogui.go.Komi;
import net.sf.gogui.go.Move;
import net.sf.gogui.go.PointList;
import net.sf.gogui.go.Score;
import net.sf.gogui.go.Score.ScoringMethod;
import net.sf.gogui.sgf.SgfError;
import net.sf.gogui.sgf.SgfReader;
import net.sf.gogui.sgf.SgfWriter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static net.sf.gogui.go.GoColor.BLACK;
import static net.sf.gogui.go.GoColor.WHITE;

public class BoardManager implements BoardClick{
	// play states left button right button
	public static final int UNINITIALIZED = 0;
	public static final int FULL_BOARD = 1; // common
	public static final int ZOOM_BOARD = 2;   //common
	public static final int MADE_MOVE = 3; // dgs client
	public static final int CANNOT_MOVE = 4;  // dgs client
	// move control types
	public static final int ZOOM_CONTROL = 0;
	public static final int D_PAD_CONTROL = 1;
	public static final int ONE_TOUCH_CONTROL = 2;
	public static final int SLIDE_CONTROL = 3;
	// button states bit mapped
	public static final int PARENT_NODE = 1;
	public static final int CHILD_NODE = 2;
	public static final int NEXT_NEIGHBOR = 4;
	public static final int PREV_NEIGHBOR = 8;
	public static final int LAST_NODE = 16;
	public static final int AUTOPLAY_RUNNING = 32;
	// game_action
	public static final String GA_UNKNOWN = "0"; 
	public static final String GA_SET_HA = "1";     // set handicap-stones
	public static final String GA_PLAY = "2";       // play next move (or pass or resign)
	public static final String GA_SCORING = "3";    // do scoring
	public static final String GA_FAIR_KOMI_0 = "10"; // fair-komi-negotiation: enter komi-bid
	public static final String GA_FAIR_KOMI_1 = "11"; // fair-komi-negotiation: enter komi-bid or accept last komi-bid
	public static final String GA_FAIR_KOMI_2 = "12"; // fair-komi-negotiation: choose color
	public static final String GA__FAIR_KOMI_3 = "13"; // fair-komi-negotiation: wait (shouldn't happen for player-to-move)
	// game status
	public static final String GS_UNKNOWN = "UNKNOWN";
	public static final String GS_KOMI = "KOMI";
	public static final String GS_PLAY = "PLAY";
	public static final String GS_PASS = "PASS";
	public static final String GS_SCORE = "SCORE";
	public static final String GS_SCORE2 = "SCORE2"; 
	
	//graphic types
	public static final int BLNK = 0; // blank area
	public static final int B = 1;
	public static final int BM = 2;
	public static final int BW = 3;
	public static final int BC = 4;
	public static final int BS = 5;
	public static final int BT = 6;
	public static final int BX = 7;
	public static final int W = 8;
	public static final int WM = 9;
	public static final int WB = 10;
	public static final int WC = 11;
	public static final int WS = 12;
	public static final int WT = 13;
	public static final int WX = 14;
	public static final int AB = 15; 
	public static final int AC = 16;
	public static final int AS = 17;
	public static final int AT = 18;
	public static final int AW = 19;
	public static final int AX = 20;
	public static final int BP = 21;
	public static final int WP = 22;
	public static final int YP = 23;
	
	// markup codes
	public static final int M_NONE = 0;
	public static final int M_MOVE = 1;
	public static final int M_MARK = 2;
	public static final int M_TRIANGLE = 3;
	public static final int M_CIRCLE = 4;
	public static final int M_SQUARE = 5;
	public static final int M_TERR_W = 6;
	public static final int M_TERR_B = 7;
	public static final int M_ADDEMPTY = 8;
	public static final int M_ADD_W = 9;
	public static final int M_ADD_B = 10;
	
	private int xFBOffset = 0;
	private int yFBOffset = 0;

	// board graphics 
	private ContextThemeWrapper ctw;
    private CommonFileStuff commonFileStuff = new CommonFileStuff();

	private FrameLayout boardSwitcher;
	private Timer autoPlayTimer = new Timer();
	private Timer autoSkipTimer = new Timer();
	private int display_width = 240;
	@SuppressWarnings("unused")
	private int display_length = 300;
    @SuppressWarnings("unused")
	private float display_scale;
	private float scale_lines;
	private static boolean monochrome_stones = false;
	private static boolean whiteBG = false;

	private ViewGroup.LayoutParams full_grid_layout;
	private ViewGroup.LayoutParams zoom_grid_layout;
	private BoardTable fullStonesTable;
	private BoardTable slideStonesTable = null;
	private BoardLines fullBoardLines;
	private PointList fullBoardHoshi = null;
	private BoardTable zoomStonesTable;
	private BoardLines zoomBoardLines;
	private PointList zoomBoardHoshi = null;
	private TableLayout fullStonesView;
	private TableLayout slideStonesView = null;
	private TableLayout zoomStonesView;
	private int fullGridBrdSize = 19;
	private int zoomGridBrdSize = 7;
	private int fullGrphsBrdSize = 19;
	private int fullGrphsStnSize = 17; // in pixels
	private float fullTxtSize;
	private int fullPadSize = 0;
	private int zoomGrphsBrdSize = 7;
	private int zoomGrphsStnSize = 17; // in pixels
	private float zoomTxtSize;
	private int zoomPadSize = 0;
	private int boardState = UNINITIALIZED;
	private GoColor playerColorToMove = GoColor.BLACK;
	private int fullBoardX, fullBoardY;
	private int goguiX, goguiY;
	@SuppressWarnings("unused")
	private float sldLastTouchX = -1;   // maybe we change our minds about setting direction and need this
	@SuppressWarnings("unused")
	private float sldLastTouchY = -1;   // ditto
	private long sldTimeStamp = 0;
	private int sldLastTouchStoneX = -1;
	private int sldLastTouchStoneY = -1;
	private int sldLastCursorStoneX = -1;
	private int sldLastCursorStoneY = -1;
	private int sldDirection = -1;	

	private Board gogui_board;
	private GameTree gogui_tree;
	private Game gogui_game;
	private Node rootNode;
	private Node lastNode;
	private Node newNode;
	private Node currNode;
	private CountScore scoreCounter = new CountScore();
	private boolean unSavedChanges = false;
	private String mSGF = "(;FF[4]GM[1]SZ[19]KM[6.5]PB[DefaultBlack]PW[DefaultWhite]PL[B])";
	private String m_game_action = GA_UNKNOWN;
	private String m_game_status = GS_UNKNOWN;
	private Komi m_komi;
	private ScoringMethod m_rules;
	private String m_handicap = "0";
	private int handicapToSet = 0;
	private String mFileName = "recovery";
	private String mGameName = "uninitialized";
	private String move_control = PrefsDGS.ZOOM7X7;
	private boolean boardCoord = false;
	private int numPrev = 0;
	private String boardCoordTxt = PrefsDGS.NO_COORD;
	private String defaultDir = PrefsDGS.DEFAULT_DIR;
	private String defaultEditMode = GameBoardOptions.BROWSE;
	private String timeLeft = "";
	private String [] score_data = null;
	private String marked_state = "";
	//private String tst_marked_state = ""; //debug
	private String game_mode = GameBoardOptions.BROWSE;
	private boolean grinder = false;   // came from grinder go back when done
	private boolean autoPlayRunning = false;
	private boolean autoSkipRunning = false;
	private boolean makingMove = false;
	private boolean resign_game = false;
	private long autoPlayInterval = GameBoardOptions.DEFAUTOPLAYINTERVAL;
	private boolean autoPlayPause = true;
	private boolean autoPlaySound = false;
	private String version_text = "";
	private int controlType = ZOOM_CONTROL;
	private boolean inClient;
	int movesMade = 0;  // accessed from PlayDGS to control comments
	private boolean turnComplete = false;
    private Node clientBaseNode = null;
	private String bgGraphic = PrefsDGS.BG_WOOD;
	private int customBGvalue = MainDGS.BOARD_COLOR;
	private String boardStone = PrefsDGS.STONE_CLAM;
	private BoardUpdate bUpdate;
	private ErrorHistory errHist = ErrorHistory.getInstance();
	
	public BoardManager(
			ContextThemeWrapper ctx,
			BoardUpdate bu,
			FrameLayout bSwitcher,
			String bg,
			int bgCustom,
			String bStone,
			String sgf, 
			String game_action,
			String game_status,
			String handicap,
			String tleft,
			String fil,
			String dfltDir,
			String mcontrol,
			String bcoord,
			String deditmode,
			String gmode,
			String ver,
			int nPrev,
			int dwidth,
			int dlength,
			float dscale,
			float scaleLines,
			long apinterval,
			boolean appause,
			boolean apsound,
			boolean fromClient,
			String [] scr_data
			) {

		ctw = ctx;
		bUpdate = bu;
		boardSwitcher = bSwitcher;
		bgGraphic = bg;
		customBGvalue = bgCustom;
		boardStone = bStone;
		mSGF = sgf;
		m_game_action = game_action;
		m_game_status = game_status;
		m_handicap = handicap;
		mFileName = fil;
		move_control = mcontrol;
		boardCoordTxt = bcoord;
		defaultDir = dfltDir;
		defaultEditMode = deditmode;
		timeLeft = tleft;
		game_mode = gmode;
		version_text = ver;
		numPrev = nPrev;
		display_scale = dscale;
		scale_lines = scaleLines;
		display_width = dwidth; // (int) (dwidth*display_scale + 0.5f);
		display_length = dlength;
		autoPlayInterval = apinterval;
		autoPlayPause = appause;
		autoPlaySound = apsound;
		inClient = fromClient;
		score_data = scr_data;

		if (score_data == null) {
			score_data = new String [PlayDGS.SCORE_DATA_SIZE];
			score_data[PlayDGS.DAME_POINTS] = ""; 
			score_data[PlayDGS.NEUTRAL_POINTS] = "";
			score_data[PlayDGS.WHITE_STONES] = "";
			score_data[PlayDGS.BLACK_STONES] = "";
			score_data[PlayDGS.WHITE_DEAD] = "";
			score_data[PlayDGS.BLACK_DEAD] = "";
			score_data[PlayDGS.WHITE_TERRITORY] = "";
			score_data[PlayDGS.BLACK_TERRITORY] = "";
		}
		if (m_game_action == null) {
			m_game_action = GA_UNKNOWN;
		}
		if (m_game_status == null) {
			m_game_status = GS_UNKNOWN;
		}
		if (m_handicap == null) {
			m_handicap = "0";
		}
		if (timeLeft == null) {
			timeLeft = "";
		}
		if (game_mode == null) {
			game_mode = defaultEditMode;
		} else
		if (game_mode.contentEquals(GameBoardOptions.AUTOPLAY)) {
			grinder = true;
		} else
		if (game_mode.contentEquals(GameBoardOptions.GUESSMOVE)) {
			grinder = true;
		}
		if (move_control == null) {
			move_control = PrefsDGS.ZOOM7X7;
		}
		if (move_control.contentEquals(PrefsDGS.DPADCONTROL)) {
			controlType = D_PAD_CONTROL;
		} else if (move_control.contentEquals(PrefsDGS.ONETOUCH)) {
			controlType = ONE_TOUCH_CONTROL;
		} else if (move_control.contentEquals(PrefsDGS.SLIDE)) {
			controlType = SLIDE_CONTROL;
		} else {
			controlType = ZOOM_CONTROL;
		}
		
		if (boardCoordTxt == null) {
			boardCoord = false;
		} else if (inClient) {
				boardCoord = (boardCoordTxt.contentEquals(PrefsDGS.ALL_COORD) || boardCoordTxt.contentEquals(PrefsDGS.PLAY_COORD));
			} else {
				boardCoord = (boardCoordTxt.contentEquals(PrefsDGS.ALL_COORD) || boardCoordTxt.contentEquals(PrefsDGS.EDIT_COORD));
			}

		boardState = FULL_BOARD;
		movesMade = 0;
		if (mSGF.contentEquals("")){
			if (!recoverFile(mFileName)) return;
		} else {
			if (!sgfToBoard(mSGF)) return;
		}
		if (fullGridBrdSize < 5) return;
		if (game_mode.contentEquals(GameBoardOptions.AUTOPLAY)) {
			go_first_node();
			if (!autoPlayPause) {
				autoPlayRunning = true;
				startAutoPlayTimer();
			}
			redisplayFullBoard();
		} else if (game_mode.contentEquals(GameBoardOptions.GUESSMOVE)) {
			go_first_node();
			redisplayFullBoard();
		} // otherwise a game mode that starts at the end
	}

    // called from GameBoard
	public void saveChanges(){
		if (autoPlayRunning) {
			if (autoPlayTimer != null) autoPlayTimer.cancel();
			autoPlayTimer = new Timer();
			autoPlayRunning = false;
		}
		if (autoSkipRunning) {
			if (autoSkipTimer != null) autoSkipTimer.cancel();
			autoSkipTimer = new Timer();
			autoSkipRunning = false;
		}
		if (unSavedChanges && !inClient) {
			saveRecovery();
		}
	}

    // called from GameBoard
	public void go_back_n_moves(int n) {
		if (boardState == FULL_BOARD) {
			if (autoSkipRunning) {
				if (autoSkipTimer != null) autoSkipTimer.cancel();
				autoSkipTimer = new Timer();
				autoSkipRunning = false;
			}
			if (currNode == null) return;
			if (currNode != rootNode) {
				if (makingMove) return;
				makingMove = true;
				if (n<1) {
					gotoGameNode(rootNode);
				} else {
					for (int i=0; i<n; i++) {
						if (currNode.hasFather()) {
							currNode = currNode.getFather();
						} else {
							break;
						}
					}
					gotoGameNode(currNode);
				}
				makingMove = false;
			}
		}
	}

    // called from GameBoard and local
	public void go_first_node() {
		if (boardState == FULL_BOARD) {
			if (autoSkipRunning) {
				if (autoSkipTimer != null) autoSkipTimer.cancel();
				autoSkipTimer = new Timer();
				autoSkipRunning = false;
			}
			if (currNode == null) return;
			if (currNode != rootNode) {
				if (makingMove) return;
				makingMove = true;
				gotoGameNode(rootNode);
				makingMove = false;
			}
		}
	}

    // called from local
	public void go_prev_node() {
		if (boardState == FULL_BOARD) {
			if (currNode == null) return;
			if (currNode != rootNode) {
				if (makingMove) return;
				makingMove = true;
				gotoGameNode(currNode.getFather());
				makingMove = false;
			} else {
				if (autoSkipRunning) {
					if (autoSkipTimer != null) autoSkipTimer.cancel();
					autoSkipTimer = new Timer();
					autoSkipRunning = false;
				}
			}
		}
	}

    // called from GameBoard
	public void start_skip_prev_node() {
		if (boardState == FULL_BOARD) {
			if (currNode == null) return;
			if (currNode != rootNode) {
				if (!autoSkipRunning) startAutoSkipTimerBackward();
				if (makingMove) return;
				makingMove = true;
				gotoGameNode(currNode.getFather());
				makingMove = false;
			}
		}
	}

    // called from GameBoard
	public void stop_skip_prev_node() {
		if (autoSkipRunning) {
			if (autoSkipTimer != null) autoSkipTimer.cancel();
			autoSkipTimer = new Timer();
			autoSkipRunning = false;
		}
	}

    // not called
	public void go_prev_variation() {  // back up to previous variation branch
		if (boardState == FULL_BOARD) {
			if (currNode == null) return;
			if (currNode != rootNode) {
				if (makingMove) return;
				makingMove = true;
				Node c = currNode;
				Node f = currNode.getFather();
				int n = f.getNumberChildren();
				while ((n == 1) && (f != rootNode)) {
					c = f;
					f = f.getFather();
					n = f.getNumberChildren();						
				}
				if (c == currNode) {
					gotoGameNode(f);
				} else {
					gotoGameNode(c);  // go to the first one of the branch we came up
				}
				makingMove = false;
			}
		}
	}

    // called from GameBoard
    //down
	public void go_next_neighbor() {
		if (boardState == FULL_BOARD) {
			if (currNode == null) return;
			if (currNode.hasFather()) {
				if (makingMove) return;
				makingMove = true;
				Node f = currNode.getFather();
				int n = f.getNumberChildren();
				if (n > 1) {
					int c = f.getChildIndex(currNode);
					if (c < n-1) {
						gotoGameNode(f.getChild(c+1));
					}
				}
				makingMove = false;		
			}
		}
	}

    // called from GameBoard
    //up
	public void go_prev_neighbor() {
		if (boardState == FULL_BOARD) {
			if (currNode == null) return;
			if (currNode.hasFather()) {
				if (makingMove) return;
				makingMove = true;
				Node f = currNode.getFather();
				int n = f.getNumberChildren();
				if (n > 1) {
					int c = f.getChildIndex(currNode);
					if (c > 0) {
						gotoGameNode(f.getChild(c-1));
					}
				}
				makingMove = false;
			}
		}
	}

    // called from local
	public void go_next_node() {
		if (boardState == FULL_BOARD) {
			if (currNode == null) return;
			if (currNode.hasChildren()) {
				if (autoPlayRunning && autoPlaySound) {
					makeSound();
				}
				if (makingMove) return;
				makingMove = true;
				gotoGameNode(currNode.getChild());
				makingMove = false;
			} else {
				if (autoSkipRunning) {
					if (autoSkipTimer != null) autoSkipTimer.cancel();
					autoSkipTimer = new Timer();
					autoSkipRunning = false;
				}
				if (autoPlayRunning) {
					if (autoPlayTimer != null) autoPlayTimer.cancel();
					autoPlayTimer = new Timer();
					autoPlayRunning = false;
					if (grinder) {
						bUpdate.finishGrinding(getButtonState());
					} else {
						redisplayFullBoard();
					}
				}
			}
		}
	}

    // called from GameBoard
	public void start_skip_next_node() {
		if (boardState == FULL_BOARD) {
			if (currNode == null) return;
			if (currNode.hasChildren()) {
				if (autoPlayRunning && autoPlaySound) {
					makeSound();
				}
				if (!autoSkipRunning) startAutoSkipTimerForward();
				if (makingMove) return;
				makingMove = true;
				gotoGameNode(currNode.getChild());
				makingMove = false;
			} else if (autoPlayRunning) {
				if (autoPlayTimer != null) autoPlayTimer.cancel();
				autoPlayTimer = new Timer();
				autoPlayRunning = false;
				if (grinder) {
					bUpdate.finishGrinding(getButtonState());
				} else {
					redisplayFullBoard();
				}
			}
		}
	}

    // called from GameBoard
	public void stop_skip_next_node() {
		if (autoSkipRunning) {
			if (autoSkipTimer != null) autoSkipTimer.cancel();
			autoSkipTimer = new Timer();
			autoSkipRunning = false;
		}
	}

    // not called
	public void go_end_variation() {  // go to end of variation
		if (boardState == FULL_BOARD) {
			if (currNode == null) return;
			if (currNode.hasChildren()) {
				if (autoPlayRunning && autoPlaySound) {
					makeSound();
				}
				if (makingMove) return;
				makingMove = true;
				Node node = currNode;
				while (node.hasChildren()) {
					node = node.getChild();
				}
				gotoGameNode(node);
				makingMove = false;
			} else if (autoPlayRunning) {
				if (autoPlayTimer != null) autoPlayTimer.cancel();
				autoPlayTimer = new Timer();
				autoPlayRunning = false;
				if (grinder) {
					bUpdate.finishGrinding(getButtonState());
				} else {
					redisplayFullBoard();
				}
			}
		}
	}

    // not called
	public void go_next_variation() {  // go to end of variation
		if (boardState == FULL_BOARD) {
			if (currNode == null) return;
			if (currNode.hasChildren()) {
				if (autoPlayRunning && autoPlaySound) {
					makeSound();
				}
				if (makingMove) return;
				makingMove = true;
				Node node = currNode;
				while (node.getNumberChildren() == 1) {
					node = node.getChild();
				}
				if (node.getNumberChildren() > 1) {  // go to the first one of the branches
					node = node.getChild();
				}
				gotoGameNode(node);
				makingMove = false;
			} else if (autoPlayRunning) {
				if (autoPlayTimer != null) autoPlayTimer.cancel();
				autoPlayTimer = new Timer();
				autoPlayRunning = false;
				if (grinder) {
					bUpdate.finishGrinding(getButtonState());
				} else {
					redisplayFullBoard();
				}
			}
		}
	}

    // called from GameBoard
	public void go_forward_n_moves(int n) {
		if (boardState == FULL_BOARD) {
			if (autoSkipRunning) {
				if (autoSkipTimer != null) autoSkipTimer.cancel();
				autoSkipTimer = new Timer();
				autoSkipRunning = false;
			}
			if (currNode == null) return;
			if (currNode != lastNode) {
				if (makingMove) return;
				makingMove = true;
				if (n<1) {
					gotoGameNode(lastNode);
				} else {
					for (int i=0; i<n; i++) {
						if (currNode.hasChildren()) {
							currNode = currNode.getChild();
						} else {
							break;
						}
					}
					gotoGameNode(currNode);
				}
				makingMove = false;
			}
		}
	}

    // called from GameBoard
	public void go_last_node() {
		switch (boardState) {
		case FULL_BOARD: 
			if (autoSkipRunning) {
				if (autoSkipTimer != null) autoSkipTimer.cancel();
				autoSkipTimer = new Timer();
				autoSkipRunning = false;
			}
			if (game_mode.contentEquals(GameBoardOptions.AUTOPLAY)) {
				if (autoPlayRunning) { 
					if (autoPlayTimer != null) autoPlayTimer.cancel();
					autoPlayTimer = new Timer();
					autoPlayRunning = false;
					redisplayFullBoard();
				} else {
					autoPlayRunning = true;
					startAutoPlayTimer();
					redisplayFullBoard();
				}
			} else if (game_mode.contentEquals(GameBoardOptions.GUESSMOVE)) {
				if (grinder) {
					bUpdate.finishGrinding(getButtonState());
				} else if (currNode != lastNode) {
					if (makingMove) return;
					makingMove = true;
					gotoGameNode(lastNode);
					makingMove = false;
				}
			} else {
				if (currNode == null) return;
				if (currNode != lastNode) {
					if (makingMove) return;
					makingMove = true;
					gotoGameNode(lastNode);
					makingMove = false;
				}
			}
			break;
		case ZOOM_BOARD: // Unzoom
			// display the large board
			boardState = FULL_BOARD;
            if (inClient && movesMade > 0) {
                boardState = MADE_MOVE;
            }
			redisplayFullBoard();
			break;
		default:
		}
	}

    // not called
	public boolean isHandicapSet(Game game) {
		ConstNode node = game.getGameInfoNode();
		if (node != null) {
			ConstGameInfo info = game.getGameInfo(node);
	 		if (info != null) {
		 		int h = info.getHandicap();
		 		if (h > 0) {
		 			ConstPointList pl = node.getSetup(GoColor.BLACK);
					return !pl.isEmpty();
				}
	 		}
		}
 		return true;
	}

    // called from local
	public GoColor playerColorToMove() {
		return currNode.getToMove();
	}

    // not called
	public String getMoveInfo() {
		Node node = currNode;
		if (node.hasFather()) {
			String coord = getMoveCoord(!inClient);
			if (coord.contentEquals("")) {
				return getMoveColor(!inClient) + " Pass ";
			} else {
				return getMoveColor(!inClient) + "[" + coord + "] ";
			}
		} else {
			ConstGameInfo info = node.getGameInfoConst();
			if (info != null) {
				int h = info.getHandicap();
				if (h > 1) {
					return "H "+h+"   ";
				}
			}
		}
			return "      ";
	}
    // called from local and PlayDGS
	public String getMoveColor(boolean notInClient) {
		Node node;
		if (notInClient) {
			node = currNode;
		} else {
			if (clientBaseNode.hasChildren()) {
				node = clientBaseNode.getChild();
			} else {
				return "";
			}
		}
		Move m = node.getMove();
		if (m == null) return "B";
		GoColor c = m.getColor();
		if (c == GoColor.WHITE) {
			return "W";
		} else {
			return "B";
		} 
	}

    // called from local and PlayDGS
	public String getMoveCoord(boolean notInClient) {
		Node node;
		if (notInClient) {
			node = currNode;
		} else {
			if (clientBaseNode.hasChildren()) {
				node = clientBaseNode.getChild();
			} else {
				return "";
			}
		}
		Move move;
		GoPoint p;
		move = node.getMove();
		if (move != null) {
			p = move.getPoint();
			if (p == null) {
				return "";
			} else {
				return getCoord(p);
			} 
		} else
			return "";
	}

    // called from local and PlayDGS
    public String getCurrentPredictedMoves(String moveId) {
        int moveNo = Integer.parseInt(moveId);
        Node node;
        StringBuilder retMoves = new StringBuilder();
		StringBuilder predictedMove = new StringBuilder();
		StringBuilder responseMove = new StringBuilder();
        node = clientBaseNode;
        if (node.hasChildren()) {
            node = node.getChild();   // skip the move we are sending and store the rest
        }
        while (node.hasChildren()) {  // need to do in pairs and inc moveId +2 after each pair
			predictedMove.setLength(0);
			responseMove.setLength(0);
            moveNo+=2;  // we must look for the next move and then send the same move number
            node = node.getChild();
            Move move;
            GoPoint p;
            move = node.getMove();
            if (move != null) {
                p = move.getPoint();
                if (p == null) {
                    return retMoves.toString();
                } else {
                    if (retMoves.length()>0) {
						predictedMove.append(";");
                    }
                    //  cMovId+","+cClr+","+cMov+";" ...
					predictedMove.append(moveNo).append(",").append(move.getColor().getUppercaseLetter()).append(",").append(getCoord(p)); // looking for
                }
            } else
                return retMoves.toString();
            if (node.hasChildren()) {
                node = node.getChild();
                move = node.getMove();
                if (move != null) {
                    p = move.getPoint();
                    if (p == null) {
                        return retMoves.toString();
                    } else {
						responseMove.append(";");
                        //  cMovId+","+cClr+","+cMov+";" ...
						responseMove.append(moveNo).append(",").append(move.getColor().getUppercaseLetter()).append(",").append(getCoord(p));  // sent this
                    }
                } else
                    return retMoves.toString();
            }
            retMoves.append(predictedMove.toString()).append(responseMove.toString());
        }
        return retMoves.toString();
    }

    // called from GameBoard
	public void updateNumPrev(int n) {
		if (n != numPrev) {
			numPrev = n;
			if (boardState == FULL_BOARD)
				gotoGameNode(currNode);
		}
	}

    // called from GameBoard and PlayDGS
	public void unzoom() {
		boardState = FULL_BOARD;
        if (inClient && movesMade > 0) {
            boardState = MADE_MOVE;
        }
		redisplayFullBoard();
	}

    // called from PlayDGS
	public void unDoMove() {
		if (m_game_action.contentEquals(GA_SET_HA)) {
			if (makingMove) return;
			makingMove = true;
			turnComplete = false;
			// remove all addBlack
			ConstPointList pl;
			Iterator<GoPoint> i;
			GoPoint p;
			pl = currNode.getSetup(BLACK);
			if (pl != null) {
				i = pl.iterator();
				while (i.hasNext()) {
					p = i.next();
					currNode.removeSetup(p);
				}
			}
			gotoGameNode(currNode);
			makingMove = false;
			return;
		}
		if (currNode.hasFather()) {
			if (makingMove) return;
			if (resign_game) return;
			makingMove = true;
            turnComplete = false;
            while ((movesMade > 0) && currNode.hasFather()) {
                Node f = currNode.getFather();
                f.removeChild(currNode);
                currNode = f;
                unSavedChanges = true;
                movesMade--;
            }
            lastNode = currNode;
            gotoGameNode(currNode);
			makingMove = false;
		}
	}

    // called from GameBoard and PlayDGS
	public void doPass() {
		goguiX = -1;  
		goguiY = -1;
		final Handler handler = new Handler();
		handler.post(new Runnable() { 
			public void run() {	
				if (game_mode.contains(GameBoardOptions.EDIT)) {
					if (m_game_action.contentEquals(GA_SET_HA) && handicapToSet > 0) {
						return;
					} else {
						playEditNode();
					}
				} else if (game_mode.contains(GameBoardOptions.GUESSMOVE)) {
					guessNode();
				} else if (game_mode.contains(GameBoardOptions.MARKUP)) {
					markNode();
				} else {
					browseNode();
				} 
			}
		});
	}

    // called from PlayDGS
	public void unDoScoreAccepted() {
		final Handler handler = new Handler();
		handler.post(new Runnable() { 
			public void run() {	
				if (makingMove) return;
				makingMove = true;
				turnComplete = false;
				unSavedChanges = true;
    			if (boardState != CANNOT_MOVE) {
    				boardState = FULL_BOARD;
    			}
				bUpdate.displayMoveInfo(ctw.getString(R.string.MarkScore));
    			bUpdate.displayGameInfo(mGameName);
    			redisplayFullBoard();
				makingMove = false;
			}
		});
	}

    // called from PlayDGS
	public void doScoreAccepted() {  
		goguiX = -1;  
		goguiY = -1;
		final Handler handler = new Handler();
		handler.post(new Runnable() { 
			public void run() {	
				if (makingMove) return;
				makingMove = true;
    			unSavedChanges = true;
    			if (boardState != CANNOT_MOVE) {
    				if (inClient) {
    					boardState = MADE_MOVE;
                        turnComplete = true;
    				} else {
    					boardState = FULL_BOARD;
    				}
    			}
    			bUpdate.displayMoveInfo(ctw.getString(R.string.AcceptScore));
    			bUpdate.displayGameInfo(mGameName);
    			redisplayFullBoard();
				makingMove = false;
			}
		});
	}

    // called from PlayDGS
	public void unDoDelete() {  // undo delete a game
		final Handler handler = new Handler();
		handler.post(new Runnable() { 
			public void run() {	
				if (makingMove) return;
				makingMove = true;
				turnComplete = false;
				gotoGameNode(currNode);
				makingMove = false;
			}
		});
	}

    // called from PlayDGS
	public void doDelete() {  // delete a game should only be used by client
		goguiX = -1;  
		goguiY = -1;
		final Handler handler = new Handler();
		handler.post(new Runnable() { 
			public void run() {	
				if (makingMove) return;
				makingMove = true;
    			unSavedChanges = true;
    			if (boardState != CANNOT_MOVE) {
    				if (inClient) {
    					boardState = MADE_MOVE;
                        turnComplete = true;
    				} else {
    					boardState = FULL_BOARD;
    				}
    			}
    			bUpdate.displayMoveInfo(ctw.getString(R.string.Delete));
    			bUpdate.displayGameInfo(mGameName);
    			redisplayFullBoard();
				makingMove = false;
			}
		});
	}

    // called from GameBoard and PlayDGS
	public void doResign() {
		goguiX = -1;  
		goguiY = -1;
		if (game_mode.contains(GameBoardOptions.EDIT)) {
			if (m_game_action.contentEquals(GA_SET_HA) && handicapToSet > 0) {
				return;
			} 
			final Handler handler = new Handler();
			handler.post(new Runnable() { 
				public void run() {	
					Node node = (Node) gogui_game.getGameInfoNode();
					if (node == null) {
						return;
					}
					if (makingMove) return;
					makingMove = true;
					ConstGameInfo game_info = gogui_game.getGameInfo(node);
			 		String game_result = game_info.get(StringInfo.RESULT);
					if (game_result == null) game_result = "";
					if (game_result.contentEquals("")) {
						resign_game = true;
						if (playerColorToMove == GoColor.WHITE) {
							game_result = "B+R";
						} else if (playerColorToMove == GoColor.BLACK) {
							game_result = "W+R";
						} else {
							game_result = "?+R";
						} 
						gogui_game.setResult(game_result);
		    			unSavedChanges = true;
		    			if (boardState != CANNOT_MOVE) {
		    				if (inClient) {
		    					boardState = MADE_MOVE;
                                turnComplete = true;
		    				} else {
		    					boardState = FULL_BOARD;
		    				}
		    			}
		    			bUpdate.displayMoveInfo(game_result+" ");
		    			bUpdate.displayGameInfo(mGameName);
		    			redisplayFullBoard();
						makingMove = false;
					} else {
				        new AlertDialog.Builder(ctw)
				        .setTitle(R.string.undoButton)
				        .setMessage(R.string.Resign)
				        .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				        	resign_game = false;
							gogui_game.setResult(null);
							turnComplete = false;
							unSavedChanges = true;
							gotoGameNode(currNode);
							makingMove = false; }})
				        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int whichButton) {
				        	makingMove = false; }})
				        .show();		
					}
				}
			});
		}
	}

    // called from GameBoard and local
	public int getButtonState() {
		int value = 0;
		if (currNode == lastNode) {
			value |= LAST_NODE;
		}
		if (currNode.hasFather()) {
			value |= PARENT_NODE;
		} 
		if (currNode.hasChildren()) {
			value |= CHILD_NODE;
		}
		if (currNode.hasFather()) {
			Node f = currNode.getFather();
			int n = f.getNumberChildren();
			if (n > 1) {
				int c = f.getChildIndex(currNode);
				if (c > 0) {
					value |= PREV_NEIGHBOR;
				}
				if (c < n-1) {
					value |= NEXT_NEIGHBOR;
				}
			}
		}
		if (autoPlayRunning) {
			value |= AUTOPLAY_RUNNING;
		}
		return value;
	}

    // called from local and PlayDGS
	public String get_comment() {
		String c;
        if (inClient && movesMade > 0) {
            if (clientBaseNode == null) {
                return "";
            }
            Node node = clientBaseNode;
            if (node.hasChildren()) {
                node = node.getChild();   // get from move palyed
                c = node.getComment();
            } else {
                return "";
            }
        } else {
            c = currNode.getComment();
        }
		if (c == null) c = "";
		return c;
	}

    // called from PlayDGS
	public String get_handicap() {
		return getHandicapStones();
	}

    // called from GameBoard and PlayDGS
	public String getGameInfo() {
		String gi;
		 try {
			 gi = getGameInfo(ctw, gogui_game, gogui_board, timeLeft);
		 } catch (Exception e) {
             errHist.writeErrorHistory("BoardManager Exception:" + e.toString());
			 gi = ctw.getString(R.string.BadGameInfo);
		 }
		 return gi;
	}
	
	private void setUpScoringNode(PointList initialMarkedPoints) {
		//Move move = Move.get(GoColor.EMPTY, null);
		Node newNode = new Node();
		if (currNode == lastNode) {
			lastNode = newNode;
		}
		currNode.append(newNode);
		try {
			gogui_game.gotoNode(newNode);
		} catch (Exception e) {  // got lost, reset to the end
			newNode = rootNode;  
			while (newNode.hasChildren()) {
				newNode = newNode.getChild();
			}
			lastNode = newNode;
			gogui_game.gotoNode(newNode);
		}
		currNode = newNode;
		gogui_board = (Board) gogui_game.getBoard();
		scoreCounter.begin(gogui_board, initialMarkedPoints);
		bUpdate.displayMoveInfo(ctw.getString(R.string.MarkScore));
	}

    // called from GameBoard and PlayDGS
	public void changeGameState(String ga, String gs) {
		m_game_action = ga;
		m_game_status = gs;
		if (ga.contains(GA_SCORING)) {
			PointList deadStones = new PointList();
			deadStones.clear();
			setUpScoringNode(deadStones);
			computeDisplayScore (false);
		} else {
			gogui_board = (Board) gogui_game.getBoard();
			mGameName = getGameName(gogui_game, rootNode);
			bUpdate.displayGameInfo(mGameName);
			if (inClient) {
				if (lastNode.hasFather()) {
					currNode = lastNode.getFather();
					currNode.removeChild(lastNode);
					lastNode = currNode;
					gotoGameNode(lastNode);
				}
			} else {
				computeDisplayScore (true);
			}
		}
	}

    // called from GameBoard and PlayDGS and local
	public void set_comment(String s) {
		unSavedChanges = true;
	    currNode.setComment(s);
	}

    // called from GameBoard
	public void set_mode(String gameMode, long apInterval, boolean apPause, boolean apSound) {
		if (!gameMode.contentEquals(game_mode)) {
			if (!(gameMode.contentEquals(GameBoardOptions.AUTOPLAY) && game_mode.contentEquals(GameBoardOptions.AUTOPLAY)) && !gameMode.contentEquals(game_mode)) {
				unSavedChanges = true;
			}
			game_mode = gameMode;
			if ((boardState != FULL_BOARD) && (boardState != MADE_MOVE)) {  // we are zoomed in
				boardState = ZOOM_BOARD;
			}
		}
		autoPlayInterval = apInterval;
		autoPlayPause = apPause;
		autoPlaySound = apSound;
		if (game_mode.contentEquals(GameBoardOptions.AUTOPLAY) && !autoPlayRunning) {
			if (!autoPlayPause) {
				autoPlayRunning = true;
				startAutoPlayTimer();
			}
			boardState = FULL_BOARD;
			redisplayFullBoard();
		} else if (autoPlayRunning) {
			if (autoPlayTimer != null) autoPlayTimer.cancel();
			autoPlayTimer = new Timer();
			autoPlayRunning = false;
			boardState = FULL_BOARD;
			redisplayFullBoard();
		}
	}

    // called from GameBoard and local
	public void set_markup(int markIndicator, int addIndicator, String p_lable) {
		if (makingMove) return;
		makingMove = true;
		if (p_lable == null) p_lable = " ";
		GoPoint p = GoPoint.get(goguiX, goguiY);
		GoColor pColor = gogui_board.getColor(p);
		setNodeMark(currNode, p, markIndicator, pColor);
		setNodeAddStone(currNode, p, addIndicator, pColor);
		currNode.setLabel(p, p_lable);
		unSavedChanges = true; 
		gotoGameNode(currNode);
		makingMove = false;
	}

	private void startAutoPlayTimer() {
		final long c_UPDATE_INTERVAL = autoPlayInterval*1000;
		  autoPlayTimer.schedule(
		      new TimerTask() {
		        @Override
				public void run() {
		        	mHandler.moveForward(); 
		        }
		      },
		      c_UPDATE_INTERVAL,
		      c_UPDATE_INTERVAL);
		}

	private void startAutoSkipTimerForward() {
		final long c_UPDATE_INTERVAL = autoPlayInterval*1000;
		final long c_UPDATE_INTERVAL_S = c_UPDATE_INTERVAL+500;
		autoSkipRunning = true;
		  autoSkipTimer.schedule(
		      new TimerTask() {
		        @Override
				public void run() {
		        	mHandler.moveForward(); 
		        }
		      },
		      c_UPDATE_INTERVAL_S,
		      c_UPDATE_INTERVAL);
		}
	
	private void startAutoSkipTimerBackward() {
		final long c_UPDATE_INTERVAL = autoPlayInterval*1000;
		final long c_UPDATE_INTERVAL_S = c_UPDATE_INTERVAL+500;
		autoSkipRunning = true;
		  autoSkipTimer.schedule(
		      new TimerTask() {
		        @Override
				public void run() {
		        	mHandler.moveBackward(); 
		        }
		      },
		      c_UPDATE_INTERVAL_S,
		      c_UPDATE_INTERVAL);
		}
	
	
	private boolean recoverFile(String fName) {
		StringBuilder sb = new StringBuilder(50);
		String s;
        File f = new File(fName);
		try {
            InputStream in = new FileInputStream(f);
            if (fName.endsWith("recovery")) {
                int i;
                while (true) {
					try {
						i = in.read();
					} catch (IOException e) {
                        errHist.writeErrorHistory("BoardManager IOException:" + e.toString());
						Toast.makeText(ctw, "Read "+fName+" failed "+e, Toast.LENGTH_LONG).show();
						try {
							in.close();
						} catch (IOException ignored) {	}
						return false;
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
				} else {  // pre 1.17 recovery file
					mFileName = s;
				}
			}
		
			SgfReader reader;
			try {
				reader = new SgfReader(in, null, null, 0);
			} catch (SgfError e) {
                errHist.writeErrorHistory("BoardManager SgfError:" + e.toString());
				Toast.makeText(ctw, "SgfError:" + e.getMessage(), Toast.LENGTH_LONG).show();
				return false;
			}
			return setUpTree(reader);
		} catch (FileNotFoundException e1) {
            errHist.writeErrorHistory("BoardManager FileNotFoundException:" + e1.toString());
			Toast.makeText(ctw, fName + ctw.getString(R.string.NotFound) + e1, Toast.LENGTH_LONG).show();
		}
		return false;
	}
	
	private void saveRecovery() {
		if (grinder) return;
		unSavedChanges = false;
		try {
			String fName = commonFileStuff.getFullFileName(commonFileStuff.ANDGS_DIR, commonFileStuff.RECOVERYFILE);
			File recovery = new File(fName);
			File dir = recovery.getParentFile();
			if (!dir.isDirectory()) commonFileStuff.makeDirectory (dir);
			OutputStream out = new FileOutputStream(recovery);
			try {
				String s = mFileName + ":" + game_mode + '\n';
				byte[] ba = new byte[0];
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
					ba = s.getBytes(StandardCharsets.UTF_8);
				} else {
					ba = s.getBytes(Charset.forName("UTF-8"));
				}
				out.write(ba);
			} catch (IOException e) {
                errHist.writeErrorHistory("BoardManager IOException:" + e.toString());
				Toast.makeText(ctw, ctw.getString(R.string.SaveRecoveryFailed) + e, Toast.LENGTH_LONG).show();
				try {
					out.close();
				} catch (IOException ignored) {  }
				return;
			}
			new SgfWriter(out, gogui_tree, "anDGS", version_text);
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
                errHist.writeErrorHistory("BoardManager IOException:" + e.toString());
				Toast.makeText(ctw, ctw.getString(R.string.CloseFailed)+e, Toast.LENGTH_LONG).show();
			}
		} catch (FileNotFoundException e) {
            errHist.writeErrorHistory("BoardManager FileNotFoundException:" + e.toString());
			Toast.makeText(ctw, "Save recovery failed "+e, Toast.LENGTH_LONG).show();
			return;
		}
	}

    // called from GameBoard and local
	public void saveTheGame(String filName) {
		if (filName.contentEquals("")) {
			filName = getGameFileName();
		}
		mFileName = filName;
		File f = new File(mFileName);
		File dir = f.getParentFile();
		if (!dir.isDirectory()) commonFileStuff.makeDirectory (dir);
		if (!dir.isDirectory()) {  // failed to create directory try to change
           final EditText inputd = new EditText(ctw);
           inputd.setText(mFileName);
           new AlertDialog.Builder(ctw)
            .setTitle(R.string.MakeDirectoryFailed)
            .setMessage(R.string.EnterFile)
            .setView(inputd)
            .setPositiveButton(R.string.Save, new DialogInterface.OnClickListener(){
                 public void onClick(DialogInterface arg0, int arg1) {
              	   saveTheGame(inputd.getText().toString());  }})
            .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener(){
                 public void onClick(DialogInterface arg0, int arg1) {
				 }})
            .show(); 
           return;
		}
		if (f.exists()) {
           final EditText inputf = new EditText(ctw);
           inputf.setText(mFileName);
           new AlertDialog.Builder(ctw)
            .setTitle(R.string.FileExists)
            .setMessage(R.string.SaveChangeCancel)
            .setView(inputf)
            .setPositiveButton(R.string.Save, new DialogInterface.OnClickListener(){
                 public void onClick(DialogInterface arg0, int arg1) {
              	   mFileName = inputf.getText().toString();
              	   File f = new File(mFileName);
              	   saveTheGameNow(f);  }})
            .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener(){
                 public void onClick(DialogInterface arg0, int arg1) {
				 }})
            .show(); 
		} else {
			 saveTheGameNow(f);
		}
	}
		
	private void saveTheGameNow(File f) {
		try {
			OutputStream out = new FileOutputStream(f);
			new SgfWriter(out, gogui_tree, "anDGS", version_text);
			String fName = commonFileStuff.getFullFileName(commonFileStuff.ANDGS_DIR, commonFileStuff.RECOVERYFILE);
			File recovery = new File(fName);
			recovery.delete();
			try {
				out.flush();
				out.close();
				unSavedChanges = false;
			} catch (IOException e) {
                errHist.writeErrorHistory("BoardManager IOException:" + e.toString());
				Toast.makeText(ctw, ctw.getString(R.string.CloseFailed) + e, Toast.LENGTH_LONG).show();
			}
		} catch (FileNotFoundException e) {
            errHist.writeErrorHistory("BoardManager FileNotFoundException:" + e.toString());
			Toast.makeText(ctw, ctw.getString(R.string.SaveSGFFailed) + e, Toast.LENGTH_LONG).show();
			return;
		}
	}

    // called from GameBoard and local
	public String getGameFileName() {
		String filName = "";
		try {
			filName = commonFileStuff.getFullDirName(defaultDir) + File.separator + getGameName(gogui_game,rootNode) + ".sgf";
        } catch (Exception e) {
            errHist.writeErrorHistory("BoardManager Exception:" + e.toString());
			Toast.makeText(ctw, ctw.getString(R.string.SaveSGFFailed) + e, Toast.LENGTH_LONG).show();
		}
		return filName;
	}
	
	public void fullOnFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            v.setBackgroundColor(MainDGS.GREEN_COLOR);
        } else {
        	v.setBackgroundColor(MainDGS.TRANSPARENT_COLOR);
        }
    }
	
	private void fullDoBoardAction () {
		if (fullBoardX < 0) fullBoardX = 0;
		if (fullBoardY < 0) fullBoardY = 0; 
		if (fullBoardX >= fullGridBrdSize) fullBoardX = fullGridBrdSize - 1; 
		if (fullBoardY >= fullGridBrdSize) fullBoardY = fullGridBrdSize - 1;
		if (controlType != ZOOM_CONTROL) { 
			final Handler handler = new Handler();
			handler.post(new Runnable() { 
				public void run() {	
					goguiX = fullBoardX;  
					goguiY = flipSGFy(fullBoardY);
					if (game_mode.contains(GameBoardOptions.EDIT)) {
						if (m_game_action.contentEquals(GA_SET_HA)) {
							setHandicapNode();
						} else if (m_game_action.contentEquals(GA_SCORING)) {
							playScoreNode();
						} else {
							playEditNode();
						}
					} else if (game_mode.contains(GameBoardOptions.GUESSMOVE)) {
						guessNode();
					} else if (game_mode.contains(GameBoardOptions.MARKUP)) {
						markNode();
					} else {
						browseNode();
					} 
				}
			});
		} else {
			final Handler handler = new Handler();
			handler.post(new Runnable() { 
				public void run() {	
					initZoomBoard(fullBoardX, fullBoardY);
					boardState = ZOOM_BOARD;
					redisplayZoomBoard();
				}
			});
		}
	}
	
	public void fullOnClick(View v) {
		if (boardState != FULL_BOARD && boardState != MADE_MOVE)
			return;
		if (inClient) {
			if (turnComplete)
				return;
		}
		if (controlType == SLIDE_CONTROL)   // ignore other clicks from the board 
			return;
	
		fullBoardY = ((View) v.getParent()).getTop() / fullGrphsStnSize;
		fullBoardX = v.getLeft() / fullGrphsStnSize;
		fullDoBoardAction ();
	}
	
	public void zoomOnClick(View v) {
		int x, y;
		y = ((View) v.getParent()).getTop() / zoomGrphsStnSize;
		x = v.getLeft() / zoomGrphsStnSize;
		if (x < 0) x = 0;
		if (y < 0) y = 0; 
		if (x >= zoomGridBrdSize) x = zoomGridBrdSize - 1; 
		if (y >= zoomGridBrdSize) y = zoomGridBrdSize - 1;
		goguiX = getGameZoomX(fullBoardX, x);  
		goguiY = getGameZoomY(fullBoardY, y);

		final Handler handler = new Handler();
		handler.post(new Runnable() { 
			public void run() {	
				if (boardState == ZOOM_BOARD) {
					if (game_mode.contains(GameBoardOptions.EDIT)) {
						if (m_game_action.contentEquals(GA_SET_HA)) {
							setHandicapNode();
						} else if (m_game_action.contentEquals(GA_SCORING)) {
							playScoreNode();
						} else {
							playEditNode();
						}
					} else if (game_mode.contains(GameBoardOptions.GUESSMOVE)) {
						guessNode();
					} else if (game_mode.contains(GameBoardOptions.MARKUP)) {
						markNode();
					} else {
						browseNode();
					}
				} 
			}
		});
	}

	private void clearSlideCursor() {
		slideStonesTable.setLocationGraphic(sldLastCursorStoneX, sldLastCursorStoneY, BLNK);
	}
	
	private int getOffset() {
		int o = (int)(fullGrphsBrdSize*0.18);
		if (o < 1) o = 1;
		return o;   // could tune for each board size: switch (fullGridBrdSize) 
	}
	
	private int cursorOffset() {
		return sldDirection*getOffset();
	}
	
	private void displaySlideCursor(int currentX, int currentY) {
		int sldGraphic;
		if (game_mode.contentEquals(GameBoardOptions.EDIT)) {
			if (currNode.getToMove() == GoColor.WHITE) {
				sldGraphic = WP;
			} else {
				sldGraphic = BP;
			}
		} else {
			sldGraphic = YP;
		}
		sldLastCursorStoneX = currentX;
		if (sldDirection == 0) {// determine direction if not set
			sldDirection = -1; 
		} 
		sldLastCursorStoneY = currentY + cursorOffset();
		slideStonesTable.setLocationGraphic(sldLastCursorStoneX, sldLastCursorStoneY, sldGraphic);
	}
	
	private void initSldVars() {
		sldLastTouchX = -1;
    	sldLastTouchY = -1;
    	sldLastTouchStoneX = -1;
    	sldLastTouchStoneY = -1;
    	sldLastCursorStoneX = -1;
    	sldLastCursorStoneY = -1;
    	sldDirection = -1;
    	sldTimeStamp = 0;
	}
	
	View.OnTouchListener touchFull = new View.OnTouchListener() {  // TODO complete this
		public boolean onTouch(View v, MotionEvent event) {
			int currentX, currentY;
			
		    final int action = event.getAction();
	        switch (action & MotionEvent.ACTION_MASK) {
		    case MotionEvent.ACTION_DOWN: 
		    {
		    	final float eventX = event.getX();
			    final float eventY = event.getY();
			    currentY = (int) (v.getTop() + eventY) / fullGrphsStnSize;
		    	currentX = (int) (v.getLeft() + eventX) / fullGrphsStnSize;
		    	if (currentY > (fullGridBrdSize-getOffset())) {
			    	sldDirection = 1;  // down
		    	} else {
			    	sldDirection = -1; // up
		    	}
		    	displaySlideCursor(currentX, currentY);
		    	sldLastTouchX = eventX;
		    	sldLastTouchY = eventY;
		    	sldTimeStamp = SystemClock.uptimeMillis();
		    	break;
		    }
		    case MotionEvent.ACTION_MOVE: 
		    {  
		    	final float eventX = event.getX();
			    final float eventY = event.getY();
			    int newSldDirection = 0;
		    	// update position of stone  direction from current etc...
		    	// display the correct dim stone on the slideTable
		    	currentY = (int) (v.getTop() + eventY) / fullGrphsStnSize;
		    	currentX = (int) (v.getLeft() + eventX) / fullGrphsStnSize;
		    	long time = SystemClock.uptimeMillis();
		    	if (time - sldTimeStamp < 250) break;
		    	sldTimeStamp = time;
		    	// >.3 seconds or currentX-Y diff sldLastTouchStoneX > 1 else break
		    	if (sldDirection > 0 && currentY < (fullGridBrdSize-3.75*getOffset())) {
		    		newSldDirection = -1;  // up
		    	} else if (sldDirection < 0 && currentY > (fullGridBrdSize-getOffset())) {
		    		newSldDirection = 1;  // down
		    	} else {
		    		newSldDirection = sldDirection;
		    	}
		    	if (currentX !=  sldLastTouchStoneX || currentY != sldLastTouchStoneY || sldDirection != newSldDirection) {
		    		sldDirection = newSldDirection;
		    		clearSlideCursor();
			    	displaySlideCursor(currentX, currentY);
		    	}
		    	sldLastTouchX = eventX;
		    	sldLastTouchY = eventY;
		    	break;
		    }
		    case MotionEvent.ACTION_POINTER_UP:
		    case MotionEvent.ACTION_UP:
		    {
		    	fullBoardX = sldLastCursorStoneX;
		    	fullBoardY = sldLastCursorStoneY;
				clearSlideCursor();
				if (fullBoardX >= 0 && fullBoardX < fullGridBrdSize && fullBoardY >= 0 && fullBoardY < fullGridBrdSize)
					fullDoBoardAction ();
		    	initSldVars();
		    	break;
		    }
		    case MotionEvent.ACTION_CANCEL:
		    {
		    	clearSlideCursor();
		    	initSldVars();
		    	break;
		    }
		    default:
		      break;
		    }
	
		    // Schedules a repaint.
		    //invalidate();
		    return true;
		}
	};
	
	private Move getMoveOffFather() {
		Node father;
    	Move move = currNode.getMove();
    	GoColor colorOfMove = null;
    	if (move != null) {
    		colorOfMove = move.getColor();
    	} else {
	    	if (!currNode.hasFather()) {
	    		colorOfMove = GoColor.BLACK;
	    	} else {
	    		father = currNode.getFather();
	    		move = father.getMove();
	    		if (move == null) {
	    			colorOfMove = GoColor.BLACK;
	    		} else {
	    			colorOfMove = move.getColor().otherColor();
	    		}
	    	}
    	}
    	if (colorOfMove != GoColor.WHITE) {
    		colorOfMove = GoColor.BLACK;
    	}
    	if (!(goguiX == -1 && goguiY == -1)) {
			if (!isValidMove(gogui_board, colorOfMove, goguiX, goguiY)) {
				gogui_game.gotoNode(currNode);
				Toast.makeText(ctw, ctw.getString(R.string.IllegalMove), Toast.LENGTH_LONG).show();
				return null;
			}
    	}
    	if (goguiX == -1 && goguiY == -1) {
			return Move.get(colorOfMove, null);
		} else {
			return Move.get(colorOfMove, goguiX, goguiY);	
		}
	}
	
	private Move getMoveOffNode() {
		Node father;
    	Move move = currNode.getMove();
    	GoColor colorOfMove = null;
    	if (move != null) {
    		colorOfMove = playerColorToMove(); 
    	} else {
	    	if (!currNode.hasFather()) {
	    		colorOfMove = GoColor.BLACK;
	    	} else {
	    		father = currNode.getFather();
	    		move = father.getMove();
	    		if (move == null) {
	    			colorOfMove = GoColor.BLACK;
	    		} else {
	    			colorOfMove = move.getColor().otherColor();
	    		}
	    	}
    	}
    	if (colorOfMove != GoColor.WHITE) {
    		colorOfMove = GoColor.BLACK;
    	}
    	if (!(goguiX == -1 && goguiY == -1)) {
			if (!isValidMove(gogui_board, colorOfMove, goguiX, goguiY)) {
				gogui_game.gotoNode(currNode);
				Toast.makeText(ctw, ctw.getString(R.string.IllegalMove), Toast.LENGTH_LONG).show();
				return null;
			}
    	}
		if (goguiX == -1 && goguiY == -1) {
			return Move.get(colorOfMove, null);
		} else {
			return Move.get(colorOfMove, goguiX, goguiY);
		}
	}
	
	private void askHandlePassPass () {
		final String[] items = {ctw.getString(R.string.Pass),
                ctw.getString(R.string.DeleteCurrentMove),
                ctw.getString(R.string.cancelButton)};

		AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(ctw.getString(R.string.select)); 
		ArrayAdapter<String> sel_adapter = new ArrayAdapter<String>(ctw,
                android.R.layout.simple_list_item_1, items);
        sel_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
				final int i = item;
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
				    	Move move = null;
				    	Node newNode = null;
				    	switch (i) {
				    	case 0: // pass
			    			if (makingMove) break;
			    			makingMove = true;
			    			if (playerColorToMove == GoColor.WHITE) {
			    				move = Move.get(GoColor.WHITE, null);
			    			} else {
			    				move = Move.get(GoColor.BLACK, null);
			    			}
			    			newNode = new Node(move);

			    			if (currNode == lastNode) {
			    				 lastNode = newNode;
			    			}
			    			currNode.append(newNode);
							turnComplete = true;
			    			unSavedChanges = true;
			    			gotoGameNode(newNode);
			    			makingMove = false;
				    		break;
				    	
				    	case 1: // delete current pass
				    		if (currNode.hasFather()) {
								if (makingMove) break;
								makingMove = true;
								Node f = currNode.getFather();
								if (currNode == lastNode) {
									lastNode = f;
								}
								f.removeChild(currNode);
								turnComplete = false;
								unSavedChanges = true;
								gotoGameNode(f);
								makingMove = false;
							}
				    		break;
				    	
				    	default: // cancel
							// do nothing
				    	}
					}
				});
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void doCreateVariation () {
		Move move = getMoveOffNode();
    	Node newNode = null;
		if (move == null) return;
		if (makingMove) return;
		makingMove = true;
		newNode = new Node(move);
		if (currNode == lastNode) {
			lastNode = newNode;
		}
		currNode.append(newNode);
		unSavedChanges = true;
		gotoGameNode(newNode);
		makingMove = false;
	}
	
	private void doReplaceOneMove() {
		Move move = getMoveOffFather();
		if (move == null) return;
		if (makingMove) return;
		makingMove = true;
		currNode.setMove(move);
		unSavedChanges = true;
		gotoGameNode(currNode);
		makingMove = false;
	}
	
	private void doReplaceAllMoves() {
		Node node = null;
		Move move = getMoveOffFather();
		if (move == null) return;
		if (makingMove) return;
		makingMove = true;
		// assume lastNode is always at the end of the main branch
		if (currNode.hasChildren()) {
			node = currNode.getChild();
			while (node.hasChildren()) {
				node = node.getChild();
			}
			if (lastNode == node) {
				lastNode = currNode;
			}
		}
		while (currNode.hasChildren()) {
			currNode.removeChild(currNode.getChild());
		}
		if (currNode == rootNode) {
    		newNode = new Node(move);
    		currNode.append(newNode);
    		currNode = newNode;
		} else {
			currNode.setMove(move);
		}
		unSavedChanges = true;
		gotoGameNode(currNode);
		makingMove = false;
	}
	
	private void doDeleteCurrentMove() {
		Node node = currNode.getFather();
		if (node == null) return;
		if (makingMove) return;
		if (currNode.getNumberChildren() > 1) {
			new AlertDialog.Builder(ctw)
              .setTitle(R.string.Warning)
              .setMessage(R.string.DeleteManyBranches)
              .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener(){
                   public void onClick(DialogInterface arg0, int arg1) {
                	   removeCurrNodeInTree();  }})
              .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener(){
                   public void onClick(DialogInterface arg0, int arg1) {
				   }})
              .show(); 
		} else {
			removeCurrNodeInTree();
		}
	}
	
	private void askMidMove() {
		final String[] items = {ctw.getString(R.string.CreateVariation),
                ctw.getString(R.string.ReplaceOneMove),
                ctw.getString(R.string.ReplaceAllMoves),
                ctw.getString(R.string.DeleteCurrentMove),
                ctw.getString(R.string.InsertBlackMove),
                ctw.getString(R.string.InsertWhiteMove),
                ctw.getString(R.string.cancelButton)};

		AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(ctw.getString(R.string.select)); 
		ArrayAdapter<String> sel_adapter = new ArrayAdapter<String>(ctw,
                android.R.layout.simple_list_item_1, items);
        sel_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
				final int i = item;
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
				    	switch (i) {
				    	case 0: // create variation
				    		doCreateVariation();
				    		break;
				    	case 1: // replace one move
				    		doReplaceOneMove();
				    		break;
				    	case 2: // replace all moves
				    		doReplaceAllMoves();
				    		break;
				    	case 3: // delete current move
				    		doDeleteCurrentMove();
				    		break;
				    	case 4: // insert Black move
				    		if (!(goguiX == -1 && goguiY == -1)) {
				    			insertMove(GoColor.BLACK);
				    		}
				    		break;
				    	case 5: // insert White move
				    		if (!(goguiX == -1 && goguiY == -1)) {
				    			insertMove(GoColor.WHITE);
				    		}
				    		break;
				    	default: // cancel
							// do nothing
				    	}
					}
				});
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void askMidPass() {
		final String[] items = {ctw.getString(R.string.CreateVariation),
                ctw.getString(R.string.ReplaceOneMove),
                ctw.getString(R.string.ReplaceAllMoves),
                ctw.getString(R.string.DeleteCurrentMove),
                ctw.getString(R.string.cancelButton)};

		AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(ctw.getString(R.string.select)); 
		ArrayAdapter<String> sel_adapter = new ArrayAdapter<String>(ctw,
                android.R.layout.simple_list_item_1, items);
        sel_adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        builder.setAdapter(sel_adapter, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
				final int i = item;
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
				    	switch (i) {
				    	case 0: // create variation
				    		doCreateVariation();
				    		break;
				    	case 1: // replace one move
				    		doReplaceOneMove();
				    		break;
				    	case 2: // replace all moves
				    		doReplaceAllMoves();
				    		break;
				    	case 3: // delete current move
				    		doDeleteCurrentMove();
				    		break;
				    	default: // cancel
							// do nothing
				    	}
					}
				});
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void computeDisplayScore (boolean saveScore) {
		scoreCounter.compute();
		Score scor = scoreCounter.getScore(m_komi, m_rules);
		for (GoPoint stone : gogui_board) {
			GoColor c = scoreCounter.getColor(stone, m_rules); 
			GoColor s = gogui_board.getColor(stone);
			if (c == GoColor.EMPTY) {
				setNodeMark(currNode,stone, M_NONE, s);
			} else if (c == GoColor.BLACK) {
				setNodeMark(currNode,stone, M_TERR_B, s);
			} else if (c == GoColor.WHITE) {
				setNodeMark(currNode,stone, M_TERR_W, s);
			}
		}
		boardState = FULL_BOARD;
		String scorString = scor.formatResult();
		if (saveScore) {
			gogui_game.setResult(scorString);
		}
		if (!inClient) {
			set_comment(scorString);
		} else {
			bUpdate.displayGameInfo(scorString);
		}
		treeToGraphics(gogui_board, currNode);
		setFullBoardLabels(currNode, numPrev);
		redisplayFullBoard();
	}
	
	private void playScoreNode() {
		gogui_board = (Board) gogui_game.getBoard();
		GoPoint p = GoPoint.get(goguiX, goguiY);
		scoreCounter.changeStatus(p);  // PointList stones = 
		computeDisplayScore (false);
		if (inClient) {
			bUpdate.setScoringUpdated(true);
		}
		/*
		String df = getScoreDifference();
		String ds = "BLACK_STONES:"+score_data[PlayDGS.BLACK_STONES]
				  + "\nWHITE_STONES:" + score_data[PlayDGS.WHITE_STONES]
				  + "\nBLACK_DEAD:"+score_data[PlayDGS.BLACK_DEAD]
				  + "\nWHITE_DEAD:" + score_data[PlayDGS.WHITE_DEAD]
				  + "\nBLACK_TERRITORY:" + score_data[PlayDGS.BLACK_TERRITORY]
				  + "\nWHITE_TERRITORY" + score_data[PlayDGS.WHITE_TERRITORY]
				  + "\nNEUTRAL_POIN:" + score_data[PlayDGS.NEUTRAL_POINTS]
				  + "\nDAME_POINTS:" + score_data[PlayDGS.DAME_POINTS]
				  + "\nOldMarked:" + marked_state
				  + "\nNewMarked:" + tst_marked_state
				  + "\nDIFF:" + df;
		 new AlertDialog.Builder(ct, WoStyle)
		    .setTitle("")
		    .setMessage(ds)
		    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	;  
			    }
			    }).show();
		*/
	}
	
	private void playEditNode() {
        Move move;
		if (!currNode.hasChildren()) {  // end of a branch
            move = currNode.getMove();
            if (move != null) {
                GoPoint p = move.getPoint();
                if (p == null) {  // null is pass
                    if (!inClient) {
                        if (goguiX == -1 && goguiY == -1) {  // pass after pass
                            askHandlePassPass();
                            return;
                        }
                    }
                } else {   // undo last move
                    int lastX = p.getX();
                    int lastY = p.getY();
                    if (goguiX == lastX && goguiY == lastY) {  // remove the last move on the branch
                        if (currNode.hasFather()) {
                            if (makingMove) return;
                            makingMove = true;
                            if (!inClient || (movesMade > 0)) {  // if inClient, must have a conditional move to remove
								Node nodeFather = currNode.getFather();
                                if (currNode == lastNode) {
                                    lastNode = nodeFather;
                                }
                                nodeFather.removeChild(currNode);
                                movesMade--;
                                unSavedChanges = true;
								gotoGameNode(nodeFather);
                            }
                            makingMove = false;
                            return;
                        }
                    }
                }
            }
			// make new move,  inClient we have no navigation to make branches, so adding at end is allways safe
			if (!(goguiX == -1 && goguiY == -1)) { 
				if (!isValidMove(gogui_board, playerColorToMove, goguiX, goguiY)) {
					Toast.makeText(ctw, ctw.getString(R.string.IllegalMove), Toast.LENGTH_LONG).show();
					return;
				}
			}
			if (makingMove) return;
			makingMove = true;

			if (goguiX == -1 && goguiY == -1) {
				if (playerColorToMove == GoColor.WHITE) {
					move = Move.get(GoColor.WHITE, null);
				} else {
					move = Move.get(GoColor.BLACK, null);
				}
			} else {
				if (playerColorToMove == GoColor.WHITE) {
					move = Move.get(GoColor.WHITE, goguiX, goguiY);
				} else {
					move = Move.get(GoColor.BLACK, goguiX, goguiY);
				}
			}
			Node newNode = new Node(move);

			if (currNode == lastNode) {
				lastNode = newNode;
			}
			currNode.append(newNode);
			unSavedChanges = true;
			movesMade++;
			gotoGameNode(newNode);
			makingMove = false;
			return;
		}
		
		if (inClient) return;
		
		// not the end of a variation, ask what to do
		if (goguiX == -1 && goguiY == -1) {
			askMidPass();
		} else {
			askMidMove();
		}
	}
	
	private void removeCurrNodeInTree() {
		Node nodeFather = currNode.getFather();
		if (nodeFather == null) return;
		if (makingMove) return;
		makingMove = true;
		int inx = nodeFather.getChildIndex(currNode);
		Node nodeChild = currNode.getChild();
		nodeFather.replaceChildAtIndex(inx, nodeChild);
		currNode = nodeFather;
		unSavedChanges = true;
		gotoGameNode(currNode);
		makingMove = false;
	}
	
	private void insertMove(GoColor c) {
		Move move = null;
		Node nodeChild = null;
		Node newNode = null;
		move = getMoveOffFather();
		if (move == null) return;
		if (makingMove) return;
		makingMove = true;
		nodeChild = currNode.getChild();
		currNode.removeChild(currNode.getChild());
		move = Move.get(c, move.getPoint());
		newNode = new Node(move);
		while (currNode.hasChildren()) {
			newNode.append(currNode.getChild());
			currNode.removeChild(currNode.getChild());
		}
		currNode.append(newNode);
		while (newNode.hasChildren()) {
			currNode.append(newNode.getChild());
			newNode.removeChild(newNode.getChild());
		}
		newNode.append(nodeChild);
		currNode = newNode;
		unSavedChanges = true;
		gotoGameNode(currNode);
		makingMove = false;
	}

	private void browseNode() {
		if (makingMove) return;
		makingMove = true;
		Move move;
		GoPoint p;
		int lastX, lastY;
		ConstNode node = rootNode;
		Node newNode = currNode;
		// walk to the identified move in the game tree
		if (node != null)
			while (true) {
				move = node.getMove();
				if (move != null) {
					p = move.getPoint();
					if (p == null) {  // null is pass
						if (goguiX == -1 && goguiY == -1) {
							newNode = (Node) node;
							break;
						}
					} else {
						lastX = p.getX();
						lastY = p.getY();
						if (goguiX == lastX && goguiY == lastY) {
							newNode = (Node) node;
							break;
						}
					}
				}
				ConstNode child = node.getChildConst();
				if (child == null) 
					return;
				node = child;
			}
		gotoGameNode(newNode);
		makingMove = false;
	}
	
	private void guessNode() {
		Move move;
		GoPoint p;
		int lastX, lastY;
		boolean pass = false;
		newNode = null;
		if (currNode.hasChildren()) {
			for (int i = 0; i < currNode.getNumberChildren(); ++i) {
				Node child = currNode.getChild(i);
				move = child.getMove();
				if (move != null) {
					p = move.getPoint();
					if (p != null) {  // null is pass
						lastX = p.getX();
						lastY = p.getY();
						if (goguiX == lastX && goguiY == lastY) {
							newNode = child;
							break;
						}
					} else pass = true;
				} else pass = true;
			}
		} else pass = true;
		if (newNode == null) {
			if (pass && goguiX == -1 && goguiY == -1) {
				bUpdate.displayGameInfo(ctw.getString(R.string.correct));
			} else {
				bUpdate.displayGameInfo(ctw.getString(R.string.incorrect));
			}
		} else {
			if (makingMove) return;
			makingMove = true;
			gotoGameNode(newNode);
			makingMove = false;
		}
	}
	
	private void setHandicapNode() {
		int aType = getNodeAddedStones(currNode, goguiX, goguiY);
		if (aType == M_NONE) {
			if (handicapToSet < 1) {  // all handicap set switch to play mode
				if (inClient) {        // except for DGS
					turnComplete = true;
                    boardState = MADE_MOVE;
					return;  
				}
				m_game_action = GA_PLAY;
				playerColorToMove = GoColor.WHITE;
				mGameName = getGameName(gogui_game, rootNode);
				playEditNode();
				return;
			}
			if (goguiX == -1 && goguiY == -1) return;
			handicapToSet--;
			mGameName = ctw.getString(R.string.HandicapToSet) + ": " + handicapToSet;
			if (inClient && handicapToSet < 1) {
                turnComplete = true;
                boardState = MADE_MOVE;
            }
			set_markup(M_NONE, M_ADD_B, null);
		} else {
			if (goguiX == -1 && goguiY == -1) return;
			handicapToSet++;
			mGameName = ctw.getString(R.string.HandicapToSet) + ": " + handicapToSet;
			set_markup(M_NONE, M_NONE, null);		
		}	
	}
	
	private void markNode() {
		if (goguiX == -1 && goguiY == -1) return;
		GoPoint p;
		p = GoPoint.get(goguiX, goguiY);
		GoColor mvColor = gogui_board.getColor(p);
		String p_label = getNodeLabel(currNode, goguiX, goguiY);
		int mType = getNodeMark(currNode, goguiX, goguiY);
		int aType = getNodeAddedStones(currNode, goguiX, goguiY);
		bUpdate.requestMark(mType,aType,p_label,mvColor);
	}
	
	private boolean sgfToBoard(String sgf) {
		byte[] bytes;

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			bytes = sgf.getBytes(StandardCharsets.UTF_8);
		} else {
			bytes = sgf.getBytes(Charset.forName("UTF-8"));
		}
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);

		SgfReader reader;
		try {
			reader = new SgfReader(in, null, null, bytes.length);
		} catch (SgfError e) {
            errHist.writeErrorHistory("BoardManager SgfError:" + e.toString());
			Toast.makeText(ctw, "SgfError:" + e.getMessage(), Toast.LENGTH_LONG).show();
			return false;
		}
		if (!setUpTree(reader)) {
			errHist.writeErrorHistory("BoardManager SgfError: bad setup");
			Toast.makeText(ctw, "SgfError: bad setup", Toast.LENGTH_LONG).show();
			return false;
		};
		return true;
	}	
	
	private boolean setUpTree(SgfReader reader) {
		int maxMove = 999;
		gogui_tree = reader.getTree();
		// String m_warnings = reader.getWarnings();

		gogui_game = new Game(gogui_tree);
		ConstNode node = gogui_tree.getRoot();
		rootNode = (Node) node;
		int moveNo = 0;
		// walk to the end of the main branch of the game tree
		if (node != null)
			while (true) {
				if (node.getMove() != null) {
					++moveNo;
					if (moveNo >= maxMove)
						break;
				}
				ConstNode child = node.getChildConst();
				if (child == null)
					break;
				node = child;
			}

		lastNode = (Node) node;
		currNode = lastNode;
        clientBaseNode = currNode;  //  for client remember where we started so that we can create the stored moves list and return if skip or undo
		gogui_game.gotoNode(node);
		gogui_board = (Board) gogui_game.getBoard();
		m_komi = getKomi();
		m_rules = ScoringMethod.TERRITORY;  // TODO get from game
		fullGridBrdSize = gogui_board.getSize();
		if (fullGridBrdSize > 25) {
			fullGridBrdSize = 19;
			mGameName = "** MAX 25x25 **";
			boardState = CANNOT_MOVE;
		} else {
			mGameName = getGameName(gogui_game, rootNode);
			if (inClient) {   // DGS actions/states
				if (m_game_action.contentEquals(GA_PLAY)) {
					// do nothing here
				} else if (m_game_action.contentEquals(GA_SET_HA)) {
					handicapToSet = Integer.parseInt(m_handicap);
					mGameName = ctw.getString(R.string.HandicapToSet) + ": " + handicapToSet;
				} else if (m_game_action.contentEquals(GA_SCORING)) {
					marked_state = score_data[PlayDGS.BLACK_DEAD] + score_data[PlayDGS.WHITE_DEAD]
						+ score_data[PlayDGS.NEUTRAL_POINTS];
					PointList pl = new PointList();
					pl.clear();
					for (int i=0; i < marked_state.length(); i+=2) {
						char xChar = marked_state.charAt(i);
				        int x = xChar - 'a';
				        char yChar = marked_state.charAt(i+1);
				        int y = flipSGFy(yChar - 'a');
						GoPoint p = GoPoint.get(x, y);
						pl.add(p);
					}
					setUpScoringNode(pl);
					/*
					String ds = "BLACK_STONES:"+score_data[PlayDGS.BLACK_STONES]
							  + "\nWHITE_STONES:" + score_data[PlayDGS.WHITE_STONES]
							  + "\nBLACK_DEAD:"+score_data[PlayDGS.BLACK_DEAD]
							  + "\nWHITE_DEAD:" + score_data[PlayDGS.WHITE_DEAD]
							  + "\nBLACK_TERRITORY:" + score_data[PlayDGS.BLACK_TERRITORY]
							  + "\nWHITE_TERRITORY" + score_data[PlayDGS.WHITE_TERRITORY]
							  + "\nNEUTRAL_POIN:" + score_data[PlayDGS.NEUTRAL_POINTS]
							  + "\nDAME_POINTS:" + score_data[PlayDGS.DAME_POINTS]
							  + "\nMARKED:" + marked_state
							  + "\nPointList:" + pl.toString();
					 new AlertDialog.Builder(ct, cWoStyle)
					    .setTitle("")
					    .setMessage(ds)
					    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int whichButton) {
						    	;  
						    }
						    }).show();
					*/
				} else { // editor actions/states
					if (m_game_status.contentEquals(GS_KOMI)) {
						// TODO support Komi negotiation
						mGameName = ctw.getString(R.string.UnsupportedGameAction);
						boardState = CANNOT_MOVE;
					} else {
						mGameName = ctw.getString(R.string.UnsupportedGameAction);
						boardState = CANNOT_MOVE;

					}
				}
			} else {
				if (!lastNode.hasFather()) {
					if (lastNode.getSetup(BLACK).isEmpty())
						if (m_game_action.contentEquals(GA_SET_HA)) {
							handicapToSet = Integer.parseInt(m_handicap);
							mGameName = ctw.getString(R.string.HandicapToSet) + ": " + handicapToSet;
						}
				}
			}
		}
// TODO start of board graphics setup
		
		bUpdate.displayGameInfo(mGameName);
		// create board image                                  
		if (boardCoord) {
			fullGrphsBrdSize = fullGridBrdSize + 1;
		} else {
			fullGrphsBrdSize = fullGridBrdSize;
		}
		fullGrphsStnSize = getStnSize(display_width, fullGrphsBrdSize);
		//fullTxtSize = (((float)fullGrphsStnSize*0.78f)-0.5f)/display_scale;  // scale down, convert px to dp 1.6
		fullTxtSize = (fullGrphsStnSize*0.78f);
		int fullGridSize = fullGrphsStnSize * fullGrphsBrdSize;
		fullPadSize = (int) Math.floor((display_width - fullGridSize) / 2);
		full_grid_layout = new ViewGroup.LayoutParams(fullGridSize, fullGridSize);

		fullStonesTable = new BoardTable(fullGrphsBrdSize, fullGrphsStnSize, fullTxtSize, true, boardCoord, controlType, ctw, this);
		fullBoardHoshi = getFullBoardHoshi(fullGridBrdSize);
		fullBoardLines = new BoardLines(ctw,fullGridBrdSize,fullGrphsStnSize,true,true,true,true,fullBoardHoshi,scale_lines);
		fullStonesView = fullStonesTable.getTableView();
		
		if (fullGridBrdSize < 9 && controlType == SLIDE_CONTROL) {
			move_control = PrefsDGS.ONETOUCH;
			controlType = ONE_TOUCH_CONTROL;
		}
		
		if (controlType == SLIDE_CONTROL) {
			slideStonesTable = new BoardTable(fullGrphsBrdSize, fullGrphsStnSize, fullTxtSize, true, boardCoord, controlType, ctw, this);
			slideStonesView = slideStonesTable.getTableView();
			slideStonesView.setOnTouchListener(touchFull);
		}
		
		if (move_control.contentEquals(PrefsDGS.ZOOM13X13)) {
			zoomGridBrdSize = 13;
		} else if (move_control.contentEquals(PrefsDGS.ZOOM11X11)) {
			zoomGridBrdSize = 11;
		} else if (move_control.contentEquals(PrefsDGS.ZOOM9X9)) {
			zoomGridBrdSize = 9;
		} else {
			zoomGridBrdSize = 7;
		}
		
		if (boardCoord) {
			zoomGrphsBrdSize = zoomGridBrdSize + 1;

		} else {
			zoomGrphsBrdSize = zoomGridBrdSize;
		}		
		zoomGrphsStnSize = getStnSize(display_width, zoomGrphsBrdSize);
		//zoomTxtSize = (((float)zoomGrphsStnSize*0.78f)-0.5f)/display_scale; // scale down, convert px to dp 1.6
		zoomTxtSize = (zoomGrphsStnSize*0.78f);
		int zoomGridSize = zoomGrphsStnSize * zoomGrphsBrdSize;
		zoomPadSize = (int) Math.floor((display_width - zoomGridSize) / 2);
		zoom_grid_layout = new ViewGroup.LayoutParams(zoomGridSize, zoomGridSize);
		
		zoomStonesTable = new BoardTable(zoomGrphsBrdSize, zoomGrphsStnSize, zoomTxtSize, false, false, controlType, ctw, this);
		zoomBoardLines = new BoardLines(ctw,zoomGridBrdSize,zoomGrphsStnSize,true,true,true,true,zoomBoardHoshi,scale_lines);
		zoomStonesView =zoomStonesTable.getTableView();
		
		if (fullGrphsBrdSize <= zoomGrphsBrdSize && controlType == ZOOM_CONTROL) {
			move_control = PrefsDGS.ONETOUCH;
			controlType = ONE_TOUCH_CONTROL;
		}
	
		if (bgGraphic.contentEquals(PrefsDGS.BG_CUSTOM) ) {
			boardSwitcher.setBackgroundColor(customBGvalue);
		} else if (bgGraphic.contentEquals(PrefsDGS.BG_WOOD) ) {
			boardSwitcher.setBackgroundResource(R.drawable.wood1);
		} else if (bgGraphic.contentEquals(PrefsDGS.BG_PLAIN) ) {
			boardSwitcher.setBackgroundColor(MainDGS.BOARD_COLOR);
		} else if (bgGraphic.contentEquals(PrefsDGS.BG_WHITE) ) {
			boardSwitcher.setBackgroundColor(MainDGS.WHITE_COLOR);
		} else {
			boardSwitcher.setBackgroundColor(MainDGS.BOARD_COLOR);
		}
		
		whiteBG = bgGraphic.contentEquals(PrefsDGS.BG_WHITE);
		monochrome_stones = boardStone.contentEquals(PrefsDGS.STONE_MONO);

		//boardSwitcher.setBackgroundColor(MainDGS.TRANSPARENT_COLOR);
		if (m_game_action.contentEquals(GA_SCORING)) {
			computeDisplayScore (false);
		} else {
			gotoGameNode(lastNode);
		}
		return true;
	}

    public void redisplayFullBoard() {
    	boardSwitcher.removeAllViews();
    	boardSwitcher.setPadding(fullPadSize, fullPadSize, fullPadSize, fullPadSize);
    	boardSwitcher.addView(fullBoardLines, full_grid_layout);
		boardSwitcher.addView(fullStonesView, full_grid_layout);
		if (controlType == SLIDE_CONTROL) {
			boardSwitcher.addView(slideStonesView, full_grid_layout);
		}
		boardSwitcher.postInvalidate();
		bUpdate.displayFullButtons(getButtonState(), boardState);
		bUpdate.displayComment(get_comment());
	}

	public void redisplayZoomBoard() {
		boardSwitcher.removeAllViews();
		boardSwitcher.setPadding(zoomPadSize, zoomPadSize, zoomPadSize, zoomPadSize);
		boardSwitcher.addView(zoomBoardLines, zoom_grid_layout);
		boardSwitcher.addView(zoomStonesView, zoom_grid_layout);
		boardSwitcher.postInvalidate();
		bUpdate.displayZoomButtons(getButtonState(), boardState);
		bUpdate.displayComment(get_comment());
	}
	
	private void gotoGameNode(Node node) {
		newNode = node;
		try {
			gogui_game.gotoNode(newNode);
		} catch (Exception e) {  // got lost, reset to the end
			newNode = rootNode;  
			while (newNode.hasChildren()) {
				newNode = newNode.getChild();
			}
			lastNode = newNode;
			gogui_game.gotoNode(newNode);
		}

		currNode = newNode;
		playerColorToMove = newNode.getToMove();
		gogui_board = (Board) gogui_game.getBoard();
		
		treeToGraphics(gogui_board, currNode);
		setFullBoardLabels(newNode, numPrev);
		if (boardState != CANNOT_MOVE) {
			if (inClient && (turnComplete || movesMade > 0)) {
				boardState = MADE_MOVE;
			} else {
				boardState = FULL_BOARD;
			}
		}
		bUpdate.displayMoveInfo(getMoveInfo(newNode));
		bUpdate.displayGameInfo(mGameName);
		redisplayFullBoard();
	}
	
	private void treeToGraphics(Board gogui_board, Node lastNode) {
		for (int y = 0; y < fullGridBrdSize; ++y) {
			for (int x = 0; x < fullGridBrdSize; ++x) {
				GoPoint p = GoPoint.get(x, y);
				GoColor c = gogui_board.getColor(p);
				if (c == GoColor.WHITE) {
					fullStonesTable.setLocationGraphic(x, flipSGFy(y), W);
				} else if (c == GoColor.BLACK) {
					fullStonesTable.setLocationGraphic(x, flipSGFy(y), B);
				} else {
					fullStonesTable.setLocationGraphic(x, flipSGFy(y), BLNK); 
				}
			}
		}
		try {
			Move move = lastNode.getMove();
			if (move != null) {
				GoPoint p = move.getPoint();
				if (p != null) {  // null is pass
					int x = p.getX();
					int y = flipSGFy(p.getY());
					int sg = fullStonesTable.getLocationGraphic(x, y);
					fullStonesTable.setLocationGraphic(x, y, changeStoneMark(sg, M_MOVE));
				}
		}
		} catch (Exception e) {
		}
		boardMarks(lastNode, M_MARK);
		boardMarks(lastNode, M_TERR_B);
		boardMarks(lastNode, M_TERR_W);
		boardMarks(lastNode, M_TRIANGLE);
		boardMarks(lastNode, M_CIRCLE);
		boardMarks(lastNode, M_SQUARE);
	}

	private boolean setFullBoardLabels(Node node, int numPrev) {
		fullStonesTable.clearBoardText(fullGridBrdSize);   // always clear the labels but not coordinates
		
		Map<GoPoint,String> label_map = node.getLabels();
		if ((label_map == null || label_map.size() == 0) && numPrev == 0)
			return false;

		int g;
		int x,y;
		GoPoint p;
		String s;
		if (!(label_map == null || label_map.size() == 0)) {
			Set<Entry<GoPoint,String>> es = label_map.entrySet();
			Iterator<Entry<GoPoint,String>> i = es.iterator();
			Entry<GoPoint,String> e;
			while (i.hasNext()) {
				e = i.next();
				p = e.getKey();
				s = e.getValue();
				x = p.getX();
				y = flipSGFy(p.getY());
				g = fullStonesTable.getLocationGraphic(x, y);
				fullStonesTable.setLocationText(x, y, isWhiteGraphic(g), s); 
				}
		}
		
		int i = 1;
		Node n = node;
		if (numPrev < 0) {	
			while (true) {
				if (i > (0-numPrev)) return true;
				if (!n.hasFather()) return true;
				n = n.getFather();
				Move m = n.getMove();
				if (m == null) return true;
				p = m.getPoint();
				if (p == null) return true; // null is pass
				x = p.getX();
				y = flipSGFy(p.getY());
				s = fullStonesTable.getLocationText(x, y);
				if (s != null)  {
					if (!s.contentEquals("")) return true;  // already have a label
				}
				g = fullStonesTable.getLocationGraphic(x, y);
				fullStonesTable.setLocationText(x, y, isWhiteGraphic(g), "-" + i); 
				++i;
			}
		} else if (numPrev > 0) {
			GoPoint[] points = new GoPoint [numPrev+1];  // 1 relative
			while (true) {
				if (i > numPrev) break;
				if (!n.hasFather()) break;
				n = n.getFather();
				Move m = n.getMove();
				if (m == null) break;
				p = m.getPoint();
				if (p == null) break; // null is pass
				points [i] = p;
				x = p.getX();
				y = flipSGFy(p.getY());
				s = fullStonesTable.getLocationText(x, y);
				if (s != null)  {
					if (!s.contentEquals("")) break;  // already have a label
				}
				++i;
			}
			for (int j=i-1; j>0; --j) {
				p = points[j];
				if (p==null) break;
				x = p.getX();
				y = flipSGFy(p.getY());
				g = fullStonesTable.getLocationGraphic(x, y);
				fullStonesTable.setLocationText(x, y, isWhiteGraphic(g), "" + (i-j)); 
			}
		}
		return true;
	}

	private void makeSound() {
		new Thread(){
            @Override
			public void run(){
            	final MediaPlayer mp = MediaPlayer.create(ctw, R.raw.stone);
            	mp.start();
        	}
        }.start();
	}
	
	 private final GameBoardHandler mHandler = new GameBoardHandler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
				case FWD:
					go_next_node();
					break;
				case BKW:
					go_prev_node();
					break;
				default:
				}
			}
		};
		
	private Komi getKomi() {
		Komi komi;
		ConstNode node = gogui_game.getGameInfoNode();
		if (node == null) {
			komi = new Komi(6.5);
		} else {
			ConstGameInfo info = gogui_game.getGameInfo(node);
	 		if (info == null) {
	 			komi = new Komi(6.5);
			} else {
				komi = info.getKomi();
			}
		}
		return komi;
	}

	private String getMoveColor(Node node) {
		Move move;
		GoColor c;
		move = node.getMove();
		if (move != null) {
			c = move.getColor();
			if (c == GoColor.WHITE) {
				return "W";
			} else if (c == GoColor.BLACK) {
				return "B";
			} 
		} else {
			if (node.hasFather()) {
				move = node.getFather().getMove();
				if (move != null) {
					c = move.getColor();
					if (c == GoColor.WHITE) {
						return "B";
					} else if (c == GoColor.BLACK) {
						return "W";
					} 
				} 
			}
		}
		return "E";
	}
	
	@SuppressWarnings("unused")
	private String getMoveColor(GoColor c) {
		if (c == GoColor.WHITE) {
			return "W";
		} else if (c == GoColor.BLACK) {
			return "B";
		} else {
			return "E";
		}
	}
	
	private String getGameName(Game game, Node node) {
		SgfProperties sgfProperties = node.getSgfProperties();
		if (sgfProperties != null) {
			if (sgfProperties.getNumberValues("GN") > 0) { 
				String value = sgfProperties.getValue("GN", 0);
				if (value != null) {
				/*	while (value.startsWith("[")) {  // SGF library seems to add a [ for some GN values 
						value = value.substring(1);
					} */
					return value;
				}
			}
		}
		return game.getGameInfo(node).suggestGameName();
	}
	
	private String getGameInfo(Context c, Game game, Board board, String timeLeft) {
		StringBuilder sb = new StringBuilder();
		ConstNode node = game.getGameInfoNode();
		if (node == null) {
			return c.getString(R.string.none);
		}
 		ConstGameInfo info = game.getGameInfo(node);
 		if (info == null) {
			return c.getString(R.string.none);
		}
		ConstSgfProperties sgfProperties = node.getSgfPropertiesConst();
		String value;
		String gamename = info.suggestGameName();
		int size = game.getSize();
		int h = info.getHandicap();
		Komi k = info.getKomi();
		String date = info.get(StringInfo.DATE);
		String playerblack = info.get(StringInfoColor.NAME, BLACK);
		String blackrating = info.get(StringInfoColor.RANK, BLACK);
		String playerwhite = info.get(StringInfoColor.NAME, WHITE);
		String whiterating = info.get(StringInfoColor.RANK, WHITE);
		String result = info.get(StringInfo.RESULT);
		String ot = "";
		String rules = info.get(StringInfo.RULES);
		if (sgfProperties != null) {
			if (sgfProperties.getNumberValues("GN") > 0) { 
				value = sgfProperties.getValue("GN", 0);
				if (value != null) {
				/*	while (value.startsWith("[")) {  // SGF library seems to add a [ for some GN values 
						value = value.substring(1);
					} */
					gamename = value;
				}
			}
			if (sgfProperties.getNumberValues("OT") > 0) { 
				value = sgfProperties.getValue("OT", 0);
				if (value != null) {
					ot = value;
				}
			}
		}
		if (gamename == null) {
			gamename = "";
		}
		if (date == null) {
			date = "";
		}
		if (playerblack == null) {
			playerblack = "";
		}
		if (blackrating == null) {
			blackrating = "";
		}
		if (playerwhite == null) {
			playerwhite = "";
		}
		if (whiterating == null) {
			whiterating = "";
		}
		if (result == null) {
			result = "";
		}
		if (rules == null) {
			rules = "";
		}
		if (k == null) {
			k = new Komi(0.5);
		}
		
		sb.append(gamename).append("\n");
        sb.append(c.getString(R.string.WhitePlayer)).append(": ").append(playerwhite).append(" ").append(whiterating).append("\n");
        sb.append(c.getString(R.string.BlackPlayer)).append(": ").append(playerblack).append(" ").append(blackrating).append("\n");
        sb.append(c.getString(R.string.GameSize)).append(": ").append(size).append("\n");

        if (h > 1) {
			sb.append(c.getString(R.string.Handicap)).append(": ").append(h).append(" ");
		}
		sb.append(c.getString(R.string.Komi)).append(": ").append(k.toString()).append("\n");
        sb.append(c.getString(R.string.Rules)).append(": ").append(rules).append("\n");
        sb.append(c.getString(R.string.Date)).append(": ").append(date).append("\n");
        sb.append(c.getString(R.string.Overtime)).append(": ").append(ot).append("\n");

        if (!timeLeft.contentEquals("")) {
			sb.append(c.getString(R.string.TimeLeft)).append(": ").append(timeLeft).append("\n");
		}
		if (!result.contentEquals("")) {
			sb.append(c.getString(R.string.Result)).append(": ").append(result).append("\n");
		}
		
		if (board != null) {
			sb.append("@").append(board.getNumberMoves()).append("\n");
            sb.append(c.getString(R.string.captured)).append(": ");
            sb.append(c.getString(R.string.white)).append(": ").append(board.getCaptured(WHITE));
            sb.append(", ");
            sb.append(c.getString(R.string.black)).append(": ").append(board.getCaptured(BLACK)).append("\n");
		}
		return sb.toString();
	}

	@SuppressWarnings("unused")
	private String getNodeComment(Node node) {
		String value = node.getComment();
		if (value != null) return value;
		return "";
	}

	private String getMoveInfo(Node node) {
		if (node.hasFather()) {
			String coord = getMoveCoord(node);
			if (coord.contentEquals("")) {
				return getMoveColor(node) + " Pass ";
			} else {
				return getMoveColor(node) + "[" + coord + "] ";
			}
		} else {
			ConstGameInfo info = node.getGameInfoConst();
			if (info != null) {
				int h = info.getHandicap();
				if (h > 1) {
					return "H "+h+"   ";
				}
			}
		}
			return "      ";
	}

	private boolean isValidMove(Board brd, GoColor playerColorToMove, int x, int y) {
		GoPoint p = GoPoint.get(x, y);
		if (brd.getColor(p) != GoColor.EMPTY)
			return false;
		if (brd.isSuicide(playerColorToMove, p))
			return false;
		return !brd.isKo(p);
	}

	private void initZoomBoard(int mX, int mY) {  // display coord
		int g;
		int zOffset = zoomGridBrdSize / 2;
		int mXo = mX - zOffset;
		int mYo = mY - zOffset;
		if (mXo < 0)
			mXo = 0;
		if (mYo < 0)
			mYo = 0;
		int maxO = fullGridBrdSize - zoomGridBrdSize;
		if (mXo > maxO)
			mXo = maxO;
		if (mYo > maxO)
			mYo = maxO;
		xFBOffset = (mX - mXo);
		yFBOffset = (mY - mYo);

		boolean topEdge = (mXo <= 0);
		boolean bottomEdge = (mXo >= maxO);
		boolean leftEdge = (mYo <= 0);
		boolean rightEdge = (mYo >= maxO);
		zoomBoardHoshi = getZoomBoardHoshi(mXo, mYo, zoomGridBrdSize, fullBoardHoshi);
		zoomBoardLines = new BoardLines(ctw,zoomGridBrdSize,zoomGrphsStnSize,topEdge,bottomEdge,leftEdge,rightEdge,zoomBoardHoshi,scale_lines);

		
		if (boardCoord) {  // fullGridBrdSize == fullGrphsBrdSize - 1
			for (int i = 0; i < zoomGridBrdSize; ++i) {
				// right column
				g = fullStonesTable.getLocationGraphic(fullGridBrdSize, mYo + i);
				zoomStonesTable.setLocationGraphic(zoomGridBrdSize, i, g);
				zoomStonesTable.setLocationText(zoomGridBrdSize, i, isWhiteGraphic(g), 
						fullStonesTable.getLocationText(fullGridBrdSize, mYo + i));

				// bottom row
				g = fullStonesTable.getLocationGraphic(mXo + i, fullGridBrdSize);
				zoomStonesTable.setLocationGraphic(i, zoomGridBrdSize, g);
				zoomStonesTable.setLocationText(i, zoomGridBrdSize, isWhiteGraphic(g), 
						fullStonesTable.getLocationText(mXo + i, fullGridBrdSize));
			}
		}

		// fill board
		for (int x = 0; x < zoomGridBrdSize; ++x) {
			for (int y = 0; y < zoomGridBrdSize; ++y) {
				g = fullStonesTable.getLocationGraphic(mXo+x, mYo+y);
				zoomStonesTable.setLocationGraphic(x, y, g);
				zoomStonesTable.setLocationText(x, y, isWhiteGraphic(g), 
						fullStonesTable.getLocationText(mXo+x, mYo+y));
			}
		}
	}
	
	public String getScoreDifference() {
		StringBuilder new_marked_state = new StringBuilder();
		for (GoPoint stone : gogui_board) {
			String coord = getCoord(stone);
			GoColor c = scoreCounter.getColor(stone, m_rules); 
			GoColor s = gogui_board.getColor(stone);
			if (c == GoColor.BLACK) {
				if (s == GoColor.WHITE) 
					new_marked_state.append(coord);
			} else if (c == GoColor.WHITE) {
				if (s == GoColor.BLACK) 
					new_marked_state.append(coord);
			} else if (c == GoColor.EMPTY) {
				if (isCoordAinB(coord, score_data[PlayDGS.NEUTRAL_POINTS])) {
					new_marked_state.append(coord);  
				} else if (!isCoordAinB(coord, score_data[PlayDGS.DAME_POINTS])){
					new_marked_state.append(coord);  // this should never happen I think
				}
			}
		}
		//tst_marked_state = new_marked_state.toString();
		return getCoordsDiff(marked_state,new_marked_state.toString());
	}
	
	private String getCoord(GoPoint p) {
		int x, y;
		x = 'a' + p.getX();
		y = 'a' + fullGridBrdSize - p.getY() - 1;
		return Character.toString((char)x) + (char) y;
	}
	
	// return the coords not in both strings
	private String getCoordsDiff(String a, String b){
        String s = "" + getCoordsAnotInB(a, b) +
                getCoordsAnotInB(b, a);
        return s;
	}
	
	// return the coords in A that are also in B
	@SuppressWarnings("unused")
	private String getCoordsAinB(String a, String b){
		StringBuilder s = new StringBuilder();
		for (int i=0; i < a.length()-1; i +=2) {
			String p = a.substring(i, i+2);
			if (isCoordAinB(p,b)) s.append(p);
		}
		return s.toString();
	}
	
	// return if coord in A that is also in B
	private boolean isCoordAinB(String a, String b){
		for (int j=0; j < b.length()-1; j +=2) {
			if (a.contentEquals(b.substring(j, j+2)))
				return true;
		}
		return false;
	}
	
	// return the coords in A that are not in B
	private String getCoordsAnotInB(String a, String b){
		StringBuilder s = new StringBuilder();
		for (int i=0; i < a.length()-1; i +=2) {
			String p = a.substring(i, i+2);
			if (!isCoordAinB(p,b)) s.append(p);
		}
		return s.toString();
	}

	private int getStnSize(int width, int boardSz) {
		double d = (width/boardSz);
		return (int) Math.floor(d);
	}

	/*
	 * sgf is layedout with 0,0 in the lower-left and n,n in the upper-right
	 * This means that the Y coordinate needs to be flipped for the display
	 */
	private int flipSGFy(int c) {
		return fullGridBrdSize - 1 - c;
	}
		

	private void boardMarks(Node node, int typ) {
		PointList pl;
		Iterator<GoPoint> i;
		GoPoint p;
		int sg;
		int x, y;
		MarkType markType;
		switch (typ) {
		case M_MARK: markType = MarkType.MARK;  break;
		case M_TRIANGLE: markType = MarkType.TRIANGLE;  break;
		case M_CIRCLE: markType = MarkType.CIRCLE;  break;
		case M_SQUARE: markType = MarkType.SQUARE;  break;
		case M_TERR_W: markType = MarkType.TERRITORY_WHITE;  break;
		case M_TERR_B: markType = MarkType.TERRITORY_BLACK;  break;
		default: markType = MarkType.SELECT; // unused value
		}
		pl = node.getMarked(markType);
		if (pl != null) {
			i = pl.iterator();
			while (i.hasNext()) {
				p = i.next();
				x = p.getX();
				y = flipSGFy(p.getY());
				sg = fullStonesTable.getLocationGraphic(x, y);
				fullStonesTable.setLocationGraphic(x, y, changeStoneMark(sg, typ));
			}
		}
	}
	
	/*
	 * sgf coordinates
	 */
	private String getNodeLabel(Node node, int x, int y) {
		GoPoint p = GoPoint.get(x, y);
		String p_label = node.getLabel(p);
		if (p_label == null) p_label = " ";
		return p_label;
	}

	/*
	 * sgf coordinates
	 */
	private int getNodeMark(Node node, int x, int y) {
		PointList pl;
		Iterator<GoPoint> i;
		GoPoint p;
		int pX, pY;
		pl = node.getMarked(MarkType.MARK);
		if (pl != null) {
			i = pl.iterator();
			while (i.hasNext()) {
				p = i.next();
				pX = p.getX();
				pY = p.getY();
				if (pX == x && pY == y) {
					return M_MARK;
				}
			}
		}
		pl = node.getMarked(MarkType.CIRCLE);
		if (pl != null) {
			i = pl.iterator();
			while (i.hasNext()) {
				p = i.next();
				pX = p.getX();
				pY = p.getY();
				if (pX == x && pY == y) {
					return M_CIRCLE;
				}
			}
		}
		pl = node.getMarked(MarkType.SQUARE);
		if (pl != null) {
			i = pl.iterator();
			while (i.hasNext()) {
				p = i.next();
				pX = p.getX();
				pY = p.getY();
				if (pX == x && pY == y) {
					return M_SQUARE;
				}
			}
		}
		pl = node.getMarked(MarkType.TRIANGLE);
		if (pl != null) {
			i = pl.iterator();
			while (i.hasNext()) {
				p = i.next();
				pX = p.getX();
				pY = p.getY();
				if (pX == x && pY == y) {
					return M_TRIANGLE;
				}
			}
		}
		pl = node.getMarked(MarkType.TERRITORY_WHITE);
		if (pl != null) {
			i = pl.iterator();
			while (i.hasNext()) {
				p = i.next();
				pX = p.getX();
				pY = p.getY();
				if (pX == x && pY == y) {
					return M_TERR_W;
				}
			}
		}
		pl = node.getMarked(MarkType.TERRITORY_BLACK);
		if (pl != null) {
			i = pl.iterator();
			while (i.hasNext()) {
				p = i.next();
				pX = p.getX();
				pY = p.getY();
				if (pX == x && pY == y) {
					return M_TERR_B;
				}
			}
		}
		return M_NONE;
	}
	
	/*
	 * sgf coordinates
	 */
	private String getHandicapStones() {
		StringBuilder moves = new StringBuilder();
		ConstPointList pl;
		Iterator<GoPoint> i;
		GoPoint p;
		pl = rootNode.getSetup(GoColor.BLACK);
		if (pl != null) {
			i = pl.iterator();
			while (i.hasNext()) {
				p = i.next();
				if (p != null) {
					moves.append(getCoord(p));
				}
			}
		}
		return moves.toString();
		}
	
	/*
	 * sgf coordinates
	 */
	private int getNodeAddedStones(Node node, int x, int y) {
		ConstPointList pl;
		Iterator<GoPoint> i;
		GoPoint p;
		int pX, pY;
		pl = node.getSetup(GoColor.EMPTY);
		if (pl != null) {
			i = pl.iterator();
			while (i.hasNext()) {
				p = i.next();
				pX = p.getX();
				pY = p.getY();
				if (pX == x && pY == y) {
					return M_ADDEMPTY;
				}
			}
		}
		pl = node.getSetup(GoColor.WHITE);
		if (pl != null) {
			i = pl.iterator();
			while (i.hasNext()) {
				p = i.next();
				pX = p.getX();
				pY = p.getY();
				if (pX == x && pY == y) {
					return M_ADD_W;
				}
			}
		}
		pl = node.getSetup(GoColor.BLACK);
		if (pl != null) {
			i = pl.iterator();
			while (i.hasNext()) {
				p = i.next();
				pX = p.getX();
				pY = p.getY();
				if (pX == x && pY == y) {
					return M_ADD_B;
				}
			}
		}
		return M_NONE;
	}
	
	/*
	 * sgf coordinates
	 */
	private void setNodeMark(Node node, GoPoint p, int markIndicator, GoColor pColor) {
		PointList pl;
		pl=node.getMarked(MarkType.MARK);
		if (pl != null) {
			if (pl.contains(p)) {
				node.removeMarked(p, MarkType.MARK);
			}
		}
		pl=node.getMarked(MarkType.TRIANGLE);
		if (pl != null) {
			if (pl.contains(p)) {
				node.removeMarked(p, MarkType.TRIANGLE);
			}
		}
		pl=node.getMarked(MarkType.CIRCLE);
		if (pl != null) {
			if (pl.contains(p)) {
				node.removeMarked(p, MarkType.CIRCLE);
			}
		}
		pl=node.getMarked(MarkType.SQUARE);
		if (pl != null) {
			if (pl.contains(p)) {
				node.removeMarked(p, MarkType.SQUARE);
			}
		}
		pl=node.getMarked(MarkType.TERRITORY_WHITE);
		if (pl != null) {
			if (pl.contains(p)) {
				node.removeMarked(p, MarkType.TERRITORY_WHITE);
			}
		}
		pl=node.getMarked(MarkType.TERRITORY_BLACK);
		if (pl != null) {
			if (pl.contains(p)) {
				node.removeMarked(p, MarkType.TERRITORY_BLACK);
			}
		}
		switch (markIndicator) {
		case M_MARK: node.addMarked(p, MarkType.MARK); break;
		case M_TRIANGLE: node.addMarked(p, MarkType.TRIANGLE);  break;
		case M_CIRCLE: node.addMarked(p, MarkType.CIRCLE);  break;
		case M_SQUARE: node.addMarked(p, MarkType.SQUARE);  break;
		case M_TERR_W: if (pColor != GoColor.WHITE) node.addMarked(p, MarkType.TERRITORY_WHITE);  break;
		case M_TERR_B: if (pColor != GoColor.BLACK) node.addMarked(p, MarkType.TERRITORY_BLACK);  break;
		}
	}
	
	private void setNodeAddStone(Node node, GoPoint p, int addIndicator, GoColor pColor) {
		node.removeSetup(p);
		switch (addIndicator) {
		case M_ADDEMPTY: node.addStone(GoColor.EMPTY, p); break;
		case M_ADD_W: node.addStone(GoColor.WHITE, p); break;
		case M_ADD_B: node.addStone(GoColor.BLACK, p); break;
		case M_NONE: break;
		}
	}
	
	public static int getGraphic(int gt) {
		switch (gt) {
		case BLNK:
			return R.drawable.blnk;
		case B:
			if (monochrome_stones) return R.drawable.b1;
			else return R.drawable.b;
		case BM:
			if (monochrome_stones) return R.drawable.bm1;
			else return R.drawable.bm;
		case BC:
			if (monochrome_stones) return R.drawable.bc1;
			else return R.drawable.bc;
		case BS:
			if (monochrome_stones) return R.drawable.bs1;
			else return R.drawable.bs;
		case BT:
			if (monochrome_stones) return R.drawable.bt1;
			else return R.drawable.bt;
		case BX:
			if (monochrome_stones) return R.drawable.bx1;
			else return R.drawable.bx;
		case W:
			if (monochrome_stones) return R.drawable.w1;
			else return R.drawable.w;
		case WM:
			if (monochrome_stones) return R.drawable.wm1;
			else return R.drawable.wm;
		case BW:
			if (monochrome_stones) return R.drawable.bw1;
			else return R.drawable.bw;
		case WB:
			if (monochrome_stones) return R.drawable.wb1;
			else return R.drawable.wb;
		case WC:
			if (monochrome_stones) return R.drawable.wc1;
			else return R.drawable.wc;
		case WS:
			if (monochrome_stones) return R.drawable.ws1;
			else return R.drawable.ws;
		case WT:
			if (monochrome_stones) return R.drawable.wt1;
			else return R.drawable.wt;
		case WX:
			if (monochrome_stones) return R.drawable.wx1;
			else return R.drawable.wx;
		case AB:
			return R.drawable.ab;
		case AC:
			return R.drawable.ac;
		case AS:
			return R.drawable.as;
		case AT:
			return R.drawable.at;
		case AW:
			if (whiteBG || monochrome_stones) return R.drawable.aw1;
			else return R.drawable.aw;
		case AX:
			return R.drawable.ax;
		case BP:
			return R.drawable.bp;
		case WP:
			return R.drawable.wp;
		case YP:
			return R.drawable.yp;
		}
		return 0;
	}

	private int changeStoneMark(int sg, int typ) {
		switch (sg) {
		case BLNK:
			switch (typ) {
			case M_MOVE: return sg;
			case M_MARK: return AX;
			case M_TRIANGLE: return AT;
			case M_CIRCLE: return AC;
			case M_SQUARE: return AS;
			case M_TERR_W: return AW;
			case M_TERR_B: return AB;
			default: return sg;
			}
		case B:
			switch (typ) {
			case M_MOVE: return BM;
			case M_MARK: return BX;
			case M_TRIANGLE: return BT;
			case M_CIRCLE: return BC;
			case M_SQUARE: return BS;
			case M_TERR_W: return BW;
			case M_TERR_B: return BM;
			default: return sg;
			}
		case W:
			switch (typ) {
			case M_MOVE: return WM;
			case M_MARK: return WX;
			case M_TRIANGLE: return WT;
			case M_CIRCLE: return WC;
			case M_SQUARE: return WS;
			case M_TERR_W: return WM;
			case M_TERR_B: return WB;
			default: return sg;
			}
		case WM:
		case WB:
			return W;  
		case BW:
		case BM:
			return B;  
		default:
		}
		return sg;
	}

	private String getMoveCoord(Node node) {
		Move move;
		GoPoint p;
		move = node.getMove();
		if (move != null) {
			p = move.getPoint();
			if (p == null) {
				return "";
			} else {
				return getCoord(p);
			} 
		} else
			return "";
	}

	private int getGameZoomX(int fullBoardX, int x) {
		int lastX;
		lastX = fullBoardX - xFBOffset + x;
		return lastX;
	}
	
	private int getGameZoomY(int fullBoardY, int y) {
		int lastY;
		lastY = fullBoardY - yFBOffset + y;
		return flipSGFy(lastY);
	}
	
	private boolean isWhiteGraphic(int sg) {
        switch (sg) {
		case BLNK:  // blank area should be treated as white
		case W:
		case WB:
		case WM:
		case WX:
		case WT:
		case WC:
		case WS:
			return true;  
		default:
		}
		return false;
	}
	
	private PointList getZoomBoardHoshi(int xMin, int yMin, int zbsize, PointList hoshiList) {
		PointList newHoshiList = new PointList();
		if (hoshiList == null) return newHoshiList;
		Iterator<GoPoint> i = hoshiList.iterator();
		while (i.hasNext()) {
			GoPoint p = i.next();
			int x = p.getX();  
			int y = p.getY();
			if (xMin <= x && (xMin+zbsize) >= x &&
				yMin <= y && (yMin+zbsize) >= y) {
				GoPoint n = GoPoint.get(x-xMin, y-yMin);  // adjust for the zoomboard location
				newHoshiList.add(n);
			}
		}
		return newHoshiList;
	}
	
	private PointList getFullBoardHoshi(int bsize) {
		PointList newHoshiList = new PointList();
		for (int x = 0; x < bsize; ++x) {
			for (int y = 0; y < bsize; ++y) {
				if (isHoshi(bsize, x, y)) {
					GoPoint p = GoPoint.get(x, y);
					newHoshiList.add(p);
				}
			}
		}
		return newHoshiList;
	}
	
	private boolean isHoshi(int m_size, int x, int y) {
		switch (m_size) {
		case 25:
			return (x == 3 || x == 12 || x == 21) && (y == 3 || y == 12 || y == 21);
		case 23:
			return (x == 3 || x == 11 || x == 19) && (y == 3 || y == 11 || y == 19);
		case 21:
			return (x == 3 || x == 10 || x == 17) && (y == 3 || y == 10 || y == 17);
		case 19:
			return (x == 3 || x == 9 || x == 15) && (y == 3 || y == 9 || y == 15);
		case 17:
			return (x == 3 || x ==8 || x == 13) && (y == 3 || y == 8 || y == 13);
		case 15:
			return (x == 3 || x ==7 || x == 11) && (y == 3 || y == 7 || y == 11);
		case 13:
			return (x == 3 || x == 6 || x == 9) && (y == 3 || y == 6 || y == 9);
		case 11:
			return (x == 3 || x == 5 || x == 7) && (y == 3 || y == 5 || y == 7);
		case 9:
			return (x == 2 || x == 4 || x == 6) && (y == 2 || y == 4 || y == 6);
		case 7:
			return (x == 3) && (y == 3);  // center only
		case 5:
			return (x == 2) && (y == 2);  // center only
		default:
		}
		return false;
	}
	
}
