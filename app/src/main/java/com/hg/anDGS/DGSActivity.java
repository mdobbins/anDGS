package com.hg.anDGS;

import java.util.Locale;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

public class DGSActivity extends Activity {
	 @Override
	    public void onCreate(Bundle savedInstanceState)
	    {
	        super.onCreate(savedInstanceState);
	        SharedPreferences settings = getSharedPreferences("MainDGS", 0);
	        Configuration config = getBaseContext().getResources().getConfiguration();
	        String defLanguage = Resources.getSystem().getConfiguration().locale.getLanguage();
	        String confLanguage = config.locale.getLanguage();
	        String myLanguage = settings.getString("com.hg.anDGS.Locale", "");
	        if (!confLanguage.contentEquals(myLanguage)) { 
	        	if (myLanguage.contentEquals("")) {
	        		if (!defLanguage.startsWith(confLanguage)) {
	        			doit(config, defLanguage);
	        		}
	        	} else {
	        		doit(config, myLanguage);
	        	}
	        }
	    }
	 
    private void doit(Configuration config, String myLanguage) {
		//Locale.setDefault(myLocale);
        config.locale = new Locale(myLanguage);
        //getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        getBaseContext().getResources().updateConfiguration(config, null);
    }
}
