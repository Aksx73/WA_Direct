package com.android.waintent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.android.waintent.ui.theme.WaIntentTheme
import com.arpitkatiyarprojects.countrypicker.CountryPicker
import com.arpitkatiyarprojects.countrypicker.enums.CountryListDisplayType
import com.arpitkatiyarprojects.countrypicker.models.CountriesListDialogDisplayProperties
import com.arpitkatiyarprojects.countrypicker.models.CountriesListDialogProperties
import com.arpitkatiyarprojects.countrypicker.models.CountryDetails
import com.arpitkatiyarprojects.countrypicker.models.CountryPickerColors
import com.arpitkatiyarprojects.countrypicker.models.FlagDimensions
import com.arpitkatiyarprojects.countrypicker.models.SelectedCountryDisplayProperties
import com.arpitkatiyarprojects.countrypicker.models.SelectedCountryProperties
import com.arpitkatiyarprojects.countrypicker.models.SelectedCountryTextStyles
import com.arpitkatiyarprojects.countrypicker.utils.CountryPickerUtils
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WaIntentTheme {
                val snackBarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val keyboardController = LocalSoftwareKeyboardController.current

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
                ) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        onButtonClick = { countryCode, countryPhoneCode, phoneNumber, message ->

                            if (CountryPickerUtils.isMobileNumberValid(phoneNumber, countryCode)) {
                                //val fullPhoneNumber = "$countryPhoneCode$phoneNumber"
                                val formattedPhoneNumber = Utils.formatPhoneNumberForWhatsApp(
                                    countryPhoneCode,
                                    phoneNumber
                                )
                                val urlEncodeMessage = Utils.urlEncodeMessage(message)
                                openWhatsApp(
                                    formattedPhoneNumber,
                                    urlEncodeMessage,
                                    onShowSnackbar = { message, duration ->
                                        keyboardController?.hide()
                                        scope.launch {
                                            snackBarHostState.showSnackbar(
                                                message,
                                                duration = duration
                                            )
                                        }
                                    })
                            } else {
                                keyboardController?.hide()
                                scope.launch {
                                    snackBarHostState.showSnackbar(
                                        "Enter valid number without country code",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }

                        }
                    )
                }
            }
        }
    }

    private fun openWhatsApp(
        phoneNumber: String,
        message: String?,
        onShowSnackbar: (String, SnackbarDuration) -> Unit
    ) {
        if (Utils.isWhatsappInstalled(packageManager)) {
            //val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${message ?: ""}"
            val url = "https://wa.me/$phoneNumber?text=${message ?: ""}"
            Log.d("TAG", "Final url: $url")
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
            //todo clear all text field and any focus and error message
        } else {
            onShowSnackbar("Whatsapp is not installed", SnackbarDuration.Short)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onButtonClick: (String, String, String, String?) -> Unit,
) {
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var showPhoneNumberError by rememberSaveable { mutableStateOf(false) }
    var countryCode by rememberSaveable { mutableStateOf("") }
    var countryPhoneNumberCode by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.displayCutout)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
    ) {
        Box {
            Column(
                modifier = Modifier
                    //.fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                CountryPickerView { country ->
                    countryCode = country.countryCode
                    countryPhoneNumberCode = country.countryPhoneNumberCode
                }

                Spacer(Modifier.size(12.dp))

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
                    label = { Text("Whatsapp Number") },
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
                        Row(Modifier.fillMaxWidth()) {
                            if (showPhoneNumberError) {
                                Text(
                                    "Required",
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text("${phoneNumber.length}")
                        }
                    },
                    singleLine = true
                )

                Spacer(Modifier.size(12.dp))

                TextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Message") },
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

                Button(
                    onClick = {
                        if (phoneNumber.isBlank()) {
                            showPhoneNumberError = true
                        } else {
                            showPhoneNumberError = false
                            onButtonClick(countryCode, countryPhoneNumberCode, phoneNumber, message)
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
            }
        }


    }
}

@Composable
fun CountryPickerView(
    modifier: Modifier = Modifier,
    onCountrySelected: (CountryDetails) -> Unit
) {
    CountryPicker(
        modifier = modifier,
        countryListDisplayType = CountryListDisplayType.BottomSheet,
        onCountrySelected = { onCountrySelected(it) },
        countryPickerColors = CountryPickerColors(
            dropDownIconColor = LocalContentColor.current.copy(alpha = 0.7f),
            backIconColor = LocalContentColor.current,
            searchIconColor = LocalContentColor.current,
            cancelIconColor = LocalContentColor.current,
            searchCursorColor = Color.Unspecified,
            selectedCountryContainerColor = Color.Transparent,
            countriesListContainerColor = MaterialTheme.colorScheme.background,
            selectedCountryDisabledContainerColor = Color.Transparent,
            dropDownDisabledIconColor = LocalContentColor.current,
        ),
        selectedCountryDisplayProperties = SelectedCountryDisplayProperties(
            properties = SelectedCountryProperties(
                showCountryCode = true,
                showCountryPhoneCode = true,
                showCountryFlag = true,
                showDropDownIcon = true
            ),
            flagDimensions = FlagDimensions(32.dp, 20.dp),
            textStyles = SelectedCountryTextStyles(
                countryPhoneCodeTextStyle = TextStyle(fontSize = 16.sp, fontWeight = Bold),
                countryCodeTextStyle = TextStyle(fontSize = 16.sp)
            ),
            flagShape = RoundedCornerShape(2.dp)
        ),
        countriesListDialogDisplayProperties = CountriesListDialogDisplayProperties(
            properties = CountriesListDialogProperties(
                showCountryCode = true
            ),
        )
    )
}

@Preview
@Composable
private fun HomeScreenPreview() {
    WaIntentTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            HomeScreen(modifier = Modifier.padding(innerPadding)) { _, _, _, _ -> }
        }
    }
}