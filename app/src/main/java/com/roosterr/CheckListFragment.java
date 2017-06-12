package com.roosterr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roosterr.adapters.CheckListAdapter;
import com.roosterr.dummy.CheckList;

import java.util.ArrayList;

public class CheckListFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_IS_EMPTY = "is_empty";
    private static final String ARG_CHECKLIST_ID = "checklist_id";
    final String RETURN_ITEMS = "ITEMS";
    
    private static CheckListActivity mActivity;
    private CheckListAdapter mAdapter;
    private static boolean newList;
    private static Integer checklist_id;
    private ArrayList<Checklists> mItems;

    public CheckListFragment() {
        mActivity = (CheckListActivity) getActivity();
    }

    public static CheckListFragment newInstance(int sectionNumber, boolean isNewList,Integer _checklistId) {
        CheckListFragment fragment = new CheckListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putBoolean(ARG_IS_EMPTY, isNewList);
        args.putInt(ARG_CHECKLIST_ID, _checklistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_check_list, container, false);

        RecyclerView checklist = (RecyclerView) rootView.findViewById(R.id.checklist);

        final TextView emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        final ImageButton addToListButton = (ImageButton) rootView.findViewById(R.id.button_add_item);
        Button addListToSMSButton = (Button) rootView.findViewById(R.id.button_add_to_checklist);

        final Button addItemButton = (Button) rootView.findViewById(R.id.add_new_item_button);
        final EditText editNewText = (EditText) rootView.findViewById(R.id.edit_new_text);

        newList = getArguments().getBoolean(ARG_IS_EMPTY);
        checklist_id=getArguments().getInt(ARG_CHECKLIST_ID);

        mItems = new ArrayList<>();

        //if (!newList) {
            mItems = mActivity.getItems(checklist_id);
        if(mItems==null || mItems.size()<=0)
            emptyView.setVisibility(View.VISIBLE);
        //} else {

        //}

        mAdapter = new CheckListAdapter(mItems);

        checklist.setAdapter(mAdapter);

        addToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToListButton.setVisibility(View.INVISIBLE);
                addItemButton.setVisibility(View.VISIBLE);
                editNewText.setVisibility(View.VISIBLE);
                editNewText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editNewText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = editNewText.getText().toString();
                //checklist_id here is the index position of the current tab
                if (result.equals("")) {
                    Toast.makeText(getActivity(), R.string.no_task_label, Toast.LENGTH_SHORT).show();
                } else {
                        String actual_id=CheckListActivity.saveChecklistItem(view.getContext(),result,checklist_id);
                        if(!actual_id.equals("-1")){
                            mAdapter.getItems().add(new Checklists(result, false, actual_id));
                            mAdapter.notifyDataSetChanged();
                            emptyView.setVisibility(View.GONE);
                        }
                }

                editNewText.setText("");

                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(editNewText.getWindowToken(), 0);

                addToListButton.setVisibility(View.VISIBLE);
                addItemButton.setVisibility(View.INVISIBLE);
                editNewText.setVisibility(View.INVISIBLE);
            }
        });

        addListToSMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = mAdapter.getCheckedStates();
                if (result.equals("")) {
                    getActivity().finish();
                } else {
                    Intent i = new Intent();
                    i.putExtra(RETURN_ITEMS, result);
                    getActivity().setResult(Activity.RESULT_OK, i);
                    getActivity().finish();
                }
            }
        });

        return rootView;
    }
}