package com.example.presidential_challenge_app;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class newActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        // TODO Create server listener

        // TODO implement with actual server generated map
        // Get List object from server object
        List<ArrayList<Double>> mapRow = new ArrayList<ArrayList<Double>>();
        Random rand = new Random();
        // Create random data in range 0 to 1
        for(int i = 0; i < 25; i++) { // TODO make index based on actual data
            ArrayList<Double> listInstance = new ArrayList<Double>();
            for (int j = 0; j < 25; j++) {  // Create random numbers to store in nested list
                Double nextDouble = new Double(rand.nextDouble());
                listInstance.add(nextDouble);
            }
            mapRow.add(listInstance);
        }

//         Display testing map
//        for (ArrayList<Double> item : mapRow) {
//            for (Double number : item) {
//                Log.d("List number: ", String.valueOf(number));
////                System.out.printf("%f ", number);
//            }
//        }

        // Create Grid Layout
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        LinearLayout mainLay = (LinearLayout) findViewById(R.id.mainLayout);


        for (ArrayList<Double> item : mapRow) {
            LinearLayout squares = new LinearLayout(this);
            squares.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < mapRow.get(0).size(); j++) {
                ImageView newImage = new ImageView(this);

                if (item.get(j) < 0.5) {
                    newImage.setImageResource(R.drawable.ic_stop_24px);
                    newImage.setOnClickListener(new ViewButtonHandler(mapRow.indexOf(item), j, false));
                }
                else {
                    newImage.setImageResource(R.drawable.red_square);
                    newImage.setOnClickListener(new ViewButtonHandler(mapRow.indexOf(item), j, true));
                }

                squares.addView(newImage);
            }
            mainLay.addView(squares);
        }

    }

    /**
     * Custom handler to handle view details button click.
     */
    private class ViewButtonHandler implements View.OnClickListener
    {
        /**
         * The productID this listener is associated with.
         */
        private int point_x;
        private int point_y;
        private boolean occupied;

        /**
         * Constructor for ViewButtonHandler class.
         *
         * @param point_x_coordinate the x coordinate value of the selected square.
         * @param point_y_coordinate the y coordinate value of the selected square.
         * @param occupied the boolean that indicates whether the selected square is occupied or not
         */
        public ViewButtonHandler(int point_x_coordinate, int point_y_coordinate, boolean occupied)
        {
            point_x = point_x_coordinate;
            point_y = point_y_coordinate;
            this.occupied = occupied;
        }

        /**
         * Handler for button press.
         *
         * @param v The current view.
         */
        public void onClick(View v)
        {
            // Check for valid selection
            if (occupied){
                Log.d("Invalid Selection","Square represent occupied region");
            }
            else {
                Log.d("Valid Selection","Square selected represents unoccupied region");
            }
            // if valid display point location
        }
    }  // End of class ViewButtonHandler

}
