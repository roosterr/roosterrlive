package com.roosterr;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BackupActivity extends AppCompatActivity {
    private int SELECT_FILE = 1;
    protected static final File DATABASE_DIRECTORY =
            new File(Environment.getExternalStorageDirectory(),"Download");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final View upgradeView = findViewById(R.id.view_upgrade);
        final View backupView = findViewById(R.id.view_backup);
        final View doneView = findViewById(R.id.view_done);

        Button upgradeButton = (Button) findViewById(R.id.button_upgrade);
        Button backupButton = (Button) findViewById(R.id.button_backup);
        Button shareButton = (Button) findViewById(R.id.button_share);
        Button restoreButton = (Button) findViewById(R.id.button_restore);

        final String go_pro =  getSharedPreferences("Purchase_Type", 0).getString("go_pro", null);
        final String both_products =  getSharedPreferences("Purchase_Type", 0).getString("both", null);

        if(go_pro.equals("1") || both_products.equals("1")){
            upgradeButton.setVisibility(View.GONE);
            upgradeView.setVisibility(View.GONE);
            backupView.setVisibility(View.VISIBLE);
        }
        else{
            upgradeButton.setVisibility(View.VISIBLE);
        }

        upgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(go_pro.equals("1") || both_products.equals("1")) {
                    upgradeView.setVisibility(View.GONE);
                    backupView.setVisibility(View.VISIBLE);
                }
                else{
                    String userId =  getSharedPreferences("AzureUser", 0).getString("azureuser", null);
                    String phoneNumber = getSharedPreferences("AzureUser", 0).getString("phone_number", null);

                    startActivity(new Intent(BackupActivity.this, UpgradeActivity.class)
                            .putExtra("UserID",userId)
                            .putExtra("Phone",phoneNumber)
                    );
                }
            }
        });

        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backupView.setVisibility(View.GONE);
                String appendText = new SimpleDateFormat("yyyyMMddhhmm", Locale.ENGLISH).format(new Date());
                String filename = "RemindKing_"+appendText+".DB";

                doneView.setVisibility(View.VISIBLE);
                DbExportImport.exportDb();

                File exportDir = DATABASE_DIRECTORY;
                File file = new File(exportDir, filename);
                file = new File(file.getAbsolutePath());
                TextView backup_text=(TextView) findViewById(R.id.backup_text);
                backup_text.setText("Backup saved in Download folder. Filename : "+file);
                getSupportActionBar().setTitle("Backup");
            }
        });

        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                showFileChooser();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Change this text.");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                */
                String appendText = new SimpleDateFormat("yyyyMMddhhmm",Locale.ENGLISH).format(new Date());
                String filename = "RemindKing_"+appendText+".DB";
                File exportDir = DATABASE_DIRECTORY;
                File filelocation = new File(exportDir, filename);

                Uri path = Uri.fromFile(filelocation);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                // set the type to 'email'
                emailIntent .setType("vnd.android.cursor.dir/email");
                String to[] = {"email@domain.com"};
                emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
                // the attachment
                emailIntent .putExtra(Intent.EXTRA_STREAM, path);
                // the mail subject
                emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
                startActivity(Intent.createChooser(emailIntent , "Send email..."));
            }
        });
    }
    private void showFileChooser() {

        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select DB File"),SELECT_FILE);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
        }
    }
    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                String filename=data.getData().getPath().replace("document/primary:","");
                String ExternalStorageDirectory = Environment.getExternalStorageDirectory().toString();
                String AbsolutePath = filename;
                AbsolutePath = AbsolutePath.replace(ExternalStorageDirectory,"");
                AbsolutePath = ExternalStorageDirectory + AbsolutePath;
                File importFile = new File(AbsolutePath);
                boolean result= DbExportImport.restoreDb(importFile);
                if(!result)
                    Toast.makeText(BackupActivity.this,R.string.backup_alert_label,Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(BackupActivity.this,R.string.backup_restore_success_label,Toast.LENGTH_LONG).show();
            } catch (Exception e) {

            }
        }

    }
}
