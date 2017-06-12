package com.roosterr;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.fabric.sdk.android.BuildConfig;

public class JavaScriptInterface {
    private SQLController dbController;
    Context mContext;

    JavaScriptInterface(Context c) {
        this.mContext = c;
    }

    public void deleteMessage(String msg_id) {
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        this.dbController.delete(Long.parseLong(msg_id));
        Toast.makeText(this.mContext, mContext.getResources().getString(R.string.message_del_label), Toast.LENGTH_SHORT).show();
    }

    public void deleteHistoryMessage(String msg_id) {
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        this.dbController.deleteHistory(Long.parseLong(msg_id));
        Toast.makeText(this.mContext, mContext.getResources().getString(R.string.message_del_label), Toast.LENGTH_SHORT).show();
    }

    public void deleteAllHistoryMessage() {
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        this.dbController.deleteAllHistory();
        Toast.makeText(this.mContext, mContext.getResources().getString(R.string.all_message_del_label), Toast.LENGTH_SHORT).show();
    }

    public void createGroup(String groups) {
        int i = 0;
        Toast.makeText(this.mContext, mContext.getResources().getString(R.string.group_creating_label), Toast.LENGTH_SHORT).show();
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        String[] collection = groups.split(",");
        int length = collection.length;
        while (i < length) {
            this.dbController.createGroups(collection[i]);
            i++;
        }
    }
    public void createChecklist(String checklists){
        int i = 0;
        //Toast.makeText(this.mContext, "Creating Checklists", Toast.LENGTH_SHORT).show();
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        String[] collection = checklists.split(",");
        int length = collection.length;
        while (i < length) {
            this.dbController.createChecklist(collection[i]);
            i++;
        }
    }
    public void saveGroupImage(String groupId, String image) {
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        this.dbController.updateGroupImage(groupId, image);
    }

    public void saveGroupContacts(String groupId, String name, String number) {
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        this.dbController.deleteExistingContact(groupId, name, number);
        this.dbController.saveGroupContacts(groupId, name, number);
    }

    public void deleteGroupContacts(String groupId, String name, String number) {
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        this.dbController.deleteExistingContact(groupId, name, number);
    }

    public void updateGroupName(String groupId, String name) {
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        this.dbController.updateGroupName(groupId, name);
    }

    public void deleteGroup(String groupId) {
        Toast.makeText(this.mContext, mContext.getResources().getString(R.string.group_del_label), Toast.LENGTH_SHORT).show();
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        this.dbController.deleteGroup(groupId);
    }

    public void toggleSMS(String msgId, String status) {
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        this.dbController.toggleSMS(msgId, status);
    }

    public void cloneMessage(String groupId, String msgId) {
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        for (String group : groupId.split(",")) {
            this.dbController.cloneMessage(group, msgId);
        }
    }

    public void createMessage(String message_list, String groups) {
        //Toast.makeText(this.mContext, "Creating Messages", Toast.LENGTH_SHORT).show();
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        for (String group : groups.split(",")) {
            if (!group.equals("")) {
                String[] group_arr=group.split("`");
                this.dbController.createGroups(group_arr[0]);
                String groupid = "";
                Cursor groupCursor = this.dbController.fetchGroupByName(group_arr[0]);
                while (!groupCursor.isAfterLast()) {
                    groupid = groupCursor.getString(groupCursor.getColumnIndex(DBHelper._ID));
                    groupCursor.moveToNext();
                }
                if(!group_arr[1].equals("0")) {
                    this.dbController.saveGroupContacts(groupid, group_arr[0], group_arr[1]);
                }
                if(!group_arr[2].equals("0")) {
                    /*
                        R.drawable.animation,
                        R.drawable.astronaut,
                        R.drawable.avatar,
                        R.drawable.avengers
                     */

                    Bitmap bitmap = null;
                    int resID = this.mContext.getResources().getIdentifier(group_arr[2] , "drawable", this.mContext.getPackageName());

                    //if(group_arr[2].equals("1"))
                        bitmap = GroupImageActivity.drawableToBitmap(this.mContext.getResources().getDrawable(resID));
                    //if(group_arr[2].equals("2"))
                    //    bitmap = GroupImageActivity.drawableToBitmap(this.mContext.getResources().getDrawable(R.drawable.dance_with_devil));
                    //if(group_arr[2].equals("3"))
                    //    bitmap = GroupImageActivity.drawableToBitmap(this.mContext.getResources().getDrawable(R.drawable.guest_male));
                    //if(group_arr[2].equals("4"))
                    //    bitmap = GroupImageActivity.drawableToBitmap(this.mContext.getResources().getDrawable(R.drawable.guest_female));

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    this.dbController.updateGroupImage(groupid,Base64.encode(stream.toByteArray()));
                }
            }
        }
        if (!message_list.equals("")) {
            for (String item : message_list.split("`")) {
                String[] messages = item.toString().split("\\|");
                String group_name = messages[0];
                String group = "";
                if (group_name != null) {
                    if (!group_name.equals("")) {
                        Cursor groupCursor = this.dbController.fetchGroupByName(group_name);
                        while (!groupCursor.isAfterLast()) {
                            group = groupCursor.getString(groupCursor.getColumnIndex(DBHelper._ID));
                            groupCursor.moveToNext();
                        }
                        groupCursor.close();
                    }
                }
                String msg = messages[1].replaceAll("'", "'");
                String msg_date = messages[2];
                String msg_time = messages[3];
                String freq = messages[4];
                String id = messages[5];
                String custom = messages[6];
                String type = "To Do";
                String trigger = "Scheduled SMS";
                String alarm="-1";
                if (id.equals("-99999")) {
                    this.dbController.insert(group, "1", msg, msg_date, msg_time, freq, msg_date, id, custom, type, trigger,alarm);
                }
                new ScheduleAlaramReceiver().scheduleAlarmMessage(freq, msg_date, msg_time, this.mContext);
            }
        }
    }

