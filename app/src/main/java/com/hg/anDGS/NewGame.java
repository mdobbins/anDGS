package com.hg.anDGS;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.io.File;

public class NewGame extends DGSActivity implements OnSeekBarChangeListener{
	 protected static final int SGF_GAMES = 2;
	 protected static final int HELP_VIEW = 0;
	 private static final int MENU_HELP = 0;
	 private static final int MIN_GAME_SIZE = 5;
	 private static final int MAX_GAME_SIZE = 25;
	 
	 private TextView tmHelp;
	
	 private SeekBar size_bar;
	 private SeekBar handicap_bar;
	 private SeekBar komi_bar;
	 private TextView size_text;
	 private TextView handicap_text;
	 private CheckBox handicap_free;
	 private TextView komi_text;
	 private TextView white_name_label;
	 private TextView black_name_label;
	 private TextView sgf_file_label;
	 private TextView white_name_button;
	 private TextView black_name_button;
	 private TextView sgf_file_button;
	 private TextView newGame_button;
	 
	 private int m_size = 19; 
	 private int m_handicap = 0; 
	 private int m_size_offset = 4;
	 private double m_komi = 5.5;
	 private int m_komi_offset = 19;
	 private String m_white_name;
	 private String m_black_name;
	 private String m_sgf_file;
	 private String m_sgf_full_path = "";
	 private String timeLeft = "";
	 private String boardLayout;
	 private String theme;
	 private long autoPlayInterval = GameBoardOptions.DEFAUTOPLAYINTERVAL;
	 private boolean autoPlayPause = true;
	 private ContextThemeWrapper ctw;
	 private CommonStuff commonStuff = new CommonStuff();
	 private CommonFileStuff commonFileStuff = new CommonFileStuff();
	 
	private String sgf = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			autoPlayInterval = extras.getLong("AUTOPLAYINTERVAL",GameBoardOptions.DEFAUTOPLAYINTERVAL);
			autoPlayPause = extras.getBoolean("AUTOPLAYPAUSE", true);
			boardLayout = extras.getString("BOARDLAYOUT");
		}
		
 		SharedPreferences prefs = getSharedPreferences("MainDGS", 0);
		theme = prefs.getString("com.hg.anDGS.Theme", PrefsDGS.DEFAULT_THEME);

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
        setContentView(R.layout.newgame);
        ctw = new ContextThemeWrapper(this, commonStuff.getCommonTheme(theme));
        
		tmHelp = findViewById(R.id.newGameTMHelp);
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
		
		m_white_name = getString(R.string.white);
	    m_black_name = getString(R.string.black);
	    m_sgf_file = getString(R.string.game);

        newGame_button = findViewById(R.id.newGameButton);
        newGame_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				do_newGame();
		}});
        
        size_bar= this.findViewById(R.id.size_slider);
        size_bar.setOnSeekBarChangeListener(this);
        size_text= this.findViewById(R.id.size_label);
        size_text.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				do_size();
		}});
        
        handicap_bar= this.findViewById(R.id.handicap_slider);
        handicap_bar.setOnSeekBarChangeListener(this);
        handicap_text= this.findViewById(R.id.handicap_label);
        handicap_text.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				do_handicap();
		}});
        handicap_free= this.findViewById(R.id.handicap_free);

        komi_bar= this.findViewById(R.id.komi_slider);
        komi_bar.setOnSeekBarChangeListener(this);
        komi_text= this.findViewById(R.id.komi_label);
        komi_text.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				do_komi();
		}});
        
        white_name_button= findViewById(R.id.WhiteNameButton);
        white_name_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				do_white_name();
		}});
        white_name_label= this.findViewById(R.id.WhiteNameLabel);
        white_name_label.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				do_white_name();
		}});
        
        black_name_button= findViewById(R.id.BlackNameButton);
        black_name_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				do_black_name();
		}});
        black_name_label= this.findViewById(R.id.BlackNameLabel);
        black_name_label.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				do_black_name();
		}});
        
        sgf_file_button= findViewById(R.id.SGFFileButton);
        sgf_file_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				do_sgf_file();
		}});
        sgf_file_label= this.findViewById(R.id.SGFFileLabel);
        sgf_file_label.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				do_sgf_file();
		}});
        sgf_file_label.setText("game");

        refresh_ui();
     }
