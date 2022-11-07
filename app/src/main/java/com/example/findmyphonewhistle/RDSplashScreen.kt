package com.example.findmyphonewhistle

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.findmyphonewhistle.databinding.ActivityRdSplashScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class RDSplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivityRdSplashScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRdSplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            CoroutineScope(Dispatchers.Main).launch {
                delay(5000)
                startBtn.visibility = View.VISIBLE
                loading.visibility = View.INVISIBLE
            }
            startBtn.setOnClickListener {
                startActivity(Intent(this@RDSplashScreen,MainActivity::class.java))
            }
        }


    }
}