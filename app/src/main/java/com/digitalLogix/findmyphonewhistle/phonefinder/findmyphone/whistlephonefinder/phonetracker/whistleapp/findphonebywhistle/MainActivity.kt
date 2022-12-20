package com.digitalLogix.findmyphonewhistle.phonefinder.findmyphone.whistlephonefinder.phonetracker.whistleapp.findphonebywhistle

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.digitalLogix.findmyphonewhistle.phonefinder.findmyphone.whistlephonefinder.phonetracker.whistleapp.findphonebywhistle.databinding.ActivityMainBinding
import com.digitalLogix.findmyphonewhistle.phonefinder.findmyphone.whistlephonefinder.phonetracker.whistleapp.findphonebywhistle.utils.AdUtils.Companion.populateUnifiedNativeAdView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object{
        var count = 0
        var clap: Int = 0
    }
    var recordAudioSync = Operation(this@MainActivity)

    private var context: Context = this

    private var clapBool = false

    private var nativeAd: NativeAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        lodeDataClap()

        binding.apply {
            if (clap == 0) {
                activateTxt.text = "Activate"
            }else{
                activateTxt.text = "DeActivate"
            }
            activate.setOnClickListener {
                MyUtils.KEERA++
                if (clap == 0) {
                    clapBool = true
                    recordAudioSync.clap = 1
                    clap = 1
                    permissions()
                    activateTxt.text = "DeActivate"
                } else {
                    clapBool = false
                    clap = 0
                    recordAudioSync.clap = 0
                    activateTxt.text = "Activate"
                }
            }
            flashLightSwitch.setOnCheckedChangeListener { _, isChecked ->

                if (isChecked) {
                    val sharedPreferences = getSharedPreferences("flashLightCheck", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("flashLight", true)
                    editor.apply()
                }else{
                    val sharedPreferences = getSharedPreferences("flashLightCheck", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("flashLight", false)
                    editor.apply()
                }

            }

            vibrationSwitch.setOnCheckedChangeListener { _, isChecked ->

                if (isChecked) {
                    val sharedPreferences = getSharedPreferences("vibrationCheck", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("vibration", true)
                    editor.apply()
                }else{
                    val sharedPreferences = getSharedPreferences("vibrationCheck", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("vibration", false)
                    editor.apply()
                }

            }

        }

        navigationView()
    }

    private fun navigationView() {
        binding.apply {
            /* ****************Navigation Drawer View****************/
            navView.bringToFront()
            val toggle = ActionBarDrawerToggle(
                this@MainActivity, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            navigationIcon.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }

            RateTheApp.setOnClickListener {
                rateApp()
            }
            shareNavigationBar.setOnClickListener {
                shareApp()
            }
            moreNavigationBar.setOnClickListener {
                moreApp()
            }
            exitNavigationBar.setOnClickListener {
                finishAffinity()
            }
        }
    }


    override fun onResume() {
        super.onResume()

        loadNativeAd()
        lodeDataClap()
        if (clap == 1) {
            recordAudioSync.clap = 1
            clap = 1
            permissions()
        }else{
            clap = 0
            recordAudioSync.clap = 0
        }

    }

    override fun onPause() {
        super.onPause()
        saveDataClap()
    }

    private fun permissions() {
        Dexter.withContext(context).withPermission(Manifest.permission.RECORD_AUDIO)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    isGranted()
                }

                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                    Toast.makeText(this@MainActivity, "Permission Requride", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissionRequest: PermissionRequest,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            }).check()
    }
    fun isGranted() {
        recordAudioSync.runing()
    }
    private fun saveDataClap() {
        val sharedPreferences = getSharedPreferences("saveDataClap", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("clap", clap)
        editor.apply()
    }
    private fun lodeDataClap() {
        val sharedPreferences = getSharedPreferences("saveDataClap", MODE_PRIVATE)
        clap = sharedPreferences.getInt("clap", 1)
    }

    private fun shareApp() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey check out my app at: https://play.google.com/store/apps/details?id=" +
                    //BuildConfig.APPLICATION_ID);
                    applicationContext.packageName
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    private fun rateApp() {
        val uri = Uri.parse(
            "https://play.google.com/store/apps/details?id=" +
                    applicationContext.packageName
        )
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    private fun moreApp() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(resources.getString(R.string.more_apps_link))
            )
        )
    }
    private fun loadNativeAd() {
        val builder = AdLoader.Builder(this@MainActivity, getString(R.string.admob_native_id))

        // OnUnifiedNativeAdLoadedListener implementation.
        builder.forNativeAd { unifiedNativeAd ->
            if (nativeAd != null) {
                nativeAd!!.destroy()
            }
            nativeAd = unifiedNativeAd
            val adView = this@MainActivity.layoutInflater
                .inflate(R.layout.native_mini, null) as NativeAdView
            populateUnifiedNativeAdView(unifiedNativeAd, adView)
            val nativeAdFrame: FrameLayout =
                this@MainActivity.findViewById<FrameLayout>(R.id.native_ad)
            nativeAdFrame.removeAllViews()
            nativeAdFrame.addView(adView)
            nativeAdFrame.visibility = View.VISIBLE
        }

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }


}