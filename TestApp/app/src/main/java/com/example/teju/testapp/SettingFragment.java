package com.example.teju.testapp;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    TestAppSharedPreferences preferences;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_setting, container, false);

        final TestAppSharedPreferences preferences = TestAppSharedPreferences.getInstance(getContext());

        final CheckBox cbReadMsgs =(CheckBox)view.findViewById(R.id.cbReadMsgs);



        cbReadMsgs.setChecked(preferences.getMessageStorageEnabled());



        cbReadMsgs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                   Log.d("checkbox","checkBoxClicked");
                }else{
                    Log.d("<><><>","CheckBox Unclicked");
                }

                preferences.setMessageStorageEnabled(isChecked);

            }
        });

        return view;
    }

}
