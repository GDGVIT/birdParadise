package com.example.dell.birdingsimplified.Adapters;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.birdingsimplified.Models.NearbyBirdsModel;
import com.example.dell.birdingsimplified.Models.NearbyBirdsRecyclerModel;
import com.example.dell.birdingsimplified.NearbyBirds;
import com.example.dell.birdingsimplified.R;

import java.util.List;


public class NearbyBirdsRecyclerAdapter extends RecyclerView.Adapter<NearbyBirdsRecyclerAdapter.BirdHolder> {

    List<NearbyBirdsRecyclerModel> list;
    Context context;

    public NearbyBirdsRecyclerAdapter(List<NearbyBirdsRecyclerModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public NearbyBirdsRecyclerAdapter.BirdHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_nearby_birds, parent, false);
        BirdHolder birdHolder = new BirdHolder(view);
        return birdHolder;
    }

    @Override
    public void onBindViewHolder(NearbyBirdsRecyclerAdapter.BirdHolder holder, int position) {

        NearbyBirdsRecyclerModel model = list.get(position);
        holder.birdName.setText(model.getName());
        holder.birdLoc.setText(model.getLocation());
        holder.birdColor.setText(model.getColor());

    }

    @Override
    public int getItemCount() {
        int arr = 0;
        try{
            if(list.size()==0){
                arr = 0;
            }
            else{
                arr=list.size();
            }
        }catch (Exception e){

        }
        return arr;
    }

    public class BirdHolder extends RecyclerView.ViewHolder {

        TextView birdName, birdLoc, birdColor;

        public BirdHolder(View itemView) {
            super(itemView);
            birdName = (TextView)itemView.findViewById(R.id.birdName);
            birdLoc = (TextView)itemView.findViewById(R.id.birdLocation);
            birdColor = (TextView)itemView.findViewById(R.id.birdcolor);
        }
    }
}
