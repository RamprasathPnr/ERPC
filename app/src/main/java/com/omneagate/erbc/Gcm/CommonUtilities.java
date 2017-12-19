//package com.omneagate.erbc.Gcm;
//
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//public final class CommonUtilities {
//
//
//
//	/**
//	 * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
//	 */
//
//	public static final String SERVER_URL ="http://";
//
//	//http://apps.medindia.net/notification/Android.aspx
//	/**
//	 * Google API project id registered to use GCM.
//	 */
//	public static final String SENDER_ID = "807689892411";
//
//	/**
//	 * Tag used on log messages.
//	 */
//	static final String TAG = "Sample";
//
//	/**
//	 * Intent used to display a message in the screen.
//	 */
//	public static final String DISPLAY_MESSAGE_ACTION = "com.medindia.app.main.DISPLAY_MESSAGE";
//
//	/**
//	 * Intent's extra that contains the message to be displayed.
//	 */
//	static final String EXTRA_MESSAGE = "message";
//
//	/**
//	 * Notifies UI to display a message.
//	 * <p>
//	 * This method is defined in the common helper because it's used both by the
//	 * UI and the background service.
//	 *
//	 * @param context
//	 *            application's context.
//	 * @param message
//	 *            message to be displayed.
//	 */
//	static void displayMessage(Context context, String message) {
//		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
//		intent.putExtra(EXTRA_MESSAGE, message);
//		Log.e("displayMessage in commonUtilities class",message);
//		context.sendBroadcast(intent);
//	}
//}
