package com.mayank.rucky;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {

    public static Boolean didThemeChange = false;
    public static Boolean advSecurity = false;
    public static double currentVersion;
    final static private int PERM = 0;
    public static final String CHANNEL_ID = "com.mayank.rucky";
    public static final String CHANNEL_NAME = "Update";
    Process p;
    private static DataOutputStream dos;
    private Boolean root = false;
    public static int distro = 0;
    public static SecretKey key;
    ArrayList<String> languages = new ArrayList<>();
    ArrayList<String> modes = new ArrayList<>();
    public static boolean piConnected = false;
    private static NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)throws NullPointerException {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences(SettingsActivity.PREF_SETTINGS, MODE_PRIVATE);
        SettingsActivity.darkTheme = settings.getBoolean(SettingsActivity.PREF_SETTINGS_DARK_THEME, true);
        advSecurity = settings.getBoolean(SettingsActivity.PREF_SETTING_ADV_SECURITY, false);
        setTheme(SettingsActivity.darkTheme?R.style.AppThemeDark:R.style.AppThemeLight);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitleTextColor(getResources().getColor(R.color.accent));
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            permission();
            getRoot();
            if(!root) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Root Access Required For USB Cable Attack!");
                builder.setCancelable(false);
                builder.setPositiveButton("Continue", ((dialog, which) -> dialog.cancel()));
                AlertDialog rootMissing = builder.create();
                rootMissing.show();
            }
        }
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersion = Double.parseDouble(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        NotificationChannel notificationChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.enableVibration(false);
            notificationChannel.canBypassDnd();
            notificationChannel.setSound(null,null);
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        } else {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        cleanup();
        Spinner language = findViewById(R.id.langMenu);
        languages.add("American English");
        languages.add("Turkish");
        languages.add("Swedish");
        languages.add("Slovenian");
        languages.add("Russian");
        languages.add("Portuguese");
        languages.add("Norwegian");
        languages.add("Italian");
        languages.add("Croatian");
        languages.add("United Kingdom English");
        languages.add("French");
        languages.add("Finnish");
        languages.add("Spanish");
        languages.add("Danish");
        languages.add("German");
        languages.add("Canadian French");
        languages.add("Brazilian Portuguese");
        languages.add("Belarusian");
        languages.add("Hungarian");
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, languages);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(langAdapter);
        Spinner mode = findViewById(R.id.modeMenu);
        modes.add("USB Cable (Root)");
        modes.add("Ras Pi (Wi-Fi) (Unused)");
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, modes);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mode.setAdapter(modeAdapter);
        Button DelBtn = findViewById(R.id.delBtn);
        Button SaveBtn = findViewById(R.id.svBtb);
        Button LoadBtn = findViewById(R.id.ldBtn);
        Button ExeBtn = findViewById(R.id.exBtn);
        DelBtn.setOnClickListener(view -> {
            final File[] tmp = Objects.requireNonNull(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).listFiles();
            assert tmp != null;
            ArrayList<File> files = new ArrayList<>();
            if (!advSecurity) {
                for (File file : tmp) {
                    if (file.getPath().endsWith(".txt")) {
                        files.add(file);
                    }
                }
            } else {
                files.addAll(Arrays.asList(tmp));
            }
            CharSequence[] fileName = new CharSequence[files.size()];
            for (int i = 0; i < files.size(); i++) {
                fileName[i] = files.get(i).getName();
            }
            AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Select File");
            builder.setCancelable(false);
            builder.setItems(fileName, (dialog, i) -> {
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),files.get(i).getName());
                file.delete();
                if(file.exists()){
                    try {
                        file.getCanonicalFile().delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(file.exists()){
                        getApplicationContext().deleteFile(file.getName());
                    }
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });
        SaveBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("File Name");
            final EditText fileName = new EditText(MainActivity.this);
            builder.setView(fileName);
            builder.setCancelable(false);
            builder.setPositiveButton("Save", (dialog, which) -> {
                EditText scripts = findViewById(R.id.code);
                File file;
                if (advSecurity) {
                    file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),fileName.getText().toString()+".enc");
                } else {
                    file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),fileName.getText().toString()+".txt");
                }
                String content = scripts.getText().toString();
                FileOutputStream fOutputStream;
                OutputStream outputStream;
                try {
                    if (advSecurity) {
                        @SuppressLint("GetInstance") Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
                        c.init(Cipher.ENCRYPT_MODE, key);
                        fOutputStream = new FileOutputStream(file);
                        outputStream = new BufferedOutputStream(new CipherOutputStream(fOutputStream, c));
                        outputStream.write(content.getBytes(Charset.forName("UTF-8")));
                        outputStream.close();
                        fOutputStream.close();
                    } else {
                        fOutputStream = new FileOutputStream(file);
                        outputStream = new BufferedOutputStream(fOutputStream);
                        outputStream.write(content.getBytes(Charset.forName("UTF-8")));
                        outputStream.close();
                        fOutputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });
        LoadBtn.setOnClickListener(view -> {
            final File[] tmp = Objects.requireNonNull(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).listFiles();
            assert tmp != null;
            ArrayList<File> files = new ArrayList<>();
            if (!advSecurity) {
                for (File file : tmp) {
                    if (file.getPath().endsWith(".txt")) {
                        files.add(file);
                    }
                }
            } else {
                files.addAll(Arrays.asList(tmp));
            }
            CharSequence[] fileName = new CharSequence[files.size()];
            for (int i = 0; i < files.size(); i++) {
                fileName[i] = files.get(i).getName();
            }
            AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Select File");
            builder.setCancelable(false);
            builder.setItems(fileName, (dialog, i) -> {
                EditText scripts = findViewById(R.id.code);
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),files.get(i).getName());
                FileInputStream fInputStream;
                InputStream inputStream;
                StringWriter writer;
                try {
                    if (advSecurity) {
                        @SuppressLint("GetInstance") Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
                        c.init(Cipher.DECRYPT_MODE, key);
                        fInputStream = new FileInputStream(file);
                        inputStream = new BufferedInputStream(new CipherInputStream(fInputStream,c));
                        writer = new StringWriter();
                        IOUtils.copy(inputStream, writer, "UTF-8");
                        scripts.setText(writer.toString());
                        inputStream.close();
                        fInputStream.close();
                    } else {
                        fInputStream = new FileInputStream(file);
                        inputStream = new BufferedInputStream(fInputStream);
                        writer = new StringWriter();
                        IOUtils.copy(inputStream, writer, "UTF-8");
                        scripts.setText(writer.toString());
                        inputStream.close();
                        fInputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });
        ExeBtn.setOnClickListener(view -> {
            EditText scripts = findViewById(R.id.code);
            launchAttack(modes.indexOf(mode.getSelectedItem().toString()),languages.indexOf(language.getSelectedItem().toString()),scripts.getText().toString());
        });
    }

    void launchAttack(int mode, int language, String scripts) {
        if(mode == 0) {
            getRoot();
            if(root) {
                if (supportedFiles()) {
                    try {
                        hid exeScript = new hid(language);
                        exeScript.parse(scripts);
                        ArrayList<String> cmds = exeScript.getCmd();
                        for (int i = 0; i < cmds.size(); i++) {
                            dos.writeBytes(cmds.get(i));
                            dos.flush();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Root Access Required For USB Cable Attack!");
                builder.setCancelable(false);
                builder.setPositiveButton("Continue", ((dialog, which) -> dialog.cancel()));
                builder.setNegativeButton("Exit", ((dialog, which) -> {
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }));
                AlertDialog rootMissing = builder.create();
                rootMissing.show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_noupdate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.Setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
        }
        return true;
    }

    @Override
    public void onResume()throws NullPointerException {
        super.onResume();
        if (didThemeChange) {
            didThemeChange = false;
            finish();
            startActivity(getIntent());
        }
        permission();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void permission() {
        ArrayList<String> permission = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (!permission.isEmpty())
            ActivityCompat.requestPermissions(this,permission.toArray(new String[permission.size()]),PERM);
    }

    private void getRoot() {
        try {
            p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            BufferedReader dis = new BufferedReader(new InputStreamReader(p.getInputStream()));
            if(dos != null) {
                dos.writeBytes("id\n");
                dos.flush();
                String rootCheck = dis.readLine();
                if(rootCheck.contains("uid=0")) {
                    root = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean supportedFiles() {
        String pathDev = "/dev";
        File file1 = new File(pathDev,"hidg0");
        if(!file1.exists()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Enable HID Gadget Mode!");
            builder.setCancelable(false);
            builder.setPositiveButton("Continue", ((dialog, which) -> dialog.cancel()));
            AlertDialog kernelExit = builder.create();
            kernelExit.show();
            return false;
        } else {
            try {
                dos.writeBytes("chmod 666 /dev/hidg0\n");
                dos.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERM && grantResults.length > 0 && permissions.length==grantResults.length) {
            for (int i = 0; i < permissions.length; i++){
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    permission();
                }
            }
        }
    }

    void cleanup() {
        if(root) {
            try {
                dos.writeBytes("rm -rf /data/local/tmp/rucky-hid\n");
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}