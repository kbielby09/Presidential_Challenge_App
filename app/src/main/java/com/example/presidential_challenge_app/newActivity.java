package com.example.presidential_challenge_app;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class newActivity extends AppCompatActivity {
    private ArrayList<Double> listInstance;
    private ArrayList<String> dataPoints;
    private final String mapLink = "http://128.153.161.14/map_data.php";
    private OkHttpClient client;
    private WebSocket ws;
    private static int currentPositionX = 3;
    private static int currentPositionY = 3;
    private static int previousPositionX;
    private static int previousPositionY;
    private LinearLayout mainLay;
    private Context mainThread;
    public ArrayList<ArrayList<Double>> mapRow = new ArrayList<ArrayList<Double>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        // Set Context
        mainThread = this;

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        client = new OkHttpClient();
        start();

        // Make JSON Array request
        JsonArrayRequest testJson = new JsonArrayRequest(Request.Method.GET, mapLink, null, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONArray response) {
//                ArrayList<ArrayList<Double>> mapRow = new ArrayList<ArrayList<Double>>();

                for(int i = 0; i < response.length(); i++) {
                    listInstance = new ArrayList<Double>();
                    dataPoints = new ArrayList<String>();
                    try {
                        // Convert JSON Array to ArrayList<String>
                        String responseString = response.get(i).toString();

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

                        // Convert ArrayList<String> to ArrayList<Double>
                        for (String item : dataPoints) {
                            Double d = Double.valueOf(item);
                            listInstance.add(d);
                        }

                        mapRow.add(listInstance);

                    }
                    catch (Exception exc){
                        Log.d("Exception: ", exc.toString());
                    }

                }
                mainLay = (LinearLayout) findViewById(R.id.mainLayout);

                updateGUI(mapRow);
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
    }

    private void updateGUI(ArrayList<ArrayList<Double>> mapRow){

        for (ArrayList<Double> item : mapRow) {
            LinearLayout squares = new LinearLayout(this);
            squares.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < item.size(); j++) {
                ImageView newImage = new ImageView(this);
                String id = String.valueOf(mapRow.indexOf(item)).concat(String.valueOf(j));
                newImage.setId(Integer.valueOf(id));

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
                // TODO propagate error message
            }
            else {
//                Log.d("Valid Selection","Square selected represents unoccupied region");

                JSONObject sentPosition = new JSONObject();
                try {
                    sentPosition.put("data_type", "start");
                    JSONObject startPoint = new JSONObject();
                    startPoint.put("x", point_x);
                    startPoint.put("y", point_y);
                    sentPosition.put("start_point", startPoint);
//                    Log.d("Test sentPoint: ", sentPosition.toString());

                    JSONObject endPointValues = new JSONObject();
                    endPointValues.put("x", point_x);
                    endPointValues.put("y", point_y);
                    sentPosition.put("end_point", endPointValues);
//                    Log.d("Test sentPoint: ", sentPosition.toString());
                    ws.send(sentPosition.toString());
                }
                catch (Exception exc){
                    Log.d("Error: start position", exc.toString());
                }

            }
        }
    }  // End of class ViewButtonHandler

    private final class EchoWebSocketListener extends okhttp3.WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
//            Log.d("Response Received", response.toString());
           // webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !");
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onMessage(WebSocket webSocket, String text) {

            try{
                JSONObject receivedData = new JSONObject(text);
                Object data_type = receivedData.get("data_type");

                if (data_type.toString().compareTo("path_data") == 0) {
                    Object path_data = receivedData.get("data");
//                    Log.d("path data:", path_data.toString());
                    String pathPoints = path_data.toString();
                    ArrayList<ArrayList<Integer>> path = new ArrayList<>();
                    ArrayList<Integer> point = new ArrayList<>();
                    String Number = " ";
                    for (int i = 1; i < pathPoints.length() - 1; i++){

                        if (pathPoints.charAt(i) == '['){
                            point = new ArrayList<>();
                            Number = "";
                        }
                        else if(pathPoints.charAt(i) == ']') {
                            if (Number.length() != 0) {
                                point.add(Integer.valueOf(Number, 10));
                                Number = "";
                            }
                            path.add(point);
                        }
                        else if(pathPoints.charAt(i) == ','){
                            if (Number.length() != 0) {
                                point.add(Integer.valueOf(Number, 10));
                            }
                            Number = "";
                        }
                        else {
                            Number += pathPoints.charAt(i);
//                            point.add(Character.getNumericValue(pathPoints.charAt(i)));
                        }
                    }
                    Log.d("Path points", path.toString());
                    setMapPath(path);
                }
                else if (data_type.toString().compareTo("position") == 0) {
                    Object xValue = receivedData.get("x");
                    Object yValue = receivedData.get("y");
                    currentPositionX = (Integer) xValue;
                    currentPositionY = (Integer) yValue;
                    Log.d("xValue", String.valueOf(currentPositionX));
                    Log.d("yValue", String.valueOf(currentPositionY));
                    String id = String.valueOf(currentPositionY).concat(String.valueOf(currentPositionX));
                    ImageView poisiton = (ImageView) findViewById(Integer.valueOf(id));
                    poisiton.setImageResource(R.drawable.position_marker);

                    // reset previous position
                    id = String.valueOf(previousPositionY).concat(String.valueOf(previousPositionX));
                    poisiton = (ImageView) findViewById(Integer.valueOf(id));
                    poisiton.setImageResource(R.drawable.ic_stop_24px);
                    previousPositionX = currentPositionX;
                    previousPositionY = currentPositionY;
                }
            }
            catch (Exception exc){
                Log.d("Exception occurred: ", exc.toString());
            }
        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            Log.d("Message Received: ", bytes.toString());
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            Log.d("Failure occurred", t.toString());
        }
    }

    private void setMapPath(ArrayList<ArrayList<Integer>> path) {
        Log.d("Path", path.toString());
        for (ArrayList<Integer> item : path) {
            String pointString = String.valueOf(item.get(0)).concat(String.valueOf(item.get(1)));
            ImageView pathPoint = (ImageView) findViewById(Integer.valueOf(pointString));
            pathPoint.setImageResource(R.drawable.path_marker);
        }

    }

    private void start() {
        okhttp3.Request request = new okhttp3.Request.Builder().url("ws://128.153.161.14:9001").build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

}
