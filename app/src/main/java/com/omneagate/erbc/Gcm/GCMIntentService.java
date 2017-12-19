//package com.omneagate.erbc.Gcm;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//
//import com.google.android.gcm.GCMBaseIntentService;
//import com.google.android.gcm.GCMRegistrar;
//import com.omneagate.erbc.R;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//
//import static com.omneagate.erbc.Gcm.CommonUtilities.displayMessage;
//
//import static com.omneagate.erbc.Gcm.CommonUtilities.SENDER_ID;
//import static com.omneagate.erbc.Gcm.CommonUtilities.SERVER_URL;
//import static com.omneagate.erbc.Gcm.CommonUtilities.displayMessage;
//
//public class GCMIntentService extends GCMBaseIntentService{
//
//	static String Title;
//
//
//	String description;
//	static String ScreenType="",articleId ="";
//	static String imageURL;
//
//
//	String url;
//	static int Notification_ID = 0;
//	static int numMessages = 0;
//
//	 private static final String TAG = "GCMIntentService";
//
//	    public GCMIntentService() {
//	        super(SENDER_ID);
//	    }
//
//	    @Override
//	    protected void onRegistered(Context context, String registrationId) {
//	        Log.i(TAG, "Device registered: regId = " + registrationId);
//	        displayMessage(context, getString(R.string.gcm_registered));
//	        ServerUtilities.register(context, registrationId);
////	        HttpConnections.sendData(SERVER_URL,registrationId);
//	    }
//	    private void playDefaultNotificationSound() {
//	    	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//	    	Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//	    	r.play();
//	    }
//
//	    @Override
//	    protected void onUnregistered(Context context, String registrationId) {
//	        Log.i(TAG, "Device unregistered");
//	        displayMessage(context, getString(R.string.gcm_unregistered));
//	        if (GCMRegistrar.isRegisteredOnServer(context)) {
//	            ServerUtilities.unregister(context, registrationId);
////	        	HttpConnections.sendData(SERVER_URL,registrationId);
//	        } else {
//	            // This callback results from the call to unregister made on
//	            // ServerUtilities when the registration to the server failed.
//	            Log.i(TAG, "Ignoring unregister callback");
//	        }
//	    }
//
//	    @Override
//	    protected void onMessage(Context context, Intent intent) {
//	        Log.i(TAG, "Received message");
//	        //String message = getString(R.string.gcm_message);
//	        //String message = intent.getStringExtra("alert");s
//	         Title = intent.getStringExtra("message");
//	         description = intent.getStringExtra("description");
//
////	         GlobalData.notification_url = intent.getStringExtra("url");
//
//			imageURL = intent.getStringExtra("imgurl");
//			//notifytype=webview/native
//
//			ScreenType = intent.getStringExtra("notifytype");
//			articleId = intent.getStringExtra("articleid");
//	        try{
//	        Log.e("Received message message",Title);
//	        Log.e("Received message description",description);
//	       Log.e("Received message url",imageURL);
//	        /*GlobalData.notificationTxt = message.toString();
//	        Log.e("Received message in onMessage ",message);
//	        displayMessage(context, message);
//	        // notifies user
//	        generateNotification(context, message);*/
//	       /* String[] placeSplits = message.split("\\|",2);
//
//	        System.out.println("placeSplits.size: " + placeSplits.length );
//	        String Notification_Title = placeSplits[0];
//	        GlobalData.Notification_Descriptions = placeSplits[1];
//	        Log.e("Notifications title ",Notification_Title);
//	        Log.e("Notifications descriptions  ", GlobalData.Notification_Descriptions);*/
//	        displayMessage(context, description);
//	        // notifies user
//	        generateNotification(context, description);
//	        }catch(Exception e){
//	        	Log.e("notification error in onMessage me thod ",""+e);
//
//	        }
//	    }
//
//	    @Override
//	    protected void onDeletedMessages(Context context, int total) {
//	        Log.i(TAG, "Received deleted messages notification");
//	        String message = getString(R.string.gcm_deleted, total);
//	        displayMessage(context, message);
//	        // notifies user
//	        generateNotification(context, message);
//	    }
//
//	    @Override
//	    public void onError(Context context, String errorId) {
//	        Log.i(TAG, "Received error: " + errorId);
//	        displayMessage(context, getString(R.string.gcm_error, errorId));
//	    }
//
//	    @Override
//	    protected boolean onRecoverableError(Context context, String errorId) {
//	        // log message
//	        Log.i(TAG, "Received recoverable error: " + errorId);
//	        displayMessage(context, getString(R.string.gcm_recoverable_error,
//	                errorId));
//	        return super.onRecoverableError(context, errorId);
//	    }
//
//	    /**
//	     * Issues a notification to inform the user that server has sent a message.
//	     */
//	    private static void generateNotification(Context context, String message) {
//
//			Bitmap remote_picture=null;
//	        int icon = R.mipmap.ic_launcher;   // Notification Icon
//			//450_237
//			Bitmap yourBitmap = null;
//			Bitmap resized = null;
//
//	        long when = System.currentTimeMillis();
//	        NotificationManager notificationManager = (NotificationManager)
//	                context.getSystemService(Context.NOTIFICATION_SERVICE);
//	        Notification notification = new Notification(icon, message, when);
//	        String title = Title;//context.getString(R.string.notification_name);
//			try {
//				remote_picture = BitmapFactory.decodeStream(
//						(InputStream) new URL(imageURL).getContent());
//				// resized = Bitmap.createScaledBitmap(remote_picture, 350, 237, true);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//	        NotificationCompat.Builder mBuilder =
//	                new NotificationCompat.Builder(context)
//	                .setSmallIcon(icon)
//	                .setContentTitle(Title)
//	                .setWhen(when)
//					.setPriority(Notification.PRIORITY_HIGH)
//					.setStyle(new NotificationCompat.BigPictureStyle()
//							.bigPicture(remote_picture).setSummaryText(message))
//	             //  .setSubText(message)
//	                // .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//	            //   .setStyle(BigTextStyle)
//	                .setContentText(message);
//	     //   .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//	        mBuilder.setNumber(++numMessages);
//
//			////notifytype=webview/native
//			Intent notificationIntent = null;
//			String s = ScreenType;
//			Log.e("screen type",""+s);
//			String s1 = articleId;
//			Log.e("articleId type",""+s1);
//
//			if(ScreenType.equalsIgnoreCase("Latest News")){
//
////				notificationIntent = new Intent(context, LatestHealthNews_Activity.class).putExtra("TAG","Latest News");
//			}
//			/*else if(ScreenType.equalsIgnoreCase("Popular News")){
//
//				notificationIntent = new Intent(context, LatestHealthNews_Activity.class).putExtra("TAG","Popular News");
//			}else if(ScreenType.equalsIgnoreCase("Health Center")){
//				// startActivity(new Intent(getBaseContext(),Health_Center_Activity.class).putExtra("TAG","Health Center"));
//				*//*Bundle b = new Bundle();
//				b.putString("NewsId", "" + GlobalData.sHealthCenterDetailsaryArt.get(position).getNewsId());
//				//  b.putString("Title", GlobalData.sHealthCenterDetailsaryArt.get(position).getTitle());
//				b.putString("TAG", TAG);
//				Intent intent = new Intent(getBaseContext(), HealthCenterDetailsActivityTwo.class);
//				intent.putExtras(b);
//				startActivity(intent);*//*
//			//	notificationIntent = new Intent(context, HealthCenterDetailsActivityTwo.class).putExtra("NewsId","Health Center").putExtra("TAG","Health Center");
//			}else if(ScreenType.equalsIgnoreCase("Medical Specialty")){
//				//notificationIntent = new Intent(context, Health_Center_Activity.class).putExtra("TAG", "Medical Specialty");
//				notificationIntent = new Intent(context, HealthCenterDetailsActivityTwo.class).putExtra("NewsId",articleId).putExtra("TAG","Medical Specialty");
//			}else if(ScreenType.equalsIgnoreCase("First Aid")){
//				notificationIntent = new Intent(context, FirstAidActivity.class).putExtra("TAG", "First Aid");
//			}else if(ScreenType.equalsIgnoreCase("Health Facts")){
//				notificationIntent = new Intent(context, FirstAidActivity.class).putExtra("TAG", "Health Facts");
//			}else if(ScreenType.equalsIgnoreCase("Drug Information")){
//				notificationIntent = new Intent(context, DrugInfoActivity.class).putExtra("TAG", "Drug Information");
//			}else if(ScreenType.equalsIgnoreCase("Health Topics A to Z")){
//				notificationIntent = new Intent(context, HealthTopAtoZActivity.class).putExtra("TAG","Health Topics A to Z");
//			}else if(ScreenType.equalsIgnoreCase("Healthly Living")){
//				notificationIntent = new Intent(context, HealthlyLivingActivity.class).putExtra("TAG","Healthly Living");
//			}else if(ScreenType.equalsIgnoreCase("Multimedia")){
//				notificationIntent = new Intent(context, MultimediaActivity.class).putExtra("TAG","Multimedia");
//			}else if(ScreenType.equalsIgnoreCase("Yoga Lifestyle")){
//				notificationIntent = new Intent(context, FirstAidActivity.class).putExtra("TAG","Yoga Lifestyle");
//			}else if(ScreenType.equalsIgnoreCase("Surgical Procedures")){
//				notificationIntent = new Intent(context, FirstAidActivity.class).putExtra("TAG","Surgical Procedures");
//			}else if(ScreenType.equalsIgnoreCase("Health Tips")){
//				notificationIntent = new Intent(context, LatestHealthNews_Activity.class).putExtra("TAG","Health Tips");
//			}else{
//				notificationIntent = new Intent(context, NotificationActivity.class);
//				// set intent so it does not start a new activity
//				notificationIntent.putExtra("url", ""+GlobalData.notification_url);
//				// startActivity(new Intent(getBaseContext(),LatestHealthNews_Activity.class).putExtra("TAG","Latest News"));
//
//			}*/
//
//
//
//			/*notificationIntent = new Intent(context, NotificationActivity.class);
//			// set intent so it does not start a new activity
//			notificationIntent.putExtra("url", ""+GlobalData.notification_url);*/
//	     //   notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//	        PendingIntent intent =
//	                PendingIntent.getActivity(context, 0, notificationIntent, 0);
//	       // notification.build();
//	        mBuilder.setAutoCancel(true);
//	        /*Uri not = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//	        mBuilder.setSound(not);*/
//	        mBuilder.setContentIntent(intent);
//			/* builder.setContentTitle("WhatsApp Notification");
//                builder.setContentText("You have a new message");
//                builder.setSmallIcon(R.drawable.ic_launcher);
//                builder.setContentIntent(pendingIntent);*/
//			mBuilder.setContentTitle(title);
//
//			mBuilder.setContentText(message);
//			//mBuilder.setContentIntent(intent);
//	     //   notification.setLatestEventInfo(context, title, message, intent);
//
//	        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//	     // Play default notification sound
//	        notification.defaults |= Notification.DEFAULT_SOUND;
//	      //notificationManager.notifyAll();
//	      //notificationManager.notify(Notification_ID, notification);
//	        Notification note = mBuilder.build();
//	        note.defaults |= Notification.DEFAULT_VIBRATE;
//	        note.defaults |= Notification.DEFAULT_SOUND;
//	        notificationManager.notify(Notification_ID,note);
//	        Notification_ID++;
//	        Log.e("Notification id = "," "+Notification_ID);
//	    }
//
//
//}
//
//
//
//