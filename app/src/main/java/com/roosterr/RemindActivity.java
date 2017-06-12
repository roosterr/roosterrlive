package com.roosterr;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class RemindActivity extends AppCompatActivity {

    private int demoType;
    private String messageID;
    MediaPlayer mp = null;
    Vibrator vibrator=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);

        demoType = getIntent().getExtras().getInt(HomeActivity.EXTRA_DEMO_TYPE, 123);
        messageID = getIntent().getExtras().getString("message_id");

        Message callReminderMessage = Message.getMessageForAlarmById(this, messageID);
        Group callReminderGroup = Group.getGroupById(this,callReminderMessage._group);
        final List<Contacts> groupContacts=Group.getGroupContacts(this,callReminderGroup._id);
        TextView triggerText = (TextView) findViewById(R.id.remind_trigger_text);
        ImageView triggerIcon = (ImageView) findViewById(R.id.remind_trigger_icon);

        ImageView groupImageView = (ImageView) findViewById(R.id.image_group);
        TextView groupTitleTextView = (TextView) findViewById(R.id.title_group);
        TextView msgTypeTextView = (TextView) findViewById(R.id.title_type);
        ImageView msgImageType = (ImageView) findViewById(R.id.image_type);
        TextView msgReminder = (TextView) findViewById(R.id.reminder_message_text);
        TextView remindTimeTextView = (TextView) findViewById(R.id.remind_time_text);

        View singleOptionView = findViewById(R.id.view_single_option_button);
        View doubleOptionView = findViewById(R.id.view_two_options_button);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
        String currentDateTimeString = df.format(c.getTime());

        remindTimeTextView.setText(currentDateTimeString);

        groupTitleTextView.setText(callReminderGroup._group_name);
        if (callReminderGroup._image != "") {
            byte[] decodedString = Base64.decode(callReminderGroup._image);
            groupImageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        }

        msgTypeTextView.setText(callReminderMessage.type);

        if(callReminderMessage.type.equals("Meeting"))
            msgImageType.setImageResource(R.drawable.ic_meeting);
        if(callReminderMessage.type.equals("To Do"))
            msgImageType.setImageResource(R.drawable.ic_to_do);
        if(callReminderMessage.type.equals("Bill Pay"))
            msgImageType.setImageResource(R.drawable.ic_bill_pay);
        if(callReminderMessage.type.equals("Birthday"))
            msgImageType.setImageResource(R.drawable.ic_birthday);
        if(callReminderMessage.type.equals("Anniversary"))
            msgImageType.setImageResource(R.drawable.ic_anniversary);

        msgReminder.setText(callReminderMessage._message.replaceAll("__", "'"));

        if (demoType == HomeActivity.EXTRA_DEMO_TYPE_ALARM) {
            triggerText.setText(RemindActivity.this.getResources().getString(R.string.alarm_label));
            triggerIcon.setImageResource(R.drawable.ic_alarms_white_24dp);
            if(callReminderMessage._custom.equals("0")) {
                vibrator = (Vibrator)getSystemService(this.VIBRATOR_SERVICE);
                int strong_vibration = 1000; //vibrate with a full power for 30 secs
                int interval = 1000;
                int dot = 1000; //one millisecond of vibration
                int short_gap = 1000; //one millisecond of break - could be more to weaken the vibration
                long[] pattern = {
                        0,  // Start immediately
                        strong_vibration,
                        interval,
                        // 15 vibrations and 15 gaps = 30millis
                        dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, //yeah I know it doesn't look good, but it's just an example. you can write some code to generate such pattern.
                };
                vibrator.vibrate(pattern,-1);
            }
            if(callReminderMessage._custom.equals("1")) {
                mp = MediaPlayer.create(this, R.raw.alarm_clock);
                mp.setLooping(true);
                mp.start();
            }
            else if(callReminderMessage._custom.equals("2")) {
                mp = MediaPlayer.create(this, R.raw.gong);
                mp.setLooping(true);
                mp.start();
            }
            else if(callReminderMessage._custom.equals("3")) {
                mp = MediaPlayer.create(this, R.raw.tibetan_tingsha);
                mp.setLooping(true);
                mp.start();
            }

            FloatingActionButton dismissAlarm = (FloatingActionButton) findViewById(R.id.alarm_off);
            dismissAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mp != null && mp.isPlaying()) {
                        mp.stop();
                        mp.release();
                        mp = null;
                    } else {
                        vibrator.cancel();
                    }
                    setResult(RESULT_OK);
                    finish();
                }
            });

            singleOptionView.setVisibility(View.VISIBLE);
            doubleOptionView.setVisibility(View.GONE);
        } else if (demoType == HomeActivity.EXTRA_DEMO_TYPE_CALL) {
            triggerText.setText(RemindActivity.this.getResources().getString(R.string.call_reminder_label));
            triggerIcon.setImageResource(R.drawable.ic_call_white_24dp);

            View numberView = findViewById(R.id.layout_calling_number);
            numberView.setVisibility(View.VISIBLE);
            TextView number = (TextView) findViewById(R.id.calling_number);
            if (groupContacts.size() > 0)
                number.setText(groupContacts.get(0).Phone);

            //play callreminder tune
            //mp = MediaPlayer.create(this, R.raw.callreminder);
            //mp.setLooping(true);
            //mp.start();
            //Now Vibrate
            vibrator = (Vibrator)getSystemService(this.VIBRATOR_SERVICE);

            int strong_vibration = 1000; //vibrate with a full power for 30 secs
            int interval = 1000;
            int dot = 1000; //one millisecond of vibration
            int short_gap = 1000; //one millisecond of break - could be more to weaken the vibration
            long[] callPattern = {
                    0,  // Start immediately
                    strong_vibration,
                    interval,
                    // 15 vibrations and 15 gaps = 30millis
                    dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, dot, short_gap, //yeah I know it doesn't look good, but it's just an example. you can write some code to generate such pattern.
            };
            vibrator.vibrate(callPattern, -1);
            FloatingActionButton callButton = (FloatingActionButton) findViewById(R.id.call_now_button);
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mp != null && mp.isPlaying()) {
                        mp.stop();
                        mp.release();
                        mp = null;
                    } else {
                        vibrator.cancel();
                    }
                    Intent callIntent = new Intent(Intent.ACTION_VIEW );//ACTION_DIAL
                    if(groupContacts.size()>0)
                        callIntent.setData(Uri.parse("tel:" + groupContacts.get(0).Phone));

                    if (callIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(callIntent);
                    }
                    finish();
                }
            });

            FloatingActionButton dismissCall = (FloatingActionButton) findViewById(R.id.dismiss_button);
            dismissCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_OK);
                    if (mp != null && mp.isPlaying()) {
                        mp.stop();
                        mp.release();
                        mp = null;
                    } else {
                        vibrator.cancel();
                    }
                    finish();
                }
            });

            singleOptionView.setVisibility(View.GONE);
            doubleOptionView.setVisibility(View.VISIBLE);
        }

    }
}
