package com.example.cardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GameScreenHost extends AppCompatActivity {


    private FirebaseDatabase database;
    private DatabaseReference gameroomRef;
    private String gameroomLocalName;
    private GameRoom gameroomLocal;
    private ArrayList<Integer> deck;
    private ArrayList<Integer> player1hand;
    // ID's to assign to individual cards to control them
    private int cardid = 9990;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen_host);


        // DATABASE INIT
        database = FirebaseDatabase.getInstance();

        //DECK of cards INIT
        initDeck();

        //Player hand init
        this.player1hand = new ArrayList<Integer>();

        // Get the Intent that started this activity and extract the roomName of the room we created
        Intent intent = getIntent();
        String roomName = intent.getStringExtra(CreateLobby.EXTRA_MESSAGE);
        gameroomLocalName = roomName;

        // "playerName" -> cards in current hand
        HashMap<String, ArrayList<Integer>> playerHands = new HashMap<String, ArrayList<Integer>>();
        ArrayList<Integer> player1hand = new ArrayList<Integer>();
        this.player1hand.add(99999);
        playerHands.put("player1",this.player1hand);

        // list of current players
        ArrayList<String> playerIDs = new ArrayList<String>();
        playerIDs.add("player1");
        // cards that are played by the players
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        playedCards.add(99999);

        // cards that are still in the deck
        ArrayList<Integer> deck = new ArrayList<Integer>();
        GameRoom gameroom = new GameRoom(roomName,playerHands,playerIDs,playedCards,deck);

        DatabaseReference roomRef = database.getReference().child("GameRooms").child(roomName);
        this.gameroomRef = roomRef;
        gameroomRef.setValue(gameroom);

        //this.gameroomRef = database.getReference().child("GameRooms").child(roomName);

        attachGameRoomValueListener(this.gameroomRef);

    }


    /**
     * Used for updating the localGameRoom whenever an update happens
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
     * Used to change the gameroomLocal variable with the up to date game room
     */

    public void changeRoom(GameRoom room){
        this.gameroomLocal = room;
    }

    /**
     * Draws a card for every player in the game and updates it
     */
    public void drawCard(View view){
        Collections.shuffle(deck);
        //ArrayList<Integer> player1hand = new ArrayList<Integer>();
        //ArrayList<Integer> player2hand = new ArrayList<Integer>();
        int addedCard = deck.get(0);
        this.player1hand.add(addedCard);
        //player2hand.add(deck.get(1));
        this.deck.remove(0);
        //deck.remove(1);
        //this.gameroomLocal.playerHands.put("player1",player1hand);
        this.gameroomLocal.playerHands.get("player1").add(addedCard);
        //this.gameroomLocal.playerHands.put("player2",player2hand);
        //gameroomRef.setValue(this.gameroomLocal);
        updateGameRoom();
        displayAddedCardInHand(addedCard);
        //displayCardsPlayerHand();
    }

    /**
     * Displays all cards in this player's hand
     */

    public void displayCardsPlayerHand(){
        ArrayList<Integer> cards = gameroomLocal.playerHands.get("player1");
        ImageView iv_card1 = (ImageView)findViewById(R.id.iv_card1);
        int card;
        for(int i =0;i<cards.size();i++){
            card = cards.get(i);
            assignCards(card,iv_card1);
        }
    }

    /**
     * The drawn card is displayed in the hand of the player
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

    public void initDeck(){
        deck = new ArrayList<>();
        // hearts
        deck.add(101);
        deck.add(102);
        deck.add(103);
        deck.add(104);
        deck.add(105);
        deck.add(106);
        deck.add(107);
        deck.add(108);
        deck.add(109);
        deck.add(110);
        deck.add(111);
        deck.add(112);
        deck.add(113);
        // diamonds
        deck.add(201);
        deck.add(202);
        deck.add(203);
        deck.add(204);
        deck.add(205);
        deck.add(206);
        deck.add(207);
        deck.add(208);
        deck.add(209);
        deck.add(210);
        deck.add(211);
        deck.add(212);
        deck.add(213);
        // spades
        deck.add(301);
        deck.add(302);
        deck.add(303);
        deck.add(304);
        deck.add(305);
        deck.add(306);
        deck.add(307);
        deck.add(308);
        deck.add(309);
        deck.add(310);
        deck.add(311);
        deck.add(312);
        deck.add(313);
        // clubs
        deck.add(401);
        deck.add(402);
        deck.add(403);
        deck.add(404);
        deck.add(405);
        deck.add(406);
        deck.add(407);
        deck.add(408);
        deck.add(409);
        deck.add(410);
        deck.add(411);
        deck.add(412);
        deck.add(413);
    }

    /**
     * Reader to read the set of all the rooms currently used and also create the room
     */

    public void removeLobby(final DatabaseReference totalRoomsRef){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> roomNames = (ArrayList<String>) dataSnapshot.getValue();
                removeLobbyFromList(roomNames,totalRoomsRef);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // ...
            }
        };
        totalRoomsRef.addListenerForSingleValueEvent(postListener);
        //totalRoomsRef.addValueEventListener(postListener);
    }


    public void removeLobbyFromList(ArrayList<String> roomNames, DatabaseReference totalRoomsRef){
        roomNames.remove(this.gameroomLocalName);
        totalRoomsRef.removeValue();
        totalRoomsRef.setValue(roomNames);
        // also removes the game from the active games
        this.gameroomRef.removeValue();

    }

    /**
     * Close the lobby and go back to main screen
     */

    public void goBack(View view){
        DatabaseReference totalRoomsRef = database.getReference().child("GameRooms").child("allRoomsSet");
        removeLobby(totalRoomsRef);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
        this.gameroomLocal.playerHands.get("player1").remove(Integer.valueOf(view.getId()));
        this.gameroomLocal.playedCards.add(view.getId());

        displayPlayedCards();
        updateGameRoom();
    }

    /**
     * Displays the top card of the middle of the table, (the play stack)
     */

    public void displayPlayedCards(){
        int card = this.gameroomLocal.playedCards.get(this.gameroomLocal.playedCards.size() -1);
        //ImageView iv_card1 = (ImageView)findViewById(R.id.iv_card1);
        ImageView playstack = (ImageView)findViewById(R.id.playstack);
        assignCards(card,playstack );
    }

    public void updateGameRoom(){
        this.gameroomRef.setValue(this.gameroomLocal);
    }

}