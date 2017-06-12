package com.roosterr;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.roosterr.adapters.ReminderListItemRecyclerViewAdapter;
import com.roosterr.dummy.DummyReminderContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    public static final int CASE_REMINDER = 0;
    public static final int CASE_COPY = 1;
    public static final int CASE_DELETE = 2;
    public static final int CASE_EDIT = 3;
    private static String globalGroupId;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final private int REQUEST_ADD_CONTACT = 456;

    private ArrayList<Map<String, String>> mPeopleList;
    private List<Contacts> mMembersList;
    private SimpleAdapter mPeopleAdapter;
    private AutoCompleteTextView mAddContactView;
    private TextView mAddContactTextView;
    private TextView mSaveButton;
    private RecyclerView membersRecyclerView;

    private ImageView emptyIcon;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String groupid;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences sharedPreferences = getSharedPreferences("groupid", 0);
        String sfGroupId = sharedPreferences.getString("groupid", null);
        if (sfGroupId == null || sfGroupId == "") {
            groupid = getIntent().getStringExtra("groupid");
        } else {
            groupid = sfGroupId;
        }
        globalGroupId = groupid;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EditActivity.this.mMembersList.isEmpty()) {
                    Toast.makeText(EditActivity.this, R.string.edit_add_group_label, Toast.LENGTH_SHORT).show();
                } else {
                    EditActivity.this.startActivity(new Intent(EditActivity.this, CreateActivity.class).putExtra("groupid",groupid).putExtra("caller","EditActivity"));
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Group grp = Group.getGroupById(getApplicationContext(), groupid);
        final ImageButton editNameButton = (ImageButton) findViewById(R.id.button_edit_name);
        final Button saveEditNameButton = (Button) findViewById(R.id.button_save_name);
        final TextView nameText = (TextView) findViewById(R.id.name_text);
        final EditText editNameText = (EditText) findViewById(R.id.name_edit_text);

        nameText.setText(grp._group_name);

        final SharedPreferences sfGroup = getSharedPreferences(DBHelper.GROUP_TABLE, 0);
        if (grp._image != "") {
            ImageView grpImg = (ImageView) findViewById(R.id.groupimage);
            byte[] decodedString = Base64.decode(grp._image);
            grpImg.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        }

        editNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNameText.setText(nameText.getText().toString());

                nameText.setVisibility(View.INVISIBLE);
                editNameButton.setVisibility(View.INVISIBLE);

                saveEditNameButton.setVisibility(View.VISIBLE);
                editNameText.setVisibility(View.VISIBLE);

                editNameText.requestFocus();
            }
        });

        saveEditNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameText.setText(editNameText.getText().toString());

                saveEditNameButton.setVisibility(View.INVISIBLE);
                editNameText.setVisibility(View.INVISIBLE);

                nameText.setVisibility(View.VISIBLE);
                editNameButton.setVisibility(View.VISIBLE);

                Group.updateGroupName(EditActivity.this.getApplicationContext(),groupid, editNameText.getText().toString());
            }
        });

        FloatingActionButton fabPic = (FloatingActionButton) findViewById(R.id.fab_pic);
        fabPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.this.startActivity(new Intent(EditActivity.this, GroupImageActivity.class).putExtra("groupid",groupid));
            }
        });
        List<Message> grpMsg = Group.getGroupMessages(getApplicationContext(), groupid);
        updateTitle(groupid);
        final  ReminderListItemRecyclerViewAdapter remindersAdapter = new ReminderListItemRecyclerViewAdapter(this, grpMsg);
        remindersAdapter.setOnItemClickListener(new ReminderListItemRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int clickOn, final int position, boolean Status) {
                switch (clickOn) {
                    case CASE_REMINDER:
                        Message msg = remindersAdapter.getItem(position);
                        JavaScriptInterface jobj = new JavaScriptInterface(EditActivity.this);
                        if (Status) {
                            jobj.toggleSMS(msg._id, "1");
                            Toast.makeText(EditActivity.this, R.string.edit_reminder_enabled_label, Toast.LENGTH_SHORT).show();
                            updateTitle(groupid);
                            return;
                        }
                        jobj.toggleSMS(msg._id, "0");
                        Toast.makeText(EditActivity.this, R.string.edit_reminder_disabled_label, Toast.LENGTH_SHORT).show();
                        updateTitle(groupid);
                        break;
                    case CASE_COPY:
                        String[] groupsIds = sfGroup.getString("groups", null).split(",");
                        final List<String> groupNames = new ArrayList();
                        final List<String> gIdList = new ArrayList();
                        int length = groupsIds.length;
                        final Message msgClone = remindersAdapter.getItem(position);
                        for (int i = 0; i < length; i ++) {
                            String[] content = groupsIds[i].split("\\|");
                            String gId = content[0];
                            String gName = content[1];
                            if (!gId.equals(groupid)) {
                                groupNames.add(gName);
                                gIdList.add(gId);
                            }
                        }
                        AlertDialog.Builder copyDialog = new AlertDialog.Builder(EditActivity.this, R.style.AppTheme_AlertDialog);
                        copyDialog.setTitle(R.string.copy_reminder_label);

                        LayoutInflater li = LayoutInflater.from(EditActivity.this);
                        View view = li.inflate(R.layout.list_view, null);
                        copyDialog.setView(view);

                        final ListView listView = (ListView) view.findViewById(R.id.listview);
                        groupNames.add(0, EditActivity.this.getResources().getString(R.string.select_all_label));
                        gIdList.add(0, EditActivity.this.getResources().getString(R.string.select_all_label));
                        ArrayAdapter<String> ad = new ArrayAdapter<String>(EditActivity.this, R.layout.simple_list_item_multiple_choice , R.id.text1, groupNames);
                        listView.setAdapter(ad);
                        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0 &&  listView.getCheckedItemPositions().get(0)) {
                                    for (int i = 0; i < groupNames.size(); i ++) {
                                        listView.setItemChecked(i, true);
                                    }
                                } else if (listView.getCheckedItemPositions().get(0)) {
                                    listView.setItemChecked(0, false);
                                }
                            }
                        });
                        listView.setDivider(null);

                        copyDialog.setPositiveButton("Done", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Dialog dialg = (Dialog) dialog;
                                SparseBooleanArray checked = listView.getCheckedItemPositions();
                                String groups = "";
                                int i = 0;
                                while (i < listView.getAdapter().getCount()) {
                                    if (checked.get(i) && i > 0) {
                                        groups = groups + ((String) gIdList.get(i)) + ",";
                                    }
                                    i += 1;
                                }
                                if (!groups.equals("")) {
                                    new JavaScriptInterface(EditActivity.this).cloneMessage(groups.substring(0, groups.length() - 1), msgClone._id);
                                }
                            }
                        });//second parameter used for onclicklistener
                        copyDialog.setNegativeButton(R.string.close, null);
                        copyDialog.show();
                        break;
                    case CASE_DELETE:
                        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(EditActivity.this, R.style.AppTheme_AlertDialog);
                        deleteDialog.setTitle(R.string.delete_reminder_label);
                        deleteDialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Dialog dialg = (Dialog) dialog;
                                new JavaScriptInterface(EditActivity.this).deleteMessage(remindersAdapter.getItem(position)._id);
                                EditActivity.this.finish();
                                EditActivity.this.startActivity(EditActivity.this.getIntent());
                            }
                        });//second parameter used for onclicklistener
                        deleteDialog.setNegativeButton(R.string.cancel, null);
                        deleteDialog.show();
                        break;
                    case CASE_EDIT /*3*/:
                        Message edMsg = remindersAdapter.getItem(position);
                        EditActivity editActivity = EditActivity.this;
                        Context context = EditActivity.this;
                        String str = groupid;
                        startActivity(new Intent(EditActivity.this, CreateActivity.class).putExtra("groupid", groupid).putExtra("msgid", edMsg._id).putExtra("caller","EditActivity"));
                    default:
                        break;
                }
            }
        });

        RecyclerView remindersRecyclerView = (RecyclerView) findViewById(R.id.list_reminders);
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        remindersRecyclerView.setNestedScrollingEnabled(false);
        remindersRecyclerView.setAdapter(remindersAdapter);

        mPeopleList = new ArrayList<>();
        mPeopleAdapter = new SimpleAdapter(this, mPeopleList, R.layout.simple_list_item_2_small, new String[] {"Name", "Phone"}, new int[] {R.id.text1, R.id.text2 });
        getContactsWrapper();

        emptyIcon = (ImageView) findViewById(R.id.empty_icon);
        emptyText = (TextView) findViewById(R.id.empty_text);

        mAddContactView = (AutoCompleteTextView) findViewById(R.id.edit_new_member);
        mAddContactView.setAdapter(mPeopleAdapter);
        mAddContactView.setThreshold(1);
        mAddContactView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAddContactView.setText("");
                HashMap<String,String> namephone =(HashMap) mPeopleAdapter.getItem(position);
                Contacts contacts=new Contacts();
                contacts.Name=namephone.get("Name");;
                contacts.Phone=namephone.get("Phone");
                mMembersList.add(contacts);
                membersRecyclerView.getAdapter().notifyDataSetChanged();
                updateEmptyMembersView();
                Group.saveGroupContacts(EditActivity.this, groupid, contacts.Name, contacts.Phone.toString().replace("-", "").replace("(","").replace(")", BuildConfig.FLAVOR).replace(" ",""));
            }
        });

        mMembersList = Group.getGroupContacts(getApplicationContext(), groupid);
        membersRecyclerView = (RecyclerView) findViewById(R.id.list_group_members);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(EditActivity.this));
        membersRecyclerView.setAdapter(new MembersAdapter(mMembersList));

        updateEmptyMembersView();

        mSaveButton = (TextView) findViewById(R.id.save_contact);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, mAddContactView.getText().toString());

                startActivityForResult(intent, REQUEST_ADD_CONTACT);
                */

                mAddContactTextView = (TextView) findViewById(R.id.edit_new_member);
                String namephone = mAddContactTextView.getText().toString();
                if(!namephone.isEmpty()) {
                    Contacts contacts = new Contacts();
                    contacts.Name = namephone;
                    contacts.Phone = namephone;
                    mMembersList.add(contacts);
                    membersRecyclerView.getAdapter().notifyDataSetChanged();
                    updateEmptyMembersView();
                    mAddContactTextView.setText("");
                    Group.saveGroupContacts(EditActivity.this, groupid, contacts.Phone.toString().replace("-", "").replace("(", "").replace(")", BuildConfig.FLAVOR).replace(" ", ""), contacts.Phone.toString().replace("-", "").replace("(", "").replace(")", BuildConfig.FLAVOR).replace(" ", ""));
                }

            }
        });
    }

    private void updateTitle(String groupid) {
        String title=EditActivity.this.getResources().getString(R.string.scheduled_reminder_label);
        List<Message> grpMsg = Group.getGroupMessages(getApplicationContext(), groupid);
        TextView reminderTitle =(TextView) findViewById(R.id.reminderTitle);
        Integer totalMsgCount =grpMsg.size();
        Integer activeMsgCount=0;
        for (Message msg : grpMsg) {
            if(msg._sms_active.toLowerCase().equals("1")){
                activeMsgCount++;
            }
        }
        reminderTitle.setText(title+" ("+totalMsgCount+") "+ EditActivity.this.getResources().getString(R.string.active_label)+" ("+activeMsgCount+")");
    }

    private void updateEmptyMembersView() {
        if (mMembersList.isEmpty()) {
            emptyIcon.setVisibility(View.VISIBLE);
            emptyText.setText(R.string.tip_this_group_has_no_members);
        } else {
            emptyIcon.setVisibility(View.INVISIBLE);
            emptyText.setText(R.string.tip_tap_to_delete);
        }
    }

    private void getContactsWrapper() {
        int hasReadContactsPermission = ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.READ_CONTACTS);

        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(EditActivity.this, Manifest.permission.READ_CONTACTS)) {
                showMessageOKCancel("You need to allow access to Contacts to add contacts to this group",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(EditActivity.this, new String[] {Manifest.permission.READ_CONTACTS}, REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }

            ActivityCompat.requestPermissions(EditActivity.this, new String[] {Manifest.permission.READ_CONTACTS}, REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }

        new FetchContactsTask().execute();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(EditActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    new FetchContactsTask().execute();
                    mAddContactView.setFocusable(true);
                    mAddContactView.setFocusableInTouchMode(true);
                    mAddContactView.setHint(R.string.type_contact_name_or_phone_number);
                } else {
                    // Permission Denied
                    mAddContactView.setFocusable(false);
                    mAddContactView.setFocusableInTouchMode(false);
                    mAddContactView.setHint(R.string.allow_contacts);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private class FetchContactsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Cursor people = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (people != null) {
                while (people.moveToNext()) {
                    String contactName = people.getString(people.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String contactId = people.getString(people.getColumnIndex(ContactsContract.Contacts._ID));
                    String hasPhone = people.getString(people.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if ((Integer.parseInt(hasPhone) > 0)) {
                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        while (phones.moveToNext()) {
                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                            Map<String, String> NamePhone = new HashMap<String, String>();
                            NamePhone.put("Name", contactName);
                            NamePhone.put("Phone", phoneNumber);

                            mPeopleList.add(NamePhone);
                        }
                        phones.close();
                    }
                }
            }
            people.close();

            return null;
        }

        protected void onPostExecute() {
            mPeopleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_delete_group:
                new DeleteGroupDialogFragment().show(getSupportFragmentManager(), "DeleteAll");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DeleteGroupDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.delete_group)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new JavaScriptInterface(DeleteGroupDialogFragment.this.getContext()).deleteGroup(EditActivity.globalGroupId);
                            Toast.makeText(getActivity(), R.string.group_delete_success, Toast.LENGTH_SHORT).show();
                            dismiss();
                            getActivity().finish();
                            startActivity(new Intent(DeleteGroupDialogFragment.this.getContext(), HomeActivity.class));
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

    private class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {
        private List<Contacts> mList;
        public MembersAdapter(List mMembersList) {
            this.mList = mMembersList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item_2_one_line, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Contacts contact = (Contacts) this.mList.get(position);

            holder.mName.setText(contact.Name);
            holder.mNumber.setText(contact.Phone);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mName;
            public TextView mNumber;

            public ViewHolder(View itemView) {
                super(itemView);

                mName = (TextView) itemView.findViewById(R.id.text1);
                mNumber = (TextView) itemView.findViewById(R.id.text2);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Contacts contact = (Contacts) EditActivity.this.mMembersList.get(ViewHolder.this.getAdapterPosition());
                        EditActivity.this.mMembersList.remove(ViewHolder.this.getAdapterPosition());
                        Group.deleteGroupContacts(EditActivity.this, globalGroupId, contact.Name, contact.Phone.toString().replace("-","").replace("(", "").replace(")", "").replace(" ", ""));

                        notifyDataSetChanged();
                        updateEmptyMembersView();
                        //mMembersList.remove(getAdapterPosition());
                    }
                });
            }
        }
    }
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
