package com.android.waintent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.waintent.ui.theme.WaIntentTheme
import com.arpitkatiyarprojects.countrypicker.CountryPicker
import com.arpitkatiyarprojects.countrypicker.enums.CountryListDisplayType
import com.arpitkatiyarprojects.countrypicker.models.CountriesListDialogDisplayProperties
import com.arpitkatiyarprojects.countrypicker.models.CountryDetails
import com.arpitkatiyarprojects.countrypicker.models.CountryPickerColors
import com.arpitkatiyarprojects.countrypicker.models.SelectedCountryDisplayProperties
import com.arpitkatiyarprojects.countrypicker.utils.CountryPickerDefault

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WaIntentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    /* private lateinit var binding: ActivityMainBinding
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
     }*/
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onButtonClick: (String, String?) -> Unit = { _, _ -> },
) {
    var phoneNumber by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var showPhoneNumberError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        CountryPicker(
            modifier = Modifier,
            onCountrySelected = {
                //todo
            },
        )
        Spacer(Modifier.size(8.dp))
        TextField(
            value = phoneNumber,
            onValueChange = { input ->
                phoneNumber = input.filter { it.isDigit() }
                showPhoneNumberError = false
            },
            leadingIcon = {
                Icon(imageVector = Icons.Outlined.Phone, contentDescription = null)
            },
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            label = { Text("Whatsapp number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            trailingIcon = {
                if (phoneNumber.isNotEmpty()) {
                    IconButton(onClick = { phoneNumber = "" }) {
                        Icon(
                            Icons.Outlined.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            isError = showPhoneNumberError,
            supportingText = {
                if (showPhoneNumberError) {
                    Text("Required", color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true
        )

        Spacer(Modifier.size(12.dp))

        TextField(
            value = message,
            onValueChange = { message = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Enter message") },
            placeholder = { Text("Enter your message here") },
            supportingText = { Text("Optional") },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_chat_black_24dp),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (message.isNotEmpty()) {
                    IconButton(onClick = { message = "" }) {
                        Icon(
                            Icons.Outlined.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            ),
        )

        Spacer(Modifier.size(32.dp))

       // Box(Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    if (phoneNumber.isBlank()) {
                        showPhoneNumberError = true
                    } else {
                        showPhoneNumberError = false
                        onButtonClick(phoneNumber, message)
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_whatsapp_black_24dp),
                    contentDescription = null
                )
                Spacer(Modifier.size(8.dp))
                Text("Open chat")
            }
      //  }

    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    WaIntentTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            HomeScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}