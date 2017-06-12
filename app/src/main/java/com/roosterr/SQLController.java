package com.roosterr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.digits.sdk.vcard.VCardConstants;
import io.fabric.sdk.android.BuildConfig;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

public class SQLController {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private Context ourcontext;

    public SQLController(Context c) {
        this.ourcontext = c;
    }

    public SQLController open() throws SQLException {
        this.dbHelper = DBHelper.getInstance(this.ourcontext);
        this.database = this.dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.dbHelper.close();
    }

    public String insert(String group_id, String active, String message, String date, String time, String repeat, String next, String id, String custom, String type, String trigger,String alarm) {
        String[] msgTime = time.split(":");
        int hour = Integer.parseInt(msgTime[0]);
        if (msgTime[1].contains("PM")) {
            if (hour == 12) {
                hour = 0;
            } else {
                hour += 12;
            }
        }
        msgTime[1] = msgTime[1].replace("PM", "").replace("AM", "").replace(" ", "");
        int min = Integer.parseInt(msgTime[1]);
        String minTime = Integer.toString(min);
        String hourTime = Integer.toString(hour);
        if (min < 10) {
            minTime = "0" + minTime;
        }
        if (hour < 10) {
            hourTime = "0" + hourTime;
        }
        String time_24 = hourTime + ":" + minTime;
        String[] nextdate = next.split("/");
        int year = Integer.parseInt(nextdate[2]);
        int month = Integer.parseInt(nextdate[1]);
        int day = Integer.parseInt(nextdate[0]);
        String strMonth = Integer.toString(month);
        String strDay = Integer.toString(day);
        if (month < 10) {
            strMonth = "0" + strMonth;
        }
        if (day < 10) {
            strDay = "0" + strDay;
        }
        String date_24 = year + "-" + strMonth + "-" + strDay + " " + time_24;
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.GROUP_ID, group_id);
        contentValue.put(DBHelper.SMS_ACTIVE, active);
        contentValue.put(SettingsJsonConstants.PROMPT_MESSAGE_KEY, message);
        contentValue.put(DBHelper.SMS_DATE, date);
        contentValue.put(DBHelper.SMS_TIME, time);
        contentValue.put(DBHelper.SMS_REPEAT, repeat);
        contentValue.put(DBHelper.SMS_NEXT, next);
        contentValue.put(DBHelper.SMS_ID, id);
        contentValue.put(DBHelper.SMS_DateTime, date_24);
        contentValue.put(DBHelper.SMS_Status, VCardConstants.PROPERTY_N);
        if (repeat.equals("8")) {
            contentValue.put(DBHelper.SMS_CUSTOM, custom);
        }
        contentValue.put(DBHelper.SMS_TYPE, type);
        contentValue.put(DBHelper.SMS_TRIGGER, trigger);
        contentValue.put(DBHelper.SMS_ALARM, alarm);
        long insert_id=this.database.insert(DBHelper.TABLE_NAME, null, contentValue);

