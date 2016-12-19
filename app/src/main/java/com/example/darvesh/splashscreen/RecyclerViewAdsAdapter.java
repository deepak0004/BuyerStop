// myadd recyclerview
package com.example.darvesh.splashscreen;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.R.id.edit;

/**
 * Created by Darvesh on 28-Nov-16.
 */
public class RecyclerViewAdsAdapter extends RecyclerView.Adapter<RecyclerViewAdsAdapter.RecylerAdsHolderClass>{


    ArrayList<String> item_list = new ArrayList<>();
    ArrayList<String> image_link = new ArrayList<>();
    ArrayList<String> price = new ArrayList<>();
    ArrayList<String> keys = new ArrayList<>();

    public RecyclerViewAdsAdapter(ArrayList<String> msg_list, ArrayList<String> image, ArrayList<String> p, ArrayList<String> k) {
        item_list = msg_list;
        image_link = image;
        price = p;
        keys = k;
    }


    @Override
    public RecylerAdsHolderClass onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_ads_row, parent, false);
        return new RecyclerViewAdsAdapter.RecylerAdsHolderClass(view);
    }

    @Override
    public void onBindViewHolder(final RecylerAdsHolderClass holder, final int position) {
        holder.tv1.setText(item_list.get(position));
        holder.tv2.setText("Rs. "+price.get(position));

        holder.edit_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), EditMyAds.class);
                i.putExtra("edit_item_key", keys.get(position));
                view.getContext().startActivity(i);
            }
        });

        holder.delete_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("items");
                db.child(keys.get(position)).removeValue();
                Toast.makeText(view.getContext(), "Product Deleted!", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(view.getContext(), MyAds.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                view.getContext().startActivity(i);

            }
        });

        Picasso.with(holder.view.getContext()).load(image_link.get(position)).rotate(-90).resize(400,400).centerInside().into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return item_list.size();
    }


    public static class RecylerAdsHolderClass extends RecyclerView.ViewHolder{
        TextView tv1, tv2;
        ImageView iv;
        public View view;
        Button edit_ad, delete_ad;
        public RecylerAdsHolderClass(final View itemView) {
            super(itemView);
            tv1 = (TextView) itemView.findViewById(R.id.recyler_product_name);
            tv2 = (TextView) itemView.findViewById(R.id.recyler_price);
            edit_ad = (Button) itemView.findViewById(R.id.button_editAd);
            delete_ad = (Button) itemView.findViewById(R.id.button_deleteAd);
            iv = (ImageView) itemView.findViewById(R.id.recyler_image);
            view = itemView;
        }
    }

}
