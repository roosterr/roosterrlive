package com.roosterr.adapters;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.roosterr.Base64;
import com.roosterr.R;
import com.roosterr.Message;
import com.roosterr.dummy.DummyReminderContent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Message}
 * TODO: Replace the implementation with code for your data type.
 */
public class SentReminderListItemRecyclerViewAdapter extends RecyclerView.Adapter<SentReminderListItemRecyclerViewAdapter.ViewHolder> {

    public static final int CASE_DELETE = 1;
    public static final int CASE_DELETE_ALL = 2;
    public static final int CASE_EDIT = 3;
    private static OnItemClickListener listener;

    private List<Message> mValues;
    private Map mTypes;
    private Map mTriggers;

    private static final int VIEW_TYPE_REMINDER = 1;
    public static final int VIEW_TYPE_EMPTY = 2;

    public interface OnItemClickListener {
        void onItemClick(int Type, int clickOn, String MessageID);
    }
    public void setOnItemClickListener(OnItemClickListener listen) {
        listener = listen;
    }
    public SentReminderListItemRecyclerViewAdapter(List<Message> items) {
        mValues = items;
        mTypes = DummyReminderContent.TYPES;
        mTriggers = DummyReminderContent.TRIGGERS_SMALL;
    }

    public void deleteAll() {
        mValues = new ArrayList<Message>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        ViewHolder vh;

        if (viewType == VIEW_TYPE_REMINDER) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sent_reminder, parent, false);
            vh = new ViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sent_reminder_empty, parent, false);
            vh = new ViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_REMINDER) {
            holder.mItem = (Message) this.mValues.get(position);
            if(holder.mItem.type.equals("null"))
                holder.mItem.type="To Do";

            holder.mTypeImageView.setImageResource(((Integer) this.mTypes.get(holder.mItem.type)).intValue());

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

            //holder.mTypeTextView.setText(holder.mItem.type);

            holder.mTriggerImageView.setImageResource(((Integer) this.mTriggers.get(holder.mItem.trigger)).intValue());

            if(holder.mItem.trigger.equals("SMS"))
                holder.mTriggerTextView.setText(R.string.sms_label);
            else if(holder.mItem.trigger.equals("Call"))
                holder.mTriggerTextView.setText(R.string.call_label);
            else if(holder.mItem.trigger.equals("Alarm"))
                holder.mTriggerTextView.setText(R.string.alarm_label);

            holder.mDateView.setText(holder.mItem._sms_next);
            holder.mTimeView.setText(holder.mItem._sms_time);
            holder.mContentView.setText(((Message) this.mValues.get(position))._message.replaceAll("__", "'"));
            if (!(((Message) this.mValues.get(position))._group == null || ((Message) this.mValues.get(position))._group.equals(""))) {
                byte[] decodedString = Base64.decode(((Message) this.mValues.get(position))._group);
                holder.mGroupImage.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
            }
            holder.mGroupName.setText(((Message) this.mValues.get(position)).group_name);
            holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        listener.onItemClick(SentReminderListItemRecyclerViewAdapter.VIEW_TYPE_REMINDER, position, ((Message) SentReminderListItemRecyclerViewAdapter.this.mValues.get(position))._id);
                        mValues.remove(position);
                        notifyItemRemoved(position);
                    }

                }
            });
        } else if (viewType == VIEW_TYPE_EMPTY) {

        }
    }

    @Override
    public int getItemCount() {
        if(mValues.size() == 0){
            return 1;
        } else {
            return mValues.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mValues.size() == 0) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_REMINDER;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView mTypeTextView;
        public final ImageView mTypeImageView;

        public final TextView mTriggerTextView;
        public final ImageView mTriggerImageView;

        public final TextView mDateView;
        public final TextView mTimeView;

        public final TextView mContentView;
        public final ImageView mGroupImage;
        public final TextView mGroupName;
        public final ImageButton mDeleteButton;

        public Message mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mTypeImageView = (ImageView) view.findViewById(R.id.reminder_type_image);
            mTypeTextView = (TextView) view.findViewById(R.id.reminder_type_text);

            mTriggerImageView = (ImageView) view.findViewById(R.id.reminder_trigger_image);
            mTriggerTextView = (TextView) view.findViewById(R.id.reminder_trigger_text);

            mDateView = (TextView) view.findViewById(R.id.date);
            mTimeView = (TextView) view.findViewById(R.id.time);

            mContentView = (TextView) view.findViewById(R.id.reminder_name);
            mGroupImage = (ImageView) view.findViewById(R.id.reminder_group_image);
            mGroupName =(TextView) view.findViewById(R.id.sent_group_name);
            mDeleteButton = (ImageButton) view.findViewById(R.id.button_reminder_delete);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        if(SentReminderListItemRecyclerViewAdapter.this.mValues.size() >0) {
                            Message msg = ((Message) SentReminderListItemRecyclerViewAdapter.this.mValues.get(ViewHolder.this.getLayoutPosition()));
                            if (msg != null && msg._sms_id != null)
                                listener.onItemClick(SentReminderListItemRecyclerViewAdapter.CASE_EDIT, ViewHolder.this.getLayoutPosition(), msg._sms_id);
                        }
                    }
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
