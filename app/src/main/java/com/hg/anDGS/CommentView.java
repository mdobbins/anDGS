package com.hg.anDGS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class CommentView extends DGSActivity {
	private TextView comment_save_button;
	private String boardLayout = PrefsDGS.PORTRAIT;
	private String theme;
	private EditText comment_text_view;
	private String original_text = "";
	private final CommonStuff commonStuff = new CommonStuff();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			original_text = extras.getString("CommentText");
			boardLayout = extras.getString("BOARDLAYOUT");
			if (original_text == null) original_text = "";
		}

		SharedPreferences prefs = getSharedPreferences("MainDGS", 0);
		theme = prefs.getString("com.hg.anDGS.Theme", PrefsDGS.DEFAULT_THEME);
/*
		if (boardLayout.contains(PrefsDGS.LANDSCAPE)) {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
*/
		this.setTheme(commonStuff.getCommonStyle(theme));
		setContentView(R.layout.comment_edit);

		comment_text_view = findViewById(R.id.commentEditView);

		comment_save_button = findViewById(R.id.commentSaveButton);
		comment_save_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				exitActions();
			}
		});
		
		comment_text_view.setText(original_text);
	}

	private void exitActions() {
		Bundle rslts = new Bundle();
		String s = comment_text_view.getText().toString();
		if (!s.contentEquals(original_text)) {
			rslts.putString("CommentText",s);
			Intent mIntent = new Intent();
			mIntent.putExtras(rslts);
			setResult(RESULT_OK, mIntent);
		} else {
			Intent mIntent = new Intent();
			mIntent.putExtras(rslts);
			setResult(RESULT_CANCELED, mIntent);
		}
		finish();
	}
}

