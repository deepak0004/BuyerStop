// Class used for chatting
package com.example.darvesh.splashscreen;

// Imports
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.Map;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;

public class Chat extends AppCompatActivity {

    // Firebase DataBase objects
    private DatabaseReference databaseReference, userNameRef;
    // Authentication objects
    private FirebaseAuth mAuth;
    EditText texx;
    DatabaseReference id_db;
    String u_id;
    TextView show_screen;
    String to_id;
    int t=0;
    String message="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        texx = (EditText) findViewById(R.id.message_text);
        // Fetching id from intent
        to_id = getIntent().getStringExtra("user_id");

        // Pointing to user's database
        userNameRef = FirebaseDatabase.getInstance().getReference().child("users").child(to_id);
        userNameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Storing key-value pairs
                Map<String, String> data = (Map) dataSnapshot.getValue();
                getSupportActionBar().setTitle(data.get("name"));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("chat");
        u_id = mAuth.getCurrentUser().getUid();

        Button send_message = (Button) findViewById(R.id.send_message);

        // Writing to database chat message
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference id2 = databaseReference.push();
                String mess = texx.getText().toString();
                id2.child("message").setValue(mess);
                id2.child("sender").setValue(u_id);
                id2.child("receiver").setValue(to_id);
                id2.child("time").setValue(ServerValue.TIMESTAMP);
                texx.setText("");
            }
        });

        // Loading in real time: intent values.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.chat_box);
                linearLayout.removeAllViewsInLayout();
                String m="";
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> data = (Map) postSnapshot.getValue();

                    String sender = (String) data.get("sender");
                    String receiver = (String) data.get("receiver");

                    if(!TextUtils.isEmpty(sender) && !TextUtils.isEmpty(receiver)){
                        if(sender.equals(u_id) && receiver.equals(to_id)){
                            String msg = (String) data.get("message");

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.gravity = Gravity.RIGHT;
                            params.setMargins(10, 10, 10, 10);

                            TextView newTv = new TextView(Chat.this);
                            newTv.setText(msg);
                            newTv.setTextSize(17);

                            newTv.setBackgroundColor(BLUE);
                            newTv.setTextColor(WHITE);
                            newTv.setPadding(25,10,25,10);

                            newTv.setLayoutParams(params);
                            linearLayout.addView(newTv);

                        }
                        if(sender.equals(to_id) && receiver.equals(u_id)){
                            String msg = (String) data.get("message");

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.gravity = Gravity.LEFT;
                            params.setMargins(10, 10, 10, 10);

                            TextView newTv = new TextView(Chat.this);
                            newTv.setText(msg);
                            newTv.setBackgroundColor(RED);
                            newTv.setTextColor(WHITE);
                            newTv.setTextSize(17);
                            newTv.setPadding(25,10,25,10);
                            newTv.setLayoutParams(params);
                            linearLayout.addView(newTv);

                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Menu item, takes to home to go back
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}