// After pressing marker displaying products
package com.example.darvesh.splashscreen;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

import static android.R.attr.data;
import static android.R.attr.id;
import static android.R.attr.name;
import static com.example.darvesh.splashscreen.R.id.location;
import static com.example.darvesh.splashscreen.R.id.map;

public class ProductDisplay extends AppCompatActivity {

    String item_id, user_id;
    TextView tv_title, tv_price, tv_loc, tv_seller;
    private DatabaseReference databaseReference, userNameRef;
    ImageView productImage;
    Button chat_btn, show_dir_btn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_products);

        item_id = getIntent().getStringExtra("item_key");
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("items").child(item_id);

        tv_title = (TextView) findViewById(R.id.load_product_name);
        tv_loc = (TextView) findViewById(R.id.load_product_user_address);
        tv_price = (TextView) findViewById(R.id.load_product_price);
        tv_seller = (TextView) findViewById(R.id.load_product_user_name);
        productImage = (ImageView) findViewById(R.id.load_product_image);
        chat_btn = (Button) findViewById(R.id.load_send_message);
        show_dir_btn = (Button) findViewById(R.id.load_direction);


        databaseReference.addValueEventListener(new ValueEventListener() {
            Map<String, Object> data;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                data = (Map) dataSnapshot.getValue();

                tv_title.setText((String)data.get("item"));

                getSupportActionBar().setTitle((String)data.get("item"));
                //getSupportActionBar().setHomeButtonEnabled(true);

                tv_loc.setText("Location: " + (String)data.get("location"));
                tv_price.setText("₹"+ (String)data.get("price"));

                user_id = (String)data.get("user_id");

                if(!data.get("image").equals("")){
                    Picasso.with(ProductDisplay.this).load((String)data.get("image")).rotate(-90).resize(300,300).centerInside().into(productImage);
                }

                if(user_id.equals(mAuth.getCurrentUser().getUid())){
                    chat_btn.setVisibility(View.GONE);
                }

                userNameRef = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);

                userNameRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, String> data2 = (Map) dataSnapshot.getValue();
                        tv_seller.setText("Seller: "+data2.get("name"));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                // display direction on map
                show_dir_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "http://maps.google.com/maps?daddr=" + Double.toString((Double)data.get("latitude")) + "," + Double.toString((Double)data.get("longitude"));
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);

                    }
                });
                // if pressed open that activity
                tv_seller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if( user_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ){
                            Intent in = new Intent(view.getContext(), ProfileActivity.class);
                            startActivity(in);
                        }
                        else {
                            Intent inte = new Intent(view.getContext(), ShowSeller.class).putExtra("ID_SELLER", user_id);
                            startActivity(inte);
                        }
                    }
                });


                chat_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ProductDisplay.this, Chat.class).putExtra("user_id" ,user_id));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