        return  Long.toString(insert_id);
    }

    public Cursor fetch() {
        Cursor cursor = this.database.query(DBHelper.TABLE_NAME, new String[]{DBHelper._ID, DBHelper.GROUP_ID, DBHelper.SMS_ACTIVE, SettingsJsonConstants.PROMPT_MESSAGE_KEY, DBHelper.SMS_DATE, DBHelper.SMS_TIME, DBHelper.SMS_REPEAT, DBHelper.SMS_NEXT, DBHelper.SMS_DateTime, DBHelper.SMS_TYPE, DBHelper.SMS_TRIGGER}, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchMessagesByDateTime(String msgDate) {
        Cursor cursor = this.database.rawQuery("select * from  Scheduler where Datetime(sms_datetime) <= Datetime('" + msgDate + "') and "+DBHelper.SMS_Status+"='N' and " + DBHelper.SMS_ACTIVE + "=1", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchMessageById(long _id) {
        Cursor cursor = this.database.rawQuery("select * from  Scheduler where _id='" + _id + "'", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String group, String active, String message, String date, String time, String repeat, String next, String custom, String type, String trigger,String alarm) {
        String[] msgTime = time.split(":");
        int hour = Integer.parseInt(msgTime[0]);
        if (msgTime[1].contains("PM")) {
            if (hour == 12) {
                hour = 0;
            } else {
                hour += 12;
            }
        }
        msgTime[1] = msgTime[1].replace("PM", "").replace("AM", "").replace(" ", "");
        int min = Integer.parseInt(msgTime[1]);
        String minTime = Integer.toString(min);
        String hourTime = Integer.toString(hour);
        if (min < 10) {
            minTime = "0" + minTime;
        }
        if (hour < 10) {
            hourTime = "0" + hourTime;
        }
        String time_24 = hourTime + ":" + minTime;
        String[] nextdate = next.split("/");
        int year = Integer.parseInt(nextdate[2]);
        int month = Integer.parseInt(nextdate[1]);
        int day = Integer.parseInt(nextdate[0]);
        String strMonth = Integer.toString(month);
        String strDay = Integer.toString(day);
        if (month < 10) {
            strMonth = "0" + strMonth;
        }
        if (day < 10) {
            strDay = "0" + strDay;
        }
        String date_24 = year + "-" + strMonth + "-" + strDay + " " + time_24;
        String status = VCardConstants.PROPERTY_N;
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.GROUP_ID, group);
        contentValue.put(DBHelper.SMS_ACTIVE, active);
        contentValue.put(SettingsJsonConstants.PROMPT_MESSAGE_KEY, message);
        contentValue.put(DBHelper.SMS_DATE, date);
        contentValue.put(DBHelper.SMS_TIME, time);
        contentValue.put(DBHelper.SMS_REPEAT, repeat);
        contentValue.put(DBHelper.SMS_NEXT, next);
        if (repeat.equals("0")) {
            contentValue.put(DBHelper.SMS_Status, status);
        }
        if (repeat.equals("8")) {
            contentValue.put(DBHelper.SMS_CUSTOM, custom);
        }
        contentValue.put(DBHelper.SMS_DateTime, date_24);
        contentValue.put(DBHelper.SMS_TYPE, type);
        contentValue.put(DBHelper.SMS_TRIGGER, trigger);
        contentValue.put(DBHelper.SMS_ALARM, alarm);
        return this.database.update(DBHelper.TABLE_NAME, contentValue, "_id = " + _id, null);
    }

    public int updateNextDate(long _id, String next, String nextDateTime, String status) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.SMS_NEXT, next);
        contentValue.put(DBHelper.SMS_DateTime, nextDateTime);
        contentValue.put(DBHelper.SMS_Status, status);
        return this.database.update(DBHelper.TABLE_NAME, contentValue, "_id = " + _id, null);
    }
    public int updateStatus(long _id,String status){
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.SMS_Status, status);
        return this.database.update(DBHelper.TABLE_NAME, contentValue, "_id = " + _id, null);
    }
    public void delete(long _id) {
        this.database.delete(DBHelper.TABLE_NAME, "_id=" + _id, null);
    }

    public void deleteHistory(long _id) {
        this.database.delete(DBHelper.HISTORY_TABLE, "_id=" + _id, null);
    }

    public void deleteAllHistory() {
        this.database.execSQL("delete from  History");
    }

    public void insertHistory(String group, String message, String date, String time, String type, String trigger, String msg_id) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.GROUP_ID, group);
        contentValue.put(SettingsJsonConstants.PROMPT_MESSAGE_KEY, message);
        contentValue.put(DBHelper.SMS_DATE, date);
        contentValue.put(DBHelper.SMS_TIME, time);
        contentValue.put(DBHelper.SMS_TYPE, type);
        contentValue.put(DBHelper.SMS_TRIGGER, trigger);
        contentValue.put(DBHelper.SMS_ID, msg_id);
        this.database.insert(DBHelper.HISTORY_TABLE, null, contentValue);
    }

    public Cursor fetchHistory() {
        Cursor cursor = this.database.query(DBHelper.HISTORY_TABLE, new String[]{DBHelper._ID, DBHelper.GROUP_ID, SettingsJsonConstants.PROMPT_MESSAGE_KEY, DBHelper.SMS_DATE, DBHelper.SMS_TIME, DBHelper.SMS_TYPE, DBHelper.SMS_TRIGGER, DBHelper.SMS_ID}, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void createGroups(String group) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.GROUP_NAME, group);
        this.database.insert(DBHelper.GROUP_TABLE, null, contentValue);
    }

    public void saveGroupWithImage(String group, String image) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.GROUP_NAME, group);
        contentValue.put(DBHelper.GROUP_IMAGE, image);
        this.database.insert(DBHelper.GROUP_TABLE, null, contentValue);
    }

    public Cursor fetchGroups() {
        Cursor cursor = this.database.query(DBHelper.GROUP_TABLE, new String[]{DBHelper._ID, DBHelper.GROUP_NAME, DBHelper.GROUP_IMAGE}, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getGroupContactCount(Integer Group_ID) {
        Cursor cursor = this.database.rawQuery("select count(*) 'group_contact_count' from  Group_Contacts where group_id = " + Group_ID, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getGroupSMSCount(Integer Group_ID) {
        Cursor cursor = this.database.rawQuery("select count(*) 'group_sms_count' from  Scheduler where group_id = " + Group_ID, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int updateGroupImage(String groupId, String image) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.GROUP_IMAGE, image);
        return this.database.update(DBHelper.GROUP_TABLE, contentValue, "_id = " + groupId, null);
    }

    public Cursor fetchGroupById(long _id) {
        Cursor cursor = this.database.rawQuery("select * from  Groups where _id='" + _id + "'", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getGroupContacts(Integer Group_ID) {
        Cursor cursor = this.database.rawQuery("select * from  Group_Contacts where group_id='" + Group_ID + "'", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void deleteExistingContact(String Group_ID, String name, String number) {
        this.database.execSQL("delete from  Group_Contacts where group_id='" + Group_ID + "' and " + DBHelper.GROUP_CONTACT_NUMBER + "='" + number + "'");
    }

    public void saveGroupContacts(String Group_ID, String name, String number) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.GROUP_ID, Group_ID);
        contentValue.put(DBHelper.GROUP_CONTACT_NAME, name);
        contentValue.put(DBHelper.GROUP_CONTACT_NUMBER, number);
        this.database.insert(DBHelper.GROUP_CONTACTS_TABLE, null, contentValue);
    }

    public int updateGroupName(String Group_ID, String name) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.GROUP_NAME, name);
        return this.database.update(DBHelper.GROUP_TABLE, contentValue, "_id = " + Group_ID, null);
    }

    public Cursor fetchMessagesByGroupID(String Group_ID) {
        Cursor cursor = this.database.rawQuery("select * from  Scheduler where group_id='" + Group_ID + "'", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void deleteGroup(String Group_ID) {
        this.database.execSQL("delete from  Groups where _id='" + Group_ID + "'");
        this.database.execSQL("delete from  Group_Contacts where group_id='" + Group_ID + "'");
        this.database.execSQL("delete from  Scheduler where group_id='" + Group_ID + "'");
    }

    public int toggleSMS(String msgId, String status) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.SMS_ACTIVE, status);
        return this.database.update(DBHelper.TABLE_NAME, contentValue, "_id = " + msgId, null);
    }

    public void cloneMessage(String groupId, String msgId) {
        this.database.execSQL("Insert into Scheduler (message,sms_date,sms_time,sms_repeat,sms_next,sms_id,sms_datetime,sms_status,group_id,sms_active,sms_type,sms_trigger) Select message,sms_date,sms_time,sms_repeat,sms_next,sms_id,sms_datetime,sms_status, '" + groupId + "' AS " + DBHelper.GROUP_ID + "," + DBHelper.SMS_ACTIVE + "," + DBHelper.SMS_TYPE + "," + DBHelper.SMS_TRIGGER + " from " + DBHelper.TABLE_NAME + " where " + DBHelper._ID + "=" + msgId);
    }

    public Cursor fetchGroupByName(String group_name) {
        Cursor cursor = this.database.rawQuery("select * from  Groups where group_name='" + group_name + "'", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void createUser(String user_number,String no_ads,String go_pro,String both) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.USER_NUMBER, user_number);
        contentValue.put(DBHelper.NO_ADS, no_ads);
        contentValue.put(DBHelper.GO_PRO, go_pro);
        contentValue.put(DBHelper.BOTH_PRODUCTS, both);
        contentValue.put(DBHelper.IS_READY, 0);
        this.database.insert(DBHelper.USERS_TABLE, null, contentValue);
    }

    public Cursor getUser() {
        Cursor cursor = this.database.rawQuery("select * from  Users_Table LIMIT 1", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public String createChecklist(String checklist) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.CHECKLIST_NAME, checklist);
        this.database.insert(DBHelper.CHECKLIST_TABLE, null, contentValue);
        Cursor cursor = this.database.rawQuery("SELECT last_insert_rowid() '_id'", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Integer _ID=-1;
        while (!cursor.isAfterLast()) {
            _ID = Integer.valueOf(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper._ID))));
            cursor.moveToNext();
        }
        return _ID.toString();
    }

    public int updateChecklistName(String Checklist_ID, String name) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.CHECKLIST_NAME, name);
        return this.database.update(DBHelper.CHECKLIST_TABLE, contentValue, "_id = " + Checklist_ID, null);
    }

    public void createChecklistItems(String Checklist_ID,String items) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.CHECKLIST_ID, Checklist_ID);
        contentValue.put(DBHelper.CHECKLIST_ITEM, items);
        this.database.insert(DBHelper.CHECKLIST_ITEMS_TABLE, null, contentValue);
    }
    public void updateChecklistItems(String Item_ID,String items) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.CHECKLIST_ITEM, items);
        this.database.update(DBHelper.CHECKLIST_ITEMS_TABLE, contentValue,"_id = " + Item_ID, null);
    }

    public Cursor fetchChecklistByName(String checklist_name){
        Cursor cursor = this.database.rawQuery("select * from  Checklist where checklist_name='" + checklist_name + "'", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchChecklists() {
        Cursor cursor = this.database.query(DBHelper.CHECKLIST_TABLE, new String[]{DBHelper._ID, DBHelper.CHECKLIST_NAME}, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public Cursor fetchChecklistItems(String checklist_id){
        Cursor cursor = this.database.rawQuery("select * from Checklist_Items where checklist_id='" + checklist_id + "'", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public Cursor fetchTodayMessages(String type) {
        if(type.equals("0")) {
            Cursor cursor = this.database.rawQuery("select * from  Scheduler where date(sms_datetime) = date('now') and " + DBHelper.SMS_ACTIVE + "=1", null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor;
        }
        else{
            Cursor cursor = this.database.rawQuery("select * from  Scheduler where date(sms_datetime) = date('now') and "+DBHelper.SMS_TYPE+"='"+type+"' and " + DBHelper.SMS_ACTIVE + "=1", null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor;
        }
    }
    public Cursor fetchWeeksMessages(String date,String type) {
        if(type.equals("0")) {
            Cursor cursor = this.database.rawQuery("select * from  Scheduler where date(sms_datetime) BETWEEN date('now','+1 day') and date('" + date + "') and " + DBHelper.SMS_ACTIVE + "=1", null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor;
        }
        else{
            Cursor cursor = this.database.rawQuery("select * from  Scheduler where date(sms_datetime) BETWEEN date('now','+1 day') and date('" + date + "') and "+DBHelper.SMS_TYPE+"='"+type+"' and " + DBHelper.SMS_ACTIVE + "=1", null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor;

        }
    }
    public Cursor fetchUpcomingMessages(String date,String type) {
        if(type.equals("0")) {
            Cursor cursor = this.database.rawQuery("select * from  Scheduler where date(sms_datetime) > date('" + date + "') and " + DBHelper.SMS_ACTIVE + "=1", null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor;
        }
        else{
            Cursor cursor = this.database.rawQuery("select * from  Scheduler where date(sms_datetime) > date('" + date + "') and "+DBHelper.SMS_TYPE+"='"+type+"' and " + DBHelper.SMS_ACTIVE + "=1", null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor;
        }
    }

    public void updatePurchase(String phone, String no_ads, String go_pro, String both) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.NO_ADS, no_ads);
        contentValue.put(DBHelper.GO_PRO, go_pro);
        contentValue.put(DBHelper.BOTH_PRODUCTS, both);
        this.database.update(DBHelper.USERS_TABLE, contentValue,"user_number='" + phone +"'",null);
    }

    public void resetDB() {
        this.database.execSQL("delete from Scheduler");
        this.database.execSQL("delete from History");
        this.database.execSQL("delete from Groups");
        this.database.execSQL("delete from Group_Contacts");
        this.database.execSQL("delete from Users_Table");
        //this.database.execSQL("DROP TABLE IF EXISTS Users_Table");
        this.database.execSQL("delete from Checklist");
        this.database.execSQL("delete from Checklist_Items");
    }


    public void insertWeeklyReminder(String group_id, String active, String message, String date, String time, String repeat, String next, String id, String custom, String type, String trigger,String alarm) {
        String[] msgTime = time.split(":");
        int hour = Integer.parseInt(msgTime[0]);
        if (msgTime[1].contains("PM")) {
            if (hour == 12) {
                hour = 0;
            } else {
                hour += 12;
            }
        }
        msgTime[1] = msgTime[1].replace("PM", "").replace("AM", "").replace(" ", "");
        int min = Integer.parseInt(msgTime[1]);
        String minTime = Integer.toString(min);
        String hourTime = Integer.toString(hour);
        if (min < 10) {
            minTime = "0" + minTime;
        }
        if (hour < 10) {
            hourTime = "0" + hourTime;
        }
        String time_24 = hourTime + ":" + minTime;
        String[] nextdate = next.split("/");
        int year = Integer.parseInt(nextdate[2]);
        int month = Integer.parseInt(nextdate[1]);
        int day = Integer.parseInt(nextdate[0]);
        String strMonth = Integer.toString(month);
        String strDay = Integer.toString(day);
        if (month < 10) {
            strMonth = "0" + strMonth;
        }
        if (day < 10) {
            strDay = "0" + strDay;
        }
        String date_24 = year + "-" + strMonth + "-" + strDay + " " + time_24;
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.GROUP_ID, group_id);
        contentValue.put(DBHelper.SMS_ACTIVE, active);
        contentValue.put(SettingsJsonConstants.PROMPT_MESSAGE_KEY, message);
        contentValue.put(DBHelper.SMS_DATE, date);
        contentValue.put(DBHelper.SMS_TIME, time);
        contentValue.put(DBHelper.SMS_REPEAT, repeat);
        contentValue.put(DBHelper.SMS_NEXT, next);
        contentValue.put(DBHelper.SMS_ID, id);
        contentValue.put(DBHelper.SMS_DateTime, date_24);
        contentValue.put(DBHelper.SMS_Status, VCardConstants.PROPERTY_N);
        if (repeat.equals("8")) {
            contentValue.put(DBHelper.SMS_CUSTOM, custom);
        }
        contentValue.put(DBHelper.SMS_TYPE, type);
        contentValue.put(DBHelper.SMS_TRIGGER, trigger);
        contentValue.put(DBHelper.SMS_ALARM, alarm);
        this.database.insert(DBHelper.WEEKLY_REMINDER, null, contentValue);
    }

    public Cursor fetchWeekendReminder() {
        Cursor cursor = this.database.rawQuery("select * from  Weeklyscheduler ", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void updateUserStatus() {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.IS_READY, 1);
        this.database.update(DBHelper.USERS_TABLE, contentValue,"_id=1",null);
    }

    public Cursor fetchWeeklyMessagesByDateTime(String msgDate) {
        Cursor cursor = this.database.rawQuery("select * from  Weeklyscheduler",null);// where Datetime(sms_datetime) <= Datetime('" + msgDate + "') and "+DBHelper.SMS_Status+"='N' and " + DBHelper.SMS_ACTIVE + "=1", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public int updateNextWeeklyDate(long _id, String status) {
        ContentValues contentValue = new ContentValues();
        //contentValue.put(DBHelper.SMS_NEXT, next);
        //contentValue.put(DBHelper.SMS_DateTime, nextDateTime);
        contentValue.put(DBHelper.SMS_Status, status);
        return this.database.update(DBHelper.WEEKLY_REMINDER, contentValue, "_id = " + _id, null);
    }

    public int updateWeeklyStatus(String status){
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.SMS_Status, status);
        return this.database.update(DBHelper.WEEKLY_REMINDER, contentValue, "_id =1", null);
    }
}
