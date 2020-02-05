package com.example.atplsdim;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InventoryInAdapter extends RecyclerView.Adapter<InventoryInViewHolder> {
    @NonNull
    @Override
    public InventoryInViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_inventory_in,parent,false);
        return new InventoryInViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryInViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

class InventoryInViewHolder extends RecyclerView.ViewHolder{

    TextView warehouse,rackNo,productSku;
    ImageView delete;

    public InventoryInViewHolder(@NonNull View itemView) {
        super(itemView);
        warehouse = itemView.findViewById(R.id.warehouse);
        rackNo = itemView.findViewById(R.id.rack);
        productSku = itemView.findViewById(R.id.sku);
        delete = itemView.findViewById(R.id.delete);
    }
}
