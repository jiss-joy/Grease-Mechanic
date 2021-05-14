package com.rsa.greasemechanic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.rsa.greasemechanic.Models.ModelStaff;
import com.rsa.greasemechanic.R;

import java.util.ArrayList;

public class AdapterStaff extends RecyclerView.Adapter<AdapterStaff.MyViewHolder> {

    private Context mContext;
    private ArrayList<ModelStaff> staffs;
    private OnStaffClickListener onStaffClickListener;

    public AdapterStaff(Context mContext, ArrayList<ModelStaff> staffs, OnStaffClickListener onStaffClickListener) {
        this.mContext = mContext;
        this.staffs = staffs;
        this.onStaffClickListener = onStaffClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterStaff.MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_mini_staff_card, parent, false), onStaffClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(mContext).load(staffs.get(position).getStaffImage()).into(holder.proPic);
        holder.name.setText(staffs.get(position).getStaffName());
        holder.dob.setText(staffs.get(position).getStaffDOB());

        holder.workExp.setText(staffs.get(position).getStaffExperience() + " Years Experience");
    }

    @Override
    public int getItemCount() {
        return staffs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircularImageView proPic;
        private TextView name, dob, workExp;

        public MyViewHolder(@NonNull View itemView, OnStaffClickListener onStaffClickListener) {
            super(itemView);

            proPic = itemView.findViewById(R.id.item_mini_staff_card_pro_pic);
            name = itemView.findViewById(R.id.item_mini_staff_card_name);
            dob = itemView.findViewById(R.id.item_mini_staff_card_dob);
            workExp = itemView.findViewById(R.id.item_mini_staff_card_workExp);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onStaffClickListener.OnStaffClick(getAdapterPosition(), v);
        }
    }

    public interface OnStaffClickListener {
        void OnStaffClick(int position, View view);
    }
}
