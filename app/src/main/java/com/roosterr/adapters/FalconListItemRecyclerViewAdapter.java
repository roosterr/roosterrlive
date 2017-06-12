package com.roosterr.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roosterr.Base64;
import com.roosterr.Group;
import com.roosterr.Message;
import com.roosterr.R;
import com.roosterr.dummy.DummyContent.DummyItem;
import com.roosterr.dummy.DummyReminderContent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}
 * TODO: Replace the implementation with code for your data type.
 */
public class FalconListItemRecyclerViewAdapter extends RecyclerView.Adapter<FalconListItemRecyclerViewAdapter.ViewHolder> {

    private final List<Message> mValues;
    private Map mTypes;
    private Map mTriggers;
    private final Context mContext;

    public FalconListItemRecyclerViewAdapter(Context context, List<Message> items) {
        mValues = items;
        mContext = context;

        mTypes = DummyReminderContent.TYPES;
        mTriggers = DummyReminderContent.TRIGGERS;
    }

    // Define listener member variable
    private static OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int clickOn, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_falcon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            holder.mItem = mValues.get(position);
            holder.mTypeImageView.setImageResource((Integer) mTypes.get(holder.mItem.type));
            String date = holder.mItem._sms_next;
            holder.mDateView.setText(date);
            holder.mTimeView.setText(new SimpleDateFormat("hh:mm aa").format(new SimpleDateFormat("HH:mm").parse(holder.mItem._sms_time)));

            //holder.mRepeatView.setText(holder.mItem._sms_repeat);
            if(holder.mItem._sms_repeat.equals("Never repeat"))
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

            if(holder.mItem._group!=null) {
                byte[] decodedString = Base64.decode(holder.mItem._group);
                holder.mReminderGroupImage.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
            }
            holder.mContentView.setText(mValues.get(position)._message.replaceAll("__", "'"));
            holder.mReminderGroupName.setText(mValues.get(position).group_name);
            holder.mDueIn.setText(getDueDays(date));
        }catch (Exception e) {
        }
    }
    private String getDueDays(String date){
        DateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date startDate;
        try {
            startDate = simpleDateFormat.parse(date);
            Date currentDate = new Date();
            currentDate.setHours(0);
            currentDate.setMinutes(0);
            currentDate.setSeconds(0);
            //long diff=getDateDiff(currentDate,startDate,TimeUnit.DAYS);
            long diff=getDateDiff(toCalendar(currentDate),toCalendar(startDate));
            return Long.toString(diff);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
    public static int getDateDiff(Calendar day1, Calendar day2){
        Calendar dayOne = (Calendar) day1.clone(),
                dayTwo = (Calendar) day2.clone();

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays ;
        }
    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mTypeImageView;
        public final TextView mDateView;
        public final TextView mTimeView;
        public final TextView mRepeatView;
        public final TextView mContentView;
        public final ImageView mReminderGroupImage;
        public final TextView mReminderGroupName;
        public final TextView mDueIn;
        public Message mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mTypeImageView = (ImageView) view.findViewById(R.id.reminder_type_image);

            mDateView = (TextView) view.findViewById(R.id.date);
            mTimeView = (TextView) view.findViewById(R.id.time);
            mRepeatView = (TextView) view.findViewById(R.id.repeat);
            mReminderGroupImage =(ImageView) view.findViewById(R.id.reminder_group_image);
            mReminderGroupName =(TextView) view.findViewById(R.id.reminder_group_name);
            mDueIn =(TextView) view.findViewById(R.id.reminder_days);
            mContentView = (TextView) view.findViewById(R.id.reminder_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
