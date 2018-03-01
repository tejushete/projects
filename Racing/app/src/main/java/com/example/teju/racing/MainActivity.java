package com.example.teju.racing;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.teju.racing.fragment.HomeFragment;
import com.example.teju.racing.fragment.ProfileFragment;
import com.example.teju.racing.persistence.RacingSharedPreferences;

public class MainActivity extends AppCompatActivity implements RacingEventListener{

    RacingSharedPreferences shared_pref;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET},
                0);


        shared_pref = RacingSharedPreferences.getInstance(MainActivity.this);

        LinearLayout llHome = (LinearLayout)findViewById(R.id.llHome);
        LinearLayout llProfile = (LinearLayout)findViewById(R.id.llProfile);

        llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();

                Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.fmMainView);
                FragmentTransaction transaction = fm.beginTransaction();
                if(oldFragment!=null){
                    transaction.remove(oldFragment);
                }
                HomeFragment homeFm = new HomeFragment();
                transaction.replace(R.id.fmMainView,homeFm);
                transaction.commit();

            }
        });

        llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shared_pref.getIsLoggedIn() == true) {
                    FragmentManager fm = getSupportFragmentManager();
                    Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.fmMainView);
                    FragmentTransaction transaction = fm.beginTransaction();
                    if (oldFragment != null) {
                        transaction.remove(oldFragment);
                    }
                    ProfileFragment profileFm = new ProfileFragment();
                    transaction.replace(R.id.fmMainView, profileFm);
                    transaction.commit();
                }else {
                    Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void logInStatusChanged(boolean newLoggedInValue) {

    }
}
