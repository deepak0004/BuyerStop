// showing profile of person
package com.example.darvesh.splashscreen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Map;

import static com.example.darvesh.splashscreen.R.drawable.gallery;

public class ProfileActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    TextView show_name, show_rating;
    ImageView profileImage;
    private RatingBar ratingBar;
    Button log_me_out, my_ads;
    int check = 0;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        log_me_out = (Button) findViewById(R.id.log_me_out);
        my_ads = (Button) findViewById(R.id.show_my_ads);
        show_name = (TextView) findViewById(R.id.load_name);
        show_rating = (TextView) findViewById(R.id.show_rating);
        profileImage = (ImageView) findViewById(R.id.profile_image);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        log_me_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

        my_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, MyAds.class));
            }
        });


        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        //Log.d( "TAG", mAuth.getCurrentUser().getUid() );

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                             //DatabaseReference db = databaseReference.child(mAuth.getCurrentUser().getUid());
                    Map<String, Object> map = (Map) dataSnapshot.getValue();

                    String a = (String) map.get("name");
                    String b = (String) map.get("image");

                    getSupportActionBar().setTitle(a);

                    float rt = Float.parseFloat((String) map.get("rating"));
                    ratingBar.setRating(rt);

                    show_rating.setText(""+rt);

                    show_name.setText(a);

                    if(!map.get("image").equals("")){
                        Picasso.with(ProfileActivity.this).load(b).rotate(-90).into(profileImage);
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.edit_profile:
                startActivity(new Intent(ProfileActivity.this, EditActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }
}
