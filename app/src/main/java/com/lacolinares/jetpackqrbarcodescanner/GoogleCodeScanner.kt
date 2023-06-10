package com.lacolinares.jetpackqrbarcodescanner

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.lacolinares.jetpackqrbarcodescanner.model.BarcodeState
import com.lacolinares.jetpackqrbarcodescanner.model.BarcodeTypeModel
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

class GoogleCodeScanner(
    context: Context
) {

    /**
     * Setting up the scanner options.
     * In this example, we will use all formats of barcode to support scanning of other barcode types.
     */
    private val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()

    private val scanner = GmsBarcodeScanning.getClient(context, options)
    val barcodeResultState = MutableStateFlow<BarcodeState<BarcodeTypeModel>>(BarcodeState.Idle)

    fun startScan() {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                barcode.rawValue
                when(barcode.valueType){
                    Barcode.TYPE_WIFI -> {
                        val encryptionType = barcode.wifi?.encryptionType
                        val wifi = BarcodeTypeModel.Wifi(
                            networkName = barcode.wifi?.ssid.orEmpty(),
                            password =  barcode.wifi?.password.orEmpty(),
                            type = getAuthType(encryptionType),
                            rawString = barcode.rawValue
                        )
                        val data = mapOf(Barcode.TYPE_WIFI to wifi)
                        barcodeResultState.value = BarcodeState.Success(data)
                    }
                    Barcode.TYPE_URL -> {
                        val url = BarcodeTypeModel.Url(
                            title = barcode.url?.title.orEmpty(),
                            url =  barcode.url?.url.orEmpty(),
                            rawString = barcode.rawValue
                        )
                        val data = mapOf(Barcode.TYPE_URL to url)
                        barcodeResultState.value = BarcodeState.Success(data)
                    }
                }
            }
            .addOnCanceledListener {
                barcodeResultState.value = BarcodeState.Cancelled
            }
            .addOnFailureListener {
                Timber.e(it)
                barcodeResultState.value = BarcodeState.Error
            }
    }

    private fun getAuthType(encryptionType: Int?): BarcodeTypeModel.Wifi.AuthType{
        return when(encryptionType){
            Barcode.WiFi.TYPE_OPEN -> BarcodeTypeModel.Wifi.AuthType.OPEN
            Barcode.WiFi.TYPE_WEP -> BarcodeTypeModel.Wifi.AuthType.WEP
            Barcode.WiFi.TYPE_WPA -> BarcodeTypeModel.Wifi.AuthType.WPA
            else -> BarcodeTypeModel.Wifi.AuthType.OPEN
        }
    }
}