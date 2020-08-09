package com.example.cardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinGame extends AppCompatActivity {

    private ChildEventListener roomListener;
    private FirebaseDatabase database;
    private FirebaseAuth mFirebaseauth;
    // de ref naar de gameroom waar we nu in zitten
    private DatabaseReference gameroomRefJoinGame;
    // de gameRoom van de game waar we nu in zitten
    private GameRoom gameroomLocalJoinGame;
    private boolean playerAdded = false;
    private boolean roomExist;

    // to send extra message
    public static final String EXTRA_MESSAGE = "com.example.cardgame.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        //database INIT
        database = FirebaseDatabase.getInstance();
    }

    public void joinGameLobby(View view){
        EditText editText = (EditText) findViewById(R.id.editTextLobbyName);
        String roomname = editText.getText().toString();
        //gameroomRef = database.getReference().child("GameRooms").child(message);
        DatabaseReference roomRef = database.getReference().child("GameRooms").child(roomname);
        this.gameroomRefJoinGame = roomRef;

        // we attach a valueListener to the gameRoom we write in the edit text
        doesRoomExist(this.gameroomRefJoinGame, roomname);


        //System.out.println(gameroomLocal.roomName);
    }


    /**
     * Attaches a listener to the room that is located on the given roomRef
     * ASSYNCHRONE METHODE DUS WE MOETEN WACHTEN OP onDataChange klaar is anders krijgen we null
     * refference als we al eerder de localGame proberen te gebruiken die pas in changeRoom geset
     * wordt
     */

    public void doesRoomExist(DatabaseReference roomRef, final String roomname){
        ValueEventListener roomValueListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                System.out.println("been heere");
                GameRoom room = dataSnapshot.getValue(GameRoom.class);
                // The room exists and we can go to the GameScreenJoined activity
                if(setRoomExist(room)){
                    TextView lobbyFoundText = (TextView) findViewById(R.id.lobbyFoundText);
                    launchGameScreenJoined(roomname);

                }
                else{
                    TextView lobbyFoundText = (TextView) findViewById(R.id.lobbyFoundText);
                    lobbyFoundText.setText("Lobby Not Found");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        roomRef.addValueEventListener(roomValueListener1);

    }

    public boolean setRoomExist(GameRoom room){
        if(room == null) {
            this.roomExist = false;
            return false;
        }
        else {
            this.roomExist = true;
            return true;
        }

    }


    public void goBack(View view){

        // we remove the user from the online users database
        //FirebaseUser user = mFirebaseauth.getCurrentUser();
        //DatabaseReference userRef = database.getReference().child("OnlineUsers").child(user.getUid());
        //userRef.removeValue();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public void launchGameScreenJoined(String roomName){
        //Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this,GameScreenJoined.class);
        intent.putExtra(EXTRA_MESSAGE, roomName);
        startActivity(intent);
    }

}