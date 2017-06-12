package com.roosterr;

import com.google.gson.annotations.SerializedName;

public class reminder_group_message_template {
    @SerializedName("frequency")
    public String frequency;
    @SerializedName("id")
    public String id;
    @SerializedName("message")
    public String message;
    @SerializedName("message_time")
    public String message_time;
    @SerializedName("reminder_group_id")
    public String reminder_group_id;
    @SerializedName("msg_type")
    public String msg_type;
    @SerializedName("msg_trigger")
    public String msg_trigger;
    @SerializedName("message_date")
    public String message_date;
    @SerializedName("message_active")
    public String message_active;
    @SerializedName("is_demo")
    public String is_demo;
    @SerializedName("demo_minutes")
    public String demo_minutes;
}
