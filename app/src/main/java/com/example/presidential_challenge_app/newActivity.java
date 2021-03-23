package com.example.presidential_challenge_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class newActivity extends AppCompatActivity {
    private List<ArrayList<Double>> mapRow;
    private ArrayList<Double> listInstance;
    private ArrayList<String> dataPoints;
    private JSONArray tryTHis = new JSONArray();
    private final String mapLink = "http://128.153.161.14/map_data.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONArray response = getJSONArrayResponse(queue);

        mapRow = new ArrayList<ArrayList<Double>>();

        JsonArrayRequest testJson = new JsonArrayRequest(Request.Method.GET, mapLink, null, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONArray response) {

                // Parse through JSONArray
                for (int i = 0; i < response.length(); i++){

                }

                for(int i = 0; i < response.length(); i++) {
                    listInstance = new ArrayList<Double>();
                    try {

                        // Convert JSON Array to ArrayList<String>
                        String responseString = response.get(i).toString();
                        dataPoints = new ArrayList<String>();
                        String dataValue = new String();
                        for (int index = 0; index < responseString.length(); index++) {
                            if (responseString.charAt(index) == ','){
                                dataPoints.add(dataValue);
                                dataValue = new String();
                            }
                            else if (responseString.charAt(index) == '"'
                                    || responseString.charAt(index) == '['
                                    || responseString.charAt(index) == ']') {
                                // Do nothing
                            }
                            else {
                                dataValue = dataValue + responseString.charAt(index);
                            }
                        }

//                        Log.d("Data PO: ", dataPoints.toString());

                        // Convert ArrayList<String> to ArrayList<Double>
                        for (String item : dataPoints) {
                            Double d = Double.valueOf(item);
                            listInstance.add(d);
                        }
//                        Log.d("listInstance: ", listInstance.toString());

                    }
                    catch (Exception exc){
                        Log.d("Exception: ", exc.toString());
                    }

                    // Add data to map
                    mapRow.add(listInstance);
                }
//
            mapRow.add(listInstance);
        }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "JSON request error occurred");
                Log.e("VOLLEY ERROR: ", error.toString());

            }
        });

        // Add the request to the RequestQueue.
        queue.add(testJson);


        for(int i = 0; i < response.length(); i++) {
                    listInstance = new ArrayList<Double>();
                    try {

                        // Convert JSON Array to ArrayList<String>
                        String responseString = response.get(i).toString();
                        dataPoints = new ArrayList<String>();
                        String dataValue = new String();
                        for (int index = 0; index < responseString.length(); index++) {
                            if (responseString.charAt(index) == ','){
                                dataPoints.add(dataValue);
                                dataValue = new String();
                            }
                            else if (responseString.charAt(index) == '"'
                                    || responseString.charAt(index) == '['
                                    || responseString.charAt(index) == ']') {
                                // Do nothing
                            }
                            else {
                                dataValue = dataValue + responseString.charAt(index);
                            }
                        }

//                        Log.d("Data PO: ", dataPoints.toString());

                        // Convert ArrayList<String> to ArrayList<Double>
                        for (String item : dataPoints) {
                            Double d = Double.valueOf(item);
                            listInstance.add(d);
                        }
//                        Log.d("listInstance: ", listInstance.toString());

                    }
                    catch (Exception exc){
                        Log.d("Exception: ", exc.toString());
                    }

                    // Add data to map
                    mapRow.add(listInstance);
        }

        Log.d("Map Data: ", mapRow.toString());

        // Create Grid Layout
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

    public JSONArray getJSONArrayResponse(RequestQueue queue) {
        final JSONArray[] returnedResponse = {new JSONArray()};
        JsonArrayRequest testJson = new JsonArrayRequest(Request.Method.GET, mapLink, null, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONArray response) {
//                Log.d("Response Returned",response.toString());
                tryTHis = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "JSON request error occurred");
                Log.e("VOLLEY ERROR: ", error.toString());

            }
        });

        // Add the request to the RequestQueue.
        queue.add(testJson);

        return tryTHis;
    }

    private void sharedResponse(String key, float response) {
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = m.edit();
        editor.putFloat(key, response);
        editor.commit();
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
