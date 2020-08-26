package com.example.cardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private String displayName;

    // to send extra message
    public static final String EXTRA_MESSAGE = "com.example.cardgame.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        //database INIT
        database = FirebaseDatabase.getInstance();

        // User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.displayName = user.getDisplayName();
    }

    public void joinGameLobby(View view){
        EditText editText = (EditText) findViewById(R.id.editTextLobbyName);
        String roomname = editText.getText().toString();

        EditText passwordtext = (EditText) findViewById(R.id.passwordText);
        String password = passwordtext.getText().toString();

        DatabaseReference roomRef = database.getReference().child("GameRooms").child(roomname);
        this.gameroomRefJoinGame = roomRef;

        // we attach a valueListener to the gameRoom we write in the edit text
        doesRoomExist(this.gameroomRefJoinGame, roomname,password);


        //System.out.println(gameroomLocal.roomName);
    }


    /**
     * Attaches a listener to the room that is located on the given roomRef
     * ASSYNCHRONE METHODE DUS WE MOETEN WACHTEN OP onDataChange klaar is anders krijgen we null
     * refference als we al eerder de localGame proberen te gebruiken die pas in changeRoom geset
     * wordt
     */

    public void doesRoomExist(DatabaseReference roomRef, final String roomname, final String password){
        ValueEventListener roomValueListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                GameRoom room = dataSnapshot.getValue(GameRoom.class);
                // The room exists and we can go to the GameScreenJoined activity
                if(setRoomExist(room)){
                    if(isPassWordCorrect(room,password)) {
                        if(!lobbyAlreadyHasSamePlayerName(room)){
                            launchGameScreenJoined(roomname);}
                        else{
                            TextView lobbyFoundText = (TextView) findViewById(R.id.lobbyFoundText);
                            lobbyFoundText.setVisibility(View.INVISIBLE);
                            TextView passwordWrongText = (TextView) findViewById(R.id.passwordwrong);
                            passwordWrongText.setVisibility(View.INVISIBLE);
                            TextView usernamenotunique = (TextView) findViewById(R.id.LobbyAlreadyContainsSameUser);
                            usernamenotunique.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                        TextView lobbyFoundText = (TextView) findViewById(R.id.lobbyFoundText);
                        lobbyFoundText.setVisibility(View.INVISIBLE);
                        TextView passwordWrongText = (TextView) findViewById(R.id.passwordwrong);
                        passwordWrongText.setVisibility(View.VISIBLE);
                        TextView usernamenotunique = (TextView) findViewById(R.id.LobbyAlreadyContainsSameUser);
                        usernamenotunique.setVisibility(View.INVISIBLE);
                    }
                }
                else{
                    TextView lobbyFoundText = (TextView) findViewById(R.id.lobbyFoundText);
                    lobbyFoundText.setVisibility(View.VISIBLE);
                    TextView passwordWrongText = (TextView) findViewById(R.id.passwordwrong);
                    passwordWrongText.setVisibility(View.INVISIBLE);
                    TextView usernamenotunique = (TextView) findViewById(R.id.LobbyAlreadyContainsSameUser);
                    usernamenotunique.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        roomRef.addListenerForSingleValueEvent(roomValueListener1);

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

    public boolean isPassWordCorrect(GameRoom room, String password){
        if(room.password.equals(password)){
            return true;
        }
        else{
            return false;
        }
    }


    public void launchGameScreenJoined(String roomName){
        //Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this,GameScreenJoined.class);
        intent.putExtra(EXTRA_MESSAGE, roomName);
        startActivity(intent);
    }

    public boolean lobbyAlreadyHasSamePlayerName(GameRoom room){
        if(room.playerIDs.contains(this.displayName))
            return true;
        else{
            return false;
        }
    }

    public void goBack(View view){

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}