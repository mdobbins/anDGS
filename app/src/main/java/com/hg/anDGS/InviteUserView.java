package com.hg.anDGS;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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

public class InviteUserView extends DGSActivity {
	
	protected static final int HELP_VIEW = 0;
	private static final int MENU_HELP = 0;

	private final String DEFAULT_FISCHERTIME = "1";
	private final String DEFAULT_MAINTIME = "1";
	private final String DEFAULT_KOMI = "6.5";
	
	private TextView tmHelp;

	private LinearLayout rules_label;
	private Spinner rules_spinner;
	private LinearLayout board_size_label;
	private Spinner board_size_spinner;
	private LinearLayout game_type_label;
	private Spinner game_type_spinner;
	private LinearLayout color_label;
	private Spinner color_spinner;
	private LinearLayout handicap_label;
	private Spinner handicap_spinner;
	private LinearLayout komiLabel;
	private TextView komiEdit;
	private LinearLayout mainTimeLabel;
	private TextView mainTimeEdit;
	private LinearLayout maintime_units_label;
	private Spinner maintime_units_spinner;
	private LinearLayout fischerTimeLabel;
	private TextView fischerTimeEdit;
	private LinearLayout fischertime_units_label;
	private Spinner fischertime_units_spinner;
	private LinearLayout weekendClock_label;
	private CheckBox weekendClockCB;
	private LinearLayout ratedGame_label;
	private CheckBox ratedGameCB;
	private LinearLayout toUserIdLabel;
	private TextView toUserIdEdit;
	private LinearLayout messageLabel;
	private TextView messageEdit;
	private TextView submit_button;
	
	private String boardLayout;
	private String theme;
	private String fromUserId;
	private String toUserId;
	private boolean weekendClock = true;
	private boolean ratedGame = true;
	private ContextThemeWrapper ctw;
	private CommonStuff commonStuff = new CommonStuff();

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	fromUserId = extras.getString("MYUSERID");
        	toUserId = extras.getString("TOUSERID");
        	boardLayout = extras.getString("BOARDLAYOUT");
        }
        
        SharedPreferences prefs = getSharedPreferences("MainDGS", 0);
		theme = prefs.getString("com.hg.anDGS.Theme", PrefsDGS.DEFAULT_THEME);

        if (boardLayout == null) {
			boardLayout = PrefsDGS.PORTRAIT;
		}
        if (fromUserId == null) {
        	fromUserId = "";
		}
        if (toUserId == null) {
        	toUserId = "";
		}
