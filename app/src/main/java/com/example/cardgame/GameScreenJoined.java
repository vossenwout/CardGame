package com.example.cardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GameScreenJoined extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

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
    // last clicked card
    private View lastClickedCard;
    private Boolean welcomeTextVisible = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen_joined);

        //database INIT
        database = FirebaseDatabase.getInstance();

        //playerhand INIT
        this.playerHand = new ArrayList<Integer>();

        // get displayname of usere
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.displayName = user.getDisplayName();

        // Get the Intent that started this activity and extract the roomName of the room we created
        Intent intent = getIntent();
        this.gameroomLocalName = intent.getStringExtra(CreateLobby.EXTRA_MESSAGE);

        // change the top bar to this lobby name
        setTitle("Lobby Name: " +gameroomLocalName);

        // for on forcefull disconnect to remove own playerid from lobby
        DatabaseReference roomRefPlayerHands = database.getReference().child("GameRooms").child(this.gameroomLocalName).child("playerHands").child(this.displayName);
        roomRefPlayerHands.onDisconnect().removeValue();

        this.gameroomRef = database.getReference().child("GameRooms").child(this.gameroomLocalName);
        joinGameLobby();

    }

    public void joinGameLobby(){

        // we attach a valueListener to the gameRoom we write in the edit text
        attachGameRoomValueListener(this.gameroomRef);

    }


    /**
     * Lisens for updates to the game room directory in the database and makes updates to the game
     * according to that.
     */

    public void attachGameRoomValueListener(DatabaseReference roomRef){
        ValueEventListener roomValueListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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

    /**
     * Update the local variable
     */

    public void changeRoom(GameRoom room){
        this.gameroomLocal = room;
    }


    /**
     * Displays the top 5 cards of the middle of the table, (the play stack)
     */

    public void displayPlayedCards() {

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
        else{
            goBackForce();
        }

    }


    /**
     * Checks which new cards are added to the player's hand and updates the visuals of the playershand
     */

    public void updatePlayerHand(){
        if (this.gameroomLocal!= null) {
            ArrayList<Integer> newPlayerHand = this.gameroomLocal.playerHands.get(this.displayName);
            if (newPlayerHand != null) {
                // Add new cards to the player's hand
                if (newPlayerHand.size() > 1 && this.welcomeTextVisible) {
                    TextView view = (TextView) findViewById(R.id.startText);

                    view.setVisibility(View.INVISIBLE);
                    this.welcomeTextVisible = false;
                }

                for (int i = 0; i < newPlayerHand.size(); i++) {
                    if (!this.playerHand.contains(newPlayerHand.get(i)))
                        if (newPlayerHand.get(i) != 99999){
                            displayAddedCardInHand(newPlayerHand.get(i));
                            Toast.makeText(getApplicationContext(), "Card added to hand", Toast.LENGTH_SHORT).show();
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
        else{
            goBackForce();
        }
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
    }


    /**
     * Plays the selected card from the player's hand and moves it to the played stack.
     */

    @SuppressLint("ResourceType")
    public void playCard(View view) {
        // updates the last played card to the clicked card
        this.lastClickedCard = view;

        // shows the popup menu for what the player wants to do with this card
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        // if the last clicked card is facedown
        if(this.lastClickedCard.getId() >= 1000)
            popup.inflate(R.menu.popup_menu_facedown_card);
        else
            popup.inflate(R.menu.popup_menu_card);
        popup.show();

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
        if(this.gameroomLocal != null) {
            if (this.gameroomLocal.playerIDs != null) {
                this.gameroomLocal.playerIDs.add(this.displayName);
                ArrayList<Integer> player2hand = new ArrayList<Integer>();
                player2hand.add(99999);
                this.gameroomLocal.playerHands.put(this.displayName, player2hand);
                updateGameRoom();
            }
        }
        else{
            goBackForce();
        }

    }

    /**
     * Remove player from the game.
     */

    public void removePlayer(){
        this.gameroomLocal.playerIDs.remove(this.displayName);
        this.gameroomLocal.playerHands.remove(this.displayName);
        updateGameRoom();
    }

    /**
     * Updates the local gameroom with new updates from the gameroom from the database
     */

    public void updateGameRoom(){
        this.gameroomRef.setValue(this.gameroomLocal);
    }

    /**
     * If we leave the gamelobby
     */

    public void goBack(View view){

        removePlayer();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Leave the lobby room if he host disconnected before
     */

    public void goBackForce(){

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
        // we add 1000 to a card if its facedown
        if(card >= 1000){
            cardView.setImageResource(R.drawable.gray_back);
        }
    }

    public void takeFromStack(View view){
        if(gameroomLocal != null) {
            int totalAmountOfPlayedCards = this.gameroomLocal.playedCards.size();
            if (totalAmountOfPlayedCards > 1) {
                // shows the popup menu for what the player wants to do with this card
                PopupMenu popup = new PopupMenu(this, view);
                popup.setOnMenuItemClickListener(this);
                if (this.gameroomLocal.playedCards.get(totalAmountOfPlayedCards - 1) >= 1000)
                    popup.inflate(R.menu.popup_menu_facedownplaystack);
                else
                    popup.inflate(R.menu.popup_menu_playstack);
                popup.show();
                /////
                // updates the last played card to the clicked card
                this.lastClickedCard = view;
            }

        }
        else{
            goBackForce();
        }
    }

    public void giveCard(int card, String playerName){

        if(gameroomLocal != null) {
            if (!playerName.equals(this.displayName)) {
                this.gameroomLocal.playerHands.get(this.displayName).remove(Integer.valueOf(card));
                this.gameroomLocal.playerHands.get(playerName).add(Integer.valueOf(card));
                removeCardFromHand(card);
                updateGameRoom();
            }
        }
        else{
            goBackForce();
        }
    }

    /**
     * Popup menu with different actions for what to do with the card that gets clicked on.
     */

    @SuppressLint("ResourceType")
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (gameroomLocal != null) {
            if(this.lastClickedCard !=null) {

                switch (menuItem.getItemId()) {
                    // play is clicked
                    case R.id.playMenuItem:
                        ViewGroup parent = (ViewGroup) this.lastClickedCard.getParent();
                        if (parent != null) {
                            parent.removeView(this.lastClickedCard);
                        }
                        this.gameroomLocal.playerHands.get(this.displayName).remove(Integer.valueOf(this.lastClickedCard.getId()));
                        this.gameroomLocal.playedCards.add(this.lastClickedCard.getId());

                        displayPlayedCards();
                        updateGameRoom();
                        this.lastClickedCard=null;
                        break;
                    // plays the card facedown by adding 1000 to the id of the card
                    case R.id.playFaceDownMenuItem:
                        parent = (ViewGroup) this.lastClickedCard.getParent();
                        if (parent != null) {
                            parent.removeView(this.lastClickedCard);
                        }
                        this.gameroomLocal.playerHands.get(this.displayName).remove(Integer.valueOf(this.lastClickedCard.getId()));
                        this.gameroomLocal.playedCards.add(this.lastClickedCard.getId() + 1000);

                        displayPlayedCards();
                        updateGameRoom();
                        this.lastClickedCard=null;
                        break;
                    case R.id.flipCardMenuItem:
                        this.gameroomLocal.playerHands.get(this.displayName).set(this.gameroomLocal.playerHands.get(this.displayName).indexOf(Integer.valueOf(this.lastClickedCard.getId())), Integer.valueOf(this.lastClickedCard.getId()) % 1000);
                        this.lastClickedCard.setId(this.lastClickedCard.getId() % 1000);
                        assignCards(this.lastClickedCard.getId(), (ImageView) this.lastClickedCard);
                        displayPlayedCards();
                        updateGameRoom();
                        this.lastClickedCard=null;
                        break;
                    case R.id.flipCard:
                        this.gameroomLocal.playerHands.get(this.displayName).set(this.gameroomLocal.playerHands.get(this.displayName).indexOf(Integer.valueOf(this.lastClickedCard.getId())), Integer.valueOf(this.lastClickedCard.getId()) + 1000);
                        this.lastClickedCard.setId(this.lastClickedCard.getId() + 1000);
                        assignCards(this.lastClickedCard.getId(), (ImageView) this.lastClickedCard);
                        this.lastClickedCard = null;
                        displayPlayedCards();
                        updateGameRoom();
                        break;
                    case R.id.flipTopCard:
                        this.gameroomLocal.playedCards.set(this.gameroomLocal.playedCards.size() - 1, this.gameroomLocal.playedCards.get(this.gameroomLocal.playedCards.size() - 1) % 1000);
                        //assignCards(this.lastClickedCard.getId(), (ImageView) this.lastClickedCard);
                        displayPlayedCards();
                        updateGameRoom();
                        this.lastClickedCard=null;
                        break;
                    // give to a player is clicked we show a new menu with the available players
                    // warnning this will call this same on menu item click so we have to handle
                    // this in the default method
                    case R.id.GiveTomenuItem:
                        PopupMenu popup = new PopupMenu(this, this.lastClickedCard);
                        popup.setOnMenuItemClickListener(this);
                        int totalPlayers = this.gameroomLocal.playerIDs.size();
                        for (int i = 0; i < totalPlayers; i++) {
                            popup.getMenu().add(this.gameroomLocal.playerIDs.get(i));
                        }
                        popup.show();
                        break;
                    // remove the card from play, dont put it back in the decck
                    case R.id.discardMenuItem:
                        this.gameroomLocal.playerHands.get(this.displayName).remove(Integer.valueOf(this.lastClickedCard.getId()));
                        removeCardFromHand(Integer.valueOf(this.lastClickedCard.getId()));
                        updateGameRoom();
                        this.lastClickedCard=null;
                        break;
                    case R.id.PutinDeckMenuItem:
                        this.gameroomLocal.playerHands.get(this.displayName).remove(Integer.valueOf(this.lastClickedCard.getId()));
                        removeCardFromHand(Integer.valueOf(this.lastClickedCard.getId()));
                        this.gameroomLocal.deck.add(Integer.valueOf(this.lastClickedCard.getId()));
                        updateGameRoom();
                        this.lastClickedCard=null;
                        break;
                    case R.id.takePlayedCardmenuItem1:
                        int totalAmountOfPlayedCards = this.gameroomLocal.playedCards.size();
                        int topcard = this.gameroomLocal.playedCards.get(totalAmountOfPlayedCards - 1);
                        this.gameroomLocal.playedCards.remove(totalAmountOfPlayedCards - 1);
                        this.gameroomLocal.playerHands.get(this.displayName).add(topcard);
                        displayAddedCardInHand(topcard);
                        Toast.makeText(getApplicationContext(), "Card added to hand", Toast.LENGTH_SHORT).show();
                        displayPlayedCards();
                        updateGameRoom();
                        this.lastClickedCard=null;
                        break;
                    case R.id.discardPlayedCardsMenuItem:
                        totalAmountOfPlayedCards = this.gameroomLocal.playedCards.size();
                        for (int i = 0; i < totalAmountOfPlayedCards; i++) {
                            this.gameroomLocal.playedCards.remove(0);
                        }
                        this.gameroomLocal.playedCards.add(99999);

                        displayPlayedCards();
                        updateGameRoom();
                        this.lastClickedCard=null;
                        break;
                    // we check if the selected item is one of the playernames

                    // we check if the selected item is one of the playernames
                    default:
                        int totalPlayerss = this.gameroomLocal.playerIDs.size();
                        for (int i = 0; i < totalPlayerss; i++) {
                            // gives the clicked on card to the selected player
                            if (menuItem.getTitle() == this.gameroomLocal.playerIDs.get(i)) {
                                giveCard(this.lastClickedCard.getId(), this.gameroomLocal.playerIDs.get(i));
                            }
                        }


                }
                return true;

            }
        }
        else{
            goBackForce();
            return true;
        }
        return true;
    }

}