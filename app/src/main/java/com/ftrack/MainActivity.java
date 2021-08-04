package com.ftrack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static MainActivity mainActivity;
    private  FtrackLocationListener ftrackLocationListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        ftrackLocationListener = FtrackLocationListener.getInstance(this);

        ((Button) findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//              ((Button) findViewById(R.id.button2)).setText("Выкл");
                if(((Button) view).getText().equals("Вкл")) {
                    ((Button) view).setText("Выкл");
                    startService(new Intent(MainActivity.this, FtrackServiceTimer.class));
                } else if(((Button) view).getText().equals("Выкл")) {
                    ((Button) view).setText("Вкл");
                    stopService(new Intent(MainActivity.this, FtrackServiceTimer.class));
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        stopService(new Intent(MainActivity.this, FtrackServiceTimer.class));
        super.onDestroy();
    }
    public static MainActivity  getInstace(){
        return mainActivity;
    }


}