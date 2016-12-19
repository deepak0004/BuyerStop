package com.example.darvesh.splashscreen;

// Activity to show seller profile

import android.content.Intent;
import android.media.Rating;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.data;
import static android.R.attr.rating;
import static com.example.darvesh.splashscreen.R.id.ratingBar;
import static com.example.darvesh.splashscreen.R.id.seller_rating_show;
import static com.example.darvesh.splashscreen.R.id.show_rating;

public class ShowSeller extends AppCompatActivity {

    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private RatingBar rbar;
    String seller_id = "hjbMa6pJNHORYj743qWeRFyx3a43";

    private TextView seller_name, seller_rating, show_rating_text;
    private CircleImageView sellerImage;
    private String user_id, key_rating;
    int equal_flag = 0;
    int people_rated;
    Float rating_give;
    Button send_messsage_to_seller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_seller);

        seller_id = getIntent().getExtras().getString("ID_SELLER");

        //Firebase auth object
        mAuth = FirebaseAuth.getInstance();

        //Firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(seller_id);

        rbar = (RatingBar) findViewById(R.id.give_rating);
        seller_name = (TextView) findViewById(R.id.seller_name);
        seller_rating = (TextView) findViewById(R.id.seller_rating_show);
        show_rating_text = (TextView) findViewById(R.id.textView5);
        sellerImage = (CircleImageView) findViewById(R.id.seller_profile_image);
        send_messsage_to_seller = (Button) findViewById(R.id.send_messsage_to_seller);

        user_id = mAuth.getCurrentUser().getUid();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> map = (Map) dataSnapshot.getValue();
                String a = (String) map.get("name");
                String b = (String) map.get("image");
                getSupportActionBar().setTitle(a);
                float rt = Float.parseFloat((String) map.get("rating"));
                seller_rating.setText("Rating: "+rt);
                seller_name.setText(a);
                show_rating_text.setText("Rate " + a);
                rating_give = rt;

                people_rated = Integer.parseInt ((String) map.get("people_rated"));


                if(!map.get("image").equals("")){
                    Picasso.with(ShowSeller.this).load(b).rotate(-90).into(sellerImage);
                }

                send_messsage_to_seller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ShowSeller.this, Chat.class).putExtra("user_id" ,seller_id));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                final String rateValue = String.valueOf(rbar.getRating());

                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("ratings");

                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            Map<String, Object> data = (Map) postSnapshot.getValue();

                            String giver = (String) data.get("give_rating");
                            String taker = (String) data.get("take_rating");

                            if(!TextUtils.isEmpty(giver) && !TextUtils.isEmpty(taker)){
                                if(giver.equals(user_id) && taker.equals(seller_id)){
                                    equal_flag = 1;
                                    key_rating = postSnapshot.getKey();
                                    break;
                                }
                            }
                        }

                        if(equal_flag==1){

//                            DatabaseReference new_db =  FirebaseDatabase.getInstance().getReference().child("ratings").child(key_rating);
//                            new_db.child("rating").setValue(rateValue);
                            Toast.makeText(getApplicationContext(), "Already Rated", Toast.LENGTH_SHORT).show();
//
//                            databaseReference.child("people_rated").setValue(Integer.toString(people_rated+1));
//
//                            Float f = (people_rated)*rating_give + Float.parseFloat(rateValue);
//                            Float r = f/(people_rated+1);
//
//                            databaseReference.child("rating").setValue(Float.toString(r));



                        }else{

                            DatabaseReference db2 = FirebaseDatabase.getInstance().getReference().child("ratings").push();
                            db2.child("give_rating").setValue(user_id);
                            db2.child("take_rating").setValue(seller_id);
                            db2.child("rating").setValue(rateValue);

                            databaseReference.child("people").setValue(Integer.toString(people_rated+1));

                            Float f = (people_rated)*rating_give + Float.parseFloat(rateValue);
                            Float r = f/(people_rated+1);

                            Float ftl = (float) Math.round(r * 100) / 100;

                            databaseReference.child("rating").setValue(Float.toString(ftl));

                            Toast.makeText(getApplicationContext(), "Rated Successfully!", Toast.LENGTH_SHORT).show();
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }
}
