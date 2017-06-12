package com.roosterr;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.view.View.OnClickListener;
import com.roosterr.adapters.SentReminderListItemRecyclerViewAdapter;
import com.roosterr.adapters.SentReminderListItemRecyclerViewAdapter.OnItemClickListener;
import com.roosterr.dummy.DummyReminderContent;

import static com.roosterr.adapters.SentReminderListItemRecyclerViewAdapter.VIEW_TYPE_EMPTY;

public class SentRemindersActivity extends AppCompatActivity {
    public static final int CASE_DELETE = 1;
    public static final int CASE_DELETE_ALL = 2;
    public static final int CASE_EDIT = 3;
    private static SentReminderListItemRecyclerViewAdapter remindersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_reminders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView sentList = (RecyclerView) findViewById(R.id.list_sent_reminders);
        remindersAdapter = new SentReminderListItemRecyclerViewAdapter(Message.getSentMessages(getApplicationContext()));
        remindersAdapter.setOnItemClickListener(new SentReminderListItemRecyclerViewAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int clickOn, int position, String messageId) {
                switch (clickOn) {
                    case CASE_DELETE /*1*/:
                        new JavaScriptInterface(SentRemindersActivity.this).deleteHistoryMessage(messageId);
                        break;
                    case CASE_EDIT /*3*/:
                        Message msg = Message.getMessageById(SentRemindersActivity.this, messageId);
                        if (msg != null) {
                            SentRemindersActivity.this.startActivity(new Intent(SentRemindersActivity.this, CreateActivity.class).putExtra("groupid", msg._group).putExtra("msgid", msg._id).putExtra("caller","SentRemindersActivity"));
                        }
                        break;
                    default:
                }
            }
        });
        sentList.setAdapter(remindersAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                if (remindersAdapter.getItemViewType(0) != VIEW_TYPE_EMPTY)
                    new DeleteAllDialogFragment().show(getSupportFragmentManager(), "DeleteAll");
                else
                    Toast.makeText(this, R.string.nothing_to_delete, Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DeleteAllDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.delete_all_sent_reminders)
                    .setPositiveButton(R.string.delete_all, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new JavaScriptInterface(DeleteAllDialogFragment.this.getContext()).deleteAllHistoryMessage();
                            remindersAdapter.deleteAll();
                            remindersAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
            startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
