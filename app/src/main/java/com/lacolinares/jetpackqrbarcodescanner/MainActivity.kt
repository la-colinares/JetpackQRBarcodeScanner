package com.lacolinares.jetpackqrbarcodescanner

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.lacolinares.jetpackqrbarcodescanner.model.BarcodeState
import com.lacolinares.jetpackqrbarcodescanner.model.BarcodeTypeModel
import com.lacolinares.jetpackqrbarcodescanner.ui.theme.JetpackQRBarcodeScannerTheme
import com.lacolinares.jetpackqrbarcodescanner.ui.theme.LightSteelBlue
import com.lacolinares.jetpackqrbarcodescanner.ui.theme.SpaceCadet
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val codeScanner = GoogleCodeScanner(this).apply {
            this.startScan()
        }
        setContent {
            JetpackQRBarcodeScannerTheme {
                val barcodeResults = codeScanner.barcodeResultState.collectAsStateWithLifecycle()
                when (barcodeResults.value) {
                    BarcodeState.Idle -> Unit
                    is BarcodeState.Success -> {
                        val data = (barcodeResults.value as BarcodeState.Success).data
                        Timber.d("#app: $data")
                        ResultScreen(
                            resultData = data,
                            onScanAgain = { codeScanner.startScan() }
                        )
                    }

                    BarcodeState.Cancelled -> finish()
                    BarcodeState.Error -> Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
                }
            }
            BackHandler {
                finish()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    resultData: Map<Int, BarcodeTypeModel> = emptyMap(),
    onScanAgain: () -> Unit = {}
) {
    val model = resultData.values.first()

    Scaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding(),
        topBar = { Toolbar() },
        bottomBar = { ScanAgainButton(onScanAgain::invoke) },
        containerColor = LightSteelBlue
    ) { paddingValues ->
        val listState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .padding(horizontal = 24.dp),
            state = listState,
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item(key = "QRCode") {
                QRImage(model.getRawValue())
            }
            item(key = "Info") {
                CardInfo()
            }
        }
    }
}

@Preview
@Composable
fun CardInfo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SpaceCadet)
            .padding(vertical = 20.dp, horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_wifi_24),
                contentDescription = "Wifi",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "WIFI",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Text(
            text = "Network Name: Scan Me\nPassword: Congratulations\nType: WPA",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Button(
            onClick = {  },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LightSteelBlue),
            contentPadding = PaddingValues(vertical = 0.dp, horizontal = 24.dp)
        ) {
            Text(
                text = "Copy Password",
                modifier = Modifier.padding(vertical = 12.dp),
                color = SpaceCadet,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun QRImage(rawValue: String = "") {
    Image(
        bitmap = barcodeEncoder(rawValue),
        contentDescription = "QR",
        modifier = Modifier.size(200.dp),
        alignment = Alignment.Center
    )
}


@Preview
@Composable
private fun ScanAgainButton(
    onScanAgain: () -> Unit = {}
) {
    Button(
        onClick = { onScanAgain() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SpaceCadet),
        contentPadding = PaddingValues(vertical = 0.dp, horizontal = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.scan_again),
            modifier = Modifier.padding(vertical = 12.dp),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Preview()
@Composable
private fun Toolbar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SpaceCadet),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.scan_result),
            modifier = Modifier.padding(vertical = 20.dp),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun barcodeEncoder(content: String): ImageBitmap {
    val encoder = BarcodeEncoder()
    return encoder.encodeBitmap(
        /* contents = */ content,
        /* format = */ BarcodeFormat.QR_CODE,
        /* width = */ 240,
        /* height = */ 240
    ).asImageBitmap()
}