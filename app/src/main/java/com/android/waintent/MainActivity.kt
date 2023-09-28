package com.android.waintent

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.DynamicColors
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hbb20.CountryCodePicker

class MainActivity : AppCompatActivity() {
    private var countryCodePicker: CountryCodePicker? = null
    private var et_number: TextInputEditText? = null
    private var et_message: TextInputEditText? = null
    private var lyt_number: TextInputLayout? = null
    private var lyt_message: TextInputLayout? = null
    private var bt_send: MaterialButton? = null
    private var messagestr: String? = null
    private var numberstr = ""
    private var root: LinearLayout? = null
    private var parent: ScrollView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        root = findViewById(R.id.lyt_root)
        countryCodePicker = findViewById(R.id.countryCode)
        et_number = findViewById(R.id.et_number)
        et_message = findViewById(R.id.et_message)
        lyt_number = findViewById(R.id.lyt_number)
        lyt_message = findViewById(R.id.lyt_message)
        bt_send = findViewById(R.id.sendbtn)
        parent = findViewById(R.id.parent)
        window.navigationBarColor = SurfaceColors.SURFACE_2.getColor(this@MainActivity)
        window.statusBarColor = SurfaceColors.SURFACE_2.getColor(this@MainActivity)
        parent?.setBackgroundColor(SurfaceColors.SURFACE_2.getColor(this@MainActivity))
        bt_send?.setOnClickListener {
            if (checkEmptyField(et_number, lyt_number)) {
                messagestr = et_message?.text.toString()
                val number = et_number?.text.toString().trim { it <= ' ' }
                    .replace(" ".toRegex(), "").replace("+", "")
                if (PhoneNumberUtils.isGlobalPhoneNumber(number)) {
                    countryCodePicker?.registerCarrierNumberEditText(et_number)
                    numberstr = countryCodePicker?.fullNumber!!
                    Log.d("TAG", "onClick: " + et_number?.text)
                    Log.d("TAG", "onClick: $numberstr")
                    if (isWhatappInstalled) {
                        val i = Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                "https://api.whatsapp.com/send?phone=" + numberstr +
                                        "&text=" + messagestr
                            )
                        )
                        startActivity(i)
                        et_message?.text!!.clear()
                        et_number?.text!!.clear()
                        lyt_number?.isErrorEnabled = false
                        lyt_number?.error = ""
                        lyt_number?.clearFocus()
                    } else {
                        Snackbar.make(root!!, "Whatsapp is not installed", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    lyt_number?.isErrorEnabled = true
                    lyt_number?.error = "Enter valid number without country code"
                }
            } else {
                //checkEmptyField(et_message,lyt_message);
                checkEmptyField(et_number, lyt_number)
            }
        }
        et_number?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (TextUtils.isEmpty(et_number?.text)) {
                    lyt_number?.isErrorEnabled = true
                    lyt_number?.error = "Required"
                } else {
                    lyt_number?.isErrorEnabled = false
                    lyt_number?.error = ""
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

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

    override fun onBackPressed() {
        if (et_number != null && et_number!!.hasFocus()) {
            et_number!!.clearFocus()
        } else if (et_message != null && et_message!!.hasFocus()) {
            et_message!!.clearFocus()
        } else super.onBackPressed()
    }

    private val isWhatappInstalled: Boolean
        private get() {
            val packageManager = packageManager
            val whatsappInstalled: Boolean = try {
                packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
            return whatsappInstalled
        }

    private fun checkEmptyField(
        editText: TextInputEditText?,
        inputLayout: TextInputLayout?
    ): Boolean {
        return if (editText!!.text.toString().trim { it <= ' ' } == "") {
            // editText.setError("Required");
            inputLayout!!.error = "Required"
            false
        } else {
            true
        }
    }
}