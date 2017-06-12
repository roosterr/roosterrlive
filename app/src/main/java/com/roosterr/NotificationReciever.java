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

import com.digits.sdk.vcard.VCardConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import io.fabric.sdk.android.services.events.EventsFilesManager;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

public class NotificationReciever extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        sendWeeklyNotification(context);
    }
    private void showWeeklyNotification(Context context,String message) {
        try {
            Builder builder = new Builder(context);
            builder.setSmallIcon(R.drawable.ic_rooster_24dp);
            builder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, HomeActivity.class), 0));
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
            builder.setContentTitle(context.getResources().getString(R.string.app_name));
            builder.setContentText(message);
            builder.setSubText(context.getResources().getString(R.string.tap_reminder_label));
            int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            ((NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE)).notify(m, builder.build());
        } catch(Exception ex) {

        }
    }

    private void sendWeeklyNotification(Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.ENGLISH);
        String date_time = simpleDateFormat.format(Calendar.getInstance().getTime());
        String[] date_time_array = date_time.split(" ");
        //String msgDate = date_time_array[0];
        String msgTime = date_time_array[1];
        Calendar c = Calendar.getInstance();

        //if (msgTime.equals("09:00")) {

            SQLController dbController = new SQLController(context);
            dbController.open();

            //Cursor cursor = dbController.fetchWeeklyMessagesByDateTime(date_time);
            //while (!cursor.isAfterLast()) {
                //String _ID = cursor.getString(cursor.getColumnIndex(DBHelper._ID));
                //String Status = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_Status));
                //String nextDateTime = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_DateTime));
                //String nextDate = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_NEXT));
                //simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
                ////String nextMsgDate = simpleDateFormat.format(c.getTime());

                //String[] date = nextDate.split("/");
                //int year = Integer.parseInt(date[2]);
                //int month = Integer.parseInt(date[1])- 1;
                //int day = Integer.parseInt(date[0]);

                //Calendar calendar = new GregorianCalendar();
                //calendar.setTimeInMillis(System.currentTimeMillis());
                //calendar.set(year, month, day);
                //calendar.add(5, 1);
                //String nextMsgDate = simpleDateFormat.format(calendar.getTime());

                //String[] nxdt = nextDateTime.split(" ");
                //String[] nxmdate = nextMsgDate.split("/");
                //int nyear = Integer.parseInt(nxmdate[2]);
                //int nmonth = Integer.parseInt(nxmdate[1]);
                //int nday = Integer.parseInt(nxmdate[0]);
                //String strMonth = Integer.toString(nmonth);
                //String strDay = Integer.toString(nday);
                //if (nmonth < 10) {
                //    strMonth = "0" + strMonth;
                //}
                //if (nday < 10) {
                //    strDay = "0" + strDay;
                //}
                //nextDateTime = nyear + "-" + strMonth + "-" + strDay + " " + nxdt[1];
                //nextMsgDate = strDay  + "/" + strMonth + "/" +nyear ;
                //dbController.updateNextWeeklyDate(Long.parseLong(_ID), nextMsgDate, nextDateTime, "N");
                //ursor.moveToNext();
                //if (Status.equals("N")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

                    c.setTime(new Date()); // Now use today date.
                    c.add(Calendar.DATE, 7); // Adding 7 days
                    String weekdate = sdf.format(c.getTime());

                    Cursor cursor = dbController.fetchWeeksMessages(weekdate, "0");
                    Cursor todayCursor = dbController.fetchTodayMessages("0");
                    String reminder_label = context.getResources().getString(R.string.reminder_label);
                    Integer todayCount = 0;
                    Integer WeekCount = 0;
                    if (todayCursor == null)
                        todayCount = 0;
                    else {
                        if (todayCursor.isAfterLast()) {
                            todayCount = 0;
                        } else {
                            while (!todayCursor.isAfterLast()) {
                                todayCount = todayCursor.getCount();
                                break;
                                //showWeeklyNotification(context, context.getResources().getString(R.string.reminder_prefix_label) + " " + records.toString() + " " + context.getResources().getString(R.string.reminder_append_label));
                                //todayCursor.moveToNext();
                            }
                        }
                    }
                    if (cursor == null) {
                        WeekCount = 0;
                    } else {
                        if (cursor.isAfterLast()) {
                            WeekCount = 0;
                            //showWeeklyNotification(context, context.getResources().getString(R.string.reminder_notification_label));
                        } else {
                            while (!cursor.isAfterLast()) {
                                WeekCount = cursor.getCount();
                                break;
                                //showWeeklyNotification(context, context.getResources().getString(R.string.reminder_prefix_label) + " " + records.toString() + " " + context.getResources().getString(R.string.reminder_append_label));
                                //cursor.moveToNext();
                            }
                        }

                    }
                    reminder_label = reminder_label.replace("{0}", "(" + todayCount.toString() + ")");
                    reminder_label = reminder_label.replace("{1}", "(" + WeekCount.toString() + ")");
                    showWeeklyNotification(context, reminder_label);
                    //dbController.updateWeeklyStatus("Y");
                //}
                //int dow = c.get (Calendar.DAY_OF_WEEK);
                //if ((dow == Calendar.MONDAY || dow == Calendar.THURSDAY) && msgTime.equals("10:00") ) {
                //new ScheduleAlaramReceiver().resetWeekendAlarm(context, 1);
            //}
        //}

    }
}
