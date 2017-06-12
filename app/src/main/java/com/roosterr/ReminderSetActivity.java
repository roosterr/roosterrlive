package com.roosterr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import io.fabric.sdk.android.*;

public class ReminderSetActivity extends AppCompatActivity {
    String groupid;
    String msgid;
    String caller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_set);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String sfGroupId = getSharedPreferences("groupid", 0).getString("groupid", null);
        if (sfGroupId == null || sfGroupId =="") {
            groupid = getIntent().getStringExtra("groupid");
        } else {
            groupid = sfGroupId;
        }
        msgid = getIntent().getStringExtra("msgid");
        SharedPreferences.Editor sfsocial = getSharedPreferences("groupid", 0).edit();
        sfsocial.putString("groupid", groupid);
        sfsocial.commit();

        String message = getIntent().getStringExtra("REMINDER_MESSAGE");
        TextView messageView = (TextView) findViewById(R.id.done_message);
        messageView.setText(message);
        caller = getIntent().getStringExtra("caller");
        View layout = findViewById(R.id.content_reminder_set);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, CreateActivity.class).putExtra("groupid", groupid).putExtra("msgid", msgid).putExtra("caller",caller));
        finish();
    }

}
