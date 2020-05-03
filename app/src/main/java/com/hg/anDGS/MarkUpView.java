package com.hg.anDGS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MarkUpView extends DGSActivity {
	
	private ImageView markNone; //M_NONE = 0;
	//not user visible M_MOVE = 1;
	private ImageView markMark;// M_MARK = 2;
	private ImageView markTriangle; //M_TRIANGLE = 3;
	private ImageView markCircle; //M_CIRCLE = 4;
	private ImageView markSquare; //M_SQUARE = 5;
	private ImageView markTerrW; //M_TERR_W = 6;
	private ImageView markTerrB; //M_TERR_B = 7;
	private ImageView markAddEmpty; //M_ADDEMPTY = 8;
	private ImageView markAddW; //M_ADD_W = 9;
	private ImageView markAddB; //M_ADD_B = 10;
	private EditText labelText;
	
	private TextView tmHelp;

	private TextView markNoneT; //M_NONE = 0;
	//not user visible M_MOVE = 1;
	private TextView markMarkT;// M_MARK = 2;
	private TextView markTriangleT; //M_TRIANGLE = 3;
	private TextView markCircleT; //M_CIRCLE = 4;
	private TextView markSquareT; //M_SQUARE = 5;
	private TextView markTerrWT; //M_TERR_W = 6;
	private TextView markTerrBT; //M_TERR_B = 7;
	private TextView markAddEmptyT; //M_ADDEMPTY = 8;
	private TextView markAddWT; //M_ADD_W = 9;
	private TextView markAddBT; //M_ADD_B = 10;

	protected static final int HELP_VIEW = 0;
	private static final int MENU_HELP = 0;
	
	private TextView done_button;
	private String boardLayout = PrefsDGS.PORTRAIT;
	private String theme;
	private int markIndicator = BoardManager.M_NONE;
	private int addIndicator = BoardManager.M_NONE;
	private int graphicNone = R.drawable.blnk; //M_NONE = 0;
	//not user visible M_MOVE = 1;
	private int graphicMark = R.drawable.blnk;// M_MARK = 2;
	private int graphicTriangle = R.drawable.blnk; //M_TRIANGLE = 3;
	private int graphicCircle = R.drawable.blnk; //M_CIRCLE = 4;
	private int graphicSquare = R.drawable.blnk; //M_SQUARE = 5;
	private int graphicTerrW = R.drawable.blnk; //M_TERR_W = 6;
	private int graphicTerrB = R.drawable.blnk; //M_TERR_B = 7;
	private int graphicAddEmpty = R.drawable.blnk; //M_SQUARE = 8;
	private int graphicAddW = R.drawable.blnk; //M_TERR_W = 9;
	private int graphicAddB = R.drawable.blnk; //M_TERR_B = 10;
	private String label = "";
	private int grphStnSize = 20;
	private CommonStuff commonStuff = new CommonStuff();
		
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	markIndicator = extras.getInt("MARKTYPE", 0);
            addIndicator = extras.getInt("ADDTYPE", 0);
            label = extras.getString("POINTLABEL");
            graphicNone = extras.getInt("GRAPHICNONE", R.drawable.blnk); 
        	graphicMark = extras.getInt("GRAPHICMARK", R.drawable.ax);
        	graphicTriangle = extras.getInt("GRAPHICTRIANGLE", R.drawable.at);
        	graphicCircle = extras.getInt("GRAPHICCIRCLE", R.drawable.ac); 
        	graphicSquare = extras.getInt("GRAPHICSQUARE", R.drawable.as); 
        	graphicTerrW = extras.getInt("GRAPHICTERRW", R.drawable.aw); 
        	graphicTerrB = extras.getInt("GRAPHICTERRB", R.drawable.ab);
        	graphicAddEmpty = extras.getInt("GRAPHICADDEMPTY", R.drawable.blnk); 
        	graphicAddW = extras.getInt("GRAPHICADDW", R.drawable.w); 
        	graphicAddB = extras.getInt("GRAPHICADDB", R.drawable.b);
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
        setContentView(R.layout.markup);
        
		tmHelp = (TextView) findViewById(R.id.markUpTMHelp);
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
                
        markNone = (ImageView) findViewById(R.id.markNoneV);
        markNone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markNoneClick();
            }
        });
        
        markNoneT = (TextView) findViewById(R.id.markNoneT);
        markNoneT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markNoneClick();
            }
        });
                
        markMark = (ImageView) findViewById(R.id.markMarkV);
    	markMark.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markMarkClick();
            }
        });
    	
    	markMarkT = (TextView) findViewById(R.id.markMarkT);
    	markMarkT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markMarkClick();
            }
        });
        
        markTriangle = (ImageView) findViewById(R.id.markTriangleV);
        markTriangle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markTriangleClick();
            }
        });
        
        markTriangleT = (TextView) findViewById(R.id.markTriangleT);
        markTriangleT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markTriangleClick();
            }
        });
        
        markCircle = (ImageView) findViewById(R.id.markCircleV);
        markCircle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markCircleClick();
            }
        });
        
        markCircleT = (TextView) findViewById(R.id.markCircleT);
        markCircleT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markCircleClick();
            }
        });
        
        markSquare = (ImageView) findViewById(R.id.markSquareV);
        markSquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markSquareClick();
            }
        });
        
        markSquareT = (TextView) findViewById(R.id.markSquareT);
        markSquareT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markSquareClick();
            }
        });
        
        markTerrW = (ImageView) findViewById(R.id.markTerrWV);
        markTerrW.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markTerrWClick();
            }
        });
        
        markTerrWT = (TextView) findViewById(R.id.markTerrWT);
        markTerrWT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markTerrWClick();
            }
        });
        
        markTerrB = (ImageView) findViewById(R.id.markTerrBV);
        markTerrB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markTerrBClick();
            }
        });
        
        markTerrBT = (TextView) findViewById(R.id.markTerrBT);
        markTerrBT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markTerrBClick();
            }
        });
        
        markAddEmpty = (ImageView) findViewById(R.id.markAddEmptyV);
        markAddEmpty.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markAddEmptyClick();
            }
        });
        
        markAddEmptyT = (TextView) findViewById(R.id.markAddEmptyT);
        markAddEmptyT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markAddEmptyClick();
            }
        });
        
        markAddW = (ImageView) findViewById(R.id.markAddWV);
        markAddW.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markAddWClick();
            }
        });
        
        markAddWT = (TextView) findViewById(R.id.markAddWT);
        markAddWT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markAddWClick();
            }
        });
        
        markAddB = (ImageView) findViewById(R.id.markAddBV);
        markAddB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markAddBClick();
            }
        });
        
        markAddBT = (TextView) findViewById(R.id.markAddBT);
        markAddBT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	markAddBClick();
            }
        });
        
        labelText = (EditText) findViewById(R.id.markUpLabelValue);
        
        done_button = (TextView) findViewById(R.id.markUpDoneButton);
        done_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	label = labelText.getText().toString();
				setSelectedBackground(done_button);
            	Bundle rslts = new Bundle();
            	rslts.putInt("MARKTYPE", markIndicator);
            	rslts.putInt("ADDTYPE", addIndicator);
            	rslts.putString("POINTLABEL", label);
                Intent mIntent = new Intent();
                mIntent.putExtras(rslts);
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });
      
    	initImageView(markNone, graphicNone); 
    	initImageView(markMark, graphicMark);
    	initImageView(markTriangle, graphicTriangle); 
    	initImageView(markCircle, graphicCircle);
    	initImageView(markSquare, graphicSquare); 
    	initImageView(markTerrW, graphicTerrW); 
    	initImageView(markTerrB, graphicTerrB);
    	initImageView(markAddEmpty, graphicAddEmpty); 
    	initImageView(markAddW, graphicAddW); 
    	initImageView(markAddB, graphicAddB);
    	
    	setOrigBackground(markNoneT);
    	setOrigBackground(markMarkT);
    	setOrigBackground(markTriangleT);
    	setOrigBackground(markCircleT);
    	setOrigBackground(markSquareT);
    	setOrigBackground(markTerrWT);
    	setOrigBackground(markTerrBT);
    	setOrigBackground(markAddEmptyT);
    	setOrigBackground(markAddWT);
    	setOrigBackground(markAddBT);
		
    	switch (markIndicator) {
    	case BoardManager.M_NONE: 
    		setSelectedBackground(markNoneT);
    		break;
		case BoardManager.M_MARK: 
			setSelectedBackground(markMarkT);
			break;
		case BoardManager.M_TRIANGLE: 
			setSelectedBackground(markTriangleT);
			break;
		case BoardManager.M_CIRCLE: 
			setSelectedBackground(markCircleT);
			break;
		case BoardManager.M_SQUARE: 
			setSelectedBackground(markSquareT);
			break;
		case BoardManager.M_TERR_W: 
			setSelectedBackground(markTerrWT);
			break;
		case BoardManager.M_TERR_B: 
			setSelectedBackground(markTerrBT);
			break;
		default:  // unused value
		}
    	
    	switch (addIndicator) {
    	case BoardManager.M_ADDEMPTY: 
    		setSelectedBackground(markAddEmptyT);
    		break;
		case BoardManager.M_ADD_W: 
			setSelectedBackground(markAddWT);
			break;
		case BoardManager.M_ADD_B: 
			setSelectedBackground(markAddBT);
			break;
		default:  // unused value
		}
    	
    	if (label == null) label = "";
    	labelText.setText(label);
    }
     
	private void setOrigBackground(TextView tv) {
    	tv.setBackgroundColor(MainDGS.TRANSPARENT_COLOR);
    }
    
    private void setSelectedBackground(TextView tv) {
    	tv.setBackgroundColor(MainDGS.GREEN_COLOR);
    }
   
    private void markNoneClick() {
    	setSelectedBackground(markNoneT);
     	setOrigBackground(markMarkT);
     	setOrigBackground(markTriangleT);
     	setOrigBackground(markCircleT);
     	setOrigBackground(markSquareT);
     	setOrigBackground(markTerrWT);
     	setOrigBackground(markTerrBT);
     	setOrigBackground(markAddEmptyT);
     	setOrigBackground(markAddWT);
     	setOrigBackground(markAddBT);

    	markIndicator = BoardManager.M_NONE;
    	addIndicator = BoardManager.M_NONE;
    }
    
    private void markMarkClick() {
    	setOrigBackground(markNoneT);
    	setSelectedBackground(markMarkT);
    	setOrigBackground(markTriangleT);
    	setOrigBackground(markCircleT);
    	setOrigBackground(markSquareT);
    	setOrigBackground(markTerrWT);
    	setOrigBackground(markTerrBT);
    	
    	markIndicator = BoardManager.M_MARK;
    }
    
    private void markTriangleClick() {
    	setOrigBackground(markNoneT);
    	setOrigBackground(markMarkT);
    	setSelectedBackground(markTriangleT);
    	setOrigBackground(markCircleT);
    	setOrigBackground(markSquareT);
    	setOrigBackground(markTerrWT);
    	setOrigBackground(markTerrBT);
    	
    	markIndicator = BoardManager.M_TRIANGLE;
    }
    
    private void markCircleClick() {
    	setOrigBackground(markNoneT);
    	setOrigBackground(markMarkT);
    	setOrigBackground(markTriangleT);
    	setSelectedBackground(markCircleT); 
    	setOrigBackground(markSquareT);
    	setOrigBackground(markTerrWT);
    	setOrigBackground(markTerrBT);
    	
    	markIndicator = BoardManager.M_CIRCLE;
    }

    private void markSquareClick() {
    	setOrigBackground(markNoneT);
    	setOrigBackground(markMarkT); 
    	setOrigBackground(markTriangleT);
    	setOrigBackground(markCircleT);
    	setSelectedBackground(markSquareT);
    	setOrigBackground(markTerrWT);
    	setOrigBackground(markTerrBT);
    	
    	markIndicator = BoardManager.M_SQUARE;
    }
    
    private void markTerrWClick() {
    	setOrigBackground(markNoneT);
    	setOrigBackground(markMarkT);
    	setOrigBackground(markTriangleT);
    	setOrigBackground(markCircleT);
    	setOrigBackground(markSquareT); 
    	setSelectedBackground(markTerrWT);
    	setOrigBackground(markTerrBT);
    	
    	markIndicator = BoardManager.M_TERR_W;
    }
    
    private void markTerrBClick() {
    	setOrigBackground(markNoneT);
    	setOrigBackground(markMarkT); 
    	setOrigBackground(markTriangleT);
    	setOrigBackground(markCircleT);
    	setOrigBackground(markSquareT);
    	setOrigBackground(markTerrWT);
    	setSelectedBackground(markTerrBT);
    	
    	markIndicator = BoardManager.M_TERR_B;
    }
    
    private void markAddEmptyClick() {
    	setSelectedBackground(markAddEmptyT);
    	setOrigBackground(markAddWT);
    	setOrigBackground(markAddBT);
    	
    	addIndicator = BoardManager.M_ADDEMPTY;
    }
    
    private void markAddWClick() {
    	setOrigBackground(markAddEmptyT);
    	setSelectedBackground(markAddWT);
    	setOrigBackground(markAddBT);
    	
     	addIndicator = BoardManager.M_ADD_W;
    }
    
    private void markAddBClick() { 
    	setOrigBackground(markAddEmptyT);
    	setOrigBackground(markAddWT);
    	setSelectedBackground(markAddBT);
    	
    	addIndicator = BoardManager.M_ADD_B;
    }
    
    private void initImageView(ImageView v, int r) {
        v.setMaxHeight(grphStnSize);
        v.setMinimumHeight(grphStnSize);
        v.setMaxWidth(grphStnSize);
        v.setMinimumWidth(grphStnSize);
        v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        v.setBackgroundColor(MainDGS.BOARD_COLOR);
        v.setImageResource(r);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	switch(requestCode) {
    	case HELP_VIEW:
    	default:
    		break;
    	}
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
		 final Intent helpIntent = commonStuff.helpDGS (commonStuff.HELP_MARKUP, this);
		 helpIntent.putExtra("BOARDLAYOUT", boardLayout);
		 startActivityForResult(helpIntent, HELP_VIEW);
	 }
	 
}
