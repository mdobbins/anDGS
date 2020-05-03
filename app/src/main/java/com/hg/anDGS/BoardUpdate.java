package com.hg.anDGS;

import net.sf.gogui.go.GoColor;

interface BoardUpdate {
	// use this interface to call back to the display activity to update button states, etc.
	void displayFullButtons(int buttonState, int playState);
	void displayZoomButtons(int buttonState, int playState);
	void displayComment(String comment);
	void finishGrinding(int buttonState);
	void displayGameInfo(String info);
	void displayMoveInfo(String info);
	void requestMark(int mType, int aType, String p_label, GoColor mvColor);
	void setScoringUpdated(boolean val);
}
