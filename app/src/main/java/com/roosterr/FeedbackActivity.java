package com.roosterr;

import android.content.Context;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.net.PasswordAuthentication;
import java.util.Properties;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import org.json.JSONException;

import java.io.IOException;
public class FeedbackActivity extends AppCompatActivity {
    private static final String SENDGRID_USERNAME = "azure_50f6054a9155ef2bad63913fd04de329@azure.com";
    private static final String SENDGRID_PASSWORD = "Indi@1947";
    private static final int ADD_ATTACHMENT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button sendButton = (Button) findViewById(R.id.buttonSend);

        final EditText feedback_email= (EditText) findViewById(R.id.feedback_email);
        final EditText feedback_message= (EditText) findViewById(R.id.feedback_message);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toMail="admin@roosterr.com";
                SendEmailASyncTask task = new SendEmailASyncTask(FeedbackActivity.this,
                        toMail,
                        feedback_email.getText().toString(),
                         FeedbackActivity.this.getResources().getString(R.string.feedback_email_label),
                        feedback_message.getText().toString(),
                        null,
                        null);
                task.execute();
                finish();
            }
        });
    }

    /**
     * ASyncTask that composes and sends email
     */
    private static class SendEmailASyncTask extends AsyncTask<Void, Void, Void> {

        private Context mAppContext;
        private String mMsgResponse;

        private String mTo;
        private String mFrom;
        private String mSubject;
        private String mText;
        private Uri mUri;
        private String mAttachmentName;

        public SendEmailASyncTask(Context context, String mTo, String mFrom, String mSubject,
                                  String mText, Uri mUri, String mAttachmentName) {
            this.mAppContext = context.getApplicationContext();
            this.mTo = mTo;
            this.mFrom = mFrom;
            this.mSubject = mSubject;
            this.mText = mText;
            this.mUri = mUri;
            this.mAttachmentName = mAttachmentName;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                SendGrid sendgrid = new SendGrid(SENDGRID_USERNAME, SENDGRID_PASSWORD);

                SendGrid.Email email = new SendGrid.Email();

                // Get values from edit text to compose email
                // TODO: Validate edit texts
                email.addTo(mTo);
                email.setFrom(mFrom);
                email.setSubject(mSubject);
                email.setText(mText);

                // Attach image
                if (mUri != null) {
                    email.addAttachment(mAttachmentName, mAppContext.getContentResolver().openInputStream(mUri));
                }

                // Send email, execute http request
                SendGrid.Response response = sendgrid.send(email);
                mMsgResponse = response.getMessage();



            } catch (SendGridException | IOException e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(mAppContext, mMsgResponse, Toast.LENGTH_SHORT).show();
        }
    }

}
