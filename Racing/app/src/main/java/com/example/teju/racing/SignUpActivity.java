package com.example.teju.racing;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.teju.racing.persistence.RacingSharedPreferences;
import com.example.teju.racing.utility.RacingUtility;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;


public class SignUpActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    RelativeLayout rlSignUpMainView;
    RelativeLayout rlProgressView;
    SignInButton btnGoogleSignUp;

    CallbackManager fbCallbackManager;
    EditText etUserName, etPhoneNumber, etEmailId, etPassword, etConfirmPassword;
    ProfileTracker fbProfileTracker;
    RacingSharedPreferences sharedPref = RacingSharedPreferences.getInstance(null);
    private GoogleApiClient googleApiClient;

    private static final int Rc_Sign_In =007;
    private final int FB_SIGN_IN = 64206;

    private void getFbProfileInfo(final Profile profileNew, AccessToken accessToken, boolean stopTracking) {

        rlSignUpMainView.setAlpha((float) 0.5);
        rlSignUpMainView.setEnabled(false);
        rlProgressView.setVisibility(View.VISIBLE);

        Log.d(SignUpActivity.class.getSimpleName() + ": getFbProfileInfo", "First name:" + profileNew.getFirstName());
        Log.d(SignUpActivity.class.getSimpleName() + ": getFbProfileInfo", "Last name:" + profileNew.getLastName());

        if (stopTracking == true) {
            fbProfileTracker.stopTracking();
        }

        Profile.setCurrentProfile(profileNew);
        final String[] uid = new String[1];
        final String[] url = new String[1];
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    Log.d(SignUpActivity.class.getSimpleName() + ": getFbProfileInfo",
                            "Tejashri FacebookCallback GraphRequest onCompleted object " + object);

                    String email = object.optString("email");
                    uid[0] = object.optString("id");
                    String gender = object.optString("gender");
                    String birthday = object.optString("birthday");

                    url[0] = ((object.getJSONObject("picture")).getJSONObject("data")).optString("url");
                    Log.d(SignUpActivity.class.getSimpleName() + ": getFbProfileInfo",
                            "Tejashri FacebookCallback GraphRequest onCompleted email:: " + email);
                    Log.d(SignUpActivity.class.getSimpleName() + ": getFbProfileInfo",
                            "Tejashri FacebookCallback GraphRequest onCompleted uid:: " + uid[0]);
                    Log.d(SignUpActivity.class.getSimpleName() + ": getFbProfileInfo",
                            "tejashri FacebookCallback GraphRequest onCompleted gender:: " + gender);
                    Log.d(SignUpActivity.class.getSimpleName() + ": getFbProfileInfo",
                            "Tejashri FacebookCallback GraphRequest onCompleted birthday:: " + birthday);

                } catch (Exception e) {
                    e.printStackTrace();
                    rlSignUpMainView.setAlpha(1);
                    rlSignUpMainView.setEnabled(true);
                    rlProgressView.setVisibility(View.GONE);
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String profilePicUrl = url[0].replace("\\", "");

                        final String file_path = RacingUtility.downloadFile(profilePicUrl, uid[0]);
                        Log.d(SignUpActivity.class.getSimpleName() + ": getFbProfileInfo", file_path);
                        //save all the data in shared preferences
                        //even file path should be saved.
                        //is logged in status

                        final CircleImageView iv_photo = findViewById(R.id.iv_photo);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                        Glide.with(SignUpActivity.this)
                                .load(Uri.fromFile(new File(file_path.trim())))
                                .override(100,100)
                                .listener(new RequestListener<Uri, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        e.printStackTrace();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(iv_photo);
                            }
                        });

                        sharedPref.setUserFirstName(profileNew.getFirstName());
                        sharedPref.setUserLastName(profileNew.getLastName());
                        sharedPref.setGender("gender");
                        sharedPref.setEmailId("email");
                        sharedPref.setProfilePath(file_path);
                        //do same thing for google plus login also.
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(SignUpActivity.this, "Sing up was successful", Toast.LENGTH_SHORT).show();
                                etUserName.setText(profileNew.getFirstName() + " " + profileNew.getLastName());
                                etPhoneNumber.requestFocus();
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                                rlSignUpMainView.setAlpha(1);
                                rlSignUpMainView.setEnabled(true);
                                rlProgressView.setVisibility(View.GONE);
                            }
                        });
                    }
                }).start();

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,gender,birthday,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_up);


        rlSignUpMainView = findViewById(R.id.rlSignUpMainView);
        rlProgressView = findViewById(R.id.rlSignUpProgressView);
        etUserName = findViewById(R.id.etUserName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etEmailId = findViewById(R.id.etEmailId);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        Button btnGetStarted = findViewById(R.id.btnGetStarted);
         btnGoogleSignUp=(SignInButton)findViewById(R.id.btnGoogleSignUp);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET},
                0);

        fbCallbackManager = CallbackManager.Factory.create();
        LoginButton btnFbLogin = findViewById(R.id.btnFbLogin);
        btnFbLogin.setReadPermissions(Arrays.asList("public_profile", "email"));
        btnFbLogin.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.d(SignUpActivity.class.getSimpleName(), "fb successfully logged in");
                rlSignUpMainView.setAlpha((float) 0.5);
                rlSignUpMainView.setEnabled(false);
                rlProgressView.setVisibility(View.VISIBLE);

                if (Profile.getCurrentProfile() == null) {
                    fbProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, final Profile profileNew) {
                            getFbProfileInfo(profileNew, loginResult.getAccessToken(), true);
                        }
                    };
                    fbProfileTracker.startTracking();

                } else {
                    Log.e(SignUpActivity.class.getSimpleName(), "FB profile is already present, re-populating the info");
                    getFbProfileInfo(Profile.getCurrentProfile(), AccessToken.getCurrentAccessToken(), false);
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

       btnGoogleSignUp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
               startActivityForResult(signInIntent,Rc_Sign_In);
           }
       });

       GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               .requestEmail()
               .build();

       googleApiClient=new GoogleApiClient.Builder(this)
               .enableAutoManage(this,this)
               .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
               .build();


    }
    private void handleSignInResult(final GoogleSignInResult result){

        if(result.isSuccess()){

            rlSignUpMainView.setEnabled(false);
            rlSignUpMainView.setAlpha((float) 0.5);
            rlProgressView.setVisibility(View.VISIBLE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    GoogleSignInAccount googleSignInAccount = result.getSignInAccount();

                    final String personName = googleSignInAccount.getDisplayName();
                    String firstName = googleSignInAccount.getGivenName();
                    String lastName = googleSignInAccount.getFamilyName();
                    String personPhotoUrl = googleSignInAccount.getPhotoUrl().toString();
                    final String email = googleSignInAccount.getEmail();
                    String accntId = googleSignInAccount.getId();

                    if(personPhotoUrl!=null){
                        sharedPref.setUserFirstName(personName);
                        sharedPref.setUserLastName(lastName);
                        sharedPref.setEmailId(email);

                        final String filePath = RacingUtility.downloadFile(personPhotoUrl,accntId);
                        sharedPref.setProfilePath(filePath);

                        final CircleImageView iv_photo = findViewById(R.id.iv_photo);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Glide.with(SignUpActivity.this)
                                        .load(Uri.fromFile(new File(filePath.trim())))
                                        .override(100,100)
                                        .listener(new RequestListener<Uri, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                e.printStackTrace();
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                return false;
                                            }
                                        })
                                        .into(iv_photo);
                            }
                        });

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etUserName.setText(personName);
                            etPhoneNumber.requestFocus();

                            etEmailId.setText(email);

                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

                            rlSignUpMainView.setAlpha(1);
                            rlSignUpMainView.setEnabled(true);
                            rlProgressView.setVisibility(View.GONE);


                        }
                    });
                }
            }).start();
        }
        else {
            rlSignUpMainView.setAlpha(1);
            rlSignUpMainView.setEnabled(true);
            rlProgressView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==RESULT_OK){
            rlSignUpMainView.setAlpha((float) 0.5);
            rlSignUpMainView.setEnabled(false);
            rlProgressView.setVisibility(View.VISIBLE);

            switch (requestCode){
                case Rc_Sign_In:
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    handleSignInResult(result);
                    break;

                case FB_SIGN_IN:
                    fbCallbackManager.onActivityResult(requestCode,resultCode,data);
            }
        }



    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
