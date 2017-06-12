package com.roosterr;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat.Builder;
import android.telephony.SmsManager;
import android.widget.Toast;
import com.digits.sdk.vcard.VCardConstants;
import io.fabric.sdk.android.BuildConfig;
import io.fabric.sdk.android.services.events.EventsFilesManager;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class SMSReciever extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        String date_time = simpleDateFormat.format(Calendar.getInstance().getTime());
        String[] date_time_array = date_time.split(" ");
        String msgDate = date_time_array[0];
        String msgTime = date_time_array[1];
        SQLController dbController = new SQLController(context);
        dbController.open();
        Cursor cursor = dbController.fetchMessagesByDateTime(date_time);
        while (!cursor.isAfterLast()) {
            String _ID = cursor.getString(cursor.getColumnIndex(DBHelper._ID));
            String message = cursor.getString(cursor.getColumnIndex(SettingsJsonConstants.PROMPT_MESSAGE_KEY)).replaceAll("'", "'");
            String frequency = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_REPEAT));
            String nextDate = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_NEXT));
            String smsTime = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_TIME));
            String nextDateTime = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_DateTime));
            String smsStatus = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_Status));
            String msgType = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_TYPE));
            String msgTrigger = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_TRIGGER));
            String group = cursor.getString(cursor.getColumnIndex(DBHelper.GROUP_ID));
            String cmsCustom = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_CUSTOM));
            String msgAlarm = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_ALARM));
            String recepients = "";
            String smsNames = "";
            Cursor groupContactCursor = dbController.getGroupContacts(Integer.valueOf(Integer.parseInt(group)));
            while (!groupContactCursor.isAfterLast()) {
                smsNames = smsNames + groupContactCursor.getString(groupContactCursor.getColumnIndex(DBHelper.GROUP_CONTACT_NAME)) + ",";
                recepients = recepients + groupContactCursor.getString(groupContactCursor.getColumnIndex(DBHelper.GROUP_CONTACT_NUMBER)) + ",";
                groupContactCursor.moveToNext();
            }
            groupContactCursor.close();
            String[] date = nextDate.split("/");
            int year = Integer.parseInt(date[2]);
            int month = Integer.parseInt(date[1])- 1;
            int day = Integer.parseInt(date[0]);
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(year, month, day);
            String status = VCardConstants.PROPERTY_N;
            if (frequency.equals("0")) {
                status = "Y";
            }
            if (frequency.equals("1")) {
                calendar.add(5, 1);
            } else {
                if (frequency.equals("2")) {
                    calendar.add(5, 7);
                } else {
                    if (frequency.equals("3")) {
                        calendar.add(2, 1);
                    } else {
                        if (frequency.equals("4")) {
                            calendar.add(1, 1);
                        }
                    }
                }
            }
            if (frequency.equals("5")) {
                calendar.add(5, 1);
            }
            if (frequency.equals("6")) {
                calendar.add(5, 1);
            }
            if (frequency.equals("8")) {
                String[] custVal = cmsCustom.split("-");
                String days = custVal[0];
                String period = custVal[1];
                if (period.equals("1")) {
                    calendar.add(5, Integer.parseInt(days));
                }
                if (period.equals("2")) {
                    calendar.add(5, Integer.parseInt(days) * 7);
                }
                if (period.equals("3")) {
                    calendar.add(2, Integer.parseInt(days));
                }
                if (period.equals("4")) {
                    calendar.add(1, Integer.parseInt(days));
                }
            }
            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
            String nextMsgDate = simpleDateFormat.format(calendar.getTime());
            String[] nxdt = nextDateTime.split(" ");
            String[] nxmdate = nextMsgDate.split("/");
            int nyear = Integer.parseInt(nxmdate[2]);
            int nmonth = Integer.parseInt(nxmdate[1]);
            int nday = Integer.parseInt(nxmdate[0]);
            String strMonth = Integer.toString(nmonth);
            String strDay = Integer.toString(nday);
            if (nmonth < 10) {
                strMonth = "0" + strMonth;
            }
            if (nday < 10) {
                strDay = "0" + strDay;
            }
            nextDateTime = nyear + "-" + strMonth + "-" + strDay + " " + nxdt[1];
            if (!frequency.equals("0")) {
                if (smsStatus.equals("Y")) {
                    status = VCardConstants.PROPERTY_N;
                }
            }
            dbController.updateNextDate(Long.parseLong(_ID), nextMsgDate, nextDateTime, status);
            if (!frequency.equals("0")) {
                new ScheduleAlaramReceiver().resetAllAlarms(context, 1);
            }
            String[] contacts = recepients.replace(EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR, "+").split(",");
            String[] contactsName = smsNames.split(",");
            int sent = 0;
            for (int i = 0; i < contacts.length; i++) {
                if (!contacts[i].equals("")) {
                    if (frequency.equals("0")) {
                        if (smsStatus.equals("Y")) {
                        }
                    }
                    if (!frequency.equals("5")) {
                        if (!frequency.equals("6")) {
                            sent = 1;
                            if(msgTrigger.equals("Call Reminder")){
                                //show RemindActivity and awake the phone
                                Intent callReminderIntent = new Intent(context, RemindActivity.class).putExtra("message_id",_ID).putExtra(HomeActivity.EXTRA_DEMO_TYPE,HomeActivity.EXTRA_DEMO_TYPE_CALL);
                                callReminderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(callReminderIntent);
                            }
                            else if(msgAlarm.equals("0") || msgAlarm.equals("1") || msgAlarm.equals("2")){
                                Intent callReminderIntent = new Intent(context, RemindActivity.class).putExtra("message_id",_ID).putExtra(HomeActivity.EXTRA_DEMO_TYPE,HomeActivity.EXTRA_DEMO_TYPE_ALARM);
                                callReminderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(callReminderIntent);
                            }
                            else {
                                sendSMS(context, contacts[i], message, contactsName[i]);
                            }
                            dbController.insertHistory(group, message, nextDate, smsTime, msgType, msgTrigger, _ID);

                        } else if (!getDOW()) {
                            sent = 1;
                            if(msgTrigger.equals("Call Reminder")){
                                //show RemindActivity and awake the phone
                                Intent callReminderIntent = new Intent(context, RemindActivity.class).putExtra("message_id",_ID).putExtra(HomeActivity.EXTRA_DEMO_TYPE,HomeActivity.EXTRA_DEMO_TYPE_CALL);
                                callReminderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(callReminderIntent);
                            }
                            else if(msgAlarm.equals("0") || msgAlarm.equals("1") || msgAlarm.equals("2")){
                                Intent callReminderIntent = new Intent(context, RemindActivity.class).putExtra("message_id",_ID).putExtra(HomeActivity.EXTRA_DEMO_TYPE,HomeActivity.EXTRA_DEMO_TYPE_ALARM);
                                callReminderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(callReminderIntent);
                            }
                            else {
                                sendSMS(context, contacts[i], message, contactsName[i]);
                            }

                            dbController.insertHistory(group, message, nextDate, smsTime, msgType, msgTrigger, _ID);
                        }
                    } else if (getDOW()) {
                        sent = 1;
                        if(msgTrigger.equals("Call Reminder")){
                            //show RemindActivity and awake the phone
                            Intent callReminderIntent = new Intent(context, RemindActivity.class).putExtra("message_id",_ID).putExtra(HomeActivity.EXTRA_DEMO_TYPE,HomeActivity.EXTRA_DEMO_TYPE_CALL);
                            callReminderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(callReminderIntent);
                        }
                        else if(msgAlarm.equals("0") || msgAlarm.equals("1") || msgAlarm.equals("2")){
                            Intent callReminderIntent = new Intent(context, RemindActivity.class).putExtra("message_id",_ID).putExtra(HomeActivity.EXTRA_DEMO_TYPE,HomeActivity.EXTRA_DEMO_TYPE_ALARM);
                            callReminderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(callReminderIntent);
                        }
                        else {
                            sendSMS(context, contacts[i], message, contactsName[i]);
                        }

                        dbController.insertHistory(group, message, nextDate, smsTime, msgType, msgTrigger, _ID);
                    }
                }
            }
            if (sent == 1) {
                String notificationMessage="SMS Messages Sent";
                if(msgTrigger.equals("Call Reminder")){
                    notificationMessage="Call Reminder";
                }
                else if(msgAlarm.equals("0") || msgAlarm.equals("1") || msgAlarm.equals("2")){
                    notificationMessage="Alarm Reminder";
                }
                showSMSNotification(context,notificationMessage);
            }
            cursor.moveToNext();
        }

    }

    private void showSMSNotification(Context context,String message) {
        try {
            Builder builder = new Builder(context);
            builder.setSmallIcon(R.drawable.ic_rooster_24dp);
            builder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, SentRemindersActivity.class), 0));
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
            builder.setContentTitle(context.getResources().getString(R.string.app_name));
            builder.setContentText(message);
            builder.setSubText(context.getResources().getString(R.string.tap_reminder_label));
            int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            ((NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE)).notify(m, builder.build());
        }catch(Exception ex){

        }
    }



    private void sendSMS(Context context, String contact, String message, String name) {
       // Toast.makeText(context, "Sending SMS to " + contact + "Message " + message, Toast.LENGTH_SHORT).show();
        SmsManager sms = SmsManager.getDefault();
        sms.sendMultipartTextMessage(contact, null, sms.divideMessage(message), null, null);
    }

    private boolean getDOW() {
        int day = Calendar.getInstance().get(7);
        return day >= 2 && day <= 6;
    }


}
