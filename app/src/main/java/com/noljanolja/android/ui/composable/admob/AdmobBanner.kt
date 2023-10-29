package com.noljanolja.android.ui.composable.admob

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.nativead.NativeAd

@Composable
fun AdmobRectangle(modifier: Modifier = Modifier) {
    AndroidView(modifier = modifier.fillMaxWidth(), factory = { context ->
        // on below line specifying ad view.
        AdView(context).apply {
            // on below line specifying ad size
            // adSize = AdSize.BANNER
            // on below line specifying ad unit id
            // currently added a test ad unit id.
            setAdSize(AdSize.MEDIUM_RECTANGLE)
            adUnitId = "ca-app-pub-3940256099942544/6300978111"
            // calling load ad to load our ad.
            loadAd(AdRequest.Builder().build())
        }
    })
}

@Composable
fun AdmobLargeBanner(modifier: Modifier = Modifier) {
    AndroidView(modifier = modifier.fillMaxWidth(), factory = { context ->
        // on below line specifying ad view.
        AdView(context).apply {
            // on below line specifying ad size
            // adSize = AdSize.BANNER
            // on below line specifying ad unit id
            // currently added a test ad unit id.
            setAdSize(AdSize.LARGE_BANNER)
            adUnitId = "ca-app-pub-3940256099942544/6300978111"
            // calling load ad to load our ad.
            loadAd(AdRequest.Builder().build())
        }
    })
}

@Composable
fun NativeAdView(
    adUnitId: String,
    content: @Composable (NativeAd) -> Unit,
) {
    val context = LocalContext.current
    val nativeAd = remember { mutableStateOf<NativeAd?>(null) }
    val adLoader = remember {
        AdLoader.Builder(context, adUnitId).forNativeAd { ad ->
            nativeAd.value = ad
        }.build()
    }

    nativeAd.value?.let {
        content(it)
    } ?: Text("load fail")

    LaunchedEffect(Unit) {
        adLoader.loadAd(AdRequest.Builder().build())
    }
}