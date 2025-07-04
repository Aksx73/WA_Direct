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
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.parent) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
            )
            v.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = bars.bottom,
            )
            WindowInsetsCompat.CONSUMED
        }

        //window.navigationBarColor = SurfaceColors.SURFACE_2.getColor(this@MainActivity)
        //window.statusBarColor = SurfaceColors.SURFACE_2.getColor(this@MainActivity)
        //binding.parent.setBackgroundColor(SurfaceColors.SURFACE_2.getColor(this@MainActivity))

        binding.apply {
            sendbtn.setOnClickListener {
                if (checkEmptyField(etNumber, lytNumber)) {
                    messagestr = etMessage.text.toString()
                    val number = etNumber.text.toString().trim { it <= ' ' }
                        .replace(" ".toRegex(), "").replace("+", "")
                    if (PhoneNumberUtils.isGlobalPhoneNumber(number)) {
                        countryCode.registerCarrierNumberEditText(etNumber)

                        numberstr = countryCode.fullNumber!!
                        Log.d("TAG", "onClick: " + etNumber.text)
                        Log.d("TAG", "onClick: $numberstr")
                        if (Utils.isWhatsappInstalled(packageManager)) {
                            val uriString =
                                "https://api.whatsapp.com/send?phone=$numberstr&text=$messagestr"
                            Log.d("TAG", "Final url: $uriString")
                            val i = Intent(Intent.ACTION_VIEW, uriString.toUri())
                            startActivity(i)
                            etMessage.text!!.clear()
                            etNumber.text!!.clear()
                            lytNumber.isErrorEnabled = false
                            lytNumber.error = ""
                            lytNumber.clearFocus()
                        } else {
                            Snackbar.make(root, "Whatsapp is not installed", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        lytNumber.isErrorEnabled = true
                        lytNumber.error = "Enter valid number without country code"
                    }
                } else {
                    //checkEmptyField(et_message,lyt_message);
                    checkEmptyField(etNumber, lytNumber)
                }
            }

            etNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    if (TextUtils.isEmpty(etNumber.text)) {
                        lytNumber.isErrorEnabled = true
                        lytNumber.error = "Required"
                    } else {
                        lytNumber.isErrorEnabled = false
                        lytNumber.error = ""
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