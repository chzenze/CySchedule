package com.cs309.cychedule.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import com.cs309.cychedule.R;
import com.cs309.cychedule.activities.Main3Activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserUtil {
	public static boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		return isNum.matches();
	}
	
	public static boolean hasSpecialChar(String str) {
		String regEx = "[ _`~!#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.find();
	}

	public static void notificationHandler(Context context, int NotificationID, String title, String text){
		NotificationManager notificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			String CHANNEL_ID = "test_channel_01";
			CharSequence name = "test_channel";
			String Description = "This is cychedule test channel.";
			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
			channel.setDescription(Description);
			channel.enableLights(true);
			channel.setLightColor(Color.RED);
			channel.enableVibration(true);
			channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
			channel.setShowBadge(false);
			notificationManager.createNotificationChannel(channel);
		}

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "test_channel_01")
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle(title)
				.setContentText(text);

		Intent resultIntent = new Intent(context, context.getClass());
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(Main3Activity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent
				= stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		notificationManager.notify(NotificationID, builder.build());
	}
}