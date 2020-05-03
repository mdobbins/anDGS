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

public class FindUserView extends DGSActivity {

    protected static final int HELP_VIEW = 0;
    private static final int MENU_HELP = 0;

    private TextView tmHelp;

    private LinearLayout toUserIdLabel;
    private TextView toUserIdEdit;
    private LinearLayout toUserNameLabel;
    private TextView toUserNameEdit;
    private TextView send_button;

    private String boardLayout;
    private String theme;
    private ContextThemeWrapper ctw;
    private CommonStuff commonStuff = new CommonStuff();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boardLayout = extras.getString("BOARDLAYOUT");
        }

        SharedPreferences prefs = getSharedPreferences("MainDGS", 0);
        theme = prefs.getString("com.hg.anDGS.Theme", PrefsDGS.DEFAULT_THEME);

        if (boardLayout == null) {
            boardLayout = PrefsDGS.PORTRAIT;
        }
/*
        if (boardLayout.contains(PrefsDGS.LANDSCAPE)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
*/
        this.setTheme(commonStuff.getCommonStyle(theme));
        setContentView(R.layout.finduser);
        ctw = new ContextThemeWrapper(this, commonStuff.getCommonTheme(theme));

        tmHelp = (TextView) findViewById(R.id.finduserTMHelp);
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

        toUserIdEdit = (TextView) findViewById(R.id.finduserToUserIdEdit);
        toUserIdLabel = (LinearLayout) findViewById(R.id.finduserToUserIdLabel);
        toUserIdLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doSetToUserId();
            }
        });

        toUserNameEdit = (TextView) findViewById(R.id.finduserToUserNameEdit);
        toUserNameLabel = (LinearLayout) findViewById(R.id.finduserToUserNameLabel);
        toUserNameLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doSetToUserName();
            }
        });

        send_button = (TextView) findViewById(R.id.finduserSendButton);
        send_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();
                sb.append("users.php");
                String toField = toUserIdEdit.getText().toString().trim();
                if (!toField.contentEquals("")) {
                    sb.append("?user=").append(commonStuff.encodeIt(toField));
                } else {
                    toField = toUserNameEdit.getText().toString().trim();
                    if (!toField.contentEquals("")) {
                        sb.append("?name=").append(commonStuff.encodeIt(toField));
                    }  // otherwise all users
                }

                Bundle rslts = new Bundle();
                rslts.putString("FindString", sb.toString());
                Intent mIntent = new Intent();
                mIntent.putExtras(rslts);
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });

        toUserIdEdit.setText("");
        toUserNameEdit.setText("");
    }

    private void doSetToUserId() {
        final EditText input = new EditText(ctw);
        input.setText(toUserIdEdit.getText());
        new AlertDialog.Builder(ctw)
                .setTitle(R.string.UserId)
                .setMessage("")
                .setView(input)
                .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        toUserIdEdit.setText(input.getText());
                    }
                })
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }})
                .show();
    }

    private void doSetToUserName() {
        final EditText input = new EditText(ctw);
        input.setText(toUserNameEdit.getText());
        new AlertDialog.Builder(ctw)
                .setTitle("To User Name, * wildcard")
                .setMessage("")
                .setView(input)
                .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        toUserNameEdit.setText(input.getText());
                    }
                })
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
        final Intent helpIntent = commonStuff.helpDGS(commonStuff.HELP_FINDUSER, this);
        helpIntent.putExtra("BOARDLAYOUT", boardLayout);
        startActivityForResult(helpIntent, HELP_VIEW);
    }

}
