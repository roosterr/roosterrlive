package com.roosterr;

import android.content.Context;
import android.database.Cursor;
import com.twitter.sdk.android.core.internal.TwitterApiConstants.Base;
import io.fabric.sdk.android.BuildConfig;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class Group {
    public Integer _group_contacts;
    public String _group_name;
    public Integer _group_sms;
    public String _id;
    public String _image;

    public Group(String id, String group_name, Integer group_contacts, Integer group_sms_count, String image) {
        this._id = id;
        this._group_name = group_name;
        this._group_contacts = group_contacts;
        this._group_sms = group_sms_count;
        this._image = image;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put(Base.PARAM_ID, this._id);
            obj.put(DBHelper.GROUP_NAME, this._group_name);
            obj.put("group_contacts", this._group_contacts);
            obj.put("group_sms", this._group_sms);
            obj.put(DBHelper.GROUP_IMAGE, this._image);
        } catch (JSONException e) {
        }
        return obj;
    }

    public static List<Group> getGroups(Context context) {
        SQLController dbController = new SQLController(context);
        dbController.open();
        Cursor cursor = dbController.fetchGroups();
        StringBuilder str = new StringBuilder();
        List<Group> groups = new ArrayList();
        while (!cursor.isAfterLast()) {
            Integer _ID = Integer.valueOf(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper._ID))));
            String group_name = cursor.getString(cursor.getColumnIndex(DBHelper.GROUP_NAME));
            String group_image = cursor.getString(cursor.getColumnIndex(DBHelper.GROUP_IMAGE));
            int contactCount = 0;
            int contactSMS = 0;
            Cursor contactCursor = dbController.getGroupContactCount(_ID);
            Cursor SMSCursor = dbController.getGroupSMSCount(_ID);
            while (!contactCursor.isAfterLast()) {
                contactCount = Integer.parseInt(contactCursor.getString(contactCursor.getColumnIndex(DBHelper.GROUP_CONTACTS_COUNT)));
                contactCursor.moveToNext();
            }
            while (!SMSCursor.isAfterLast()) {
                contactSMS = Integer.parseInt(SMSCursor.getString(SMSCursor.getColumnIndex(DBHelper.GROUP_SMS_COUNT)));
                SMSCursor.moveToNext();
            }
            groups.add(new Group(_ID.toString(), group_name, Integer.valueOf(contactCount), Integer.valueOf(contactSMS), group_image));
            cursor.moveToNext();
        }
        return groups;
    }

    public static Group getGroupById(Context context, String groupid) {
        SQLController dbController = new SQLController(context);
        dbController.open();
        Group grp = null;
        Cursor cursor = dbController.fetchGroupById(Long.parseLong(groupid));
        while (!cursor.isAfterLast()) {
            String _ID = cursor.getString(cursor.getColumnIndex(DBHelper._ID));
            String group_name = cursor.getString(cursor.getColumnIndex(DBHelper.GROUP_NAME));
            String group_image = cursor.getString(cursor.getColumnIndex(DBHelper.GROUP_IMAGE));
            if (group_image == null) {
                group_image = "";
            }
            grp = new Group(_ID.toString(), group_name, Integer.valueOf(0), Integer.valueOf(0), group_image);
            cursor.moveToNext();
            cursor.close();
        }
        return grp;
    }

    public static List<Message> getGroupMessages(Context context, String groupid) {
        SQLController dbController = new SQLController(context);
        dbController.open();
        Cursor MessageCursor = dbController.fetchMessagesByGroupID(groupid);
        StringBuilder str = new StringBuilder();
        List<Message> messages = new ArrayList();
        while (!MessageCursor.isAfterLast()) {
            String Message_ID = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper._ID));
            String group = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.GROUP_ID));
            String active = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_ACTIVE));
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
            String nextDate = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_DateTime));
            String msgTime = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_TIME));
            String msgType = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_TYPE));
            String msgTrigger = MessageCursor.getString(MessageCursor.getColumnIndex(DBHelper.SMS_TRIGGER));
            String[] date = nextDate.split("/");
            String nextMsgDate = "";
            messages.add(new Message(Message_ID, group, active, message, nextDate, msgTime, frequency, msgType, msgTrigger));
            MessageCursor.moveToNext();
        }
        MessageCursor.close();
        return messages;
    }

    public static void updateGroupName(Context context, String groupid, String name) {
        SQLController dbController = new SQLController(context);
        dbController.open();
        dbController.updateGroupName(groupid, name);
    }

    public static void saveGroupContacts(Context context, String groupid, String name, String number) {
        SQLController dbController = new SQLController(context);
        dbController.open();
        dbController.deleteExistingContact(groupid, name, number);
        dbController.saveGroupContacts(groupid, name, number);
    }

    public static void deleteGroupContacts(Context context, String groupId, String name, String number) {
        SQLController dbController = new SQLController(context);
        dbController.open();
        dbController.deleteExistingContact(groupId, name, number);
    }

    public static List<Contacts> getGroupContacts(Context context, String groupid) {
        SQLController dbController = new SQLController(context);
        dbController.open();
        List<Contacts> contacts = new ArrayList();
        Cursor groupContactCursor = dbController.getGroupContacts(Integer.valueOf(Integer.parseInt(groupid)));
        while (!groupContactCursor.isAfterLast()) {
            Contacts contact = new Contacts();
            contact.Name = groupContactCursor.getString(groupContactCursor.getColumnIndex(DBHelper.GROUP_CONTACT_NAME));
            contact.Phone = groupContactCursor.getString(groupContactCursor.getColumnIndex(DBHelper.GROUP_CONTACT_NUMBER));
            contacts.add(contact);
            groupContactCursor.moveToNext();
        }
        groupContactCursor.close();
        return contacts;
    }

    public static String saveGroupMessages(Context context, String group, String msg, String msg_date, String msg_time, String freq, String id, String custom, String type, String trigger,String alarm) {
        SQLController dbController = new SQLController(context);
        msg = msg.replaceAll("'", "'");
        dbController.open();
        String msgId="";
        if (id.equals("-99999")) {
            msgId = dbController.insert(group, "1", msg, msg_date, msg_time, freq, msg_date, id, custom, type, trigger,alarm);
        } else {
            msgId=id;
            dbController.update(Long.parseLong(id), group, "1", msg, msg_date, msg_time, freq, msg_date, custom, type, trigger,alarm);
        }
        new ScheduleAlaramReceiver().scheduleAlarmMessage(freq, msg_date, msg_time, context);
        return msgId;
    }
    public static void saveGroupMessages(Context context, String group, String msg, String msg_date, String msg_time, String freq, String id, String custom, String type, String trigger,String alarm,String status) {
        SQLController dbController = new SQLController(context);
        msg = msg.replaceAll("'", "'");
        dbController.open();
        if (id.equals("-99999")) {
            dbController.insert(group, "1", msg, msg_date, msg_time, freq, msg_date, id, custom, type, trigger,alarm);
        } else {
            dbController.update(Long.parseLong(id), group, "1", msg, msg_date, msg_time, freq, msg_date, custom, type, trigger,alarm);
            dbController.updateStatus(Long.parseLong(id),status);
        }
        new ScheduleAlaramReceiver().scheduleAlarmMessage(freq, msg_date, msg_time, context);
    }

}
