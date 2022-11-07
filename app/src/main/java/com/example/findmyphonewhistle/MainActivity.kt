package com.example.findmyphonewhistle

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.findmyphonewhistle.databinding.ActivityMainBinding
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

    var context: Context = this

    private var clapBool = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        lodeDataClap()

        binding.apply {
            if (clap == 0) {
                activate.text = "DeActivate"
            }else{
                activate.text = "Activate"
            }
            activate.setOnClickListener {
                MyUtils.KEERA++
                if (clap == 0) {
                    clapBool = true
                    recordAudioSync.clap = 1
                    clap = 1
                    permissions()
                    activate.text = "Activate"
                } else {
                    clapBool = false
                    clap = 0
                    recordAudioSync.clap = 0
                    activate.text = "DeActivate"
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

    }


    override fun onResume() {
        super.onResume()

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

    fun permissions() {
        Dexter.withContext(context).withPermission(Manifest.permission.RECORD_AUDIO)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    isgranted()
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
    fun isgranted() {
        recordAudioSync.runing()
    }
    fun saveDataClap() {
        val sharedPreferences = getSharedPreferences("saveDataClap", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("clap", clap)
        editor.apply()
    }
    fun lodeDataClap() {
        val sharedPreferences = getSharedPreferences("saveDataClap", MODE_PRIVATE)
        clap = sharedPreferences.getInt("clap", 1)
    }

}