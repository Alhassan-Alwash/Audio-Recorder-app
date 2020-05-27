package com.example.voicerecorder;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {

private NavController navController;
private ImageButton list_btn;
private ImageButton recordbtn;
private boolean isrecording = false;
private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE =21;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private Chronometer timer;
    private TextView filenameText;

    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        list_btn = view.findViewById(R.id.list_btn);
        recordbtn = view.findViewById(R.id.record_btn);
        timer =view.findViewById(R.id.record_timer);
        filenameText = view.findViewById(R.id.record_file_name);
        list_btn.setOnClickListener(this);
        recordbtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.list_btn:

                if (isrecording){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                            isrecording = false;
                        }
                    });
                    alertDialog.setNegativeButton("Cancel",null);
                    alertDialog.setTitle("Audio Still Recording");
                    alertDialog.setMessage("Do You Want To Stop The Recording ?");
                    alertDialog.create().show();
                }else {
                    navController.navigate(R.id.action_recordFragment_to_audioListFragment);

                }

                break;

            case R.id.record_btn:
                if(isrecording) {
                    stopRecording();
                    recordbtn.setImageDrawable(getResources().getDrawable(R.drawable.record_stopped));
                    isrecording = false;
                }else {
                    if(checkPermissions()) {
                        startRecording();
                        recordbtn.setImageDrawable(getResources().getDrawable(R.drawable.recording));
                        isrecording = true;
                    }
                }
                break;
        }
    }

    private void stopRecording() {
        timer.stop();
        filenameText.setText("Finished & File Saved\n"+ recordFile);
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder=null;
    }

    private void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        String recordPath= getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy_MM_dd , hh_mm_ss", Locale.CANADA);
        Date now =new Date();
        recordFile = "Voice #"+ formatter.format(now) +".3gp";
        filenameText.setText("Recording...\n"+ recordFile);
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath +"/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();;
    }

    private boolean checkPermissions() {
        if(ActivityCompat.checkSelfPermission(getContext(), recordPermission)== PackageManager.PERMISSION_GRANTED) {
        return true;
        }else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission},PERMISSION_CODE);
        return false;
        }
        }

    @Override
    public void onStop() {
        super.onStop();
        if(isrecording)
        stopRecording();
    }
}

