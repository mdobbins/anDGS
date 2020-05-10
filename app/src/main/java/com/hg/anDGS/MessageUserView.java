package com.hg.anDGS;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MessageUserView extends DGSActivity {
	
	protected static final int HELP_VIEW = 0;
	private static final int MENU_HELP = 0;
	
	private TextView tmHelp;

	private LinearLayout toLabel;
	private TextView toEdit;
	private TextView toTextEdit;
	private LinearLayout subjectLabel;
	private TextView subjectEdit;
	private LinearLayout messageLabel;
	private TextView messageEdit;
	private TextView send_button;
	
	private String boardLayout;
	private String theme;
	private String forMsgId = "0";
	private String oldSubject = "";
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
        	forMsgId = extras.getString("FORMSGID");
        	oldSubject = extras.getString("OLDSUBJECT");
        	boardLayout = extras.getString("BOARDLAYOUT");
        }
        
        SharedPreferences prefs = getSharedPreferences("MainDGS", 0);
		theme = prefs.getString("com.hg.anDGS.Theme", PrefsDGS.DEFAULT_THEME);

        if (boardLayout == null) {
			boardLayout = PrefsDGS.PORTRAIT;
		}
        if (forMsgId == null) {
        	forMsgId = "";
		}
        if (toUserId == null) {
        	toUserId = "";
		}
        if (toUid == null) {
        	toUid = "";
		}
        if (oldSubject == null) {
        	oldSubject = "";
		}
/*
        if (boardLayout.contains(PrefsDGS.LANDSCAPE)) {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
 */
        this.setTheme(commonStuff.getCommonStyle(theme));
        setContentView(R.layout.message);
		ctw = new ContextThemeWrapper(this, commonStuff.getCommonTheme(theme));
        
		tmHelp = findViewById(R.id.messageTMHelp);
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

        toTextEdit = findViewById(R.id.messageToTextLabel);
    	toEdit = findViewById(R.id.messageToEdit);
    	toLabel = findViewById(R.id.messageToLabel);
    	toLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetTo();
            }
        });
    	
    	subjectEdit = findViewById(R.id.messageSubjectEdit);
    	subjectLabel = findViewById(R.id.messageSubjectLabel);
    	subjectLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetSubject();
            }
        });
    	
    	messageEdit = findViewById(R.id.messageMessageEdit);
    	messageLabel = findViewById(R.id.messageMessageLabel);
    	messageLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	doSetMessage();
            }
        });
    	
    	send_button = findViewById(R.id.messageSendButton);
    	send_button.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	StringBuilder sb = new StringBuilder();
            	String toField = toEdit.getText().toString().trim();
            	if (forMsgId.contentEquals("")) {
            		if (toUserId.contentEquals("") && toField.contentEquals("")) {
            			if (toUid.contentEquals("")) {
            				Toast.makeText(ctw, "Need To field!", Toast.LENGTH_LONG).show();
            				return;
            			} else {
            				sb.append("&ouid=").append(toField);
            			}
    				} else {
    					sb.append("&ouser=").append(toField);
    				}
            	} else {
            		sb.append("&mid=").append(forMsgId.trim());
            		}
 
            	sb.append("&subj=").append(subjectEdit.getText().toString().trim());
            	sb.append("&msg=").append(messageEdit.getText().toString().trim());
             	Bundle rslts = new Bundle();
             	rslts.putString("MessageString",sb.toString()); 
             	Intent mIntent = new Intent();
                mIntent.putExtras(rslts);
                setResult(RESULT_OK, mIntent);
                finish();
             }
         });
    	
    	if (!forMsgId.contentEquals("")) {
    		toTextEdit.setText(R.string.messageId);
    		toEdit.setText(forMsgId);
    	} else if (!toUserId.contentEquals("") || toUid.contentEquals("")) {
    		toEdit.setText(toUserId);
    	} else {
    		toTextEdit.setText(R.string.Uid);
    		toEdit.setText(toUid);
    	}
    	subjectEdit.setText(oldSubject);   	
	}

	private void doSetTo() { 
		if (!forMsgId.contentEquals("")) {
			// can't edit message number for reply
    	} else if (!toUserId.contentEquals("") || toUid.contentEquals("")) {
    		doSetToUserId();
    	} else {
    		doSetToUid();
    	}
    }

	private void doSetToUid() { 
    	final EditText input = new EditText(ctw);
	    input.setText(toEdit.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.Uid)
	    .setMessage("")
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	toEdit.setText(input.getText());
	    }})
        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }})
	    .show();
    }
	
	private void doSetToUserId() { 
    	final EditText input = new EditText(ctw);
	    input.setText(toEdit.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.UserId)
	    .setMessage("")
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	toEdit.setText(input.getText());
	    }})
        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }})
	    .show();
    }
	
	private void doSetSubject() { 
    	final EditText input = new EditText(ctw);
	    input.setText(subjectEdit.getText());
	    new AlertDialog.Builder(ctw)
	    .setTitle(R.string.SetPreference)
	    .setMessage(R.string.Subject)
	    .setView(input)
	    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int whichButton) {
	    	subjectEdit.setText(input.getText());
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
		 if (item.getItemId() == MENU_HELP) {
			 doHelp();
		 }
		 return false;
	 }
	 
	 private void doHelp() {
		final Intent helpIntent = commonStuff.helpDGS (commonStuff.HELP_SEND_MSG, this);
		helpIntent.putExtra("BOARDLAYOUT", boardLayout);
		startActivityForResult(helpIntent, HELP_VIEW);   
	 }
	 
}
