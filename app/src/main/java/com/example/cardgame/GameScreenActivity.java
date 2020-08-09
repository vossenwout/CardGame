package com.example.cardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class GameScreenActivity extends AppCompatActivity {


    private ImageView iv_card1;
    private ArrayList<Integer> cards;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ChildEventListener mListener;
    private FirebaseAuth mFirebaseauth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        // DATABASE INIT
        database = FirebaseDatabase.getInstance();
        mFirebaseauth = FirebaseAuth.getInstance();

        if (mFirebaseauth.getCurrentUser() != null){
            //user already signed in
            System.out.println("pookie");
            FirebaseUser user = mFirebaseauth.getCurrentUser();
            onSignedInInitialize(user);
        }
        // game logic init



        iv_card1 = (ImageView)findViewById(R.id.iv_card1);


        cards = new ArrayList<>();
        // hearts
        cards.add(101);
        cards.add(102);
        cards.add(103);
        cards.add(104);
        cards.add(105);
        cards.add(106);
        cards.add(107);
        cards.add(108);
        cards.add(109);
        cards.add(110);
        cards.add(111);
        cards.add(112);
        cards.add(113);
        // diamonds
        cards.add(201);
        cards.add(202);
        cards.add(203);
        cards.add(204);
        cards.add(205);
        cards.add(206);
        cards.add(207);
        cards.add(208);
        cards.add(209);
        cards.add(210);
        cards.add(211);
        cards.add(212);
        cards.add(213);
        // spades
        cards.add(301);
        cards.add(302);
        cards.add(303);
        cards.add(304);
        cards.add(305);
        cards.add(306);
        cards.add(307);
        cards.add(308);
        cards.add(309);
        cards.add(310);
        cards.add(311);
        cards.add(312);
        cards.add(313);
        // clubs
        cards.add(401);
        cards.add(402);
        cards.add(403);
        cards.add(404);
        cards.add(405);
        cards.add(406);
        cards.add(407);
        cards.add(408);
        cards.add(409);
        cards.add(410);
        cards.add(411);
        cards.add(412);
        cards.add(413);

        iv_card1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                database = FirebaseDatabase.getInstance();
                //myRef = database.getReference("/games");



                Collections.shuffle(cards);
                int card = cards.get(0);
                assignCards(card, iv_card1);
            }
        });

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

    public void goBack(View view){

        // we remove the user from the online users database
        FirebaseUser user = mFirebaseauth.getCurrentUser();
        DatabaseReference userRef = database.getReference().child("OnlineUsers").child(user.getUid());
        userRef.removeValue();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onSignedInInitialize(FirebaseUser user){
        // in OnlineUsers directory maken we een key-value paar met als key de userId
        DatabaseReference userRef = database.getReference().child("OnlineUsers").child(user.getUid());
        userRef.setValue(user.getEmail());
        //userRef.push().setValue("hello");
    }


}