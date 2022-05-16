package com.example.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        System.out.println(BTAdapter.getBondedDevices()); //print the paired device


        BluetoothDevice HC05 = BTAdapter.getRemoteDevice(""); //device mac address
        System.out.println(HC05.getName()); //get the name of the device

        BluetoothSocket BTSocket = null;
        int counter = 0;
        do {
            try {
                BTSocket = HC05.createInsecureRfcommSocketToServiceRecord(mUUID);
                System.out.println(BTSocket);
                BTSocket.connect();
                System.out.println(BTSocket.isConnected()); //see if connected with the server

            } catch (IOException e) {
                e.printStackTrace();
            }
            counter++;
        }while (!BTSocket.isConnected() && counter<3); //3 attempts to connect

        //send and receive data

        try {
            OutputStream outputStream = BTSocket.getOutputStream();
            outputStream.write(48); // send ASCII code value for character 0
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream inputStream = null;
        try {
            inputStream = BTSocket.getInputStream();
            inputStream.skip(inputStream.available());

            for(int i =0; i<26; i++){
                byte b = (byte) inputStream.read();
                System.out.println((char)b);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            BTSocket.close();
            System.out.println(BTSocket.isConnected()); //see if closed with the server
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}