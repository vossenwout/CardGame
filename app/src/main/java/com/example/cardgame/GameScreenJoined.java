package com.example.cardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GameScreenJoined extends AppCompatActivity {

    private ChildEventListener roomListener;
    private FirebaseDatabase database;
    private DatabaseReference gameroomRef;
    private FirebaseAuth mFirebaseauth;
    // de gameRoom van de game waar we nu in zitten
    private GameRoom gameroomLocal;
    private boolean playerAdded = false;
    private String gameroomLocalName;
    private ArrayList<Integer> playerHand;
    private String displayName;

    // TODO verander dat de class werkt met de echte playerName ipv POOKIE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen_joined);

        //database INIT
        database = FirebaseDatabase.getInstance();

        //playerhand INIT
        this.playerHand = new ArrayList<Integer>();

        // User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.displayName = user.getDisplayName();

        // Get the Intent that started this activity and extract the roomName of the room we created
        Intent intent = getIntent();
        this.gameroomLocalName = intent.getStringExtra(CreateLobby.EXTRA_MESSAGE);

        this.gameroomRef = database.getReference().child("GameRooms").child(this.gameroomLocalName);
        joinGameLobby();

    }

    public void joinGameLobby(){

        // we attach a valueListener to the gameRoom we write in the edit text
        attachGameRoomValueListener(this.gameroomRef);

    }


    /**
     * Attaches a listener to the room that is located on the given roomRef
     * ASSYNCHRONE METHODE DUS WE MOETEN WACHTEN OP onDataChange klaar is anders krijgen we null
     * refference als we al eerder de localGame proberen te gebruiken die pas in changeRoom geset
     * wordt
     */

    public void attachGameRoomValueListener(DatabaseReference roomRef){
        ValueEventListener roomValueListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                System.out.println("been heere");
                GameRoom room = dataSnapshot.getValue(GameRoom.class);
                changeRoom(room);

                if( !playerAdded){
                    addPlayer();
                    playerAdded = true;}
                displayPlayedCards();
                updatePlayerHand();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        roomRef.addValueEventListener(roomValueListener1);

    }


    public void changeRoom(GameRoom room){
        System.out.println("hello");
        System.out.println(room.roomName);
        this.gameroomLocal = room;
    }

    /**
     * Displays the top card of the middle of the table, (the play stack)
     */

    public void displayPlayedCards(){
        int card = this.gameroomLocal.playedCards.get(this.gameroomLocal.playedCards.size() -1);
        //ImageView iv_card1 = (ImageView)findViewById(R.id.iv_card1);
        if (card != 99999){
            ImageView playstack = (ImageView)findViewById(R.id.playstack);
            assignCards(card,playstack );
        }

    }


    /**
     * Checks which new cards are added to the player's hand and updates the visuals acordingly
     */

    public void updatePlayerHand(){
        ArrayList<Integer> newPlayerHand = this.gameroomLocal.playerHands.get(this.displayName);
        // Add new cards to the player's hand
        for(int i=0; i < newPlayerHand.size();i++){
            if(!this.playerHand.contains(newPlayerHand.get(i)))
                if(newPlayerHand.get(i) != 99999)
                    displayAddedCardInHand(newPlayerHand.get(i));
        }
        // Remove removed cards from the player's jamd
        for(int i=0; i < this.playerHand.size();i++){
            if(!newPlayerHand.contains(this.playerHand.get(i)))
                if(this.playerHand.get(i) != 99999)
                    removeCardFromHand(this.playerHand.get(i));
        }

        this.playerHand = newPlayerHand;
    }

    /**
     * The drawn card is displayed in the hand of the player, this id of this view is the same
     * as the id of the card
     */

    public void displayAddedCardInHand(int card){
        LayoutInflater inflator = LayoutInflater.from(this);
        LinearLayout gallery = findViewById(R.id.gallery);
        View view2 = inflator.inflate(R.layout.card,gallery,false);
        gallery.addView(view2);
        // creates an unique id for each card
        ImageView kaart= findViewById(R.id.kaart);
        assignCards(card, kaart);
        kaart.setId(card);
        //cardid += 1;
    }

    /**
     * Plays the selected card from the player's hand and moves it to the played stack
     * THis is called when you click on a card
     */

    public void playCard(View view){
        System.out.println(view.getId());
        // Removes the card from the player hand display
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        this.gameroomLocal.playerHands.get(this.displayName).remove(Integer.valueOf(view.getId()));
        this.gameroomLocal.playedCards.add(view.getId());

        displayPlayedCards();
        updateGameRoom();
    }

    /**
     * Removes the card with the given cardId from the playersHannd
     */

    public void removeCardFromHand(int cardid){
        View view = (View) findViewById(cardid);
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
    }


    /**
     * Rare workaround nodig met met this door het eerst aan een lokale variabele te binden
     * GEFIXED NU
     */

    public void addPlayer(){
        this.gameroomLocal.playerIDs.add(this.displayName);
        ArrayList<Integer> player2hand = new ArrayList<Integer>();
        player2hand.add(99999);
        this.gameroomLocal.playerHands.put(this.displayName,player2hand);
        updateGameRoom();
    }

    public void removePlayer(){
        this.gameroomLocal.playerIDs.remove(this.displayName);
        this.gameroomLocal.playerHands.remove(this.displayName);
        updateGameRoom();
    }

    public void updateGameRoom(){
        this.gameroomRef.setValue(this.gameroomLocal);
    }

    public void goBack(View view){

        // we remove the user from the online users database
        //FirebaseUser user = mFirebaseauth.getCurrentUser();
        //DatabaseReference userRef = database.getReference().child("OnlineUsers").child(user.getUid());
        //userRef.removeValue();
        removePlayer();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // assigns the corresponding image to the cardview of the card thats been drawn
    public void assignCards(int card, ImageView cardView){
        //hearts
        switch (card){
            case 101:
                cardView.setImageResource(R.drawable.ah);
        }
        switch (card){
            case 102:
                cardView.setImageResource(R.drawable.c2h);
        }
        switch (card){
            case 103:
                cardView.setImageResource(R.drawable.c3h);
        }
        switch (card){
            case 104:
                cardView.setImageResource(R.drawable.c4h);
        }
        switch (card){
            case 105:
                cardView.setImageResource(R.drawable.c5h);
        }
        switch (card){
            case 106:
                cardView.setImageResource(R.drawable.c6h);
        }
        switch (card){
            case 107:
                cardView.setImageResource(R.drawable.c7h);
        }
        switch (card){
            case 108:
                cardView.setImageResource(R.drawable.c8h);
        }
        switch (card){
            case 109:
                cardView.setImageResource(R.drawable.c9h);
        }
        switch (card){
            case 110:
                cardView.setImageResource(R.drawable.c10h);
        }
        switch (card){
            case 111:
                cardView.setImageResource(R.drawable.jh);
        }
        switch (card){
            case 112:
                cardView.setImageResource(R.drawable.qh);
        }
        switch (card){
            case 113:
                cardView.setImageResource(R.drawable.kh);
        }

        //diamonds
        switch (card){
            case 201:
                cardView.setImageResource(R.drawable.ad);
        }
        switch (card){
            case 202:
                cardView.setImageResource(R.drawable.c2d);
        }
        switch (card){
            case 203:
                cardView.setImageResource(R.drawable.c3d);
        }
        switch (card){
            case 204:
                cardView.setImageResource(R.drawable.c4d);
        }
        switch (card){
            case 205:
                cardView.setImageResource(R.drawable.c5d);
        }
        switch (card){
            case 206:
                cardView.setImageResource(R.drawable.c6d);
        }
        switch (card){
            case 207:
                cardView.setImageResource(R.drawable.c7d);
        }
        switch (card){
            case 208:
                cardView.setImageResource(R.drawable.c8d);
        }
        switch (card){
            case 209:
                cardView.setImageResource(R.drawable.c9d);
        }
        switch (card){
            case 210:
                cardView.setImageResource(R.drawable.c10d);
        }
        switch (card){
            case 211:
                cardView.setImageResource(R.drawable.jd);
        }
        switch (card){
            case 212:
                cardView.setImageResource(R.drawable.qd);
        }
        switch (card){
            case 213:
                cardView.setImageResource(R.drawable.kd);
        }

        //spades
        switch (card){
            case 301:
                cardView.setImageResource(R.drawable.as);
        }
        switch (card){
            case 302:
                cardView.setImageResource(R.drawable.c2s);
        }
        switch (card){
            case 303:
                cardView.setImageResource(R.drawable.c3s);
        }
        switch (card){
            case 304:
                cardView.setImageResource(R.drawable.c4s);
        }
        switch (card){
            case 305:
                cardView.setImageResource(R.drawable.c5s);
        }
        switch (card){
            case 306:
                cardView.setImageResource(R.drawable.c6s);
        }
        switch (card){
            case 307:
                cardView.setImageResource(R.drawable.c7s);
        }
        switch (card){
            case 308:
                cardView.setImageResource(R.drawable.c8s);
        }
        switch (card){
            case 309:
                cardView.setImageResource(R.drawable.c9s);
        }
        switch (card){
            case 310:
                cardView.setImageResource(R.drawable.c10s);
        }
        switch (card){
            case 311:
                cardView.setImageResource(R.drawable.js);
        }
        switch (card){
            case 312:
                cardView.setImageResource(R.drawable.qs);
        }
        switch (card){
            case 313:
                cardView.setImageResource(R.drawable.ks);
        }

        //clubs
        switch (card){
            case 401:
                cardView.setImageResource(R.drawable.ac);
        }
        switch (card){
            case 402:
                cardView.setImageResource(R.drawable.c2c);
        }
        switch (card){
            case 403:
                cardView.setImageResource(R.drawable.c3c);
        }
        switch (card){
            case 404:
                cardView.setImageResource(R.drawable.c4c);
        }
        switch (card){
            case 405:
                cardView.setImageResource(R.drawable.c5c);
        }
        switch (card){
            case 406:
                cardView.setImageResource(R.drawable.c6c);
        }
        switch (card){
            case 407:
                cardView.setImageResource(R.drawable.c7c);
        }
        switch (card){
            case 408:
                cardView.setImageResource(R.drawable.c8c);
        }
        switch (card){
            case 409:
                cardView.setImageResource(R.drawable.c9c);
        }
        switch (card){
            case 410:
                cardView.setImageResource(R.drawable.c10c);
        }
        switch (card){
            case 411:
                cardView.setImageResource(R.drawable.jc);
        }
        switch (card){
            case 412:
                cardView.setImageResource(R.drawable.qc);
        }
        switch (card){
            case 413:
                cardView.setImageResource(R.drawable.kc);
        }
    }
}