package com.example.darvesh.splashscreen;

// Imports
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.R.attr.button;
import static com.google.android.gms.common.api.Status.we;

public class EditActivity extends AppCompatActivity {

    private ImageButton take_photo, gallery;

    private ProgressDialog progressDialog;
    private Uri file_uri;
    Button change_name_btn;

    TextView show_name, show_rating;
    EditText change_name;
   // ImageView profileImage;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        getSupportActionBar().setTitle("Edit Profile");

        take_photo = (ImageButton) findViewById(R.id.profile_photo);
        gallery = (ImageButton) findViewById(R.id.profile_gallery);
        //profileImage = (ImageView) findViewById(R.id.profile_image);
        change_name_btn = (Button) findViewById(R.id.change_name_btn);
        change_name = (EditText) findViewById(R.id.change_my_name);

        progressDialog = new ProgressDialog(this);


        //Fireabase Authentication objects
        mAuth = FirebaseAuth.getInstance();

        //Fireabase Storage objects
        storageReference = FirebaseStorage.getInstance().getReference();

        //Fireabase Databsbe objects
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());


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
                i.setType("image/*");            // which type of data: image of all types
                startActivityForResult(i, 10);   // when it comes back we receive 10 back
            }
        });

        change_name_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String new_name = change_name.getText().toString();
                if(!TextUtils.isEmpty(new_name)){
                    databaseReference.child("name").setValue(new_name);
                    Toast.makeText(EditActivity.this, "Name Changed!", Toast.LENGTH_SHORT).show();
                    change_name.setText("");
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //method to get back Result from Camera/Gallery intent
        if (requestCode == 10 && resultCode == RESULT_OK) {
            progressDialog.setMessage("Uploading....");
            progressDialog.show();
            file_uri = data.getData();
            upload_data();
        }
    }

    public void upload_data(){
        //Obtaining path of image captured, and then pushing it to firebase storage, using firebase storage object
        StorageReference file_path = storageReference.child("photos").child(file_uri.getLastPathSegment());
        file_path.putFile(file_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {   // Uploading on the storage
                String image_uri = taskSnapshot.getDownloadUrl().toString();
                databaseReference.child("image").setValue(image_uri);
                //Picasso.with(EditActivity.this).load(file_uri).into(profileImage);
                progressDialog.dismiss();
                Toast.makeText(EditActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
