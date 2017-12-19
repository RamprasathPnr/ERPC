package com.omneagate.erbc.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.omneagate.erbc.Dto.EnumDto.CommonStatus;
import com.omneagate.erbc.Dto.EnumDto.RequestType;
import com.omneagate.erbc.Dto.ServiceListenerType;
import com.omneagate.erbc.Dto.UpgradeDto;
import com.omneagate.erbc.R;
import com.omneagate.erbc.Services.HttpClientWrapper;
import com.omneagate.erbc.Util.NetworkConnection;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user1 on 4/7/16.
 */
public class AutoUpgrataionActivity extends BaseActivity {

    Integer oldVersion, newVersion;
    String downloadApkPath;
    //Downloading the progressbar
    private ProgressBar progressBar;
    // Download percentage
    private TextView tvUploadCount;
    private SimpleDateFormat sdf;
    private static final int REQUEST_RUNTIME_PERMISSION = 123;
    private String appPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autoupgrade);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        progressBar = (ProgressBar) findViewById(R.id.autoUpgradeprogressBar);
        progressBar.setVisibility(View.VISIBLE);
        tvUploadCount = (TextView) findViewById(R.id.tvUploadCount);
        downloadApkPath = getIntent().getStringExtra("downloadPath");
        newVersion = getIntent().getIntExtra("newVersion", 0);

        if (CheckPermission(AutoUpgrataionActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // you have permission go ahead
            createFolder();
        } else {
            // you do not have permission go request runtime permissions
            RequestPermission(AutoUpgrataionActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_RUNTIME_PERMISSION);
        }

    }

    public void RequestPermission(Activity thisActivity, String Permission, int Code) {
        if (ContextCompat.checkSelfPermission(thisActivity,
                Permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity, Permission)) {
            } else {
                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Permission}, Code);
            }
        }
    }

    private void getDownloadUrl() {

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            oldVersion = pInfo.versionCode;
            UpgradeDto upgradeDto = new UpgradeDto();
            sdf = new SimpleDateFormat("dd-MMM-yyyy");
            Log.e("date", "" + sdf.format(new Date()));
            Log.e("date1", "" + sdf.format(new Date().getTime()));
            upgradeDto.setCreatedDate("" + sdf.format(new Date().getTime()));
            upgradeDto.setPreviousVersion(pInfo.versionCode);
            upgradeDto.setCurrentVersion(newVersion);
            upgradeDto.setVersionStatus(CommonStatus.UPDATE_START);
            upgradeDto.setApplicationType("PUBLIC_APP");
            upgradeData(upgradeDto, ServiceListenerType.AUTOUPGRADE);
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("AutoUpgrade", e.toString(), e);
            Toast.makeText(getApplicationContext(),
                    getString(R.string.internalError), Toast.LENGTH_SHORT).show();
        }
    }

    private void createFolder() {

        File file = new File(Environment.getExternalStorageDirectory(), "ERPC");
        if (!file.exists())
            file.mkdir();

        appPath = Environment.getExternalStorageDirectory() + "/ERPC/ERPC_PUBLIC.apk";
        getDownloadUrl();
    }

    public boolean CheckPermission(Context context, String Permission) {
        if (ContextCompat.checkSelfPermission(context,
                Permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void upgradeData(UpgradeDto upgradeDto, ServiceListenerType type) throws Exception {
        httpConnection = new HttpClientWrapper();
        String checkVersion = new Gson().toJson(upgradeDto);
        StringEntity se = new StringEntity(checkVersion, HTTP.UTF_8);
        String url = "/upgradehistory/addhistory";
        Log.e("AutoUpgradationActivity", "Checking version of apk in device");
        httpConnection.sendRequest(url, null, type, SyncHandler, RequestType.POST, se, this);
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        Log.e("serviceListenertype", "" + what);
        switch (what) {
            case AUTOUPGRADE:
                setData(message);
                break;
            case CHECKVERSION:
                setData(message);
                break;
            case AUTOUPGRADE_ACTIVE:
                break;
            default:
                Log.e("AutoUpgrade", "Auto upgrade");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_RUNTIME_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // you have permission go ahead
                    createFolder();

                } else {
                    // you do not have permission show toast.
                }
                return;
            }
        }
    }

    private void setData(Bundle message) {
        try {
            downloadStart();
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("AutoUpgrade ", e.toString(), e);
        }
    }

    private void downloadStart() throws Exception {
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        oldVersion = pInfo.versionCode;

        /*File file = new File(Environment.getExternalStorageDirectory(), "ERPC");
        if (!file.exists()) {
            file.mkdir();
        }
        final String path = Environment.getExternalStorageDirectory() + "/ERPC/ERPC_PUBLIC.apk";*/
        Log.e("AutoUpgrationActivity", "path....." + appPath);
        getFutureFile(appPath);
    }

    private void getFutureFile(String path) {
        Log.e("AutoUpgrationActivity", "downloadApkPath....." + downloadApkPath);
        Ion.with(AutoUpgrataionActivity.this).load(downloadApkPath)
                .progressBar(progressBar)
                .progressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        double ratio = downloaded / (double) total;
                        DecimalFormat percentFormat = new DecimalFormat("#.#%");
                        tvUploadCount.setText("" + percentFormat.format(ratio));
                    }
                })
                .write(new File(path))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        if (e != null) {
                            System.out.println("onCompleted Exception :: " + e.getMessage());
                            Toast.makeText(AutoUpgrataionActivity.this, "Error downloading file.Try again", Toast.LENGTH_LONG).show();
                            onBackPressed();
                            return;
                        }
                        upGradeComplete();
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_VIEW);
                        i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        startActivity(i);
                        finish();
                    }
                });
    }

    private void upGradeComplete() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            UpgradeDto upgradeDto = new UpgradeDto();
            Log.e("date", "" + sdf.format(new Date()));
            upgradeDto.setCreatedDate("" + sdf.format(new Date()));
            upgradeDto.setPreviousVersion(pInfo.versionCode);
            upgradeDto.setCurrentVersion(newVersion);
            upgradeDto.setVersionStatus(CommonStatus.APK_DOWNLOAD);
            upgradeDto.setApplicationType("PUBLIC_APP");
            upgradeData(upgradeDto, ServiceListenerType.AUTOUPGRADE_ACTIVE);
        } catch (Exception e) {
            GlobalAppState.getInstance().trackException(e);
            Log.e("AutoUpgrade", e.toString(), e);
        }
    }
}
