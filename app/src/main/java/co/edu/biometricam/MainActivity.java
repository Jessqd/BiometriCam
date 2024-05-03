package co.edu.biometricam;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 25;
    //1. Declaracion de los objetos de la interfaz que se usaran en la parte logica

    private Button btnCheckPermissions;
    private Button btnRequestPermissions;

    private TextView tvCamera;
    private TextView tvBiometric;
    private TextView tvInternalWS;
    private TextView tvReadInternalS;
    private TextView tvResponse;
    //1.1 Objetos para recursos
    private TextView versionAndroid;
    private int versionSDK;


    IntentFilter batFilter;
    CameraManager cameraManager;
    String cameraId;
    private Button btnOn;
    private Button btnOff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //3. Llamado del método de enlace de objetos
        initObject();


        //4.Enlace de botonos a los métodos
        btnCheckPermissions.setOnClickListener(this::voidCheckPermissions);
        btnRequestPermissions.setOnClickListener(this::voidRequestPermissions);
        btnPermissionIS.setOnClickListener(this::voidPermissionIS);
        btnPermissionRS.setOnClickListener(this::voidPermissionRS);


        //botones para la linterna
        btnOn.setOnClickListener(this::onLigth);
        btnOff.setOnClickListener(this::offLigth);


    }




    //8.Implementacion del OnResume para la version de Android

    @Override
    protected void onResume() {
        super.onResume();
        String versionSO = Build.VERSION.RELEASE;
        versionSDK = Build.VERSION.SDK_INT;

        versionAndroid.setText("Version SO:"+versionSO+" / SDK"+versionSDK);
    }

    //9. Encedido de y pagado de linterna

    private void onLigth(View view) {
        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId,true);
        }catch (Exception e){
            Toast.makeText(this, "No se puede encender la linterna", Toast.LENGTH_SHORT).show();
            Log.i("FLASH",e.getMessage());
        }
    }
    private void offLigth(View view) {
        try {
            cameraManager.setTorchMode(cameraId,false);
        }catch (Exception e){
            Toast.makeText(this, "No se puede encender la linterna", Toast.LENGTH_SHORT).show();
            Log.i("FLASH",e.getMessage());
        }
    }

    //5.Verificacion de permisos
    private void voidCheckPermissions(View view) {
        //Si hay permiso me devuelve 0 si no me devuelve 1

        int statusCamera = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA);
        int statusWIS = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_INTERNAL_STORAGE);
        int statusRIS = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_INTERNAL_STORAGE);
        int statusBiometric = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.USE_BIOMETRIC);



        tvCamera.setText("Status Camera:"+statusCamera);
        tvInternalWS.setText("Status WIS:"+statusWIS);
        tvReadInternalS.setText("Status RIS:"+statusRES);
        tvBiometric.setText("Status Biometric:"+statusBiometric);


        btnRequestPermissions.setEnabled(true);
        btnPermissionRS.setEnabled(true);
        btnPermissionES.setEnabled(true);


    }
    //6.Solicitud de permiso de Camara
    private void voidRequestPermissions(View view) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA},REQUEST_CODE);
        }
    }
    private void voidPermissionRS(View view) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_INTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_INTERNAL_STORAGE},10);
        }
    }

    private void voidPermissionIS(View view) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_INTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_INTERNAL_STORAGE},20);
        }
    }


//7.Gestión de respuesta del usuario respecto a la solicitud del permiso

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        tvResponse.setText(" "+grantResults[0]);
        if (requestCode == REQUEST_CODE)
        {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                new AlertDialog.Builder(this)
                        .setTitle("Box Permissions")
                        .setMessage("You denied the permissions Camera")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).create().show();

            }else {
                Toast.makeText(this, "Usted no ha otorgado los permisos", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Usted ha otorgado los permisos", Toast.LENGTH_SHORT).show();
        }
    }


    //2. Enlace de objetos

    private void initObject() {
        btnCheckPermissions = findViewById(R.id.btnCheckPermission);
        btnRequestPermissions = findViewById(R.id.btnRequestPermission);
        btnPermissionIS = findViewById(R.id.btnPermissionIS);
        btnPermissionRS = findViewById(R.id.btnPermissionRS);


        //Desactivar Boton
        btnRequestPermissions.setEnabled(false);
        btnPermissionIS.setEnabled(false);
        btnPermissionRS.setEnabled(false);


        //Unir Pantallas
        tvCamera = findViewById(R.id.tvCamera);
        tvBiometric = findViewById(R.id.tvDactilar);
        tvInternalWS = findViewById(R.id.tvIws);
        tvReadInternalS = findViewById(R.id.tvRS);
        tvResponse = findViewById(R.id.tvResponse);


        versionAndroid = findViewById(R.id.tvVersionAndroid);

        btnOn = findViewById(R.id.btnOn);
        btnOff = findViewById(R.id.btnOff);



    }
}

/*
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnCheckPermissions;
    private Button btnRequestPermissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tvDactilar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initObject();


        btnCheckPermissions.setOnClickListener(this::voidCheckPermissions);
        btnRequestPermissions.setOnClickListener(this::voidRequestPermissions);

        btnOn.setOnClickListener(this::onLigth);
        btnOff.setOnClickListener(this::offLigth);

        }
    //5.Verificacion de permisos
    private void voidRequestPermissions(View view) {

        int statusCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int statusBiometric = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.USE_BIOMETRIC);

        tvCamera.setText("Status Camera:"+statusCamera);
        tvBiometric.setText("Status Biometric:"+statusBiometric);
        btnRequestPermissions.setEnabled(true);


    }
    //6.Solicitud de permiso de Camara
    private void voidCheckPermissions(View view) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        }

    }


//7.Gestión de respuesta del usuario respecto a la solicitud del permiso

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        tvResponse.setText(" "+grantResults[0]);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setTitle("Box Permissions")
                        .setMessage("You denied the permissions Camera")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).create().show();
            } else {
                Toast.makeText(this, "Usted no ha otorgado los permisos", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Usted ha otorgado los permisos", Toast.LENGTH_SHORT).show();
        }
    }

    //2. Enlace de objetos
    private void initObject() {
        btnCheckPermissions = findViewById(R.id.btnCheckPermission);
        btnRequestPermissions = findViewById(R.id.btnRequestPermission);

        //Desactivar Boton
        btnRequestPermissions.setEnabled(false);


        //Unir Pantallas
        tvCamera = findViewById(R.id.tvCamera);
        tvBiometric = findViewById(R.id.tvDactilar);
        tvResponse = findViewById(R.id.tvResponse);

        versionAndroid = findViewById(R.id.tvVersionAndroid);
        btnOn = findViewById(R.id.btnOn);
        btnOff = findViewById(R.id.btnOff);




    }
}*/

