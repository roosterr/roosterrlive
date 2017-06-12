package com.roosterr;

import com.google.gson.annotations.SerializedName;

public class reminder_group_template {
    @SerializedName("country_code")
    public String country_code;
    @SerializedName("id")
    public String id;
    @SerializedName("language_code")
    public String language_code;
    @SerializedName("reminder_group_name")
    public String reminder_group_name;
    @SerializedName("reminder_group_name_english")
    public String reminder_group_name_english;
    @SerializedName("sort_order")
    public String sort_order;
    @SerializedName("is_user_group")
    public String is_user_group;
    @SerializedName("group_profile")
    public String group_profile;
}
