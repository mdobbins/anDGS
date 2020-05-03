package com.hg.anDGS;

import android.view.View;

interface BoardClick {
	void zoomOnClick(View v);
	void fullOnClick(View v);
	void fullOnFocusChange(View v, boolean hasFocus);
}