/*
        if (boardLayout.contains(PrefsDGS.LANDSCAPE)) {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
*/
        this.setTheme(commonStuff.getCommonStyle(theme));
        setContentView(R.layout.invite);
		ctw = new ContextThemeWrapper(this, commonStuff.getCommonTheme(theme));
        
		tmHelp = (TextView) findViewById(R.id.inviteTMHelp);
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

		String[] rules_items = new String[] {getString(R.string.Japanese), getString(R.string.Chinese)};
		rules_spinner = (Spinner) findViewById(R.id.inviteRulesSpinner);
		ArrayAdapter<String> rules_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, rules_items);
		rules_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		rules_spinner.setAdapter(rules_adapter);
		rules_label = (LinearLayout) findViewById(R.id.inviteRulesLabel);
		rules_label.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doSetRules();
			}
		});

        String[] boardsize_items = new String[] {"7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25"};
        board_size_spinner = (Spinner) findViewById(R.id.inviteBoardSizeSpinner);
        ArrayAdapter<String> boardsize_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, boardsize_items);
        boardsize_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        board_size_spinner.setAdapter(boardsize_adapter);
        board_size_label = (LinearLayout) findViewById(R.id.inviteBoardSizeLabel);
        board_size_label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetBoardSize();
            }
        });
        
    	String[] gametype_items = new String[] {getString(R.string.conventionalGame), getString(R.string.properGame), getString(R.string.manualGame)};
    	game_type_spinner = (Spinner) findViewById(R.id.inviteGameTypeSpinner);
        ArrayAdapter<String> gametype_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, gametype_items);
        gametype_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        game_type_spinner.setAdapter(gametype_adapter);
        game_type_label = (LinearLayout) findViewById(R.id.inviteGameTypeLabel);
        game_type_label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetGameType();
            }
        });
    	
        String[] color_items = new String[] {getString(R.string.evenGame), getString(R.string.doubleGame), getString(R.string.white), getString(R.string.black)};
    	color_spinner = (Spinner) findViewById(R.id.inviteColorSpinner);
        ArrayAdapter<String> color_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, color_items);
        color_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        color_spinner.setAdapter(color_adapter);
        color_label = (LinearLayout) findViewById(R.id.inviteColorLabel);
        color_label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetColor();
            }
        });
    	
    	String[] handicap_items = new String[] {"0","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21"};
    	handicap_spinner = (Spinner) findViewById(R.id.inviteHandicapSpinner);
        ArrayAdapter<String> handicap_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, handicap_items);
        handicap_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        handicap_spinner.setAdapter(handicap_adapter);
        handicap_label = (LinearLayout) findViewById(R.id.inviteHandicapLabel);
        handicap_label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetHandicap();
            }
        });
    	
    	komiEdit = (TextView) findViewById(R.id.inviteKomiEdit);
    	komiLabel = (LinearLayout) findViewById(R.id.inviteKomiLabel);
    	komiLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetKomi();
            }
        });
    	
    	mainTimeEdit = (TextView) findViewById(R.id.inviteMainTimeEdit);
    	mainTimeLabel = (LinearLayout) findViewById(R.id.inviteMainTimeLabel);
    	mainTimeLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetMainTime();
            }
        });
    	
    	String[] maintime_units_items = new String[] {getString(R.string.hours), getString(R.string.days), getString(R.string.months)};
    	maintime_units_spinner = (Spinner) findViewById(R.id.inviteMainTimeUnitsSpinner);
        ArrayAdapter<String> maintime_units_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, maintime_units_items);
        maintime_units_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maintime_units_spinner.setAdapter(maintime_units_adapter);
        maintime_units_label = (LinearLayout) findViewById(R.id.inviteMainTimeUnitsLabel);
        maintime_units_label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetMainTimeUnits();
            }
        });
    	
    	fischerTimeEdit = (TextView) findViewById(R.id.inviteFischerTimeEdit);
    	fischerTimeLabel = (LinearLayout) findViewById(R.id.inviteFischerTimeLabel);
    	fischerTimeLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetFischerTime();
            }
        });
    	
    	String[] fischertime_units_items = new String[] {getString(R.string.hours), getString(R.string.days), getString(R.string.months)};
    	fischertime_units_spinner = (Spinner) findViewById(R.id.inviteFisherTimeUnitsSpinner);
        ArrayAdapter<String> fischertime_units_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, fischertime_units_items);
        fischertime_units_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fischertime_units_spinner.setAdapter(fischertime_units_adapter);
        fischertime_units_label = (LinearLayout) findViewById(R.id.inviteFischerTimeUnitsLabel);
        fischertime_units_label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetFischerTimeUnits();
            }
        });
    	
    	weekendClockCB = (CheckBox) findViewById(R.id.inviteWeekendClock);
    	weekendClock_label = (LinearLayout) findViewById(R.id.inviteWeekendClockLabel);
    	weekendClock_label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetWeekendClock();
            }
        });
    	
    	ratedGameCB = (CheckBox) findViewById(R.id.inviteRatedGame);
    	ratedGame_label = (LinearLayout) findViewById(R.id.inviteRatedGameLabel);
    	ratedGame_label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetRatedGame();
            }
        });
    	
    	toUserIdEdit = (TextView) findViewById(R.id.inviteToUserIdEdit);
    	toUserIdLabel = (LinearLayout) findViewById(R.id.inviteToUserIdLabel);
    	toUserIdLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetToUserId();
            }
        });
    	
    	messageEdit = (TextView) findViewById(R.id.inviteMessageEdit);
    	messageLabel = (LinearLayout) findViewById(R.id.inviteMessageLabel);
    	messageLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetMessage();
            }
        });
    	
    	submit_button = (TextView) findViewById(R.id.inviteSubmitButton);
    	submit_button.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	int i;
            	StringBuilder sb = new StringBuilder();
