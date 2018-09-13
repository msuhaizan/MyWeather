package my.edu.utem.myweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //MyRecyclerViewAdapter adapter;
    EditText cityEditText;
    CustomAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityEditText = findViewById(R.id.editText);
        adapter = new CustomAdapter(getApplicationContext());
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvWeather);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        //recyclerView.setAdapter(adapter);
    }

    public void buttonPressed(View view)
    {
        //final TextView mTextView = (TextView) findViewById(R.id.tvWeather);
        String city = cityEditText.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        //String url ="https://api.openweathermap.org/data/2.5/forecast/daily?q=Melaka,My&appid=9fd7a449d055dba26a982a3220f32aa2";
        String url ="https://api.openweathermap.org/data/2.5/forecast/daily?q="+city+",My&appid=9fd7a449d055dba26a982a3220f32aa2";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("debug", response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray weatherArray = jsonObject.getJSONArray("list");
                            for (int i = 0; i < weatherArray.length(); i++){
                                adapter.addWeather(weatherArray.getJSONObject(i));
                            }
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /*@Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }*/

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView weatherTextView;
        TextView dateTextView;
        TextView tempTextView;

        public CustomViewHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.recyclerview_row, parent, false));
            imageView = itemView.findViewById(R.id.imageView);
            weatherTextView = itemView.findViewById(R.id.weatherTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            tempTextView = itemView.findViewById(R.id.tempTextView);
        }
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder>{
        List<JSONObject> weatherList = new ArrayList<>();
        Context context;

        public CustomAdapter (Context context){
            this.context = context;
        }


        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new CustomViewHolder(LayoutInflater.from(viewGroup.getContext()), viewGroup);
        }

        public void onBindViewHolder (CustomViewHolder holder, int position){
            JSONObject currentWeather = weatherList.get(position);
            try{
                long datetime = currentWeather.getInt("dt");
                Date date = new Date(datetime * 1000);
                holder.dateTextView.setText(date.toString());
                //Date date1=new SimpleDateFormat("dd/MM/yyyy").parse();
                //holder.dateTextView.setText(""+currentWeather.getInt("dt"));
                holder.tempTextView.setText(""+currentWeather.getJSONObject("temp").getDouble("day"));
                holder.weatherTextView.setText(""+currentWeather.getJSONArray("weather").getJSONObject(0).getString("main"));
                String iconId = currentWeather.getJSONArray("weather").getJSONObject(0).getString("icon");
                String iconURL = "https://openweathermap.org/img/w/"+iconId+".png";
                Glide.with(MainActivity.this).load(iconURL).into(holder.imageView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public int getItemCount(){
            return weatherList.size();
        }

        public void addWeather(JSONObject weather){
            weatherList.add(weather);
        }
    }
}