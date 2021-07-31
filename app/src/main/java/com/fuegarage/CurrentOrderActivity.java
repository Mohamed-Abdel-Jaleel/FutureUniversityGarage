package com.fuegarage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.fuegarage.MainActivity.fueRef;
import static com.fuegarage.MainActivity.myRef;

public class CurrentOrderActivity extends AppCompatActivity {

    TextView carBlatteTextView , carIdTextView , startTextView , endTextView , numOfHoursTextView , currentState , moneyBackTextView;
    private ProgressBar loadingProgress;

    Long remain = 0L , userMoney , moneyBack ,available;
    String key;

    Boolean isInGarage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order);
        loadingProgress = findViewById(R.id.cancelProgressBar);
        loadingProgress.setVisibility(View.INVISIBLE);
        carBlatteTextView =findViewById(R.id.current_car_blatte_txt_view);
        carIdTextView =findViewById(R.id.current_car_uid_txt_view);
        startTextView =findViewById(R.id.current_start_time_txt_view);
        endTextView =findViewById(R.id.current_end_time_txt_view);
        numOfHoursTextView =findViewById(R.id.current_num_of_hours_txt_view);
        currentState =findViewById(R.id.current_activity_current_status_txt_view);

        moneyBackTextView =findViewById(R.id.current_money_back);

        key = getIntent().getStringExtra("key");

        myRef.child("orders").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                order orderItem =snapshot.getValue(order.class);
                Long start = orderItem.getStart();
                Long end = orderItem.getEnd();
                remain = end - System.currentTimeMillis();
                moneyBack = (remain/3600000)*10;
                carBlatteTextView.setText(orderItem.getCarBlatte());
                carIdTextView.setText(orderItem.getCarID());
                numOfHoursTextView.setText(""+orderItem.getNumOfHours()+"");

                startTextView.setText(getDateCurrentTimeZone(orderItem.getStart()));
                endTextView.setText(getDateCurrentTimeZone(orderItem.getEnd()));

                currentState.setText("Remaining Time : "+GetTimeAgo.getTimeLater(remain));
                currentState.setTextColor(Color.GREEN);

                moneyBackTextView.setText((remain/3600000)*10+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        myRef.child("orders").child(key).child("inGarage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isInGarage = (Boolean) snapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public  String getDateCurrentTimeZone(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(new Date(timestamp));
        return dateString;
    }

    public void cancelOrderButtonPressed(View view) {

        if(!isInGarage){
            view.setVisibility(View.INVISIBLE);
            loadingProgress.setVisibility(View.VISIBLE);

            long futureValue = moneyBack+userMoney;
            myRef.child("wallet").setValue(futureValue)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                }
            });
            myRef.child("orders").child(key).child("end").setValue(System.currentTimeMillis())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    fueRef.setValue((available+1));
                    finish();
                }
            });

        }else{
            Toast.makeText(this, "cant cancel will still in garage", Toast.LENGTH_SHORT).show();
        }
    }
}