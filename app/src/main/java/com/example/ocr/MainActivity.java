package com.example.ocr;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntroFragment introFragment =  new IntroFragment();
        MainFragment mainFragment = new MainFragment();
        PdfFragment pdfFragment = new PdfFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.navHostFragment, mainFragment)
                .commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}