package com.example.nicolas4.nicolasminesweeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void tInsClick (View view) {
        Intent i = new Intent (this,instructions.class);
        startActivity(i);
    }

    public void toGame (View view) {
        Intent i = new Intent (this,game_screen.class);
        startActivity(i);
    }

}
