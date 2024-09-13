package com.hg.anDGS;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.view.Display;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommonStuff {

	String timeDiff(long t1, long t2) {
		long diff = t1 - t2;
		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		return "" + diffDays + " " + diffHours + ":" + diffMinutes + ":" + diffSeconds;
	}

	OkHttpClient getNewOkHttpClient() {
		OkHttpClient client = new OkHttpClient();
		CookieManager cookieManager = new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		client.setCookieHandler(cookieManager);
		client.setRetryOnConnectionFailure(true);
		client.interceptors().add(new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				Request request = chain.request();
				// try the request
				Response response = chain.proceed(request);
				int tryCount = 0;
				int maxLimit = 4; //Set your max limit here
				while (!response.isSuccessful() && tryCount < maxLimit) {
					Log.d("intercept", "Request failed - " + tryCount);
					tryCount++;
					// retry the request
					response = chain.proceed(request);
				}
				// otherwise just pass the original response on
				return response;
			}
		});
		return client;
	}

	String executeHTTPreq(String baseURL, Map<String,String> httpParams) {
		if (MainDGS.okHTTPclient == null) {
			MainDGS.okHTTPclient = getNewOkHttpClient();
		}
		HttpUrl.Builder urlBuilder;
		try {
			urlBuilder = HttpUrl.parse(baseURL).newBuilder();
		} catch (Exception e) {
			return "#Failed: " + e.toString();
		}
		Map<String, String> params = new HashMap<String, String>(httpParams);
		Set<String> setCodes = params.keySet();

		for (String code : setCodes) {
			String val = params.get(code);
			urlBuilder.addQueryParameter(code, val);
		}
		String url = urlBuilder.build().toString();
		RequestBody body = RequestBody.create(MediaType.parse("text/plain"),"");
		Request request = new Request.Builder()
				//.header("Authorization", "your token")
				.url(url)
				.post(body)
				.build();
		Response response;
		String rsp;
		try {
			response = MainDGS.okHTTPclient.newCall(request).execute();
			try {
				rsp = response.body().string();
				if (!response.isSuccessful()) {
					rsp = "#Failed: " + rsp;
				}
			} catch (IOException e) {
				rsp = "#Failed: " + e.toString();
			}
		} catch (IOException e) {
			rsp = "#Failed: " + e.toString();
		}
		return rsp;
	}

	String executeLogonToDGS(String DGSUser, String DGSPass) {
		Map<String,String> HTTPparamsLogin = new HashMap<String, String>();
		HTTPparamsLogin.clear();
		HTTPparamsLogin.put("quick_mode","1");
		HTTPparamsLogin.put("userid", DGSUser);
		HTTPparamsLogin.put("passwd", DGSPass);
		String rslt = executeHTTPreq(MainDGS.loginURL, HTTPparamsLogin);
		return rslt;
	}

	void executeLogOutDGS() {
		Map<String,String> HTTPparamsLogin = new HashMap<String, String>();
		HTTPparamsLogin.clear();
		HTTPparamsLogin.put("quick_mode","1");
		HTTPparamsLogin.put("logout","1");
		String rslts = executeHTTPreq(MainDGS.loginURL, HTTPparamsLogin);
		// TODO check and log rslts??
	}

	void startStopNotifier(boolean start, Context context, boolean restartable, boolean resetCounters) {
		final Intent notifIntent = new Intent(context, DGSNotifier.class);
		notifIntent.putExtra("Start", start);
		notifIntent.putExtra("Restart", restartable);
		notifIntent.putExtra("Reset", resetCounters);
		if (start) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !resetCounters) {
				try {
					context.startForegroundService(notifIntent);
				} catch (Exception e) {
					context.startService(notifIntent);
				}
			} else {
				context.startService(notifIntent);
			}
		} else {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				context.startService(notifIntent);
			} else {
				context.stopService(notifIntent);
			}
		}
	}

	boolean isNotifierRunning(ActivityManager am){
		List<ActivityManager.RunningServiceInfo> list;
		try{
			list = am.getRunningServices(Integer.MAX_VALUE);
		}catch(Throwable ex){
			return false;
		}

		for(ActivityManager.RunningServiceInfo info: list){
			if (info.service.getClassName().contains("DGSNotifier")) {
				return true;
			}
		}
		return false;
	}
	
	int getScrOrientation(Activity act)
	{
	    Display getOrient = act.getWindowManager().getDefaultDisplay();
	    if (getOrient.getWidth() < getOrient.getHeight()) {
            return Configuration.ORIENTATION_PORTRAIT;
        } else { 
            return Configuration.ORIENTATION_LANDSCAPE;
        }
	}

	float convertFromDp(float input, Context ctx) {
	    final float scale = ctx.getResources().getDisplayMetrics().density;
	    return ((input - 0.5f) / scale);
	}

	public int getCommonTheme(String theme) {
		if (theme.contains(PrefsDGS.METALLICGREYINVERSE)) {
			return R.style.Theme_MyMetallicGreyInverse;
		} else if (theme.contains(PrefsDGS.GREENONWHITE)) {
			return R.style.Theme_MyGreenOnWhite;
		} else if (theme.contains(PrefsDGS.BLUEONWHITE)) {
			return R.style.Theme_MyBlueOnWhite;
		} else if (theme.contains(PrefsDGS.BLACKONWHITE)) {
			return R.style.Theme_MyBlackOnWhite;
		} else if (theme.contains(PrefsDGS.METALLICGREY)) {
        	return R.style.Theme_MyMetallicGrey;
        } else if (theme.contains(PrefsDGS.WHITEONBLUE)) {
            return R.style.Theme_MyWhiteOnBlue;
        } else if (theme.contains(PrefsDGS.WHITEONBLACK)) {
        	return R.style.Theme_MyWhiteOnBlack;
        } else if (theme.contains(PrefsDGS.WHITEONGREEN)) {
            return R.style.Theme_MyWhiteOnGreen;
        } else {
        	return R.style.Theme_MyWhiteOnGreen;
        }
	}

    public int getCommonStyle(String theme) {
		if (theme.contains(PrefsDGS.METALLICGREYINVERSE)) {
			return R.style.MyMetallicGreyInverseStyle;
		} else if (theme.contains(PrefsDGS.GREENONWHITE)) {
			return R.style.MyGreenOnWhiteStyle;
		} else if (theme.contains(PrefsDGS.BLUEONWHITE)) {
			return R.style.MyBlueOnWhiteStyle;
		} else if (theme.contains(PrefsDGS.BLACKONWHITE)) {
			return R.style.MyBlackOnWhiteStyle;
		} else if (theme.contains(PrefsDGS.METALLICGREY)) {
            return R.style.MyMetallicGreyStyle;
        } else if (theme.contains(PrefsDGS.WHITEONBLUE)) {
            return R.style.MyWhiteOnBlueStyle;
        } else if (theme.contains(PrefsDGS.WHITEONBLACK)) {
            return R.style.MyWhiteOnBlackStyle;
        } else if (theme.contains(PrefsDGS.WHITEONGREEN)) {
            return R.style.MyWhiteOnGreenStyle;
        } else {
            return R.style.MyWhiteOnGreenStyle;
        }
    }

	final int HELP_HELP = 0;
	final int HELP_MAIN = 1;
	final int HELP_PREFS = 2;
	final int HELP_PLAY = 3;
	final int HELP_SAVED = 4;
	final int HELP_NEW = 5;
	final int HELP_EDIT = 6;
	final int HELP_DOWNLOAD = 7;
	final int HELP_CREDITS = 8;
	final int HELP_PLAY_OPTIONS = 9;
	final int HELP_MARKUP = 10;
	final int HELP_CLIENT = 11;
	final int HELP_STATUS = 12;
	final int HELP_MSG = 13;
	final int HELP_GAMEINFO = 14;
	final int HELP_GRINDER = 15;
	final int HELP_LOGON = 16;
	final int HELP_GAMES = 17;
	final int HELP_SEND_MSG = 18;
	final int HELP_SEND_INVITE = 19;
	final int HELP_PHRASES = 20;
	final int HELP_USERINFO = 21;
    final int HELP_STOREDMOVES = 22;
	final int HELP_GETGRAPH = 23;
	final int HELP_FINDUSER = 24;
	final int HELP_ERRORHISTORY = 25;
	final int HELP_FILE_PERMISSIONS = 26;
	
	public Intent helpDGS (int helpType, Context ctx) {
		String help_text;
		String title_text;
		switch (helpType) {
		case HELP_MAIN: 
			help_text = ctx.getString(R.string.HelpMain);
			title_text = "anDGS";
			break;
		case HELP_PREFS:
			help_text = ctx.getString(R.string.HelpPrefs);
			title_text = ctx.getString(R.string.Preferences);
			break;
		case HELP_PLAY:
			help_text = ctx.getString(R.string.HelpPlay);
			title_text = ctx.getString(R.string.play);
			break;
		case HELP_SAVED:
			help_text = ctx.getString(R.string.HelpSaved);
			title_text = ctx.getString(R.string.savedGame);
			break;
		case HELP_NEW:
			help_text = ctx.getString(R.string.HelpNew);
			title_text = ctx.getString(R.string.newGame);
			break;
		case HELP_EDIT:
			help_text = ctx.getString(R.string.HelpEdit);
			title_text = ctx.getString(R.string.edit);
			break;
		case HELP_DOWNLOAD:
			help_text = ctx.getString(R.string.HelpDownLoad);
			title_text = ctx.getString(R.string.downLoadGame);
			break;
		case HELP_CREDITS:
			help_text = ctx.getString(R.string.HelpCredits);
			title_text = ctx.getString(R.string.Credits);
			break;
		case HELP_PLAY_OPTIONS:
			help_text = ctx.getString(R.string.HelpPlayOptions);
			title_text = ctx.getString(R.string.Options);
			break;
		case HELP_MARKUP:
			help_text = ctx.getString(R.string.HelpMarkUp);
			title_text = ctx.getString(R.string.markup);
			break;
		case HELP_CLIENT:
			help_text = ctx.getString(R.string.HelpClient);
			title_text = ctx.getString(R.string.DGSClient);
			break;
		case HELP_STATUS:
			help_text = ctx.getString(R.string.HelpStatus);
			title_text = ctx.getString(R.string.Status);
			break;
		case HELP_MSG:
			help_text = ctx.getString(R.string.HelpMsg);
			title_text = ctx.getString(R.string.message);
			break;
		case HELP_GAMEINFO:
			help_text=ctx.getString(R.string.HelpGameInfo);
			title_text = ctx.getString(R.string.GameInfo);
			break;
		case HELP_GRINDER:
			help_text = ctx.getString(R.string.HelpGrinder);
			title_text = ctx.getString(R.string.grinderTitle);
			break;
		case HELP_LOGON:
			help_text = ctx.getString(R.string.HelpLogon);
			title_text = ctx.getString(R.string.Logon);
			break;
		case HELP_GAMES:
			help_text = ctx.getString(R.string.HelpGames);
			title_text = ctx.getString(R.string.Games);
			break;
		case HELP_SEND_MSG:
			help_text = ctx.getString(R.string.HelpSendMsg);
			title_text = ctx.getString(R.string.MessageUser);
			break;
		case HELP_SEND_INVITE:
				help_text = ctx.getString(R.string.HelpInvite);
				title_text = ctx.getString(R.string.InviteUser);
				break;
		case HELP_GETGRAPH:
				help_text = ctx.getString(R.string.HelpGetGraph);
				title_text = ctx.getString(R.string.GetUserGraph);
				break;
		case HELP_FINDUSER:
				help_text = ctx.getString(R.string.HelpFindUser);
				title_text = ctx.getString(R.string.findUser);
				break;
		case HELP_PHRASES:
			help_text = ctx.getString(R.string.HelpPhrases);
			title_text = ctx.getString(R.string.Comment);
			break;
		case HELP_USERINFO:
			help_text = ctx.getString(R.string.HelpUserInfo);
			title_text = ctx.getString(R.string.Info);
			break;
        case HELP_STOREDMOVES:
            help_text = ctx.getString(R.string.HelpPredictedMoves);
            title_text = ctx.getString(R.string.PredictedMoves);
            break;
        case HELP_ERRORHISTORY:
            help_text = ctx.getString(R.string.HelpErrorHistory);
            title_text = ctx.getString(R.string.ErrorHistory);
            break;
		case HELP_FILE_PERMISSIONS:
			help_text = ctx.getString(R.string.HelpFilePermissions);
			title_text = ctx.getString(R.string.NoFiles);
			break;
		default:
			help_text=ctx.getString(R.string.HelpUnknown);
			title_text = ctx.getString(R.string.Help);
			break;
		} 
		
		String [] buttonTexts = new String [1];
		buttonTexts[0] = ctx.getString(R.string.doneButton);
		final Intent msgIntent = new Intent(ctx, MsgView.class);
		msgIntent.putExtra("TITLE", title_text);
		msgIntent.putExtra("ButtonTexts", buttonTexts);
		msgIntent.putExtra("MsgText", help_text); 
		msgIntent.putExtra("MsgHelpType", HELP_HELP);
		return msgIntent;
	}
}
