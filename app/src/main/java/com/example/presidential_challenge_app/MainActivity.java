package com.example.presidential_challenge_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import java.util.*; // Needed for list creation and operations

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Create server listener

        // TODO implement with actual server generated map
        // Get List object from server object
        List<ArrayList<Double>> mapRow = new ArrayList<ArrayList<Double>>();
        Random rand = new Random();
        // Create random data in range 0 to 1
        for(int i = 0; i < 256; i++) {
            ArrayList<Double> listInstance = new ArrayList<Double>();
            for (int j = 0; j < 256; j++) {  // Create random numbers to store in nested list
              Double nextDouble = new Double(rand.nextDouble());
              listInstance.add(nextDouble);
            }
            mapRow.add(listInstance);
        }

        // TODO Display testing map
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
}