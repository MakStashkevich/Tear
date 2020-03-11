package com.makstashkevich.tear.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.makstashkevich.tear.R;
import com.splunk.mint.Mint;

public class StartScreenActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        Mint.initAndStartSession(this.getApplication(), "585f5bee");

        String version = "Специально для КСТМиА УО РИПО";
        try {
            version += System.lineSeparator() + "v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView textView = findViewById(R.id.version);
        textView.setText(version);

        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(StartScreenActivity.this, MainActivity.class);
            startActivity(mainIntent);
        }, 2000);
    }
}