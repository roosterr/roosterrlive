package com.roosterr.adapters;

import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.roosterr.Checklists;
import com.roosterr.R;
import com.roosterr.dummy.CheckList;
import java.util.ArrayList;

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder> {
    ArrayList<Checklists> mItems;

    public CheckListAdapter(ArrayList<Checklists> mItems) {
        this.mItems = mItems;
    }

    public String getCheckedStates() {
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < mItems.size(); i++) {

            if (mItems.get(i).checked) {
                result.append("- ");
                result.append(mItems.get(i).name);
                result.append("\n");
            }
        }

        return result.toString();
    }

    @Override
    public CheckListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_checklist, parent, false);
        CheckListAdapter.ViewHolder vh = new CheckListAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(CheckListAdapter.ViewHolder holder, int position) {
        holder.mText.setText(mItems.get(position).name);
        holder.mEditText.setText(mItems.get(position).name);
        holder.mCheckBox.setChecked(mItems.get(position).checked);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public ArrayList<Checklists> getItems() {
        return mItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mText;
        CheckBox mCheckBox;

        EditText mEditText;
        Button mSaveButton;

        ViewSwitcher mViewSwitcher;
        RelativeLayout mNormalView;
        RelativeLayout mEditView;

        public ViewHolder(View itemView) {
            super(itemView);
            mViewSwitcher = (ViewSwitcher) itemView.findViewById(R.id.item_view_switcher);
            mNormalView = (RelativeLayout) itemView.findViewById(R.id.normal_view);
            mEditView = (RelativeLayout) itemView.findViewById(R.id.edit_view);

            mText = (TextView) itemView.findViewById(R.id.text1);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            mEditText = (EditText) itemView.findViewById(R.id.edit_text1);
            mSaveButton = (Button) itemView.findViewById(R.id.save_button);

            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getLayoutPosition();
                    boolean currentState = mItems.get(pos).checked;
                    mItems.get(pos).checked = !currentState;
                    notifyItemChanged(pos);
                }
            });

            mText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mViewSwitcher.getCurrentView() != mNormalView){
                        mViewSwitcher.showPrevious();
                    } else if (mViewSwitcher.getCurrentView() != mEditView){
                        mViewSwitcher.showNext();
                    }

                    mEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);
                }
            });

            mSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String result = mEditText.getText().toString();
                    mItems.get(getLayoutPosition()).name = result;
                    String item_id = mItems.get(getLayoutPosition()).id;
                    String checklist_id = mItems.get(getLayoutPosition()).checklist_id;
                    // mItems.set(getLayoutPosition(), result);
                    mText.setText(result);

                    InputMethodManager mgr = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                    Checklists.saveChecklistItem(view.getContext(), item_id, result,checklist_id);
                    if (mViewSwitcher.getCurrentView() != mNormalView){
                        mViewSwitcher.showPrevious();
                    } else if (mViewSwitcher.getCurrentView() != mEditView){
                        mViewSwitcher.showNext();
                    }
                }
            });
        }
    }
}
