package com.android.waintent

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.android.waintent.databinding.ActivityMainBinding
import com.google.android.material.color.DynamicColors
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var messagestr: String? = null
    private var numberstr = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                    if (Utils.isWhatsappInstalled(packageManager)) {
                        val uriString = "https://api.whatsapp.com/send?phone=$numberstr&text=$messagestr"
                        Log.d("TAG", "Final url: $uriString")
                        val i = Intent(Intent.ACTION_VIEW, uriString.toUri())
                        startActivity(i)
                        et_message?.text!!.clear()
                        et_number?.text!!.clear()
                        lyt_number?.isErrorEnabled = false
                        lyt_number?.error = ""
                        lyt_number?.clearFocus()
                    }
                    else {
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

        }



        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.etNumber.hasFocus()) {
                    binding.etNumber.clearFocus()
                } else if (binding.etMessage.hasFocus()) {
                    binding.etMessage.clearFocus()
                } else {
                    // Call the default back action
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

    }

    private fun checkEmptyField(
        editText: TextInputEditText?,
        inputLayout: TextInputLayout?
    ): Boolean {
        return if (editText!!.text.toString().trim { it <= ' ' } == "") {
            inputLayout!!.error = "Required"
            false
        } else {
            true
        }
    }
}