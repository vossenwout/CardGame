package com.example.cardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GameScreenHost extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {


    private FirebaseDatabase database;
    private DatabaseReference gameroomRef;
    private String gameroomLocalName;
    private String roomPassword;
    private GameRoom gameroomLocal;
    private ArrayList<Integer> deck;
    private ArrayList<Integer> player1hand;
    private String displayName;
    // players in previous round
    private ArrayList<String> previousPlayers;
    // last clicked card
    private View lastClickedCard;
    // this players hand
    private ArrayList<Integer> playerHand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen_host);

        // DATABASE INIT
        database = FirebaseDatabase.getInstance();

        // User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.displayName = user.getDisplayName();

        //DECK of cards INIT
        initDeck();

        //previousPlayers
        previousPlayers = new ArrayList<String>();

        //Player hand init
        this.playerHand = new ArrayList<Integer>();
        // this one is used for init the room
        this.player1hand = new ArrayList<Integer>();



        //spinner with the playerID's
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String itemValue = parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), itemValue, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Get the Intent that started this activity and extract the roomName of the room we created
        Intent intent = getIntent();
        String roomName = intent.getStringExtra(CreateLobby.EXTRA_MESSAGE);
        gameroomLocalName = roomName;

        // change the top bar to this lobby name
        setTitle("Lobby Name: " +gameroomLocalName);

        // Set the password for people to join the room
        this.roomPassword = intent.getStringExtra(CreateLobby.PASSWORD);

        // "playerName" -> cards in current hand
        HashMap<String, ArrayList<Integer>> playerHands = new HashMap<String, ArrayList<Integer>>();
        ArrayList<Integer> player1hand = new ArrayList<Integer>();
        this.player1hand.add(99999);
        playerHands.put(displayName, this.player1hand);

        // list of current players
        ArrayList<String> playerIDs = new ArrayList<String>();
        playerIDs.add(displayName);
        // cards that are played by the players
        ArrayList<Integer> playedCards = new ArrayList<Integer>();
        playedCards.add(99999);

        // cards that are still in the deck
        //ArrayList<Integer> deck = new ArrayList<Integer>();
        GameRoom gameroom = new GameRoom(roomName, playerHands, playerIDs, playedCards, this.deck, this.roomPassword);

        DatabaseReference roomRef = database.getReference().child("GameRooms").child(roomName);
        this.gameroomRef = roomRef;
        gameroomRef.setValue(gameroom);

        //this.gameroomRef = database.getReference().child("GameRooms").child(roomName);

        attachGameRoomValueListener(this.gameroomRef);

    }


    /**
     * Used for updating the localGameRoom whenever an update happens
     */

    public void attachGameRoomValueListener(DatabaseReference roomRef) {
        ValueEventListener roomValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GameRoom room = dataSnapshot.getValue(GameRoom.class);
                changeRoom(room);
                displayPlayedCards();
                updatePlayerHand();
                updateSpinnerWithPlayers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        roomRef.addValueEventListener(roomValueListener);
    }


    /**
     * Checks which new cards are added to the player's hand and updates the visuals acordingly
     */

    public void updatePlayerHand(){
        ArrayList<Integer> newPlayerHand = this.gameroomLocal.playerHands.get(this.displayName);
        if(newPlayerHand != null) {
            // Add new cards to the player's hand
            for (int i = 0; i < newPlayerHand.size(); i++) {
                if (!this.playerHand.contains(newPlayerHand.get(i)))
                    if (newPlayerHand.get(i) != 99999){
                        displayAddedCardInHand(newPlayerHand.get(i));
                    }
            }
            // Remove removed cards from the player's jamd
            for (int i = 0; i < this.playerHand.size(); i++) {
                if (!newPlayerHand.contains(this.playerHand.get(i)))
                    if (this.playerHand.get(i) != 99999)
                        removeCardFromHand(this.playerHand.get(i));
            }

            this.playerHand = newPlayerHand;
        }
    }

    /**
     * Used to change the gameroomLocal variable with the up to date game room
     */

    public void changeRoom(GameRoom room) {
        this.gameroomLocal = room;
    }

    /**
     * Draws a card for every player in the game and updates it
     */
    public void drawCard(View view) {
        //Collections.shuffle(deck);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String drawForWho = spinner.getSelectedItem().toString();
        if(this.gameroomLocal.deck != null) {
            if (drawForWho == "All players") {
                int addedCard;
                int playerCount = this.gameroomLocal.playerIDs.size();
                int addedCardForThisPlayer = this.gameroomLocal.deck.get(0);
                for (int i = 0; i < playerCount; i++) {
                    addedCard = this.gameroomLocal.deck.get(0);
                    this.gameroomLocal.deck.remove(0);
                    this.gameroomLocal.playerHands.get(this.gameroomLocal.playerIDs.get(i)).add(addedCard);
                    if (this.gameroomLocal.playerIDs.get(i) == this.displayName){
                        Toast.makeText(getApplicationContext(), "Card added to hand", Toast.LENGTH_SHORT).show();
                        addedCardForThisPlayer = addedCard;}
                }

                updateGameRoom();
                displayAddedCardInHand(addedCardForThisPlayer);
            } else {
                int addedCardForThisPlayer = this.gameroomLocal.deck.get(0);
                this.gameroomLocal.deck.remove(0);
                this.gameroomLocal.playerHands.get(drawForWho).add(addedCardForThisPlayer);

                if (drawForWho.equals(this.displayName)) {
                    displayAddedCardInHand(addedCardForThisPlayer);

                    Toast.makeText(getApplicationContext(), "Card added to hand", Toast.LENGTH_SHORT).show();
                }
                updateGameRoom();
            }
        }
        else{
            System.out.println("deck has no more cards");
        }
    }

    public void giveCard(int card, String playerName){
        if(!playerName.equals(this.displayName)){
            this.gameroomLocal.playerHands.get(this.displayName).remove(Integer.valueOf(card));
            this.gameroomLocal.playerHands.get(playerName).add(Integer.valueOf(card));
            removeCardFromHand(card);
            updateGameRoom();
        }
    }




    /**
     * Takes the last card or cards back from the selected players
     */

    public void takeCardBack(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String takeFromWho = spinner.getSelectedItem().toString();
        if (takeFromWho == "All players") {
            int playerCount = this.gameroomLocal.playerIDs.size();
            int removedCardForThisPlayer;
            for (int i = 0; i < playerCount; i++) {
                this.gameroomLocal.deck.remove(0);
                // remove the last card
                int lastcardIndex = this.gameroomLocal.playerHands.get(this.gameroomLocal.playerIDs.get(i)).size() - 1;
                if (lastcardIndex > 0) {
                    removedCardForThisPlayer = this.gameroomLocal.playerHands.get(this.gameroomLocal.playerIDs.get(i)).get(lastcardIndex);
                    this.gameroomLocal.playerHands.get(this.gameroomLocal.playerIDs.get(i)).remove(lastcardIndex);
                    this.gameroomLocal.deck.add(Integer.valueOf(removedCardForThisPlayer));
                    if (this.gameroomLocal.playerIDs.get(i).equals(this.displayName))
                        removeCardFromHand(removedCardForThisPlayer);
                }
            }

            updateGameRoom();
        } else {
            int removedCardForThisPlayer;
            int lastcardIndex = this.gameroomLocal.playerHands.get(takeFromWho).size() - 1;
            if (lastcardIndex > 0) {
                removedCardForThisPlayer = this.gameroomLocal.playerHands.get(takeFromWho).get(lastcardIndex);
                this.gameroomLocal.playerHands.get(takeFromWho).remove(lastcardIndex);
                this.gameroomLocal.deck.add(Integer.valueOf(removedCardForThisPlayer));
                if (takeFromWho.equals(this.displayName)) {
                    removeCardFromHand(removedCardForThisPlayer);
                }
            }
            updateGameRoom();
        }

    }

    /**
     * Displays all cards in this player's hand
     */

    public void displayCardsPlayerHand() {
        ArrayList<Integer> cards = gameroomLocal.playerHands.get("player1");
        ImageView iv_card1 = (ImageView) findViewById(R.id.iv_card1);
        int card;
        for (int i = 0; i < cards.size(); i++) {
            card = cards.get(i);
            assignCards(card, iv_card1);
        }
    }

    /**
     * Update the spinner with players
     */

    public void updateSpinnerWithPlayers() {
        //Spinner tests
        ArrayList<String> currentPlayers = new ArrayList<String>();
        for (int i = 0; i < this.gameroomLocal.playerIDs.size(); i++) {
            currentPlayers.add(this.gameroomLocal.playerIDs.get(i));
        }
        currentPlayers.add("All players");

        if (!this.previousPlayers.containsAll(currentPlayers)) {
            this.previousPlayers = new ArrayList<String>();
            for (int i = 0; i < this.gameroomLocal.playerIDs.size(); i++) {
                this.previousPlayers.add(this.gameroomLocal.playerIDs.get(i));
            }
            if (!this.previousPlayers.contains("All players"))
                this.previousPlayers.add(0, "All players");
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_dropdown_item, this.previousPlayers);
            spinner.setAdapter(spinnerAdapter);
        }

        if (!currentPlayers.containsAll(this.previousPlayers)) {
            this.previousPlayers = new ArrayList<String>();
            for (int i = 0; i < this.gameroomLocal.playerIDs.size(); i++) {
                this.previousPlayers.add(this.gameroomLocal.playerIDs.get(i));
            }
            if (!this.previousPlayers.contains("All players"))
                this.previousPlayers.add(0, "All players");
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_dropdown_item, this.previousPlayers);
            spinner.setAdapter(spinnerAdapter);
        }


    }

    /**
     * The drawn card is displayed in the hand of the player, this id of this view is the same
     * as the id of the card
     */

    public void displayAddedCardInHand(int card) {
        LayoutInflater inflator = LayoutInflater.from(this);
        LinearLayout gallery = findViewById(R.id.gallery);
        View view2 = inflator.inflate(R.layout.card, gallery, false);
        gallery.addView(view2);
        // creates an unique id for each card
        ImageView kaart = findViewById(R.id.kaart);
        assignCards(card, kaart);
        kaart.setId(card);
        //cardid += 1;
    }


    // assigns the corresponding image to the cardview of the card thats been drawn
    public void assignCards(int card, ImageView cardView) {
        //hearts
        switch (card) {
            case 101:
                cardView.setImageResource(R.drawable.ah);
        }
        switch (card) {
            case 102:
                cardView.setImageResource(R.drawable.c2h);
        }
        switch (card) {
            case 103:
                cardView.setImageResource(R.drawable.c3h);
        }
        switch (card) {
            case 104:
                cardView.setImageResource(R.drawable.c4h);
        }
        switch (card) {
            case 105:
                cardView.setImageResource(R.drawable.c5h);
        }
        switch (card) {
            case 106:
                cardView.setImageResource(R.drawable.c6h);
        }
        switch (card) {
            case 107:
                cardView.setImageResource(R.drawable.c7h);
        }
        switch (card) {
            case 108:
                cardView.setImageResource(R.drawable.c8h);
        }
        switch (card) {
            case 109:
                cardView.setImageResource(R.drawable.c9h);
        }
        switch (card) {
            case 110:
                cardView.setImageResource(R.drawable.c10h);
        }
        switch (card) {
            case 111:
                cardView.setImageResource(R.drawable.jh);
        }
        switch (card) {
            case 112:
                cardView.setImageResource(R.drawable.qh);
        }
        switch (card) {
            case 113:
                cardView.setImageResource(R.drawable.kh);
        }

        //diamonds
        switch (card) {
            case 201:
                cardView.setImageResource(R.drawable.ad);
        }
        switch (card) {
            case 202:
                cardView.setImageResource(R.drawable.c2d);
        }
        switch (card) {
            case 203:
                cardView.setImageResource(R.drawable.c3d);
        }
        switch (card) {
            case 204:
                cardView.setImageResource(R.drawable.c4d);
        }
        switch (card) {
            case 205:
                cardView.setImageResource(R.drawable.c5d);
        }
        switch (card) {
            case 206:
                cardView.setImageResource(R.drawable.c6d);
        }
        switch (card) {
            case 207:
                cardView.setImageResource(R.drawable.c7d);
        }
        switch (card) {
            case 208:
                cardView.setImageResource(R.drawable.c8d);
        }
        switch (card) {
            case 209:
                cardView.setImageResource(R.drawable.c9d);
        }
        switch (card) {
            case 210:
                cardView.setImageResource(R.drawable.c10d);
        }
        switch (card) {
            case 211:
                cardView.setImageResource(R.drawable.jd);
        }
        switch (card) {
            case 212:
                cardView.setImageResource(R.drawable.qd);
        }
        switch (card) {
            case 213:
                cardView.setImageResource(R.drawable.kd);
        }

        //spades
        switch (card) {
            case 301:
                cardView.setImageResource(R.drawable.as);
        }
        switch (card) {
            case 302:
                cardView.setImageResource(R.drawable.c2s);
        }
        switch (card) {
            case 303:
                cardView.setImageResource(R.drawable.c3s);
        }
        switch (card) {
            case 304:
                cardView.setImageResource(R.drawable.c4s);
        }
        switch (card) {
            case 305:
                cardView.setImageResource(R.drawable.c5s);
        }
        switch (card) {
            case 306:
                cardView.setImageResource(R.drawable.c6s);
        }
        switch (card) {
            case 307:
                cardView.setImageResource(R.drawable.c7s);
        }
        switch (card) {
            case 308:
                cardView.setImageResource(R.drawable.c8s);
        }
        switch (card) {
            case 309:
                cardView.setImageResource(R.drawable.c9s);
        }
        switch (card) {
            case 310:
                cardView.setImageResource(R.drawable.c10s);
        }
        switch (card) {
            case 311:
                cardView.setImageResource(R.drawable.js);
        }
        switch (card) {
            case 312:
                cardView.setImageResource(R.drawable.qs);
        }
        switch (card) {
            case 313:
                cardView.setImageResource(R.drawable.ks);
        }

        //clubs
        switch (card) {
            case 401:
                cardView.setImageResource(R.drawable.ac);
        }
        switch (card) {
            case 402:
                cardView.setImageResource(R.drawable.c2c);
        }
        switch (card) {
            case 403:
                cardView.setImageResource(R.drawable.c3c);
        }
        switch (card) {
            case 404:
                cardView.setImageResource(R.drawable.c4c);
        }
        switch (card) {
            case 405:
                cardView.setImageResource(R.drawable.c5c);
        }
        switch (card) {
            case 406:
                cardView.setImageResource(R.drawable.c6c);
        }
        switch (card) {
            case 407:
                cardView.setImageResource(R.drawable.c7c);
        }
        switch (card) {
            case 408:
                cardView.setImageResource(R.drawable.c8c);
        }
        switch (card) {
            case 409:
                cardView.setImageResource(R.drawable.c9c);
        }
        switch (card) {
            case 410:
                cardView.setImageResource(R.drawable.c10c);
        }
        switch (card) {
            case 411:
                cardView.setImageResource(R.drawable.jc);
        }
        switch (card) {
            case 412:
                cardView.setImageResource(R.drawable.qc);
        }
        switch (card) {
            case 413:
                cardView.setImageResource(R.drawable.kc);
        }
    }

    public void initDeck() {
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
        if(this.gameroomLocal != null){
            this.gameroomLocal.deck = deck;
            updateGameRoom();}
    }

    /**
     * Reader to read the set of all the rooms currently used and also create the room
     */

    public void removeLobby(final DatabaseReference totalRoomsRef) {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> roomNames = (ArrayList<String>) dataSnapshot.getValue();
                removeLobbyFromList(roomNames, totalRoomsRef);
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


    public void removeLobbyFromList(ArrayList<String> roomNames, DatabaseReference totalRoomsRef) {
        roomNames.remove(this.gameroomLocalName);
        totalRoomsRef.removeValue();
        totalRoomsRef.setValue(roomNames);
        // also removes the game from the active games
        this.gameroomRef.removeValue();

    }

    /**
     * Close the lobby and go back to main screen
     */

    public void goBack(View view) {
        DatabaseReference totalRoomsRef = database.getReference().child("GameRooms").child("allRoomsSet");
        removeLobby(totalRoomsRef);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Plays the selected card from the player's hand and moves it to the played stack
     * THis is called when you click on a card
     */

    public void playCard(View view) {
        // shows the popup menu for what the player wants to do with this card
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu_card);
        popup.show();
        /////
        // updates the last played card to the clicked card
        this.lastClickedCard = view;

    }

    public void removeCardFromHand(int cardid) {
        View view = (View) findViewById(cardid);
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
    }

    public void updateSpinnerWithPlayerNames(){
        ;
    }

    /**
     * Displays the top card of the middle of the table, (the play stack)
     */

    public void displayPlayedCards() {

        /**
         if(this.gameroomLocal.playedCards != null) {
         int totalAmountOfPlayedCards = this.gameroomLocal.playedCards.size();
         if (totalAmountOfPlayedCards <= 1) {
         ImageView playstack = (ImageView) findViewById(R.id.playstack);
         playstack.setImageResource(R.drawable.gray_back);
         } else {
         int card = this.gameroomLocal.playedCards.get(this.gameroomLocal.playedCards.size() - 1);
         ImageView playstack = (ImageView) findViewById(R.id.playstack);
         assignCards(card, playstack);
         }
         }
         */

        if (this.gameroomLocal != null) {
            int totalAmountOfPlayedCards = this.gameroomLocal.playedCards.size();
            if (totalAmountOfPlayedCards < 6 && totalAmountOfPlayedCards > 1) {
                ImageView playstack = (ImageView) findViewById(R.id.playstack);
                ImageView playstack2 = (ImageView) findViewById(R.id.playstack2);
                ImageView playstack3 = (ImageView) findViewById(R.id.playstack3);
                ImageView playstack4 = (ImageView) findViewById(R.id.playstack4);
                ImageView playstack5 = (ImageView) findViewById(R.id.playstack5);
                switch (totalAmountOfPlayedCards) {
                    case 2: {
                        int card = this.gameroomLocal.playedCards.get(this.gameroomLocal.playedCards.size() - 1);
                        playstack.setVisibility(View.VISIBLE);
                        playstack2.setVisibility(View.INVISIBLE);
                        playstack3.setVisibility(View.INVISIBLE);
                        playstack4.setVisibility(View.INVISIBLE);
                        playstack5.setVisibility(View.INVISIBLE);
                        assignCards(card, playstack);
                        break;
                    }
                    case 3: {
                        int card2 = this.gameroomLocal.playedCards.get(this.gameroomLocal.playedCards.size() - 1);
                        playstack.setVisibility(View.VISIBLE);
                        playstack2.setVisibility(View.VISIBLE);
                        playstack3.setVisibility(View.INVISIBLE);
                        playstack4.setVisibility(View.INVISIBLE);
                        playstack5.setVisibility(View.INVISIBLE);
                        assignCards(card2, playstack2);
                        break;
                    }
                    case 4: {
                        int card3 = this.gameroomLocal.playedCards.get(this.gameroomLocal.playedCards.size() - 1);
                        playstack.setVisibility(View.VISIBLE);
                        playstack2.setVisibility(View.VISIBLE);
                        playstack3.setVisibility(View.VISIBLE);
                        playstack4.setVisibility(View.INVISIBLE);
                        playstack5.setVisibility(View.INVISIBLE);
                        assignCards(card3, playstack3);
                        break;
                    }
                    case 5: {
                        int card4 = this.gameroomLocal.playedCards.get(this.gameroomLocal.playedCards.size() - 1);
                        playstack.setVisibility(View.VISIBLE);
                        playstack2.setVisibility(View.VISIBLE);
                        playstack3.setVisibility(View.VISIBLE);
                        playstack4.setVisibility(View.VISIBLE);
                        playstack5.setVisibility(View.INVISIBLE);
                        assignCards(card4, playstack4);
                        break;
                    }

                }
            } else if (totalAmountOfPlayedCards >= 6) {
                ImageView playstack = (ImageView) findViewById(R.id.playstack);
                ImageView playstack2 = (ImageView) findViewById(R.id.playstack2);
                ImageView playstack3 = (ImageView) findViewById(R.id.playstack3);
                ImageView playstack4 = (ImageView) findViewById(R.id.playstack4);
                ImageView playstack5 = (ImageView) findViewById(R.id.playstack5);
                playstack.setVisibility(View.VISIBLE);
                playstack2.setVisibility(View.VISIBLE);
                playstack3.setVisibility(View.VISIBLE);
                playstack4.setVisibility(View.VISIBLE);
                playstack5.setVisibility(View.VISIBLE);
                int newCard = this.gameroomLocal.playedCards.get(this.gameroomLocal.playedCards.size() - 1);
                int currentCard4 = this.gameroomLocal.playedCards.get(this.gameroomLocal.playedCards.size() - 2);
                int currentCard3 = this.gameroomLocal.playedCards.get(this.gameroomLocal.playedCards.size() - 3);
                int currentCard2 = this.gameroomLocal.playedCards.get(this.gameroomLocal.playedCards.size() - 4);
                int currentCard1 = this.gameroomLocal.playedCards.get(this.gameroomLocal.playedCards.size() - 5);
                assignCards(newCard, playstack5);
                assignCards(currentCard4, playstack4);
                assignCards(currentCard3, playstack3);
                assignCards(currentCard2, playstack2);
                assignCards(currentCard1,playstack);
            } else {
                // set everything invisble
                ImageView playstack = (ImageView) findViewById(R.id.playstack);
                ImageView playstack2 = (ImageView) findViewById(R.id.playstack2);
                ImageView playstack3 = (ImageView) findViewById(R.id.playstack3);
                ImageView playstack4 = (ImageView) findViewById(R.id.playstack4);
                ImageView playstack5 = (ImageView) findViewById(R.id.playstack5);
                playstack.setVisibility(View.INVISIBLE);
                playstack2.setVisibility(View.INVISIBLE);
                playstack3.setVisibility(View.INVISIBLE);
                playstack4.setVisibility(View.INVISIBLE);
                playstack5.setVisibility(View.INVISIBLE);
            }


        }

    }

    public void updateGameRoom() {
        this.gameroomRef.setValue(this.gameroomLocal);
    }

    public void resetGame(View view) {
        // We reset the deck
        initDeck();
        // We remove all the cards from the hands of the players
        int playerCount = this.gameroomLocal.playerIDs.size();
        int removedCardForThisPlayer;
        for (int i = 0; i < playerCount; i++) {
            // cards that are played by the players
            ArrayList<Integer> playedCards = new ArrayList<Integer>();
            playedCards.add(99999);
            this.gameroomLocal.playedCards = playedCards;
            int totalCardsForThatPlayer = this.gameroomLocal.playerHands.get(this.gameroomLocal.playerIDs.get(i)).size();
            if (totalCardsForThatPlayer > 0) {
                for (int j = 0; j < totalCardsForThatPlayer; j++) {
                    // we iterate trough all the cards of the player and remove these
                    removedCardForThisPlayer = this.gameroomLocal.playerHands.get(this.gameroomLocal.playerIDs.get(i)).get(this.gameroomLocal.playerHands.get(this.gameroomLocal.playerIDs.get(i)).size() - 1);
                    if (removedCardForThisPlayer != 99999) {
                        this.gameroomLocal.playerHands.get(this.gameroomLocal.playerIDs.get(i)).remove(this.gameroomLocal.playerHands.get(this.gameroomLocal.playerIDs.get(i)).size() - 1);
                        if (this.gameroomLocal.playerIDs.get(i).equals(this.displayName)) {
                            removeCardFromHand(removedCardForThisPlayer);
                        }
                    }
                }
            }

        }
        // we set the middle cards back to invisible
        ImageView playstack = (ImageView) findViewById(R.id.playstack);
        ImageView playstack2 = (ImageView) findViewById(R.id.playstack2);
        ImageView playstack3 = (ImageView) findViewById(R.id.playstack3);
        ImageView playstack4 = (ImageView) findViewById(R.id.playstack4);
        playstack.setImageDrawable(null);
        playstack.setVisibility(View.INVISIBLE);
        playstack2.setImageDrawable(null);
        playstack2.setVisibility(View.INVISIBLE);
        playstack3.setImageDrawable(null);
        playstack3.setVisibility(View.INVISIBLE);
        playstack4.setImageDrawable(null);
        playstack4.setVisibility(View.INVISIBLE);
        //  we update the gameroom
        updateGameRoom();
        this.gameroomRef.child("deck").setValue(this.deck);
    }

    /**
     * Takes the  last card from the played cards stack and puts it into the player's hand
     */

    public void takeFromStack(View view) {
        int totalAmountOfPlayedCards = this.gameroomLocal.playedCards.size();
        if (totalAmountOfPlayedCards > 1) {
            // shows the popup menu for what the player wants to do with this card
            PopupMenu popup = new PopupMenu(this, view);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.popup_menu_playstack);
            popup.show();
            /////
            // updates the last played card to the clicked card
            this.lastClickedCard = view;

        }
    }

    public void shuffleDeck(View view){
        Collections.shuffle(this.gameroomLocal.deck);
        updateGameRoom();
    }

    /**
     * Need to implement this for the popupmenu on the card
     */

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            // play is clicked
            case R.id.menuItem1:
                ViewGroup parent = (ViewGroup) this.lastClickedCard.getParent();
                if (parent != null) {
                    parent.removeView(this.lastClickedCard);
                }
                this.gameroomLocal.playerHands.get(this.displayName).remove(Integer.valueOf(this.lastClickedCard.getId()));
                this.gameroomLocal.playedCards.add(this.lastClickedCard.getId());

                displayPlayedCards();
                updateGameRoom();
                break;
                // give to a player is clicked we show a new menu with the available players
                // warnning this will call this same on menu item click so we have to handle
                // this in the default method
            case R.id.menuItem2:
                PopupMenu popup = new PopupMenu(this, this.lastClickedCard);
                popup.setOnMenuItemClickListener(this);
                int totalPlayers = this.gameroomLocal.playerIDs.size();
                for (int i = 0; i < totalPlayers; i++) {
                    popup.getMenu().add(this.gameroomLocal.playerIDs.get(i));
                }
                popup.show();
                break;
                // remove the card from play, dont put it back in the decck
            case R.id.menuItem3:
                this.gameroomLocal.playerHands.get(this.displayName).remove(Integer.valueOf(this.lastClickedCard.getId()));
                removeCardFromHand(Integer.valueOf(this.lastClickedCard.getId()));
                updateGameRoom();
                break;
            case R.id.menuItem4:
                this.gameroomLocal.playerHands.get(this.displayName).remove(Integer.valueOf(this.lastClickedCard.getId()));
                removeCardFromHand(Integer.valueOf(this.lastClickedCard.getId()));
                this.gameroomLocal.deck.add(Integer.valueOf(this.lastClickedCard.getId()));
                updateGameRoom();
                break;

            case R.id.playmenuItem1:
                int totalAmountOfPlayedCards = this.gameroomLocal.playedCards.size();
                int topcard = this.gameroomLocal.playedCards.get(totalAmountOfPlayedCards - 1);
                this.gameroomLocal.playedCards.remove(totalAmountOfPlayedCards - 1);
                this.gameroomLocal.playerHands.get(this.displayName).add(topcard);
                Toast.makeText(getApplicationContext(), "Card added to hand", Toast.LENGTH_SHORT).show();
                displayAddedCardInHand(topcard);
                displayPlayedCards();
                updateGameRoom();
                break;
            case R.id.playmenuItem2:
                totalAmountOfPlayedCards = this.gameroomLocal.playedCards.size();
                for(int i = 0; i<totalAmountOfPlayedCards;i++){
                    this.gameroomLocal.playedCards.remove(0);
                }
                this.gameroomLocal.playedCards.add(99999);

                displayPlayedCards();
                updateGameRoom();
                break;
                // we check if the selected item is one of the playernames

            default:
                int totalPlayerss = this.gameroomLocal.playerIDs.size();
                for (int i = 0; i < totalPlayerss; i++) {
                    // gives the clicked on card to the selected player
                    if(menuItem.getTitle() == this.gameroomLocal.playerIDs.get(i)){
                        giveCard(this.lastClickedCard.getId(),this.gameroomLocal.playerIDs.get(i));
                    }
                }


        }
        return true;
    }
}