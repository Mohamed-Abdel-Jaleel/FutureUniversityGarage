package com.fuegarage.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fuegarage.ChossePaymentActivity;
import com.fuegarage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.fuegarage.MainActivity.myRef;

public class WalletFragment extends Fragment {


    Button openMapButton , rechargeBalanceButton;
    TextView balanceTextView;
    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    String userUid ;
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            userUid = currentUser.getUid();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        balanceTextView = view.findViewById(R.id.wallet_fragment_balance);


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long v = (Long) dataSnapshot.child("wallet").getValue();
//                String value = dataSnapshot.child("wallet").getValue(String.class);
                balanceTextView.setText(""+v);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        openMapButton = view.findViewById(R.id.open_map_btn);
        rechargeBalanceButton = view.findViewById(R.id.recharge_balance_btn);

        openMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.container , new MapsFragment()).commit();
            }
        });

        rechargeBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent choosePaymentIntent = new Intent(getActivity() , ChossePaymentActivity.class);
                startActivity(choosePaymentIntent);

            }
        });

    }
}