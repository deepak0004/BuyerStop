// Allow the class to post ads.
package com.example.darvesh.splashscreen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.category;
import static android.R.attr.data;
import static android.R.attr.id;
import static android.R.attr.name;
import static android.R.attr.password;
import static com.example.darvesh.splashscreen.R.id.imageView;
import static com.example.darvesh.splashscreen.R.id.r_email;
import static java.net.Proxy.Type.HTTP;


public class PostAd extends AppCompatActivity {

    private ImageButton take_photo, gallery;
    private Button submit;
    private String product_name, location, price, category_val;
    private Uri file_uri;
    private EditText et_name, et_loc, et_price, et_category;
    private ProgressDialog progressDialog;
    Double lat, lon;
    LatLng location_points;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_post);

        progressDialog = new ProgressDialog(this);

        getSupportActionBar().setTitle("Post Advertisement");

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("items");


        take_photo = (ImageButton) findViewById(R.id.take_photo);
        gallery = (ImageButton) findViewById(R.id.gallery_photo);
        submit = (Button) findViewById(R.id.submit_ad);

        et_loc = (EditText) findViewById(R.id.location);
        et_price = (EditText) findViewById(R.id.item_price);
        et_name = (EditText) findViewById(R.id.product_name);
        et_category = (EditText) findViewById(R.id.select_category);


        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, 10);
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, 10);
            }
        });
        // Dialog
        et_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(PostAd.this), 12);
                }
                catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
                catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        // Dialog
        et_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence options[] = new CharSequence[] {"Books", "Furniture", "Vehicle", "Technology", "Others"};
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Select a category");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        et_category.setText(options[which]);
                    }
                });
                builder.show();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product_name = et_name.getText().toString();
                location = et_loc.getText().toString();
                price = et_price.getText().toString();
                category_val = et_category.getText().toString();

                if(TextUtils.isEmpty(product_name) || TextUtils.isEmpty(location) || TextUtils.isEmpty(price)) {
                    Toast.makeText(getApplicationContext(), "All fields must be filled!", Toast.LENGTH_LONG).show();
                }else{

                    progressDialog.setMessage("Uploading Image....");
                    progressDialog.show();

                    upload_data();

                }

            }
        });
        if(savedInstanceState!=null){
            et_name.setText(savedInstanceState.getString("name"));
            et_loc.setText(savedInstanceState.getString("loc"));
            et_price.setText(savedInstanceState.getString("price"));
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        String a = et_name.getText().toString();
        String a3 = et_loc.getText().toString();
        String a2 = et_price.getText().toString();

        savedInstanceState.putString("name", a3);
        savedInstanceState.putString("loc", a);
        savedInstanceState.putString("price", a2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 10 && resultCode == RESULT_OK) {
            file_uri = data.getData();

            take_photo.setVisibility(View.GONE);
            gallery.setVisibility(View.GONE);

            LinearLayout ll = (LinearLayout) findViewById(R.id.ll_image);
            ll.getLayoutParams().height = 500;

            ImageView iv = new ImageView(PostAd.this);
            //iv.getLayoutParams().height = 100;
//            iv.getLayoutParams().width = 100;
            Picasso.with(this).load(file_uri).resize(800,400).centerInside().into(iv);
            //iv.setLayoutParams(new ViewGroup.LayoutParams(100, ViewGroup.LayoutParams.MATCH_PARENT));
            //iv.setImageResource(R.drawable.some_drawable_of_yours); //or iv.setImageDrawable(getResources().getDrawable(R.drawable.some_drawable_of_yours));
            ll.addView(iv);
        }

        if (requestCode == 12) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                et_loc.setText(place.getAddress());
                location_points = place.getLatLng();
                lat = location_points.latitude;
                lon = location_points.longitude;
            }
        }

    }

    public void upload_data(){
        StorageReference file_path = storageReference.child("photos").child(file_uri.getLastPathSegment());

        file_path.putFile(file_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                String image_uri = taskSnapshot.getDownloadUrl().toString();

                String u_id = mAuth.getCurrentUser().getUid();

                DatabaseReference id_db =  databaseReference.push();
                id_db.child("user_id").setValue(u_id);
                id_db.child("item").setValue(product_name);
                id_db.child("price").setValue(price);
                id_db.child("location").setValue(location);
                id_db.child("image").setValue(image_uri);
                id_db.child("category").setValue(category_val);
                id_db.child("latitude").setValue(lat);
                id_db.child("longitude").setValue(lon);

                progressDialog.dismiss();
                setContentView(R.layout.ad_post);

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(PostAd.this, MapsActivity.class);
        startActivity(intent);
    }


}
