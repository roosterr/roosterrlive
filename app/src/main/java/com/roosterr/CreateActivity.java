package com.roosterr;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateActivity extends AppCompatActivity {
    public static String MsgTrigger;
    public static String MsgType;
    String cfreq;
    String number;
    Integer repeat;

    final private int REQUEST_CODE_NEW = 1234;
    final private int REQUEST_CODE_CHECK = 5678;
    final String RETURN_ITEMS = "ITEMS";

    public EditText message;
    String oldFreq;
    String oldDate;
    String oldTime;
    String caller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String groupid;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("groupid", 0);
        String sfGroupId = sharedPreferences.getString("groupid", null);
        if (sfGroupId == null || sfGroupId == "") {
            groupid = getIntent().getStringExtra("groupid");
        } else {
            groupid = sfGroupId;
        }
        Editor sfsocial = getSharedPreferences("groupid", 0).edit();
        sfsocial.putString("groupid", groupid);
        sfsocial.commit();

        SharedPreferences.Editor alarmTune = getSharedPreferences("Alarm_Tune", 0).edit();
        alarmTune.putString("alarm","-1");
        alarmTune.commit();

        final String msgid = getIntent().getStringExtra("msgid");
        caller     = getIntent().getStringExtra("caller");
        message = (EditText) findViewById(R.id.reminder_message);

        int[] typeImageListGrey = new int[]{R.drawable.ic_meeting_greyscale, R.drawable.ic_todo_greyscale, R.drawable.ic_billpay_greyscale, R.drawable.ic_birthday_greyscale, R.drawable.ic_anniversary_greyscale};
        int[] typeImageList = new int[]{R.drawable.ic_to_do,R.drawable.ic_meeting,  R.drawable.ic_bill_pay, R.drawable.ic_birthday, R.drawable.ic_anniversary};

        String[] typeTextList = new String[]{CreateActivity.this.getResources().getString(R.string.todo_label),
                CreateActivity.this.getResources().getString(R.string.meeting_label), CreateActivity.this.getResources().getString(R.string.bill_pay_label),
                CreateActivity.this.getResources().getString(R.string.birthday_label), CreateActivity.this.getResources().getString(R.string.anniversary_label)};

        String[] typeEngTextList = new String[]{"To Do","Meeting", "Bill Pay", "Birthday", "Anniversary"};

        int[] triggerImageList = new int[]{R.drawable.ic_message_24dp, R.drawable.ic_alarms_24dp, R.drawable.ic_call_24dp};
        String[] triggerTextList = new String[]{CreateActivity.this.getResources().getString(R.string.scheduled_sms_text),
                CreateActivity.this.getResources().getString(R.string.alarm_label),
                CreateActivity.this.getResources().getString(R.string.call_reminder_label)};

        String[] triggerEngTextList = new String[]{"Scheduled SMS", "Alarm", "Call Reminder"};

        final String[] triggerHintTextList = new String[]{CreateActivity.this.getResources().getString(R.string.sms_hint_label),
                CreateActivity.this.getResources().getString(R.string.alarm_hint_label), CreateActivity.this.getResources().getString(R.string.optional_call_reminder_hint_label)};

        RecyclerView typeReminderGrid = (RecyclerView) findViewById(R.id.reminder_type_list);
        final ImageAdapter typeAdapter = new ImageAdapter(CreateActivity.this, 0, typeImageListGrey, typeTextList, typeImageList);
        typeAdapter.setOnItemClickListener(null);
        typeReminderGrid.setAdapter(typeAdapter);

        final String go_pro =  getSharedPreferences("Purchase_Type", 0).getString("go_pro", null);
        final String both_products =  getSharedPreferences("Purchase_Type", 0).getString("both", null);

        final View buttonAlarmTune = findViewById(R.id.button_alarm_tune);
        buttonAlarmTune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmTuneDialogFragment tuneDialog = new AlarmTuneDialogFragment();
                tuneDialog.show(getFragmentManager(), "AlarmTuneDialog");
            }
        });

        final View buttonChecklist = findViewById(R.id.button_check);
        //if(go_pro.equals("1") || both_products.equals("1")) {
            buttonChecklist.setVisibility(View.VISIBLE);
        //}
        //else{
        //    buttonChecklist.setVisibility(View.INVISIBLE);
        //}
        buttonChecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(CreateActivity.this, CheckListActivity.class), REQUEST_CODE_CHECK);
            }
        });

        final RecyclerView triggerGrid = (RecyclerView) findViewById(R.id.reminder_trigger_list);
        final ImageAdapter triggerAdapter = new ImageAdapter(CreateActivity.this, 1, triggerImageList, triggerTextList, null);
        triggerAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                message.setHint(triggerHintTextList[position]);

                if(position == 0) {
                    buttonAlarmTune.setVisibility(View.INVISIBLE);
                    //if(go_pro.equals("1") || both_products.equals("1")) {
                        buttonChecklist.setVisibility(View.VISIBLE);
                    //}
                    //else{
                    //    buttonChecklist.setVisibility(View.INVISIBLE);
                    //}
                } else if (position == 1) {
                    buttonChecklist.setVisibility(View.INVISIBLE);
                    buttonAlarmTune.setVisibility(View.VISIBLE);
                } else {
                    buttonChecklist.setVisibility(View.INVISIBLE);
                    buttonAlarmTune.setVisibility(View.INVISIBLE);
                }
            }
        });
        triggerGrid.setAdapter(triggerAdapter);

        View pickDate = findViewById(R.id.create_pick_date);
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        View pickTime = findViewById(R.id.create_pick_time);
        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        final String[] repititionOptions = new String[]{CreateActivity.this.getResources().getString(R.string.never_repeat_label),
                CreateActivity.this.getResources().getString(R.string.every_day_label), CreateActivity.this.getResources().getString(R.string.every_week_label),
                CreateActivity.this.getResources().getString(R.string.mon_fri_label), CreateActivity.this.getResources().getString(R.string.sat_sun_label), CreateActivity.this.getResources().getString(R.string.every_month_label),
                CreateActivity.this.getResources().getString(R.string.every_year_label),CreateActivity.this.getResources().getString(R.string.custom_label)};
        View pickRepetition = findViewById(R.id.create_pick_repitition);
        pickRepetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder repetitionDialog = new AlertDialog.Builder(CreateActivity.this, R.style.AppTheme_AlertDialog);
                repetitionDialog.setSingleChoiceItems(repititionOptions, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        if (which == 0 || which == 1 || which == 2) {
                            CreateActivity.this.repeat = Integer.valueOf(which);
                        }
                        if (which == 3 || which == 4) {
                            CreateActivity.this.repeat = Integer.valueOf(which + 2);
                        }
                        if (which == 5 || which == 6) {
                            CreateActivity.this.repeat = Integer.valueOf(which - 2);
                        }
                        if(which == (repititionOptions.length - 1)) {
                            CreateActivity.this.repeat = Integer.valueOf(8);
                            AlertDialog.Builder customRepetitionDialog = new AlertDialog.Builder(CreateActivity.this, R.style.AppTheme_AlertDialog);
                            customRepetitionDialog.setTitle(CreateActivity.this.getResources().getString(R.string.repeat_every_label));
                            customRepetitionDialog.setPositiveButton(CreateActivity.this.getResources().getString(R.string.ok_label), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog2, int which) {
                                    Dialog dialg = (Dialog) dialog2;
                                    Spinner spnr = (Spinner) dialg.findViewById(R.id.spinner);
                                    CreateActivity.this.number = spnr.getSelectedItem().toString();
                                    Spinner spnr_f = (Spinner) dialg.findViewById(R.id.spinner2);
                                    CreateActivity.this.cfreq = spnr_f.getSelectedItem().toString();
                                    dialog.dismiss();
                                }
                            });
                            customRepetitionDialog.setNegativeButton(CreateActivity.this.getResources().getString(R.string.cancel), null);
                            customRepetitionDialog.setView(R.layout.dialog_repetition_custom);
                            customRepetitionDialog.setCancelable(false);
                            customRepetitionDialog.show();
                        } else {
                            dialog.dismiss();
                        }
                        ((TextView) CreateActivity.this.findViewById(R.id.repitition_text)).setText(repititionOptions[which]);
                    }
                });
                repetitionDialog.show();
            }
        });

        final Button saveButton = (Button) findViewById(R.id.reminder_save);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //loose focus from TextBox on click of Save button
                CreateActivity.this.findViewById(R.id.reminder_message).clearFocus();
                saveButton.requestFocus();
                String triggerText;
                String typeText;
                final int trigger = triggerAdapter.selectedPos;
                final int type = typeAdapter.selectedPos;
                String cfreqVal = "";
                String message;
                String status="Y";
                switch (trigger) {
                    case 0:
                        message = CreateActivity.this.getResources().getString(R.string.sms_scheduled_label);
                        triggerText = "Scheduled SMS";
                        break;
                    case 1:
                        message = CreateActivity.this.getResources().getString(R.string.alarm_scheduled_label);
                        triggerText = "Alarm";
                        break;
                    case 2:
                        message = CreateActivity.this.getResources().getString(R.string.call_scheduled_label);
                        triggerText = "Call Reminder";
                        break;
                    default:
                        message = "";
                        triggerText = "Call Reminder";
                        break;
                }
                switch (type) {
                    case 0:
                        typeText = "To Do";
                        break;
                    case 1:
                        typeText = "Meeting";
                        break;
                    case 2:
                        typeText = "Bill Pay";
                        break;
                    case 3:
                        typeText = "Birthday";
                        break;
                    case 4:
                        typeText = "Anniversary";
                        break;
                    default:
                        typeText = "";
                        break;
                }
                if (CreateActivity.this.cfreq != null) {
                    String str = CreateActivity.this.cfreq;
                    final String MONTH_LABEL = CreateActivity.this.getResources().getString(R.string.days_label);
                    final String DAY_LABEL = CreateActivity.this.getResources().getString(R.string.days_label);
                    final String WEEK_LABEL =CreateActivity.this.getResources().getString(R.string.weeks_label);
                    final String YEAR_LABEL = CreateActivity.this.getResources().getString(R.string.years_label);

                    if(str.equals(MONTH_LABEL)){
                        cfreqVal = "3";
                    }
                    if(str.equals(DAY_LABEL)){
                        cfreqVal = "1";
                    }
                    if(str.equals(WEEK_LABEL)){
                        cfreqVal = "2";
                    }
                    if(str.equals(YEAR_LABEL)){
                        cfreqVal = "4";
                    }

                }
                String alarmTune  =  getSharedPreferences("Alarm_Tune", 0).getString("alarm", null);
                if(triggerText.equals("Alarm") && alarmTune.equals("-1")) {
                    alarmTune="0";
                }
                String content = ((EditText) CreateActivity.this.findViewById(R.id.reminder_message)).getText().toString();
                TextView date = (TextView) CreateActivity.this.findViewById(R.id.reminder_date);
                TextView time = (TextView) CreateActivity.this.findViewById(R.id.reminder_time);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                String language = Locale.getDefault().getLanguage();
                if(language.equals("ja"))
                    simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd");

                SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm aa");
                try {
                    Date selectedDate = simpleDateFormat.parse(date.getText().toString());
                    simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
                    SimpleDateFormat simpleDateFormat2;
                    try {
                        String message_date = simpleDateFormat.format(selectedDate);
                        simpleDateFormat = new SimpleDateFormat("HH:mm",Locale.ENGLISH);
                        String message_time = simpleDateFormat.format(inFormat.parse(time.getText().toString()));
                        String frequency="0";
                        if(CreateActivity.this.repeat!=null)
                            frequency = CreateActivity.this.repeat.toString();

                        String data_val = CreateActivity.this.number + "-" + cfreqVal;
                        //if date is current date and time is in past then increase the date by 1 day
                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
                        String currentDate = currentDateFormat.format(Calendar.getInstance().getTime());
                        if(currentDate.equals(message_date)){
                            Calendar datetime = Calendar.getInstance();
                            Calendar c = Calendar.getInstance();
                            datetime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(message_time.split(":")[0]));
                            datetime.set(Calendar.MINUTE, Integer.parseInt(message_time.split(":")[1]));
                            if(datetime.getTimeInMillis() > c.getTimeInMillis()){
                                //it's after current
                            }else{
                                //add a day to current date
                                Calendar crDate = Calendar.getInstance();
                                crDate.setTime(selectedDate);
                                crDate.add(Calendar.DATE, 1);  // number of days to add
                                message_date = currentDateFormat.format(crDate.getTime());  // dt is now the new date
                            }
                        }
                        if (msgid == null || msgid == "") {
                            String recordId = Group.saveGroupMessages(CreateActivity.this.getApplicationContext(), groupid, content, message_date, message_time, frequency, "-99999", data_val, typeText, triggerText,alarmTune);
                            Intent i = new Intent(CreateActivity.this, ReminderSetActivity.class).putExtra("groupid", groupid).putExtra("msgid",recordId).putExtra("caller",caller);
                            i.putExtra("REMINDER_MESSAGE", message);
                            CreateActivity.this.startActivityForResult(i, REQUEST_CODE_NEW);
                        } else {
                            if(oldDate!=date.getText().toString())
                                status="N";
                            if(oldTime!=time.getText().toString())
                                status="N";
                            if(oldFreq!=frequency)
                                status="N";

                            Group.saveGroupMessages(CreateActivity.this.getApplicationContext(), groupid, content, message_date, message_time, frequency, msgid, data_val, typeText, triggerText,alarmTune,status);

                            Intent i = new Intent(CreateActivity.this, ReminderSetActivity.class).putExtra("groupid", groupid).putExtra("msgid",msgid).putExtra("caller",caller);

                            i.putExtra("REMINDER_MESSAGE", message);
                            CreateActivity.this.startActivityForResult(i, REQUEST_CODE_NEW);

                        }
                        simpleDateFormat2 = simpleDateFormat;
                    } catch (Exception e) {
                        simpleDateFormat2 = simpleDateFormat;
                    }
                }
                catch (Exception e2) {
                    Log.e("MYAPP", "exception", e2);
                }

            }
        });

        Button cancelButton = (Button) findViewById(R.id.reminder_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO handle click
                finish();
            }
        });

        Calendar c = Calendar.getInstance();
        long current = c.getTimeInMillis() + 300000;
        c.setTimeInMillis(current);
        //System.out.println("Current time => " + c.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String language = Locale.getDefault().getLanguage();
        if(language.equals("ja"))
            simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd");

        String formattedDate = simpleDateFormat.format(c.getTime());

        SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm a");
        TextView date = (TextView) findViewById(R.id.reminder_date);
        date.setText(formattedDate);

        String formattedTime = inFormat.format(c.getTime());
        TextView time = (TextView) findViewById(R.id.reminder_time);
        time.setText(formattedTime);

        if (msgid != null && msgid != "") {
            Message editMessage = Message.getMessageById(this, msgid);
            ((EditText) findViewById(R.id.reminder_message)).setText(editMessage._message.replaceAll("__", "'"));

            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            inFormat = new SimpleDateFormat("HH:mm");
            try {
                Date selectedDate = simpleDateFormat.parse(editMessage._sms_next);

                simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                if(language.equals("ja"))
                    simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd");
                try {
                    date.setText(simpleDateFormat.format(selectedDate));
                    time.setText(new SimpleDateFormat("hh:mm aa").format(inFormat.parse(editMessage._sms_time)));
                    this.repeat = Integer.valueOf(Integer.parseInt(editMessage._sms_repeat));
                    String frequency = this.repeat.toString();
                    oldFreq=frequency;
                    if (frequency.equals("0")) {
                        frequency = CreateActivity.this.getResources().getString(R.string.never_repeat_label);
                    }
                    if (frequency.equals("1")) {
                        frequency = CreateActivity.this.getResources().getString(R.string.every_day_label);
                    }
                    if (frequency.equals("2")) {
                        frequency = CreateActivity.this.getResources().getString(R.string.every_week_label);
                    }
                    if (frequency.equals("3")) {
                        frequency = CreateActivity.this.getResources().getString(R.string.every_month_label);
                    }
                    if (frequency.equals("4")) {
                        frequency = CreateActivity.this.getResources().getString(R.string.every_year_label);
                    }
                    if (frequency.equals("5")) {
                        frequency = CreateActivity.this.getResources().getString(R.string.mon_fri_label);
                    }
                    if (frequency.equals("6")) {
                        frequency = CreateActivity.this.getResources().getString(R.string.sat_sun_label);
                    }
                    if (frequency.equals("8")) {
                        frequency = CreateActivity.this.getResources().getString(R.string.custom_label);
                    }

                    oldDate=date.getText().toString();
                    oldTime=time.getText().toString();


                    ((TextView) findViewById(R.id.repitition_text)).setText(frequency);
                    MsgType = editMessage.type;
                    MsgTrigger = editMessage.trigger;
                    if (!(MsgTrigger == null || MsgTrigger == "")) {
                        triggerAdapter.selectedPos = Integer.valueOf(Arrays.asList(triggerEngTextList).indexOf(MsgTrigger)).intValue();
                        if(MsgTrigger.equals("Alarm")){
                            buttonChecklist.setVisibility(View.INVISIBLE);
                            buttonAlarmTune.setVisibility(View.VISIBLE);
                        }

                    }
                    if (MsgType != null && MsgType != "") {
                        typeAdapter.selectedPos = Integer.valueOf(Arrays.asList(typeEngTextList).indexOf(MsgType)).intValue();
                    }
                } catch (Exception e) {
                    SimpleDateFormat simpleDateFormat2 = simpleDateFormat;
                }
            } catch (Exception e2) {
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_NEW && resultCode == RESULT_OK) {
            finish();
        }

        if (requestCode == REQUEST_CODE_CHECK && resultCode == RESULT_OK) {
            if (data != null) {
                message.setText(CreateActivity.this.getResources().getString(R.string.checklist_label)+": \n" + data.getStringExtra(RETURN_ITEMS));
            } else {
                Toast.makeText(this, CreateActivity.this.getResources().getString(R.string.no_items_selected_label), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private int[] list;
        private int[] colorList;
        private String[] listText;
        private int selectedPos = 0;
        private Context context;
        private int type;

        public ImageAdapter(Context context, int i, int[] list, String[] textList, int[] color) {
            this.selectedPos = 0;
            this.context = context;
            this.list = list;
            this.listText = textList;
            this.type = i;

            if (type == 0) {
                this.colorList = color;
            } else {
                this.colorList = null;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item_create, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (type == 0) {
                holder.image.setImageResource(colorList[position]);
            } else {
                holder.image.setImageResource(list[position]);
            }

            holder.text.setText(listText[position]);
            holder.checkmark.setSelected(selectedPos == position);
        }

        @Override
        public int getItemCount() {
            return list.length;
        }

        // Define listener member variable
        private static OnItemClickListener listener;
        // Define the listener interface
        public interface OnItemClickListener {
            void onItemClick(View itemView, int position);
        }
        // Define the method that allows the parent activity or fragment to define the listener
        public void setOnItemClickListener(OnItemClickListener onlistener) {
            this.listener = onlistener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView image;
            public TextView text;
            public ImageView checkmark;

            public ViewHolder(View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.image);
                text = (TextView) itemView.findViewById(R.id.text);
                checkmark = (ImageView) itemView.findViewById(R.id.checkmark);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyItemChanged(selectedPos);
                        selectedPos = getLayoutPosition();
                        notifyItemChanged(selectedPos);

                        if (listener != null && type == 1)
                            listener.onItemClick(v, selectedPos);
                    }
                });
            }
        }
    }

    public void onBackPressed() {
        super.onBackPressed();

        if(caller.equals("EditActivity"))
            startActivity(new Intent(this, EditActivity.class));
        else
            startActivity(new Intent(this, SentRemindersActivity.class));
        finish();
    }
}
