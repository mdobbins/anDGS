package com.hg.anDGS;

import java.util.Iterator;

import net.sf.gogui.go.ConstPointList;
import net.sf.gogui.go.GoPoint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class BoardLines extends View {
	int boardSize;
	int stoneSize;
	boolean topEdge;
	boolean bottomEdge;
	boolean leftEdge;
	boolean rightEdge;
	ConstPointList hoshiList;
	Paint paint = new Paint();
	float lineWidth;
	float circleRadius;

	public BoardLines(Context context, int bSize, int sSize, boolean tEdge, boolean bEdge, boolean lEdge, boolean rEdge, ConstPointList hoshiLst, float scaleLines) {
		super(context);
		boardSize = bSize;	// actual playing board size witout coords
		stoneSize = sSize;	// stone size in pixels
		topEdge = tEdge;
		bottomEdge = bEdge;
		leftEdge = lEdge;
		rightEdge = rEdge;
		hoshiList = hoshiLst;
		paint.setColor(Color.BLACK);
		lineWidth = 19/boardSize;  // calculate from boardSize...
		lineWidth = lineWidth * scaleLines;
		DisplayMetrics dm = getResources().getDisplayMetrics() ;  //
		lineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lineWidth, dm);  //
		if (lineWidth < 1) lineWidth = 1;
		circleRadius = 2*lineWidth;  // calculate from boardSize...
		paint.setStrokeWidth(lineWidth);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		float nMin = stoneSize/2;
		float nMax = nMin + ((boardSize - 1) * stoneSize);
		float xMin = leftEdge ? nMin : 0;
		float xMax = rightEdge ? nMax : nMax+nMin;
		float yMin = topEdge ? nMin : 0;
		float yMax = bottomEdge ? nMax : nMax+nMin;
		float nPoint;
		// draw grid
		for (int i=0; i<boardSize;  ++i) {
			nPoint = nMin + (i * stoneSize);
			canvas.drawLine(yMin, nPoint, yMax, nPoint, paint);
			canvas.drawLine(nPoint, xMin, nPoint, xMax, paint);
		}
		// draw hoshi
		if (hoshiList == null) return;
		Iterator<GoPoint> i = hoshiList.iterator();
		while (i.hasNext()) {
			GoPoint p = i.next();
			float nX = nMin + (p.getX() * stoneSize);
			float nY = nMin + (p.getY() * stoneSize);
			canvas.drawCircle(nX, nY, circleRadius, paint);
		}
	}
}
