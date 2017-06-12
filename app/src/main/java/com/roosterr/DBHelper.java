package com.roosterr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String CREATE_GROUP_CONTACTS_TABLE = "create table Group_Contacts(_id INTEGER PRIMARY KEY AUTOINCREMENT,group_id INTEGER,contact_name TEXT,contact_number TEXT );";
    private static final String CREATE_GROUP_TABLE = "create table Groups(_id INTEGER PRIMARY KEY AUTOINCREMENT,group_name TEXT,group_image TEXT,group_profile TEXT);";
    private static final String CREATE_HISTORY_TABLE = "create table History(_id INTEGER PRIMARY KEY AUTOINCREMENT,message TEXT,sms_type TEXT,sms_trigger TEXT,sms_id INTEGER,sms_date TEXT,sms_time TEXT,group_id INTEGER );";
    private static final String CREATE_TABLE = "create table Scheduler(_id INTEGER PRIMARY KEY AUTOINCREMENT,message TEXT,sms_date TEXT,sms_time TEXT,sms_repeat TEXT,sms_next TEXT,sms_id TEXT,sms_datetime TEXT,sms_status TEXT,sms_custom TEXT,sms_type TEXT,sms_trigger TEXT,group_id INTEGER,sms_active INTEGER,sms_alarm INTEGER );";

    private static final String CREATE_WEEKLYREMIND = "create table Weeklyscheduler(_id INTEGER PRIMARY KEY AUTOINCREMENT,message TEXT,sms_date TEXT,sms_time TEXT,sms_repeat TEXT,sms_next TEXT,sms_id TEXT,sms_datetime TEXT,sms_status TEXT,sms_custom TEXT,sms_type TEXT,sms_trigger TEXT,group_id INTEGER,sms_active INTEGER,sms_alarm INTEGER );";

    private static final String CREATE_USERS_TABLE = "create table Users_Table(_id INTEGER PRIMARY KEY AUTOINCREMENT,user_number TEXT,no_ads TEXT,go_pro Text,both Text,is_ready INTEGER);";

    private static final String CREATE_CHECKLIST_TABLE = "create table Checklist(_id INTEGER PRIMARY KEY AUTOINCREMENT,checklist_name TEXT);";
    private static final String CREATE_CHECKLIST_ITEMS_TABLE = "create table Checklist_Items(_id INTEGER PRIMARY KEY AUTOINCREMENT,item_name TEXT,checklist_id INTEGER);";

    static final String DB_NAME = "SMSScheduler.DB";
    static final int DB_VERSION = 11;
    public static final String GROUP_CONTACTS_COUNT = "group_contact_count";
    public static final String GROUP_CONTACTS_TABLE = "Group_Contacts";
    public static final String GROUP_CONTACT_NAME = "contact_name";
    public static final String GROUP_CONTACT_NUMBER = "contact_number";
    public static final String GROUP_ID = "group_id";
    public static final String GROUP_IMAGE = "group_image";
    public static final String GROUP_NAME = "group_name";
    public static final String GROUP_SMS_COUNT = "group_sms_count";
    public static final String GROUP_PROFILE="group_profile";

    public static final String GROUP_TABLE = "Groups";
    public static final String CHECKLIST_TABLE="Checklist";
    public static final String CHECKLIST_ITEMS_TABLE="Checklist_Items";
    public static final String CHECKLIST_ID = "checklist_id";
    public static final String CHECKLIST_NAME = "checklist_name";
    public static final String CHECKLIST_ITEM = "item_name";

    public static final String HISTORY_TABLE = "History";
    public static final String SMS_ACTIVE = "sms_active";
    public static final String SMS_CUSTOM = "sms_custom";
    public static final String SMS_DATE = "sms_date";
    public static final String SMS_DateTime = "sms_datetime";
    public static final String SMS_ID = "sms_id";
    public static final String SMS_MSG = "message";
    public static final String SMS_NEXT = "sms_next";
    public static final String SMS_REPEAT = "sms_repeat";
    public static final String SMS_Status = "sms_status";
    public static final String SMS_TIME = "sms_time";
    public static final String SMS_TRIGGER = "sms_trigger";
    public static final String SMS_TYPE = "sms_type";
    public static final String TABLE_NAME = "Scheduler";
    public static final String USERS_TABLE = "Users_Table";
    public static final String USER_NUMBER = "user_number";
    public static final String _ID = "_id";
    public static final String SMS_ALARM="sms_alarm";
    public static final String IS_READY="is_ready";

    public static final String WEEKLY_REMINDER="Weeklyscheduler";

    public static final String NO_ADS="no_ads";
    public static final String GO_PRO="go_pro";
    public static final String BOTH_PRODUCTS="both";

    private static DBHelper mInstance;

    static {
        mInstance = null;
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_HISTORY_TABLE);
        db.execSQL(CREATE_GROUP_TABLE);
        db.execSQL(CREATE_GROUP_CONTACTS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_CHECKLIST_TABLE);
        db.execSQL(CREATE_CHECKLIST_ITEMS_TABLE);
        db.execSQL(CREATE_WEEKLYREMIND);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Scheduler");
        db.execSQL("DROP TABLE IF EXISTS History");
        db.execSQL("DROP TABLE IF EXISTS Groups");
        db.execSQL("DROP TABLE IF EXISTS Group_Contacts");
        db.execSQL("DROP TABLE IF EXISTS Users_Table");
        db.execSQL("DROP TABLE IF EXISTS Checklist");
        db.execSQL("DROP TABLE IF EXISTS Checklist_Items");
        db.execSQL("DROP TABLE IF EXISTS Weeklyscheduler");
        onCreate(db);
    }

    public static DBHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }
}
