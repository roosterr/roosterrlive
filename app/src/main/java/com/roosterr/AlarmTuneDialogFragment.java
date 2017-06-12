package com.roosterr;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageButton;

public class AlarmTuneDialogFragment extends DialogFragment {

    String[] audioNamesList = new String[4];
    int[] audioFilesList = new int[] {R.raw.alarm_clock,R.raw.alarm_clock, R.raw.gong, R.raw.tibetan_tingsha};
    MediaPlayer mp = null;
    int selectedTune=0;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mp = new MediaPlayer();
        audioNamesList[0]= getActivity().getResources().getString(R.string.vibrate_label);
        audioNamesList[1]= getActivity().getResources().getString(R.string.alarm_clock_label);//,"Gong","Ting Tong"};
        audioNamesList[2]= getActivity().getResources().getString(R.string.gong_label);//,"Gong","Ting Tong"};
        audioNamesList[3]= getActivity().getResources().getString(R.string.tingsha_label);//,"Gong","Ting Tong"};
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RecyclerView list = (RecyclerView) inflater.inflate(R.layout.dialog_alarm, null);
        AudioRecyclerViewAdapter adapter = new AudioRecyclerViewAdapter();
        list.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_alarm_title)
                .setView(list)
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //save it in SharedPreferences
                        SharedPreferences.Editor alarmTune = getActivity().getApplicationContext().getSharedPreferences("Alarm_Tune", 0).edit();
                        alarmTune.putString("alarm", Integer.toString(selectedTune));
                        alarmTune.commit();
                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        SharedPreferences.Editor alarmTune = getActivity().getApplicationContext().getSharedPreferences("Alarm_Tune", 0).edit();
                        alarmTune.putString("alarm","-1");
                        alarmTune.commit();
                    }
                });

        return builder.create();
    }

    private class AudioRecyclerViewAdapter extends RecyclerView.Adapter<AudioRecyclerViewAdapter.ViewHolder> {

        private int selectedPos = 0;
        boolean[] buttonState = new boolean[] {false, false, false,false};
        private Handler myHandler = new Handler();


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_alarm, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AudioRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.checkedText.setChecked(selectedPos == position);
            holder.checkedText.setText(audioNamesList[position]);

            if (buttonState[position]) {
                holder.playButton.setVisibility(View.INVISIBLE);
                holder.pauseButton.setVisibility(View.VISIBLE);
            } else {
                holder.playButton.setVisibility(View.VISIBLE);
                holder.pauseButton.setVisibility(View.INVISIBLE);
            }
            if(position==0){
                holder.playButton.setVisibility(View.INVISIBLE);
                holder.pauseButton.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return audioNamesList.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageButton playButton;
            ImageButton pauseButton;
            CheckedTextView checkedText;

            public ViewHolder(View itemView) {
                super(itemView);

                playButton = (ImageButton) itemView.findViewById(R.id.buttonPlay);
                pauseButton = (ImageButton) itemView.findViewById(R.id.buttonPause);
                checkedText = (CheckedTextView) itemView.findViewById(android.R.id.text1);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentPos = selectedPos;
                        selectedPos = getLayoutPosition();
                        selectedTune = selectedPos;
                        notifyItemChanged(currentPos);
                        notifyItemChanged(selectedPos);
                    }
                });

                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getLayoutPosition();

                        for (int i = 0; i < buttonState.length; i++) {
                            if (i == position) {
                                buttonState[i] = true;
                            } else {
                                buttonState[i] = false;
                            }
                            notifyItemChanged(i);
                        }

                        playAlarmTune(position);
                    }
                });

                pauseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getLayoutPosition();
                        buttonState[position] = false;
                        notifyItemChanged(position);

                        stopAlarmTune();
                    }
                });
            }
        }

        private void stopAlarmTune() {
            if (mp != null && mp.isPlaying()) {
                mp.stop();
                mp.release();
                mp = null;
            }
        }

        private void playAlarmTune(final int position) {
            if (mp != null) {
                mp.stop();
                mp.release();
                mp = null;
            }

            mp = MediaPlayer.create(getActivity(), audioFilesList[position]);
            mp.start();

            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    buttonState[position] = false;
                    notifyItemChanged(position);
                }
            }, mp.getDuration());
        }

    }



}
