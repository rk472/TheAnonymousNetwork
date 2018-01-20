package com.example.ramakanta.theanonymousnetwork;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import br.com.simplepass.loading_button_lib.interfaces.OnAnimationEndListener;

public class RegisterOnceActivity extends AppCompatActivity {

    CircularProgressButton circularProgressButton;
    private Calendar myCalendar;
    private EditText dob_reg,name_reg,designation_reg,lives_reg,phone_reg,email_reg,joined_reg,bio_reg;
    private String s_dob_reg,s_name_reg,s_designation_reg,s_lives_reg,s_phone_reg,s_email_reg,s_joined_reg,s_bio_reg,s_gender_reg;
    private Spinner gender_reg;
    private ArrayAdapter<CharSequence> adapter;
    private DatePickerDialog.OnDateSetListener date_pick;
    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register_once);
        circularProgressButton = findViewById(R.id.btn_reg);
        linearLayout = findViewById(R.id.register_layout);
        dob_reg = findViewById(R.id.dob_reg);
        designation_reg = findViewById(R.id.designation_reg);
        name_reg = findViewById(R.id.name_reg);
        lives_reg = findViewById(R.id.lives_reg);
        phone_reg = findViewById(R.id.phone_reg);
        email_reg = findViewById(R.id.email_reg);
        joined_reg = findViewById(R.id.joined_reg);
        bio_reg = findViewById(R.id.bio_reg);
        gender_reg = findViewById(R.id.gender_reg);

        //Spinner Item Insertion
        adapter = ArrayAdapter.createFromResource(this , R.array.gender_select,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_reg.setAdapter(adapter);
        gender_reg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                s_gender_reg = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //Select the Date
        myCalendar= Calendar.getInstance();
        date_pick = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        //Submit button click event
        circularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("StaticFieldLeak")
                AsyncTask<String, String, String> demoDownload = new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String res = validateAllFields();
                        return res;
                    }
                    @Override
                    protected void onPostExecute(String s)
                    {
                        if(s.equals("not"))
                        {
                            circularProgressButton.revertAnimation(new OnAnimationEndListener() {
                                @Override
                                public void onAnimationEnd() {
                                }
                            });
                            circularProgressButton.setBackgroundResource(R.drawable.btnshape11);
                        }
                        if(s.equals("done"))
                        {
                            circularProgressButton.revertAnimation();
                            circularProgressButton.setBackgroundResource(R.drawable.btnshape11);
                        }
                    }
                };
                circularProgressButton.startAnimation();
                demoDownload.execute();
            }
        });
    }

    private String validateAllFields() {

        if(dob_reg.getText().toString().equals("")||name_reg.getText().toString().equals("")||designation_reg.getText().toString().equals("")||lives_reg.getText().toString().equals("")||phone_reg.getText().toString().equals("")||email_reg.getText().toString().equals("")||joined_reg.getText().toString().equals("")||bio_reg.getText().toString().equals(""))
        {
            Snackbar snackbar = Snackbar
                    .make(linearLayout , "Fields Cannot Be Blank", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return "error";
        }
        else if(true){
            return "error";
        }
        return "success";
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        circularProgressButton.setBackgroundResource(R.drawable.btnshape11);
    }
    public void otc(View v) {
        new DatePickerDialog(RegisterOnceActivity.this, date_pick, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dob_reg.setText(sdf.format(myCalendar.getTime()));
    }
}