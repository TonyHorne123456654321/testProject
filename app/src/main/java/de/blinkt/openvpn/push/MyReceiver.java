package de.blinkt.openvpn.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.aixiaoqi.R;
import cn.jpush.android.api.JPushInterface;
import de.blinkt.openvpn.activities.LoginMainActivity;
import de.blinkt.openvpn.activities.MyDeviceActivity;
import de.blinkt.openvpn.activities.SMSAcivity;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.fragments.SmsFragment;
import de.blinkt.openvpn.util.querylocaldatebase.SearchConnectterHelper;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();


		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.e(TAG, "[MyReceiver] 接收Registration Id : " + regId);

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			Log.e(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//
			String type=	bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
			if("SMSReceiveNew".equals(type)){
			processNotification(context, bundle);
			}else if("SMSSendResult".equals(type)){
				processCustomMessage(context, bundle);
			}

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			Log.e(TAG, "[MyReceiver] 接收到推送下来的通知");
//			int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//			processCustomNotify(context);

			//TODO 注册成功后与一正服务器断开连接
//			Log.e(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			Log.e(TAG, "[MyReceiver] 用户点击打开了通知");
			String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Log.e(TAG, "[MyReceiver] 用户点击打开了通知"+extra);
			JsonObject jsonObject = new JsonParser().parse(extra).getAsJsonObject();
			String   tipActivityType = jsonObject.get("alertType").getAsString();
			String   tel = jsonObject.get("Tel").getAsString();
			if(!TextUtils.isEmpty(extra)){

				Intent i;
				if ("SMSReceiveNew" .equals(tipActivityType)) {
					//打开自定义的Activity
					i = new Intent(context, SMSAcivity.class);
					i.putExtra(IntentPutKeyConstant.RECEIVE_SMS, tel);
				}else if("EjoDVCloseLontTime" .equals(tipActivityType)){
					i = new Intent(context, MyDeviceActivity.class);
				} else {
					i = new Intent(context, LoginMainActivity.class);
				}
				//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(i);
			}

		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			Log.e(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
			//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
			Intent i = new Intent(context, SMSAcivity.class);
			i.putExtras(bundle);
			//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(i);
		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
			boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.e(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
		} else {
			Log.e(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}



	private void processCustomMessage(Context context, Bundle bundle) {
		processCustomNotify(context);
		if (SMSAcivity.isForeground) {
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Intent msgIntent = new Intent(SMSAcivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(SMSAcivity.KEY_MESSAGE, message);
			if (!TextUtils.isEmpty(extras)) {
				try {
					JSONObject extraJson = new JSONObject(extras);
					if ( extraJson.length() > 0) {
						msgIntent.putExtra(SMSAcivity.KEY_EXTRAS, extras);
					}
				} catch (JSONException e) {

				}
			}
			context.sendBroadcast(msgIntent);
		}
	}

	private void processCustomNotify(Context context) {
		if (!SmsFragment.isForeground) {
			Intent msgIntent = new Intent(SmsFragment.NOTIFY_RECEIVED_ACTION);
			context.sendBroadcast(msgIntent);
		}
	}
	NotificationManager	mNotificationManager;
	private void processNotification(Context context,Bundle bundle){
		if(mNotificationManager==null){
			mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		processCustomNotify(context);
		initNotify(context,bundle);
	}
	NotificationCompat.Builder mBuilder;
	int notifyId = 100;
	private void initNotify(Context context,Bundle bundle){

		if(mBuilder==null){
			mBuilder = new NotificationCompat.Builder(context);
		}
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		JsonObject jsonObject = new JsonParser().parse(extras).getAsJsonObject();
		String   SMSContent = jsonObject.get("SMSContent").getAsString();
		String   tel = jsonObject.get("Tel").getAsString();
		String smsID=jsonObject.get("SMSID").getAsString();
		String name;
		String telName=SearchConnectterHelper.getContactNameByPhoneNumber(context,tel);
		if(TextUtils.isEmpty(telName)){
			name=tel;
		}else{
			name=telName;
		}
		mBuilder.setContentTitle(name)
				.setContentText(SMSContent)
				.setNumber(3)//显示数量
				.setTicker("有新短信来啦")//通知首次出现在通知栏，带上升动画效果的
				.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
				.setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
				.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
//				.setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
				//Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
				.setSmallIcon(R.drawable.login_icon);

		Intent intent;
		if(!TextUtils.isEmpty(tel)){
			//打开自定义的Activity
			intent = new Intent(context, SMSAcivity.class);
			intent.putExtra(IntentPutKeyConstant.RECEIVE_SMS, tel);
		}else{
			intent = new Intent(context, LoginMainActivity.class);
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contextIntent = PendingIntent.getActivity(context, 0,intent, 0);
		mBuilder.setContentIntent(contextIntent);
		mNotificationManager.notify(notifyId, mBuilder.build());
	}

}
