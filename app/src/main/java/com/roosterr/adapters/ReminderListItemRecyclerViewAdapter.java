package com.roosterr.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.roosterr.R;
import com.roosterr.Message;
import com.roosterr.dummy.DummyReminderContent;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Message}
 * TODO: Replace the implementation with code for your data type.
 */
public class ReminderListItemRecyclerViewAdapter extends RecyclerView.Adapter<ReminderListItemRecyclerViewAdapter.ViewHolder> {

    private final List<Message> mValues;
    private Map mTypes;
    private Map mTriggers;
    private final Context mContext;

    public ReminderListItemRecyclerViewAdapter(Context context, List<Message> items) {
        mValues = items;
        mContext = context;

        mTypes = DummyReminderContent.TYPES;
        mTriggers = DummyReminderContent.TRIGGERS;
    }

    // Define listener member variable
    private static OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int clickOn, int position,boolean status);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            holder.mItem = (Message) this.mValues.get(position);
            holder.mTypeImageView.setImageResource(((Integer) this.mTypes.get(holder.mItem.type)).intValue());
            //holder.mTypeTextView.setText(holder.mItem.type);
            if(holder.mItem.type.equals("To Do"))
                holder.mTypeTextView.setText(R.string.todo_label);
            else if(holder.mItem.type.equals("Anniversary"))
                holder.mTypeTextView.setText(R.string.anniversary_label);
            else if(holder.mItem.type.equals("Birthday"))
                holder.mTypeTextView.setText(R.string.birthday_label);
            else if(holder.mItem.type.equals("Bill Pay"))
                holder.mTypeTextView.setText(R.string.bill_pay_label);
            else if(holder.mItem.type.equals("Meeting"))
                holder.mTypeTextView.setText(R.string.meeting_label);

            holder.mTriggerImageView.setImageResource(((Integer) this.mTriggers.get(holder.mItem.trigger)).intValue());
            //holder.mTriggerTextView.setText(holder.mItem.trigger);
            if(holder.mItem.trigger.equals("Scheduled SMS"))
                holder.mTriggerTextView.setText(R.string.sms_label);
            else if(holder.mItem.trigger.equals("Call Reminder"))
                holder.mTriggerTextView.setText(R.string.call_label);
            else if(holder.mItem.trigger.equals("Alarm"))
                holder.mTriggerTextView.setText(R.string.alarm_label);

            holder.mDateView.setText(holder.mItem._sms_next.split(" ")[0]);
            holder.mTimeView.setText(new SimpleDateFormat("hh:mm aa").format(new SimpleDateFormat("HH:mm").parse(holder.mItem._sms_time)));

            //holder.mRepeatView.setText(holder.mItem._sms_repeat);

            if(holder.mItem._sms_repeat.equals("Never Repeat"))
                holder.mRepeatView.setText(R.string.never_repeat_label);
            else if(holder.mItem._sms_repeat.equals("Every Day"))
                holder.mRepeatView.setText(R.string.every_day_label);
            else if(holder.mItem._sms_repeat.equals("Every Week"))
                holder.mRepeatView.setText(R.string.every_week_label);
            else if(holder.mItem._sms_repeat.equals("Monday-Friday"))
                holder.mRepeatView.setText(R.string.mon_fri_label);
            else if(holder.mItem._sms_repeat.equals("Saturday-Sunday"))
                holder.mRepeatView.setText(R.string.sat_sun_label);
            else if(holder.mItem._sms_repeat.equals("Every Month"))
                holder.mRepeatView.setText(R.string.every_month_label);
            else if(holder.mItem._sms_repeat.equals("Every Year"))
                holder.mRepeatView.setText(R.string.every_year_label);
            else if(holder.mItem._sms_repeat.equals("Custom"))
                holder.mRepeatView.setText(R.string.custom_label);

            if (holder.mItem._sms_active.toString().equals("1")) {
                holder.mReminderSwitch.setChecked(true);
            } else {
                holder.mReminderSwitch.setChecked(false);
            }
            holder.mContentView.setText(((Message) this.mValues.get(position))._message.replaceAll("__", "'"));
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
    public Message getItem(int position) {
        return (Message) this.mValues.get(position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView mTypeTextView;
        public final ImageView mTypeImageView;

        public final TextView mTriggerTextView;
        public final ImageView mTriggerImageView;

        public final TextView mDateView;
        public final TextView mTimeView;
        public final TextView mRepeatView;

        public final TextView mContentView;

        public final ImageButton mCopyButton;
        public final ImageButton mDeleteButton;

        public Message mItem;
        public final SwitchCompat mReminderSwitch;

        public static final int CASE_REMINDER = 0;
        public static final int CASE_COPY = 1;
        public static final int CASE_DELETE = 2;
        public static final int EDIT_REMINDER = 3;
        public ViewHolder(View view) {
            super(view);
            mView = view;

            mTypeImageView = (ImageView) view.findViewById(R.id.reminder_type_image);
            mTypeTextView = (TextView) view.findViewById(R.id.reminder_type_text);

            mTriggerImageView = (ImageView) view.findViewById(R.id.reminder_trigger_image);
            mTriggerTextView = (TextView) view.findViewById(R.id.reminder_trigger_text);

            mDateView = (TextView) view.findViewById(R.id.date);
            mTimeView = (TextView) view.findViewById(R.id.time);
            mRepeatView = (TextView) view.findViewById(R.id.repeat);

            mContentView = (TextView) view.findViewById(R.id.reminder_name);

            mCopyButton = (ImageButton) view.findViewById(R.id.button_reminder_copy);
            mDeleteButton = (ImageButton) view.findViewById(R.id.button_reminder_delete);
            mReminderSwitch = (SwitchCompat) view.findViewById(R.id.switch_reminder);
            mCopyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemClick(itemView, CASE_COPY, getLayoutPosition(),false);
                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemClick(itemView, CASE_DELETE, getLayoutPosition(),false);
                }
            });
            mReminderSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemClick(itemView, CASE_REMINDER, getLayoutPosition(),ViewHolder.this.mReminderSwitch.isChecked());
                }
            });
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemClick(itemView, EDIT_REMINDER, getLayoutPosition(),false);
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
