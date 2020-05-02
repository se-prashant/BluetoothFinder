package com.example.bluetoothfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    TextView statusTextView;
    Button searchButton;
    ArrayList<String> devicesData = new ArrayList<>();
    ArrayList<String> addresses = new ArrayList<>();
    ArrayAdapter adapter;

    BluetoothAdapter bluetoothAdapter;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("Action", action);

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                statusTextView.setText("Finished");
                searchButton.setEnabled(true);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress();
                String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
                //Log.i("Device Found","Name"+name+" Address:"+address+" RSSI"+ rssi);

                if (!addresses.contains(address)) {
                    Log.i("device", address);
                    addresses.add(address);
                    String bluetoothDevice = "";
                    if (name == null || name.equals("")) {
                        bluetoothDevice = address + ",  RSSI " + rssi + "dBm";
                    } else {
                        bluetoothDevice = name + ", RSSI " + rssi + "dBm";
                    }

                    Log.i("bli", bluetoothDevice);
                    devicesData.add(bluetoothDevice);
                    adapter.notifyDataSetChanged();
                }
            }
        }

    };

        public void setSearchButton(View view) {
            statusTextView.setText("Searching...");
            searchButton.setEnabled(false);
            devicesData.clear();
            addresses.clear();
            bluetoothAdapter.startDiscovery();
            int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            listView = findViewById(R.id.listView);
            statusTextView = findViewById(R.id.searchTextView);
            searchButton = findViewById(R.id.searchButton);

            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, devicesData);

            listView.setAdapter(adapter);

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(broadcastReceiver, intentFilter);


        }
}

