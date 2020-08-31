package com.example.cardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateLobby extends AppCompatActivity {


    private FirebaseDatabase database;
    private static GameRoom gameroomLocal;
    private HashMap roomNames;
    // needs to be turned on and off so we dont keep creating rooms
    private boolean hostingRoomActive = false;

    // to send extra message
    public static final String EXTRA_MESSAGE = "com.example.cardgame.MESSAGE";
    public static final String PASSWORD = "com.example.cardgame.PASSWORD";

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);

        // DATABASE INIT
        database = FirebaseDatabase.getInstance();
        // Init banner adds
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setTitle("Host lobby");

    }

    /**
     * Creates the lobby and afterwards joins it (if lobby name not already taken etc
     */

    public void createLobby(View view){
        // find the chosen lobbyname
        EditText editText = (EditText) findViewById(R.id.editTextLobbyName);
        // chosen name for this room
        String roomName = editText.getText().toString();

        //Generate roomName by looking in folder containing all group names

        //DatabaseReference totalRoomsRef = database.getReference().child("GameRooms").child("allRoomsSet");
        DatabaseReference totalRoomsRef = database.getReference().child("GameRooms");
        addLobby(totalRoomsRef,roomName);
    }

    /**
     * Reader to read the set of all the rooms currently used and also create the room
     */

    public void addLobby(final DatabaseReference totalRoomsRef, final String roomname){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!hostingRoomActive) {
                    // Get Post object and use the values to update the UI

                    HashMap roomNames = (HashMap) dataSnapshot.getValue();
                    if (roomNames == null) {
                        roomNames = new HashMap();
                        ///roomNames.add("test");
                        //totalRoomsRef.setValue(roomNames);
                        addRoomnamesSet(roomNames);
                    } else {
                        addRoomnamesSet(roomNames);
                    }

                    Boolean isUniqueRoomName = checkOfRoomnameIsUnique(roomname, totalRoomsRef);
                    if(isUniqueRoomName){
                        hostingRoomActive = true;
                        // launch other activity
                        launchGameScreenHost(roomname);
                    }
                    else{
                        // find the chosen password
                        TextView lobbyFreeText = (TextView) findViewById(R.id.lobbyfree);
                        // chosen name for this room
                        String lobbyalreadytaken = "lobbyname already taken";
                        lobbyFreeText.setText(lobbyalreadytaken);
                        lobbyFreeText.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
            }
        };
        totalRoomsRef.addValueEventListener(postListener);
    }

    public void changeRoom(GameRoom room){
        gameroomLocal = room;
        System.out.println(gameroomLocal.playerIDs.get(0));
    }

    public void addRoomnamesSet(HashMap roomNames){
        this.roomNames = roomNames;
    }

    /**
     * Checks if the given roomName is uniaue. If it is unique (no other room with same name)
     * Then it is added to the list with all the roomnames. And true is returned
     */

    public Boolean checkOfRoomnameIsUnique(String roomName, DatabaseReference roomsSetRef){
        if( this.roomNames.containsKey(roomName)){
            return false;
        }
        else{
            //this.roomNames.add(roomName);
            //roomsSetRef.setValue(this.roomNames);
            return true;
        }
    }

    /**
     * We pass the roomname and the chosen password as an intent extra to the launchgame
     */

    public void launchGameScreenHost(String roomName){
        Intent intent = new Intent(this, GameScreenHost.class);
        intent.putExtra(EXTRA_MESSAGE, roomName);

        // find the chosen password
        EditText editText = (EditText) findViewById(R.id.editTextPassword);
        // chosen name for this room
        String password = editText.getText().toString();
        intent.putExtra(PASSWORD,password);


        startActivity(intent);
    }

    public void goBack(View view){

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}