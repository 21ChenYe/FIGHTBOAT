package com.example.battleship;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameState {
    public String player1_board;
    public String player2_board;
    private String gameID;
    public boolean player_turn; //Player 1 when 0, player 2 when 1
    private boolean which_player;
    private DatabaseReference mDatabase;

    //Need to implement a way to choose which player
    GameState(){
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.gameID = mDatabase.push().getKey();
        player1_board = "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        player2_board = "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        player_turn = false;

        //Push game to database
        mDatabase.child(gameID).setValue(this);
    }

    //Database Access//
    public void pushGameState(){
        mDatabase.child(gameID).setValue(this);
    }


    //I think querying has to be done inside of an activity rather than inside of this class
    //This code is a listener for database changes

    /*
    ref.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {}

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            Post changedPost = dataSnapshot.getValue(Post.class);
            System.out.println("The updated post title is: " + changedPost.title);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {}

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

        @Override
        public void onCancelled(DatabaseError databaseError) {}
    });
    */

    //Getters
    public String getGameID() {
        return gameID;
    }

    public boolean getWhich_player() {return which_player; }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }
}
