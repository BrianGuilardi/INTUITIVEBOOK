package com.example.intuitivebook;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.telephony.SmsManager;
import android.util.Log;
//http://www.tutorialspoint.com/android/android_sending_sms.htm

/**
 * Created by dmitridimov on 12/21/14.
 */

public class SMS extends Activity
{
    private String phone = "";
    private EditText smsMessage = null;
    private String myMessage = "";
    private Button sendMessange = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagingclient);
        phone = getIntent().getExtras().getString("messageTo");
        smsMessage = (EditText) findViewById(R.id.editText);
        sendMessange = (Button) findViewById(R.id.button);
        sendMessange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMessage = smsMessage.getText().toString();
                try
                {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone, null, myMessage, null, null);
                    Toast.makeText(getApplicationContext(), "Message sent.",
                            Toast.LENGTH_LONG).show();
                }
                catch(Exception e)
                {
                    Log.i("SMS Failure","Could Not Deliver Your Message");
                }
            }
        });
    }
}
