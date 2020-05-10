package com.hg.anDGS;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/*
 * The board is accessed with 0,0 in display upper-left and n,n in display lower-right
 */

class BoardTable {

	private TableLayout tableView;
	private int label_gravity = Gravity.TOP|Gravity.CENTER_HORIZONTAL;
	private TableLayout.LayoutParams tableRowParams;
	private TableRow.LayoutParams tableRowViewParams;
	private int boardSize;
	private int stoneSize;
	private int tableSize;
	private float textSize;
	private boolean fullBoard;
	private boolean boardCoord;
	private int controlType;
	private ContextThemeWrapper ctw;
	private CommonStuff commonStuff = new CommonStuff();

	private BoardClick bClick;
	private String [] coord_H = {"a","b","c","d","e","f","g","h","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	private String [] coord_V = {"25","24","23","22","21","20","19","18","17","16","15","14","13","12","11","10","9","8","7","6","5","4","3","2","1"};
	
	// create a 2 dimensional table 
	// y is which row
	// x is which column
	BoardTable(int bSize, int sSize, float tSize, boolean f_brd, boolean b_coord, int ctrl_typ, ContextThemeWrapper ct, BoardClick bc) {
		boardSize = bSize;
		stoneSize = sSize;
		textSize = commonStuff.convertFromDp(tSize,ct);
		fullBoard = f_brd;
		boardCoord = b_coord;
		controlType = ctrl_typ;
		ctw = ct;
		bClick = bc;
		tableSize = boardSize * stoneSize;
		tableRowViewParams = new TableRow.LayoutParams(stoneSize,stoneSize);
		tableRowParams = new TableLayout.LayoutParams(tableSize,stoneSize);
		tableView = new TableLayout(ctw);
		tableView.setLayoutParams(new TableLayout.LayoutParams(tableSize, tableSize));
		tableView.setBackgroundColor(Color.TRANSPARENT);
		for (int i=0; i<boardSize; i++) {
			TableRow tableRow = new TableRow(ctw);
			tableRow.setBackgroundColor(Color.TRANSPARENT);
			tableView.addView(tableRow,tableRowParams);
			for (int j=0; j<boardSize; j++) {
				TextView textView = getNewTextView();
				tableRow.addView(textView,tableRowViewParams);
			}
		}
		
		if (boardCoord) {
			for (int i = 0; i < boardSize-1; ++i) {
				// treat coordinates as displayed over white
				setLocationText(i, boardSize-1, true, coord_H[i]);   // bottom row
				setLocationText(boardSize-1, i, true, coord_V[(25-boardSize)+i+1]);  // right column
			}
		}
	}
	
	TableLayout getTableView() {
		return tableView;
	}
	
	void clearBoardText(int bSiz) {
		for (int x = 0; x < bSiz; ++x) { 
			for (int y = 0; y < bSiz; ++y) {
				setLocationText(x, y, true, "");
			}
		}
	}
	
	int getLocationGraphic(int x, int y) {
		if (x < 0 || !(x < boardSize) || y < 0 || !(y < boardSize)) {
			return BoardManager.BLNK;
		}
		TextView textView = getLocationTextView(x, y);
		return Integer.parseInt((String) textView.getTag());
	}
	
	void setLocationGraphic(int x, int y, int g) {
		if (x < 0 || !(x < boardSize) || y < 0 || !(y < boardSize)) {
			return;
		}
		TextView textView = getLocationTextView(x, y);
		textView.setTag("0"+g);
		textView.setBackgroundResource(BoardManager.getGraphic(g));
	}
	
	String getLocationText(int x, int y) {
		if (x < 0 || !(x < boardSize) || y < 0 || !(y < boardSize)) {
			return "";
		}
		TextView textView = getLocationTextView(x, y);
		return textView.getText().toString();
	}
	
	void setLocationText(int x, int y, boolean isWhiteGraphic, String s) {
		if (x < 0 || !(x < boardSize) || y < 0 || !(y < boardSize)) {
			return;
		}
		TextView textView = getLocationTextView(x, y);
		if (s != null) {
			if (s.contentEquals("")) {
				textView.setTextColor(MainDGS.label_color_m);
			} else {
				if (isWhiteGraphic) {
					textView.setTextColor(MainDGS.label_color_d);
				} else {
					textView.setTextColor(MainDGS.label_color_l);
				}
				if (s.length() > 2) {
						s = s.substring(0, 2);
					}
			}
			textView.setText(s);
		} else {
			textView.setTextColor(MainDGS.label_color_m);
			textView.setText("");
		}
	}
	
	private TextView getLocationTextView(int x, int y) {
		TableRow tableRow = (TableRow) tableView.getChildAt(y);
		return (TextView) tableRow.getChildAt(x);
	}
	
	// create a TextView for a board position
	// initialize to blank/empty
	private TextView getNewTextView() {
		String txt = "";
		TextView textView = new TextView(ctw);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
		textView.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
		textView.setIncludeFontPadding(true);
		textView.setGravity(label_gravity);
		textView.setText(txt);
		textView.setTextColor(MainDGS.label_color_m);
		textView.setBackgroundColor(Color.TRANSPARENT);
		textView.setBackgroundResource(BoardManager.getGraphic(BoardManager.BLNK));
		textView.setTag("0"+BoardManager.BLNK);
		if (fullBoard) {
			switch (controlType) {
			case BoardManager.ZOOM_CONTROL:
			case BoardManager.ONE_TOUCH_CONTROL:
				textView.setOnClickListener(clickFull);
				break;
			case BoardManager.D_PAD_CONTROL:
				textView.setOnClickListener(clickFull);
				textView.setFocusable(true);
				textView.setFocusableInTouchMode(true);
				textView.setOnFocusChangeListener(focusFull);
			case BoardManager.SLIDE_CONTROL:
			default: 
				break;
			}
		} else {
			textView.setOnClickListener(clickZoom);
		}
		
		return textView;
	}

	private View.OnFocusChangeListener focusFull = new View.OnFocusChangeListener() {
		public void onFocusChange(View v, boolean hasFocus) {
			bClick.fullOnFocusChange(v, hasFocus);
		}
	};

	private View.OnClickListener clickFull = new View.OnClickListener() {
		public void onClick(View v) {
			bClick.fullOnClick(v);
		}
	};
	
	private View.OnClickListener clickZoom = new View.OnClickListener() {
		public void onClick(View v) {
			bClick.zoomOnClick(v);
		}
	};
	
}
