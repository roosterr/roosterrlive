package com.roosterr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.roosterr.adapters.FalconListItemRecyclerViewAdapter;
import com.roosterr.ui.DividerItemDecoration;

import java.util.List;

public class FalconActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    static List<Message> meetMessageList;
    static List<Message> meetMessageListWeek;
    static List<Message> meetMessageListUpcoming;

    static List<Message> todoMessageList;
    static List<Message> todoMessageListWeek;
    static List<Message> todoMessageListUpcoming;

    static List<Message> billPayMessageList;
    static List<Message> billPayMessageListWeek;
    static List<Message> billPayMessageListUpcoming;

    static List<Message> birthMessageList;
    static List<Message> birthMessageListWeek;
    static List<Message> birthMessageListUpcoming;

    static List<Message> annMessageList;
    static List<Message> annMessageListWeek;
    static List<Message> annMessageListUpcoming;

    static List<Message> messageList;
    static List<Message> messageListWeek;
    static List<Message> messageListUpcoming;

    private static int _sectionNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falcon_upgrade);
        Button buttonUpgrade = (Button) findViewById(R.id.button_upgrade);

        final String go_pro =  getSharedPreferences("Purchase_Type", 0).getString("go_pro", null);
        final String both_products =  getSharedPreferences("Purchase_Type", 0).getString("both", null);

        messageList=Message.getMessagesFor(FalconActivity.this,"Today","0");
        messageListWeek=Message.getMessagesFor(FalconActivity.this,"Weeks","0");
        messageListUpcoming=Message.getMessagesFor(FalconActivity.this,"Upcoming","0");

        meetMessageList=Message.getMessagesFor(FalconActivity.this,"Today","Meeting");
        meetMessageListWeek=Message.getMessagesFor(FalconActivity.this,"Weeks","Meeting");
        meetMessageListUpcoming=Message.getMessagesFor(FalconActivity.this,"Upcoming","Meeting");

        todoMessageList=Message.getMessagesFor(FalconActivity.this,"Today","To Do");
        todoMessageListWeek=Message.getMessagesFor(FalconActivity.this,"Weeks","To Do");
        todoMessageListUpcoming=Message.getMessagesFor(FalconActivity.this,"Upcoming","To Do");

        billPayMessageList=Message.getMessagesFor(FalconActivity.this,"Today","Bill Pay");
        billPayMessageListWeek=Message.getMessagesFor(FalconActivity.this,"Weeks","Bill Pay");
        billPayMessageListUpcoming=Message.getMessagesFor(FalconActivity.this,"Upcoming","Bill Pay");

        birthMessageList=Message.getMessagesFor(FalconActivity.this,"Today","Birthday");
        birthMessageListWeek=Message.getMessagesFor(FalconActivity.this,"Weeks","Birthday");
        birthMessageListUpcoming=Message.getMessagesFor(FalconActivity.this,"Upcoming","Birthday");

        annMessageList=Message.getMessagesFor(FalconActivity.this,"Today","Anniversary");
        annMessageListWeek=Message.getMessagesFor(FalconActivity.this,"Weeks","Anniversary");
        annMessageListUpcoming=Message.getMessagesFor(FalconActivity.this,"Upcoming","Anniversary");

        if(go_pro.equals("1") || both_products.equals("1")){
            setupView();
        }
        else{
            buttonUpgrade.setVisibility(View.VISIBLE);
        }

        buttonUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(go_pro.equals("1") || both_products.equals("1")) {
                    setupView();
                }
                else {
                    String userId =  getSharedPreferences("AzureUser", 0).getString("azureuser", null);
                    String phoneNumber = getSharedPreferences("AzureUser", 0).getString("phone_number", null);

                    startActivity(new Intent(FalconActivity.this, UpgradeActivity.class)
                            .putExtra("UserID",userId)
                            .putExtra("Phone",phoneNumber)
                    );
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.falcon_view_label);
    }

    public void setupView() {
        setContentView(R.layout.activity_falcon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.all_reminders_label);

        int[] typeImageList = new int[]{R.drawable.ic_bell, R.drawable.ic_meeting, R.drawable.ic_to_do, R.drawable.ic_bill_pay, R.drawable.ic_birthday, R.drawable.ic_anniversary};

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setVisibility(View.VISIBLE);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffsetPixels == 0) {
                    //Do something on selected page at position
                    changeTitle(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                changeTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setVisibility(View.VISIBLE);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);

            tab.setCustomView(R.layout.falcon_tab_view);
            ImageView icon = (ImageView) tab.getCustomView().findViewById(R.id.tab_icon);
            TextView allTextCount = (TextView) tab.getCustomView().findViewById(R.id.item_count);
            switch (i){
                case 0:
                    Integer itemCount = messageList.size()+messageListWeek.size()+messageListUpcoming.size();
                    allTextCount.setText(Integer.toString(itemCount));
                    break;
                case 1:
                    Integer meetingItemCount = meetMessageList.size()+meetMessageListWeek.size()+meetMessageListUpcoming.size();
                    allTextCount.setText(Integer.toString(meetingItemCount));
                    break;
                case 2:
                    Integer todoItemCount = todoMessageList.size()+todoMessageListWeek.size()+todoMessageListUpcoming.size();
                    allTextCount.setText(Integer.toString(todoItemCount));
                    break;
                case 3:
                    Integer billPayitemCount = billPayMessageList.size()+billPayMessageListWeek.size()+billPayMessageListUpcoming.size();
                    allTextCount.setText(Integer.toString(billPayitemCount));
                    break;
                case 4:
                    Integer birthItemCount = birthMessageList.size()+birthMessageListWeek.size()+birthMessageListUpcoming.size();
                    allTextCount.setText(Integer.toString(birthItemCount));
                    break;
                case 5:
                    Integer anniversaryItemCount = annMessageList.size()+annMessageListWeek.size()+annMessageListUpcoming.size();
                    allTextCount.setText(Integer.toString(anniversaryItemCount));
                    break;
            }
            icon.setImageResource(typeImageList[i]);

        }

    }

    public  void changeTitle(int pos) {
        switch (pos) {
            case 0:
                getSupportActionBar().setTitle(R.string.all_reminders_label);
                break;
            case 1:
                getSupportActionBar().setTitle(R.string.meeting_reminders_label);
                break;
            case 2:
                getSupportActionBar().setTitle(R.string.todo_reminders_label);
                break;
            case 3:
                getSupportActionBar().setTitle(R.string.bill_pay_reminders);
                break;
            case 4:
                getSupportActionBar().setTitle(R.string.birthday_reminders_label);
                break;
            case 5:
                getSupportActionBar().setTitle(R.string.anniversary_reminders_label);
                break;
            default:
                getSupportActionBar().setTitle(R.string.falcon_view_label);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_falcon, container, false);

            RecyclerView todayList = (RecyclerView) rootView.findViewById(R.id.section_day_list);
            RecyclerView weekList = (RecyclerView) rootView.findViewById(R.id.section_week_list);
            RecyclerView upcomingList = (RecyclerView) rootView.findViewById(R.id.section_upcoming_list);

            TextView todayText=(TextView) rootView.findViewById(R.id.section_day);
            TextView weekText=(TextView) rootView.findViewById(R.id.section_week);
            TextView upcomingText=(TextView) rootView.findViewById(R.id.section_upcoming);

            todayList.setNestedScrollingEnabled(false);
            weekList.setNestedScrollingEnabled(false);
            weekList.setNestedScrollingEnabled(false);

            _sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            FalconListItemRecyclerViewAdapter remindersTodayAdapter;
            FalconListItemRecyclerViewAdapter remindersWeekAdapter;
            FalconListItemRecyclerViewAdapter remindersUpcomingAdapter;
            switch (_sectionNumber){
                case 2:
                    todayText.setText(this.getResources().getString(R.string.today_label)+" ("+meetMessageList.size()+")");
                    weekText.setText(this.getResources().getString(R.string.next_7_label)+" ("+meetMessageListWeek.size()+")");
                    upcomingText.setText(this.getResources().getString(R.string.upcoming_label)+" ("+meetMessageListUpcoming.size()+")");

                    remindersTodayAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), meetMessageList);
                    remindersWeekAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), meetMessageListWeek);
                    remindersUpcomingAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), meetMessageListUpcoming);
                    break;
                case 3:
                    todayText.setText(this.getResources().getString(R.string.today_label)+" ("+todoMessageList.size()+")");
                    weekText.setText(this.getResources().getString(R.string.next_7_label)+" ("+todoMessageListWeek.size()+")");
                    upcomingText.setText(this.getResources().getString(R.string.upcoming_label)+" ("+todoMessageListUpcoming.size()+")");

                    remindersTodayAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), todoMessageList);
                    remindersWeekAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), todoMessageListWeek);
                    remindersUpcomingAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), todoMessageListUpcoming);
                    break;
                case 4:
                    todayText.setText(this.getResources().getString(R.string.today_label)+" ("+billPayMessageList.size()+")");
                    weekText.setText(this.getResources().getString(R.string.next_7_label)+" ("+billPayMessageListWeek.size()+")");
                    upcomingText.setText(this.getResources().getString(R.string.upcoming_label)+" ("+billPayMessageListUpcoming.size()+")");

                    remindersTodayAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), billPayMessageList);
                    remindersWeekAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), billPayMessageListWeek);
                    remindersUpcomingAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), billPayMessageListUpcoming);
                    break;
                case 5:
                    todayText.setText(this.getResources().getString(R.string.today_label)+" ("+birthMessageList.size()+")");
                    weekText.setText(this.getResources().getString(R.string.next_7_label)+" ("+birthMessageListWeek.size()+")");
                    upcomingText.setText(this.getResources().getString(R.string.upcoming_label)+" ("+birthMessageListUpcoming.size()+")");

                    remindersTodayAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), birthMessageList);
                    remindersWeekAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), birthMessageListWeek);
                    remindersUpcomingAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), birthMessageListUpcoming);
                    break;
                case 6:
                    todayText.setText(this.getResources().getString(R.string.today_label)+" ("+annMessageList.size()+")");
                    weekText.setText(this.getResources().getString(R.string.next_7_label)+" ("+annMessageListWeek.size()+")");
                    upcomingText.setText(this.getResources().getString(R.string.upcoming_label)+" ("+annMessageListUpcoming.size()+")");

                    remindersTodayAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), annMessageList);
                    remindersWeekAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), annMessageListWeek);
                    remindersUpcomingAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), annMessageListUpcoming);
                    break;
                default:

                    todayText.setText(this.getResources().getString(R.string.today_label)+" ("+messageList.size()+")");
                    weekText.setText(this.getResources().getString(R.string.next_7_label)+" ("+messageListWeek.size()+")");
                    upcomingText.setText(this.getResources().getString(R.string.upcoming_label)+" ("+messageListUpcoming.size()+")");

                    remindersTodayAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), messageList);
                    remindersWeekAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), messageListWeek);
                    remindersUpcomingAdapter = new FalconListItemRecyclerViewAdapter(getActivity(), messageListUpcoming);
                    break;
            }

            todayList.setAdapter(remindersTodayAdapter);
            weekList.setAdapter(remindersWeekAdapter);
            upcomingList.setAdapter(remindersUpcomingAdapter);

            todayList.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            weekList.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            upcomingList.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return FalconActivity.this.getResources().getString(R.string.all_label);
                case 1:
                    return FalconActivity.this.getResources().getString(R.string.meeting_label);
                case 2:
                    return FalconActivity.this.getResources().getString(R.string.todo_label);
                case 3:
                    return FalconActivity.this.getResources().getString(R.string.bill_pay_label);
                case  4:
                    return FalconActivity.this.getResources().getString(R.string.birthday_label);
                case 5:
                    return FalconActivity.this.getResources().getString(R.string.anniversary_label);
            }
            return null;
        }
    }
}
