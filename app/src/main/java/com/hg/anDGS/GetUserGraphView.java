package com.hg.anDGS;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class GetUserGraphView extends DGSActivity {
	
	protected static final int HELP_VIEW = 0;
	private static final int MENU_HELP = 0;
	
	private TextView tmHelp;

	private LinearLayout toLabel;
	private LinearLayout startLabel;
	private LinearLayout endLabel;
	private TextView toTextEdit;
	private TextView toUserIDedit;
	private TextView startMonth;
	private TextView startYear;
	private TextView endMonth;
	private TextView endYear;
	private TextView send_button;
	
	private String boardLayout;
	private String theme;
	private String regDate = "";
	private String toUserId = "";
	private String toUid = "";
	private ContextThemeWrapper ctw;
	private CommonStuff commonStuff = new CommonStuff();

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	toUid = extras.getString("TOUID");
        	toUserId = extras.getString("TOUSERID");
        	regDate = extras.getString("REGISTRATIONDATE");
        	boardLayout = extras.getString("BOARDLAYOUT");
        }
        
        SharedPreferences prefs = getSharedPreferences("MainDGS", 0);
		theme = prefs.getString("com.hg.anDGS.Theme", PrefsDGS.DEFAULT_THEME);

        if (boardLayout == null) {
			boardLayout = PrefsDGS.PORTRAIT;
		}
        if (toUserId == null) {
        	toUserId = "";
		}
        if (toUid == null) {
        	toUid = "";
		}
        if (regDate == null) {
        	regDate = "";
		}
/*
        if (boardLayout.contains(PrefsDGS.LANDSCAPE)) {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
*/
        this.setTheme(commonStuff.getCommonStyle(theme));
        setContentView(R.layout.getusergraph);
		ctw = new ContextThemeWrapper(this, commonStuff.getCommonTheme(theme));
        
		tmHelp = (TextView) findViewById(R.id.gugTMHelp);
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

		toTextEdit = (TextView) findViewById(R.id.gugToTextLabel);
		toUserIDedit = (TextView) findViewById(R.id.gugUserId);
    	toLabel = (LinearLayout) findViewById(R.id.gugToLabel);
    	toLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetTo();
            }
        });

		startLabel = (LinearLayout) findViewById(R.id.gugStartLabel);
		startLabel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doSetStartMonth();
			}
		});
    	
    	startMonth = (TextView) findViewById(R.id.gugStartDateMonth);

		startYear = (TextView) findViewById(R.id.gugStartDateYear);

		endLabel = (LinearLayout) findViewById(R.id.gugEndLabel);
		endLabel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doSetEndMonth();
			}
		});

		endMonth = (TextView) findViewById(R.id.gugEndDateMonth);

		endYear = (TextView) findViewById(R.id.gugEndDateYear);

    	send_button = (TextView) findViewById(R.id.gugSendButton);
    	send_button.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	StringBuilder sb = new StringBuilder();
            	String toField = toUserIDedit.getText().toString().trim();
				sb.append("ratinggraph.php?");
				if (toUserId.contentEquals("") && toField.contentEquals("")) {
					if (toUid.contentEquals("")) {
						Toast.makeText(ctw, "Need To field!", Toast.LENGTH_LONG).show();
						return;
					} else {
						sb.append("uid=").append(commonStuff.encodeIt(toField));
					}
				} else {
					sb.append("user=").append(commonStuff.encodeIt(toField));
				}
                sb.append("&startyear=").append(commonStuff.encodeIt(startYear.getText().toString().trim()));
                sb.append("&startmonth=").append(commonStuff.encodeIt(startMonth.getText().toString().trim()));
                sb.append("&endyear=").append(commonStuff.encodeIt(endYear.getText().toString().trim()));
                sb.append("&endmonth=").append(commonStuff.encodeIt(endMonth.getText().toString().trim()));
             	Bundle rslts = new Bundle();
             	rslts.putString("MessageString",sb.toString()); 
             	Intent mIntent = new Intent();
                mIntent.putExtras(rslts);
                setResult(RESULT_OK, mIntent);
                finish();
             }
         });
    	
    	if (!toUserId.contentEquals("") || toUid.contentEquals("")) {
			toUserIDedit.setText(toUserId);
    	} else {
			toTextEdit.setText(R.string.Uid);
			toUserIDedit.setText(toUid);
    	}
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		endYear.setText(Integer.toString(year));
		endMonth.setText(Integer.toString(month));
		if (regDate.contentEquals("")) {
			startYear.setText(Integer.toString(year-1));
			startMonth.setText(Integer.toString(month));
		} else {
			String[] e = regDate.split("-");
			int sYear = Integer.parseInt(e[0]);
			if (year - sYear > 5) {
				startYear.setText(Integer.toString(year-5));
			} else {
				startYear.setText(e[0]);
			}
			startMonth.setText(e[1]);
		}
	}

	private void doSetTo() { 
		if (!toUserId.contentEquals("") || toUid.contentEquals("")) {
    		doSetToUserId();
    	} else {
    		doSetToUid();
    	}
    }

	private void doSetToUid() { 
    	final EditText input = new EditText(ctw);
	    input.setText(toUserIDedit.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.Uid)
	    .setMessage("")
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
			toUserIDedit.setText(input.getText());
	    }})
        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }})
	    .show();
    }
	
	private void doSetToUserId() { 
    	final EditText input = new EditText(ctw);
	    input.setText(toUserIDedit.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.UserId)
	    .setMessage("")
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
			toUserIDedit.setText(input.getText());
	    }})
        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }})
	    .show();
    }
	
	private void doSetStartMonth() {
		final EditText input = new EditText(ctw);
		input.setText(startMonth.getText());
		new AlertDialog.Builder(ctw)
				.setTitle(R.string.startDate)
				.setMessage("Month")
				.setView(input)
				.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						startMonth.setText(input.getText());
						doSetStartYear();
					}})
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }})
				.show();
	}

	private void doSetStartYear() {
		final EditText input = new EditText(ctw);
		input.setText(startYear.getText());
		new AlertDialog.Builder(ctw)
				.setTitle(R.string.startDate)
				.setMessage("Year")
				.setView(input)
				.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						startYear.setText(input.getText());
					}})
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }})
				.show();
	}

	private void doSetEndMonth() {
		final EditText input = new EditText(ctw);
		input.setText(endMonth.getText());
		new AlertDialog.Builder(ctw)
				.setTitle(R.string.endDate)
				.setMessage("Month")
				.setView(input)
				.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						endMonth.setText(input.getText());
						doSetEndYear();
					}})
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }})
				.show();
	}

	private void doSetEndYear() {
		final EditText input = new EditText(ctw);
		input.setText(endYear.getText());
		new AlertDialog.Builder(ctw)
				.setTitle(R.string.endDate)
				.setMessage("Year")
				.setView(input)
				.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						endYear.setText(input.getText());
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
		final Intent helpIntent = commonStuff.helpDGS (commonStuff.HELP_GETGRAPH, this);
		helpIntent.putExtra("BOARDLAYOUT", boardLayout);
		startActivityForResult(helpIntent, HELP_VIEW);   // TODO  new help text
	 }
	 
}
