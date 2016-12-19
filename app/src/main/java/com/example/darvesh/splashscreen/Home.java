package com.example.darvesh.splashscreen;

// Display Home sreen( display in arraylist )
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity {

    RecyclerView home_show_products;
    ArrayList<String> values = new ArrayList<>();
    ArrayList<String> msg_list = new ArrayList<>();
    ArrayList<String> item_image = new ArrayList<>();
    ArrayList<String> item_price = new ArrayList<>();
    ArrayList<String> loc_list = new ArrayList<>();
    private DatabaseReference item_list;
    String user_id;
    FirebaseAuth mAuth;


    RecyclerView.Adapter rc_adapter;
    RecyclerView.LayoutManager rc_lm;
    Button open_chat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        item_list = FirebaseDatabase.getInstance().getReference().child("items");
        home_show_products = (RecyclerView) findViewById(R.id.home_show_products);

        getSupportActionBar().setTitle("Home");

        open_chat = (Button) findViewById(R.id.open_chat_window);
        open_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, ChatMessages.class));
            }
        });

        item_list.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> data = (Map) postSnapshot.getValue();
                    String item_name = (String) data.get("item");
                    String image = (String) data.get("image");
                    String price = (String) data.get("price");
                    msg_list.add(item_name);
                    item_price.add(price);
                    item_image.add(image);
                    values.add(postSnapshot.getKey());

                    String location_name = (String) data.get("location");
                    String[] format_location = location_name.split(",");
                    String final_location = format_location[format_location.length-4];
                    loc_list.add(final_location);

                }

                //showing objects using a recyler view for better memory management
                rc_adapter = new RecyclerViewAdapter(msg_list, item_image, item_price, loc_list);
                home_show_products.setHasFixedSize(true);
                home_show_products.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                home_show_products.setAdapter(rc_adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
