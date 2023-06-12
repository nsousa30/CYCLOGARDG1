package com.example.cyclogard;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import java.util.*;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    Button conectar, mapa, camerabut, upload;
    TextView estado, mensagem;

    ListView lista;

    Switch modosilencioso;

    ArrayAdapter arrayAdapter;

    SendReceive sendReceive;

    int distancia;

    boolean silencio = false;
    boolean medir = true;

    boolean tempo = true;

    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    private BluetoothDevice device2;

    DataBaseHelper dataBaseHelper;

    String latitude, longitude;

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    private static final long UPDATE_INTERVAL = 1000;

    Base base;



    CameraView camera;

    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;


    private static final int TEMPO_ESPERA = 500;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewByIds();
        implement();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }



        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        dataBaseHelper = new DataBaseHelper(MainActivity.this);

        MostrarLista();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);


        ObterCoordenadas();




        camera = findViewById(R.id.camera2);
        camera.setLifecycleOwner(this);
        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {


                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
                } else {
                    savePicture(result.getData());
                }

            }
        });


        if (bluetoothAdapter == null) {
            estado.setText("O dispositivo não suporta Bluetooth");
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {

            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("CYCLOGARD")) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);
                    device2 = device;
                    ClientClass clientClass = new ClientClass(device2);
                    clientClass.start();


                }
            }
        }


    }

    private void MostrarLista() {
        arrayAdapter= new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, dataBaseHelper.getEveryone());
        lista.setAdapter(arrayAdapter);
    }


    private void findViewByIds() {

        conectar = (Button) findViewById(R.id.Conectar);
        estado = (TextView) findViewById(R.id.Estado);
        mensagem = (TextView) findViewById(R.id.Mensagem);
        modosilencioso = (Switch) findViewById(R.id.modosilencioso);
        lista = (ListView) findViewById(R.id.lista);
        mapa = (Button) findViewById(R.id.mapa);
        camerabut = (Button) findViewById(R.id.camerabut);
        upload =(Button) findViewById(R.id.upload);



    }

    private void implement() {

        conectar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = Message.obtain();
                message.what = STATE_CONNECTING;
                handler.sendMessage(message);
                ClientClass clientClass = new ClientClass(device2);
                clientClass.start();



            }
        });

        modosilencioso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                silencio = b;
            }
        });

        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (arrayAdapter.getCount() > 0) {
                    showMap();
                } else {
                    Toast.makeText(MainActivity.this, "Não existe ocorrências para mostrar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Base lista = (Base) parent.getItemAtPosition(position);

                String ID = String.valueOf(lista.getId());

                Intent intent = new Intent(MainActivity.this, OcorrenciaActivity.class);
                intent.putExtra("ID", ID);
                startActivity(intent);



            }
        });
        camerabut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(CameraActivity.class);

            }

        });

        upload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new UploadDataTask().execute() ;



                File localFolderPath = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CYCLOGARD");

                String remoteFolderPath = "";

                FTPUploader ftpUploader = new FTPUploader("192.168.137.1", 21, "teste", "teste");
                ftpUploader.uploadFolder(localFolderPath, remoteFolderPath);


            }

        });


    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what) {

                case STATE_CONNECTING:
                    estado.setText("...");
                    estado.setTextColor(Color.BLUE);
                    break;
                case STATE_CONNECTED:
                    estado.setText("Conectado");
                    estado.setTextColor(Color.GREEN);
                    conectar.setText("Desconectar");
                    break;
                case STATE_CONNECTION_FAILED:
                    estado.setText("Desconectado");
                    conectar.setText("Conectar");
                    estado.setTextColor(Color.RED);
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    String tempMsg2 = tempMsg + " cm";
                    mensagem.setText(tempMsg2);
                    if (silencio) {
                        String string1 = "1";
                        sendReceive.write(string1.getBytes());
                    } else {
                        String string2 = "0";
                        sendReceive.write(string2.getBytes());
                    }
                    try{
                        distancia = Integer.parseInt(tempMsg);
                    }
                    catch (NumberFormatException ex){
                        ex.printStackTrace();
                    }
                    if (distancia <= 50 && medir && tempo) {

                        Date dataHoraAtual = new Date();
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                        ObterCoordenadas();

                        if (!(latitude == null || longitude == null)) {



                        String data = formato.format(dataHoraAtual);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    camera.takePicture();
                                }
                            }, TEMPO_ESPERA);




                        try{
                            base = new Base(-1, data, latitude, longitude);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }


                        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);

                        dataBaseHelper.addOne(base);



                        MostrarLista();




                        medir = false;
                        tempo= false;
                            Handler handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    tempo = true;
                                }
                            }, TEMPO_ESPERA+500);}

                    } else if (distancia >= 50 && !medir) {
                        medir = true;
                    }


                    break;
            }
            return true;
        }
    });

    private class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1) {
            device = device1;
            UUID uuid = device.getUuids()[0].getUuid();

            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showMap() {

            startMapsActivity();

    }

    private void startMapsActivity() {
        // Iniciar a MapsActivity
        startActivity(MapsActivity.class);
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    private void ObterCoordenadas() {

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {

                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        latitude = String.valueOf(lat);
                        longitude = String.valueOf(lon);

                    }
                }

            }

        };

    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        camera.close();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            File directory = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "CYCLOGARD");
            if (!directory.exists()) {
                directory.mkdirs();
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.destroy();
    }
    private void savePicture(byte[] data) {
        if (isExternalStorageWritable()) {
            File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CYCLOGARD");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            if (directory != null) {
                String fileName = dataBaseHelper.obterId()+".jpg";
                File outputFile = new File(directory, fileName);

                try {
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    fos.write(data);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(MainActivity.this, "Não é possível salvar a imagem", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private class UploadDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            uploadData();
            return null;
        }
    }

    public void uploadData() {
        List<Data> dataList = dataBaseHelper.getDataFromSQLite();


        for (Data Data : dataList) {
            String value1 = Data.getValue1();
            String value2 = Data.getValue2();
            String value3 = Data.getValue3();
            String value4 = Data.getValue4();

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("ID", value1)
                    .add("Data", value2)
                    .add("Latitude", value3)
                    .add("Longitude", value4)
                    .build();
            Request request = new Request.Builder()
                    .url("http://192.168.137.1/sqllite.php")
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {

                } else {

                }

            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }




}









