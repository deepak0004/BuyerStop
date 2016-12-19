// home screen-> recyclerview
package com.example.darvesh.splashscreen;

import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Darvesh on 27-Nov-16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecylerHolderClass> {

    ArrayList<String> item_list = new ArrayList<>();
    ArrayList<String> image_link = new ArrayList<>();
    ArrayList<String> price = new ArrayList<>();
    ArrayList<String> location = new ArrayList<>();

    public RecyclerViewAdapter(ArrayList<String> msg_list, ArrayList<String> image, ArrayList<String> p, ArrayList<String> l) {
        item_list = msg_list;
        image_link = image;
        price = p;
        location = l;
    }

    @Override
    public RecylerHolderClass onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_row_data, parent, false);
        return new RecylerHolderClass(view);
    }

    @Override
    public void onBindViewHolder(RecylerHolderClass holder, final int position) {
        holder.tv1.setText(item_list.get(position));
        holder.tv2.setText("Rs." + price.get(position));
        holder.tv3.setText(location.get(position));

        Picasso.with(holder.view.getContext()).load(image_link.get(position)).rotate(-90).resize(400,400).centerInside().into(holder.iv);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(view.getContext(), ProductPager.class);
                i.putExtra("item_position", position);
                view.getContext().startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return item_list.size();
    }

    public static class RecylerHolderClass extends RecyclerView.ViewHolder{
        TextView tv1, tv2, tv3;
        ImageView iv;
        public View view;
        public RecylerHolderClass(final View itemView) {
            super(itemView);
            tv1 = (TextView) itemView.findViewById(R.id.recyler_product_name);
            tv2 = (TextView) itemView.findViewById(R.id.recyler_price);
            iv = (ImageView) itemView.findViewById(R.id.recyler_image);
            tv3 = (TextView) itemView.findViewById(R.id.recyler_formatted_location_name);

            view = itemView;
        }
    }
}
