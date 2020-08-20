package sg.edu.rp.c346.id19018582.smsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText etTo, etCtx;
    Button btnSend, btnSVM;
    private BroadcastReceiver mr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTo = findViewById(R.id.editTextTo);
        etCtx = findViewById(R.id.editTextContent);
        btnSend = findViewById(R.id.button);
        btnSVM = findViewById(R.id.buttonSVM);

        mr = new MessageReceiver();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(mr, filter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                SmsManager smsManager = SmsManager.getDefault();
                if(etTo.getText().toString().contains(",")){
                    String[] noArray = etTo.getText().toString().split(",");
                    for (int i=0; i<noArray.length; i++){
                        smsManager.sendTextMessage(noArray[i], null, etCtx.getText().toString(), null, null);
                    }
                }
                else{
                    smsManager.sendTextMessage(etTo.getText().toString(), null, etCtx.getText().toString(), null, null);
                }

                etCtx.setText("");
                Toast.makeText(MainActivity.this, "Message sent", Toast.LENGTH_LONG).show();
            }
        });

        btnSVM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + etTo.getText()));
                intent.putExtra("sms_body", etCtx.getText().toString());
                startActivity(intent);
                }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mr);
    }

    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);

        }
    }
}