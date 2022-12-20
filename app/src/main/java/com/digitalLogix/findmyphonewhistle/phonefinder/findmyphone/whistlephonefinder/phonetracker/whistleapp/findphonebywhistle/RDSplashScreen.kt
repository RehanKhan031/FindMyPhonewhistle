package com.digitalLogix.findmyphonewhistle.phonefinder.findmyphone.whistlephonefinder.phonetracker.whistleapp.findphonebywhistle

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.digitalLogix.findmyphonewhistle.phonefinder.findmyphone.whistlephonefinder.phonetracker.whistleapp.findphonebywhistle.databinding.ActivityRdSplashScreenBinding
import com.digitalLogix.findmyphonewhistle.phonefinder.findmyphone.whistlephonefinder.phonetracker.whistleapp.findphonebywhistle.utils.AdUtils.Companion.adShown
import com.digitalLogix.findmyphonewhistle.phonefinder.findmyphone.whistlephonefinder.phonetracker.whistleapp.findphonebywhistle.utils.AdUtils.Companion.incrementUserAction
import com.digitalLogix.findmyphonewhistle.phonefinder.findmyphone.whistlephonefinder.phonetracker.whistleapp.findphonebywhistle.utils.AdUtils.Companion.shouldShowAd
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class RDSplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivityRdSplashScreenBinding
    private var pendingClickView: View? = null
    var interstitialAd: InterstitialAd? = null

    private val AD_UNIT_ID = "ca-app-pub-1200903482292954/6574658428"
    private val TAG = "MyActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRdSplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            CoroutineScope(Dispatchers.Main).launch {
                delay(6000)
                startBtn.visibility = View.VISIBLE
                loading.visibility = View.INVISIBLE
            }
            startBtn.setOnClickListener {
                incrementUserAction()
                if (interstitialAd != null && shouldShowAd()) {
                    interstitialAd!!.show(this@RDSplashScreen)
                    adShown()
                    pendingClickView = it
                } else {
                    pendingClickView = it
                    startActivity(Intent(this@RDSplashScreen, MainActivity::class.java))
                    finish()
                }

            }
        }


    }

    override fun onResume() {
        super.onResume()

        loadAd()
    }


    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    this@RDSplashScreen.interstitialAd = interstitialAd
                    interstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                if (pendingClickView != null) {
                                    pendingClickView!!.performClick()
                                    pendingClickView = null
                                }
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                // Called when fullscreen content failed to show.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                if (pendingClickView != null) {
                                    pendingClickView!!.performClick()
                                    pendingClickView = null
                                }
                                this@RDSplashScreen.interstitialAd = null
                                Log.d("TAG", "The ad failed to show.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                Log.d("TAG", "The ad was shown.")
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.i(TAG, loadAdError.message)
                    interstitialAd = null

                }
            })
    }

}