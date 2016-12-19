package com.example.darvesh.splashscreen;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

import static android.R.attr.data;

public class EditMyAds extends AppCompatActivity {
    private ImageButton take_photo, gallery;
    private Button submit;
    private String product_name, location, price, category_val;
    private Uri file_uri;
    private EditText et_name, et_loc, et_price, et_category;
    private ProgressDialog progressDialog;
    Double lat, lon;
    LatLng location_points;

    String edit_ad_key;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_ads);

        getSupportActionBar().setTitle("Edit Item");

        progressDialog = new ProgressDialog(this);

        edit_ad_key = getIntent().getStringExtra("edit_item_key");

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("items");

        submit = (Button) findViewById(R.id.submit_ad);

        et_loc = (EditText) findViewById(R.id.location);
        et_price = (EditText) findViewById(R.id.item_price);
        et_name = (EditText) findViewById(R.id.product_name);
        et_category = (EditText) findViewById(R.id.select_category);

        et_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    //starting the google location place picker for easy choosing of location

                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(EditMyAds.this), 12);

                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        et_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a radio button alert dialog to select category

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

                progressDialog.setMessage("Updating data....");
                progressDialog.show();

                upload_data();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //receiving data from place picker intent

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

    public void upload_data() {

        //uploading data

        DatabaseReference id_db = databaseReference.child(edit_ad_key);

        if (!TextUtils.isEmpty(product_name))
            id_db.child("item").setValue(product_name);

        if (!TextUtils.isEmpty(price))
            id_db.child("price").setValue(price);

        if (!TextUtils.isEmpty(location)){
            id_db.child("latitude").setValue(lat);
            id_db.child("longitude").setValue(lon);
            id_db.child("location").setValue(location);
        }

        if(!TextUtils.isEmpty(category_val))
            id_db.child("category").setValue(category_val);

        et_loc.setText("");
        et_price.setText("");
        et_name.setText("");
        et_category.setText("");

        progressDialog.dismiss();

        setContentView(R.layout.activity_edit_my_ads);

    }


}
