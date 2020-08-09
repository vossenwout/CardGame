package com.example.cardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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

    private DatabaseReference myRef;
    private ChildEventListener roomListener;
    private FirebaseDatabase database;
    private FirebaseAuth mFirebaseauth;
    private DatabaseReference gameroomRef;
    private static GameRoom gameroomLocal;
    private ArrayList<String> roomNames;
    // needs to be turned on and off so we dont keep creating rooms
    private boolean hostingRoomActive = false;

    // to send extra message
    public static final String EXTRA_MESSAGE = "com.example.cardgame.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);

        // DATABASE INIT
        database = FirebaseDatabase.getInstance();
    }

    /**
     * Creates the lobby and afterwards joins it
     */

    public void createLobby(View view){
        // find the chosen lobbyname
        EditText editText = (EditText) findViewById(R.id.editTextLobbyName);
        // chosen name for this room
        String roomName = editText.getText().toString();

        //Generate roomName by looking in folder containing all group names

        DatabaseReference totalRoomsRef = database.getReference().child("GameRooms").child("allRoomsSet");
        addLobby(totalRoomsRef,roomName);

        /**
        // the name of this gameRoom
        //String roomName = message;
        // "playerName" -> cards in current hand
        HashMap<String, ArrayList<Integer>> playerHands = new HashMap<String, ArrayList<Integer>>();
        // list of current players
        ArrayList<String> playerIDs = new ArrayList<String>();
        playerIDs.add("player1");
        // cards that are played by the players
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        // cards that are still in the deck
        ArrayList<Integer> deck = new ArrayList<Integer>();
        GameRoom gameroom = new GameRoom(roomName,playerHands,playerIDs,playedCards,deck);

        DatabaseReference roomRef = database.getReference().child("GameRooms").child(roomName);
        gameroomRef = roomRef;
        roomRef.setValue(gameroom);
        */
        //attachGameRoomValueListener(roomRef);


        //Intent intent = new Intent(this, GameScreenHost.class);
        //intent.putExtra(EXTRA_MESSAGE, roomName);
        //startActivity(intent);

    }

    /**
     * Fetches the last state of the gameRoom and sets it to gameRoomLocal
     * @param roomRef
     */

    public void attachGameRoomValueListener(DatabaseReference roomRef){
        ValueEventListener roomValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GameRoom room = dataSnapshot.getValue(GameRoom.class);
                changeRoom(room);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        roomRef.addValueEventListener(roomValueListener);
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
                    ArrayList<String> roomNames = (ArrayList<String>) dataSnapshot.getValue();
                    if (roomNames == null) {
                        roomNames = new ArrayList<String>();
                        roomNames.add("test");
                        totalRoomsRef.setValue(roomNames);
                        addRoomnamesSet(roomNames);
                    } else {
                        addRoomnamesSet(roomNames);
                    }
                    String uniqueRoomName = addUniqueRoomNameToRoomsSet(roomname, totalRoomsRef);
                    hostingRoomActive = true;
                    // launch other activity
                    launchGameScreenHost(uniqueRoomName);

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

    public void addRoomnamesSet(ArrayList<String> roomNames){
        this.roomNames = roomNames;
    }

    public String addUniqueRoomNameToRoomsSet(String roomName, DatabaseReference roomsSetRef){
        if( this.roomNames.contains(roomName)){
            this.roomNames.add(roomName + "1");
            roomsSetRef.setValue(this.roomNames);
            return (roomName + "1");
        }
        else{
            this.roomNames.add(roomName);
            roomsSetRef.setValue(this.roomNames);
            return roomName;
        }
    }

    public void launchGameScreenHost(String roomName){
        Intent intent = new Intent(this, GameScreenHost.class);
        intent.putExtra(EXTRA_MESSAGE, roomName);
        startActivity(intent);
    }

}