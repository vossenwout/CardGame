package com.example.cardgame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This activity is called on startUp
 */

public class MainActivity extends AppCompatActivity  {

    private static final int RC_SIGN_IN = 1;
    private DatabaseReference myRef;
    private ChildEventListener roomListener;
    private FirebaseDatabase database;
    private FirebaseAuth mFirebaseauth;
    private DatabaseReference gameroomRef;
    private static GameRoom gameroomLocal;
    private ArrayList<String> roomNames;
    // needs to be turned on and off so we dont keep creating rooms
    private boolean hostingRoomActive = false;
    // add
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //default init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                initInterAdd();
            }
        });

        setTitle("Deck of Cards");

        // DATABASE INIT
        database = FirebaseDatabase.getInstance();

        //AUTHENTICATION
        mFirebaseauth = FirebaseAuth.getInstance();
        // dit zorgt voor email
        System.out.println("welcome");
        if (mFirebaseauth.getCurrentUser() != null){
        }
        else{
            //user signed out
            System.out.println("signing in");
            signIn();

        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        EditText txtView = (EditText) findViewById(R.id.userNameEditText);
        if(user != null)
            txtView.setText(user.getDisplayName());


    }

    /**
     * We initialize the interstitial adds
     */

    public void initInterAdd(){
        this.mInterstitialAd = new InterstitialAd(this);
        this.mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        this.mInterstitialAd.loadAd(new AdRequest.Builder().build());
        this.mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                System.out.println("add failed to load");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
            }
        });
    }


    /**
     * We sign out of firebase
     * @param view
     */

    public void onSignedOutCleanUp(View view){

        // we sign the user out
        AuthUI.getInstance().signOut(this);
        signIn();
    }

    /**
     * Method used for signin in
     */

    public void signIn(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build()))
                        .build(),
                RC_SIGN_IN);

    }

    /**
     * Method to start a solo game
     */

    public void soloGame(View view){
        Intent intent = new Intent(this, GameScreenActivity.class);
        startActivity(intent);
    }

    /**
     * Method to join online game
     */

    public void joinGame(View view){
        Intent intent = new Intent(this, JoinGame.class);
        startActivity(intent);
    }

    /**
     * Method to host online game
     */

    public void hostGame(View view){
        Intent intent = new Intent(this, CreateLobby.class);
        startActivity(intent);
    }

    /**
     * Method for updating the displayname
     */

    public void updateUsername(View view){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final EditText txtView = (EditText) findViewById(R.id.userNameEditText);

        final String newUsername = txtView.getText().toString();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Username Updated", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}