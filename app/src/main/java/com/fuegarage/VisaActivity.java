package com.fuegarage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;
import com.fuegarage.Fragments.MapsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.fuegarage.MainActivity.myRef;

public class VisaActivity extends AppCompatActivity {
    Long  userMoney ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visa);

        String amount = getIntent().getStringExtra("amount");
        CardForm cardForm = findViewById(R.id.cardform);

        TextView cardTxt =findViewById(R.id.payment_amount);
        cardTxt.setText(amount+ "  EGP");

        Button payBtn = findViewById(R.id.btn_pay);
        payBtn.setText("Recharge with "+cardTxt.getText() );

        EditText name_pay = cardForm.findViewById(R.id.card_name);
        EditText card_number = cardForm.findViewById(R.id.card_number);


        ///////
        myRef.child("wallet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long userCurrentMoney = (Long) dataSnapshot.getValue();
                userMoney = userCurrentMoney;

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        Long moneyBack = Long.parseLong(amount);

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name_pay.getText().toString().equals("")){
                    name_pay.setError("Enter Card Name");
                }else if(card_number.getText().toString().equals("")){
                    name_pay.setError("Enter Card Number");
                }else {
                    long futureValue = moneyBack+userMoney;
                    myRef.child("wallet").setValue(futureValue)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    finish();
                                }
                            });
                }

            }
        });
    }
}