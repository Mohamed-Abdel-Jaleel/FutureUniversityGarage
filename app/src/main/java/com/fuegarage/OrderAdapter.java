package com.fuegarage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<order> mOrderList = new ArrayList<>();
    private Context mContext ;

    public OrderAdapter(List<order> mOrderList, Context mContext) {
        this.mOrderList = mOrderList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_item ,null  , false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        OrderViewHolder orderViewHolder = new OrderViewHolder(view);
        return orderViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        order orderItem = mOrderList.get(position);
        Long start = orderItem.getStart();
        Long end = orderItem.getEnd();
        Long remain = end - System.currentTimeMillis();

        holder.carBlatteTextView.setText(orderItem.getCarBlatte());
        holder.carIdTextView.setText(orderItem.getCarID());
        holder.numOfHoursTextView.setText(""+orderItem.getNumOfHours()+"");

        holder.startTextView.setText(getDateCurrentTimeZone(orderItem.getStart()));
        holder.endTextView.setText(getDateCurrentTimeZone(orderItem.getEnd()));

        if(remain < 0){
            holder.currentState.setText("Your Order has Ended");
            holder.currentState.setTextColor(Color.RED);
        }
        else {
            holder.currentState.setText("Remaining Time : "+GetTimeAgo.getTimeLater(remain));
            holder.currentState.setTextColor(Color.GREEN);

            holder.orderConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( mContext , CurrentOrderActivity.class);
                    intent.putExtra("key" , mOrderList.get(position).getKey());
                    mContext.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public  String getDateCurrentTimeZone(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(new Date(timestamp));
        return dateString;
    }
    public class OrderViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout orderConstraintLayout;
        TextView carBlatteTextView , carIdTextView , startTextView , endTextView , numOfHoursTextView , currentState;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderConstraintLayout = itemView.findViewById(R.id.order_item_layout);
            carBlatteTextView = itemView.findViewById(R.id.car_blatte_txt_view);
            carIdTextView = itemView.findViewById(R.id.car_uid_txt_view);
            startTextView = itemView.findViewById(R.id.start_time_txt_view);
            endTextView = itemView.findViewById(R.id.end_time_txt_view);
            numOfHoursTextView = itemView.findViewById(R.id.num_of_hours_txt_view);
            currentState = itemView.findViewById(R.id.current_status_txt_view);
        }
    }
}
