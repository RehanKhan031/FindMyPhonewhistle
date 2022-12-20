package com.digitalLogix.findmyphonewhistle.phonefinder.findmyphone.whistlephonefinder.phonetracker.whistleapp.findphonebywhistle

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Operation(var context: Context) {
    var r: Runnable? = null
    var mediaRecorder: MediaRecorder? = null
    var clap = 1
    var startAmlitued = 0

    //    int amlituedTreshold = 20000;
    var amlituedTreshold = 10000
    var service: ExecutorService? = null

    var action: String? = null
    var handler = Handler(Looper.getMainLooper())
    private var cameraManager: CameraManager? = null
    private var co = 0
    private var cameraid: String? = null

    fun runing() {
        Handler(Looper.getMainLooper())
        service = Executors.newSingleThreadExecutor()
        service!!.execute(Runnable {
            recordAudio()
            handler.post { }
        })
    }


    private fun recordAudio() {
        mediaRecorder = MediaRecorder()
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder!!.setOutputFile("/data/data/" + context.packageName + "/music.3gp")
        try {
            mediaRecorder!!.prepare()
            mediaRecorder!!.start()
            startAmlitued = mediaRecorder!!.maxAmplitude
        } catch (e: IOException) {
            e.printStackTrace()
        }
        loope()
    }

    private fun loope() {
        var finishAmlitued: Int
        do {
            if (clap == 1) {
                finishAmlitued = mediaRecorder!!.maxAmplitude
                if (finishAmlitued >= amlituedTreshold) {
                    if (co == 0) {

                        val vibrationSharedPreferences = context.getSharedPreferences("vibrationCheck",
                            AppCompatActivity.MODE_PRIVATE)
                        val vibration = vibrationSharedPreferences.getBoolean("vibration", false)


                        val flashLightSharedPreferences = context.getSharedPreferences("flashLightCheck",
                            AppCompatActivity.MODE_PRIVATE)
                        val flashLight = flashLightSharedPreferences.getBoolean("flashLight", false)


                        if (vibration) {
                            val vibrator =
                                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                            if (Build.VERSION.SDK_INT >= 26) {
                                vibrator.vibrate(
                                    VibrationEffect.createOneShot(
                                        200,
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                    )
                                )
                            } else {
                                vibrator.vibrate(200)
                            }
                        }
                        if (flashLight) {
                            cameraManager =
                                context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                try {
                                    if (cameraManager != null) {
                                        cameraid = cameraManager!!.cameraIdList[0]
                                        cameraManager!!.setTorchMode(cameraid!!, true)
                                        MainActivity.count = 1
                                        action = "On"
                                        sandBroadCast(action!!)
                                        Log.d("my ", "action $action")
                                    }
                                } catch (e: CameraAccessException) {
                                    e.printStackTrace()
                                }
                                co = 1
                            }
                        }
                    } else {
                        cameraManager =
                            context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (cameraManager != null) {
                                try {
                                    cameraid = cameraManager!!.getCameraIdList()[0]
                                } catch (e: CameraAccessException) {
                                    e.printStackTrace()
                                }
                                try {
                                    cameraManager!!.setTorchMode(cameraid!!, false)
                                    MainActivity.count = 0
                                    action = "Off"
                                    sandBroadCast(action!!)
                                } catch (e: CameraAccessException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        co = 0
                        Log.d("My Tag", "In Loop....")
             }
                }
            } else {
                doun()
                break
            }
        } while (true)
    }

    private fun sandBroadCast(action: String) {
        val intent = Intent(action)
        context.sendBroadcast(intent)
    }

    fun doun() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder!!.stop()
                mediaRecorder!!.release()
                service!!.isShutdown
            } catch (e: Exception) {
            }
        }
    }

    private fun waitSome() {
        try {
            Thread.sleep(1500)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

}