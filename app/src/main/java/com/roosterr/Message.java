package com.roosterr;

import android.content.Context;
import android.database.Cursor;
import com.twitter.sdk.android.core.internal.TwitterApiConstants.Base;
import io.fabric.sdk.android.BuildConfig;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
    public String _custom;
    public String _group;
    public String _id;
    public String _message;
    public String _sms_active;
    public String _sms_date;
    public String _sms_id;
    public String _sms_next;
    public String _sms_repeat;
    public String _sms_time;
    public String trigger;
    public String type;
    public String group_name;

    public Message(String id, String group, String sms_active, String message, String sms_next, String sms_time, String sms_repeat, String msgType, String msgTrigger) {
        this._id = id;
        this._group = group;
        this._sms_active = sms_active;
        this._message = message;
        this._sms_time = sms_time;
        this._sms_repeat = sms_repeat;
        this._sms_next = sms_next;
        this.type = msgType;
        this.trigger = msgTrigger;
    }

    public Message(String id, String group, String sms_active, String message, String sms_next, String sms_time, String sms_repeat, String msgType, String msgTrigger, String custom) {
        this._id = id;
        this._group = group;
        this._sms_active = sms_active;
        this._message = message;
        this._sms_time = sms_time;
        this._sms_repeat = sms_repeat;
        this._sms_next = sms_next;
        this.type = msgType;
        this.trigger = msgTrigger;
        if (custom == null) {
            return;
        }
        if (custom.indexOf("-") != -1) {
            this._custom = custom;
        } else if(custom.indexOf("alarm")!=-1) {
            this._custom=custom.replace("alarm","");
        }
        else{
            this._sms_id = custom;
        }
    }

    public Message(String id, String group, String sms_active, String message, String sms_next, String sms_time, String sms_repeat, String msgType, String msgTrigger, String custom,String _group_name) {
        this._id = id;
        this._group = group;
        this._sms_active = sms_active;
        this._message = message;
        this._sms_time = sms_time;
        this._sms_repeat = sms_repeat;
        this._sms_next = sms_next;
        this.type = msgType;
        this.trigger = msgTrigger;
        this.group_name=_group_name;
        if (custom == null) {
            return;
        }
        if (custom.indexOf("-") != -1) {
            this._custom = custom;
        } else {
            this._sms_id = custom;
        }
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put(Base.PARAM_ID, this._id);
            obj.put("group", this._group);
            obj.put(DBHelper.SMS_ACTIVE, this._sms_active);
            obj.put(SettingsJsonConstants.PROMPT_MESSAGE_KEY, this._message);
            obj.put(DBHelper.SMS_TIME, this._sms_time);
            obj.put(DBHelper.SMS_REPEAT, this._sms_repeat);
            obj.put(DBHelper.SMS_NEXT, this._sms_next);
        } catch (JSONException e) {
        }
        return obj;
    }

    public static List<Message> getSentMessages(Context context) {
        SQLController dbController = new SQLController(context);
        dbController.open();
        Cursor MessageCursor = dbController.fetchHistory();
        StringBuilder str = new StringBuilder();
        List<Message> messages = new ArrayList();
        while (!MessageCursor.isAfterLast()) {
            String Message_ID = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper._ID));
            String group = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.GROUP_ID));
            String active = "";
            String message = MessageCursor.getString(MessageCursor.getColumnIndex(SettingsJsonConstants.PROMPT_MESSAGE_KEY)).replaceAll("'", "__");
            String frequency = "";
            String nextDate = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_DATE));
            String msgTime = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_TIME));
            String msgType = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_TYPE));
            String msgTrigger = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_TRIGGER));
            String smsID = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_ID));
            if (msgTrigger.equals("Scheduled SMS")) {
                msgTrigger = "SMS";
            }
            if (msgTrigger.equals("Call Reminder")) {
                msgTrigger = "Call";
            }
            String group_image = "";
            String group_name = "";
            Cursor GroupCursor = dbController.fetchGroupById((long) Integer.parseInt(group));
            while (!GroupCursor.isAfterLast()) {
                group_image = GroupCursor.getString(GroupCursor.getColumnIndex(DBHelper.GROUP_IMAGE));
                group_name=GroupCursor.getString(GroupCursor.getColumnIndex(DBHelper.GROUP_NAME));
                GroupCursor.moveToNext();
            }
            GroupCursor.close();
            messages.add(new Message(Message_ID, group_image, active, message, nextDate, msgTime, frequency, msgType, msgTrigger, smsID,group_name));
            MessageCursor.moveToNext();
        }
        MessageCursor.close();
        return messages;
    }

    public static Message getMessageById(Context context, String msgid) {
        SQLController sQLController = new SQLController(context);
        sQLController.open();
        Cursor cursor = sQLController.fetchMessageById(Long.parseLong(msgid));
        Message msg = null;
        while (!cursor.isAfterLast()) {
            String _ID = cursor.getString(cursor.getColumnIndex(DBHelper._ID));
            String group = cursor.getString(cursor.getColumnIndex(DBHelper.GROUP_ID));
            String active = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_ACTIVE));
            String message = cursor.getString(cursor.getColumnIndex(SettingsJsonConstants.PROMPT_MESSAGE_KEY)).replaceAll("'", "__");
            String frequency = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_REPEAT));
            msg = new Message(_ID, group, active, message, cursor.getString(cursor.getColumnIndex(DBHelper.SMS_NEXT)), cursor.getString(cursor.getColumnIndex(DBHelper.SMS_TIME)), frequency, cursor.getString(cursor.getColumnIndex(DBHelper.SMS_TYPE)), cursor.getString(cursor.getColumnIndex(DBHelper.SMS_TRIGGER)), cursor.getString(cursor.getColumnIndex(DBHelper.SMS_CUSTOM)));
            cursor.moveToNext();
        }
        return msg;
    }
    public static Message getMessageForAlarmById(Context context, String msgid) {
        SQLController sQLController = new SQLController(context);
        sQLController.open();
        Cursor cursor = sQLController.fetchMessageById(Long.parseLong(msgid));
        Message msg = null;
        while (!cursor.isAfterLast()) {
            String _ID = cursor.getString(cursor.getColumnIndex(DBHelper._ID));
            String group = cursor.getString(cursor.getColumnIndex(DBHelper.GROUP_ID));
            String active = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_ACTIVE));
            String message = cursor.getString(cursor.getColumnIndex(SettingsJsonConstants.PROMPT_MESSAGE_KEY)).replaceAll("'", "__");
            String frequency = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_REPEAT));
            String alarm = cursor.getString(cursor.getColumnIndex(DBHelper.SMS_ALARM));
            msg = new Message(
                    _ID,
                    group,
                    active,
                    message,
                    cursor.getString(cursor.getColumnIndex(DBHelper.SMS_NEXT)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.SMS_TIME)),
                    frequency, cursor.getString(cursor.getColumnIndex(DBHelper.SMS_TYPE)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.SMS_TRIGGER)),
                    "alarm"+alarm);
            cursor.moveToNext();
        }
        return msg;
    }
    public static List<Message> getMessagesFor(Context context,String DayType,String MessageType){
        //
        SQLController dbController = new SQLController(context);
        dbController.open();
        Cursor MessageCursor=null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DATE, 7); // Adding 7 days
        String weekdate = sdf.format(c.getTime());

        switch (DayType){
            case "Today":
                MessageCursor = dbController.fetchTodayMessages(MessageType);
                break;
            case "Weeks":
                MessageCursor = dbController.fetchWeeksMessages(weekdate,MessageType);
                break;
            case "Upcoming":
                MessageCursor = dbController.fetchUpcomingMessages(weekdate,MessageType);
                break;
        }
        StringBuilder str = new StringBuilder();
        List<Message> messages = new ArrayList();
        while (!MessageCursor.isAfterLast()) {
            String Message_ID = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper._ID));
            String group = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.GROUP_ID));
            String active = "";
            String message = MessageCursor.getString(MessageCursor.getColumnIndex(SettingsJsonConstants.PROMPT_MESSAGE_KEY)).replaceAll("'", "__");
            String frequency = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_REPEAT));
            if (frequency.equals("0")) {
                frequency = "Never Repeat";
            }
            if (frequency.equals("1")) {
                frequency = "Every Day";
            }
            if (frequency.equals("2")) {
                frequency = "Every Week";
            }
            if (frequency.equals("3")) {
                frequency = "Every Month";
            }
            if (frequency.equals("4")) {
                frequency = "Every Year";
            }
            if (frequency.equals("5")) {
                frequency = "Mon-Fri";
            }
            if (frequency.equals("6")) {
                frequency = "Sat-Sun";
            }
            if (frequency.equals("8")) {
                frequency = "Custom";
            }
            String nextDate = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_NEXT));
            String msgTime = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_TIME));
            String msgType = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_TYPE));
            String msgTrigger = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_TRIGGER));
            String smsID = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_ID));
            if (msgTrigger.equals("Scheduled SMS")) {
                msgTrigger = "SMS";
            }
            if (msgTrigger.equals("Call Reminder")) {
                msgTrigger = "Call";
            }
            String group_image = "";
            String group_name="";
            Cursor GroupCursor = dbController.fetchGroupById((long) Integer.parseInt(group));
            while (!GroupCursor.isAfterLast()) {
                group_image = GroupCursor.getString(GroupCursor.getColumnIndex(DBHelper.GROUP_IMAGE));
                group_name = GroupCursor.getString(GroupCursor.getColumnIndex(DBHelper.GROUP_NAME));
                GroupCursor.moveToNext();
            }
            GroupCursor.close();
            messages.add(new Message(Message_ID, group_image, active, message, nextDate, msgTime, frequency, msgType, msgTrigger, smsID,group_name));
            MessageCursor.moveToNext();
        }
        MessageCursor.close();
        return messages;
    }
}
