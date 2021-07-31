package com.fuegarage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ChossePaymentActivity extends AppCompatActivity {

    ImageButton visaButton , masterButton;
    EditText amountChargeEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosse_payment);

        visaButton = findViewById(R.id.vise_btn);
        masterButton = findViewById(R.id.master_btn);
        amountChargeEditText = findViewById(R.id.amount_edit_text);

    }

    public void visaButtonPressed(View view) {
        visaButton.setSelected(true);
        masterButton.setSelected(false);
    }

    public void openVisaPage(View view) {
        String amount = amountChargeEditText.getText().toString();
        if(amount.matches("")){
            Toast.makeText(this, "Enter Value of Recharge Amount", Toast.LENGTH_SHORT).show();
            amountChargeEditText.setError("Enter Value of Recharge Amount");
        }else {
            int amountInt =Integer.parseInt(amount);
            if(amountInt <10 || amountInt>1000){
                Toast.makeText(this, "Value of Recharge Amount must be between 10 and 1000", Toast.LENGTH_SHORT).show();
                amountChargeEditText.setError("Value of Recharge Amount must be between 10 and 1000");
            }else{
                Intent visaIntent = new Intent(this , VisaActivity.class);
                visaIntent.putExtra("amount" , amount);
                startActivity(visaIntent);
                finish();
            }
        }
    }

    public void masterButtonPressed(View view) {
        masterButton.setSelected(true);
        visaButton.setSelected(false);
        masterButton.setSelected(true);
    }
}