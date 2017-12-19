package com.omneagate.erbc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.omneagate.erbc.R;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;



/**
 * Created by user1 on 27/4/16.
 */
public class CountryselectionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countryslection);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_dropdown_item_1line, CountryDetails.country);
        ArrayAdapter<String> countryadpter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_dropdown_item_1line, CountryDetails.country);
        MaterialBetterSpinner langualge = (MaterialBetterSpinner)findViewById(R.id.lang_selection);
        MaterialBetterSpinner country = (MaterialBetterSpinner)findViewById(R.id.countryselction);
        Button click = (Button)findViewById(R.id.next1);
        country.setAdapter(countryadpter);
        langualge.setAdapter(adapter);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(),RegisterationActivity.class);
                startActivity(next);
                finish();
            }
        });

    }
}
