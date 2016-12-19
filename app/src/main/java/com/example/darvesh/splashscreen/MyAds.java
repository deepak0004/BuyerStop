package com.example.darvesh.splashscreen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import static com.example.darvesh.splashscreen.R.id.home_show_products;
import static com.example.darvesh.splashscreen.R.id.item_price;

public class MyAds extends AppCompatActivity {

    RecyclerView.Adapter rc_adapter;
    RecyclerView.LayoutManager rc_lm;
    RecyclerView rv_my_ads;
    private DatabaseReference items;

    ArrayList<String> msg_list = new ArrayList<>();
    ArrayList<String> item_image = new ArrayList<>();
    ArrayList<String> item_price = new ArrayList<>();
    ArrayList<String> key_list = new ArrayList<>();

    String user_id;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);

        items = FirebaseDatabase.getInstance().getReference().child("items");
        rv_my_ads = (RecyclerView) findViewById(R.id.recyler_my_ads);

        firebaseAuth = FirebaseAuth.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        getSupportActionBar().setTitle("My Products");

        items.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> data = (Map) postSnapshot.getValue();

                    String get_user_id = (String) data.get("user_id");

                    if(user_id.equals(get_user_id)){
                        String item_name = (String) data.get("item");
                        String image = (String) data.get("image");
                        String price = (String) data.get("price");

                        msg_list.add(item_name);
                        item_price.add(price);
                        item_image.add(image);
                        key_list.add(postSnapshot.getKey());
                    }
                }

                rc_adapter = new RecyclerViewAdsAdapter(msg_list, item_image, item_price, key_list);
                rv_my_ads.setHasFixedSize(true);
                rv_my_ads.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rv_my_ads.setAdapter(rc_adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
