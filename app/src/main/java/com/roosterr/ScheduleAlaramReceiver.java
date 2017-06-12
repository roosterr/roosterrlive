package com.roosterr;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.digits.sdk.vcard.VCardConfig;
import com.google.common.primitives.Ints;
import io.fabric.sdk.android.BuildConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ScheduleAlaramReceiver extends WakefulBroadcastReceiver {
    private PendingIntent alarmIntent;
    private AlarmManager alarmManager;

    public void onReceive(Context context, Intent intent) {
        WakefulBroadcastReceiver.startWakefulService(context, new Intent(context, SchedulingService.class));
    }

    public void resetAllAlarms(Context context, int toast) {
        SQLController dbController = new SQLController(context);
        dbController.open();
        Cursor cursor = dbController.fetch();
        while (!cursor.isAfterLast()) {
            String frequency = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_REPEAT));
            String nextDate = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_NEXT));
            String msgTime = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_TIME));
            String[] date = nextDate.split("/");
            int year = Integer.parseInt(date[2]);
            int month = Integer.parseInt(date[1]) - 1;
            int day = Integer.parseInt(date[0]);
            String[] time = msgTime.split(":");
            int hour = Integer.parseInt(time[0]);
            if (time[1].contains("PM")) {
                if (hour == 12) {
                    hour = 0;
                } else {
                    hour += 12;
                }
            }
            time[1] = time[1].replace("PM", "").replace("AM", "").replace(" ", "");
            int min = Integer.parseInt(time[1]);
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(year, month, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            Intent intent = new Intent(context, SMSReciever.class);
            this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int _id = (int) System.currentTimeMillis();
            if (frequency.equals("0")) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(context, _id, intent, PendingIntent.FLAG_ONE_SHOT));
            } else {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,PendingIntent.getBroadcast(context, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
            }
            ComponentName componentName = new ComponentName(context, SMSBootReceiver.class);

            PackageManager pm = context.getPackageManager();

            pm.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            cursor.moveToNext();
        }
        //set weekly notification
        if(toast==0) {
            String msg_date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            String msg_time = "09:00";
            String freq = "1";
            scheduleNotificationAlarm(freq, msg_date, msg_time, context);
        }
    }

    public void scheduleAlarmMessage(String frequency, String msgDate, String msgTime, Context context) {
        String[] date = msgDate.split("/");
        int year = Integer.parseInt(date[2]);
        int month = Integer.parseInt(date[1]) - 1;
        int day = Integer.parseInt(date[0]);
        String[] time = msgTime.split(":");
        int hour = Integer.parseInt(time[0]);
        if (time[1].contains("PM")) {
            if (hour == 12) {
                hour = 0;
            } else {
                hour += 12;
            }
        }
        time[1] = time[1].replace("PM", "").replace("AM", "").replace(" ","");
        int min = Integer.parseInt(time[1]);
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(year, month, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        Intent intentAlarm = new Intent(context, SMSReciever.class);
        int _id = (int) System.currentTimeMillis();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (frequency.equals("0")) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(context, _id, intentAlarm, PendingIntent.FLAG_ONE_SHOT));
            //Toast.makeText(context, "Alarm Scheduled For "+msgDate+" : "+msgTime, Toast.LENGTH_LONG).show();
        }
        else{
            //set the alarm for particular time
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,PendingIntent.getBroadcast(context, _id, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
            //Toast.makeText(context, "Alarm Scheduled For- "+msgDate+" : "+msgTime, Toast.LENGTH_LONG).show();
        }

        ComponentName receiver = new ComponentName(context, SMSBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void scheduleNotificationAlarm(String frequency,String msgDate,String msgTime,Context context) {
            String[] date = msgDate.split("/");
            int year = Integer.parseInt(date[2]);
            int month = Integer.parseInt(date[1]) - 1;
            int day = Integer.parseInt(date[0]);
            String[] time = msgTime.split(":");
            int hour = Integer.parseInt(time[0]);
            if (time[1].contains("PM")) {
                if (hour == 12) {
                    hour = 0;
                } else {
                    hour += 12;
                }
            }
            time[1] = time[1].replace("PM", "").replace("AM", "").replace(" ","");
            int min = Integer.parseInt(time[1]);
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(System.currentTimeMillis());
            //Till here I have the current time
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = df.format(calendar.getTime());
            Date currDT=null;
            try {
                currDT = df.parse(currentDate);
            } catch(Exception ex){}
            //calendar.set(year, month, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            //Now I have the old time
            String alarmDate = df.format(calendar.getTime());
            Date alarmDT = null;
            try {
                alarmDT = df.parse(alarmDate);
            } catch (Exception ex) {

            }
            if (alarmDT.compareTo(currDT) < 0) {
                calendar.add(Calendar.DAY_OF_MONTH,1);
            }

            Intent intentAlarm = new Intent(context, NotificationReciever.class);
            int _id = (int) System.currentTimeMillis();
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            //set the alarm for particular time
            int _REFRESH_INTERVAL = 60 * 1;
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,PendingIntent.getBroadcast(context, _id, intentAlarm, PendingIntent.FLAG_CANCEL_CURRENT));
            ComponentName receiver = new ComponentName(context, SMSBootReceiver.class);
            PackageManager pm = context.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
    }

    public void resetWeekendAlarm(Context context, int i) {
        SQLController dbController = new SQLController(context);
        dbController.open();
        Cursor cursor = dbController.fetchWeekendReminder();
        while (!cursor.isAfterLast()) {
            String frequency = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_REPEAT));
            String nextDate = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_NEXT));
            String msgTime = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_TIME));
            String[] date = nextDate.split("/");
            int year = Integer.parseInt(date[2]);
            int month = Integer.parseInt(date[1]) - 1;
            int day = Integer.parseInt(date[0]);
            String[] time = msgTime.split(":");
            int hour = Integer.parseInt(time[0]);
            if (time[1].contains("PM")) {
                if (hour == 12) {
                    hour = 0;
                } else {
                    hour += 12;
                }
            }
            time[1] = time[1].replace("PM", "").replace("AM", "").replace(" ", "");
            int min = Integer.parseInt(time[1]);
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(year, month, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            Intent intent = new Intent(context, SMSReciever.class);
            this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int _id = (int) System.currentTimeMillis();
            if (frequency.equals("0")) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PendingIntent.getBroadcast(context, _id, intent, PendingIntent.FLAG_ONE_SHOT));
            } else {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,PendingIntent.getBroadcast(context, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
            }
            ComponentName componentName = new ComponentName(context, SMSBootReceiver.class);

            PackageManager pm = context.getPackageManager();

            pm.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            cursor.moveToNext();
        }
    }
}
