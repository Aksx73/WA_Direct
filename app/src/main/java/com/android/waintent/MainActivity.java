package com.android.waintent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

public class MainActivity extends AppCompatActivity {

    private CountryCodePicker countryCodePicker;
    private TextInputEditText et_number, et_message;
    private TextInputLayout lyt_number, lyt_message;
    private MaterialButton bt_send;
    private String messagestr, numberstr = "";
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DynamicColors.applyToActivityIfAvailable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.lyt_root);
        countryCodePicker = findViewById(R.id.countryCode);
        et_number = findViewById(R.id.et_number);
        et_message = findViewById(R.id.et_message);
        lyt_number = findViewById(R.id.lyt_number);
        lyt_message = findViewById(R.id.lyt_message);
        bt_send = findViewById(R.id.sendbtn);

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (/*checkEmptyField(et_message,lyt_message) &&*/ checkEmptyField(et_number, lyt_number)) {

                    messagestr = et_message.getText().toString();
                    String number = et_number.getText().toString().trim().replaceAll(" ","").replace("+","");

                    if (PhoneNumberUtils.isGlobalPhoneNumber(number)){
                        countryCodePicker.registerCarrierNumberEditText(et_number);
                        numberstr = countryCodePicker.getFullNumber();

                        Log.d("TAG", "onClick: " + et_number.getText());
                        Log.d("TAG", "onClick: " + numberstr);

                        if (isWhatappInstalled()) {
                            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + numberstr +
                                    "&text=" + messagestr));
                            startActivity(i);
                             et_message.getText().clear();
                            et_number.getText().clear();
                            lyt_number.setErrorEnabled(false);
                            lyt_number.setError("");
                            lyt_number.clearFocus();

                        } else {
                            Snackbar.make(root, "Whatsapp is not installed", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        lyt_number.setErrorEnabled(true);
                        lyt_number.setError("Enter valid number without country code");
                    }

                } else {
                    //checkEmptyField(et_message,lyt_message);
                    checkEmptyField(et_number, lyt_number);
                }

            }
        });


        et_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(et_number.getText())) {
                    lyt_number.setErrorEnabled(true);
                    lyt_number.setError("Required");
                } else {
                    lyt_number.setErrorEnabled(false);
                    lyt_number.setError("");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

      /*  et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et_message.getText().toString().trim().equals("")){
                    lyt_message.setErrorEnabled(true);
                    lyt_message.setError("Required");
                }else {
                    lyt_message.setErrorEnabled(false);
                    lyt_message.setError("");
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

    }

    private boolean isWhatappInstalled() {
        PackageManager packageManager = getPackageManager();
        boolean whatsappInstalled;
        try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            whatsappInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            whatsappInstalled = false;
        }
        return whatsappInstalled;
    }

    private boolean checkEmptyField(TextInputEditText editText, TextInputLayout inputLayout) {
        if (editText.getText().toString().trim().equals("")) {
            // editText.setError("Required");
            inputLayout.setError("Required");
            return false;
        } else {
            return true;
        }
    }

}


