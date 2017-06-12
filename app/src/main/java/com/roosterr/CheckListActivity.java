package com.roosterr;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roosterr.dummy.CheckList;
import com.roosterr.ui.NonSwipeableViewPager;

import java.util.ArrayList;
import java.util.List;

public class CheckListActivity extends AppCompatActivity {


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
    private NonSwipeableViewPager mViewPager;
    private static ArrayList<Checklists> mTabTitles;
    private static ArrayList<Checklists> mItems;
    public TextView nameText;

    public NavigationView tabs;
    public int tabsGroupID = 123;
    static Context appContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        appContext=CheckListActivity.this;
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);


        TextView title = (TextView) findViewById(R.id.centered_title);
        title.setText(R.string.title_activity_check_list);

        mItems = new ArrayList<>();
        mTabTitles = new ArrayList<>();

        final ImageButton editNameButton = (ImageButton)  findViewById(R.id.button_edit_name);
        final Button saveEditNameButton = (Button)  findViewById(R.id.button_save_name);
        nameText = (TextView)  findViewById(R.id.name_text);
        final EditText editNameText = (EditText)  findViewById(R.id.name_edit_text);

        List<Checklists> checklists=Checklists.getChecklists(CheckListActivity.this);
        for(Checklists checklist: checklists){
            mTabTitles.add(checklist);
        }
        if(mTabTitles.size()<=0){
            for (int i = 1; i <= 5; i++) {
                String name = CheckListActivity.this.getResources().getString(R.string.item_label)+" "+ String.valueOf(i);
                Checklists item = new Checklists(String.valueOf(i),name,false);

                mTabTitles.add(item);
            }
        }
        /*
        for (int i = 1; i <= 5; i++) {
            String name = "Item " + String.valueOf(i);
            Checklists item = new Checklists(name, false);

            mItems.add(item);
        }
        */
        // Create the mAdapter that will return a fragment for each of the checklists.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections mAdapter.
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.container);
        mViewPager.setPagingEnabled(false);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabs = (NavigationView) findViewById(R.id.tabs2);
        Menu tabsMenu = tabs.getMenu();

        for(int i = 0; i < mTabTitles.size(); i++) {
            tabsMenu.add(tabsGroupID, i, i, mTabTitles.get(i).name.substring(0,1).toUpperCase());
        }

        tabsMenu.setGroupCheckable(tabsGroupID, true, true);
        tabsMenu.getItem(0).setChecked(true);

        tabs.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switchTab(item);
                return true;
            }
        });

        editNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNameText.setText(nameText.getText().toString());

                nameText.setVisibility(View.INVISIBLE);
                editNameButton.setVisibility(View.INVISIBLE);

                saveEditNameButton.setVisibility(View.VISIBLE);
                editNameText.setVisibility(View.VISIBLE);

                editNameText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editNameText, InputMethodManager.SHOW_IMPLICIT);

            }
        });

        saveEditNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editNameText.getText().toString();
                nameText.setText(name);

                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(editNameText.getWindowToken(), 0);

                int currentPos = mViewPager.getCurrentItem();
                Checklists chklsts =mTabTitles.get(currentPos);
                chklsts.name=name;
                chklsts.id=Checklists.updateChecklistName(CheckListActivity.this,chklsts.id, editNameText.getText().toString());
                mTabTitles.set(currentPos, chklsts);

                tabs.getMenu().getItem(currentPos).setTitle(name.substring(0,1).toUpperCase());

                saveEditNameButton.setVisibility(View.INVISIBLE);
                editNameText.setVisibility(View.INVISIBLE);

                nameText.setVisibility(View.VISIBLE);
                editNameButton.setVisibility(View.VISIBLE);

                CheckListFragment newFragment = (CheckListFragment) mSectionsPagerAdapter.getItem(currentPos);
                mSectionsPagerAdapter.notifyDataSetChanged();
                switchTab(tabs.getMenu().getItem(currentPos));
            }
        });

        nameText.setText(mTabTitles.get(0).name);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                changeTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ImageButton newListButton = (ImageButton) findViewById(R.id.button_add_list);
        newListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTab();
            }
        });


    }

    private void switchTab(MenuItem item) {
        item.setChecked(true);
        mViewPager.setCurrentItem(item.getItemId());
        changeTitle(item.getItemId());
    }

    private void addNewTab() {
        int index = mTabTitles.size();
        Checklists chklsts = new Checklists("-1",this.getResources().getString(R.string.checklist_default_name));
        mTabTitles.add(index, chklsts);
        tabs.getMenu().add(tabsGroupID, index, index, mTabTitles.get(index).name.substring(0,1).toUpperCase());
        tabs.getMenu().setGroupCheckable(tabsGroupID, true, true);

        CheckListFragment newFragment = (CheckListFragment) mSectionsPagerAdapter.getItem(index);
        mSectionsPagerAdapter.notifyDataSetChanged();

        switchTab(tabs.getMenu().getItem(index));
    }

    public void changeTitle(int pos) {
        nameText.setText(mTabTitles.get(pos).name);
    }

    public static ArrayList<Checklists> getItems() {
        return mItems;
    }
    public static ArrayList<Checklists> getItems(Integer pos) {
        String checklist_id = mTabTitles.get(pos).id.toString();
        mItems=Checklists.getChecklistItems(appContext, checklist_id);
        if(mItems.size()<=0){
            for (int i = 1; i <= 5; i++) {
                String name = appContext.getResources().getString(R.string.item_label)+" "+ String.valueOf(i);
                Checklists item = new Checklists(String.valueOf(i),name,String.valueOf(i));

                mItems.add(item);
            }
        }
        return mItems;
    }
    public static String saveChecklistItem(Context context,String checklist_name,int pos){
        String checklist_id =mTabTitles.get(pos).id;
        Checklists.saveChecklistItem(context, null, checklist_name, checklist_id);
        return checklist_id;
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a com.roosterr.CheckListFragment (defined as a static inner class below).
            // TODO adapt to data structure
            Checklists chklsts = mTabTitles.get(position);
            if (chklsts!=null && Integer.parseInt(chklsts.id)> 0) {
                return CheckListFragment.newInstance(position + 1, false,position);
            } else {
                return CheckListFragment.newInstance(position + 1, true,position);
            }
        }

        @Override
        public int getCount() {
            return mTabTitles.size();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles.get(position).name;
        }
    }
}
