// Loading the list who want to chat or have chatted
package com.example.darvesh.splashscreen;

// Imports
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

public class ChatMessages extends AppCompatActivity {

    ListView chat_view;
    ArrayList<String> values = new ArrayList<>();
    ArrayList<String> msg_list = new ArrayList<>();
    // DataBase
    private DatabaseReference chat_people, userNameRef;
    String user_id;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);

        getSupportActionBar().setTitle("Messages");

        mAuth = FirebaseAuth.getInstance();
        chat_people = FirebaseDatabase.getInstance().getReference().child("chat");

        chat_view = (ListView) findViewById(R.id.list_view);
        user_id = mAuth.getCurrentUser().getUid();

        chat_people.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> data = (Map) postSnapshot.getValue();

                    String sender = (String) data.get("sender");
                    String receiver = (String) data.get("receiver");

                    if(!TextUtils.isEmpty(sender))
                    if (sender.equals(user_id)) {
                        if(!values.contains(receiver)) {
                            values.add(receiver);

                            userNameRef = FirebaseDatabase.getInstance().getReference().child("users").child(receiver);

                            userNameRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Map<String, String> data2 = (Map) dataSnapshot.getValue();
                                    msg_list.add(data2.get("name"));
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ChatMessages.this, android.R.layout.simple_list_item_1, msg_list);
                                    chat_view.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    if(!TextUtils.isEmpty(receiver))
                    if (receiver.equals(user_id)) {
                        if(!values.contains(sender)) {
                            values.add(sender);
                            userNameRef = FirebaseDatabase.getInstance().getReference().child("users").child(sender);
                            userNameRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Map<String, String> data2 = (Map) dataSnapshot.getValue();
                                    msg_list.add(data2.get("name"));
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ChatMessages.this, android.R.layout.simple_list_item_1, msg_list);
                                    chat_view.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        chat_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(ChatMessages.this, Chat.class).putExtra("user_id", values.get(i)));
            }
        });
    }
}