/*    
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferences(0); 

     }  */

    protected String getHandicapPositions() {
    	switch (m_size) {
	    	case 7:
				switch (m_handicap) {
				case 1: return ""; 
				case 2: return "AB[ce][ec]";
				case 3: return "AB[ce][ec][ee]";
				case 4: return "AB[ce][ec][ee][cc]";
				default: return ""; 
				}
    		case 9:
    			switch (m_handicap) {
    			case 1: return ""; 
    			case 2: return "AB[cg][gc]";
    			case 3: return "AB[cg][gc][gg]";
    			case 4: return "AB[cg][gc][gg][cc]";
    			case 5: return "AB[cg][gc][gg][cc][ee]";
    			default: return ""; 
    			}
    		case 11:
    			switch (m_handicap) {
    			case 1: return ""; 
    			case 2: return "AB[dh][hd]";
    			case 3: return "AB[dh][hd][hh]";
    			case 4: return "AB[dh][hd][hh][dd]";
    			case 5: return "AB[dh][hd][hh][dd][ff]";
    			case 6: return "AB[dh][hd][hh][dd][df][hf]";
    			case 7: return "AB[dh][hd][hh][dd][df][hf][ff]";
    			case 8: return "AB[dh][hd][hh][dd][df][hf][fd][fh]";
    			case 9: return "AB[dh][hd][hh][dd][df][hf][fd][fh][ff]";
    			default: return ""; 
    			}
    		case 13:
    			switch (m_handicap) {
    			case 1: return ""; 
    			case 2: return "AB[dj][jd]";
    			case 3: return "AB[dj][jd][jj]";
    			case 4: return "AB[dj][jd][jj][dd]";
    			case 5: return "AB[dj][jd][jj][dd][gg]";
    			case 6: return "AB[dj][jd][jj][dd][dg][jg]";
    			case 7: return "AB[dj][jd][jj][dd][dg][jg][gg]";
    			case 8: return "AB[dj][jd][jj][dd][dg][jg][gd][gj]";
    			case 9: return "AB[dj][jd][jj][dd][dg][jg][gd][gj][gg]";
    			default: return ""; 
    			}
    		case 15:
    			switch (m_handicap) {
    			case 1: return ""; 
    			case 2: return "AB[dl][ld]";
    			case 3: return "AB[dl][ld][ll]";
    			case 4: return "AB[dl][ld][ll][dd]";
    			case 5: return "AB[dl][ld][ll][dd][hh]";
    			case 6: return "AB[dl][ld][ll][dd][dh][lh]";
    			case 7: return "AB[dl][ld][ll][dd][dh][lh][hh]";
    			case 8: return "AB[dl][ld][ll][dd][dh][lh][hd][hl]";
    			case 9: return "AB[dl][ld][ll][dd][dh][lh][hd][hl][hh]";
    			default: return ""; 
    			}
    		case 17:
    			switch (m_handicap) {
    			case 1: return ""; 
    			case 2: return "AB[dn][nd]";
    			case 3: return "AB[dn][nd][nn]";
    			case 4: return "AB[dn][nd][nn][dd]";
    			case 5: return "AB[dn][nd][nn][dd][ii]";
    			case 6: return "AB[dn][nd][nn][dd][di][ni]";
    			case 7: return "AB[dn][nd][nn][dd][di][ni][ii]";
    			case 8: return "AB[dn][nd][nn][dd][di][ni][id][in]";
    			case 9: return "AB[dn][nd][nn][dd][di][ni][id][in][ii]";
    			default: return ""; 
    			}
    		case 19:
    			switch (m_handicap) {
    			case 1: return ""; 
    			case 2: return "AB[dp][pd]";
    			case 3: return "AB[dp][pd][pp]";
    			case 4: return "AB[dp][pd][pp][dd]";
    			case 5: return "AB[dp][pd][pp][dd][jj]";
    			case 6: return "AB[dp][pd][pp][dd][pj][dj]";
    			case 7: return "AB[dp][pd][pp][dd][pj][dj][jj]";
    			case 8: return "AB[dp][pd][pp][dd][pj][dj][jd][jp]";
    			case 9: return "AB[dp][pd][pp][dd][pj][dj][jd][jp][jj]";
    			default: return ""; 
    			}
    		case 21:
    			switch (m_handicap) {
    			case 1: return ""; 
    			case 2: return "AB[dr][rd]";
    			case 3: return "AB[dr][rd][rr]";
    			case 4: return "AB[dr][rd][rr][dd]";
    			case 5: return "AB[dr][rd][rr][dd][kk]";
    			case 6: return "AB[dr][rd][rr][dd][rk][dk]";
    			case 7: return "AB[dr][rd][rr][dd][rk][dk][kk]";
    			case 8: return "AB[dr][rd][rr][dd][rk][dk][kd][kr]";
    			case 9: return "AB[dr][rd][rr][dd][rk][dk][kd][kr][kk]";
    			default: return ""; 
    			}
    		case 23:
    			switch (m_handicap) {
    			case 1: return ""; 
    			case 2: return "AB[dt][td]";
    			case 3: return "AB[dt][td][tt]";
    			case 4: return "AB[dt][td][tt][dd]";
    			case 5: return "AB[dt][td][tt][dd][ll]";
    			case 6: return "AB[dt][td][tt][dd][tl][dl]";
    			case 7: return "AB[dt][td][tt][dd][tl][dl][ll]";
    			case 8: return "AB[dt][td][tt][dd][tl][dl][ld][lt]";
    			case 9: return "AB[dt][td][tt][dd][tl][dl][ld][lt][ll]";
    			default: return ""; 
    			}
    		case 25:
    			switch (m_handicap) {
    			case 1: return ""; 
    			case 2: return "AB[dv][vd]";
    			case 3: return "AB[dv][vd][vv]";
    			case 4: return "AB[dv][vd][vv][dd]";
    			case 5: return "AB[dv][vd][vv][dd][mm]";
    			case 6: return "AB[dv][vd][vv][dd][vm][dm]";
    			case 7: return "AB[dv][vd][vv][dd][vm][dm][mm]";
    			case 8: return "AB[dv][vd][vv][dd][vm][dm][md][mv]";
    			case 9: return "AB[dv][vd][vv][dd][vm][dm][md][mv][mm]";
    			default: return ""; 
    			}
    		default: return "";
    	}
	}

	/**
     * Any time we are paused we need to save away the current state, so it
     * will be restored correctly when we are resumed.
     */
    @Override
    protected void onPause() {
        super.onPause();

        /*
        SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.putString("com.hg.anDGS.DGSUser", DGSUser);
        editor.putString("com.hg.anDGS.DGSPass", DGSPass);
        editor.commit();
        */
    }
    
    public void refresh_ui() {
    	size_text.setText(getString(R.string.GameSize) + " " + m_size + "x" + m_size);
    	handicap_text.setText(getString(R.string.Handicap) + " " + m_handicap);
    	komi_text.setText(getString(R.string.Komi) + " " + m_komi);
    	if (size_bar.getProgress() != (m_size-m_size_offset))
    		size_bar.setProgress(m_size-m_size_offset);
    	if (handicap_bar.getProgress() != m_handicap)
    		handicap_bar.setProgress(m_handicap);
    	if (komi_bar.getProgress() != (int)(m_komi*2 + m_komi_offset))
    		komi_bar.setProgress((int) (m_komi*2 + m_komi_offset));
    	}
    
    private void limitMaxHandicapForBoard (int bSz) {
    	switch (bSz) {
		case 5: m_handicap = 0; break;
		case 6: m_handicap = 0; break;
		case 7: if (m_handicap > 4) m_handicap= 4; break;
		case 8: m_handicap = 0; break;
		case 9: if (m_handicap > 5) m_handicap= 5; break;
		case 10: m_handicap = 0; break;
		case 11: break;
		case 12: m_handicap = 0; break;
		case 13: break;
		case 14: m_handicap = 0; break;
		case 15: break;
		case 16: m_handicap = 0; break;
		case 17: break;
		case 18: m_handicap = 0; break;
		case 19: break;
		case 20: break;
		case 21: break;
		case 22: break;
		case 23: break;
		case 24: break;
		case 25: break;
		default:
		}
    }
    
    public void onProgressChanged(SeekBar sBar, int progress, boolean fromUser) {
    		if (sBar==size_bar) {
    			int s = (progress+m_size_offset);
    			if (s < MIN_GAME_SIZE) s = MIN_GAME_SIZE;
    			if (s > MAX_GAME_SIZE) s = MAX_GAME_SIZE;
    			m_size = s;
    			limitMaxHandicapForBoard(s);
    		}
    		else if (sBar==handicap_bar) {
    			if (m_handicap==progress) return;
    			m_handicap=progress;
    			limitMaxHandicapForBoard(m_size);
    		}
     		else if (sBar==komi_bar) {
     			double new_komi = ((double) (progress - m_komi_offset))/2;
     			if (m_komi == new_komi) return;
     			m_komi = new_komi;
     		}
		 
    		refresh_ui();
    		}
    
    public void onStartTrackingTouch(SeekBar sBar) {
    }
     
    public void onStopTrackingTouch(SeekBar sBar) {
     
    }
    
    public void refresh_labels() {
        white_name_label.setText(m_white_name);
        black_name_label.setText(m_black_name);
        sgf_file_label.setText(m_sgf_file);
    	}
    
    public String buildInitialSGF() {
    	String sgf;
    	sgf = "(;FF[4]GM[1]SZ["+m_size+"]KM["+m_komi+"]PB["+m_black_name+"]PW["+m_white_name+"]";
    	if (m_handicap > 1) {
    		if (handicap_free.isChecked()) {
        		sgf = sgf+"HA["+m_handicap+"]"; 			
    		} else {
        		sgf = sgf+"HA["+m_handicap+"]"+getHandicapPositions()+"PL[W])";	
    		}
    	} else {
    		sgf = sgf+"PL[B])";
    	}
    	return sgf;
    }
    
    public void do_size() { 
    	final EditText input = new EditText(ctw);
    	input.setInputType(InputType.TYPE_CLASS_NUMBER);
	    input.setText(Integer.toString(m_size));
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetValue)
	    .setMessage(R.string.GameSize)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	m_size=Integer.parseInt(input.getText().toString());
	    	refresh_ui();
	    }})
        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
               }})
	    .show();
    }
    
    public void do_handicap() { 
    	final EditText input = new EditText(ctw);
    	input.setInputType(InputType.TYPE_CLASS_NUMBER);
	    input.setText(Integer.toString(m_handicap));
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetValue)
	    .setMessage(R.string.Handicap)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	m_handicap=Integer.parseInt(input.getText().toString());
	    	refresh_ui();
	    }})
        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }})
        .show();
}
    
    public void do_komi() { 
    	final EditText input = new EditText(ctw);
    	input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
	    input.setText(Double.toString(m_komi));
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetValue)
	    .setMessage(R.string.Komi)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	m_komi=Double.parseDouble(input.getText().toString());
	    	if (m_komi > 9.5) m_komi = 9.5;
	    	if (m_komi < -9.5) m_komi = -9.5;
	    	refresh_ui();
	    }})
        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }})
	    .show();
    }

    public void do_white_name() { 
    	final EditText input = new EditText(ctw);
	    input.setText(m_white_name);
	    new AlertDialog.Builder(ctw)
        .setTitle(R.string.SetWhitePlayerName)
        .setMessage(R.string.EnterName)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                m_white_name=input.getText().toString();
                refresh_labels();
            }})
        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }})
	    .show();
    }
    
    public void do_black_name() { 
    	final EditText input = new EditText(ctw);
	    input.setText(m_black_name);
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetBlackPlayerName)
	    .setMessage(R.string.EnterName)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	m_black_name=input.getText().toString();
	        refresh_labels();
	    }})
        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }})
	    .show();
    }

	public void do_sgf_file() {
		final EditText input = new EditText(ctw);
		input.setText(m_sgf_file);
		new AlertDialog.Builder(ctw)
				.setTitle(R.string.SetSGFFile)
				.setMessage(R.string.EnterFile)
				.setView(input)
				.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						m_sgf_file = input.getText().toString();
						refresh_labels();
					}})
				.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}})
				.show();
	}

	public void do_newGame() {
    	m_sgf_full_path = commonFileStuff.getSgfDirName() + File.separator + m_sgf_file + ".sgf";
		File f = new File(m_sgf_full_path);
		if (f.exists()) {
			final TextView tv = new TextView(ctw);
	        tv.setText(m_sgf_file);
	        new AlertDialog.Builder(ctw)
	        .setTitle(R.string.FileExists)
	        .setMessage(R.string.UseOldorNew)
	        .setView(tv)
	        .setNegativeButton(R.string.Old, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            	   sgf = ""; 
	            	   start_game(); }})
	        .setPositiveButton(R.string.New, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	                   sgf = buildInitialSGF(); 
	                   start_game(); }})
                    .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }})
	        .show(); 
		} else {
			 sgf = buildInitialSGF();
			 start_game(); 
		}
	}
    
    public void start_game() {
    	final Intent sgfGameIntent = new Intent(NewGame.this,GameBoard.class);
    	sgfGameIntent.putExtra("SGF", sgf); // key/value pair, 
    	sgfGameIntent.putExtra("FILE", m_sgf_full_path);
		sgfGameIntent.putExtra("GAMESTATUS",BoardManager.GS_PLAY);
		if (handicap_free.isChecked() && m_handicap > 0) {
	    	sgfGameIntent.putExtra("GAMEACTION",BoardManager.GA_SET_HA);
			sgfGameIntent.putExtra("HANDICAP","" + m_handicap);
		} else {
	    	sgfGameIntent.putExtra("GAMEACTION",BoardManager.GA_PLAY);
			sgfGameIntent.putExtra("HANDICAP","" + m_handicap);
		}
    	sgfGameIntent.putExtra("TIMELEFT", timeLeft);
    	sgfGameIntent.putExtra("MODE", GameBoardOptions.EDIT);
    	sgfGameIntent.putExtra("AUTOPLAYPAUSE", autoPlayPause);
    	sgfGameIntent.putExtra("AUTOPLAYINTERVAL", autoPlayInterval);
     	startActivityForResult(sgfGameIntent, SGF_GAMES);
    }
	    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	Intent mIntent;
    	switch(requestCode) {
    	case HELP_VIEW:
    		break;
    	case SGF_GAMES:
    	default:
    		mIntent = new Intent();
    		setResult(RESULT_OK, mIntent);
    		finish();
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
		 final Intent helpIntent = commonStuff.helpDGS (commonStuff.HELP_NEW, this);
		 helpIntent.putExtra("BOARDLAYOUT", boardLayout);
		 startActivityForResult(helpIntent, HELP_VIEW);
	 }

}
