package com.example.matrixui;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainView extends SurfaceView implements SurfaceHolder.Callback{

    private MainThread thread;
    private LedSprite ledTest;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final UUID BtModuleUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Set<BluetoothDevice> pairedDevices;
    private ArrayList<SingleRow> pairedDevicesList;
    private BluetoothDevice[] btArray;
    private ClientClass clientClass;
    private CustomAdapter customAdapter;
    private LedMatrix matrix;
    private ArrayList<Led> _Leds;


    public MainView(Context context){
        super(context);
        getHolder().addCallback(this);
        this.setOnTouchListener(handleTouch);
        thread = new MainThread(getHolder(),this);
        setFocusable(true);

    }


    public void update(){

        matrix.updateLeds();

    }



    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);
        if(canvas != null){
            canvas.drawColor(Color.rgb(214,214,214));
            matrix.draw(canvas);


        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

        matrix = new LedMatrix(new int[] {45,330},new int[] {width-45,330+width-45});
        _Leds = matrix.getLeds();

    }




    @Override
    public void surfaceCreated(SurfaceHolder holder){

        if(!bluetoothAdapter.isEnabled()){
            bluetoothAdapter.enable();

            while(!bluetoothAdapter.isEnabled()){
            }
        }

        if(bluetoothAdapter.isEnabled()){


            Toast.makeText(getContext(),"Bluetooth Adapter is enabled",Toast.LENGTH_SHORT).show();

            pairedDevices = bluetoothAdapter.getBondedDevices();
            pairedDevicesList = new ArrayList<>(pairedDevices.size());
            String[] strings = new String[pairedDevices.size()];
            btArray = new BluetoothDevice[pairedDevices.size()];
            int index = 0;

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {

                    btArray[index] = device;
                    String deviceName = device.getName();
                    strings[index] = deviceName;
                    pairedDevicesList.add(new SingleRow(deviceName,device.getAddress()));
                    index++;

                }
            }

            customAdapter = new CustomAdapter(getContext(),pairedDevicesList);

            listPairedDevices(findViewById(android.R.id.content));




        }else{
            Toast.makeText(getContext(),"Exiting app because bluetooth not enabled",Toast.LENGTH_SHORT).show();
            System.exit(0);


        }


        thread = thread.getState().equals(Thread.State.BLOCKED) ? thread : new MainThread(getHolder(),this);
        thread.setRunning(true);
        thread.start();


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        if(bluetoothAdapter.isEnabled()){
            bluetoothAdapter.disable();
        }

        boolean retry = true;
        while(retry){
            try{
                thread.setRunning(false);
                thread.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            retry = false;
        }

    }

    public void listPairedDevices(View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.row_item,null);
        ListView li = (ListView)row.findViewById(R.id.listView);
        li.setAdapter(customAdapter);


        builder.setView(row);
        final AlertDialog dialog = builder.create();
        dialog.show();


        li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clientClass = new ClientClass(btArray[position]);
                clientClass.start();
                Toast.makeText(getContext(),"Connecting to "+btArray[position]+"\n",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }


    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int x = (int) event.getRawX();
            int y = (int) event.getRawY();

            switch (event.getAction()) {


                case MotionEvent.ACTION_DOWN:

                    for(Led _led:_Leds){
                        _led.setOnLed(false);
                    }

                  break;

                case MotionEvent.ACTION_MOVE:
                    int i = 0;

                    for(Led _led: _Leds) {

                        if (x > _led.getX() && x < _led.getWidth() && y > _led.getY() && y < _led.getHeight()) {

                            //this case is used to check if finger is on the led, and if it is, don't change state
                                    if(!_led.getLedOn()){
                                        _led.changeState();
                                        _led.setOnLed(true);

                                        //this occurs whenever the led is state is changed
                                        if(bluetoothAdapter.isEnabled()){
                                            clientClass.write(String.valueOf(i).getBytes());
                                            clientClass.write("*".getBytes());
                                            Log.d("MatrixUi:TouchEvent","Wrote data " +i+"*");
                                        }

                                    }

                        }else{
                                  //the finger's not on the led anymore
                                  _led.setOnLed(false);

                        }

                        i+=1;

                       }


                break;


            }
            return true;

        }
    };



    private class ClientClass extends Thread{

        private BluetoothDevice device;
        private BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;


        public ClientClass(BluetoothDevice device1){
            this.device = device1;
            InputStream tempIn = null;
            OutputStream tempOut  = null;

            try {
                socket = device.createRfcommSocketToServiceRecord(BtModuleUUID);

                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();




            }catch (IOException e){
                e.printStackTrace();
            }


            inputStream = tempIn;
            outputStream = tempOut;

        }

        public void run(){

            try {
                socket.connect();
                Log.d("MatrixUi","Bluetooth - Connection success :)");

            } catch (IOException e) {
                Log.d("MatrixUi","Bluetooth- Connection failed :(");
                e.printStackTrace();


            }
        }


        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }




    }


    private class SendReceive extends Thread{

        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket){
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut  = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while(true){
                try {
                    bytes = inputStream.read(buffer);
                    //handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }



    }




}





