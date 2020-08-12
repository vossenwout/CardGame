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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //default init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DATABASE INIT
        database = FirebaseDatabase.getInstance();

        //AUTHENTICATION
        mFirebaseauth = FirebaseAuth.getInstance();
        // dit zorgt voor email
        System.out.println("welcome");
        if (mFirebaseauth.getCurrentUser() != null){
            //user already signed in
            //FirebaseUser user = mFirebaseauth.getCurrentUser();
            //onSignedInInitialize(user);
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
     * Wordt opgroepen als gebruiker ingelogd is
     */

    public void onSignedInInitialize(FirebaseUser user){
        // in OnlineUsers directory maken we een key-value paar met als key de userId
        // de laatste child is de key waarvoor we met setValue een value zetten
        DatabaseReference userRef = database.getReference().child("OnlineUsers").child(user.getUid());
        userRef.setValue(user.getEmail());
        //userRef.push().setValue("hello");
    }





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
     * Attaches a listener to the room that is located on the given roomRef
     */

    public void attachGameRoomValueListener(DatabaseReference roomRef){
        ValueEventListener roomValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                //System.out.println("been heere");
                GameRoom room = dataSnapshot.getValue(GameRoom.class);
                changeRoom(room);
                //gameroomLocal = room;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        roomRef.addValueEventListener(roomValueListener);
    }

    /**
     * Reader to read the set of all the rooms currently used
     */

    public void totalRoomsReader(final DatabaseReference totalRoomsRef, final String roomname){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!hostingRoomActive) {
                    // Get Post object and use the values to update the UI
                    ArrayList<String> roomNames = (ArrayList<String>) dataSnapshot.getValue();
                    if (roomNames == null) {
                        roomNames = new ArrayList<String>();
                        roomNames.add("test");
                        totalRoomsRef.setValue(roomNames);
                        addRoomnamesSet(roomNames);
                    } else {
                        addRoomnamesSet(roomNames);
                    }
                    addUniqueRoomNameToRoomsSet(roomname, totalRoomsRef);
                    hostingRoomActive = true;
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

    /**
     * Listens to all the childs of a given reference
     * @param roomRef
     */

    public void attachGameRoomChildListener(DatabaseReference roomRef){
        //myRef = database.getReference().child("message");
        // we voegen een pushID toe om elke key uniek te maken en zetten de waarde
        // van die string toe naar heeloo
        // we voegen een even listener toe
        roomListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // snapshot geeft de data in de ref. Met getValue krijgen we deze data
                // We geven de klasse van de data als paramater mee en printen hem daarna uit voor confirmatie
                GameRoom room = snapshot.getValue(GameRoom.class);
                gameroomLocal = room;
                System.out.println(previousChildName);
                System.out.println(room.playerIDs.get(0));
                room.playerIDs.add("player5");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        // refference bepaald naar welke directory we luisteren in de database
        // listener bepaald wat er met de data gebeurt
        roomRef.addChildEventListener(roomListener);
    }

    public void addUniqueRoomNameToRoomsSet(String roomName, DatabaseReference roomsSetRef){
        if( this.roomNames.contains(roomName)){
            this.roomNames.add(roomName + "1");
            roomsSetRef.setValue(this.roomNames);
        }
        else{
            this.roomNames.add(roomName);
            roomsSetRef.setValue(this.roomNames);
        }
    }


    public void addUserToGameRoom(String user, DatabaseReference ref){
        gameroomLocal.playerIDs.add("pookie");
        ref.setValue(gameroomLocal);
    }

    public void changeRoom(GameRoom room){
        gameroomLocal = room;
        System.out.println(gameroomLocal.playerIDs.get(0));
    }

    public void addRoomnamesSet(ArrayList<String> roomNames){
        this.roomNames = roomNames;
    }

    public void addPlayer(View view){
        System.out.println(gameroomLocal.playerIDs.get(0));
        gameroomLocal.playerIDs.add("pookie");
        gameroomRef.setValue(gameroomLocal);

    }

    public void joinGame(View view){
        Intent intent = new Intent(this, JoinGame.class);
        startActivity(intent);
    }

    public void hostGame(View view){
        Intent intent = new Intent(this, CreateLobby.class);
        startActivity(intent);
    }

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