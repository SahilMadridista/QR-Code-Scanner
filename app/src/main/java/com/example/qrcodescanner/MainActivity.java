package com.example.qrcodescanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkpermission())
            {
                Toast.makeText(MainActivity.this,"Permission Granted",Toast.LENGTH_SHORT).show();
            }

            else
            {
                requestPermission();
            }
        }

    }

    private boolean checkpermission()
    {
        return (ContextCompat.checkSelfPermission(MainActivity.this, CAMERA) ==
                PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this,new String[]{CAMERA},REQUEST_CAMERA);
    }

    public void onRequestPermissionResult(int requestcode , String permissions[],int grantResults[])
    {

        switch (requestcode)

        {

            case REQUEST_CAMERA:
                if(grantResults.length>0)
                {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted)
                    {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_SHORT).show();
                    }

                    else
                    {
                        Toast.makeText(MainActivity.this, "Permission Denied",
                                Toast.LENGTH_SHORT).show();

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        {
                            if(shouldShowRequestPermissionRationale(CAMERA))
                            {
                                displayAlertMessage("You need to set permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                requestPermissions(new String[]{CAMERA},REQUEST_CAMERA);
                                            }
                                        });

                                return;
                            }
                        }

                    }

                }

                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkpermission())
            {
                if(scannerView == null)
                {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }

                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }

            else
            {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void displayAlertMessage(String message , DialogInterface.OnClickListener listener)
    {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK",listener)
                .setNegativeButton("Cancel",null)
                .create()
                .show();

    }

    @Override
    public void handleResult(final Result result) {

        //This method will contain the logic to handle the result to the scan

        final String scanresult = result.getText();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                scannerView.resumeCameraPreview(MainActivity.this);


            }
        });

        builder.setNeutralButton("Go to URL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanresult));
                startActivity(intent);

            }
        });

        builder.setMessage(scanresult);
        AlertDialog alert = builder.create();
        alert.show();

    }
}
