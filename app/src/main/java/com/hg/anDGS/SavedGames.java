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
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class SavedGames extends DGSActivity implements OnClickListener {

	protected static final int PLAY = 0;
	protected static final int SELECT = 1;
	private enum DISPLAYMODE{ ABSOLUTE, RELATIVE}

    private final DISPLAYMODE displayMode = DISPLAYMODE.RELATIVE;
    private List<String> directoryEntries = new ArrayList<String>();
    private File currentDirectory = null;
    protected static final int SGF_GAMES = 2;
    protected static final int HELP_VIEW = 0;
	private static final int MENU_HELP = 0;

	private TextView statusTitleView;
	private ListView statusListView;
	
	private String boardLayout;
	private long autoPlayInterval = GameBoardOptions.DEFAUTOPLAYINTERVAL;
	private int mode = PLAY;
	private boolean autoPlayPause = true;
	private String theme;
	private ContextThemeWrapper ctw;
	private CommonStuff commonStuff = new CommonStuff();
	private CommonFileStuff commonFileStuff = new CommonFileStuff();

    /** Called when the activity is first created. */
    public void onCreate(Bundle icicle) {
         super.onCreate(icicle);
 		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

         Bundle extras = getIntent().getExtras();
 		 if (extras != null) {
 			mode = extras.getInt("MODE", PLAY);
 			autoPlayInterval = extras.getLong("AUTOPLAYINTERVAL",GameBoardOptions.DEFAUTOPLAYINTERVAL);
 			autoPlayPause = extras.getBoolean("AUTOPLAYPAUSE", true);
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
 		setContentView(R.layout.statusview);
		ctw = new ContextThemeWrapper(this, commonStuff.getCommonTheme(theme));

		TextView tmHelp = findViewById(R.id.statusTMHelp);
		tmHelp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final Handler handler = new Handler();
				handler.post(new Runnable() { 
					public void run() {	
						doHelp();
					}
				});
			}
		});

		statusTitleView = findViewById(R.id.statusTitle);
		statusListView = findViewById(R.id.statusList);
        statusListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				String selectedFileString = av.getItemAtPosition(position).toString();
		         if (selectedFileString.equals(".")) {
		              // Refresh
		              browseTo(currentDirectory);
		         } else if(selectedFileString.equals("..")){
		              upOneLevel();
		         } else {
		              File clickedFile = null;
		              switch(displayMode){
		                   case RELATIVE:
		                        clickedFile = new File(currentDirectory.getAbsolutePath()
		                                                           + selectedFileString);
		                        break;
		                   case ABSOLUTE:
		                        clickedFile = new File(selectedFileString);
		                        break;
		              }
					 browseTo(clickedFile);
		         }
			}
        });
 		
        // setContentView() gets called within the next line, so we do not need it here.
        browseToSGFRoot();
    }
    
    /**
     * This function browses to the
     * root-directory of the file-system.
     */
    private void browseToSGFRoot() {
		String dName = commonFileStuff.getSgfDirName();
		File path = new File(dName);
		browseTo(path);
   }
    
    /**
     * This function browses up one level
     * according to the field: currentDirectory
     */
    private void upOneLevel(){
         if(currentDirectory.getParent() != null)
              browseTo(currentDirectory.getParentFile());
    }
    
    private void browseTo(final File aDir){
         if (aDir.isDirectory()){
        	 if (mode == SELECT) {
        		 DialogInterface.OnClickListener selectButtonListener = new DialogInterface.OnClickListener(){
                     public void onClick(DialogInterface arg0, int arg1) {
                  	   openFile(aDir);
                     }
                };
                DialogInterface.OnClickListener enterButtonListener = new DialogInterface.OnClickListener(){
                     public void onClick(DialogInterface arg0, int arg1) {
                    	 currentDirectory = aDir;
                         if (currentDirectory.listFiles() == null) {
                       	  Toast.makeText(ctw, getString(R.string.NoFiles), Toast.LENGTH_LONG).show();
                         } else {
                       	  fill(sortedFiles(currentDirectory.listFiles()));
                         }
                     }
                };
                final TextView tv = new TextView(ctw);
                tv.setText(aDir.getName());
                new AlertDialog.Builder(ctw)
                .setTitle(R.string.savedGame)
                .setMessage(R.string.selectOrEnterDir)
                .setView(tv)
                .setPositiveButton(R.string.select, selectButtonListener)
                .setNegativeButton(R.string.enter, enterButtonListener)
                .show();
        	 } else {
                 currentDirectory = aDir;
         		 statusTitleView.setText(aDir.getAbsolutePath());
                 if (currentDirectory.listFiles() == null) {
               	  Toast.makeText(ctw, getString(R.string.NoFiles), Toast.LENGTH_LONG).show();
                 } else {
               	  fill(sortedFiles(currentDirectory.listFiles()));
                 }
        	 }
         } else {
	    	 DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener(){
	               public void onClick(DialogInterface arg0, int arg1) {
	            	   openFile(aDir);
	               }
	          };
	          DialogInterface.OnClickListener cancelButtonListener = new DialogInterface.OnClickListener(){
	               public void onClick(DialogInterface arg0, int arg1) {
	                    // Do nothing
	               }
	          };
	          final TextView tv = new TextView(ctw);
	          tv.setText(aDir.getName());
	          new AlertDialog.Builder(ctw)
	          .setTitle(R.string.savedGame)
	          .setMessage(R.string.OpenFile)
	          .setView(tv)
	          .setPositiveButton(R.string.Ok, okButtonListener)
	          .setNegativeButton(R.string.Cancel, cancelButtonListener)
	          .show();  
         }
    }
    
    private File[] sortedFiles(File[] files) {
    	Arrays.sort(files, new Comparator<File>(){
    	    public int compare(File f1, File f2)
    	    {
    	        return f1.getName().compareTo(f2.getName());
    	    } });
    	return files;
    }

    private void fill(File[] files) {
    
         directoryEntries.clear();
    	         
         // Add the "." and the ".." == 'Up one level'
         try {
              Thread.sleep(10);
         } catch (InterruptedException ignore) {
         }
         directoryEntries.add(".");
         
         if(currentDirectory.getParent() != null)
             directoryEntries.add("..");
         
         switch(this.displayMode){
              case ABSOLUTE:
                   for (File f : files){
                        directoryEntries.add(f.getPath());
                   }
                   break;
              case RELATIVE: // On relative Mode, we will have to add the current-path to the beginning
                   int currentPathStringLenght = currentDirectory.getAbsolutePath().length();
                   for (File f : files){
                        directoryEntries.add(f.getAbsolutePath().substring(currentPathStringLenght));
                   }
                   break;
         }
         
         ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
                   R.layout.file_row, directoryEntries);
         
         statusListView.setAdapter(directoryList);
    }
    

	private void openFile(File f) {
    	FileInputStream in;
    	String fName = f.getAbsolutePath();
    	switch (mode) {
    	case PLAY: 
			String end = fName.substring(fName.lastIndexOf(".")+1).toLowerCase(getResources().getConfiguration().locale);
	    	if(end.equals("sgf")) {
	    		try {
					in = new FileInputStream(f);
				} catch (FileNotFoundException e) {
					return;
				}
				TextHelper th = new TextHelper();
				String sgf = th.GetText(in);
				final Intent sgfGameIntent = new Intent(SavedGames.this,GameBoard.class);
	        	sgfGameIntent.putExtra("SGF", sgf); // key/value pair, 
	        	sgfGameIntent.putExtra("FILE", fName);
	        	sgfGameIntent.putExtra("GAMEACTION",BoardManager.GA_PLAY);
	    		sgfGameIntent.putExtra("GAMESTATUS",BoardManager.GS_PLAY);
	    		sgfGameIntent.putExtra("HANDICAP","0");
	    		sgfGameIntent.putExtra("TIMELEFT", "");
	        	sgfGameIntent.putExtra("AUTOPLAYPAUSE", autoPlayPause);
	        	sgfGameIntent.putExtra("AUTOPLAYINTERVAL", autoPlayInterval);
	        	sgfGameIntent.putExtra("BOARDLAYOUT", boardLayout);
	         	startActivityForResult(sgfGameIntent, SGF_GAMES);
	    	}
         	break;
	    case SELECT:
	    	Bundle rslts = new Bundle();
	    	rslts.putString("TARGETNAME", fName);
	    	Intent mIntent = new Intent();
	    	mIntent.putExtras(rslts);
    		setResult(RESULT_OK, mIntent);
    		finish();
    		break;
	    default:
	    }
   
    } 
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	Intent mIntent;
    	switch(requestCode) {
    	case HELP_VIEW:
     		break;
    	case SGF_GAMES:
    	default:
    		mIntent = new Intent();
    		setResult(RESULT_OK, mIntent);
    		finish();
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
		 if (item.getItemId() == MENU_HELP) {
			 doHelp();
		 }
		 return false;
	 }
	 
	 private void doHelp() {
		 final Intent helpIntent = commonStuff.helpDGS (commonStuff.HELP_SAVED, this);
		 helpIntent.putExtra("BOARDLAYOUT", boardLayout);
		 startActivityForResult(helpIntent, HELP_VIEW);
	 }
	 
	 public void onClick(View v) {
		}
         
}
