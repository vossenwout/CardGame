package com.example.cardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameScreenJoined extends AppCompatActivity {

    private ChildEventListener roomListener;
    private FirebaseDatabase database;
    private DatabaseReference gameroomRef;
    private FirebaseAuth mFirebaseauth;
    // de ref naar de gameroom waar we nu in zitten
    private DatabaseReference gameroomRefJoinGame;
    // de gameRoom van de game waar we nu in zitten
    private GameRoom gameroomLocalJoinGame;
    private GameRoom gameroomLocal;
    private boolean playerAdded = false;
    private String gameroomLocalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        //database INIT
        //database = FirebaseDatabase.getInstance();

        // Get the Intent that started this activity and extract the roomName of the room we created
        //Intent intent = getIntent();
        //this.gameroomLocalName = intent.getStringExtra(CreateLobby.EXTRA_MESSAGE);

        //this.gameroomRef = database.getReference().child("GameRooms").child(this.gameroomLocalName);
        //this.gameroomRef = roomRef;

    }





}