/* for quick suite
				sb.append("&to_user=");
				sb.append(toUserIdEdit.getText().toString().trim());
				sb.append("&message=");
				sb.append(commonStuff.encodeIt(messageEdit.getText().toString().trim()));
*/
// screen scrape version

				sb.append("send_message=Send+Invitation&mode=Invite&mid=0&view=0&gsc=1");
				sb.append("&to=");
				sb.append(toUserIdEdit.getText().toString().trim());
				sb.append("&senderuser=");   // this could be senderid
				sb.append(fromUserId);
				sb.append("&message=");
				sb.append(commonStuff.encodeIt(messageEdit.getText().toString().trim()));
				sb.append("&subject=Game+invitation&type=INVITATION");

				sb.append("&ruleset=");
				 i = (int) rules_spinner.getSelectedItemId();
				 switch (i) {
					 case 0: sb.append("JAPANESE"); break;
					 case 1: sb.append("CHINESE"); break;
					 default: sb.append("JAPANESE");
				 }
				sb.append("&size=");
            	sb.append((String) board_size_spinner.getSelectedItem());
				 sb.append("&cat_htype=");   // game type
				 i = (int) game_type_spinner.getSelectedItemId();
				 switch (i) {
					 case 0: sb.append("conv"); break;
					 case 1: sb.append("proper"); break;
					 case 2: sb.append("manual"); break;
					 default: sb.append("manual");
				 }
            	sb.append("&color_m=");
            	i = (int) color_spinner.getSelectedItemId();
            	switch (i) {
					case 0: sb.append("nigiri"); break;   // string evenGame
					case 1: sb.append("double"); break;
					case 2: sb.append("white"); break;
					case 3: sb.append("black"); break;
					default: sb.append("double");
            	}
            	sb.append("&handicap_m=");
            	sb.append((String) handicap_spinner.getSelectedItem());
            	sb.append("&komi_m=");
            	sb.append(komiEdit.getText().toString().trim());
            	sb.append("&fk_htype=auko_opn");

            	sb.append("&stdhandicap=Y&adj_handicap=0&min_handicap=0&max_handicap=-1&adj_komi=0&jigo_mode=KEEP_KOMI");
            	sb.append("&timevalue=");
            	sb.append(mainTimeEdit.getText().toString().trim());
            	sb.append("&timeunit=");
            	i = (int) maintime_units_spinner.getSelectedItemId();
            	switch (i) {
            	case 0: sb.append("hours"); break;
            	case 1: sb.append("days"); break;
            	case 2: sb.append("months"); break;
            	default: sb.append("months");
            	}
            	sb.append("&byotimevalue_jap=1&timeunit_jap=days&byoperiods_jap=10&byotimevalue_can=15&timeunit_can=days&byoperiods_can=15");
            	sb.append("&byoyomitype=FIS");
            	sb.append("&byotimevalue_fis=");
            	sb.append(fischerTimeEdit.getText().toString().trim());
            	sb.append("&timeunit_fis=");
            	i = (int) fischertime_units_spinner.getSelectedItemId();
            	switch (i) {
            	case 0: sb.append("hours"); break;
            	case 1: sb.append("days"); break;
            	case 2: sb.append("months"); break;
            	default: sb.append("months");
            	}
            	sb.append("&weekendclock=");
            	if (weekendClock) {
            		sb.append("Y");
            	} else {
            		sb.append("N");
            	}
            	sb.append("&rated=");
            	if (ratedGame) {
            		sb.append("Y");
            	} else {
            		sb.append("N");
            	}
             	
             	Bundle rslts = new Bundle();
             	rslts.putString("InviteString",sb.toString()); 
             	Intent mIntent = new Intent();
                mIntent.putExtras(rslts);
                setResult(RESULT_OK, mIntent);
                finish();
             }
         });
    	
    	toUserIdEdit.setText(toUserId);
		rules_spinner.setSelection(0);  // Japanese
    	board_size_spinner.setSelection(12); // 7 - 25 i.e. 19-7=12 
    	game_type_spinner.setSelection(2);  // Domanual
    	color_spinner.setSelection(1);   // double
    	handicap_spinner.setSelection(0);  // 0
		komiEdit.setText(DEFAULT_KOMI);  // 6.5
    	maintime_units_spinner.setSelection(2); // Months
		mainTimeEdit.setText(DEFAULT_MAINTIME);  // 1 month
    	fischertime_units_spinner.setSelection(1); // Days
		fischerTimeEdit.setText(DEFAULT_FISCHERTIME);  // 1 day
    	weekendClockCB.setChecked(weekendClock);
    	ratedGameCB.setChecked(ratedGame);
	}

	private void doSetRules() {
		rules_spinner.performClick();
	}
	
	private void doSetBoardSize() {
		board_size_spinner.performClick();
	}
	
	private void doSetGameType() {
		game_type_spinner.performClick();
	}
	
	private void doSetColor() {
		color_spinner.performClick();
	}
	
	private void doSetHandicap() {
		handicap_spinner.performClick();
	}
	
	private void doSetMainTimeUnits() {
		maintime_units_spinner.performClick();
	}
	
	private void doSetFischerTimeUnits() {
		fischertime_units_spinner.performClick();
	}
	
	private void doSetRatedGame() {
		ratedGame = !ratedGameCB.isChecked();
    	ratedGameCB.setChecked(ratedGame);
	}
	
	private void doSetWeekendClock() {
		weekendClock = !weekendClockCB.isChecked();
    	weekendClockCB.setChecked(weekendClock);
	}
	
	private void doSetKomi() { 
    	final EditText input = new EditText(ctw);
	    input.setText(komiEdit.getText());
	    input.setInputType(InputType.TYPE_CLASS_NUMBER);
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.specifyKomi)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	komiEdit.setText(input.getText());
	    }})
        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }})
	    .show();
    }
	
	private void doSetMainTime() { 
    	final EditText input = new EditText(ctw);
	    input.setText(mainTimeEdit.getText());
	    input.setInputType(InputType.TYPE_CLASS_NUMBER);
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.specifyMainTime)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	mainTimeEdit.setText(input.getText());
	    }})
        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }})
	    .show();
    }
	
	private void doSetFischerTime() { 
    	final EditText input = new EditText(ctw);
	    input.setText(fischerTimeEdit.getText());
	    input.setInputType(InputType.TYPE_CLASS_NUMBER);
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.specifyFischerTime)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	fischerTimeEdit.setText(input.getText());
	    }})
        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }})
	    .show();
    }
	
	private void doSetToUserId() { 
    	final EditText input = new EditText(ctw);
	    input.setText(toUserIdEdit.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.UserId)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	toUserIdEdit.setText(input.getText());
	    }})
        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }})
	    .show();
    }
	
	private void doSetMessage() { 
    	final EditText input = new EditText(ctw);
	    input.setText(messageEdit.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.message)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	messageEdit.setText(input.getText());
	    }})
        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }})
	    .show();
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
		final Intent helpIntent = commonStuff.helpDGS (commonStuff.HELP_SEND_INVITE, this);
		helpIntent.putExtra("BOARDLAYOUT", boardLayout);
		startActivityForResult(helpIntent, HELP_VIEW);
	 }

}
