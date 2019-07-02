package com.example.nicolas4.nicolasminesweeper;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class game_screen extends AppCompatActivity {
    Gson gson = new Gson();
    int amt = 10;
    int row = 11;
    int col = 10;
    int mine[][] = new int[row][col];
    int show[][] = new int[row][col];
    int flags[][] = new int[row][col];
    boolean flag = false;
    boolean lost=false;
    boolean won = false;
    Chronometer chronometer;
    long timeStopped=0;
    ImageView pics[] = new ImageView[row * col];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        startTime();
        randBomb();
        neighbours();
        printmap();
        //MediaPlayer ring= MediaPlayer.create(MainActivity.this,R.raw.ring);
        //ring.start();
    }

    public void saveClick (View view) {
        //if you lost or won the game, you cannot save
        if (lost == true) {
            Toast.makeText(getApplicationContext(), "Unable to save lost games", Toast.LENGTH_SHORT).show();
        }
        else if(won == true) {
            Toast.makeText(getApplicationContext(), "Unable to save a game that has been won", Toast.LENGTH_SHORT).show();
        }
        //otherwise save the game
        else {
            Toast.makeText(getApplicationContext(), "Game has been saved", Toast.LENGTH_SHORT).show();
            saveGame();
        }
    }

    //saves the data of the array
    public void saveGame () {
        Context context = this;
        File path = context.getFilesDir();
        File minefile = new File(path, "mine.txt");
        File showfile = new File(path, "show.txt");
        File flagsfile = new File(path, "flags.txt");
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(minefile);
            stream.write(gson.toJson(mine).getBytes());
            stream.close();
            stream = new FileOutputStream(showfile);
            stream.write(gson.toJson(show).getBytes());
            stream.close();
            stream = new FileOutputStream(flagsfile);
            stream.write(gson.toJson(flags).getBytes());
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //loads the data that was saved into the array
    public void loadClick (View view) {
        if (lost == true) {
            Toast.makeText(getApplicationContext(), "Unable to load during a lost game", Toast.LENGTH_SHORT).show();
        }
        else if(won == true) {
            Toast.makeText(getApplicationContext(), "Unable to load during a game that has been won", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Game Loaded", Toast.LENGTH_SHORT).show();
            loadGame();
        }
    }

    public void loadGame () {
        Context context = this;
        File path = context.getFilesDir();
        //Opens the 3 files from the arrays
        File minefile = new File(path, "mine.txt");
        File showfile = new File(path, "show.txt");
        File flagsfile = new File(path, "flags.txt");
        //Gets the length of each file
        int minelength = (int) minefile.length();
        int showlength = (int) showfile.length();
        int flagslength = (int) flagsfile.length();
        //Creates byte array for each save type
        byte[] minebytes = new byte[minelength];
        byte[] showbytes = new byte[showlength];
        byte[] flagsbytes = new byte[flagslength];
        //Loads the file contents into byte arrays
        FileInputStream in;
        try {
            in = new FileInputStream(minefile);
            in.read(minebytes);
            in = new FileInputStream(showfile);
            in.read(showbytes);
            in = new FileInputStream(flagsfile);
            in.read(flagsbytes);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Converts byte arrays back into strings
        String minestring = new String(minebytes);
        String showstring = new String(showbytes);
        String flagsstring = new String(flagsbytes);
        //Converts the strings into arrays
        mine = gson.fromJson(minestring, int[][].class);
        show = gson.fromJson(showstring, int[][].class);
        flags = gson.fromJson(flagsstring, int[][].class);
        redraw();
    }
    //starts the time for the game
    public void startTime() {
        chronometer = (Chronometer) findViewById(R.id.time);
        chronometer.setBase(SystemClock.elapsedRealtime()+timeStopped);
        chronometer.start();
    }
    //prints the grid for the screen to load
    public void printmap() {
        GridLayout g = (GridLayout) findViewById(R.id.grid);
        int m = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                pics[m] = new ImageView(this);
                pics[m].setImageResource(R.drawable.m10);
                pics[m].setId(m);
                g.addView(pics[m]);
                pics[m].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gridButtonClick(v.getId());
                    }
                });
                m++;
            }
        }
    }

    public void randBomb() {
        for (int i = 0; i < amt; i++) {
            int x = (int) (Math.random() * row);
            int y = (int) (Math.random() * col);
            //guarantees that there will always be a certain number of bombs
            while (mine[x][y] == 9) {
                x = (int) (Math.random() * row);
                y = (int) (Math.random() * col);
            }
            mine[x][y] = 9;
        }
    }

    public void minecount () {
        //if a flag is placed on the bomb then the number of flags goes down by one
        TextView minenum1 = (TextView) findViewById(R.id.minenum);
        int count = 0;
        //tests every spot in the grid to see if there is a flag in it
        for (int x = 0; x < row; x++) {
            for (int y = 0; y < col; y++) {
                if (flags[x][y] == 1) {
                    count++;
                }
            }
        }
        int totalmines = amt - count;
        if (totalmines < 0)
            totalmines = 0;
        minenum1.setText ("Mines: " + totalmines);
    }

    public void flagClick(View view) {
        Button flag1 = (Button) findViewById(R.id.flag);
        if (flag == true) {
            flag = false;
            flag1.setText("No Flag");
        } else {
            flag = true;
            flag1.setText("Flag");
        }
    }

    public void lose() {
        Toast.makeText(getApplicationContext(), "You lose", Toast.LENGTH_SHORT).show();
        lost=true;
        //if you click on a square that has a mine on it and is not flagged then you lose
        for (int x = 0; x < row; x++) {
            for (int y = 0; y < col; y++) {
                if (mine[x][y] == 9) {
                    show[x][y] = 1;
                    flags[x][y] = 0;
                }
            }
        }
        timeStopped=0;
        chronometer.stop();
    }

    public void neighbours() {
        for (int x = 0; x < row; x++) {
            for (int y = 0; y < col; y++) {
                try {
                    if (mine[x][y] != 9) {
                        int count = 0;
                        //top left corner
                        if (x - 1 >= 0 && y - 1 >= 0 && mine[x - 1][y - 1] == 9)
                            count++;
                        //top center
                        if (x - 1 >= 0 && mine[x - 1][y] == 9)
                            count++;
                        //top right corner
                        if (x - 1 >= 0 && y + 1 < col && mine[x - 1][y + 1] == 9)
                            count++;
                        //middle row left
                        if (y - 1 >= 0 && mine[x][y - 1] == 9)
                            count++;
                        //middle row right
                        if (y + 1 < col && mine[x][y + 1] == 9)
                            count++;
                        //bottom left corner
                        if (x + 1 < row && y - 1 >= 0 && mine[x + 1][y - 1] == 9)
                            count++;
                        //bottom center
                        if (x + 1 < row && mine[x + 1][y] == 9)
                            count++;
                        //bottom right corner
                        if (x + 1 < row && y + 1 < col && mine[x + 1][y + 1] == 9)
                            count++;
                        mine[x][y] = count;
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();

                    e.printStackTrace();
                }
            }
        }
    }

    //all the actions
    public void gridButtonClick(int pos) {
        if(!lost) {
            int x = pos / col;
            int y = pos % col;
            //TO DO: code as needed to process a button click
            Log.d("asdf",String.format("%d, %d", x, y));
            if (flags[x][y] == 1)
                flags[x][y] = 0;
            else if (flag == true && show[x][y]==0) {
                flags[x][y] = 1;
            } else if(show[x][y]==0){
                show[x][y] = 1;
                if (!flag) {
                    if (mine[x][y] == 9)
                        lose();
                    else
                        open(x, y);
                }
            }
            redraw();
            minecount();
            if(win()){
                Toast.makeText(getApplicationContext(), "Win!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void resetClick(View view) {
        //the grid should be blank again
        //the bombs should be randomized again
        int m = 0;
        lost = false;
        won = false;
        for (int x = 0; x < row; x++) {
            for (int y = 0; y < col; y++) {
                pics[m].setImageResource(R.drawable.m10);
                show[x][y] = 0;
                flags[x][y] = 0;
                mine[x][y] = 0;
            }
        }
        timerReset();
        TextView minenum1 = (TextView) findViewById(R.id.minenum);
        minenum1.setText("Mines: " + amt);
        randBomb();
        neighbours();
        redraw();
        saveGame();

    }

    //stops the timer and restarts it
    public void timerReset () {
        timeStopped=0;
        chronometer.stop();
        chronometer.setText("00:00");
        chronometer = (Chronometer) findViewById(R.id.time);
        chronometer.setBase(SystemClock.elapsedRealtime() + timeStopped);
        chronometer.start();
    }

    //brings you to the instructions screen from the game screen
    public void bInsClick (View view) {
        Intent i = new Intent (this,instructions.class);
        startActivity (i);
    }

    //to win, the user has to flag all of the bombs
    public boolean win(){

        //returns true if user has won, false otherwise
        int count = 0;
        int count1 = 0;
        int count2 = 0;
        int box = (row * col)-amt;
        for(int x = 0; x<row; x++){
            for(int y=0; y<col; y++){
                //if there's a mine with a flag on it and the mine is not clicked
                if (flags[x][y]==1 && mine[x][y] == 9 && show[x][y] == 0)
                    count++;
                //if there are extra flags you cannot win
                if (flags[x][y]==1 && mine[x][y] != 9 && show[x][y] == 0)
                    count1++;
                //if certain boxes are not clicked you cannot win
                if (show[x][y] == 1)
                    count2++;
            }
        }
        //made it all the way through, no errors
        //win if all the bombs are flagged and all the squares without bombs are clicked on
        if (count == amt && count1 == 0 && count2 == box) {
            //stops the timer when you win
            won = true;
            timeStopped=0;
            chronometer.stop();
            return true;
        }
        else
            return false;
    }

    //when you click it
    public void open(int x, int y) {
        Log.d("asdf",String.format("Tried to open %d, %d", x, y));
        if (mine[x][y] != 0) {
            Log.d("tracing", "mine[x][y] != 0");
            return;
        }
        //top left corner
        try {
            if (x - 1 >= 0 && y - 1 >= 0 && show[x - 1][y - 1] == 0) {
                Log.d("tracing", "x - 1 >= 0 && y - 1 >= 0 && show[x - 1][y - 1] == 0");

                show[x - 1][y - 1] = 1;
                if (mine[x - 1][y - 1] == 0) {
                    Log.d("tracing", "mine[x - 1][y - 1] == 0");
                    open(x - 1, y - 1);
                }
            }
            //top center
            if (x - 1 >= 0 && show[x - 1][y] == 0) {
                Log.d("tracing", "(x - 1 >= 0 && show[x - 1][y] == 0");

                show[x - 1][y] = 1;
                if (mine[x - 1][y] == 0)
                    open(x - 1, y);
            }
            //top right corner
            if (x - 1 >= 0 && y + 1 < col && show[x - 1][y + 1] == 0) {
                Log.d("tracing", "(x - 1 >= 0 && y + 1 < col && show[x - 1][y + 1] == 0");

                show[x - 1][y + 1] = 1;
                if (mine[x - 1][y + 1] == 0)
                    open(x - 1, y + 1);
            }
            //middle row left
            if (y - 1 >= 0 && show[x][y - 1] == 0) {
                Log.d("tracing", "(y - 1 >= 0 && show[x][y - 1] == 0)");
                show[x][y - 1] = 1;
                Log.d("tracing", "no exception");

                if (mine[x][y - 1] == 0)
                    open(x, y - 1);
            }
            //middle row right
            if (y + 1 < col && show[x][y + 1] == 0) {
                Log.d("tracing", "y + 1 < col && show[x][y + 1] == 0");

                show[x][y + 1] = 1;
                if (mine[x][y + 1] == 0)
                    open(x, y + 1);
            }
            //bottom left corner
            if (x + 1 < row && y - 1 >= 0 && show[x + 1][y - 1] == 0) {
                Log.d("tracing", "x + 1 < row && y - 1 >= 0 && show[x + 1][y - 1] == 0");

                show[x + 1][y - 1] = 1;
                if (mine[x + 1][y - 1] == 0)
                    open(x + 1, y - 1);
            }
            //bottom center
            if (x + 1 < row && show[x + 1][y] == 0) {
                Log.d("tracing", "x + 1 < row && show[x + 1][y] == 0)");

                show[x + 1][y] = 1;
                if (mine[x + 1][y] == 0)
                    open(x + 1, y);
            }
            //bottom right corner
            if (x + 1 < row && y + 1 < col && show[x + 1][y + 1] == 0) {
                Log.d("tracing", "(x + 1 < row && y + 1 < col && show[x + 1][y + 1] == 0)");

                show[x + 1][y + 1] = 1;
                if (mine[x + 1][y + 1] == 0)
                    open(x + 1, y + 1);
            }
        } catch (Exception e) {
             Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void redraw() {
        int m = 0;
        for (int x = 0; x < row; x++) {
            for (int y = 0; y < col; y++) {
                if (show[x][y] == 0 && flags[x][y] == 0)
                    pics[m].setImageResource(R.drawable.m10);
                else if (flags[x][y] == 1)
                    pics[m].setImageResource(R.drawable.m11);
                else if (show[x][y] == 1 && flags[x][y] == 0 && mine[x][y] == 0)
                    pics[m].setImageResource(R.drawable.m0);
                else if (show[x][y] == 1 && flags[x][y] == 0 && mine[x][y] == 1)
                    pics[m].setImageResource(R.drawable.m1);
                else if (show[x][y] == 1 && flags[x][y] == 0 && mine[x][y] == 2)
                    pics[m].setImageResource(R.drawable.m2);
                else if (show[x][y] == 1 && flags[x][y] == 0 && mine[x][y] == 3)
                    pics[m].setImageResource(R.drawable.m3);
                else if (show[x][y] == 1 && flags[x][y] == 0 && mine[x][y] == 4)
                    pics[m].setImageResource(R.drawable.m4);
                else if (show[x][y] == 1 && flags[x][y] == 0 && mine[x][y] == 5)
                    pics[m].setImageResource(R.drawable.m5);
                else if (show[x][y] == 1 && flags[x][y] == 0 && mine[x][y] == 6)
                    pics[m].setImageResource(R.drawable.m6);
                else if (show[x][y] == 1 && flags[x][y] == 0 && mine[x][y] == 7)
                    pics[m].setImageResource(R.drawable.m7);
                else if (show[x][y] == 1 && flags[x][y] == 0 && mine[x][y] == 8)
                    pics[m].setImageResource(R.drawable.m8);
                else if (show[x][y] == 1 && flags[x][y] == 0 && mine[x][y] == 9)
                    pics[m].setImageResource(R.drawable.m9);
                m++;
            }
        }
    }
}

