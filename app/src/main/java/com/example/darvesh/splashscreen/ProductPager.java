// home screen view pager, recycler view
package com.example.darvesh.splashscreen;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import static android.R.attr.data;
import static android.R.attr.id;
import static com.example.darvesh.splashscreen.R.id.image;
import static com.example.darvesh.splashscreen.R.id.item_price;
import static com.google.android.gms.internal.zznu.im;
import static java.lang.System.load;

public class ProductPager extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    int get_val;
    private DatabaseReference databaseReference, userNameRef;
    FirebaseAuth mAuth;
    String user_id;
    String item_id;

    ArrayList<String> items_list = new ArrayList<String>();
    ArrayList<String> price_list = new ArrayList<>();
    ArrayList<String> image_item_list = new ArrayList<>();
    ArrayList<String> loc_list = new ArrayList<>();
    ArrayList<String> id_list = new ArrayList<>();
    ArrayList<String> seller_name = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_pager);

        mViewPager = (ViewPager) findViewById(R.id.product_show_pager);
        get_val = getIntent().getExtras().getInt("item_position");

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("items");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Map<String, Object> data = (Map) postSnapshot.getValue();

                    String item_name = (String) data.get("item");
                    String image = (String) data.get("image");
                    String price = (String) data.get("price");
                    String loc = (String) data.get("location");
                    String ids = (String) data.get("user_id");

                    items_list.add(item_name);
                    price_list.add(price);
                    image_item_list.add(image);
                    loc_list.add(loc);
                    id_list.add(ids);


                }
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), items_list, price_list, image_item_list, loc_list, id_list);
                mViewPager.setAdapter(mSectionsPagerAdapter);
                mViewPager.setCurrentItem(get_val);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }


    public static class PlaceholderFragment extends Fragment {
        String id;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.product_fragment, container, false);

            TextView tv_title = (TextView) rootView.findViewById(R.id.load_product_name);
            TextView tv_loc = (TextView) rootView.findViewById(R.id.load_product_user_address);
            TextView tv_price = (TextView) rootView.findViewById(R.id.load_product_price);
            final TextView tv_seller = (TextView) rootView.findViewById(R.id.load_product_user_name);
            ImageView productImage = (ImageView) rootView.findViewById(R.id.load_product_image);
            Button chat_btn = (Button) rootView.findViewById(R.id.load_send_message);
            Button show_dir_btn = (Button) rootView.findViewById(R.id.load_direction);

            Bundle bundle = getArguments();

            String i = bundle.getString("get_value_item");
            String p = bundle.getString("get_value_price");
            String im = bundle.getString("get_value_image");
            final String loc = bundle.getString("get_value_loc");
            id = bundle.getString("get_value_id");

            if( id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ) {
                chat_btn.setVisibility(View.GONE);
            }

            tv_title.setText(i);
            tv_loc.setText("Location: " + loc);
            tv_price.setText("Rs. " + p);


            DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference().child("users").child(id);

            userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, String> data2 = (Map) dataSnapshot.getValue();
                    tv_seller.setText("Seller: "+data2.get("name"));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            tv_seller.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ){
                        Intent in = new Intent(view.getContext(), ProfileActivity.class);
                        startActivity(in);
                    }
                    else {
                        Intent inte = new Intent(view.getContext(), ShowSeller.class).putExtra("ID_SELLER", id);
                        startActivity(inte);
                    }
                }
            });

            chat_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(view.getContext(), Chat.class).putExtra("user_id", id));
                }
            });

            show_dir_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uri = "http://maps.google.com/maps?daddr=" + loc;
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);

                }
            });

            Picasso.with(rootView.getContext()).load(im).rotate(-90).fit().into(productImage);

            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<String> item_list, price_list, image_list, loc_list, seller_list, id_list;

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<String> t, ArrayList<String> a, ArrayList<String> b, ArrayList<String> c, ArrayList<String> d) {
            super(fm);
            item_list = t;
            price_list = a;
            image_list = b;
            loc_list = c;
            id_list = d;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = new PlaceholderFragment();
            Bundle bundle = new Bundle();
            bundle.putString("get_value_item", item_list.get(position));
            bundle.putString("get_value_price", price_list.get(position));
            bundle.putString("get_value_image", image_list.get(position));
            bundle.putString("get_value_loc", loc_list.get(position));
            bundle.putString("get_value_id", id_list.get(position));
            f.setArguments(bundle);
            return f;
        }

        @Override
        public int getCount() {
            return item_list.size();
        }
    }
}