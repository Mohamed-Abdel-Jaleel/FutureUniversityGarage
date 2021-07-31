package com.fuegarage.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuegarage.OrderAdapter;
import com.fuegarage.R;
import com.fuegarage.order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.fuegarage.MainActivity.myRef;


public class OrderFragment extends Fragment {

    private List<order> mOrderList;
    private RecyclerView.LayoutManager mOrderListLayoutManger;
    private RecyclerView mOrderRecyclerView;
    private OrderAdapter orderAdapter;


    public OrderFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView(view);
        readOreders();
    }

    private void readOreders() {
        myRef.child("orders").orderByChild("order").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mOrderList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    order orderItem =dataSnapshot.getValue(order.class);
                    orderItem.setKey(key);
                    mOrderList.add(orderItem);
                    orderAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initRecyclerView(View view) {
        mOrderRecyclerView = view.findViewById(R.id.order_recycler_view);

        mOrderListLayoutManger = new LinearLayoutManager(getContext() , LinearLayoutManager.VERTICAL , false);
        mOrderListLayoutManger.isSmoothScrolling();

        mOrderRecyclerView.setLayoutManager(mOrderListLayoutManger);
        mOrderRecyclerView.setNestedScrollingEnabled(true);
        mOrderRecyclerView.setHasFixedSize(true);
        mOrderRecyclerView.clearOnScrollListeners();

        //call adapter
        mOrderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(mOrderList , getContext());
        mOrderRecyclerView.setAdapter(orderAdapter);
    }


}