    public void createMessagesForGroups(String message_list, String groups) {
        //Toast.makeText(this.mContext, "Creating Messages", Toast.LENGTH_SHORT).show();
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        String[] group_collection = groups.split(",");
        for (String str : group_collection) {
            String[] group_detail = str.toString().split("\\|");
            String group_id = group_detail[0].toString();
            String grp_name = group_detail[1].toString();
            if (!message_list.equals("")) {
                for (String item : message_list.split("`")) {
                    String[] messages = item.toString().split("\\|");
                    String group_name = grp_name;
                    String group = "";
                    if (messages[7].equals(group_id)) {
                        if (group_name != null) {
                            if (!group_name.equals("")) {
                                Cursor groupCursor = this.dbController.fetchGroupByName(group_name);
                                while (!groupCursor.isAfterLast()) {
                                    group = groupCursor.getString(groupCursor.getColumnIndex(DBHelper._ID));
                                    groupCursor.moveToNext();
                                }
                                groupCursor.close();
                            }
                        }
                        String msg = messages[1].replaceAll("'", "'");
                        String msg_date = messages[2];
                        String msg_time = messages[3];
                        String freq = messages[4];
                        String id = messages[5];
                        String custom = messages[6];
                        String type = messages[8];//"To Do";
                        String alarm="-1";
                        String sms_active=messages[10];

                        if(type.toLowerCase().equals("to do")){
                            type="To Do";
                        }
                        if(type.toLowerCase().equals("meeting")){
                            type="Meeting";
                        }
                        if(type.toLowerCase().equals("bill pay")){
                            type="Bill Pay";
                        }
                        if(type.toLowerCase().equals("birthday")){
                            type="Birthday";
                        }
                        if(type.toLowerCase().equals("anniversary")){
                            type="Anniversary";
                        }
                        String trigger = messages[9];//"Scheduled SMS";
                        if(trigger.toLowerCase().equals("scheduled sms")){
                            trigger="Scheduled SMS";
                        }
                        if(trigger.toLowerCase().equals("alarm")){
                            trigger="Alarm";
                            alarm="0";
                        }
                        if(trigger.toLowerCase().equals("call reminder")){
                            trigger="Call Reminder";
                        }

                        if (id.equals("-99999")) {
                            this.dbController.insert(group, sms_active, msg, msg_date, msg_time, freq, msg_date, id, custom, type, trigger,alarm);
                        }
                        new ScheduleAlaramReceiver().scheduleAlarmMessage(freq, msg_date, msg_time, this.mContext);
                    }
                }
            }
        }
        //update is_ready here
        this.dbController.updateUserStatus();
        this.mContext.startActivity(new Intent(this.mContext, HomeActivity.class));
    }

    public void createItemsForChecklists(String item_list,String checklists){
        //Toast.makeText(this.mContext, "Creating Checklist Items", Toast.LENGTH_LONG).show();
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        String[] checklist_collection = checklists.split(",");
        for (String str : checklist_collection) {
            String[] checklist_detail = str.toString().split("\\|");
            String checklist_id = checklist_detail[0].toString();
            String chk_name = checklist_detail[1].toString();

            if (!item_list.equals("")) {
                for (String item : item_list.split("`")) {
                    String[] messages = item.toString().split("\\|");
                    String checklist_name = chk_name;
                    String checklist = "";
                    if (messages[2].equals(checklist_id)) {
                        if (checklist_name != null) {
                            if (!checklist_name.equals("")) {
                                Cursor chkCursor = this.dbController.fetchChecklistByName(checklist_name);
                                while (!chkCursor.isAfterLast()) {
                                    checklist = chkCursor.getString(chkCursor.getColumnIndex(DBHelper._ID));
                                    chkCursor.moveToNext();
                                }
                                chkCursor.close();
                            }
                        }
                        String msg = messages[1].replaceAll("'", "'");
                        this.dbController.createChecklistItems(checklist,msg);
                    }
                }
            }
        }

    }

    public void createWeeklyReminder() {
        this.dbController = new SQLController(this.mContext);
        this.dbController.open();
        String msg = "1";
        String msg_date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        String msg_time = "09:00";
        String freq = "1";
        String id = "-99999";
        String custom = "0";
        String type = "To Do";
        String alarm= "-1";
        String sms_active="1";
        String trigger = "Notification";

        if (id.equals("-99999")) {
            this.dbController.insertWeeklyReminder("0", sms_active, msg, msg_date, msg_time, freq, msg_date, id, custom, type, trigger,alarm);
        }
        new ScheduleAlaramReceiver().scheduleNotificationAlarm(freq, msg_date, msg_time, this.mContext);
    }
}
