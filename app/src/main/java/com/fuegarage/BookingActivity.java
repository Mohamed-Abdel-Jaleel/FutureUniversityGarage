package com.fuegarage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fuegarage.Fragments.MapsFragment;
import com.fuegarage.Fragments.OrderFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.fuegarage.MainActivity.carId;
import static com.fuegarage.MainActivity.fueRef;
import static com.fuegarage.MainActivity.myRef;

public class BookingActivity extends AppCompatActivity {

    EditText numberOfHourEditText ,carBlatteEditText , carIDEditText;
    TextView priceHoursTextView , remainingTextView;
    Long available = null;
    int balance = 0 , price=0 , remainingBalance = 0;
    int numOfHours = 0;
    private ProgressBar loadingProgress;
    final int[] length = new int[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        numberOfHourEditText = findViewById(R.id.number_hours_edit_text);
        priceHoursTextView = findViewById(R.id.price_hours_text_view);
        remainingTextView = findViewById(R.id.remaining_booking_txt_view);
        loadingProgress = findViewById(R.id.regProgressBar);
        loadingProgress.setVisibility(View.INVISIBLE);

        carBlatteEditText = findViewById(R.id.car_blatte_edittext);
        carIDEditText  =findViewById(R.id.car_id_edittext);

        ///////////////
        // Read wallet balance from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long value = dataSnapshot.child("wallet").getValue(Long.class);
                remainingTextView.setText(value+" EGP");
                try {
                    balance =Integer.parseInt(value.toString());
                }catch(NumberFormatException ex){
                    balance = 0 ;
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        //listen on edit text to determine the price
        numberOfHourEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                priceHoursTextView.setText(" 0 EGP");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.equals("")){
                    priceHoursTextView.setText(" 0 EGP");

                }else{
                    try{
                        numOfHours = Integer.parseInt(s.toString());
                    } catch(NumberFormatException ex){
                        numOfHours = 0 ;
                    }
                    price =numOfHours * 10;
                    remainingBalance = balance - price ;
                    priceHoursTextView.setText(price+" EGP");
                    remainingTextView.setText(remainingBalance+" EGP");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // get the count


        carId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                length[0] = (int)snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

            }
        });

        fueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long value = (Long) snapshot.getValue();
                available = value;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void openChoosePaymentActivity(View view) {
        Intent choosePaymentIntent = new Intent(this , ChossePaymentActivity.class);
        startActivity(choosePaymentIntent);
    }

    public void bookButtonPressed(View view) {

        String carID = carIDEditText.getText().toString();
        String carBlatte = carBlatteEditText.getText().toString();




        if (carID.length()<5){
            carIDEditText.setError("Enter Valid Car ID");
        }else if (carBlatte.length()<5){
            carBlatteEditText.setError("Enter Valid Car Blatte");
        }else if(remainingBalance<0){
            Toast.makeText(this, "You need to Recharge Your Balance", Toast.LENGTH_SHORT).show();
        }else if (numOfHours <1){
            numberOfHourEditText.setError("Enter number of Hours");
        }
        else {

            view.setVisibility(View.INVISIBLE);
            loadingProgress.setVisibility(View.VISIBLE);


            HashMap<String , Object> reserveObject = new HashMap<>();
            reserveObject.put("carBlatte", carBlatte);
            reserveObject.put("carID", carID);
            reserveObject.put("numOfHours", numOfHours);
            reserveObject.put("inGarage", false);
//            reserveObject.put("startTime" ,ServerValue.TIMESTAMP);

            Long start = System.currentTimeMillis();
            Long end = start +(numOfHours*3600000);
            reserveObject.put("start" ,start);
            reserveObject.put("order" ,-1*start);
            reserveObject.put("end" ,end);

            String index = "";
            index = ""+length[0];

            carId.child(index).setValue(carID);

            myRef.child("orders").push().setValue(reserveObject).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    myRef.child("wallet").setValue(remainingBalance);
                    fueRef.setValue((available-1));
                    Toast.makeText(BookingActivity.this, "Reservation Success", Toast.LENGTH_SHORT).show();

                    finish();
                }
            });
        }


    }
}