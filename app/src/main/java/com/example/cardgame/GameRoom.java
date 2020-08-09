package com.example.cardgame;

import java.util.ArrayList;
import java.util.HashMap;

public class GameRoom {

    // the name of this gameRoom
    public String roomName;
    // "playerName" -> cards in current hand
    public HashMap<String, ArrayList<Integer>> playerHands;
    // list of current players
    public ArrayList<String> playerIDs;
    // cards that are played by the players
    public ArrayList<Integer> playedCards;
    // cards that are still in the deck
    public ArrayList<Integer> deck;





    public GameRoom(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public GameRoom(String roomName, HashMap<String, ArrayList<Integer>> playerHands, ArrayList<String> playerIDs,
                    ArrayList<Integer> playedCards, ArrayList<Integer> deck ) {
        this.roomName = roomName;
        this.playerHands = playerHands;
        this.playerIDs = playerIDs;
        this.playedCards = playedCards;
        this.deck = deck;
    }


}
